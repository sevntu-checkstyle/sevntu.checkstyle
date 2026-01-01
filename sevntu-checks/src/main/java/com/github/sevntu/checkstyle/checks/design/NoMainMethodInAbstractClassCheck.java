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

import java.util.Deque;
import java.util.LinkedList;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Forbids main methods in abstract classes. Existence of 'main' method can
 * mislead a developer to consider this class as a ready-to-use implementation.
 *
 * @author Baratali Izmailov <a href="mailto:barataliba@gmail.com">email</a>
 * @since 1.9.0
 */
public class NoMainMethodInAbstractClassCheck extends AbstractCheck {

    /**
     * Key for error message.
     */
    public static final String MSG_KEY = "avoid.main.method.in.abstract.class";

    /** String representation of string class. */
    private static final String STRING_CLASS = "String";

    /**
     * Keep OBJBLOCKs of classes that are under validation.
     */
    private final Deque<DetailAST> objBlockTokensStack =
            new LinkedList<>();

    @Override
    public final int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.CLASS_DEF,
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
    public final void visitToken(final DetailAST ast) {
        if (ast.getType() == TokenTypes.CLASS_DEF) {
            if (isNotInnerClass(ast)) {
                // remove all tokens from stack
                objBlockTokensStack.clear();
            }
            if (hasAbstractModifier(ast)) {
                objBlockTokensStack.push(
                        ast.findFirstToken(TokenTypes.OBJBLOCK));
            }
        }
        // type of token is METHOD_DEF
        else if (isChildOfCurrentObjBlockToken(ast) && isMainMethod(ast)) {
            log(ast, MSG_KEY);
            // remove current objblock
            objBlockTokensStack.pop();
        }
    }

    /**
     * Verify that class is not inner.
     *
     * @param classDefAST
     *        DetailAST of class definition.
     * @return true if class is not inner, false otherwise.
     */
    private boolean isNotInnerClass(final DetailAST classDefAST) {
        boolean result = true;
        final DetailAST objBlockAST = classDefAST.getParent();
        for (DetailAST currentObjBlock : objBlockTokensStack) {
            if (objBlockAST == currentObjBlock) {
                result = false;
                break;
            }
        }
        return result;
    }

    /**
     * Verify that aMethodDefAST is child token of considered objblock.
     *
     * @param methodDefAST DetailAST of method definition.
     * @return true if aMethodDefAST is child of of considered objblock.
     */
    private boolean isChildOfCurrentObjBlockToken(final DetailAST methodDefAST) {
        final DetailAST objBlockAST = objBlockTokensStack.peek();
        return objBlockAST != null
                && methodDefAST.getParent() == objBlockAST;
    }

    /**
     * Return true if AST has abstract modifier.
     *
     * @param classDefAST
     *        AST which has modifier
     * @return true if AST has abstract modifier, false otherwise.
     */
    private static boolean hasAbstractModifier(final DetailAST classDefAST) {
        final DetailAST modifiers =
                classDefAST.findFirstToken(TokenTypes.MODIFIERS);
        return hasChildToken(modifiers, TokenTypes.ABSTRACT);
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
     * Get identifier of AST. These can be names of types, subpackages,
     * fields, methods, parameters, and local variables.
     *
     * @param ast
     *        DetailAST instance
     * @return identifier of AST, null if AST does not have name.
     */
    private static String getIdentifier(final DetailAST ast) {
        final DetailAST ident = ast.findFirstToken(TokenTypes.IDENT);
        return ident.getText();
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
        final DetailAST modifiers =
                methodAST.findFirstToken(TokenTypes.MODIFIERS);
        return hasChildToken(modifiers, TokenTypes.LITERAL_PUBLIC)
                && hasChildToken(modifiers, TokenTypes.LITERAL_STATIC);
    }

    /**
     * Verifies that given AST has type and this type is void.
     *
     * @param methodAST
     *        DetailAST instance.
     * @return true if AST's type void, false otherwise.
     */
    private static boolean isVoidType(final DetailAST methodAST) {
        final DetailAST methodTypeAST = methodAST.findFirstToken(TokenTypes.TYPE);
        return hasChildToken(methodTypeAST, TokenTypes.LITERAL_VOID);
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
                result = STRING_CLASS.equals(parameterName);
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
                result = STRING_CLASS.equals(parameterName);
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

}
