////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2015 the original author or authors.
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

package com.github.sevntu.checkstyle.checks.design;

import java.util.Deque;
import java.util.LinkedList;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Forbids main methods in abstract classes. Existence of 'main' method can
 * mislead a developer to consider this class as a ready-to-use implementation.
 * @author Baratali Izmailov <a href="mailto:barataliba@gmail.com">email</a>
 */
public class NoMainMethodInAbstractClassCheck extends Check
{
    /**
     * Key for error message.
     */
    public static final String MSG_KEY = "avoid.main.method.in.abstract.class";
    /**
     * Keep OBJBLOCKs of classes that are under validation.
     */
    private Deque<DetailAST> objBlockTokensStack =
            new LinkedList<DetailAST>();

    @Override
    public final int[] getDefaultTokens()
    {
        return new int[] { TokenTypes.CLASS_DEF, TokenTypes.METHOD_DEF };
    }

    @Override
    public final void visitToken(final DetailAST ast)
    {
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
            log(ast.getLineNo(), MSG_KEY);
            // remove current objblock
            objBlockTokensStack.pop();
        }
    }

    /**
     * Verify that class is not inner.
     * @param classDefAST
     *        DetailAST of class definition.
     * @return true if class is not inner, false otherwise.
     */
    private boolean isNotInnerClass(final DetailAST classDefAST)
    {
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
     * @param methodDefAST DetailAST of method definition.
     * @return true if aMethodDefAST is child of of considered objblock.
     */
    private boolean isChildOfCurrentObjBlockToken(final DetailAST methodDefAST)
    {
        final DetailAST objBlockAST = objBlockTokensStack.peek();
        return objBlockAST != null
                && methodDefAST.getParent() == objBlockAST;
    }

    /**
     * Return true if AST has abstract modifier.
     * @param classDefAST
     *        AST which has modifier
     * @return true if AST has abstract modifier, false otherwise.
     */
    private static boolean hasAbstractModifier(final DetailAST classDefAST)
    {
        boolean result = false;
        if (hasChildToken(classDefAST, TokenTypes.MODIFIERS)) {
            final DetailAST modifiers =
                    classDefAST.findFirstToken(TokenTypes.MODIFIERS);
            result = hasChildToken(modifiers, TokenTypes.ABSTRACT);
        }
        return result;
    }

    /**
     * Verifies that the given DetailAST is a main method.
     * @param methodAST
     *        DetailAST instance.
     * @return true if aMethodAST is a main method, false otherwise.
     */
    private static boolean isMainMethod(final DetailAST methodAST)
    {
        boolean result = true;
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
     * @param ast
     *        DetailAST instance
     * @return identifier of AST, null if AST does not have name.
     */
    private static String getIdentifier(final DetailAST ast)
    {
        final DetailAST ident = ast.findFirstToken(TokenTypes.IDENT);
        if (ident != null) {
            return ident.getText();
        }
        return null;
    }

    /**
     * Verifies that given AST has appropriate modifiers for main method.
     * @param methodAST
     *        DetailAST instance.
     * @return true if aMethodAST has (public & static & !abstract) modifiers,
     *         false otherwise.
     */
    private static boolean isMainMethodModifiers(final DetailAST methodAST)
    {
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
     * @param methodAST
     *        DetailAST instance.
     * @return true if AST's type void, false otherwise.
     */
    private static boolean isVoidType(final DetailAST methodAST)
    {
        boolean result = true;
        DetailAST methodTypeAST = null;
        if (hasChildToken(methodAST, TokenTypes.TYPE)) {
            methodTypeAST = methodAST.findFirstToken(TokenTypes.TYPE);
            result = hasChildToken(methodTypeAST, TokenTypes.LITERAL_VOID);
        }
        return result;
    }

    /**
     * Verifies that given AST has appropriate for main method parameters.
     * @param methodAST
     *        instance of a method
     * @return true if parameters of aMethodAST are appropriate for main method,
     *         false otherwise.
     */
    private static boolean isMainMethodParameters(final DetailAST methodAST)
    {
        final DetailAST params =
                methodAST.findFirstToken(TokenTypes.PARAMETERS);
        return hasOnlyStringArrayParameter(params)
                || hasOnlyStringEllipsisParameter(params);
    }

    /**
     * Return true if AST of method parameters has String[] parameter child
     * token.
     * @param parametersAST
     *        DetailAST of method parameters.
     * @return true if AST has String[] parameter child token, false otherwise.
     */
    private static boolean
    hasOnlyStringArrayParameter(final DetailAST parametersAST)
    {
        boolean result = true;
        if (parametersAST.getChildCount(TokenTypes.PARAMETER_DEF) != 1) {
            result = false;
        }
        else { // there is one parameter
            final DetailAST parameterDefinitionAST =
                    parametersAST.findFirstToken(TokenTypes.PARAMETER_DEF);
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
     * @param parametersAST
     *        DetailAST of method parameters.
     * @return true if aParametersAST has String... parameter child token, false
     *         otherwise.
     */
    private static boolean
    hasOnlyStringEllipsisParameter(final DetailAST parametersAST)
    {
        boolean result = true;
        if (parametersAST.getChildCount(TokenTypes.PARAMETER_DEF) != 1) {
            result = false;
        }
        // there is one parameter
        else {
            final DetailAST parameterDefinitionAST =
                    parametersAST.findFirstToken(TokenTypes.PARAMETER_DEF);
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
     * @param ast
     *        DetailAST instance.
     * @param tokenType
     *        one of TokenTypes
     * @return true if aAST has token of given type, or false otherwise.
     */
    private static boolean hasChildToken(DetailAST ast, int tokenType)
    {
        return ast.findFirstToken(tokenType) != null;
    }
}
