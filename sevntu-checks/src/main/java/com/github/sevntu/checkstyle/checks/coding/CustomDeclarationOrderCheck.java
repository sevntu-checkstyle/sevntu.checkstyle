///////////////////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code and other text files for adherence to a set of rules.
// Copyright (C) 2001-2026 the original author or authors.
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
///////////////////////////////////////////////////////////////////////////////////////////////

package com.github.sevntu.checkstyle.checks.coding;

import java.beans.Introspector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.github.sevntu.checkstyle.SevntuUtil;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * Checks that the parts of a class(main, nested, member inner) declaration
 * appear in the rules order set by user using regular expressions.
 * </p>
 * <p>
 * The check forms line which consists of class member annotations, modifiers,
 * type and name from your code and compares it with your RegExp.
 * </p>
 * The rule consists of:
 *
 * <pre>
 * ClassMember(RegExp)
 * </pre>
 * To set class order use the following notation of the class members (case
 * insensitive):
 * <ol>
 * <li>"Field" to denote the Fields</li>
 * <li>"DeclareAnonClassField" to denote the fields keeping objects of anonymous classes</li>
 * <li>"Ctor" to denote the Constructors</li>
 * <li>"Method" to denote the Methods</li>
 * <li>"GetterSetter" to denote the group of getter and setter methods</li>
 * <li>"MainMethod" to denote the main method</li>
 * <li>"InnerClass" to denote the Inner Classes</li>
 * <li>"InnerInterface" to denote the Inner Interfaces</li>
 * <li>"InnerEnum" to denote the Inner Enums</li>
 * </ol>
 * RegExp can include:
 * <ol>
 * <li>Annotations</li>
 * <li>Modifiers(public, protected, private, abstract, static,
 * final)</li>
 * <li>Type</li>
 * <li>Name</li>
 * </ol>
 * ATTENTION!
 * <p>
 * Use separator <code>' ', '.', '\s'</code> between declaration in the RegExp.
 * Whitespace should be added after each modifier.
 * </p>
 * <pre>
 * Example:
 *      Field(public .*final .*)
 *      Field(public final .*)
 *      Field(public<code>\s*</code>final .*)
 * </pre>
 * NOTICE!
 * <p>
 * It is important to write exact order of modifiers in rules. So rule
 * <code><i>Field(public final)</i></code> does not match to
 * <code><i>final public value;</i></code>.
 * <a href='https://checkstyle.org/config_modifier.html#ModifierOrder'>
 * ModifierOrderCheck</a>
 * is recommended to use.
 * </p>
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
 * Field(public static final .*) ### Field(.*private .*) ### Ctor(.*) ###
 * GetterSetter(.*) ### Method(.*public .*final .*|@Ignore.*public .*) ###
 * Method(public static .*(final|(new|edit|create).*).*) ###
 * InnerClass(public abstract .*) ### InnerInterface(.*) ### InnerEnum(.*)</code>
 * </p>
 *
 * <p><b>What is group of getters and setters(<code>GetterSetter</code>)?</b></p>
 *
 * <p>
 * It is ordered sequence of getters and setters like:
 * </p>
 *
 * <pre>
 * public int getValue() {
 *     log.info("Getting value");
 *     return value;
 * }
 *
 * public void setValue(int newValue) {
 *     value = newValue;
 * }
 *
 * public Object getObj() {
 *    return obj;
 * }
 *
 * public void setObj(Object obj) {
 *    if (obj != null) {
 *      this.obj = obj;
 *    } else {
 *      throw new IllegalArgumentException("Null value");
 *    }
 * }
 *
 * ...
 * </pre>
 * <p>Getter is public method that returns class field. Name of getter should be
 * 'get<i>FieldName</i>' in camel case.</p>
 * <p>Setter is public method with one parameter that assigns this parameter to class field.
 * Name of setter should be 'set<i>FieldName</i>' in camel case.</p>
 * <p>Setter of field X should be right after getter of field X.</p>
 *
 * @author <a href="mailto:solid.danil@gmail.com">Danil Lopatin</a>
 * @author <a href="mailto:barataliba@gmail.com">Baratali Izmailov</a>
 * @since 1.8.0
 */
public class CustomDeclarationOrderCheck extends AbstractCheck {

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY_FIELD = "custom.declaration.order.field";

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY_METHOD = "custom.declaration.order.method";

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY_CONSTRUCTOR = "custom.declaration.order.constructor";

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY_CLASS = "custom.declaration.order.class";

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY_INTERFACE = "custom.declaration.order.interface";

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY_ENUM = "custom.declaration.order.enum";

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY_INVALID_SETTER = "custom.declaration.order.invalid.setter";

    /** Macro string for inner enumeration. */
    private static final String INNER_ENUM_MACRO = "InnerEnum";

    /** Macro string for inner interface. */
    private static final String INNER_INTERFACE_MACRO = "InnerInterface";

    /** Macro string for inner class. */
    private static final String INNER_CLASS_MACRO = "InnerClass";

    /** Macro string for constructor. */
    private static final String CTOR_MACRO = "Ctor";

    /** Macro string for method. */
    private static final String METHOD_MACRO = "Method";

    /** Macro string for anonymous class field. */
    private static final String ANON_CLASS_FIELD_MACRO = "DeclareAnonClassField";

    /** Macro string for field. */
    private static final String FIELD_MACRO = "Field";

    /** Macro string for getter and setter. */
    private static final String GETTER_SETTER_MACRO = "GetterSetter";

    /** Macro string for main method. */
    private static final String MAIN_METHOD_MACRO = "MainMethod";

    /** Prefix for boolean getter method name. */
    private static final String BOOLEAN_GETTER_PREFIX = "is";

    /** Prefix for getter method name. */
    private static final String GETTER_PREFIX = "get";

    /** Prefix for setter method name. */
    private static final String SETTER_PREFIX = "set";

    /** Macro string for String. */
    private static final String STRING_MACRO = "String";

    /** Default format for custom declaration check. */
    private static final String DEFAULT_DECLARATION = "Field(.*public .*) "
            + "### Field(.*protected .*) ### Field(.*private .*) ### CTOR(.*) ### "
            + "MainMethod(.*) ### GetterSetter(.*) ### Method(.*) ### InnerClass(.*) "
            + "### InnerInterface(.*) ### InnerEnum(.*)";

    /**
     * Compares line numbers.
     */
    private static final Comparator<DetailAST> AST_LINE_COMPARATOR = new Comparator<>() {
        @Override
        public int compare(DetailAST aObj1, DetailAST aObj2) {
            return aObj1.getLineNo() - aObj2.getLineNo();
        }
    };

    /** List of order declaration customizing by user. */
    private final List<FormatMatcher> customOrderDeclaration =
        new ArrayList<>();

    /** Save compile flags for further usage. */
    private int compileFlags;

    /** Allow check inner classes. */
    private boolean checkInnerClasses;

    /**
     * Allows to check getters and setters.
     */
    private boolean checkGettersSetters;

    /**
     * Prefix of class fields.
     */
    private String fieldPrefix = "";

    /**
     * Stack of GetterSetterContainer objects to keep all getters and all setters
     * of certain class.
     */
    private final Deque<ClassDetail> classDetails = new LinkedList<>();

    /** Constructor to set default format. */
    public CustomDeclarationOrderCheck() {
        setCustomDeclarationOrder(DEFAULT_DECLARATION);
    }

    /**
     * Set custom order declaration from string with user rules.
     *
     * @param inputOrderDeclaration The string line with the user custom
     *            declaration.
     * @throws IllegalArgumentException if the input rule could not be parsed
     */
    public final void setCustomDeclarationOrder(final String inputOrderDeclaration) {
        customOrderDeclaration.clear();
        for (String currentState : inputOrderDeclaration.split("\\s*###\\s*")) {
            try {
                customOrderDeclaration
                        .add(parseInputDeclarationRule(currentState));
            }
            catch (StringIndexOutOfBoundsException exp) {
                // if the structure of the input rule isn't correct
                throw new IllegalArgumentException("Unable to parse input rule: "
                        + currentState, exp);
            }
        }
    }

    /**
     * Set prefix of class fields.
     *
     * @param fieldPrefix string
     */
    public void setFieldPrefix(String fieldPrefix) {
        this.fieldPrefix = fieldPrefix;
    }

    /**
     * Set whether or not the match is case sensitive.
     *
     * @param caseSensitive true if the match is case sensitive.
     */
    public void setCaseSensitive(final boolean caseSensitive) {
        // 0 - case sensitive flag
        if (caseSensitive) {
            compileFlags = 0;
        }
        else {
            compileFlags = Pattern.CASE_INSENSITIVE;
        }

        for (FormatMatcher currentRule : customOrderDeclaration) {
            currentRule.setCompileFlags(compileFlags);
        }
    }

    @Override
    public int[] getDefaultTokens() {
        final int size = customOrderDeclaration.size();
        final int[] tokenTypes = new int[size + 1];

        for (int index = 0; index < size; index++) {
            final FormatMatcher currentRule = customOrderDeclaration.get(index);
            tokenTypes[index] = currentRule.getClassMember();

            if (currentRule.hasRule(INNER_CLASS_MACRO)) {
                checkInnerClasses = true;
            }
            else if (currentRule.hasRule(GETTER_SETTER_MACRO)) {
                checkGettersSetters = true;
            }
        }

        tokenTypes[size] = TokenTypes.CLASS_DEF;

        return tokenTypes;
    }

    @Override
    public int[] getAcceptableTokens() {
        return getDefaultTokens();
    }

    @Override
    public int[] getRequiredTokens() {
        return getDefaultTokens();
    }

    @Override
    public void visitToken(DetailAST ast) {
        if (ast.getType() == TokenTypes.CLASS_DEF) {
            validateClassDef(ast);
        }
        else {
            validateNonClassDef(ast);
        }
    }

    /**
     * Validate the class definition.
     *
     * @param ast The class definition.
     */
    private void validateClassDef(DetailAST ast) {
        if (!isClassDefInMethodDef(ast)) {
            if (checkInnerClasses && !classDetails.isEmpty()) {
                final int position = getPositionInOrderDeclaration(ast);

                if (position != -1) {
                    if (isWrongPosition(position)) {
                        logWrongOrderedElement(ast, position);
                    }
                    else {
                        classDetails.peek().setCurrentPosition(position);
                    }
                }
            }

            classDetails.push(new ClassDetail());
        }
    }

    /**
     * Validate the {@code ast} which is not a class definition.
     *
     * @param ast The AST to validate.
     */
    private void validateNonClassDef(DetailAST ast) {
        final DetailAST objBlockAst = ast.getParent();
        if (objBlockAst != null
            && objBlockAst.getType() == TokenTypes.OBJBLOCK) {
            final DetailAST classDefAst = objBlockAst.getParent();

            if (classDefAst.getType() == TokenTypes.CLASS_DEF
                && !isClassDefInMethodDef(classDefAst)) {
                if (checkGettersSetters) {
                    collectGetterSetter(ast);
                }

                final int position = getPositionInOrderDeclaration(ast);

                if (position != -1) {
                    if (isWrongPosition(position)) {
                        logWrongOrderedElement(ast, position);
                    }
                    else {
                        classDetails.peek().setCurrentPosition(position);
                    }
                }
            }
        }
    }

    @Override
    public void leaveToken(DetailAST ast) {
        if (ast.getType() == TokenTypes.CLASS_DEF
                && !isClassDefInMethodDef(ast)) {
            // -@cs[MoveVariableInsideIf] assignment value is a modification
            // call so it can't be moved
            final ClassDetail classDetail = classDetails.pop();

            if (checkGettersSetters) {
                final Map<DetailAST, DetailAST> gettersSetters =
                        classDetail.getWrongOrderedGettersSetters();

                logWrongOrderedSetters(gettersSetters);
            }
        }
    }

    /**
     * Parse input current declaration rule and create new instance of
     * FormatMather with matcher.
     *
     * @param currentState input string with MemberDefinition and RegExp.
     * @return new FormatMatcher with parsed and compile rule
     * @throws IllegalArgumentException if the macro could not be parsed
     */
    private FormatMatcher parseInputDeclarationRule(final String currentState) {
        // parse mClassMember
        final String macro = currentState.substring(0,
                currentState.indexOf('(')).trim();
        final int classMember = convertMacroToTokenType(macro);
        if (classMember == -1) {
            // if Class Member has been specified wrong
            throw new IllegalArgumentException("Unable to parse " + macro);
        }

        // parse regExp
        String regExp = currentState.substring(
                currentState.indexOf('(') + 1,
                currentState.lastIndexOf(')'));
        if (regExp.isEmpty()) {
            // package level
            regExp = "package";
        }

        final FormatMatcher matcher = new FormatMatcher(currentState, classMember);
        matcher.updateRegexp(regExp, compileFlags);

        return matcher;
    }

    /**
     * Finds correspondence between the reduced name of class member of and
     * its complete naming in system.
     *
     * @param inputMemberName a string name which must be normalize.
     * @return correct name of member or initial string if no matches was
     *         found.
     */
    private static int convertMacroToTokenType(
            String inputMemberName) {
        int result = -1;
        if (FIELD_MACRO.equalsIgnoreCase(inputMemberName)
                || ANON_CLASS_FIELD_MACRO.equalsIgnoreCase(inputMemberName)) {
            result = TokenTypes.VARIABLE_DEF;
        }
        else if (GETTER_SETTER_MACRO.equalsIgnoreCase(inputMemberName)
                || METHOD_MACRO.equalsIgnoreCase(inputMemberName)
                || MAIN_METHOD_MACRO.equalsIgnoreCase(inputMemberName)) {
            result = TokenTypes.METHOD_DEF;
        }
        else if (CTOR_MACRO.equalsIgnoreCase(inputMemberName)) {
            result = TokenTypes.CTOR_DEF;
        }
        else if (INNER_CLASS_MACRO.equalsIgnoreCase(inputMemberName)) {
            result = TokenTypes.CLASS_DEF;
        }
        else if (INNER_INTERFACE_MACRO.equalsIgnoreCase(inputMemberName)) {
            result = TokenTypes.INTERFACE_DEF;
        }
        else if (INNER_ENUM_MACRO.equalsIgnoreCase(inputMemberName)) {
            result = TokenTypes.ENUM_DEF;
        }
        return result;
    }

    /**
     * Verify that class definition is in method definition.
     *
     * @param classDef
     *        DetailAST of CLASS_DEF.
     * @return true if class definition is in method definition.
     */
    private static boolean isClassDefInMethodDef(DetailAST classDef) {
        boolean result = false;
        DetailAST currentParentAst = classDef.getParent();
        while (currentParentAst != null) {
            if (currentParentAst.getType() == TokenTypes.METHOD_DEF) {
                result = true;
                break;
            }
            currentParentAst = currentParentAst.getParent();
        }
        return result;
    }

    /**
     * Logs wrong ordered element.
     *
     * @param ast DetailAST of any class element.
     * @param position Position in the custom order declaration.
     */
    private void logWrongOrderedElement(final DetailAST ast, final int position) {
        String token = null;
        switch (ast.getType()) {
            case TokenTypes.VARIABLE_DEF:
                token = MSG_KEY_FIELD;
                break;
            case TokenTypes.METHOD_DEF:
                token = MSG_KEY_METHOD;
                break;
            case TokenTypes.CTOR_DEF:
                token = MSG_KEY_CONSTRUCTOR;
                break;
            case TokenTypes.CLASS_DEF:
                token = MSG_KEY_CLASS;
                break;
            case TokenTypes.INTERFACE_DEF:
                token = MSG_KEY_INTERFACE;
                break;
            case TokenTypes.ENUM_DEF:
                token = MSG_KEY_ENUM;
                break;
            default:
                SevntuUtil.reportInvalidToken(ast.getType());
                break;
        }

        final int expectedPosition = classDetails.peek().getCurrentPosition();
        log(ast,
                token,
                customOrderDeclaration.get(position).getRule(),
                customOrderDeclaration.get(expectedPosition).getRule());
    }

    /**
     * Check that position is wrong in custom declaration order.
     *
     * @param position position of class member.
     * @return true if position is wrong.
     */
    private boolean isWrongPosition(final int position) {
        boolean result = false;
        final ClassDetail classDetail = classDetails.peek();
        final Integer classCurrentPosition = classDetail.getCurrentPosition();
        if (classCurrentPosition > position) {
            result = true;
        }
        return result;
    }

    /**
     * Log wrong ordered setters.
     *
     * @param gettersSetters map that has getter as key and setter as value.
     */
    private void logWrongOrderedSetters(Map<DetailAST, DetailAST> gettersSetters) {
        for (Entry<DetailAST, DetailAST> entry: gettersSetters.entrySet()) {
            final DetailAST setterAst = entry.getKey();
            final DetailAST getterAst = entry.getValue();

            log(setterAst,
                    MSG_KEY_INVALID_SETTER,
                    getIdentifier(setterAst),
                    getIdentifier(getterAst));
        }
    }

    /**
     * If method definition is getter or setter,
     * then adds this method to collection.
     *
     * @param methodDefAst DetailAST of method definition.
     */
    private void collectGetterSetter(DetailAST methodDefAst) {
        if (methodDefAst.getType() == TokenTypes.METHOD_DEF) {
            final String methodName = getIdentifier(methodDefAst);
            if (isGetterName(methodName)) {
                if (isGetterCorrect(methodDefAst, GETTER_PREFIX)) {
                    classDetails.peek().addGetter(methodDefAst);
                }
            }
            else if (isBooleanGetterName(methodName)) {
                if (isGetterCorrect(methodDefAst, BOOLEAN_GETTER_PREFIX)) {
                    classDetails.peek().addGetter(methodDefAst);
                }
            }
            else if (isSetterName(methodName)
                    && isSetterCorrect(methodDefAst, SETTER_PREFIX)) {
                classDetails.peek().addSetter(methodDefAst);
            }
        }
    }

    /**
     * Search in existing custom declaration order current aAST state. It's
     * necessary for getting order of declarations.
     *
     * @param ast current DetailAST state.
     * @return position in the list of the sequence declaration if
     *         correspondence has been found. Else -1.
     */
    private int getPositionInOrderDeclaration(final DetailAST ast) {
        int result = -1;
        final String modifiers = getCombinedModifiersList(ast);
        for (int index = 0; result != 1 && index < customOrderDeclaration.size(); index++) {
            final FormatMatcher currentRule = customOrderDeclaration.get(index);
            if (currentRule.getClassMember() == ast.getType()
                    && currentRule.getRegexp().matcher(modifiers).find()) {
                if (currentRule.hasRule(ANON_CLASS_FIELD_MACRO)
                        || currentRule.hasRule(GETTER_SETTER_MACRO)
                        || currentRule.hasRule(MAIN_METHOD_MACRO)) {
                    final String methodName = getIdentifier(ast);
                    final ClassDetail classDetail = classDetails.peek();

                    if (isAnonymousClassField(ast)
                            || classDetail.containsGetter(methodName)
                            || classDetail.containsSetter(methodName)
                            || isMainMethod(ast)) {
                        result = index;
                    }
                }
                else {
                    // if more than one rule matches current AST node, then keep first one
                    if (result == -1) {
                        result = index;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Verify that there is anonymous class in variable definition and this
     * variable is a field.
     *
     * @param varDefinitionAst
     *        DetailAST of variable definition.
     * @return true if there is anonymous class in variable definition and this
     *         variable is a field.
     */
    private static boolean isAnonymousClassField(DetailAST varDefinitionAst) {
        boolean result = false;
        // ClassDef -> ObjBlock -> VarDef
        final int parentType = varDefinitionAst.getParent().getParent().getType();
        if (parentType == TokenTypes.CLASS_DEF) {
            final DetailAST assignAst = varDefinitionAst
                    .findFirstToken(TokenTypes.ASSIGN);
            if (assignAst != null) {
                final DetailAST expressionToAssignAst = assignAst
                        .findFirstToken(TokenTypes.EXPR);
                result = expressionToAssignAst != null
                        && isAnonymousClass(expressionToAssignAst);
            }
        }
        return result;
    }

    /**
     * Verify that method name starts with getter prefix (get).
     *
     * @param methodName method name
     * @return true if method name starts with getter prefix.
     */
    private static boolean isGetterName(String methodName) {
        return methodName.startsWith(GETTER_PREFIX);
    }

    /**
     * Verify that method name starts with boolean getter prefix (is).
     *
     * @param methodName method name
     * @return true if method name starts with boolean getter prefix.
     */
    private static boolean isBooleanGetterName(String methodName) {
        return methodName.startsWith(BOOLEAN_GETTER_PREFIX);
    }

    /**
     * Verify that method name starts with setter prefix (set).
     *
     * @param methodName method name
     * @return true if method name starts with setter prefix.
     */
    private static boolean isSetterName(String methodName) {
        return methodName.startsWith(SETTER_PREFIX);
    }

    /**
     * Returns true when getter is correct. Correct getter is method that has no parameters,
     * returns class field and has name 'get<i>FieldName</i>'.
     *
     * @param methodDef
     *        - DetailAST contains method definition.
     * @param methodPrefix
     *          Prefix for method (get, set, is).
     * @return true when getter is correct.
     */
    private boolean isGetterCorrect(DetailAST methodDef, String methodPrefix) {
        boolean result = false;

        final DetailAST parameters = methodDef.findFirstToken(TokenTypes.PARAMETERS);

        // no parameters
        if (parameters.getChildCount() == 0) {
            final DetailAST statementsAst = methodDef.findFirstToken(TokenTypes.SLIST);
            if (statementsAst != null) {
                final DetailAST returnStatementAst = statementsAst
                        .findFirstToken(TokenTypes.LITERAL_RETURN);

                if (returnStatementAst != null) {
                    final DetailAST exprAst = returnStatementAst.getFirstChild();
                    final String returnedFieldName = getNameOfGetterField(exprAst);
                    final String methodName = getIdentifier(methodDef);
                    final String methodNameWithoutPrefix = getNameWithoutPrefix(methodName,
                            methodPrefix);
                    if (returnedFieldName != null
                            && !localVariableHidesField(statementsAst, returnedFieldName)
                            && verifyFieldAndMethodName(returnedFieldName,
                                    methodNameWithoutPrefix)) {
                        result = true;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Checks if a local variable hides a field.
     *
     * @param slist The token to examine.
     * @param fieldName The name of the field.
     * @return true if the local variable is hidden from a field.
     */
    private static boolean localVariableHidesField(DetailAST slist,
            String fieldName) {
        boolean result = false;
        DetailAST currNode = slist.getFirstChild();
        while (currNode != null) {
            if (currNode.getType() == TokenTypes.VARIABLE_DEF
                    && fieldName.equals(getIdentifier(currNode))) {
                result = true;
                break;
            }
            currNode = currNode.getNextSibling();
        }
        return result;
    }

    /**
     * Returns true when setter is correct. Correct setter is method that has one parameter,
     * assigns this parameter to class field and has name 'set<i>FieldName</i>'.
     *
     * @param methodDefAst
     *        - DetailAST contains method definition.
     * @param methodPrefix
     *          Prefix for method (get, set, is).
     * @return true when setter is correct.
     */
    private boolean isSetterCorrect(DetailAST methodDefAst, String methodPrefix) {
        boolean result = false;

        final DetailAST methodTypeAst = methodDefAst.findFirstToken(TokenTypes.TYPE);

        if (methodTypeAst.findFirstToken(TokenTypes.LITERAL_VOID) != null) {
            final DetailAST statementsAst = methodDefAst.findFirstToken(TokenTypes.SLIST);
            final String methodName = getIdentifier(methodDefAst);
            final String setterFieldName = fieldPrefix
                    + getNameWithoutPrefix(methodName, methodPrefix);

            result = statementsAst != null
                    && !localVariableHidesField(statementsAst, setterFieldName)
                    && isFieldUpdate(statementsAst, setterFieldName);
        }
        return result;
    }

    /**
     * Verify that expression is anonymous class.
     *
     * @param expressionAst
     *        DetailAST of expression.
     * @return true if expression is anonymous class.
     */
    private static boolean isAnonymousClass(DetailAST expressionAst) {
        boolean result = false;
        final DetailAST literalNewAst = expressionAst
                .findFirstToken(TokenTypes.LITERAL_NEW);
        if (literalNewAst != null) {
            final DetailAST objBlockAst = literalNewAst.findFirstToken(TokenTypes.OBJBLOCK);
            result = objBlockAst != null;
        }
        return result;
    }

    /**
     * Use for concatenation modifiers, annotations, type and
     * name of member in single line. <br>
     * Contains TokenTypes parameters for entry in child.
     *
     * @param ast current DetailAST state.
     * @return the unit annotations and modifiers and list.
     */
    private static String getCombinedModifiersList(final DetailAST ast) {
        final StringBuilder modifiers = new StringBuilder();
        DetailAST astNode = ast.findFirstToken(TokenTypes.MODIFIERS);
        if (astNode.getFirstChild() == null) {
            // if we met package level modifier
            modifiers.append("package ");
        }

        while (astNode.getType() != TokenTypes.IDENT) {
            if (astNode.getFirstChild() != null) {
                modifiers.append(getModifiersAsText(astNode.getFirstChild()));
                modifiers.append(' ');
            }
            astNode = astNode.getNextSibling();
        }
        // add IDENT(name)
        modifiers.append(astNode.getText());

        return modifiers.toString();
    }

    /**
     * Get text representation of MODIFIERS node.
     *
     * @param ast current DetailAST node.
     * @return text representation of MODIFIERS node.
     */
    private static String getModifiersAsText(final DetailAST ast) {
        DetailAST astNode = ast;
        String separator = "";
        final StringBuilder modifiers = new StringBuilder();

        if (astNode.getParent().getType() == TokenTypes.MODIFIERS) {
            // add separator between access modifiers and annotations
            separator = " ";
        }
        while (astNode != null) {
            if (astNode.getFirstChild() == null) {
                if (astNode.getType() == TokenTypes.RBRACK) {
                    // if array
                    modifiers.append('[');
                }
                modifiers.append(astNode.getText());
            }
            else {
                modifiers.append(getModifiersAsText(astNode.getFirstChild()));
            }
            modifiers.append(separator);
            astNode = astNode.getNextSibling();
        }
        return modifiers.toString().trim();
    }

    /**
     * Get name without prefix.
     *
     * @param name name
     * @param prefix prefix
     * @return name without prefix or null if name does not have such prefix.
     */
    private static String getNameWithoutPrefix(String name, String prefix) {
        String result = null;
        if (name.startsWith(prefix)) {
            result = name.substring(prefix.length());
            result = Introspector.decapitalize(result);
        }
        return result;
    }

    /**
     * Get identifier of AST. These can be names of types, subpackages,
     * fields, methods, parameters, and local variables.
     *
     * @param ast
     *        DetailAST instance
     * @return identifier of AST, null if AST does not have name.
     */
    private static String getIdentifier(final DetailAST ast) {
        String result = null;
        final DetailAST ident = ast.findFirstToken(TokenTypes.IDENT);
        if (ident != null) {
            result = ident.getText();
        }
        return result;
    }

    /**
     * Verify that exists updating of a field.
     *
     * @param statementsAst DetailAST of statements (SLIST).
     * @param fieldName name of target field.
     * @return true if there is updating of aFieldName in aStatementsAst.
     */
    private static boolean isFieldUpdate(DetailAST statementsAst, String fieldName) {
        boolean result = false;
        DetailAST currentStatement = statementsAst.getFirstChild();

        while (currentStatement != null && currentStatement != statementsAst) {
            String nameOfSetterField = null;
            if (currentStatement.getType() == TokenTypes.ASSIGN) {
                nameOfSetterField = getNameOfAssignedField(currentStatement);
            }
            else if (currentStatement.getType() == TokenTypes.METHOD_CALL) {
                nameOfSetterField = getNameOfSuperClassUpdatedField(currentStatement);
            }

            if (fieldName.equalsIgnoreCase(nameOfSetterField)) {
                result = true;
                break;
            }

            DetailAST nextStatement = currentStatement.getFirstChild();

            while (currentStatement != null && nextStatement == null) {
                nextStatement = currentStatement.getNextSibling();
                if (nextStatement == null) {
                    currentStatement = currentStatement.getParent();
                }
            }

            currentStatement = nextStatement;
        }
        return result;
    }

    /**
     * <p>
     * Return name of the field, that was assigned in current setter.
     * </p>
     *
     * @param assignAst
     *        - DetailAST contains ASSIGN from EXPR of the setter.
     * @return name of field, that use in setter.
     */
    private static String getNameOfAssignedField(DetailAST assignAst) {
        String nameOfSettingField = null;

        if (assignAst.getChildCount() > 0
                        && (assignAst.getLastChild().getType() == TokenTypes.IDENT
                        || assignAst.getLastChild().getType() == TokenTypes.METHOD_CALL)) {
            final DetailAST methodCallDot = assignAst.getFirstChild();
            if (methodCallDot.getChildCount() == 2
                && "this".equals(methodCallDot.getFirstChild().getText())) {
                nameOfSettingField = methodCallDot.getLastChild().getText();
            }
        }

        return nameOfSettingField;
    }

    /**
     * <p>
     * Return name of the field of a super class, that was assigned in setter.
     * </p>
     *
     * @param methodCallAst
     *        - DetailAST contains METHOD_CALL from EXPR of the setter.
     * @return name of field, that used in setter.
     */
    private static String getNameOfSuperClassUpdatedField(DetailAST methodCallAst) {
        String nameOfSettingField = null;

        final DetailAST methodCallDot = methodCallAst.getFirstChild();
        if (methodCallDot.getChildCount() == 2
                && "super".equals(methodCallDot.getFirstChild().getText())) {
            nameOfSettingField = getFieldName(methodCallDot);
        }

        return nameOfSettingField;
    }

    /**
     * Gets name of the field, that was used in calling setter from a super class.
     *
     * @param methodCallDotAst The token to examine.
     * @return
     *      name of field in method parameter.
     */
    private static String getFieldName(final DetailAST methodCallDotAst) {
        String nameOfSettingField = null;
        final DetailAST parameterOfSetterMethod = methodCallDotAst.getNextSibling().getFirstChild();
        if (parameterOfSetterMethod != null) {
            nameOfSettingField = parameterOfSetterMethod.getFirstChild().getText();
        }
        return nameOfSettingField;
    }

    /**
     * <p>
     * Compare name of the field and part of name of the method. Return true
     * when they are different.
     * </p>
     *
     * @param fieldName
     *        - name of the field.
     * @param methodName
     *        - part of name of the method (without "set", "get" or "is").
     * @return true when names are different.
     */
    private boolean verifyFieldAndMethodName(String fieldName,
            String methodName) {
        return (fieldPrefix + methodName).equalsIgnoreCase(fieldName);
    }

    /**
     * <p>
     * Return name of the field, that use in the getter.
     * </p>
     *
     * @param expr
     *        - DetailAST contains expression from getter.
     * @return name of the field, that use in getter.
     */
    private static String getNameOfGetterField(DetailAST expr) {
        String nameOfGetterField = null;

        if (expr.getChildCount() == 1) {
            final DetailAST exprFirstChild = expr.getFirstChild();

            if (exprFirstChild.getType() == TokenTypes.IDENT) {
                nameOfGetterField = exprFirstChild.getText();
            }
            else if (exprFirstChild.getType() == TokenTypes.DOT
                    && exprFirstChild.getChildCount() == 2
                    && exprFirstChild.getFirstChild().getType() == TokenTypes.LITERAL_THIS
                    && exprFirstChild.getLastChild().getType() == TokenTypes.IDENT) {
                nameOfGetterField = exprFirstChild.getLastChild().getText();
            }
        }

        return nameOfGetterField;
    }

    /**
     * Verifies that the given DetailAST is a main method.
     *
     * @param methodAST
     *        DetailAST instance.
     * @return true if aMethodAST is a main method, false otherwise.
     */
    private static boolean isMainMethod(final DetailAST methodAST) {
        final boolean result;
        final String methodName = getIdentifier(methodAST);
        if ("main".equals(methodName)) {
            result = isVoidType(methodAST)
                    && isMainMethodModifiers(methodAST)
                    && isMainMethodParameters(methodAST);
        }
        else {
            result = false;
        }
        return result;
    }

    /**
     * Verifies that given AST has appropriate modifiers for main method.
     *
     * @param methodAST
     *        DetailAST instance.
     * @return true if aMethodAST has (public & static & !abstract) modifiers,
     *         false otherwise.
     */
    private static boolean isMainMethodModifiers(final DetailAST methodAST) {
        boolean result = false;
        if (hasChildToken(methodAST, TokenTypes.MODIFIERS)) {
            final DetailAST modifiers =
                    methodAST.findFirstToken(TokenTypes.MODIFIERS);
            result = hasChildToken(modifiers, TokenTypes.LITERAL_PUBLIC)
                    && hasChildToken(modifiers, TokenTypes.LITERAL_STATIC);
        }
        return result;
    }

    /**
     * Verifies that given AST has type and this type is void.
     *
     * @param methodAST
     *        DetailAST instance.
     * @return true if AST's type void, false otherwise.
     */
    private static boolean isVoidType(final DetailAST methodAST) {
        boolean result = true;
        if (hasChildToken(methodAST, TokenTypes.TYPE)) {
            final DetailAST methodTypeAST = methodAST.findFirstToken(TokenTypes.TYPE);
            result = hasChildToken(methodTypeAST, TokenTypes.LITERAL_VOID);
        }
        return result;
    }

    /**
     * Verifies that given AST has appropriate for main method parameters.
     *
     * @param methodAST
     *        instance of a method
     * @return true if parameters of aMethodAST are appropriate for main method,
     *         false otherwise.
     */
    private static boolean isMainMethodParameters(final DetailAST methodAST) {
        final DetailAST params =
                methodAST.findFirstToken(TokenTypes.PARAMETERS);
        return hasOnlyStringArrayParameter(params)
                || hasOnlyStringEllipsisParameter(params);
    }

    /**
     * Return true if AST of method parameters has String[] parameter child
     * token.
     *
     * @param parametersAST
     *        DetailAST of method parameters.
     * @return true if AST has String[] parameter child token, false otherwise.
     */
    private static boolean hasOnlyStringArrayParameter(final DetailAST parametersAST) {
        final boolean result;
        if (parametersAST.getChildCount(TokenTypes.PARAMETER_DEF) == 1) {
            final DetailAST parameterDefinitionAST =
                    parametersAST.findFirstToken(TokenTypes.PARAMETER_DEF);
            final DetailAST parameterTypeAST = parameterDefinitionAST
                    .findFirstToken(TokenTypes.TYPE);
            if (hasChildToken(parameterTypeAST, TokenTypes.ARRAY_DECLARATOR)) {
                final String parameterName =
                        parameterTypeAST.getFirstChild().getText();
                result = STRING_MACRO.equals(parameterName);
            }
            else {
                result = false;
            }
        }
        else {
            // there is none or multiple parameters
            result = false;
        }
        return result;
    }

    /**
     * Return true if AST of method parameters has String... parameter child
     * token.
     *
     * @param parametersAST
     *        DetailAST of method parameters.
     * @return true if aParametersAST has String... parameter child token, false
     *         otherwise.
     */
    private static boolean hasOnlyStringEllipsisParameter(final DetailAST parametersAST) {
        final boolean result;
        if (parametersAST.getChildCount(TokenTypes.PARAMETER_DEF) == 1) {
            final DetailAST parameterDefinitionAST =
                    parametersAST.findFirstToken(TokenTypes.PARAMETER_DEF);
            if (hasChildToken(parameterDefinitionAST, TokenTypes.ELLIPSIS)) {
                final DetailAST parameterTypeAST =
                        parameterDefinitionAST.findFirstToken(TokenTypes.TYPE);
                final String parameterName =
                        getIdentifier(parameterTypeAST);
                result = STRING_MACRO.equals(parameterName);
            }
            else {
                result = false;
            }
        }
        else {
            // there is none or multiple parameters
            result = false;
        }
        return result;
    }

    /**
     * Return true if aAST has token of aTokenType type.
     *
     * @param ast
     *        DetailAST instance.
     * @param tokenType
     *        one of TokenTypes
     * @return true if aAST has token of given type, or false otherwise.
     */
    private static boolean hasChildToken(DetailAST ast, int tokenType) {
        return ast.findFirstToken(tokenType) != null;
    }

    /**
     * Private class for members of class and their patterns.
     */
    private static final class FormatMatcher {

        /** The regexp to match against. */
        private Pattern regExp;
        /** The Member of Class. */
        private final int classMember;
        /** The input full one rule with original names. */
        private final String rule;
        /** The string format of the RegExp. */
        private String format;

        /**
         * Creates a new <code>FormatMatcher</code> instance.
         *
         * @param inputRule input string with MemberDefinition and RegExp.
         * @param classMember the member of class
         */
        /* package */ FormatMatcher(final String inputRule,
                final int classMember) {
            this.classMember = classMember;
            rule = inputRule;
        }

        /**
         * Getter for the regexp field.
         *
         * @return the RegExp to match against
         */
        public Pattern getRegexp() {
            return regExp;
        }

        /**
         * Getter for the rule field.
         *
         * @return the original immutable input rule
         */
        public String getRule() {
            return rule;
        }

        /**
         * Getter for the class member field.
         *
         * @return the Class Member
         */
        public int getClassMember() {
            return classMember;
        }

        /**
         * Set the compile flags for the regular expression.
         *
         * @param compileFlags the compile flags to use.
         */
        public void setCompileFlags(final int compileFlags) {
            updateRegexp(format, compileFlags);
        }

        /**
         * Updates the regular expression using the supplied format and compiler
         * flags. Will also update the member variables.
         *
         * @param newFormat the format of the regular expression.
         * @param compileFlags the compiler flags to use.
         * @throws IllegalArgumentException when there some problems during parsing of newFormat
         */
        private void updateRegexp(final String newFormat, final int compileFlags) {
            try {
                regExp = Pattern.compile(newFormat, compileFlags);
                format = newFormat;
            }
            catch (final PatternSyntaxException exc) {
                throw new IllegalArgumentException("unable to parse " + newFormat, exc);
            }
        }

        /**
         * Check that format matcher contains rule.
         *
         * @param ruleCheck string
         * @return true if format matcher contains rule.
         */
        public boolean hasRule(String ruleCheck) {
            return rule.indexOf(ruleCheck) > -1;
        }

        @Override
        public String toString() {
            return rule;
        }

    }

    /**
     * Class to keep current position and collect getters, setters.
     */
    private static final class ClassDetail {

        /**
         * Current position in custom order declaration.
         */
        private int currentPosition;
        /**
         * List of getter ASTs.
         */
        private final List<DetailAST> getters = new LinkedList<>();
        /**
         * List of setter ASTs.
         */
        private final List<DetailAST> setters = new LinkedList<>();

        /**
         * Returns the current position.
         *
         * @return the current position
         */
        public int getCurrentPosition() {
            return currentPosition;
        }

        /**
         * Setter for the current position.
         *
         * @param position the new position
         */
        public void setCurrentPosition(int position) {
            currentPosition = position;
        }

        /**
         * Add getter.
         *
         * @param getterAst DetailAST of getter.
         */
        public void addGetter(DetailAST getterAst) {
            getters.add(getterAst);
        }

        /**
         * Add setter.
         *
         * @param setterAst DetailAST of setter.
         */
        public void addSetter(DetailAST setterAst) {
            setters.add(setterAst);
        }

        /**
         * Compare order of getters and setters. Order should be like "getX; setX; getY; setY; ...".
         * If it is wrong order, then wrong ordered setters and getters will be returned as map.
         *
         * @return Map with setter AST as key and getter AST as value.
         */
        public Map<DetailAST, DetailAST> getWrongOrderedGettersSetters() {
            final Map<DetailAST, DetailAST> result = new LinkedHashMap<>();
            if (!getters.isEmpty() && !setters.isEmpty()) {
                //  all getters and setters
                final List<DetailAST> allGettersSetters = new ArrayList<>(getters);
                allGettersSetters.addAll(setters);
                // sort by line numbers
                Collections.sort(allGettersSetters, AST_LINE_COMPARATOR);

                for (int index = 0; index < allGettersSetters.size(); index++) {
                    result.putAll(getWrongOrderedGettersSetters(allGettersSetters, index));
                }
            }
            return result;
        }

        /**
         * Compare order of getters and setters. Order should be like "getX; setX; getY; setY; ...".
         * If it is wrong order, then wrong ordered setters and getters will be returned as map.
         *
         * @param allGettersSetters collection of all getter and setters
         * @param index index from upper loo
         * @return Map with setter AST as key and getter AST as value.
         */
        private static Map<DetailAST, DetailAST> getWrongOrderedGettersSetters(
                List<DetailAST> allGettersSetters, int index) {
            final DetailAST getterAst = allGettersSetters.get(index);
            final String getterName = getIdentifier(getterAst);
            String getterField = null;
            if (isGetterName(getterName)) {
                getterField = getNameWithoutPrefix(getIdentifier(getterAst), GETTER_PREFIX);
            }
            else if (isBooleanGetterName(getterName)) {
                getterField = getNameWithoutPrefix(getIdentifier(getterAst),
                        BOOLEAN_GETTER_PREFIX);
            }
            final Map<DetailAST, DetailAST> result = new LinkedHashMap<>();

            if (getterField != null) {
                // review rest of the list to find a proper setter
                for (int allGettersSettersIndex = 0;
                     allGettersSettersIndex < allGettersSetters.size();
                     allGettersSettersIndex++) {
                    if (index != allGettersSettersIndex) {
                        // method is NOT getter
                        final DetailAST setterAst = allGettersSetters.get(allGettersSettersIndex);
                        final String setterName = getIdentifier(setterAst);
                        if (isSetterName(setterName)) {
                            final String setterField = getNameWithoutPrefix(
                                    getIdentifier(setterAst), SETTER_PREFIX);

                            // if fields are same and setter is sibling with getter
                            if (allGettersSettersIndex != index + 1
                                    && getterField.equals(setterField)) {
                                result.put(setterAst, getterAst);
                                break;
                            }
                        }
                    }
                }
            }
            return result;
        }

        /**
         * Verify that specified method was saved as getter.
         *
         * @param methodName name of method.
         * @return true if specified method was saved as getter.
         */
        private boolean containsGetter(String methodName) {
            boolean result = false;
            for (DetailAST methodAst: getters) {
                final String name = getIdentifier(methodAst);
                if (name.equals(methodName)) {
                    result = true;
                }
            }
            return result;
        }

        /**
         * Verify that specified method was saved as setter.
         *
         * @param methodName name of method.
         * @return true if specified method was saved as setter.
         */
        private boolean containsSetter(String methodName) {
            boolean result = false;
            for (DetailAST methodAst: setters) {
                final String name = getIdentifier(methodAst);
                if (name.equals(methodName)) {
                    result = true;
                }
            }
            return result;
        }

    }

}
