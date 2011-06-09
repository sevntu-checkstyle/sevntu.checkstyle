package com.puppycrawl.tools.checkstyle.checks.coding;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FastStack;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * It is a bad practice to use <code>Boolean</code> type for ternary logic.
 * It is intended to be used for binary logic.
 * </p>
 * @author Ivan Sopov
 */
public class ReturnNullInsteadOfBoolean extends Check
{

    /** Stack of states of the need in exploring the methods. */
    private final FastStack<Boolean> mMethodStack = FastStack.newInstance();
    /** Should we explore current method or not. */
    private Boolean mExploreMethod;

    @Override
    public int[] getDefaultTokens()
    {
        return new int[]{
            TokenTypes.METHOD_DEF,
            TokenTypes.LITERAL_RETURN,
        };
    }

    @Override
    public int[] getRequiredTokens()
    {
        return new int[]{
            TokenTypes.METHOD_DEF,
        };
    }

    @Override
    public void beginTree(DetailAST aRootAST)
    {
        mExploreMethod = null;
        mMethodStack.clear();
    }

    @Override
    public void visitToken(DetailAST aAST)
    {
        switch (aAST.getType()) {
        case TokenTypes.METHOD_DEF:
            mMethodStack.push(mExploreMethod);
            final DetailAST returnTypeAST =
                aAST.findFirstToken(TokenTypes.TYPE).getFirstChild();
            mExploreMethod = "Boolean".equals(returnTypeAST.getText());
            break;
        case TokenTypes.LITERAL_RETURN:
            if (mExploreMethod) {
                final DetailAST exprToken =
                    aAST.findFirstToken(TokenTypes.EXPR).getFirstChild();
                if ("null".equals(exprToken.getText())) {
                    log(aAST, "return.null.Boolean", (Object[]) null);
                }
            }
            break;
        default:
            throw new IllegalStateException(aAST.toString());
        }
    }

    @Override
    public void leaveToken(DetailAST aAST)
    {
        switch (aAST.getType()) {
        case TokenTypes.METHOD_DEF:
            mExploreMethod = mMethodStack.pop();
            break;
        case TokenTypes.LITERAL_RETURN:
            // Do nothing
            break;
        default:
            throw new IllegalStateException(aAST.toString());
        }
    }
}
