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

import java.util.ArrayDeque;
import java.util.Deque;

import com.github.sevntu.checkstyle.SevntuUtil;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * It is a bad practice to use <code>Boolean</code> type for ternary logic. It
 * is intended to be used for binary logic.
 * </p>
 *
 * @author Ivan Sopov
 * @since 1.8.0
 */
public class ReturnNullInsteadOfBooleanCheck extends AbstractCheck {

    /**
     * Warning message key.
     */
    public static final String MSG_KEY = "return.null.Boolean";

    /** Stack of states of the need in exploring the methods. */
    private final Deque<Boolean> methodStack = new ArrayDeque<>();
    /** Should we explore current method or not. */
    private boolean exploreMethod;

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.METHOD_DEF,
            TokenTypes.LITERAL_RETURN,
        };
    }

    @Override
    public int[] getAcceptableTokens() {
        return getDefaultTokens();
    }

    @Override
    public int[] getRequiredTokens() {
        return new int[] {
            TokenTypes.METHOD_DEF,
        };
    }

    @Override
    public void beginTree(DetailAST rootAST) {
        methodStack.clear();
    }

    @Override
    public void visitToken(DetailAST ast) {
        switch (ast.getType()) {
            case TokenTypes.METHOD_DEF:
                methodStack.push(exploreMethod);
                final DetailAST returnTypeAST = ast
                    .findFirstToken(TokenTypes.TYPE).getFirstChild();
                exploreMethod = "Boolean".equals(returnTypeAST.getText());
                break;
            case TokenTypes.LITERAL_RETURN:
                if (exploreMethod) {
                    final DetailAST exprToken = ast
                        .findFirstToken(TokenTypes.EXPR).getFirstChild();
                    if ("null".equals(exprToken.getText())) {
                        log(ast, MSG_KEY);
                    }
                }
                break;
            default:
                SevntuUtil.reportInvalidToken(ast.getType());
                break;
        }
    }

    @Override
    public void leaveToken(DetailAST ast) {
        switch (ast.getType()) {
            case TokenTypes.METHOD_DEF:
                exploreMethod = methodStack.pop();
                break;
            case TokenTypes.LITERAL_RETURN:
                // Do nothing
                break;
            default:
                SevntuUtil.reportInvalidToken(ast.getType());
                break;
        }
    }

}
