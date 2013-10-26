////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2011  Oliver Burn
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

import org.apache.commons.beanutils.ConversionException;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
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
 * <li>"DeclareAnnonClassField" to denote the fields keeping objects of anonymous classes</li>
 * <li>"Ctor" to denote the Constructors</li>
 * <li>"Method" to denote the Methods</li>
 * <li>"GetterSetter" to denote the group of getter and setter methods</li>
 * <li>"MainMethod" to denote the main method</li>
 * <li>"InnerClass" to denote the Inner Classes</li>
 * <li>"InnerInterface" to denote the Inner Interfaces</li>
 * <li>"InnerEnum" to denote the Inner Enums</li>
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
 * It is important to write exact order of modifiers in rules. So rule
 * <code><i>Field(public final)</i></code> does not match to <code><i>final public value;</i></code>.
 * <a href='http://checkstyle.sourceforge.net/config_modifier.html#ModifierOrder'>ModifierOrderCheck</a>
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
 * Field(public static final.*) ### Field(.*private.*) ### Ctor(.*) ###
 * GetterSetter(.*) ### Method(.*public.*final.*|@Ignore.*public.*) ###
 * Method(public static.*(final|(new|edit|create).*).*) ###
 * InnerClass(public abstract.*) ### InnerInterface(.*) ### InnerEnum(.*)</code>
 * </p>
 *
 * <p><b>What is group of getters and setters(<code>GetterSetter</code>)?</b></p>
 * <p>
 * It is ordered sequence of getters and setters like:
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
 * </p>
 * <p>Getter is public method that returns class field. Name of getter should be
 * 'get<i>FieldName</i>' in camel case.</p>
 * <p>Setter is public method with one parameter that assigns this parameter to class field.
 * Name of setter should be 'set<i>FieldName</i>' in camel case.</p>
 * <p>Setter of field X should be right after getter of field X.</p>
 *
 * @author <a href="mailto:solid.danil@gmail.com">Danil Lopatin</a>
 * @author <a href="mailto:barataliba@gmail.com">Baratali Izmailov</a>
 */
public class CustomDeclarationOrderCheck extends Check
{

    private static final String INNER_ENUM_MACRO = "InnerEnum";

    private static final String INNER_INTERFACE_MACRO = "InnerInterface";

    private static final String INNER_CLASS_MACRO = "InnerClass";

    private static final String CTOR_MACRO = "Ctor";

    private static final String METHOD_MACRO = "Method";

    private static final String ANNON_CLASS_FIELD_MACRO = "DeclareAnnonClassField";

    private static final String FIELD_MACRO = "Field";

    private static final String GETTER_SETTER_MACRO = "GetterSetter";

    private static final String MAIN_METHOD_MACRO = "MainMethod";

    private static final String BOOLEAN_GETTER_PREFIX = "is";

    private static final String GETTER_PREFIX = "get";

    private static final String SETTER_PREFIX = "set";

    /** Default format for custom declaration check */
    private static final String DEFAULT_DECLARATION = "Field(.*public.*) "
            + "### Field(.*protected.*) ### Field(.*private.*) ### CTOR(.*) ### "
            + "MainMethod(.*) ### GetterSetter(.*) ### Method(.*) ### InnerClass(.*) "
            + "### InnerInterface(.*) ### InnerEnum(.*)";

    /**
     * Compares line numbers.
     */
    private static final Comparator<DetailAST> AST_LINE_COMPARATOR = new Comparator<DetailAST>()
    {
        @Override
        public int compare(DetailAST aObj1, DetailAST aObj2)
        {
            return aObj1.getLineNo() - aObj2.getLineNo();
        }
    };

    /** List of order declaration customizing by user */
    private final List<FormatMatcher> mCustomOrderDeclaration =
        new ArrayList<FormatMatcher>();

    /** save compile flags for further usage */
    private int mCompileFlags;

    /** allow check inner classes */
    private boolean mCheckInnerClasses;

    /**
     * Allows to check getters and setters.
     */
    private boolean mCheckGettersSetters;

    /**
     * Prefix of class fields.
     */
    private String mFieldPrefix = "";


    /**
     * Stack of GetterSetterContainer objects to keep all getters and all setters
     * of certain class.
     */
    private final Deque<ClassDetail> mClassDetails = new LinkedList<ClassDetail>();


    /** Constructor to set default format. */
    public CustomDeclarationOrderCheck()
    {
        setCustomDeclarationOrder(DEFAULT_DECLARATION);
    }

    /**
     * Set custom order declaration from string with user rules.
     *
     * @param aInputOrderDeclaration The string line with the user custom
     *            declaration.
     */
    public void setCustomDeclarationOrder(final String aInputOrderDeclaration)
    {
        mCustomOrderDeclaration.clear();
        for (String currentState : aInputOrderDeclaration.split("\\s*###\\s*"))
        {
            try {
                mCustomOrderDeclaration
                        .add(parseInputDeclarationRule(currentState));
            }
            catch (StringIndexOutOfBoundsException exp) {
                //if the structure of the input rule isn't correct
                throw new RuntimeException("Unable to parse input rule: "
                        + currentState, exp);
            }
        }
    }

    /**
     * Set prefix of class fields.
     * @param aFieldPrefix string
     */
    public void setFieldPrefix(String aFieldPrefix)
    {
        mFieldPrefix = aFieldPrefix;
    }

    /**
     * Set whether or not the match is case sensitive.
     *
     * @param aCaseSensitive true if the match is case sensitive.
     */
    public void setCaseSensitive(final boolean aCaseSensitive)
    {
        // 0 - case sensitive flag
        mCompileFlags = aCaseSensitive ? 0 : Pattern.CASE_INSENSITIVE;

        for (FormatMatcher currentRule : mCustomOrderDeclaration) {
            currentRule.setCompileFlags(mCompileFlags);
        }
    }

    @Override
    public int[] getDefaultTokens()
    {
        final int size = mCustomOrderDeclaration.size();
        final int[] tokenTypes = new int[size + 1];

        for (int i = 0; i < size; i++) {
            final FormatMatcher currentRule = mCustomOrderDeclaration.get(i);
            tokenTypes[i] = currentRule.getClassMember();

            if (currentRule.hasRule(INNER_CLASS_MACRO)) {
                mCheckInnerClasses = true;
            }
            else if (currentRule.hasRule(GETTER_SETTER_MACRO)) {
                mCheckGettersSetters = true;
            }
        }

        tokenTypes[size] = TokenTypes.CLASS_DEF;

        return tokenTypes;
    }

    @Override
    public void visitToken(DetailAST aAST)
    {
        switch (aAST.getType()) {

        case TokenTypes.CLASS_DEF:
            if (!isClassDefInMethodDef(aAST))
            {
                if (mCheckInnerClasses && !mClassDetails.isEmpty()) {

                    final int position = getPositionInOrderDeclaration(aAST);

                    if (position != -1) {
                        if (isWrongPosition(position)) {
                            logWrongOrderedElement(aAST, position);
                        }
                        else {
                            mClassDetails.peek().setCurrentPosition(position);
                        }
                    }
                }

                mClassDetails.push(new ClassDetail());
            }
            break;

        default:
            final DetailAST objBlockAst = aAST.getParent();
            if (objBlockAst != null
                    && objBlockAst.getType() == TokenTypes.OBJBLOCK) {

                final DetailAST classDefAst = objBlockAst.getParent();

                if (classDefAst.getType() == TokenTypes.CLASS_DEF
                        && !isClassDefInMethodDef(classDefAst))
                {
                    if (mCheckGettersSetters) {
                        collectGetterSetter(aAST);
                    }

                    final int position = getPositionInOrderDeclaration(aAST);

                    if (position != -1) {
                        if (isWrongPosition(position)) {
                            logWrongOrderedElement(aAST, position);
                        }
                        else {
                            mClassDetails.peek().setCurrentPosition(position);
                        }
                    }
                }
            }

        }
    }

    @Override
    public void leaveToken(DetailAST aAST)
    {
        if (aAST.getType() == TokenTypes.CLASS_DEF
                && !isClassDefInMethodDef(aAST))
        {
            final ClassDetail classDetail = mClassDetails.pop();

            if (mCheckGettersSetters) {

                final Map<DetailAST, DetailAST> gettersSetters =
                        classDetail.getWrongOrderedGettersSetters();

                logWrongOrderedSetters(gettersSetters);
            }
        }
    }

    /**
     * Parse input current declaration rule and create new instance of
     * FormatMather with matcher
     *
     * @param aCurrentState input string with MemberDefinition and RegExp.
     * @return new FormatMatcher with parsed and compile rule
     */
    private FormatMatcher parseInputDeclarationRule(final String aCurrentState)
    {
        // parse mClassMember
        final String macro = aCurrentState.substring(0,
                aCurrentState.indexOf('(')).trim();
        final int classMember = convertMacroToTokenType(macro);
        if (classMember == -1) {
            // if Class Member has been specified wrong
            throw new ConversionException("Unable to parse " + macro);
        }

        // parse regExp
        String regExp = aCurrentState.substring(
                aCurrentState.indexOf('(') + 1,
                aCurrentState.lastIndexOf(')'));
        if (regExp.isEmpty()) {
            regExp = "package"; // package level
        }

        final FormatMatcher matcher = new FormatMatcher(aCurrentState, classMember);
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
    private static int convertMacroToTokenType(
            String aInputMemberName)
    {
        int result = -1;
        if (FIELD_MACRO.equalsIgnoreCase(aInputMemberName)
                || ANNON_CLASS_FIELD_MACRO.equalsIgnoreCase(aInputMemberName))
        {
            result = TokenTypes.VARIABLE_DEF;
        }
        else if (GETTER_SETTER_MACRO.equalsIgnoreCase(aInputMemberName)
                || METHOD_MACRO.equalsIgnoreCase(aInputMemberName)
                || MAIN_METHOD_MACRO.equalsIgnoreCase(aInputMemberName))
        {
            result = TokenTypes.METHOD_DEF;
        }
        else if (CTOR_MACRO.equalsIgnoreCase(aInputMemberName)) {
            result = TokenTypes.CTOR_DEF;
        }
        else if (INNER_CLASS_MACRO.equalsIgnoreCase(aInputMemberName)) {
            result = TokenTypes.CLASS_DEF;
        }
        else if (INNER_INTERFACE_MACRO.equalsIgnoreCase(aInputMemberName)) {
            result = TokenTypes.INTERFACE_DEF;
        }
        else if (INNER_ENUM_MACRO.equalsIgnoreCase(aInputMemberName)) {
            result = TokenTypes.ENUM_DEF;
        }
        return result;
    }

    /**
     * Verify that class definition is in method definition.
     * @param aClassDef
     *        DetailAST of CLASS_DEF.
     * @return true if class definition is in method definition.
     */
    private static boolean isClassDefInMethodDef(DetailAST aClassDef)
    {
        boolean result = false;
        DetailAST currentParentAst = aClassDef.getParent();
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
     * @param aAST DetailAST of any class element.
     */
    private void logWrongOrderedElement(final DetailAST aAST, final int aPosition)
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
        case TokenTypes.INTERFACE_DEF:
            token = "custom.declaration.order.interface";
            break;
        case TokenTypes.ENUM_DEF:
            token = "custom.declaration.order.enum";
            break;
        default:
            token = "Unknown element: " + aAST.getType();
        }

        final int expectedPosition = mClassDetails.peek().getCurrentPosition();
        log(aAST,
                token,
                mCustomOrderDeclaration.get(aPosition).getRule(),
                mCustomOrderDeclaration.get(expectedPosition).getRule());
    }

    /**
     * Check that position is wrong in custom declaration order.
     * @param aPosition position of class member.
     * @return true if position is wrong.
     */
    private boolean isWrongPosition(final int aPosition)
    {
        boolean result = false;
        final ClassDetail classDetail = mClassDetails.peek();
        final Integer classCurrentPosition = classDetail.getCurrentPosition();
        if (classCurrentPosition > aPosition) {
            result = true;
        }
        return result;
    }

    /**
     * Log wrong ordered setters.
     * @param aGettersSetters map that has getter as key and setter as value.
     */
    private void logWrongOrderedSetters(Map<DetailAST, DetailAST> aGettersSetters)
    {
        for (Entry<DetailAST, DetailAST> entry: aGettersSetters.entrySet()) {

            final DetailAST setterAst = entry.getKey();
            final DetailAST getterAst = entry.getValue();

            log(setterAst.getLineNo()
                    , "custom.declaration.order.invalid.setter"
                    , getIdentifier(setterAst)
                    , getIdentifier(getterAst));
        }
    }

    /**
     * If method definition is getter or setter,
     * then adds this method to collection.
     * @param aMethodDefAst DetailAST of method definition.
     */
    private void collectGetterSetter(DetailAST aMethodDefAst)
    {
        if (aMethodDefAst.getType() == TokenTypes.METHOD_DEF) {
            final String methodName = getIdentifier(aMethodDefAst);
            if (isGetterName(methodName)) {
                if (isGetterCorrect(aMethodDefAst, GETTER_PREFIX)) {
                    mClassDetails.peek().addGetter(aMethodDefAst);
                }
            }
            else if (isBooleanGetterName(methodName)) {
                if (isGetterCorrect(aMethodDefAst, BOOLEAN_GETTER_PREFIX)) {
                    mClassDetails.peek().addGetter(aMethodDefAst);
                }
            }
            else if (isSetterName(methodName)
                    && isSetterCorrect(aMethodDefAst, SETTER_PREFIX))
            {
                mClassDetails.peek().addSetter(aMethodDefAst);
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
    private int getPositionInOrderDeclaration(final DetailAST aAST)
    {
        int result = -1;
        final String modifiers = getCombinedModifiersList(aAST);
        for (int index = 0; index < mCustomOrderDeclaration.size(); index++) {
            final FormatMatcher currentRule = mCustomOrderDeclaration.get(index);
            if (currentRule.getClassMember() == aAST.getType()
                    && currentRule.getRegexp().matcher(modifiers).find())
            {
                if (currentRule.hasRule(ANNON_CLASS_FIELD_MACRO)) {
                    if (isAnonymousClassField(aAST)) {
                        result = index;
                        break;
                    }
                }
                else if (currentRule.hasRule(GETTER_SETTER_MACRO)) {
                    final String methodName = getIdentifier(aAST);
                    final ClassDetail classDetail = mClassDetails.peek();
                    if (classDetail.containsGetter(methodName)
                            || classDetail.containsSetter(methodName))
                    {
                        result = index;
                        break;
                    }
                }
                else if (currentRule.hasRule(MAIN_METHOD_MACRO)) {
                    if (isMainMethod(aAST)) {
                        result = index;
                        break;
                    }
                }
                else {
                	// if more than one rule matches current AST node, then keep first one
                    result = (result == -1) ? index : result;
                    if (aAST.getType() == TokenTypes.METHOD_DEF
                    		|| aAST.getType() == TokenTypes.VARIABLE_DEF)
                    {
                    	// continue to find more specific rule
                    	continue;
                    } else {
                    	break;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Verify that there is anonymous class in variable definition and this
     * variable is a field.
     * @param aVarDefinitionAst
     *        DetailAST of variable definition.
     * @return true if there is anonymous class in variable definition and this
     *         variable is a field.
     */
    private static boolean isAnonymousClassField(DetailAST aVarDefinitionAst)
    {
        boolean result = false;
        // ClassDef -> ObjBlock -> VarDef
        final int parentType = aVarDefinitionAst.getParent().getParent().getType();
        if (parentType == TokenTypes.CLASS_DEF) {
            final DetailAST assignAst = aVarDefinitionAst
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
     * @param aMethodName method name
     * @return true if method name starts with getter prefix.
     */
    private static boolean isGetterName(String aMethodName)
    {
        return aMethodName.startsWith(GETTER_PREFIX);
    }

    /**
     * Verify that method name starts with boolean getter prefix (is).
     * @param aMethodName method name
     * @return true if method name starts with boolean getter prefix.
     */
    private static boolean isBooleanGetterName(String aMethodName)
    {
        return aMethodName.startsWith(BOOLEAN_GETTER_PREFIX);
    }

    /**
     * Verify that method name starts with setter prefix (set).
     * @param aMethodName method name
     * @return true if method name starts with setter prefix.
     */
    private static boolean isSetterName(String aMethodName)
    {
        return aMethodName.startsWith(SETTER_PREFIX);
    }

    /**
     * Returns true when getter is correct. Correct getter is method that has no parameters,
     * returns class field and has name 'get<i>FieldName</i>'.
     * @param aMethodDef
     *        - DetailAST contains method definition.
     * @param aMethodPrefix
     *          Prefix for method (get, set, is).
     * @return true when getter is correct.
     */
    private boolean isGetterCorrect(DetailAST aMethodDef, String aMethodPrefix)
    {
        boolean result = false;

        final String methodName = getIdentifier(aMethodDef);
        final String methodNameWithoutPrefix = getNameWithoutPrefix(methodName, aMethodPrefix);

        final DetailAST parameters = aMethodDef.findFirstToken(TokenTypes.PARAMETERS);

        // no parameters
        if (parameters.getChildCount() == 0) {

            final DetailAST statementsAst = aMethodDef.findFirstToken(TokenTypes.SLIST);
            if (statementsAst != null) {
                final DetailAST returnStatementAst = statementsAst
                        .findFirstToken(TokenTypes.LITERAL_RETURN);

                if (returnStatementAst != null) {
                    final DetailAST exprAst = returnStatementAst.getFirstChild();
                    final String returnedFieldName = getNameOfGetterField(exprAst);
                    if (returnedFieldName != null
                    		&& !localVariableHidesField(statementsAst, returnedFieldName)
                            && verifyFieldAndMethodName(returnedFieldName, methodNameWithoutPrefix))
                    {
                        result = true;
                    }
                }
            }
        }
        return result;
    }

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
     * @param aMethodDefAst
     *        - DetailAST contains method definition.
     * @param aMethodPrefix
     *          Prefix for method (get, set, is).
     * @return true when setter is correct.
     */
    private boolean isSetterCorrect(DetailAST aMethodDefAst, String aMethodPrefix)
    {
        boolean result = false;

        final String methodName = getIdentifier(aMethodDefAst);
        final String setterFieldName = mFieldPrefix
                + getNameWithoutPrefix(methodName, aMethodPrefix);

        final DetailAST methodTypeAst = aMethodDefAst.findFirstToken(TokenTypes.TYPE);

        if (methodTypeAst.branchContains(TokenTypes.LITERAL_VOID)) {

            final DetailAST statementsAst = aMethodDefAst.findFirstToken(TokenTypes.SLIST);

            result = statementsAst != null
            		&& !localVariableHidesField(statementsAst, setterFieldName)
                    && containsAssignmentToField(statementsAst, setterFieldName);
        }
        return result;
    }

    /**
     * Verify that expression is anonymous class.
     * @param aExpressionAst
     *        DetailAST of expression.
     * @return true if expression is anonymous class.
     */
    private static boolean isAnonymousClass(DetailAST aExpressionAst)
    {
        boolean result = false;
        final DetailAST literalNewAst = aExpressionAst
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
     * Contains TokenTypes parameters for entry in child. </br>
     *
     * @param aAST current DetailAST state.
     * @return the unit annotations and modifiers and list.
     */
    private static String getCombinedModifiersList(final DetailAST aAST)
    {
        final StringBuffer modifiers = new StringBuffer();
        DetailAST ast = aAST.findFirstToken(TokenTypes.MODIFIERS);
        if (ast.getFirstChild() == null) {
            //if we met package level modifier
            modifiers.append("package ");
        }

        while (ast.getType() != TokenTypes.IDENT) {
            if (ast != null && ast.getFirstChild() != null) {
                modifiers.append(getModifiersAsText(ast.getFirstChild()));
                modifiers.append(" ");
            }
            ast = ast.getNextSibling();
        }
        // add IDENT(name)
        modifiers.append(ast.getText());

        return modifiers.toString();
    }

    /**
     * Get text representation of MODIFIERS node.
     *
     * @param aAST current DetailAST node.
     * @return text representation of MODIFIERS node.
     */
    private static String getModifiersAsText(final DetailAST aAST)
    {
        DetailAST ast = aAST;
        String separator = "";
        final StringBuffer modifiers = new StringBuffer();

        if (ast.getParent().getType() == TokenTypes.MODIFIERS) {
            // add separator between access modifiers and annotations
            separator = " ";
        }
        while (ast != null) {
            if (ast.getFirstChild() != null) {
                modifiers.append(getModifiersAsText(ast.getFirstChild()));
            }
            else {
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
     * Get name without prefix.
     * @param aName name
     * @param aPrefix prefix
     * @return name without prefix or null if name does not have such prefix.
     */
    private static String getNameWithoutPrefix(String aName, String aPrefix)
    {
        String result = null;
        if (aName.startsWith(aPrefix)) {
            result = aName.substring(aPrefix.length());
            result = Introspector.decapitalize(result);
        }
        return result;
    }

    /**
     * Get identifier of AST. These can be names of types, subpackages,
     * fields, methods, parameters, and local variables.
     * @param aAST
     *        DetailAST instance
     * @return identifier of AST, null if AST does not have name.
     */
    private static String getIdentifier(final DetailAST aAST)
    {
        final DetailAST ident = aAST.findFirstToken(TokenTypes.IDENT);
        if (ident != null) {
            return ident.getText();
        }
        return null;
    }

    /**
     * Verify that exists assignment to field among statements.
     * @param aStatementsAst DetailAST of statements (SLIST).
     * @param aFieldName name of target field.
     * @return true if there is assignment to aFieldName in aStatementsAst.
     */
    private static boolean containsAssignmentToField(DetailAST aStatementsAst, String aFieldName)
    {
        boolean result = false;
        DetailAST currentStatement = aStatementsAst.getFirstChild();

        while (currentStatement != null && currentStatement != aStatementsAst) {

            if (currentStatement.getType() == TokenTypes.ASSIGN) {
                final String nameOfSetterField = getNameOfSetterField(currentStatement);

                if (aFieldName.equalsIgnoreCase(nameOfSetterField)) {
                    result = true;
                    break;
                }
            }

            DetailAST nextStatement = currentStatement.getFirstChild();

            while ((currentStatement != null) && (nextStatement == null)) {
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
     * Return name of the field, that use in the setter.
     * </p>
     * @param aAssignAst
     *        - DetailAST contains ASSIGN from EXPR of the setter.
     * @return name of field, that use in setter.
     */
    private static String getNameOfSetterField(DetailAST aAssignAst)
    {
        String nameOfSettingField = null;

        if (aAssignAst.getChildCount() == 2
                && aAssignAst.getLastChild().getType() == TokenTypes.IDENT) {

            final DetailAST leftPart = aAssignAst.getFirstChild();

            if (leftPart.getType() == TokenTypes.IDENT) {

                nameOfSettingField = leftPart.getText();

            }
            else if (leftPart.getType() == TokenTypes.DOT) {

                if (leftPart.getChildCount() == 2
                        && "this".equals(leftPart.getFirstChild().getText())
                        && leftPart.getLastChild().getType() == TokenTypes.IDENT)
                {
                    nameOfSettingField = leftPart.getLastChild().getText();
                }
            }
        }

        return nameOfSettingField;
    }

    /**
     * <p>
     * Compare name of the field and part of name of the method. Return true
     * when they are different.
     * </p>
     * @param aFieldName
     *        - name of the field.
     * @param aMethodName
     *        - part of name of the method (without "set", "get" or "is").
     * @return true when names are different.
     */
    private boolean verifyFieldAndMethodName(String aFieldName,
            String aMethodName)
    {
        return aFieldName.equalsIgnoreCase(mFieldPrefix + aMethodName);
    }

    /**
     * <p>
     * Return name of the field, that use in the getter.
     * </p>
     * @param aExpr
     *        - DetailAST contains expression from getter.
     * @return name of the field, that use in getter.
     */
    private static String getNameOfGetterField(DetailAST aExpr)
    {
        String nameOfGetterField = null;

        if (aExpr.getChildCount() == 1) {
            final DetailAST exprFirstChild = aExpr.getFirstChild();

            if (exprFirstChild.getType() == TokenTypes.IDENT) {

                nameOfGetterField = exprFirstChild.getText();

            }
            else if (exprFirstChild.getType() == TokenTypes.DOT
                    && exprFirstChild.getChildCount() == 2
                    && exprFirstChild.getFirstChild().getType() == TokenTypes.LITERAL_THIS
                    && exprFirstChild.getLastChild().getType() == TokenTypes.IDENT)
            {
                nameOfGetterField = exprFirstChild.getLastChild().getText();
            }
        }

        return nameOfGetterField;
    }

    /**
     * Verifies that the given DetailAST is a main method.
     * @param aMethodAST
     *        DetailAST instance.
     * @return true if aMethodAST is a main method, false otherwise.
     */
    private static boolean isMainMethod(final DetailAST aMethodAST)
    {
        boolean result = true;
        final String methodName = getIdentifier(aMethodAST);
        if ("main".equals(methodName)) {
            result = isVoidType(aMethodAST)
                    && isMainMethodModifiers(aMethodAST)
                    && isMainMethodParameters(aMethodAST);
        }
        else {
            result = false;
        }
        return result;
    }


    /**
     * Verifies that given AST has appropriate modifiers for main method.
     * @param aMethodAST
     *        DetailAST instance.
     * @return true if aMethodAST has (public & static & !abstract) modifiers,
     *         false otherwise.
     */
    private static boolean isMainMethodModifiers(final DetailAST aMethodAST)
    {
        boolean result = false;
        if (hasChildToken(aMethodAST, TokenTypes.MODIFIERS)) {
            final DetailAST modifiers =
                    aMethodAST.findFirstToken(TokenTypes.MODIFIERS);
            result = hasChildToken(modifiers, TokenTypes.LITERAL_PUBLIC)
                    && hasChildToken(modifiers, TokenTypes.LITERAL_STATIC);
        }
        return result;
    }

    /**
     * Verifies that given AST has type and this type is void.
     * @param aMethodAST
     *        DetailAST instance.
     * @return true if AST's type void, false otherwise.
     */
    private static boolean isVoidType(final DetailAST aMethodAST)
    {
        boolean result = true;
        DetailAST methodTypeAST = null;
        if (hasChildToken(aMethodAST, TokenTypes.TYPE)) {
            methodTypeAST = aMethodAST.findFirstToken(TokenTypes.TYPE);
            result = hasChildToken(methodTypeAST, TokenTypes.LITERAL_VOID);
        }
        return result;
    }

    /**
     * Verifies that given AST has appropriate for main method parameters.
     * @param aMethodAST
     *        instance of a method
     * @return true if parameters of aMethodAST are appropriate for main method,
     *         false otherwise.
     */
    private static boolean isMainMethodParameters(final DetailAST aMethodAST)
    {
        final DetailAST params =
                aMethodAST.findFirstToken(TokenTypes.PARAMETERS);
        return hasOnlyStringArrayParameter(params)
                || hasOnlyStringEllipsisParameter(params);
    }

    /**
     * Return true if AST of method parameters has String[] parameter child
     * token.
     * @param aParametersAST
     *        DetailAST of method parameters.
     * @return true if AST has String[] parameter child token, false otherwise.
     */
    private static boolean
    hasOnlyStringArrayParameter(final DetailAST aParametersAST)
    {
        boolean result = true;
        if (aParametersAST.getChildCount(TokenTypes.PARAMETER_DEF) != 1) {
            result = false;
        }
        else { // there is one parameter
            final DetailAST parameterDefinitionAST =
                    aParametersAST.findFirstToken(TokenTypes.PARAMETER_DEF);
            final DetailAST parameterTypeAST = parameterDefinitionAST
                    .findFirstToken(TokenTypes.TYPE);
            if (hasChildToken(parameterTypeAST, TokenTypes.ARRAY_DECLARATOR)) {
                final DetailAST arrayDeclaratorAST = parameterTypeAST
                        .findFirstToken(TokenTypes.ARRAY_DECLARATOR);
                final String parameterName =
                        getIdentifier(arrayDeclaratorAST);
                result = "String".equals(parameterName);
            }
            else {
                result = false;
            }
        }
        return result;
    }

    /**
     * Return true if AST of method parameters has String... parameter child
     * token.
     * @param aParametersAST
     *        DetailAST of method parameters.
     * @return true if aParametersAST has String... parameter child token, false
     *         otherwise.
     */
    private static boolean
    hasOnlyStringEllipsisParameter(final DetailAST aParametersAST)
    {
        boolean result = true;
        if (aParametersAST.getChildCount(TokenTypes.PARAMETER_DEF) != 1) {
            result = false;
        }
        // there is one parameter
        else {
            final DetailAST parameterDefinitionAST =
                    aParametersAST.findFirstToken(TokenTypes.PARAMETER_DEF);
            if (hasChildToken(parameterDefinitionAST, TokenTypes.ELLIPSIS)) {
                final DetailAST parameterTypeAST =
                        parameterDefinitionAST.findFirstToken(TokenTypes.TYPE);
                final String parameterName =
                        getIdentifier(parameterTypeAST);
                result = "String".equals(parameterName);
            }
            else {
                result = false;
            }
        }
        return result;
    }

    /**
     * Return true if aAST has token of aTokenType type.
     * @param aAST
     *        DetailAST instance.
     * @param aTokenType
     *        one of TokenTypes
     * @return true if aAST has token of given type, or false otherwise.
     */
    private static boolean hasChildToken(DetailAST aAST, int aTokenType)
    {
        return aAST.findFirstToken(aTokenType) != null;
    }

    /**
     * private class for members of class and their patterns.
     */
    private static class FormatMatcher
    {
        /** The regexp to match against */
        private Pattern mRegExp;
        /** The Member of Class */
        private final int mClassMember;
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
                final int aClassMember)
        {
            mClassMember = aClassMember;
            mRule = aInputRule;
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
        public final int getClassMember()
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

        /**
         * Check that format matcher contains rule.
         * @param aRule string
         * @return true if format matcher contains rule.
         */
        public boolean hasRule(String aRule)
        {
            return mRule.indexOf(aRule) > -1;
        }

        @Override
        public String toString()
        {
            return mRule;
        }
    }

    /**
     * Class to keep current position and collect getters, setters.
     */
    private static class ClassDetail {
        /**
         * Current position in custom order declaration
         */
        private int mCurrentPosition;
        /**
         * List of getter ASTs
         */
        private final List<DetailAST> mGetters = new LinkedList<DetailAST>();
        /**
         * List of setter ASTs
         */
        private final List<DetailAST> mSetters = new LinkedList<DetailAST>();

        public int getCurrentPosition()
        {
            return mCurrentPosition;
        }

        public void setCurrentPosition(int aPosition)
        {
            mCurrentPosition = aPosition;
        }

        /**
         * Add getter.
         * @param aGetterAst DetailAST of getter.
         */
        public void addGetter(DetailAST aGetterAst)
        {
            mGetters.add(aGetterAst);
        }

        /**
         * Add setter.
         * @param aSetterAst DetailAST of setter.
         */
        public void addSetter(DetailAST aSetterAst)
        {
            mSetters.add(aSetterAst);
        }

        /**
         * Compare order of getters and setters. Order should be like "getX; setX; getY; setY; ...".
         * If it is wrong order, then wrong ordered setters and getters will be returned as map.
         * @return Map with setter AST as key and getter AST as value.
         */
        public Map<DetailAST, DetailAST> getWrongOrderedGettersSetters()
        {
            final Map<DetailAST, DetailAST> result = new LinkedHashMap<DetailAST, DetailAST>();
            if (!mGetters.isEmpty() & !mSetters.isEmpty()) {
                //  all getters and setters
                final List<DetailAST> allGettersSetters = new ArrayList<DetailAST>(mGetters);
                allGettersSetters.addAll(mSetters);
                // sort by line numbers
                Collections.sort(allGettersSetters, AST_LINE_COMPARATOR);

                for (int i = 0; i < allGettersSetters.size(); i++) {
                    final DetailAST getterAst = allGettersSetters.get(i);
                    final String getterName = getIdentifier(getterAst);
                    String getterField = null;
                    if (isGetterName(getterName)) {
                        getterField = getNameWithoutPrefix(getIdentifier(getterAst), GETTER_PREFIX);
                    }
                    else if (isBooleanGetterName(getterName)) {
                        getterField = getNameWithoutPrefix(getIdentifier(getterAst), BOOLEAN_GETTER_PREFIX);
                    }

                    if (getterField != null) {
                        // review rest of the list to find a proper setter
                        for (int j = 0; j < allGettersSetters.size(); j++) {
                            if (i == j) { // method is getter
                                continue;
                            }
                            final DetailAST setterAst = allGettersSetters.get(j);
                            final String setterName = getIdentifier(setterAst);
                            String setterField = null;
                            if (isSetterName(setterName)) {
                                setterField = getNameWithoutPrefix(
                                        getIdentifier(setterAst), SETTER_PREFIX);
                            }
                            else { // non-setter method
                                continue;
                            }
                            // if fields are same and setter is sibling with getter
                            if (getterField.equals(setterField)
                                    && j != (i + 1))
                            {
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
         * @param aMethodName name of method.
         * @return true if specified method was saved as getter.
         */
        private boolean containsGetter(String aMethodName)
        {
            boolean result = false;
            for (DetailAST methodAst: mGetters) {
                final String methodName = getIdentifier(methodAst);
                if (methodName.equals(aMethodName)) {
                    result = true;
                }
            }
            return result;
        }

        /**
         * Verify that specified method was saved as setter.
         * @param aMethodName name of method.
         * @return true if specified method was saved as setter.
         */
        private boolean containsSetter(String aMethodName)
        {
            boolean result = false;
            for (DetailAST methodAst: mSetters) {
                final String methodName = getIdentifier(methodAst);
                if (methodName.equals(aMethodName)) {
                    result = true;
                }
            }
            return result;
        }
    }

}
