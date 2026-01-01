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

package com.github.sevntu.checkstyle.checks.design;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import com.github.sevntu.checkstyle.SevntuUtil;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.AnnotationUtil;
import com.puppycrawl.tools.checkstyle.utils.ScopeUtil;

/**
 * <p>
 * Custom check to ensure Checkstyle tests are designed correctly.
 * </p>
 *
 * <p>Rationale: This check was made to ensure tests follow a specific design implementation
 * so 3rd party utilities like the regression utility can parse the tests for information
 * used in creating regression reports.
 *
 * <p>
 * Check have following options:
 * </p>
 * <ul>
 * <li>
 * createMethodRegexp - Regular expression for matching a create configuration method by name. This
 * is the name of the method that starts creating a custom module configuration to be used for
 * verifying results for regression purposes.
 * Default value is {@code create(Root|Module)Config|getModuleConfig}.</li>
 *
 * <li>
 * verifyMethodRegexp - Regular expression for matching a verify method by name. This is the name
 * of the method that verifies the execution results of the custom configuration created for
 * regression. As such, it should accept the custom configuration as a parameter.
 * Default value is {@code verify(Warns|Suppressed)?}.</li>
 * </ul>
 *
 * <p>
 * To configure the check to report incorrectly made checkstyle tests:
 * </p>
 *
 * <pre>
 * &lt;module name=&quot;CheckstyleTestMakeup&quot;/&gt;
 * </pre>
 *
 * @author Richard Veach
 * @since 1.25.0
 */
public class CheckstyleTestMakeupCheck extends AbstractCheck {

    /** Violations message. */
    public static final String MSG_KEY_CONFIG_NOT_ASSIGNED = "tester.config.not.assigned";
    /** Violations message. */
    public static final String MSG_KEY_CONFIG_NOT_ASSIGNED_WITH = "tester.config.not.assigned.with";
    /** Violations message. */
    public static final String MSG_KEY_CONFIG_NOT_ASSIGNED_PROPERLY =
            "tester.config.not.assigned.properly";
    /** Violations message. */
    public static final String MSG_KEY_UNKNOWN_PROPERTY = "tester.unknown.property";
    /** Violations message. */
    public static final String MSG_KEY_CONFIG_NOT_FOUND = "tester.config.not.found";

    /** Name of 'getPath' method. */
    private static final String METHOD_GET_PATH = "getPath";

    /** AST of method that is currently being examined. */
    private DetailAST methodAst;
    /** List of variable names that reference a file. */
    private final Set<String> fileVariableNames = new HashSet<>();
    /** List of variable names that reference a configuration. */
    private final Set<String> checkConfigNames = new HashSet<>();
    /** {@code true} if the 'verify' method was found in the method. */
    private boolean foundVerify;

    /** List of violations generated for a method. */
    private final Map<DetailAST, String> violations = new HashMap<>();

    /** Regular expression for matching a create method by name. */
    private Pattern createMethodRegexp = Pattern
            .compile("create(Root|Module)Config|getModuleConfig");

    /** Regular expression for matching a verify method by name. */
    private Pattern verifyMethodRegexp = Pattern.compile("verify(Warns|Suppressed)?");

    /**
     * Setter for {@link #createMethodRegexp}.
     *
     * @param createMethodRegexp The value to set.
     */
    public void setCreateMethodRegexp(Pattern createMethodRegexp) {
        this.createMethodRegexp = createMethodRegexp;
    }

    /**
     * Setter for {@link #verifyMethodRegexp}.
     *
     * @param verifyMethodRegexp The value to set.
     */
    public void setVerifyMethodRegexp(Pattern verifyMethodRegexp) {
        this.verifyMethodRegexp = verifyMethodRegexp;
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.METHOD_DEF,
            TokenTypes.VARIABLE_DEF,
            TokenTypes.METHOD_CALL,
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
    public void beginTree(DetailAST rootAST) {
        resetInternalFields();
    }

    @Override
    public void visitToken(DetailAST ast) {
        switch (ast.getType()) {
            case TokenTypes.METHOD_DEF:
                checkMethod(ast);
                break;
            case TokenTypes.VARIABLE_DEF:
                checkVariable(ast);
                break;
            case TokenTypes.METHOD_CALL:
                checkMethodCall(ast);
                break;
            default:
                SevntuUtil.reportInvalidToken(ast.getType());
                break;
        }
    }

    /**
     * Examines the method to see if it is part of a Test.
     *
     * @param ast The method to examine.
     */
    private void checkMethod(DetailAST ast) {
        if (methodAst == null && AnnotationUtil.containsAnnotation(ast, "Test")
                || AnnotationUtil.containsAnnotation(ast, "org.junit.jupiter.api.Test")
                || AnnotationUtil.containsAnnotation(ast, "org.junit.Test")) {
            methodAst = ast;
        }
    }

    /**
     * Examines the variable declaration to see if it is a specific variable type to track.
     * Variables of type {@link Configuration} or {@link  DefaultConfiguration} need to be assigned
     * a {@code null}, createModuleConfig, or createRootConfig and is tracked for future purposes.
     * Variables of type {@link File} with the modifier {@code final} are tracked for future
     * purposes.
     *
     * @param ast The variable to examine.
     */
    private void checkVariable(DetailAST ast) {
        if (methodAst != null && ScopeUtil.isLocalVariableDef(ast)) {
            final DetailAST type = ast.findFirstToken(TokenTypes.TYPE).findFirstToken(
                    TokenTypes.IDENT);

            if (type != null) {
                final String typeText = type.getText();

                if ("DefaultConfiguration".equals(typeText) || "Configuration".equals(typeText)) {
                    checkConfigurationVariable(ast);
                }
                else if ("File".equals(typeText)
                        && ast.findFirstToken(TokenTypes.MODIFIERS)
                                .findFirstToken(TokenTypes.FINAL) != null) {
                    fileVariableNames.add(ast.findFirstToken(TokenTypes.IDENT).getText());
                }
            }
        }
    }

    /**
     * Examines the configuration variable to see if it is defined as described in
     * {@link #checkVariable(DetailAST)}.
     *
     * @param ast The variable to examine.
     */
    private void checkConfigurationVariable(DetailAST ast) {
        checkConfigNames.add(ast.findFirstToken(TokenTypes.IDENT).getText());
        final DetailAST assignment = ast.findFirstToken(TokenTypes.ASSIGN);

        if (assignment == null) {
            violations.put(ast, MSG_KEY_CONFIG_NOT_ASSIGNED);
        }
        else if (assignment.getFirstChild().getFirstChild().getType() == TokenTypes.METHOD_CALL) {
            final DetailAST assignmentMethod = assignment.getFirstChild()
                    .getFirstChild().findFirstToken(TokenTypes.IDENT);

            if (assignmentMethod != null
                    && !createMethodRegexp.matcher(assignmentMethod.getText()).matches()) {
                violations.put(assignment, MSG_KEY_CONFIG_NOT_ASSIGNED_WITH);
            }
        }
        else if (assignment.getFirstChild().getFirstChild().getType() != TokenTypes.LITERAL_NULL) {
            violations.put(ast, MSG_KEY_CONFIG_NOT_ASSIGNED_PROPERLY);
        }
    }

    /**
     * Examines the method call and verify it is defined correctly.
     * addAttribute/addProperty method which is called by one of the configurations found earlier,
     * must have all its parameters be acceptable to
     * {@link #isValidMethodCallExpression(DetailAST)}.
     * Any method that matches {@link #verifyMethodRegexp} are tracked for future purposes.
     *
     * @param ast The method call to examine.
     */
    private void checkMethodCall(DetailAST ast) {
        if (methodAst != null) {
            final DetailAST firstChild = ast.getFirstChild();
            final String methodCallName = getMethodCallName(firstChild);
            final String methodCallerName = getMethodCallerName(firstChild);

            if (isAddPropertyMethod(methodCallName)
                    && checkConfigNames.contains(methodCallerName)) {
                final DetailAST elist = ast.findFirstToken(TokenTypes.ELIST);

                for (DetailAST expression = elist.getFirstChild(); expression != null;
                        expression = expression.getNextSibling()) {
                    if (expression.getType() == TokenTypes.EXPR
                            && !isValidMethodCallExpression(expression.getFirstChild())) {
                        violations.put(expression, MSG_KEY_UNKNOWN_PROPERTY);
                    }
                }
            }
            else if (methodCallerName.equals(methodCallName)
                    && ast.getParent().getParent().getType() != TokenTypes.METHOD_CALL
                    && verifyMethodRegexp.matcher(methodCallName).matches()) {
                foundVerify = true;
            }
        }
    }

    /**
     * Checks if the supplied {@code methodName} is addAttribute or addProperty.
     *
     * @param methodName The method name to examine.
     * @return {@code true} if the method is the expected name.
     */
    private static boolean isAddPropertyMethod(String methodName) {
        return "addAttribute".equals(methodName)
                || "addProperty".equals(methodName);
    }

    /**
     * Retrieves the name of the method being called.
     *
     * @param ast The method call token to examine.
     * @return The name of the method.
     */
    private String getMethodCallName(DetailAST ast) {
        final String result;
        if (ast.getType() == TokenTypes.DOT) {
            result = getMethodCallName(ast.getFirstChild().getNextSibling());
        }
        else {
            result = ast.getText();
        }
        return result;
    }

    /**
     * Retrieves the name of the variable calling the method.
     *
     * @param ast The method call token to examine.
     * @return The name of who is calling the method.
     */
    private String getMethodCallerName(DetailAST ast) {
        final String result;
        if (ast.getType() == TokenTypes.DOT) {
            result = getMethodCallName(ast.getFirstChild());
        }
        else {
            result = ast.getText();
        }
        return result;
    }

    /**
     * Identifies if the parameter of the method call is valid.
     * Plain string literal is allowed.
     * Adding multiple string literals is allowed because of line length limits.
     * Plain {@code null} is allowed due to backward compatibility.
     * Method calls are allowed only if they are any form of getPath, converting an enum to a
     * string, or retrieving the path of a final {@link File} variable.
     *
     * @param expression The expression to examine.
     * @return {@code true} if the method call is defined correctly.
     */
    private boolean isValidMethodCallExpression(DetailAST expression) {
        boolean result = false;
        final DetailAST firstChild = expression.getFirstChild();

        switch (expression.getType()) {
            case TokenTypes.STRING_LITERAL:
                result = true;
                break;
            case TokenTypes.METHOD_CALL:
                result = isValidMethodCallExpressionMethodCall(firstChild);
                break;
            case TokenTypes.PLUS:
                result = isValidMethodCallExpression(firstChild)
                        && isValidMethodCallExpression(firstChild.getNextSibling());
                break;
            case TokenTypes.LITERAL_NULL:
                result = true;
                break;
            default:
                break;
        }

        return result;
    }

    /**
     * Identifies if the inner method call of a method call is valid as defined in
     * {@link #isValidMethodCallExpression(DetailAST)}.
     *
     * @param firstChild The first child of the method call.
     * @return {@code true} if the method call is defined correctly.
     */
    private boolean isValidMethodCallExpressionMethodCall(DetailAST firstChild) {
        boolean result = false;

        if (firstChild.getType() == TokenTypes.DOT) {
            if (firstChild.getFirstChild().getType() == TokenTypes.DOT) {
                result = isEnumerationCall(firstChild);
            }
            else if (isFileVariable(firstChild.getFirstChild())) {
                result = true;
            }
        }
        else {
            final String methodName = firstChild.getText();

            if (isMethodGetPath(methodName)) {
                result = true;
            }
        }

        return result;
    }

    /**
     * Checks if the method call is calling toString, getName, or name on an enumeration.
     *
     * @param ast The AST to examine.
     * @return {@code true} if the method call is on a enumeration.
     */
    private static boolean isEnumerationCall(DetailAST ast) {
        boolean result = false;
        final DetailAST firstChild = ast.getFirstChild();
        final DetailAST methodCalled = firstChild.getNextSibling();
        final DetailAST parameters = ast.getNextSibling();

        if (firstChild.getFirstChild().getType() == TokenTypes.IDENT
                && ("toString".equals(methodCalled.getText())
                        || "getName".equals(methodCalled.getText())
                        || "name".equals(methodCalled.getText()))
                && parameters.getChildCount() == 0) {
            result = true;
        }

        return result;
    }

    /**
     * Checks if the method call is 'getPath' on a {@link File} variable.
     *
     * @param firstChild The AST to examine.
     * @return {@code true} if the method call is on a file variable.
     */
    private boolean isFileVariable(DetailAST firstChild) {
        return METHOD_GET_PATH.equals(firstChild.getNextSibling().getText())
                && fileVariableNames.contains(firstChild.getText());
    }

    /**
     * Checks if the method name is a form of 'getPath'.
     *
     * @param methodName The name to examine.
     * @return {@code true} if the method is of the form.
     */
    private static boolean isMethodGetPath(String methodName) {
        return METHOD_GET_PATH.equals(methodName)
                || "getNonCompilablePath".equals(methodName)
                || "getUriString".equals(methodName)
                || "getResourcePath".equals(methodName);
    }

    @Override
    public void leaveToken(DetailAST ast) {
        if (ast == methodAst) {
            if (foundVerify) {
                if (checkConfigNames.isEmpty()) {
                    violations.put(ast, MSG_KEY_CONFIG_NOT_FOUND);
                }

                for (Map.Entry<DetailAST, String> entry : violations.entrySet()) {
                    log(entry.getKey(), entry.getValue());
                }
            }

            resetInternalFields();
        }
    }

    /** Resets the internal fields when a new file/method is to be processed. */
    private void resetInternalFields() {
        methodAst = null;
        fileVariableNames.clear();
        checkConfigNames.clear();
        foundVerify = false;
        violations.clear();
    }

}
