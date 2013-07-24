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
    private Deque<DetailAST> mObjBlockTokensStack =
            new LinkedList<DetailAST>();

    @Override
    public final int[] getDefaultTokens()
    {
        return new int[] { TokenTypes.CLASS_DEF, TokenTypes.METHOD_DEF };
    }

    @Override
    public final void visitToken(final DetailAST aAST)
    {
        if (aAST.getType() == TokenTypes.CLASS_DEF) {
            if (isNotInnerClass(aAST)) {
                // remove all tokens from stack
                mObjBlockTokensStack.clear();
            }
            if (hasAbstractModifier(aAST)) {
                mObjBlockTokensStack.push(
                        aAST.findFirstToken(TokenTypes.OBJBLOCK));
            }
        }
        // type of token is METHOD_DEF
        else if (isChildOfCurrentObjBlockToken(aAST) && isMainMethod(aAST)) {
            log(aAST.getLineNo(), MSG_KEY);
            // remove current objblock
            mObjBlockTokensStack.pop();
        }
    }

    /**
     * Verify that class is not inner.
     * @param aClassDefAST
     *        DetailAST of class definition.
     * @return true if class is not inner, false otherwise.
     */
    private boolean isNotInnerClass(final DetailAST aClassDefAST)
    {
        boolean result = true;
        final DetailAST objBlockAST = aClassDefAST.getParent();
        for (DetailAST currentObjBlock : mObjBlockTokensStack) {
            if (objBlockAST == currentObjBlock) {
                result = false;
                break;
            }
        }
        return result;
    }

    /**
     * Verify that aMethodDefAST is child token of considered objblock.
     * @param aMethodDefAST DetailAST of method definition.
     * @return true if aMethodDefAST is child of of considered objblock.
     */
    private boolean isChildOfCurrentObjBlockToken(final DetailAST aMethodDefAST)
    {
        final DetailAST objBlockAST = mObjBlockTokensStack.peek();
        return objBlockAST != null
                && aMethodDefAST.getParent() == objBlockAST;
    }

    /**
     * Return true if AST has abstract modifier.
     * @param aClassDefAST
     *        AST which has modifier
     * @return true if AST has abstract modifier, false otherwise.
     */
    private static boolean hasAbstractModifier(final DetailAST aClassDefAST)
    {
        boolean result = false;
        if (hasChildToken(aClassDefAST, TokenTypes.MODIFIERS)) {
            final DetailAST modifiers =
                    aClassDefAST.findFirstToken(TokenTypes.MODIFIERS);
            result = hasChildToken(modifiers, TokenTypes.ABSTRACT);
        }
        return result;
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
}
