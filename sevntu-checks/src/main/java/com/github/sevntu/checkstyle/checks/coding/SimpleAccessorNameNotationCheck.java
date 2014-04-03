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

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * This check verify incorrect name of setter and getter methods if it used
 * field with other name.
 * </p>
 * <p>
 * For example, method has name 'setXXX', but define field with name 'YYY'.
 * Setter and getter methods must have next view: XXXType getXXXName() {return
 * XXXName} XXXType getXXXName() {return this.XXXName} void setXXXName(XXXType
 * value) { this.XXXName = value} void setXXXName(XXXType value) { XXXName =
 * value}
 * </p>
 * <p>
 * If name of field contains prefix,then must to be define parameter 'prefix',
 * for example:
 * 
 * <pre>
 * &lt;module name="SimpleAccesorNameNotationCheck"&gt; &lt;
 * property name="prefix" value="m_"/&gt; 
 * &lt;/module&gt;
 * </pre>
 * 
 * </p>
 * @author <a href="mailto:hidoyatov.v.i@gmail.com">Hidoyatov Victor</a>
 * @author <a href="mailto:iliadubinin91@gmail.com">Ilja Dubinin</a>
 */
public class SimpleAccessorNameNotationCheck extends Check
{
	public static final String MSG_KEY_GETTER = "incorrect.getter.name";
	public static final String MSG_KEY_SETTER = "incorrect.setter.name";
    private static final String BOOLEAN_GETTER_PREFIX = "is";
    private static final String GETTER_PREFIX = "get";
    private static final String SETTER_PREFIX = "set";
    /**
     * Prefix of field's name.
     */
    private String prefix = "";

    /**
     * setPrefix is a setter for prefix.
     * @param aPrefix
     *        - prefix of field's name
     */
    public void setPrefix(String aPrefix)
    {
        prefix = aPrefix;
    }

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] { TokenTypes.METHOD_DEF };
    }

    @Override
    public void visitToken(DetailAST aMethodDef)
    {
        String methodName = aMethodDef.findFirstToken(TokenTypes.IDENT).getText();
        if (hasBody(aMethodDef) && !isMethodAtAnonymousClass(aMethodDef))
        {
            if (methodName.startsWith(BOOLEAN_GETTER_PREFIX))
            {
                if (!isGetterCorrect(aMethodDef, methodName.substring(BOOLEAN_GETTER_PREFIX.length())))
                {
                    log(aMethodDef.getLineNo(), MSG_KEY_GETTER);
                }
            }
            else if (methodName.startsWith(SETTER_PREFIX))
            {
                if (!isSetterCorrect(aMethodDef, methodName.substring(SETTER_PREFIX.length())))
                {
                    log(aMethodDef.getLineNo(), MSG_KEY_SETTER);
                }
            }
            else if (methodName.startsWith(GETTER_PREFIX))
            {
                if (!isGetterCorrect(aMethodDef, methodName.substring(GETTER_PREFIX.length())))
                {
                    log(aMethodDef.getLineNo(), MSG_KEY_GETTER);
                }
            }
        }
    }

    /**
     * <p>
     * Returns true when setter is correct.
     * </p>
     * @param aMethodDef
     *        - DetailAST contains method definition.
     * @param aMethodName
     *        - name of setter without "set".
     */
    private boolean isSetterCorrect(DetailAST aMethodDef, String aMethodName)
    {
        DetailAST methodType = aMethodDef.findFirstToken(TokenTypes.TYPE);
        boolean result = true;
        if (methodType.branchContains(TokenTypes.LITERAL_VOID)) {

            DetailAST currentVerifiedTop = aMethodDef.findFirstToken(TokenTypes.SLIST);

            if (containsOnlyExpression(currentVerifiedTop)) {

                currentVerifiedTop = currentVerifiedTop.getFirstChild();
                boolean containsOnlyOneAssignment = currentVerifiedTop.getChildCount() == 1 &&
                        currentVerifiedTop.getFirstChild().getType() == TokenTypes.ASSIGN;
                if (containsOnlyOneAssignment) {

                    currentVerifiedTop = currentVerifiedTop.getFirstChild();
                    DetailAST parameters =
                            aMethodDef.findFirstToken(TokenTypes.PARAMETERS);
                    String nameOfSettingField = getNameOfSettingField(
                            currentVerifiedTop, parameters);

                    if (nameOfSettingField != null
                            && verifyFieldAndMethodName(nameOfSettingField,
                                    aMethodName)) {

                        result = false;
                    }
                }
            }
        }
        return result;
    }

    /**
     * <p>
     * Returns true when getter is correct.
     * </p>
     * .
     * @param aMethodDef
     *        - DetailAST contains method definition.
     * @param aMethodName
     *        - name of getter without "get" or "is".
     */
    private boolean isGetterCorrect(DetailAST aMethodDef, String aMethodName)
    {
        DetailAST parameters = aMethodDef.findFirstToken(TokenTypes.PARAMETERS);
        boolean result = true;
        if (parameters.getChildCount() == 0) {

            DetailAST currentVerifiedTop =
                    aMethodDef.findFirstToken(TokenTypes.SLIST);
            if (containsOnlyReturn(currentVerifiedTop)) {

                currentVerifiedTop = currentVerifiedTop.getFirstChild();

                if (isCorrectReturn(currentVerifiedTop)) {

                    currentVerifiedTop = currentVerifiedTop.getFirstChild();
                    String nameOfGettingField = getNameOfGettingField(currentVerifiedTop);

                    if (nameOfGettingField != null
                            && verifyFieldAndMethodName(nameOfGettingField,
                                    aMethodName)) {

                        result = false;
                    }
                }
            }
        }
        return result;
    }

    /**
     * <p>
     * Returns true, when object block contains only three child: EXPR, SEMI and
     * RCURLY.
     * </p>
     * @param aObjectBlock
     *        - is a link to checked block
     * @return true if object block is correct
     */
    private static boolean containsOnlyExpression(DetailAST aObjectBlock)
    {
        //three child: EXPR, SEMI and RCURLY
        return aObjectBlock.getChildCount() == 3
                && aObjectBlock.getFirstChild().getType() == TokenTypes.EXPR
                && aObjectBlock.findFirstToken(TokenTypes.SEMI) != null;
    }

    /**
     * <p>
     * Return name of the field, that use in the setter.
     * </p>
     * @param aAssign
     *        - DetailAST contains ASSIGN from EXPR of the setter.
     * @param aParameters
     *        - DetailAST contains parameters of the setter.
     * @return name of field, that use in setter.
     */
    private static String getNameOfSettingField(DetailAST aAssign,
            DetailAST aParameters)
    {
        String nameOfSettingField = null;

        DetailAST assigningFirstChild = aAssign.getFirstChild();

        if (aAssign.getChildCount() == 2
                && aAssign.getLastChild().getType() == TokenTypes.IDENT) {

            if (assigningFirstChild.getType() == TokenTypes.IDENT) {

                nameOfSettingField = assigningFirstChild.getText();

                if (checkNameOfParameters(aParameters, nameOfSettingField)) {
                    nameOfSettingField = null;
                }

            }
            else {
                if (assigningFirstChild.getType() == TokenTypes.DOT) {

                    if (assigningFirstChild.getChildCount() == 2
                            && "this".equals(assigningFirstChild
                                    .getFirstChild().getText())
                            && assigningFirstChild.getLastChild().getType() == TokenTypes.IDENT) {

                        nameOfSettingField = assigningFirstChild.getLastChild()
                                .getText();
                    }

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
        String methodName = prefix + aMethodName;
        return !aFieldName.equalsIgnoreCase(methodName);
    }

    /**
     * <p>
     * Returns true, when object block contains only one child: LITERAL_RETURN.
     * </p>
     * @param aMethodBody
     *        - DetailAST contains object block of the getter.
     * @return true when object block correct.
     */
    private static boolean containsOnlyReturn(DetailAST aMethodBody)
    {
        return aMethodBody.getFirstChild().getType() == TokenTypes.LITERAL_RETURN;
    }

    /**
     * <p>
     * Return true when getter has correct arguments of return.
     * </p>
     * @param aReturn
     *        - DeailAST contains LITERAL_RETURN
     * @return - true when getter has correct return.
     */
    private static boolean isCorrectReturn(DetailAST aReturn)
    {
        //two child: EXPR and SEMI
        return aReturn.getChildCount() == 2
                && aReturn.getFirstChild().getType() == TokenTypes.EXPR
                && aReturn.getLastChild().getType() == TokenTypes.SEMI;
    }

    /**
     * <p>
     * Return name of the field, that use in the getter.
     * </p>
     * @param aExpr
     *        - DetailAST contains expression from getter.
     * @return name of the field, that use in getter.
     */
    private static String getNameOfGettingField(DetailAST aExpr)
    {
        String nameOfGettingField = null;

        if (aExpr.getChildCount() == 1) {
            DetailAST exprFirstChild = aExpr.getFirstChild();

            if (exprFirstChild.getType() == TokenTypes.IDENT) {

                nameOfGettingField = exprFirstChild.getText();

            }
            else {
                if (exprFirstChild.getType() == TokenTypes.DOT
                        && exprFirstChild.getChildCount() == 2
                        && exprFirstChild.getFirstChild().getType() == TokenTypes.LITERAL_THIS
                        && exprFirstChild.getLastChild().getType() == TokenTypes.IDENT) {

                    nameOfGettingField = exprFirstChild.getLastChild().getText();
                }
            }
        }

        return nameOfGettingField;
    }

    /**
     * <p>
     * Return true when name of the field is not contained in parameters of the
     * setter method.
     * </p>
     * @param aParamrters
     *        - DetailAST contains parameters of the setter.
     * @param aFieldName
     *        - name of the field.
     * @return true when name of the field is not contained in parameters.
     */
    private static boolean checkNameOfParameters(DetailAST aParamrters,
            String aFieldName)
    {

        boolean isNameOfParameter = false;
        int parametersChildCount = aParamrters.getChildCount();

        DetailAST parameterDef = aParamrters
                .findFirstToken(TokenTypes.PARAMETER_DEF);

        for (int i = 0; i < parametersChildCount && !isNameOfParameter; i++) {

            isNameOfParameter = parameterDef.findFirstToken(TokenTypes.IDENT).getText().equals(aFieldName);

        }

        return isNameOfParameter;
    }

    /**
     * <p>
     * Returns true when method has contained into an anonymous class.
     * </p>
     * @param aMethodDef
     * @return
     */
    private static boolean isMethodAtAnonymousClass(DetailAST aMethodDef)
    {
        DetailAST classObjBlock = aMethodDef.getParent();
        return classObjBlock.getParent().getType() == TokenTypes.LITERAL_NEW;
    }

    /**
     * <p>
     * Returns true when method or other block has a body.
     * </p>
     * @param aMethodDef
     *        - method definition node
     * @return true when method or other block has a body.
     */
    private static boolean hasBody(DetailAST aMethodDef)
    {
        DetailAST body = aMethodDef.findFirstToken(TokenTypes.SLIST);
        return body != null;
    }
}
