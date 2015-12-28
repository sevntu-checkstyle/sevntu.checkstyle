package com.github.sevntu.checkstyle.checks.coding;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * It is a bad practice to return boolean values from ternary operations. Just use the value inside branch instead.
 * </p>
 * 
 * @author Ivan Sopov
 */
public class ReturnBooleanFromTernaryCheck extends Check {

    /**
     * Warning message key.
     */
    public final static String MSG_KEY = "return.boolean.ternary";

    @Override
    public int[] getDefaultTokens() {
        return new int[] { TokenTypes.QUESTION };
    }

    @Override
    public void visitToken(DetailAST ast) {
        DetailAST secondBranch = ast.getLastChild();
        DetailAST firstBranch = secondBranch.getPreviousSibling().getPreviousSibling();
        if ("true".equals(secondBranch.getText()) || "false".equals(secondBranch.getText())
                || "true".equals(firstBranch.getText()) || "false".equals(firstBranch.getText())) {
            log(ast, "return.boolean.ternary");
        }
    }
}
