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
import java.util.Iterator;
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
 * Checks that the parts of a class declaration appear in the rules order set
 * by user using regular expressions.<br>
 * The rule consists of:
 * <pre> 
 * ClassMember(RegExp)
 * </pre>
 * </p>
 * To set class order use the following notation of the class members
 * (register is not important):
 * <p>
 * <ol>
 * <li>"Field" to denote the Fields</li>
 * <li>"CTOR" to denote the Constructors</li>
 * <li>"Method" to denote the Methods</li>
 * <li>"InnerClass" to denote the Inner Classes</li>
 * </ol>
 * <p>
 * RegExp can include modifiers(public, protected, private,
 * abstract, static and others) and annotations of a class member.
 * </p>
 * </p>
 * <p>For Example:</p>
 * <pre>
 * <code>Method(.*final.*public|.*public.*final|@Ignore.*public.*)</code>
 * </pre>
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

    /** Private class to encapsulate the state */
    private static class ClassStates
    {
        /** new state */
        private int mClassStates = INITIAL_STATE;
    }

    /** Is current class as root */
    private boolean mClassRoot = true;

    /**
     * Parsing input line with custom declaration order into massive.
     * 
     * @param aInputOrderDeclaration The string line with the user custom
     *            declaration.
     */
    public void setCustomDeclarationOrder(final String aInputOrderDeclaration)
    {
        for (String currentState : aInputOrderDeclaration.split("\\s*###\\s*"))
       {
            mCustomOrderDeclaration.add(new FormatMatcher(currentState));
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
                FormatMatcher.setFlags(Pattern.CASE_INSENSITIVE);
            }
        }
    }

    @Override
    public int[] getDefaultTokens()
    {
        //HashSet for unique Tokens
        final HashSet<String> classMembers = new HashSet<String>();

        for (FormatMatcher currentRule : mCustomOrderDeclaration) {
            classMembers.add(currentRule.mClassMember); //add Tokens
        }

        int defaultTokens[] = new int[classMembers.size()];
        Iterator<String> iteratorClassMember = classMembers.iterator();

        for (int i = 0; i < defaultTokens.length; i++) {
            final String token = iteratorClassMember.next();
            if (token.equals("VARIABLE_DEF")) {
                defaultTokens[i] = TokenTypes.VARIABLE_DEF;
            }
            else if (token.equals("METHOD_DEF")) {
                defaultTokens[i] = TokenTypes.METHOD_DEF;
            }
            else if (token.equals("CTOR_DEF")) {
                defaultTokens[i] = TokenTypes.CTOR_DEF;
            }
            else if (token.equals("CLASS_DEF")) {
                defaultTokens[i] = TokenTypes.CLASS_DEF;
            }
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
                checkOrderLogic(aAST);
                mClassStates.push(new ClassStates());
            }
        }
        else {
            final int parentType = aAST.getParent().getType();
            if (parentType != TokenTypes.OBJBLOCK) {
                return;
            }
            checkOrderLogic(aAST);
        }
    }

    /**
     * Check class declaration order with custom declaration order.
     * 
     * @param aAST current DetailAST state.
     */
    private final void checkOrderLogic(DetailAST aAST)
    {
        ClassStates state = mClassStates.peek();
        final int position = getPosition(aAST);
        if (position >= 0) {
            if (state.mClassStates > position) {
                writeLog(aAST);
            }
            else {
                state.mClassStates = position;
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
    private int getPosition(DetailAST aAST)
    {
        final String modifiers = getUniteModifiersList(aAST);
        for (int i = 0; i < mCustomOrderDeclaration.size(); i++) {
            final FormatMatcher currentRule = mCustomOrderDeclaration.get(i);
            if (currentRule.getClassMember().equals(aAST.getText())) {
                /* find correspondence between list of modifiers and RegExp */
                if (currentRule.getRegexp().matcher(modifiers).find()) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * Writes log according to met type of token.
     * 
     * @param aAST state for log.
     */
    private final void writeLog(DetailAST aAST)
    {
        switch (aAST.getType()) {
        case TokenTypes.VARIABLE_DEF:
            log(aAST, "declaration.order.field");
            break;
        case TokenTypes.METHOD_DEF:
            log(aAST, "declaration.order.method");
            break;
        case TokenTypes.CTOR_DEF:
            log(aAST, "declaration.order.constructor");
            break;
        case TokenTypes.CLASS_DEF:
            log(aAST, "declaration.order.class");
            break;
        default:
        }
    }

    @Override
    public void leaveToken(DetailAST aAST)
    {
        if (aAST.getType() == TokenTypes.CLASS_DEF) {
            mClassStates.pop();
        }
    }

    /**
     * Use for concatenation modifiers and annotations in single line. <br>
     * Contains TokenTypes parameters for entry in child.
     * 
     * @param aAST current DetailAST state.
     * @return the unit annotations and modifiers and list.
     */
    private String getUniteModifiersList(DetailAST aAST)
    {
        String modifiers = "";
        aAST = aAST.findFirstToken(TokenTypes.MODIFIERS);

        if (aAST != null && aAST.getFirstChild() != null) {
            aAST = aAST.getFirstChild();
            modifiers += concatLogic(aAST);
        }
        return modifiers;
    }

    /**
     * Use for recursive tree traversal from first child of current tree top.
     * 
     * @param aAST current DetailAST state, first child of current tree top.
     * @return the unit modifiers and annotation list.
     */
    private String concatLogic(DetailAST aAST)
    {
        String modifiers = "";

        while (aAST != null) {
            if (aAST.getType() == TokenTypes.ANNOTATION
                    || aAST.getType() == TokenTypes.EXPR) {
                modifiers += concatLogic(aAST.getFirstChild());
                aAST = aAST.getNextSibling();
            }
            else {
                modifiers += aAST.getText();
                aAST = aAST.getNextSibling();
            }
        }
        return modifiers;
    }

    /**
     * private class for members of class and their patterns.
     */
    private static class FormatMatcher
    {
        /**
         * The flags to create the regular expression with. <br>
         * Default compile flag is 0 (the default).
         */
        private static int mCompileFlags = 0;
        /** The regexp to match against */
        private Pattern mRegExp;
        /** The Member of Class */
        private String mClassMember;
        /** The string format of the RegExp */
        private String mFormat;

        /**
         * Creates a new <code>FormatMatcher</code> instance. Parse into Member
         * and RegEx. Defaults the compile flag to 0 (the default).
         * 
         * @param aInputRule input string with MemberDefinition and RegEx.
         * @throws ConversionException unable to parse aDefaultFormat.
         */
        public FormatMatcher(final String aInputRule)
        {
            this(aInputRule, mCompileFlags);
        }

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
            try {
                /* parse mClassMember */
                mClassMember = aInputRule.substring(0, aInputRule.indexOf('('))
                        .trim().toLowerCase();
                final String classMember = normalizeMembersNames(mClassMember);
                if (mClassMember.equals(classMember)) {
                    /* if Class Member has been specified wrong */
                    throw new ConversionException("unable to parse "
                            + mClassMember);
                }
                else {
                    mClassMember = classMember;
                }

                /* parse regExp */
                String regExp = aInputRule.substring(
                        aInputRule.indexOf('(') + 1,
                        aInputRule.lastIndexOf(')'));
                if (regExp.isEmpty()) {
                    regExp = "$^"; // the empty regExp
                }
                updateRegexp(regExp, aCompileFlags);
            }
            catch (StringIndexOutOfBoundsException exp) {
                /* if the structure of the input rule isn't correct */
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
        private static final String normalizeMembersNames(
                String aInputMemberName)
        {
            if (aInputMemberName.equals("field")) {
                return "VARIABLE_DEF";
            }
            else {
                if (aInputMemberName.equals("method")) {
                    return "METHOD_DEF";
                }
                else {
                    if (aInputMemberName.equals("ctor")) {
                        return "CTOR_DEF";
                    }
                    else {
                        if (aInputMemberName.equals("innerclass")) {
                            return "CLASS_DEF";
                        }
                    }
                }
            }
            return aInputMemberName;
        }

        /**
         * Saving compile flags for further usage.
         * 
         * @param aCompileFlags the aCompileFlags to set.
         */
        public static final void setFlags(final int aCompileFlags)
        {
            mCompileFlags = aCompileFlags;
        }

        /** @return the RegExp to match against */
        public final Pattern getRegexp()
        {
            return mRegExp;
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