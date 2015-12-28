package com.github.sevntu.checkstyle.checks.coding;

import java.util.ArrayDeque;
import java.util.Deque;

import com.github.sevntu.checkstyle.Utils;
import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * It is a bad practice to use <code>Boolean</code> type for ternary logic. It
 * is intended to be used for binary logic.
 * </p>
 * 
 * @author Ivan Sopov
 */
public class ReturnNullInsteadOfBooleanCheck extends Check {

    /**
     * Warning message key
     */
    public final static String MSG_KEY = "return.null.Boolean";

    /** Stack of states of the need in exploring the methods. */
    private final Deque<Boolean> methodStack = new ArrayDeque<Boolean>();
    /** Should we explore current method or not. */
    private boolean exploreMethod = false;

    @Override
    public int[] getDefaultTokens() {
        return new int[] { TokenTypes.METHOD_DEF, TokenTypes.LITERAL_RETURN, };
    }

    @Override
    public int[] getRequiredTokens() {
        return new int[] { TokenTypes.METHOD_DEF, };
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
                Utils.reportInvalidToken(ast.getType());
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
                Utils.reportInvalidToken(ast.getType());
                break;
        }
    }
}
