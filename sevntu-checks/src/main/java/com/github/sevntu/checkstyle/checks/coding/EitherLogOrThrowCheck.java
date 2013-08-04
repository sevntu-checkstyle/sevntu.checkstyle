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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * Detects cases when in catch clause programmer logs exception's stack trace
 * and throws same(or derived) exception again.
 * </p>
 * <p>
 * <b>Examples:</b>
 * <pre>
 * catch (NoSuchMethodException e) {
 *     LOG.error("Message", e);
 *     throw e;
 * }
 * </pre>
 * <b>or</b>
 * <pre>
 * catch (NoSuchMethodException e) {
 *     LOG.error("Message", e);
 *     throw new MyServiceException("AnotherMessage", e);
 * }
 * </pre>
 * </p>
 * <p>
 * Logging and throwing exception will lead to multiple messages of the same
 * problem. So find origin place of problem will be difficult.
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
     * Logger full class name.
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
            TokenTypes.LITERAL_CATCH, };
    }

    @Override
    public void visitToken(DetailAST aAst)
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
            saveClassFieldNamesOfLoggerType(aAst);
            break;
        case TokenTypes.LITERAL_CATCH:
            processCatchNode(aAst);
            break;
        default:
            throw new IllegalArgumentException("Non-correct AST node.");
        }
    }

    /**
     * Find all logger fields in aClassDefAst and save them.
     * @param aClassDefAst DetailAST of class definition.
     */
    private void saveClassFieldNamesOfLoggerType(DetailAST aClassDefAst)
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
     * @param aCatchAst DetailAST of catch block.
     */
    private void processCatchNode(DetailAST aCatchAst)
    {
        boolean loggingExceptionFound = false;
        int loggingExceptionLineNumber = 0;
        final List<String> loggerVariableNames = new LinkedList<String>();
        final List<String> exceptionVariableNames = new LinkedList<String>();
        final String catchParameterName = getCatchParameterName(aCatchAst);
        final DetailAST statementsAst =
                aCatchAst.findFirstToken(TokenTypes.SLIST);
        DetailAST currentStatementAst = statementsAst.getFirstChild();
        while (currentStatementAst != null) {
            // variable definition
            if (currentStatementAst.getType() == TokenTypes.VARIABLE_DEF) {
                if (isLoggerVariableDefinition(currentStatementAst)) {
                    loggerVariableNames
                            .add(getIdentifier(currentStatementAst));
                }
                else if (isExceptionCreatedWithCause(currentStatementAst,
                        catchParameterName))
                {
                    exceptionVariableNames
                            .add(getIdentifier(currentStatementAst));
                }
            }
            // expression
            else if (currentStatementAst.getType() == TokenTypes.EXPR) {
                if (!loggingExceptionFound
                        && isLoggingExpression(currentStatementAst,
                                loggerVariableNames)
                        && hasExceptionAsParameter(currentStatementAst,
                                catchParameterName))
                {
                    loggingExceptionFound = true;
                    loggingExceptionLineNumber = currentStatementAst
                            .getLineNo();
                }
            }
            // throw statement
            else if (loggingExceptionFound
                    && currentStatementAst.getType() == TokenTypes.LITERAL_THROW
                    && throwsException(currentStatementAst,
                            catchParameterName, exceptionVariableNames))
            {
                log(loggingExceptionLineNumber, "either.log.or.throw");
                break;
            }
            currentStatementAst = currentStatementAst.getNextSibling();
        }
    }

    /**
     * @param aImportAst
     *        DetailAST of import statement.
     * @return true if import equals logger full class name.
     */
    private boolean isLoggerImport(DetailAST aImportAst)
    {
        final String importIdentifier =
                FullIdent.createFullIdent(aImportAst.getFirstChild()).getText();
        return mLoggerFullyQualifiedClassName.equals(importIdentifier);
    }

    /**
     * Verify that aVariableDefAst is variable of logger type.
     * @param aVariableDefAst
     *        DetailAST of variable definition.
     * @return true if variable is of logger type.
     */
    private boolean isLoggerVariableDefinition(final DetailAST aVariableDefAst)
    {
        boolean result = false;
        final DetailAST variableTypeAst =
                aVariableDefAst.findFirstToken(TokenTypes.TYPE).getFirstChild();
        final String variableTypeIdentifier =
                FullIdent.createFullIdent(variableTypeAst).getText();
        if (mHasLoggerClassInImports) {
            result = variableTypeIdentifier.equals(mLoggerSimpleClassName);
        }
        if (!result) {
            result = variableTypeIdentifier
                    .equals(mLoggerFullyQualifiedClassName);
        }
        return result;
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
        final DetailAST identAst = aAST.findFirstToken(TokenTypes.IDENT);
        if (identAst != null) {
            return identAst.getText();
        }
        return null;
    }

    /**
     * Verify that given variable definition is exception object definition with
     * cause aExceptionCauseName.
     * @param aVariableDefAst DetailAST of variable definition.
     * @param aExceptionCauseName name of cause exception.
     * @return true if given variable definition is exception object definition
     * with cause aExceptionCauseName.
     */
    private static boolean isExceptionCreatedWithCause(
            DetailAST aVariableDefAst, String aExceptionCauseName)
    {
        boolean result = false;
        final DetailAST assignAst = aVariableDefAst
                .findFirstToken(TokenTypes.ASSIGN);
        if (assignAst != null) {
            result = isCreatingExceptionObject(assignAst.getFirstChild(),
                    aExceptionCauseName);
        }
        return result;
    }

    /**
     * Verify that given expression is creating new exception based on another
     * exception object named aExeceptionParameterName.
     * @param aExpressionAst
     *        DetailAST of expression.
     * @param aExceptionParameterName
     *        Exception parameter name.
     * @return true if given expression is creating new exception based on
     *         another exception object named aExeceptionParameterName.
     */
    private static boolean isCreatingExceptionObject(DetailAST aExpressionAst,
            String aExceptionParameterName)
    {
        boolean result = false;
        final DetailAST literalNewAst =
                aExpressionAst.findFirstToken(TokenTypes.LITERAL_NEW);
        if (literalNewAst != null) {
            final DetailAST parametersAst = literalNewAst
                    .findFirstToken(TokenTypes.ELIST);
            if (parametersAst != null) {
                result = containsExceptionParameter(parametersAst,
                        aExceptionParameterName);
            }
        }
        return result;
    }

    /**
     * Verify that given expression is a logging expression.
     * @param aExpressionAst
     *        DetailAST of expression.
     * @param aLoggerVariableNames
     *        names list of locally created logger variables.
     * @return true if given expression is a logging expression.
     */
    private boolean isLoggingExpression(DetailAST aExpressionAst,
            List<String> aLoggerVariableNames)
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
            result = (aLoggerVariableNames.contains(loggerObjectIdentifier)
                    || mLoggerFieldNames.contains(loggerObjectIdentifier))
                    && mLoggingMethodNames.contains(invokedMethodIdentifier);
        }
        return result;
    }

    /**
     * Verify that given expression takes exception as a parameter.
     * @param aExpressionAst
     *        DetailAST of expression.
     * @param aExceptionParameterName
     *        name of exception parameter.
     * @return true if given logging expression takes exception as a parameter.
     */
    private static boolean hasExceptionAsParameter(
            DetailAST aExpressionAst, String aExceptionParameterName)
    {
        final DetailAST methodCallAst = aExpressionAst.getFirstChild();
        final DetailAST loggerParametersAst =
                methodCallAst.findFirstToken(TokenTypes.ELIST);
        return containsExceptionParameter(loggerParametersAst,
                aExceptionParameterName);
    }

    /**
     * Verify that aExceptionParameterName is in aParametersAst.
     * @param aParametersAst
     *        DetailAST of expression list(ELIST).
     * @param aExceptionParameterName
     *        name of exception.
     * @return true if aExceptionParameterName is in aParametersAst.
     */
    private static boolean containsExceptionParameter(
            DetailAST aParametersAst, String aExceptionParameterName)
    {
        boolean result = false;
        DetailAST parameterAst = aParametersAst.getFirstChild();
        while (parameterAst != null) {
            if (isExceptionParameter(parameterAst, aExceptionParameterName)) {
                result = true;
                break;
            }
            parameterAst = parameterAst.getNextSibling();
        }
        return result;
    }

    /**
     * Verify that given aParameterAst is exception object or method which is
     * invoked from exception object. And exception object's name is
     * aExceptionParameterName.
     * @param aParameterExpressionAst
     *        DetailAST of parameter expression(EXPR).
     * @param aExceptionParameterName
     *        exception parameter name.
     * @return true if given parameter is exception.
     */
    private static boolean isExceptionParameter(
            DetailAST aParameterExpressionAst, String aExceptionParameterName)
    {
        boolean result = false;
        // parameter is exception
        if (aExceptionParameterName.equals(
                getIdentifier(aParameterExpressionAst)))
        {
            result = true;
        }
        // parameter is an invoking exception's method
        else {
            final DetailAST methodCallAst =
                    aParameterExpressionAst.getFirstChild();
            if (methodCallAst != null
                    && methodCallAst.getType() == TokenTypes.METHOD_CALL)
            {
                final String methodCallIdent =
                        FullIdent.createFullIdentBelow(methodCallAst).getText();
                final int firstDotIndex = methodCallIdent.indexOf('.');
                final String usedObjectName =
                        methodCallIdent.substring(0, firstDotIndex);
                if (usedObjectName.equals(aExceptionParameterName)) {
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * Verify that exception aExceptionVariableName is throwed or created
     * locally exception(based on aExceptionVariableName) is throwed.
     * @param aThrowStatementAst
     *            DetailAST of literal throw.
     * @param aExceptionVariableName
     *            name of exception parameter.
     * @param aLocalExceptionVariableNames
     *            names list of created locally exceptions(based on
     *            aExceptionVariableName).
     * @return true if exception aExceptionVariableName is throwed or created
     *         locally exception(based on aExceptionVariableName) is throwed.
     */
    private static boolean throwsException(
            DetailAST aThrowStatementAst, String aExceptionVariableName,
            List<String> aLocalExceptionVariableNames)
    {
        boolean result = false;
        final DetailAST thrownExceptionAst = aThrowStatementAst.getFirstChild();
        if (aExceptionVariableName.equals(getIdentifier(thrownExceptionAst))
            || aLocalExceptionVariableNames.contains(
                    getIdentifier(thrownExceptionAst))
            || isCreatingExceptionObject(thrownExceptionAst,
                aExceptionVariableName))
        {
            result = true;
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

}
