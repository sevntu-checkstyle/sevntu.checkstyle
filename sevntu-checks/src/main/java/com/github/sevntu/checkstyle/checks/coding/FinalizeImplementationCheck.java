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

/**
 * Validates finalize method implementation.
 * <p>
 * This Check detects 3 most common cases of incorrect finalize() method implementation:
 * </p>
 * <p>
 * 1. Negates effect of superclass finalize:
 * </p>
 * <pre>
 * protected void finalize() { }
 * protected void finalize() { doSomething(); }
 * </pre>
 * <p>
 * 2. Useless (or worse) finalize:
 * </p>
 * <pre>
 * protected void finalize() { super.finalize(); }
 * </pre>
 * <p>
 * 3. Public finalize:
 * </p>
 * <pre>
 * public void finalize(){
 *     try {doSomething();}
 *     finally {super.finalize()}
 * }</pre>
 *
 * @author <a href="mailto:maxvetrenko2241@gmail.com">Max Vetrenko</a>
 * @since 1.11.0
 */
public class FinalizeImplementationCheck extends AbstractCheck {

    /**
     * The key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY_MISSED_TRY_FINALLY =
            "finalize.implementation.missed.try.finally";

    /**
     * The key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY_PUBLIC_FINALIZE = "finalize.implementation.public";

    /**
     * The key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY_USELESS_FINALIZE = "finalize.implementation.useless";

    /**
     * The key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY_MISSED_SUPER_FINALIZE_CALL =
            "finalize.implementation.missed.super.finalize";

    /**
     * The name of finalize() method.
     */
    private static final String FINALIZE_METHOD_NAME = "finalize";

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
    public void visitToken(DetailAST methodDefToken) {
        if (isFinalizeMethodSignature(methodDefToken)) {
            final String warningMessage = validateFinalizeMethod(methodDefToken);

            if (warningMessage != null) {
                log(methodDefToken, warningMessage);
            }
        }
    }

    /**
     * Checks, if finalize implementation is correct. If implementation is bad,
     * this method will call log() with suitable warning message.
     *
     * @param finalizeMethodToken
     *        current finalize() token
     * @return warning message or null, if all is well.
     */
    private static String validateFinalizeMethod(DetailAST finalizeMethodToken) {
        String warningMessage = null;
        if (hasModifier(TokenTypes.LITERAL_PROTECTED, finalizeMethodToken)) {
            final DetailAST methodOpeningBrace = finalizeMethodToken.getLastChild();
            final DetailAST literalTry = methodOpeningBrace.findFirstToken(TokenTypes.LITERAL_TRY);

            if (literalTry == null) {
                if (containsSuperFinalizeCall(methodOpeningBrace)) {
                    warningMessage = MSG_KEY_USELESS_FINALIZE;
                }
                else {
                    warningMessage = MSG_KEY_MISSED_TRY_FINALLY;
                }
            }
            else {
                final DetailAST literalFinally = literalTry
                        .findFirstToken(TokenTypes.LITERAL_FINALLY);

                if (literalFinally != null
                        && !containsSuperFinalizeCall(literalFinally.getLastChild())) {
                    warningMessage = MSG_KEY_MISSED_SUPER_FINALIZE_CALL;
                }
            }
        }
        else {
            warningMessage = MSG_KEY_PUBLIC_FINALIZE;
        }
        return warningMessage;
    }

    /**
     * Checks, if current method is finalize().
     *
     * @param methodDefToken
     *        current method definition.
     * @return true, if method is finalize() method.
     */
    private static boolean isFinalizeMethodSignature(DetailAST methodDefToken) {
        return !hasModifier(TokenTypes.LITERAL_STATIC, methodDefToken)
                && isFinalizeMethodName(methodDefToken) && isVoid(methodDefToken)
                && getParamsCount(methodDefToken) == 0;
    }

    /**
     * Checks, if finalize() has "static" access modifier.
     *
     * @param modifierType
     *        modifier type.
     * @param methodToken
     *        MODIFIERS Token.
     * @return true, if finalize() has "protected" access modifier.
     */
    private static boolean hasModifier(int modifierType, DetailAST methodToken) {
        final DetailAST modifiersToken = methodToken.getFirstChild();
        return modifiersToken.findFirstToken(modifierType) != null;
    }

    /**
     * Checks, if current method name is "finalize".
     *
     * @param methodDefToken
     *        method definition Token.
     * @return true, if current method name is "finalize".
     */
    private static boolean isFinalizeMethodName(DetailAST methodDefToken) {
        final DetailAST identToken = methodDefToken.findFirstToken(TokenTypes.IDENT);
        return FINALIZE_METHOD_NAME.equals(identToken.getText());
    }

    /**
     * Checks, if method is void.
     *
     * @param methodDefToken
     *        method definition Token.
     * @return true, if method is void.
     */
    private static boolean isVoid(DetailAST methodDefToken) {
        final DetailAST typeToken = methodDefToken.findFirstToken(TokenTypes.TYPE);
        return typeToken.findFirstToken(TokenTypes.LITERAL_VOID) != null;
    }

    /**
     * Counts number of parameters.
     *
     * @param methodDefToken
     *        method definition Token.
     * @return number of parameters.
     */
    private static int getParamsCount(DetailAST methodDefToken) {
        return methodDefToken.findFirstToken(TokenTypes.PARAMETERS).getChildCount();
    }

    /**
     * Checks, if current method has super.finalize() call.
     *
     * @param openingBrace
     *        current method definition.
     * @return true, if method has super.finalize() call.
     */
    private static boolean containsSuperFinalizeCall(DetailAST openingBrace) {
        boolean result = false;
        final DetailAST methodCallToken = openingBrace.getFirstChild().getFirstChild();
        if (methodCallToken != null && methodCallToken.getType() == TokenTypes.METHOD_CALL) {
            final DetailAST dotToken = methodCallToken.getFirstChild();
            if (dotToken.getType() == TokenTypes.DOT
                    && dotToken.findFirstToken(TokenTypes.LITERAL_SUPER) != null) {
                result = true;
            }
        }
        return result;
    }

}
