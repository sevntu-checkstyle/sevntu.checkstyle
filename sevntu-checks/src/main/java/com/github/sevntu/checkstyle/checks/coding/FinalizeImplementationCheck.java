////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2012 Oliver Burn
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
////////////////////////////////////////////////////////////////////////////////

package com.github.sevntu.checkstyle.checks.coding;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;


/**
 * <p>
 * This Check detects 3 most common cases of incorrect finalize() method implementation:
 * </p>
 * <p>
 * 1. Negates effect of superclass finalize:
 * </p><br/>
 * <code><pre>
 * protected void finalize() { }
 * protected void finalize() { doSomething(); }
 * </pre></code>
 * <p>
 * 2. Useless (or worse) finalize:
 * </p><br/>
 * <code><pre>
 * protected void finalize() { super.finalize(); }
 * </pre></code>
 * <p>
 * 3. Public finalize:
 * </p><br/>
 * <code><pre>
 * public void finalize(){ 
 *     try {doSomething();} 
 *     finally {super.finalize()}
 * }</pre></code>
 * 
 * @author <a href="mailto:maxvetrenko2241@gmail.com">Max Vetrenko</a>
 *
 */
public class FinalizeImplementationCheck extends Check
{
	
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
    public int[] getDefaultTokens()
    {
        return new int[] { TokenTypes.METHOD_DEF };
    }

    @Override
    public void visitToken(DetailAST aMethodDefToken)
    {
        if (isFinalizeMethodSignature(aMethodDefToken)) {
        	
        	String warningMessage = validateFinalizeMethod(aMethodDefToken);
        	
        	if (warningMessage != null) {
        		log(aMethodDefToken.getLineNo(), warningMessage);
        	}
        }
    }

    /**
     * Checks, if finalize implementation is correct. If implementation is bad, 
     * this method will call log() with suitable warning message. 
     * @param aFinalizeMethodToken 
     *        current finalize() token
     * @return warning message or null, if all is well. 
     */
    private static String validateFinalizeMethod(DetailAST aFinalizeMethodToken)
    {
    	String warningMessage = null;
        if (hasModifier(TokenTypes.LITERAL_PROTECTED, aFinalizeMethodToken)) {
                DetailAST methodOpeningBrace = aFinalizeMethodToken.getLastChild();
                DetailAST literalTry = methodOpeningBrace.findFirstToken(TokenTypes.LITERAL_TRY);
                
                if (literalTry == null) {
                    if (containsSuperFinalizeCall(methodOpeningBrace)) {
                        warningMessage = MSG_KEY_USELESS_FINALIZE;
                    } else {
                        warningMessage = MSG_KEY_MISSED_TRY_FINALLY;
                    }
                    
                } else {
                    DetailAST literalFinally = literalTry.findFirstToken(TokenTypes.LITERAL_FINALLY);
                    
                    if (literalFinally != null &&
                            !containsSuperFinalizeCall(literalFinally.getLastChild())) {
                        warningMessage = MSG_KEY_MISSED_SUPER_FINALIZE_CALL;
                    }
                }
            
        } else {
            warningMessage = MSG_KEY_PUBLIC_FINALIZE;
        }
        return warningMessage;
    }

    /**
     * Checks, if current method is finalize().
     * @param aMethodDefToken
     *        current method definition.
     * @return true, if method is finalize() method.
     */
    public static boolean isFinalizeMethodSignature(DetailAST aMethodDefToken)
    {
        return !hasModifier(TokenTypes.LITERAL_STATIC ,aMethodDefToken)
                && isFinalizeMethodName(aMethodDefToken) && isVoid(aMethodDefToken)
                && getParamsCount(aMethodDefToken) == 0;
    }
    
    /**
     * Checks, if finalize() has "static" access modifier.
     * @param aModifireType 
     *        modifier type.
     * @param aMethodNToken
     *        MODIFIRES Token.
     * @return true, if finalize() has "protected" access modifier.
     */
    public static boolean hasModifier(int aModifireType, DetailAST aMethodToken)
    {
        DetailAST modifiersToken = aMethodToken.getFirstChild();
        return modifiersToken.findFirstToken(aModifireType) != null;
    }

    /**
     * Checks, if current method name is "finalize".
     * @param aMethodDefToken
     *        method definition Token.
     * @return true, if current method name is "finalize".
     */
    private static boolean isFinalizeMethodName(DetailAST aMethodDefToken)
    {
        DetailAST identToken = aMethodDefToken.findFirstToken(TokenTypes.IDENT);
        return FINALIZE_METHOD_NAME.equals(identToken.getText());
    }

    
    /**
     * Checks, if method is void.
     * @param aMethodDefToken
     *        method definition Token.
     * @return true, if method is void.
     */
    private static boolean isVoid(DetailAST aMethodDefToken)
    {
        DetailAST typeToken = aMethodDefToken.findFirstToken(TokenTypes.TYPE);
        return typeToken.findFirstToken(TokenTypes.LITERAL_VOID) != null;
    }
    
    /**
     * Counts number of parameters.
     * @param aMethodDefToken
     *        method definition Token.
     * @return number of parameters.
     */
    private static int getParamsCount(DetailAST aMethodDefToken)
    {
        return aMethodDefToken.findFirstToken(TokenTypes.PARAMETERS).getChildCount();
    }

    /**
     * Checks, if finalize() is empty.
     * @param aMethodOpeningBraceToken
     *        method opening brace.
     * @return true, if finalize() is empty.
     */
    public static boolean isMethodEmpty(DetailAST aMethodOpeningBraceToken)
    {
        return aMethodOpeningBraceToken.getFirstChild().getType() == TokenTypes.RCURLY;
    }

    /**
     * Checks, if current method has super.finalize() call.
     * @param aOpeningBrace
     *        current method definition.
     * @return true, if method has super.finalize() call.
     */
    public static boolean containsSuperFinalizeCall(DetailAST aOpeningBrace)
    {
        DetailAST methodCallToken = aOpeningBrace.getFirstChild().getFirstChild();
        if (methodCallToken != null) {
            DetailAST dotToken = methodCallToken.getFirstChild();
            if (dotToken.findFirstToken(TokenTypes.LITERAL_SUPER) != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks, if current method has try-finally block.
     * @param aMethodOpeningBrace
     *        Method opening brace.
     * @return
     */
    public static boolean hasTryFinallyBlock(DetailAST aMethodOpeningBrace)
    {
        DetailAST tryToken = aMethodOpeningBrace.findFirstToken(TokenTypes.LITERAL_TRY);  
        return tryToken != null && tryToken.getLastChild().getType() == TokenTypes.LITERAL_FINALLY;
    }
}
