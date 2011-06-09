package com.github.sevntu.checkstyle.checks.coding;

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
 * Checks that the parts of a class(main, nested, member inner) declaration
 * appear in the rules order set by user using regular expressions.
 * <p>
 * The check forms line which consists of class member annotations, modifiers,
 * type and name from your code and compares it with your RegExp.
 * </p>
 * The rule consists of:
 *
 * <pre>
 * ClassMember(RegExp)
 * </pre>
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
 * RegExp can include:
 * <p>
 * <ol>
 * <li>Annotations</li>
 * <li>Modifiers(public, protected, private, abstract, static,
 * final)</li>
 * <li>Type</li>
 * <li>Name</li>
 * </ol>
 * </p>
 * ATTENTION!
 * <p>
 * Use separator <code>' ', '.', '\s'</code> between declaration in the RegExp.
 * </p>
 * <pre>
 * Example:
 *      Field(public.*final.*)
 *      Field(public final.*)
 *      Field(public<code>\s*</code>final.*)
 * </pre>
 * NOTICE!
 * <p>
 * If you set empty RegExp e.g. <code>Field()</code>, it means that class member
 * doesn't have modifiers(default modifier) and checking the type and name of
 * member doesn't occur.
 * </p>
 * <p>
 * Between the declaration of a array and generic can't be whitespaces.
 * E.g.: <code>ArrayList&lt;String[]&gt; someName</code>
 * </p>
 * <p>
 * Use the separator '###' between the class declarations.
 * </p>
 * <p>
 * For Example:
 * </p>
 * <p>
 * <code>Field(private static final long serialVersionUID) ###
 * Field(public static final.*) ### Field(.*private.*) ### Ctor(.*) ###
 * Method(.*public.*final.*|@Ignore.*public.*) ###
 * Method(public static.*(final|(new|edit|create).*).*) ###
 * InnerClass(public abstract.*)</code>
 * </p>
 *
 * @author <a href="mailto:solid.danil@gmail.com">Danil Lopatin</a>
 */
public class CustomDeclarationOrderCheck extends Check {

    /** Default format for custom declaration check */
    private static final String DEFAULT_DECLARATION = "Field(.*public.*)"
            + "### Field(.*protected.*) ### Field(.*private.*) ### CTOR(.*)"
            + "### Method(.*) ### InnerClass(.*)";
    /** List of order declaration customizing by user */
    private final ArrayList<FormatMatcher> mCustomOrderDeclaration =
            new ArrayList<FormatMatcher>();
    /**
     * List of Declaration States. This is necessary due to inner classes that
     * have their own state.
     */
    private final FastStack<ClassStates> mClassStates = new FastStack<ClassStates>();
    /** Initialization declaration order from an initial position */
    private static final int INITIAL_STATE = 0;
    /** save compile flags for further usage */
    private int mCompileFlags;
    /** Is current class as root */
    private boolean mClassRoot = true;
    /** allow check inner classes */
    private boolean mInnerClass;

    /** Private class to encapsulate the state. */
    private static class ClassStates {

        /** new state */
        private int mClassStates = INITIAL_STATE;
    }

    /** Constructor to set default format. */
    public CustomDeclarationOrderCheck() {
        setCustomDeclarationOrder(DEFAULT_DECLARATION);
    }

    /**
     * Parsing input line with custom declaration order into massive.
     *
     * @param aInputOrderDeclaration The string line with the user custom
     *            declaration.
     */
    public void setCustomDeclarationOrder(final String aInputOrderDeclaration) {
        if (!mCustomOrderDeclaration.isEmpty()) {
            mCustomOrderDeclaration.clear();
        }
        for (String currentState : aInputOrderDeclaration.split("\\s*###\\s*")) {
            mCustomOrderDeclaration.add(parseInputDeclarationRule(currentState));
        }
    }

    /**
     * Parse input current declaration rule and create new instance of
     * FormatMather with matcher
     *
     * @param aCurrentState input string with MemberDefinition and RegExp.
     * @return new FormatMatcher with parsed and compile rule
     */
    private FormatMatcher parseInputDeclarationRule(final String aCurrentState) {
        String classMember;
        String regExp;
        try {
            // parse mClassMember
            classMember = aCurrentState.substring(0,
                    aCurrentState.indexOf('(')).trim();
            final String classMemberNormalized =
                    normalizeMembersNames(classMember.toLowerCase());
            if (classMember.toLowerCase().equals(classMemberNormalized)) {
                // if Class Member has been specified wrong
                throw new ConversionException("unable to parse "
                        + classMember);
            } else {
                classMember = classMemberNormalized;
            }

            // parse regExp
            regExp = aCurrentState.substring(
                    aCurrentState.indexOf('(') + 1,
                    aCurrentState.lastIndexOf(')'));
            if (regExp.isEmpty()) {
                regExp = "package"; // package level
            }

        } catch (StringIndexOutOfBoundsException exp) {
            //if the structure of the input rule isn't correct
            throw new StringIndexOutOfBoundsException(
                    "unable to parse input rule: "
                    + aCurrentState + " " + exp);
        }

        final FormatMatcher matcher = new FormatMatcher(aCurrentState,
                classMember, mCompileFlags);
        matcher.updateRegexp(regExp, mCompileFlags);
        return matcher;
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
            String aInputMemberName) {
        String member = aInputMemberName;
        if ("field".equals(aInputMemberName)) {
            member = "VARIABLE_DEF";
        } else {
            if ("method".equals(aInputMemberName)) {
                member = "METHOD_DEF";
            } else {
                if ("ctor".equals(aInputMemberName)) {
                    member = "CTOR_DEF";
                } else {
                    if ("innerclass".equals(aInputMemberName)) {
                        member = "CLASS_DEF";
                    }
                }
            }
        }
        return member;
    }

    /**
     * Set whether or not the match is case sensitive.
     *
     * @param aCaseInsensitive true if the match is case insensitive.
     */
    public void setIgnoreRegExCase(final boolean aCaseInsensitive) {
        if (aCaseInsensitive) {
            if (!mCustomOrderDeclaration.isEmpty()) {
                for (FormatMatcher currentRule : mCustomOrderDeclaration) {
                    currentRule.setCompileFlags(Pattern.CASE_INSENSITIVE);
                }
            } else {
                mCompileFlags = Pattern.CASE_INSENSITIVE;
            }
        }
    }

    @Override
    public int[] getDefaultTokens() {
        //HashSet for unique Tokens
        final HashSet<String> classMembers = new HashSet<String>();

        for (FormatMatcher currentRule : mCustomOrderDeclaration) {
            // check existing of InnerClass in rule
            if ("CLASS_DEF".equals(currentRule.getClassMember())) {
                mInnerClass = true;
            } else {
                classMembers.add(currentRule.mClassMember); //add Tokens
            }
        }

        final int defaultTokens[] = new int[classMembers.size() + 1];
        defaultTokens[0] = TokenTypes.CLASS_DEF;

        int index = 1;
        for (String token : classMembers) {
            if ("VARIABLE_DEF".equals(token)) {
                defaultTokens[index] = TokenTypes.VARIABLE_DEF;
            } else if ("METHOD_DEF".equals(token)) {
                defaultTokens[index] = TokenTypes.METHOD_DEF;
            } else if ("CTOR_DEF".equals(token)) {
                defaultTokens[index] = TokenTypes.CTOR_DEF;
            } else {
                defaultTokens[index] = defaultTokens[0];
            }
            ++index;
        }
        return defaultTokens;
    }

    @Override
    public void visitToken(DetailAST aAST) {

        if (aAST.getType() == TokenTypes.CLASS_DEF) {
            if (mClassRoot) {
                mClassStates.push(new ClassStates());
                mClassRoot = false;
            } else {
                if (mInnerClass) {
                    //if we have condition to check Inner Classes order
                    checkOrderLogic(aAST);
                }
                mClassStates.push(new ClassStates());
            }
        } else {
            final int parentParentType = aAST.getParent().getParent().getType();
            if (parentParentType == TokenTypes.CLASS_DEF) {
                checkOrderLogic(aAST);
            }
        }
    }

    /**
     * Check class declaration order with custom declaration order.
     *
     * @param aAST current DetailAST state.
     */
    private void checkOrderLogic(final DetailAST aAST) {
        final ClassStates previousState = mClassStates.peek();
        final int currentState = getPosition(aAST);
        if (currentState >= 0) {
            if (previousState.mClassStates > currentState) {
                writeLog(aAST, currentState, previousState.mClassStates);
            } else {
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
    private int getPosition(final DetailAST aAST) {
        int result = -1;
        final String modifiers = getUniteModifiersList(aAST);
        for (int index = 0; index < mCustomOrderDeclaration.size(); index++) {
            final FormatMatcher currentRule = mCustomOrderDeclaration.get(index);
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
            final int aExpectPosition, final int aCurrentPosition) {
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
    public void leaveToken(DetailAST aAST) {
        if (aAST.getType() == TokenTypes.CLASS_DEF) {
            mClassStates.pop();
            if (mClassStates.isEmpty()) {
                mClassRoot = true;
            }
        }
    }

    /**
     * Use for concatenation modifiers, annotations, type and
     * name of member in single line. <br>
     * Contains TokenTypes parameters for entry in child. </br>
     *
     * @param aAST current DetailAST state.
     * @return the unit annotations and modifiers and list.
     */
    private String getUniteModifiersList(final DetailAST aAST) {
        final StringBuffer modifiers = new StringBuffer();
        DetailAST ast = aAST.findFirstToken(TokenTypes.MODIFIERS);
        if (null == ast.getFirstChild()) {
            //if we met package level modifier
            modifiers.append("package ");
        }
        while (ast.getType() != TokenTypes.IDENT) {
            if (ast != null && ast.getFirstChild() != null) {
                modifiers.append(concatLogic(ast.getFirstChild()));
                modifiers.append(" ");
            }
            ast = ast.getNextSibling();
        }
        // add IDENT(name)
        modifiers.append(ast.getText());

        return modifiers.toString();
    }

    /**
     * Use for recursive tree traversal from first child of current tree top.
     *
     * @param aAST current DetailAST state, first child of current tree top.
     * @return the unit modifiers and annotation list.
     */
    private String concatLogic(final DetailAST aAST) {
        DetailAST ast = aAST;
        String separator = "";
        final StringBuffer modifiers = new StringBuffer();

        if (ast.getParent().getType() == TokenTypes.MODIFIERS) {
            // add separator between access modifiers and annotations
            separator = " ";
        }
        while (ast != null) {
            if (ast.getFirstChild() != null) {
                modifiers.append(concatLogic(ast.getFirstChild()));
            } else {
                if (ast.getType() == TokenTypes.RBRACK) {
                    //if array
                    modifiers.append("[");
                }
                modifiers.append(ast.getText());
            }
            modifiers.append(separator);
            ast = ast.getNextSibling();
        }
        return modifiers.toString().trim();
    }

    /**
     * private class for members of class and their patterns.
     */
    private static class FormatMatcher {

        /**
         * Save compile flag. It can be necessary to further change the logic of
         * check.
         */
        private final int mCompileFlags;
        /** The regexp to match against */
        private Pattern mRegExp;
        /** The Member of Class */
        private final String mClassMember;
        /** The input full one rule with original names */
        private final String mRule;
        /** The string format of the RegExp */
        private String mFormat;

        /**
         * Creates a new <code>FormatMatcher</code> instance.
         *
         * @param aInputRule input string with MemberDefinition and RegExp.
         * @param aClassMember the member of class
         * @param aCompileFlags the Pattern flags to compile the regexp with.
         *            See {@link Pattern#compile(java.lang.String, int)}
         */
        public FormatMatcher(final String aInputRule,
                final String aClassMember, final int aCompileFlags) {
            mClassMember = aClassMember;
            mCompileFlags = aCompileFlags;
            mRule = aInputRule;

        }

        /** @return the RegExp to match against */
        public final Pattern getRegexp() {
            return mRegExp;
        }

        /** @return the original immutable input rule */
        public final String getRule() {
            return mRule;
        }

        /** @return the Class Member */
        public final String getClassMember() {
            return mClassMember;
        }

        /**
         * Set the compile flags for the regular expression.
         *
         * @param aCompileFlags the compile flags to use.
         */
        public final void setCompileFlags(final int aCompileFlags) {
            updateRegexp(mFormat, aCompileFlags);
        }

        /**
         * Updates the regular expression using the supplied format and compiler
         * flags. Will also update the member variables.
         *
         * @param aFormat the format of the regular expression.
         * @param aCompileFlags the compiler flags to use.
         */
        private void updateRegexp(final String aFormat, final int aCompileFlags) {
            try {
                mRegExp = Utils.getPattern(aFormat, aCompileFlags);
                mFormat = aFormat;
            } catch (final PatternSyntaxException e) {
                throw new ConversionException("unable to parse " + aFormat, e);
            }
        }
    }
}
