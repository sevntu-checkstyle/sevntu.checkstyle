////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2012  Oliver Burn
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import com.puppycrawl.tools.checkstyle.api.Check;
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
 * </p>
 * <p>
 * <b>What check can detect:</b> <br>
 * <b>Loggers</b>
 * <ul>
 * <li>logger is declared as class field</li>
 * <li>logger is declared as method's local variable</li>
 * <li>logger is declared as local variable in <code>catch</code> block</li>
 * <li>logger is passed through method's parameters</li>
 * </ul>
 * <b>Exceptions</b>
 * <ul>
 * <li>logger logs <code>catch</code> parameter exception or it's message</li>
 * <li>throw <code>catch</code> parameter exception</li>
 * <li>throw another exception which is based on <code>catch</code> parameter
 * exception</li>
 * <li>printStackTrace was called on <code>catch</code> parameter exception</li>
 * </ul>
 * </p>
 * <p>
 * <b>What check can not detect:</b> <br>
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
 * </p>
 * <p>
 * Default parameters are:
 * <ul>
 * <li><b>loggerFullyQualifiedClassName</b> - fully qualified class name of
 * logger type. Default value is <i>"org.slf4j.Logger"</i>.</li>
 * <li><b>loggingMethodNames</b> - comma separated names of logging methods.
 * Default value is <i>"error, warn, info, debug"</i>.</li>
 * </ul>
 * </p>
 * <p>
 * Note that check works with only one logger type. If you have multiple
 * different loggers, then create another instance of this check.
 * </p>
 * @author <a href="mailto:barataliba@gmail.com">Baratali Izmailov</a>
 */
public class EitherLogOrThrowCheck extends Check
{
    /**
     * Key for error message.
     */
    public static final String MSG_KEY = "either.log.or.throw";
    /**
     * Logger fully qualified class name.
     */
    private String mLoggerFullyQualifiedClassName = "org.slf4j.Logger";
    /**
     * Logger class name.
     */
    private String mLoggerSimpleClassName = "Logger";
    /**
     * Logger method names.
     */
    private List<String> mLoggingMethodNames =
            Arrays.asList("error", "warn", "info", "debug");
    /**
     * Variables names of logger variables.
     */
    private List<String> mLoggerFieldNames = new LinkedList<String>();
    /**
     * Logger class is in imports.
     */
    private boolean mHasLoggerClassInImports;
    /**
     * Regexp of printStackTrace method.
     */
    private static final Pattern PRINT_STACK_TRACE_METHOD_PATTERN = Pattern
            .compile(".+\\.printStackTrace");
    /**
     * Considered class definition.
     */
    private DetailAST mCurrentClassDefAst;
    /**
     * Considered method definition.
     */
    private DetailAST mCurrentMethodDefAst;
    /**
     * Current local variable names of logger type. It can be method's parameter
     * or method's local variable.
     */
    private List<String> mCurrentLocalLoggerVariableNames = new ArrayList<String>();

    /**
     * Set logger full class name and logger simple class name.
     * @param aLoggerFullyQualifiedClassName
     *        Logger full class name. Example: org.slf4j.Logger.
     */
    public void setLoggerFullyQualifiedClassName(
            String aLoggerFullyQualifiedClassName)
    {
        mLoggerFullyQualifiedClassName = aLoggerFullyQualifiedClassName;
        mLoggerSimpleClassName = mLoggerFullyQualifiedClassName;
        final int lastDotIndex =
                mLoggerFullyQualifiedClassName.lastIndexOf('.');
        if (lastDotIndex != -1) {
            mLoggerSimpleClassName = mLoggerFullyQualifiedClassName
                    .substring(lastDotIndex + 1);
        }
    }

    public void setLoggingMethodNames(String[] aLoggingMethodNames)
    {
        mLoggingMethodNames = Arrays.asList(aLoggingMethodNames);
    }

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] {
            TokenTypes.IMPORT,
            TokenTypes.CLASS_DEF,
            TokenTypes.LITERAL_CATCH,
            TokenTypes.VARIABLE_DEF,
            TokenTypes.METHOD_DEF, };
    }

    @Override
    public void visitToken(final DetailAST aAst)
    {
        switch (aAst.getType()) {
        case TokenTypes.IMPORT:
            if (!mHasLoggerClassInImports
                    && isLoggerImport(aAst))
            {
                mHasLoggerClassInImports = true;
            }
            break;
        case TokenTypes.CLASS_DEF:
            if (!isInnerClass(aAst)) {
                mCurrentClassDefAst = aAst;
                collectLoggerFieldNames(aAst);
            }
            break;
        case TokenTypes.METHOD_DEF:
            if (isMethodOfCurrentClass(aAst)) {
                mCurrentMethodDefAst = aAst;
                mCurrentLocalLoggerVariableNames.clear();
                final DetailAST parametersAst = mCurrentMethodDefAst
                        .findFirstToken(TokenTypes.PARAMETERS);
                collectLoggersFromParameters(parametersAst);
            }
            break;
        case TokenTypes.VARIABLE_DEF:
            final DetailAST methodDefAst = aAst.getParent().getParent();
            if (methodDefAst.getType() == TokenTypes.METHOD_DEF
                    && methodDefAst == mCurrentMethodDefAst
                    && isLoggerVariableDefinition(aAst))
            {
                mCurrentLocalLoggerVariableNames.add(getIdentifier(aAst));
            }
            break;
        case TokenTypes.LITERAL_CATCH:
            processCatchNode(aAst);
            break;
        default:
            throw new IllegalArgumentException("Non-correct AST node.");
        }
    }

    /**
     * @param aImportAst
     *        DetailAST of import statement.
     * @return true if import equals logger full class name.
     */
    private boolean isLoggerImport(final DetailAST aImportAst)
    {
        final String importIdentifier =
                FullIdent.createFullIdent(aImportAst.getFirstChild()).getText();
        return mLoggerFullyQualifiedClassName.equals(importIdentifier);
    }

    /**
     * Verify that class is inner.
     * @param aClassDefAst
     *        DetailAST of class definition.
     * @return true if class is inner, false otherwise.
     */
    private boolean isInnerClass(final DetailAST aClassDefAst)
    {
        boolean result = false;
        DetailAST parentAst = aClassDefAst.getParent();
        while (parentAst != null) {
            if (parentAst == mCurrentClassDefAst) {
                result = true;
                break;
            }
            parentAst = parentAst.getParent();
        }
        return result;
    }

    /**
     * Save names of parameters which have logger type.
     * @param aParametersAst
     *        DetailAST of parameters.
     */
    private void collectLoggersFromParameters(final DetailAST aParametersAst)
    {
        DetailAST currentParameterAst = aParametersAst
                .findFirstToken(TokenTypes.PARAMETER_DEF);
        while (currentParameterAst != null) {
            final DetailAST parameterTypeAst = currentParameterAst
                    .findFirstToken(TokenTypes.TYPE);
            final String className = getIdentifier(parameterTypeAst);
            if (className != null && isLoggerClassName(className)) {
                mCurrentLocalLoggerVariableNames
                        .add(getIdentifier(currentParameterAst));
            }
            currentParameterAst = currentParameterAst.getNextSibling();
        }
    }

    /**
     * Verify that method's parent is class, stored in mCurrentClassDefAst.
     * @param aMethodDefAst DetailAST of METHOD_DEF.
     * @return true if method's parent is class, stored in mCurrentClassDefAst.
     */
    private boolean isMethodOfCurrentClass(final DetailAST aMethodDefAst)
    {
        final DetailAST classDefAst = aMethodDefAst.getParent().getParent();
        return classDefAst == mCurrentClassDefAst;
    }

    /**
     * Find all logger fields in aClassDefAst and save them.
     * @param aClassDefAst
     *        DetailAST of class definition.
     */
    private void collectLoggerFieldNames(final DetailAST aClassDefAst)
    {
        final DetailAST objBlockAst =
                aClassDefAst.findFirstToken(TokenTypes.OBJBLOCK);
        DetailAST variableDefAst =
                objBlockAst.findFirstToken(TokenTypes.VARIABLE_DEF);
        while (variableDefAst != null) {
            if (variableDefAst.getType() == TokenTypes.VARIABLE_DEF
                    && isLoggerVariableDefinition(variableDefAst))
            {
                mLoggerFieldNames.add(getIdentifier(variableDefAst));
            }
            variableDefAst = variableDefAst.getNextSibling();
        }
    }

    /**
     * Look at the each statement of catch block to find logging and throwing.
     * If same exception is being logged and throwed, then prints warning
     * message.
     * @param aCatchAst
     *        DetailAST of catch block.
     */
    private void processCatchNode(final DetailAST aCatchAst)
    {
        boolean isLoggingExceptionFound = false;
        int loggingExceptionLineNumber = 0;
        final List<String> exceptionVariableNames = new LinkedList<String>();
        final String catchParameterName = getCatchParameterName(aCatchAst);
        final DetailAST statementsAst =
                aCatchAst.findFirstToken(TokenTypes.SLIST);
        DetailAST currentStatementAst = statementsAst.getFirstChild();
        while (currentStatementAst != null) {
            switch (currentStatementAst.getType()) {
            // local logger or exception variable definition
            case TokenTypes.VARIABLE_DEF:
                if (isLoggerVariableDefinition(currentStatementAst)) {
                    mCurrentLocalLoggerVariableNames
                            .add(getIdentifier(currentStatementAst));
                }
                else {
                    final DetailAST assignAst = currentStatementAst
                            .findFirstToken(TokenTypes.ASSIGN);
                    if (assignAst != null
                            && isInstanceCreationBasedOnException(
                                    assignAst.getFirstChild(),
                                    catchParameterName))
                    {
                        exceptionVariableNames
                                .add(getIdentifier(currentStatementAst));
                    }
                }
                break;
            // logging exception or printStackTrace
            case TokenTypes.EXPR:
                if (!isLoggingExceptionFound
                        && (isLoggingExceptionArgument(currentStatementAst, catchParameterName)
                        || isPrintStackTrace(currentStatementAst, catchParameterName)))
                {
                    isLoggingExceptionFound = true;
                    loggingExceptionLineNumber = currentStatementAst.getLineNo();
                }
                break;
            // throw exception
            case TokenTypes.LITERAL_THROW:
                if (isLoggingExceptionFound)
                {
                    exceptionVariableNames.add(catchParameterName);
                    final DetailAST thrownExceptionAst = currentStatementAst
                            .getFirstChild();
                    if (exceptionVariableNames.contains(getIdentifier(thrownExceptionAst))
                            || isInstanceCreationBasedOnException(
                                    thrownExceptionAst, catchParameterName))
                    {
                        log(loggingExceptionLineNumber, "either.log.or.throw");
                        break;
                    }
                }
            }
            currentStatementAst = currentStatementAst.getNextSibling();
        }
    }

    /**
     * Verify that aVariableDefAst is variable of logger type.
     * @param aVariableDefAst
     *        DetailAST of variable definition.
     * @return true if variable is of logger type.
     */
    private boolean isLoggerVariableDefinition(final DetailAST aVariableDefAst)
    {
        final DetailAST variableTypeAst =
                aVariableDefAst.findFirstToken(TokenTypes.TYPE).getFirstChild();
        final String variableTypeName =
                FullIdent.createFullIdent(variableTypeAst).getText();
        return isLoggerClassName(variableTypeName);
    }

    /**
     * Verify that aClassName is class name of logger type.
     * @param aClassName name of checked class.
     * @return true aClassName is class name of logger type.
     */
    private boolean isLoggerClassName(String aClassName)
    {
        return (mHasLoggerClassInImports
                && aClassName.equals(mLoggerSimpleClassName))
                || aClassName.equals(mLoggerFullyQualifiedClassName);
    }

    /**
     * Get parameter name of catch block.
     * @param aCatchAst
     *        DetailAST of catch block.
     * @return name of parameter.
     */
    private static String getCatchParameterName(final DetailAST aCatchAst)
    {
        final DetailAST parameterDefAst =
                aCatchAst.findFirstToken(TokenTypes.PARAMETER_DEF);
        return getIdentifier(parameterDefAst);
    }

    /**
     * Get identifier of AST. These can be names of types, subpackages, fields,
     * methods, parameters, and local variables.
     * @param aAST
     *        DetailAST instance
     * @return identifier of AST, null if AST does not have name.
     */
    private static String getIdentifier(final DetailAST aAST)
    {
        String result = null;
        if (aAST != null) {
            final DetailAST identAst = aAST.findFirstToken(TokenTypes.IDENT);
            if (identAst != null) {
                result = identAst.getText();
            }
        }
        return result;
    }

    /**
     * Verify that expression is creating instance. And this instance is created
     * with exception argument. Example: new MyException("message", exception).
     * @param aExpressionAst
     *        DetailAST of expression.
     * @param aExceptionArgumentName
     *        Exception argument name.
     * @return true if given expression is creating new exception based on
     *         another exception object named aExeceptionParameterName.
     */
    private static boolean isInstanceCreationBasedOnException(
            final DetailAST aExpressionAst, final String aExceptionArgumentName)
    {
        boolean result = false;
        final DetailAST literalNewAst =
                aExpressionAst.findFirstToken(TokenTypes.LITERAL_NEW);
        if (literalNewAst != null) {
            final DetailAST parametersAst = literalNewAst
                    .findFirstToken(TokenTypes.ELIST);
            if (parametersAst != null) {
                result = containsExceptionParameter(parametersAst,
                        aExceptionArgumentName);
            }
        }
        return result;
    }

    /**
     * Verify that expression is logging exception.
     * @param aExpressionAst DetailAST of expression(EXPR).
     * @param aExceptionVariableName name of exception variable.
     * @return true if expression is logging exception.
     */
    private boolean isLoggingExceptionArgument(
            final DetailAST aExpressionAst, final String aExceptionVariableName)
    {
        boolean result = false;
        if (isLoggingExpression(aExpressionAst)) {
            final DetailAST loggingMethodCallAst =
                    aExpressionAst.getFirstChild();
            final DetailAST loggerParametersAst =
                    loggingMethodCallAst.findFirstToken(TokenTypes.ELIST);
            result = containsExceptionParameter(
                    loggerParametersAst, aExceptionVariableName);
        }
        return result;
    }

    /**
     * Verify that aExpressionAst is a logging expression.
     * @param aExpressionAst
     *        DetailAST of expression.
     * @return true if aExpressionAst is a logging expression.
     */
    private boolean isLoggingExpression(final DetailAST aExpressionAst)
    {
        boolean result = false;
        final DetailAST methodCallAst = aExpressionAst.getFirstChild();
        if (methodCallAst.getType() == TokenTypes.METHOD_CALL
                && hasChildToken(methodCallAst, TokenTypes.DOT))
        {
            final DetailAST dotAst = methodCallAst.getFirstChild();
            final DetailAST loggerObjectAst = dotAst.getFirstChild();
            final DetailAST invokedMethodAst = loggerObjectAst.getNextSibling();
            final String loggerObjectIdentifier =
                    FullIdent.createFullIdent(loggerObjectAst).getText();
            final String invokedMethodIdentifier = invokedMethodAst.getText();
            result = (mCurrentLocalLoggerVariableNames
                    .contains(loggerObjectIdentifier)
                    || mLoggerFieldNames.contains(loggerObjectIdentifier))
                    && mLoggingMethodNames.contains(invokedMethodIdentifier);
        }
        return result;
    }

    /**
     * Verify that aExceptionVariableName is in aParametersAst.
     * @param aParametersAst
     *            DetailAST of expression list(ELIST).
     * @param aExceptionVariableName
     *            name of exception.
     * @return true if aExceptionVariableName is in aParametersAst.
     */
    private static boolean containsExceptionParameter(
            final DetailAST aParametersAst, final String aExceptionVariableName)
    {
        boolean result = false;
        DetailAST parameterAst = aParametersAst.getFirstChild();
        while (parameterAst != null) {
            if (aExceptionVariableName.equals(getIdentifier(parameterAst)))
            {
                result = true;
                break;
            }
            else {
                final DetailAST methodCallAst = parameterAst.getFirstChild();
                if (isInstanceMethodCall(aExceptionVariableName,
                        methodCallAst))
                {
                    result = true;
                    break;
                }
            }
            parameterAst = parameterAst.getNextSibling();
        }
        return result;
    }

    /**
     * Verify that expression is call of exception's printStackTrace method.
     * @param aExpressionAst
     *        DetailAST of expression.
     * @param aExceptionVariableName
     *        name of exception variable.
     * @return true if expression is call of exception's printStackTrace method.
     */
    private static boolean isPrintStackTrace(final DetailAST aExpressionAst,
            final String aExceptionVariableName)
    {
        boolean result = false;
        final DetailAST methodCallAst = aExpressionAst.getFirstChild();
        if (isInstanceMethodCall(aExceptionVariableName, methodCallAst)) {
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
     * @param aUsedInstanseName name of instance.
     * @param aMethodCallAst DetailAST of METHOD_CALL.
     * @return true if method is invoked on aUsedInstanceName.
     */
    private static boolean isInstanceMethodCall(final String aUsedInstanseName,
            final DetailAST aMethodCallAst)
    {
        boolean result = false;
        if (aMethodCallAst != null
                && aMethodCallAst.getType() == TokenTypes.METHOD_CALL)
        {
            final String methodCallIdent =
                    FullIdent.createFullIdentBelow(aMethodCallAst).getText();
            final int firstDotIndex = methodCallIdent.indexOf('.');
            if (firstDotIndex != -1) {
                final String usedObjectName =
                        methodCallIdent.substring(0, firstDotIndex);
                if (usedObjectName.equals(aUsedInstanseName)) {
                    result = true;
                }
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
    private static boolean hasChildToken(final DetailAST aAST, int aTokenType)
    {
        return aAST.findFirstToken(aTokenType) != null;
    }

}
