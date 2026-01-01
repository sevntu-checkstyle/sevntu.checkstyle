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

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.AnnotationUtil;

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
 * <p>If name of field contains prefix,then must to be define parameter 'prefix'
 * , for example:</p>
 *
 * <pre>
 * &lt;module name="SimpleAccessorNameNotationCheck"&gt; &lt;
 * property name="prefix" value="m_"/&gt;
 * &lt;/module&gt;
 * </pre>
 *
 *
 * @author <a href="mailto:hidoyatov.v.i@gmail.com">Hidoyatov Victor</a>
 * @author <a href="mailto:iliadubinin91@gmail.com">Ilja Dubinin</a>
 * @since 1.9.0
 */
public class SimpleAccessorNameNotationCheck extends AbstractCheck {

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY_GETTER = "incorrect.getter.name";
    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY_SETTER = "incorrect.setter.name";
    /** Prefix for boolean getter methods. */
    private static final String BOOLEAN_GETTER_PREFIX = "is";
    /** Prefix for non-boolean getter methods. */
    private static final String GETTER_PREFIX = "get";
    /** Prefix for setter methods. */
    private static final String SETTER_PREFIX = "set";
    /** {@link Override Override} annotation name. */
    private static final String OVERRIDE = "Override";

    /** Fully-qualified {@link Override Override} annotation name. */
    private static final String FQ_OVERRIDE = "java.lang." + OVERRIDE;

    /**
     * Number of children in expression only block. Expecting three children:
     * EXPR, SEMI and RCURLY.
     */
    private static final int EXPRESSION_BLOCK_CHILD_COUNT = 3;
    /**
     * Prefix of field's name.
     */
    private String prefix = "";

    /**
     * Setter for prefix.
     *
     * @param prefix
     *        - prefix of field's name
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.METHOD_DEF,
        };
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
    public void visitToken(DetailAST methodDef) {
        if (!isOverrideMethod(methodDef) && hasBody(methodDef)
                && !isMethodAtAnonymousClass(methodDef)) {
            final String methodName = methodDef.findFirstToken(TokenTypes.IDENT).getText();
            if (methodName.startsWith(BOOLEAN_GETTER_PREFIX)) {
                if (!isGetterCorrect(methodDef,
                        methodName.substring(BOOLEAN_GETTER_PREFIX.length()))) {
                    log(methodDef, MSG_KEY_GETTER);
                }
            }
            else if (methodName.startsWith(SETTER_PREFIX)) {
                if (!isSetterCorrect(methodDef, methodName.substring(SETTER_PREFIX.length()))) {
                    log(methodDef, MSG_KEY_SETTER);
                }
            }
            else if (methodName.startsWith(GETTER_PREFIX)
                    && !isGetterCorrect(methodDef, methodName.substring(GETTER_PREFIX.length()))) {
                log(methodDef, MSG_KEY_GETTER);
            }
        }
    }

    /**
     * <p>
     * Returns true when setter is correct.
     * </p>
     *
     * @param methodDef
     *        - DetailAST contains method definition.
     * @param methodName
     *        - name of setter without "set".
     * @return true when setter is correct.
     */
    private boolean isSetterCorrect(DetailAST methodDef, String methodName) {
        final DetailAST methodType = methodDef.findFirstToken(TokenTypes.TYPE);
        boolean result = true;
        if (methodType.findFirstToken(TokenTypes.LITERAL_VOID) != null) {
            DetailAST currentVerifiedTop = methodDef.findFirstToken(TokenTypes.SLIST);

            if (containsOnlyExpression(currentVerifiedTop)) {
                currentVerifiedTop = currentVerifiedTop.getFirstChild();
                final boolean containsOnlyOneAssignment = currentVerifiedTop.getFirstChild()
                        .getType() == TokenTypes.ASSIGN;
                if (containsOnlyOneAssignment) {
                    currentVerifiedTop = currentVerifiedTop.getFirstChild();
                    final DetailAST parameters =
                            methodDef.findFirstToken(TokenTypes.PARAMETERS);
                    final String nameOfSettingField = getNameOfSettingField(
                            currentVerifiedTop, parameters);

                    if (nameOfSettingField != null
                            && verifyFieldAndMethodName(nameOfSettingField,
                                    methodName)) {
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
     *
     * @param methodDef
     *        - DetailAST contains method definition.
     * @param methodName
     *        - name of getter without "get" or "is".
     * @return true when getter is correct.
     */
    private boolean isGetterCorrect(DetailAST methodDef, String methodName) {
        final DetailAST parameters = methodDef.findFirstToken(TokenTypes.PARAMETERS);
        boolean result = true;
        if (parameters.getChildCount() == 0) {
            DetailAST currentVerifiedTop =
                    methodDef.findFirstToken(TokenTypes.SLIST);
            if (containsOnlyReturn(currentVerifiedTop)) {
                currentVerifiedTop = currentVerifiedTop.getFirstChild();

                if (isCorrectReturn(currentVerifiedTop)) {
                    currentVerifiedTop = currentVerifiedTop.getFirstChild();
                    final String nameOfGettingField = getNameOfGettingField(currentVerifiedTop);

                    if (nameOfGettingField != null
                            && verifyFieldAndMethodName(nameOfGettingField,
                                    methodName)) {
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
     *
     * @param objectBlock
     *        - is a link to checked block
     * @return true if object block is correct
     */
    private static boolean containsOnlyExpression(DetailAST objectBlock) {
        // three child: EXPR, SEMI and RCURLY
        return objectBlock.getChildCount() == EXPRESSION_BLOCK_CHILD_COUNT
                && objectBlock.getFirstChild().getType() == TokenTypes.EXPR;
    }

    /**
     * <p>
     * Return name of the field, that use in the setter.
     * </p>
     *
     * @param assign
     *        - DetailAST contains ASSIGN from EXPR of the setter.
     * @param parameters
     *        - DetailAST contains parameters of the setter.
     * @return name of field, that use in setter.
     */
    private static String getNameOfSettingField(DetailAST assign,
            DetailAST parameters) {
        String nameOfSettingField = null;

        if (assign.getChildCount() == 2
                && assign.getLastChild().getType() == TokenTypes.IDENT) {
            final DetailAST assigningFirstChild = assign.getFirstChild();

            if (assigningFirstChild.getType() == TokenTypes.IDENT) {
                nameOfSettingField = assigningFirstChild.getText();

                if (checkNameOfParameters(parameters, nameOfSettingField)) {
                    nameOfSettingField = null;
                }
            }
            else if (assigningFirstChild.getType() == TokenTypes.DOT
                    && "this".equals(assigningFirstChild.getFirstChild().getText())) {
                nameOfSettingField = assigningFirstChild.getLastChild()
                        .getText();
            }
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
        final String name = prefix + methodName;
        return !fieldName.equalsIgnoreCase(name);
    }

    /**
     * <p>
     * Returns true, when object block contains only one child: LITERAL_RETURN.
     * </p>
     *
     * @param methodBody
     *        - DetailAST contains object block of the getter.
     * @return true when object block correct.
     */
    private static boolean containsOnlyReturn(DetailAST methodBody) {
        return methodBody.getFirstChild().getType() == TokenTypes.LITERAL_RETURN;
    }

    /**
     * <p>
     * Return true when getter has correct arguments of return.
     * </p>
     *
     * @param literalReturn
     *        - DetailAST contains LITERAL_RETURN
     * @return - true when getter has correct return.
     */
    private static boolean isCorrectReturn(DetailAST literalReturn) {
        // two child: EXPR and SEMI
        return literalReturn.getChildCount() == 2
                && literalReturn.getFirstChild().getType() == TokenTypes.EXPR;
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
    private static String getNameOfGettingField(DetailAST expr) {
        String nameOfGettingField = null;

        if (expr.getChildCount() == 1) {
            final DetailAST exprFirstChild = expr.getFirstChild();

            if (exprFirstChild.getType() == TokenTypes.IDENT) {
                nameOfGettingField = exprFirstChild.getText();
            }
            else {
                if (exprFirstChild.getType() == TokenTypes.DOT
                        && exprFirstChild.getChildCount() == 2
                        && exprFirstChild.getFirstChild().getType() == TokenTypes.LITERAL_THIS) {
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
     *
     * @param parameters
     *        - DetailAST contains parameters of the setter.
     * @param fieldName
     *        - name of the field.
     * @return true when name of the field is not contained in parameters.
     */
    private static boolean checkNameOfParameters(DetailAST parameters,
            String fieldName) {
        boolean isNameOfParameter = false;
        final int parametersChildCount = parameters.getChildCount();

        final DetailAST parameterDef = parameters
                .findFirstToken(TokenTypes.PARAMETER_DEF);

        for (int index = 0; index < parametersChildCount && !isNameOfParameter; index++) {
            isNameOfParameter = parameterDef.findFirstToken(TokenTypes.IDENT).getText()
                    .equals(fieldName);
        }

        return isNameOfParameter;
    }

    /**
     * <p>
     * Returns true when method has contained into an anonymous class.
     * </p>
     *
     * @param methodDef the METHOD_DEF token.
     * @return true when method has contained into an anonymous class.
     */
    private static boolean isMethodAtAnonymousClass(DetailAST methodDef) {
        final DetailAST classObjBlock = methodDef.getParent();
        return classObjBlock.getParent().getType() == TokenTypes.LITERAL_NEW;
    }

    /**
     * <p>
     * Returns true when method or other block has a body.
     * </p>
     *
     * @param methodDef
     *        - method definition node
     * @return true when method or other block has a body.
     */
    private static boolean hasBody(DetailAST methodDef) {
        final DetailAST body = methodDef.findFirstToken(TokenTypes.SLIST);
        return body != null;
    }

    /**
     * Returns true when method has an override annotation.
     *
     * @param methodDef method definition node
     * @return true when method has an override annotation.
     */
    private static boolean isOverrideMethod(DetailAST methodDef) {
        return AnnotationUtil.containsAnnotation(methodDef, OVERRIDE)
                || AnnotationUtil.containsAnnotation(methodDef, FQ_OVERRIDE);
    }

}
