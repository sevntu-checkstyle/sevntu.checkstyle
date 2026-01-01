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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import com.github.sevntu.checkstyle.SevntuUtil;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * Either log the exception, or throw it, but never do both. Logging and
 * throwing results in multiple log messages for a single problem in the code,
 * and makes problems for the support engineer who is trying to dig through the
 * logs. This is one of the most annoying error-handling antipatterns. All of
 * these examples are equally wrong.
 * </p>
 * <p>
 * <b>Examples:</b>
 * </p>
 *
 * <pre>
 * catch (NoSuchMethodException e) {
 *     LOG.error("Message", e);
 *     throw e;
 * }
 * </pre>
 *
 * <b>or</b>
 *
 * <pre>
 * catch (NoSuchMethodException e) {
 *     LOG.error("Message", e);
 *     throw new MyServiceException("AnotherMessage", e);
 * }
 * </pre>
 *
 * <b>or</b>
 *
 * <pre>
 * catch (NoSuchMethodException e) {
 *      e.printStackTrace();
 *      throw new MyServiceException("Message", e);
 * }
 * </pre>
 *
 * <p>
 * <b>What check can detect:</b> <br>
 * <b>Loggers</b>
 * </p>
 * <ul>
 * <li>logger is declared as class field</li>
 * <li>logger is declared as method's local variable</li>
 * <li>logger is declared as local variable in <code>catch</code> block</li>
 * <li>logger is passed through method's parameters</li>
 * </ul>
 * <b>Exceptions</b>
 * <ul>
 * <li>logger logs <code>catch</code> parameter exception or its message</li>
 * <li>throw <code>catch</code> parameter exception</li>
 * <li>throw another exception which is based on <code>catch</code> parameter
 * exception</li>
 * <li>printStackTrace was called on <code>catch</code> parameter exception</li>
 * </ul>
 * <p>
 * <b>What check can not detect:</b> <br>
 * </p>
 * <ul>
 * <li>loggers that is used like method's return value. Example:
 *
 * <pre>
 * getLogger().error(&quot;message&quot;, e)
 * </pre>
 *
 * </li>
 * <li>loggers that is used like static fields from another classes:
 *
 * <pre>
 * MyAnotherClass.LOGGER.error("message", e);
 * </pre>
 * </li>
 * </ul>
 * <p>
 * Default parameters are:
 * </p>
 * <ul>
 * <li><b>loggerFullyQualifiedClassName</b> - fully qualified class name of
 * logger type. Default value is <i>"org.slf4j.Logger"</i>.</li>
 * <li><b>loggingMethodNames</b> - comma separated names of logging methods.
 * Default value is <i>"error, warn, info, debug"</i>.</li>
 * </ul>
 * <p>
 * Note that check works with only one logger type. If you have multiple
 * different loggers, then create another instance of this check.
 * </p>
 *
 * @author <a href="mailto:barataliba@gmail.com">Baratali Izmailov</a>
 * @since 1.9.0
 */
public class EitherLogOrThrowCheck extends AbstractCheck {

    /**
     * Key for error message.
     */
    public static final String MSG_KEY = "either.log.or.throw";

    /**
     * Regexp of printStackTrace method.
     */
    private static final Pattern PRINT_STACK_TRACE_METHOD_PATTERN = Pattern
            .compile(".+\\.printStackTrace");

    /**
     * Variables names of logger variables.
     */
    private final List<String> loggerFieldNames = new LinkedList<>();

    /**
     * Current local variable names of logger type. It can be method's parameter
     * or method's local variable.
     */
    private final List<String> currentLocalLoggerVariableNames = new ArrayList<>();

    /**
     * Logger fully qualified class name.
     */
    private String loggerFullyQualifiedClassName = "org.slf4j.Logger";

    /**
     * Logger class name.
     */
    private String loggerSimpleClassName = "Logger";

    /**
     * Logger method names.
     */
    private List<String> loggingMethodNames =
            Arrays.asList("error", "warn", "info", "debug");

    /**
     * Logger class is in imports.
     */
    private boolean hasLoggerClassInImports;

    /**
     * Considered class definition.
     */
    private DetailAST currentClassDefAst;

    /**
     * Considered method definition.
     */
    private DetailAST currentMethodDefAst;

    /**
     * Set logger full class name and logger simple class name.
     *
     * @param loggerFullyQualifiedClassName
     *        Logger full class name. Example: org.slf4j.Logger.
     */
    public void setLoggerFullyQualifiedClassName(
            String loggerFullyQualifiedClassName) {
        this.loggerFullyQualifiedClassName = loggerFullyQualifiedClassName;
        loggerSimpleClassName = loggerFullyQualifiedClassName;
        final int lastDotIndex =
                this.loggerFullyQualifiedClassName.lastIndexOf('.');
        if (lastDotIndex != -1) {
            loggerSimpleClassName = this.loggerFullyQualifiedClassName
                    .substring(lastDotIndex + 1);
        }
    }

    /**
     * Set logging method names.
     *
     * @param loggingMethodNames Logger method names.
     */
    public void setLoggingMethodNames(String... loggingMethodNames) {
        this.loggingMethodNames = Arrays.asList(loggingMethodNames);
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.IMPORT,
            TokenTypes.CLASS_DEF,
            TokenTypes.LITERAL_CATCH,
            TokenTypes.VARIABLE_DEF,
            TokenTypes.METHOD_DEF, };
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
    public void visitToken(final DetailAST ast) {
        switch (ast.getType()) {
            case TokenTypes.IMPORT:
                if (!hasLoggerClassInImports
                    && isLoggerImport(ast)) {
                    hasLoggerClassInImports = true;
                }
                break;
            case TokenTypes.CLASS_DEF:
                if (!isInnerClass(ast)) {
                    currentClassDefAst = ast;
                    collectLoggerFieldNames(ast);
                }
                break;
            case TokenTypes.METHOD_DEF:
                if (isMethodOfCurrentClass(ast)) {
                    currentMethodDefAst = ast;
                    currentLocalLoggerVariableNames.clear();
                    final DetailAST parametersAst = currentMethodDefAst
                        .findFirstToken(TokenTypes.PARAMETERS);
                    collectLoggersFromParameters(parametersAst);
                }
                break;
            case TokenTypes.VARIABLE_DEF:
                final DetailAST methodDefAst = ast.getParent().getParent();
                if (methodDefAst == currentMethodDefAst
                    && methodDefAst.getType() == TokenTypes.METHOD_DEF
                    && isLoggerVariableDefinition(ast)) {
                    currentLocalLoggerVariableNames.add(getIdentifier(ast));
                }
                break;
            case TokenTypes.LITERAL_CATCH:
                processCatchNode(ast);
                break;
            default:
                SevntuUtil.reportInvalidToken(ast.getType());
                break;
        }
    }

    /**
     * Checks if AST object is logger import.
     *
     * @param importAst
     *        DetailAST of import statement.
     * @return true if import equals logger full class name.
     */
    private boolean isLoggerImport(final DetailAST importAst) {
        final String importIdentifier =
                FullIdent.createFullIdent(importAst.getFirstChild()).getText();
        return loggerFullyQualifiedClassName.equals(importIdentifier);
    }

    /**
     * Verify that class is inner.
     *
     * @param classDefAst
     *        DetailAST of class definition.
     * @return true if class is inner, false otherwise.
     */
    private boolean isInnerClass(final DetailAST classDefAst) {
        boolean result = false;
        DetailAST parentAst = classDefAst.getParent();
        while (parentAst != null) {
            if (parentAst == currentClassDefAst) {
                result = true;
                break;
            }
            parentAst = parentAst.getParent();
        }
        return result;
    }

    /**
     * Save names of parameters which have logger type.
     *
     * @param parametersAst
     *        DetailAST of parameters.
     */
    private void collectLoggersFromParameters(final DetailAST parametersAst) {
        DetailAST currentParameterAst = parametersAst
                .findFirstToken(TokenTypes.PARAMETER_DEF);
        while (currentParameterAst != null) {
            final DetailAST parameterTypeAst = currentParameterAst
                    .findFirstToken(TokenTypes.TYPE);
            final String className = getIdentifier(parameterTypeAst);
            if (className != null && isLoggerClassName(className)) {
                currentLocalLoggerVariableNames
                        .add(getIdentifier(currentParameterAst));
            }
            currentParameterAst = currentParameterAst.getNextSibling();
        }
    }

    /**
     * Verify that method's parent is class, stored in mCurrentClassDefAst.
     *
     * @param methodDefAst DetailAST of METHOD_DEF.
     * @return true if method's parent is class, stored in mCurrentClassDefAst.
     */
    private boolean isMethodOfCurrentClass(final DetailAST methodDefAst) {
        final DetailAST classDefAst = methodDefAst.getParent().getParent();
        return classDefAst == currentClassDefAst;
    }

    /**
     * Find all logger fields in aClassDefAst and save them.
     *
     * @param classDefAst
     *        DetailAST of class definition.
     */
    private void collectLoggerFieldNames(final DetailAST classDefAst) {
        final DetailAST objBlockAst =
                classDefAst.findFirstToken(TokenTypes.OBJBLOCK);
        DetailAST variableDefAst =
                objBlockAst.findFirstToken(TokenTypes.VARIABLE_DEF);
        while (variableDefAst != null) {
            if (variableDefAst.getType() == TokenTypes.VARIABLE_DEF
                    && isLoggerVariableDefinition(variableDefAst)) {
                loggerFieldNames.add(getIdentifier(variableDefAst));
            }
            variableDefAst = variableDefAst.getNextSibling();
        }
    }

    /**
     * Look at the each statement of catch block to find logging and throwing.
     * If same exception is being logged and throwed, then prints warning
     * message.
     *
     * @param catchAst
     *        DetailAST of catch block.
     */
    private void processCatchNode(final DetailAST catchAst) {
        boolean isLoggingExceptionFound = false;
        DetailAST loggingExceptionAst = null;
        final List<String> exceptionVariableNames = new LinkedList<>();
        final String catchParameterName = getCatchParameterName(catchAst);
        final DetailAST statementsAst =
                catchAst.findFirstToken(TokenTypes.SLIST);
        DetailAST currentStatementAst = statementsAst.getFirstChild();
        while (currentStatementAst != null) {
            switch (currentStatementAst.getType()) {
                // local logger or exception variable definition
                case TokenTypes.VARIABLE_DEF:
                    if (isLoggerVariableDefinition(currentStatementAst)) {
                        currentLocalLoggerVariableNames
                            .add(getIdentifier(currentStatementAst));
                    }
                    else {
                        final DetailAST assignAst = currentStatementAst
                            .findFirstToken(TokenTypes.ASSIGN);
                        if (assignAst != null
                            && isInstanceCreationBasedOnException(
                                assignAst.getFirstChild(),
                                 catchParameterName)) {
                            exceptionVariableNames
                                .add(getIdentifier(currentStatementAst));
                        }
                    }
                    break;
                    // logging exception or printStackTrace
                case TokenTypes.EXPR:
                    if (!isLoggingExceptionFound
                        && (isLoggingExceptionArgument(currentStatementAst, catchParameterName)
                        || isPrintStackTrace(currentStatementAst, catchParameterName))) {
                        isLoggingExceptionFound = true;
                        loggingExceptionAst = currentStatementAst;
                    }
                    break;
                    // throw exception
                case TokenTypes.LITERAL_THROW:
                    if (isLoggingExceptionFound) {
                        exceptionVariableNames.add(catchParameterName);
                        final DetailAST thrownExceptionAst = currentStatementAst
                            .getFirstChild();
                        if (exceptionVariableNames.contains(getIdentifier(thrownExceptionAst))
                            || isInstanceCreationBasedOnException(
                                thrownExceptionAst, catchParameterName)) {
                            log(loggingExceptionAst, MSG_KEY);
                            break;
                        }
                    }
                    break;
                default:
                    // rest tokens shall be skipped
                    break;
            }
            currentStatementAst = currentStatementAst.getNextSibling();
        }
    }

    /**
     * Verify that aVariableDefAst is variable of logger type.
     *
     * @param variableDefAst
     *        DetailAST of variable definition.
     * @return true if variable is of logger type.
     */
    private boolean isLoggerVariableDefinition(final DetailAST variableDefAst) {
        final DetailAST variableTypeAst =
                variableDefAst.findFirstToken(TokenTypes.TYPE).getFirstChild();
        final String variableTypeName =
                FullIdent.createFullIdent(variableTypeAst).getText();
        return isLoggerClassName(variableTypeName);
    }

    /**
     * Verify that aClassName is class name of logger type.
     *
     * @param className name of checked class.
     * @return true aClassName is class name of logger type.
     */
    private boolean isLoggerClassName(String className) {
        return hasLoggerClassInImports
                && className.equals(loggerSimpleClassName)
                || className.equals(loggerFullyQualifiedClassName);
    }

    /**
     * Get parameter name of catch block.
     *
     * @param catchAst
     *        DetailAST of catch block.
     * @return name of parameter.
     */
    private static String getCatchParameterName(final DetailAST catchAst) {
        final DetailAST parameterDefAst =
                catchAst.findFirstToken(TokenTypes.PARAMETER_DEF);
        return getIdentifier(parameterDefAst);
    }

    /**
     * Get identifier of AST. These can be names of types, subpackages, fields,
     * methods, parameters, and local variables.
     *
     * @param ast
     *        DetailAST instance
     * @return identifier of AST, null if AST does not have name.
     */
    private static String getIdentifier(final DetailAST ast) {
        String result = null;
        if (ast != null) {
            final DetailAST identAst = ast.findFirstToken(TokenTypes.IDENT);
            if (identAst != null) {
                result = identAst.getText();
            }
        }
        return result;
    }

    /**
     * Verify that expression is creating instance. And this instance is created
     * with exception argument. Example: new MyException("message", exception).
     *
     * @param expressionAst
     *        DetailAST of expression.
     * @param exceptionArgumentName
     *        Exception argument name.
     * @return true if given expression is creating new exception based on
     *         another exception object named aExeceptionParameterName.
     */
    private static boolean isInstanceCreationBasedOnException(
            final DetailAST expressionAst, final String exceptionArgumentName) {
        boolean result = false;
        final DetailAST literalNewAst =
                expressionAst.findFirstToken(TokenTypes.LITERAL_NEW);
        if (literalNewAst != null) {
            final DetailAST parametersAst = literalNewAst
                    .findFirstToken(TokenTypes.ELIST);
            if (parametersAst != null) {
                result = containsExceptionParameter(parametersAst,
                        exceptionArgumentName);
            }
        }
        return result;
    }

    /**
     * Verify that expression is logging exception.
     *
     * @param expressionAst DetailAST of expression(EXPR).
     * @param exceptionVariableName name of exception variable.
     * @return true if expression is logging exception.
     */
    private boolean isLoggingExceptionArgument(
            final DetailAST expressionAst, final String exceptionVariableName) {
        boolean result = false;
        if (isLoggingExpression(expressionAst)) {
            final DetailAST loggingMethodCallAst =
                    expressionAst.getFirstChild();
            final DetailAST loggerParametersAst =
                    loggingMethodCallAst.findFirstToken(TokenTypes.ELIST);
            result = containsExceptionParameter(
                    loggerParametersAst, exceptionVariableName);
        }
        return result;
    }

    /**
     * Verify that aExpressionAst is a logging expression.
     *
     * @param expressionAst
     *        DetailAST of expression.
     * @return true if aExpressionAst is a logging expression.
     */
    private boolean isLoggingExpression(final DetailAST expressionAst) {
        boolean result = false;
        final DetailAST methodCallAst = expressionAst.getFirstChild();
        if (methodCallAst.getType() == TokenTypes.METHOD_CALL
                && hasChildToken(methodCallAst, TokenTypes.DOT)) {
            final DetailAST dotAst = methodCallAst.getFirstChild();
            final DetailAST loggerObjectAst = dotAst.getFirstChild();
            final DetailAST invokedMethodAst = loggerObjectAst.getNextSibling();
            final String loggerObjectIdentifier =
                    FullIdent.createFullIdent(loggerObjectAst).getText();
            final String invokedMethodIdentifier = invokedMethodAst.getText();
            result = (currentLocalLoggerVariableNames
                    .contains(loggerObjectIdentifier)
                    || loggerFieldNames.contains(loggerObjectIdentifier))
                    && loggingMethodNames.contains(invokedMethodIdentifier);
        }
        return result;
    }

    /**
     * Verify that aExceptionVariableName is in aParametersAst.
     *
     * @param parametersAst
     *            DetailAST of expression list(ELIST).
     * @param exceptionVariableName
     *            name of exception.
     * @return true if aExceptionVariableName is in aParametersAst.
     */
    private static boolean containsExceptionParameter(
            final DetailAST parametersAst, final String exceptionVariableName) {
        boolean result = false;
        DetailAST parameterAst = parametersAst.getFirstChild();

        while (parameterAst != null) {
            if (exceptionVariableName.equals(getIdentifier(parameterAst))
                    || isInstanceMethodCall(exceptionVariableName,
                            parameterAst.getFirstChild())) {
                result = true;
                parameterAst = null;
            }
            else {
                parameterAst = parameterAst.getNextSibling();
            }
        }
        return result;
    }

    /**
     * Verify that expression is call of exception's printStackTrace method.
     *
     * @param expressionAst
     *        DetailAST of expression.
     * @param exceptionVariableName
     *        name of exception variable.
     * @return true if expression is call of exception's printStackTrace method.
     */
    private static boolean isPrintStackTrace(final DetailAST expressionAst,
            final String exceptionVariableName) {
        boolean result = false;
        final DetailAST methodCallAst = expressionAst.getFirstChild();
        if (isInstanceMethodCall(exceptionVariableName, methodCallAst)) {
            final String methodCallStr =
                    FullIdent.createFullIdentBelow(methodCallAst).getText();
            if (PRINT_STACK_TRACE_METHOD_PATTERN.matcher(methodCallStr).matches()) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Verify that method is invoked on aUsedInstanceName.
     *
     * @param usedInstanceName name of instance.
     * @param methodCallAst DetailAST of METHOD_CALL.
     * @return true if method is invoked on aUsedInstanceName.
     */
    private static boolean isInstanceMethodCall(final String usedInstanceName,
            final DetailAST methodCallAst) {
        boolean result = false;
        if (methodCallAst != null
                && methodCallAst.getType() == TokenTypes.METHOD_CALL) {
            final String methodCallIdent =
                    FullIdent.createFullIdentBelow(methodCallAst).getText();
            final int firstDotIndex = methodCallIdent.indexOf('.');
            if (firstDotIndex != -1) {
                final String usedObjectName =
                        methodCallIdent.substring(0, firstDotIndex);
                if (usedObjectName.equals(usedInstanceName)) {
                    result = true;
                }
            }
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
    private static boolean hasChildToken(final DetailAST ast, int tokenType) {
        return ast.findFirstToken(tokenType) != null;
    }

}
