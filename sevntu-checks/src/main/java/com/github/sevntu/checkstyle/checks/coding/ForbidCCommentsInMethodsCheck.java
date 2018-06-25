////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2018 the original author or authors.
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

import java.util.ArrayDeque;
import java.util.Deque;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * This check forbid to use C style comments into the method body. If you use
 * class declaration into the method body you will get an error.
 * </p>
 * @author <a href="mailto:IliaDubinin91@gmail.com">Ilia Dubinin</a>
 * @since 1.6.0
 */
public class ForbidCCommentsInMethodsCheck extends AbstractCheck {

    /**
     * Warning message key.
     */
    public static final String MSG_KEY = "forbid.c.comments.in.the.method.body";

    /** AST to use for null checks because {@link Deque} doesn't allow null elements. */
    private static final DetailAST NULL_AST = new DetailAST();

    /** Method stack. */
    private final Deque<DetailAST> scopeStack = new ArrayDeque<>();

    /** Reference to current method. */
    private DetailAST methodAst;

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.METHOD_DEF,
            TokenTypes.OBJBLOCK,
            TokenTypes.BLOCK_COMMENT_BEGIN,
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
    public boolean isCommentNodesRequired() {
        return true;
    }

    @Override
    public void beginTree(DetailAST rootAST) {
        methodAst = NULL_AST;
    }

    @Override
    public void visitToken(DetailAST ast) {
        scopeStack.push(methodAst);

        switch (ast.getType()) {
            case TokenTypes.METHOD_DEF:
                methodAst = ast;
                break;
            case TokenTypes.OBJBLOCK:
                methodAst = NULL_AST;
                break;
            default:
                if (methodAst != NULL_AST) {
                    final DetailAST lcurly = methodAst.findFirstToken(TokenTypes.SLIST);
                    if (lcurly != null
                            && (ast.getLineNo() > lcurly.getLineNo()
                                    || ast.getLineNo() == lcurly.getLineNo()
                                        && ast.getColumnNo() > lcurly.getColumnNo())) {
                        log(ast, MSG_KEY);
                    }
                }
                break;
        }
    }

    @Override
    public void leaveToken(DetailAST ast) {
        methodAst = scopeStack.pop();
    }

}
