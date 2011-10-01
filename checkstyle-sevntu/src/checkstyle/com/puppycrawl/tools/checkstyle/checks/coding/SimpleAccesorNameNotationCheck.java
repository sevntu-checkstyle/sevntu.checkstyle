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
package com.puppycrawl.tools.checkstyle.checks.coding;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**This check verify incorrect name of setter or getter methods if it used
 * field with other name. For example, method has name 'setXXX', but define 
 * field with name 'YYY'.
 * Setter and getter methods must have next view:
 * XXXType getXXXName() {return XXXName}
 * XXXType getXXXName() {return this.XXXName}
 * void setXXXName(XXXType value) { this.XXXName = value}
 * void setXXXName(XXXType value) { XXXName = value}
 * If name of field contains prefix,then must to be define parameter 'prefix',
 * for example:
 * <pre>
 * &lt;module name="SimpleAccesorNameNotationCheck"&gt; &lt;
 * property name="prefix" value="m_"/&gt; 
 * &lt;/module&gt;
 * </pre> 
 * @author <a href="mailto:hidoyatov.v.i@gmail.com">Hidoyatov Victor</a>
 */
public class SimpleAccesorNameNotationCheck extends Check
{
    /**
     * prefix contains prefix of field's name.
     */
    private String prefix = "";
    /**
     * setPrefix is a setter for prefix.
     * @param aPrefix - prefix of field's name
     */
    public void setPrefix(String aPrefix)
    {
        prefix = aPrefix;
    }

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] {TokenTypes.METHOD_DEF };
    }

    @Override
    public void visitToken(DetailAST aAST)
    {

        String methodName = aAST.findFirstToken(TokenTypes.IDENT).getText();

        if (methodName.indexOf("is") == 0) {
            checkGetter(aAST, methodName.substring(2));
        } else {

            String firstThreeCharMethodName = methodName.substring(0, 3);
            String lastCharMethodName = methodName.substring(3);

            if ("set".equalsIgnoreCase(firstThreeCharMethodName)) {
                checkSetter(aAST, lastCharMethodName);
            } else if ("get".equalsIgnoreCase(firstThreeCharMethodName)) {
                checkGetter(aAST, lastCharMethodName);

            }
        }

    }
    /**
     * Method verify setter aSetterDef.
     * @param aSetterDef is a link to checked setter
     * @param aMethodName is a name of setter without "set"
     */
    private void checkSetter(DetailAST aSetterDef, String aMethodName)
    {

        if (aSetterDef.findFirstToken(TokenTypes.TYPE).branchContains(
                TokenTypes.LITERAL_VOID)) {

            DetailAST currentVerifiedTop = aSetterDef
                    .findFirstToken(TokenTypes.SLIST);
            if (isCorrectSetterObjectBlock(currentVerifiedTop)) {

                currentVerifiedTop = currentVerifiedTop.getFirstChild();

                if (isCorrectSetterExpr(currentVerifiedTop)) {

                    currentVerifiedTop = currentVerifiedTop.getFirstChild();
                    DetailAST parameters = aSetterDef
                            .findFirstToken(TokenTypes.PARAMETERS);
                    String nameOfSettingField = getNameOfSettingField(
                            currentVerifiedTop, parameters);

                    if (nameOfSettingField != null
                            && verifyFieldAndMethodName(nameOfSettingField,
                                    aMethodName)) {

                        log(aSetterDef.getLineNo(), "incorrect.setter.name");
                    }
                }
            }
        }
    }
    /**
     * Method verify getter aGetterDef.
     * @param aGetterDef is a link to checked getter
     * @param aMethodName is a name of setter without "get" or "is"
     */
    private void checkGetter(DetailAST aGetterDef, String aMethodName)
    {

        if (aGetterDef.findFirstToken(TokenTypes.PARAMETERS).getChildCount() == 0) {

            DetailAST currentVerifiedTop = aGetterDef
                    .findFirstToken(TokenTypes.SLIST);
            if (isCorrectGetterObjectBlock(currentVerifiedTop)) {

                currentVerifiedTop = currentVerifiedTop.getFirstChild();

                if (isCorrectGetterReturn(currentVerifiedTop)) {

                    currentVerifiedTop = currentVerifiedTop.getFirstChild();
                    String nameOfGettingField = getNameOfGettingField(currentVerifiedTop);

                    if (nameOfGettingField != null
                            && verifyFieldAndMethodName(nameOfGettingField,
                                    aMethodName)) {

                        log(aGetterDef.getLineNo(), "incorrect.getter.name");
                    }
                }
            }
        }
    }
    /**
     * Setter's object block is correct?
     * It must has three child: EXPR, SEMI and RCURLY.
     * @param aObjectBlock is a link to checked block
     * @return true if object block is correct
     */
    private boolean isCorrectSetterObjectBlock(DetailAST aObjectBlock)
    {

        return aObjectBlock.getChildCount() == 3
                && aObjectBlock.getFirstChild().getType() == TokenTypes.EXPR
                && aObjectBlock.findFirstToken(TokenTypes.SEMI) != null;
    }
    /**
     * Setter's EXPR is correct?
     * EXPR must has one child: ASSIGN.
     * @param aExpr is a link to EXPR.
     * @return true if EXPR is correct
     */
    private boolean isCorrectSetterExpr(DetailAST aExpr)
    {

        return aExpr.getChildCount() == 1
                && aExpr.getFirstChild().getType() == TokenTypes.ASSIGN;
    }

    private String getNameOfSettingField(DetailAST aAssign,
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

            } else {
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

    private boolean verifyFieldAndMethodName(String aFieldName,
            String aMethodName)
    {
        String fieldName = getNonPrefixName(aFieldName);

        return !fieldName.equalsIgnoreCase(aMethodName);
    }

    private boolean isCorrectGetterObjectBlock(DetailAST aObjectBlock)
    {

        return aObjectBlock.getChildCount() == 2
                && aObjectBlock.getFirstChild().getType() == TokenTypes.LITERAL_RETURN;
    }

    private boolean isCorrectGetterReturn(DetailAST aReturn)
    {

        return aReturn.getChildCount() == 2
                && aReturn.getFirstChild().getType() == TokenTypes.EXPR
                && aReturn.getLastChild().getType() == TokenTypes.SEMI;
    }

    private String getNameOfGettingField(DetailAST aExpr)
    {
        String nameOfGettingField = null;

        if (aExpr.getChildCount() == 1) {
            DetailAST exprFirstChild = aExpr.getFirstChild();

            if (exprFirstChild.getType() == TokenTypes.IDENT) {

                nameOfGettingField = exprFirstChild.getText();

            } else {
                if (exprFirstChild.getType() == TokenTypes.DOT
                        && exprFirstChild.getChildCount() == 2
                        && exprFirstChild.getFirstChild().getType() == TokenTypes.LITERAL_THIS
                        && exprFirstChild.getLastChild().getType() == TokenTypes.IDENT) {

                    nameOfGettingField = exprFirstChild.getLastChild()
                            .getText();
                }
            }
        }

        return nameOfGettingField;
    }

    private String getNonPrefixName(String aFieldName)
    {
        String fieldName = aFieldName;

        if (fieldName.indexOf(prefix) == 0) {
            fieldName = fieldName.substring(prefix.length());
        }

        return fieldName;
    }

    private boolean checkNameOfParameters(DetailAST aParamrters,
            String aFieldName)
    {

        boolean isNameOfParameter = false;
        int paramrtersChildCount = aParamrters.getChildCount();

        DetailAST parameterDef = aParamrters
                .findFirstToken(TokenTypes.PARAMETER_DEF);

        for (int i = 0; i < paramrtersChildCount && !isNameOfParameter; i++) {

            isNameOfParameter = parameterDef.findFirstToken(TokenTypes.IDENT)
                    .getText().equals(aFieldName);

        }

        return isNameOfParameter;
    }
}
