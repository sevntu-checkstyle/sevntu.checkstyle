////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2010  Oliver Burn
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////
package com.puppycrawl.tools.checkstyle.checks.coding;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.beanutils.ConversionException;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FastStack;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.api.Utils;

/**
 * <p>
 * Checks that the parts of a class declaration appear in the rules order set by
 * user using regular expressions.<br>
 * The rule consists of:
 *
 * <pre>
 * ClassMember(RegExp)
 * </pre>
 *
 * </p>
 * To set class order use the following notation of the class members (case
 * insensitive):
 * <p>
 * <ol>
 * <li>"Field" to denote the Fields</li>
 * <li>"Ctor" to denote the Constructors</li>
 * <li>"Method" to denote the Methods</li>
 * <li>"InnerClass" to denote the Inner Classes</li>
 * </ol>
 * </p>
 * RegExp can include modifiers(public, protected, private, abstract, static and
 * others) and annotations of a class member.
 * <p>
 * Use the separator # # # between the declarations
 * </p>
 * <p>
 * For Example:
 * </p>
 *
 * <pre>
 * <code>Field(public) ### Method(.*public.*final|@Ignore.*public.*)</code>
 * </pre>
 *
 * @author <a href="mailto:solid.danil@gmail.com">Danil Lopatin</a>
 */
public class CustomDeclarationOrderCheck extends Check
{

    /** List of order declaration customizing by user */
    private final ArrayList<FormatMatcher> mCustomOrderDeclaration =
        new ArrayList<FormatMatcher>();

    /**
     * List of Declaration States. This is necessary due to inner classes that
     * have their own state.
     */
    private final FastStack<ClassStates> mClassStates =
        new FastStack<ClassStates>();

    /** Initialization declaration order from an initial position */
    private static final int INITIAL_STATE = 0;

    /** save compile flags for further usage */
    private int mCompileFlags;

    /** Is current class as root */
    private boolean mClassRoot = true;

    /** allow check inner classes */
    private boolean mInnerClass;

    /** Private class to encapsulate the state */
    private static class ClassStates
    {
        /** new state */
        private int mClassStates = INITIAL_STATE;
    }

    /**
     * Parsing input line with custom declaration order into massive.
     *
     * @param aInputOrderDeclaration The string line with the user custom
     *            declaration.
     */
    public void setCustomDeclarationOrder(final String aInputOrderDeclaration)
    {
        for (String currentState
                : aInputOrderDeclaration.split("\\s*###\\s*"))
        {
            mCustomOrderDeclaration.add(new FormatMatcher(currentState,
                    mCompileFlags));
        }
    }

    /**
     * Set whether or not the match is case sensitive.
     *
     * @param aCaseInsensitive true if the match is case insensitive.
     */
    public void setIgnoreRegExCase(final boolean aCaseInsensitive)
    {
        if (aCaseInsensitive) {
            if (!mCustomOrderDeclaration.isEmpty()) {
                for (FormatMatcher currentRule : mCustomOrderDeclaration) {
                    currentRule.setCompileFlags(Pattern.CASE_INSENSITIVE);
                }
            }
            else {
                mCompileFlags = Pattern.CASE_INSENSITIVE;
            }
        }
    }

    @Override
    public int[] getDefaultTokens()
    {
        //HashSet for unique Tokens
        final HashSet<String> classMembers = new HashSet<String>();

        for (FormatMatcher currentRule : mCustomOrderDeclaration) {
            // check existing of InnerClass in rule
            if ("CLASS_DEF".equals(currentRule.getClassMember())) {
                mInnerClass = true;
            }
            else {
                classMembers.add(currentRule.mClassMember); //add Tokens
            }
        }

        final int defaultTokens[] = new int[classMembers.size() + 1];
        defaultTokens[0] = TokenTypes.CLASS_DEF;

        int index = 1;
        for (String token : classMembers) {
            if ("VARIABLE_DEF".equals(token)) {
                defaultTokens[index] = TokenTypes.VARIABLE_DEF;
            }
            else if ("METHOD_DEF".equals(token)) {
                defaultTokens[index] = TokenTypes.METHOD_DEF;
            }
            else if ("CTOR_DEF".equals(token)) {
                defaultTokens[index] = TokenTypes.CTOR_DEF;
            }
            else {
                defaultTokens[index] = defaultTokens[0];
            }
            ++index;
        }
        return defaultTokens;
    }

    @Override
    public void visitToken(DetailAST aAST)
    {

        if (aAST.getType() == TokenTypes.CLASS_DEF) {
            if (mClassRoot) {
                mClassStates.push(new ClassStates());
                mClassRoot = false;
            }
            else {
                if (mInnerClass) {
                    checkOrderLogic(aAST);
                }
                mClassStates.push(new ClassStates());
            }
        }
        else {
            final int parentType = aAST.getParent().getType();
            if (parentType == TokenTypes.OBJBLOCK) {
                checkOrderLogic(aAST);
            }
        }
    }

    /**
     * Check class declaration order with custom declaration order.
     *
     * @param aAST current DetailAST state.
     */
    private void checkOrderLogic(final DetailAST aAST)
    {
        final ClassStates previousState = mClassStates.peek();
        final int currentState = getPosition(aAST);
        if (currentState >= 0) {
            if (previousState.mClassStates > currentState) {
                writeLog(aAST, currentState, previousState.mClassStates);
            }
            else {
                previousState.mClassStates = currentState;
            }
        }
    }

    /**
     * Search in existing custom declaration order current aAST state. It's
     * necessary for getting order of declarations.
     *
     * @param aAST current DetailAST state.
     * @return position in the list of the sequence declaration if
     *         correspondence has been found. Else -1.
     */
    private int getPosition(final DetailAST aAST)
    {
        int result = -1;
        final String modifiers = getUniteModifiersList(aAST);
        for (int index = 0; index < mCustomOrderDeclaration.size(); index++) {
            final FormatMatcher currentRule = mCustomOrderDeclaration
                    .get(index);
            if (currentRule.getClassMember().equals(aAST.getText())) {
                // find correspondence between list of modifiers and RegExp
                if (currentRule.getRegexp().matcher(modifiers).find()) {
                    result = index;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Writes log according to met type of token.
     *
     * @param aAST state for log.
     * @param aExpectPosition the expected first position
     * @param aCurrentPosition the current wrong position
     */
    private void writeLog(final DetailAST aAST,
            final int aExpectPosition, final int aCurrentPosition)
    {
        String token;
        switch (aAST.getType()) {
        case TokenTypes.VARIABLE_DEF:
            token = "custom.declaration.order.field";
            break;
        case TokenTypes.METHOD_DEF:
            token = "custom.declaration.order.method";
            break;
        case TokenTypes.CTOR_DEF:
            token = "custom.declaration.order.constructor";
            break;
        case TokenTypes.CLASS_DEF:
            token = "custom.declaration.order.class";
            break;
        default:
            token = "Unknown element: " + aAST.getType();
        }
        log(aAST,
            token,
            mCustomOrderDeclaration.get(aExpectPosition).getRule(),
            mCustomOrderDeclaration.get(aCurrentPosition).getRule());
    }

    @Override
    public void leaveToken(DetailAST aAST)
    {
        if (aAST.getType() == TokenTypes.CLASS_DEF) {
            mClassStates.pop();
            if (mClassStates.isEmpty()) {
                mClassRoot = true;
            }
        }
    }

    /**
     * Use for concatenation modifiers and annotations in single line. <br>
     * Contains TokenTypes parameters for entry in child.
     *
     * @param aAST current DetailAST state.
     * @return the unit annotations and modifiers and list.
     */
    private String getUniteModifiersList(final DetailAST aAST)
    {
        DetailAST ast = aAST.findFirstToken(TokenTypes.MODIFIERS);

        final StringBuffer modifiers = new StringBuffer();
        if (ast != null && ast.getFirstChild() != null) {
            ast = ast.getFirstChild();
            modifiers.append(concatLogic(ast));
        }

        return modifiers.toString();
    }

    /**
     * Use for recursive tree traversal from first child of current tree top.
     *
     * @param aAST current DetailAST state, first child of current tree top.
     * @return the unit modifiers and annotation list.
     */
    private String concatLogic(final DetailAST aAST)
    {
        DetailAST ast = aAST;

        final StringBuffer modifiers = new StringBuffer();
        while (ast != null) {
            if (ast.getType() == TokenTypes.ANNOTATION
                    || ast.getType() == TokenTypes.EXPR)
            {
                modifiers.append(concatLogic(ast.getFirstChild()));
            }
            else {
                modifiers.append(ast.getText());
            }
            ast = ast.getNextSibling();
        }
        return modifiers.toString();
    }

    /**
     * private class for members of class and their patterns.
     */
    private static class FormatMatcher
    {
        /**
         * Save compile flag. It can be necessary to further change the logic of
         * check.
         */
        private int mCompileFlags;
        /** The regexp to match against */
        private Pattern mRegExp;
        /** The Member of Class */
        private String mClassMember;
        /** The input full one rule with original names */
        private String mRule;
        /** The string format of the RegExp */
        private String mFormat;

        /**
         * Creates a new <code>FormatMatcher</code> instance.
         *
         * @param aInputRule input string with MemberDefinition and RegEx.
         * @param aCompileFlags the Pattern flags to compile the regexp with.
         *            See {@link Pattern#compile(java.lang.String, int)}
         * @throws ConversionException unable to parse aDefaultFormat.
         */
        public FormatMatcher(final String aInputRule, final int aCompileFlags)
        {
            mRule = aInputRule;
            mCompileFlags = aCompileFlags;
            try {
                // parse mClassMember
                mClassMember = aInputRule.substring(0, aInputRule.indexOf('('))
                        .trim();
                final String classMember = normalizeMembersNames(mClassMember
                        .toLowerCase());
                if (mClassMember.toLowerCase().equals(classMember)) {
                    // if Class Member has been specified wrong
                    throw new ConversionException("unable to parse "
                            + mClassMember);
                }
                else {
                    mClassMember = classMember;
                }

                // parse regExp
                String regExp = aInputRule.substring(
                        aInputRule.indexOf('(') + 1,
                        aInputRule.lastIndexOf(')'));
                if (regExp.isEmpty()) {
                    regExp = "$^"; // the empty regExp
                }
                updateRegexp(regExp, aCompileFlags);
            }
            catch (StringIndexOutOfBoundsException exp) {
                //if the structure of the input rule isn't correct
                throw new StringIndexOutOfBoundsException(
                        "unable to parse input rule: "
                        + aInputRule + " " + exp);
            }
        }

        /**
         * Finds correspondence between the reduced name of class member of and
         * its complete naming in system.
         *
         * @param aInputMemberName a string name which must be normalize.
         * @return correct name of member or initial string if no matches was
         *         found.
         */
        private static String normalizeMembersNames(
                String aInputMemberName)
        {
            String member = aInputMemberName;
            if ("field".equals(aInputMemberName)) {
                member = "VARIABLE_DEF";
            }
            else {
                if ("method".equals(aInputMemberName)) {
                    member = "METHOD_DEF";
                }
                else {
                    if ("ctor".equals(aInputMemberName)) {
                        member = "CTOR_DEF";
                    }
                    else {
                        if ("innerclass".equals(aInputMemberName)) {
                            member = "CLASS_DEF";
                        }
                    }
                }
            }
            return member;
        }

        /** @return the RegExp to match against */
        public final Pattern getRegexp()
        {
            return mRegExp;
        }

        /** @return the original immutable input rule */
        public final String getRule()
        {
            return mRule;
        }

        /** @return the Class Member */
        public final String getClassMember()
        {
            return mClassMember;
        }

        /**
         * Set the compile flags for the regular expression.
         *
         * @param aCompileFlags the compile flags to use.
         */
        public final void setCompileFlags(final int aCompileFlags)
        {
            updateRegexp(mFormat, aCompileFlags);
        }

        /**
         * Updates the regular expression using the supplied format and compiler
         * flags. Will also update the member variables.
         *
         * @param aFormat the format of the regular expression.
         * @param aCompileFlags the compiler flags to use.
         */
        private void updateRegexp(final String aFormat, final int aCompileFlags)
        {
            try {
                mRegExp = Utils.getPattern(aFormat, aCompileFlags);
                mFormat = aFormat;
            }
            catch (final PatternSyntaxException e) {
                throw new ConversionException("unable to parse " + aFormat, e);
            }
        }
    }

}