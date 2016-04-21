package com.github.sevntu.checkstyle.checks.coding;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * This checks enforces whitespace before array initializer
 * </p>
 * Examples:
 * This code is perfectly valid:
 *
 * <pre>
 * int[] ints = new int[] {
 *     0, 1, 2, 3
 * };
 * </pre>
 *
 * This example is valid too:
 *
 * <pre>
 * int[] tab = new int[]
 *                 {0, 1, 2, 3}
 * </pre>
 *
 * But this violates check:
 *
 * <pre>
 * int[] ints = new int[]{0, 1, 2, 3};
 * </pre>
 *
 *
 * @author <a href="mailto:piotr.listkiewicz@gmail.com">liscju</a>
 */
public class WhitespaceBeforeArrayInitializerCheck extends Check {

    /**
     * Violation message key.
     */
    public static final String MSG_KEY = "whitespace.before.array.initializer";

    @Override
    public int[] getDefaultTokens() {
        return new int[] {TokenTypes.ARRAY_INIT};
    }

    @Override
    public void visitToken(DetailAST ast) {
        DetailAST previousAst = getPreviousAst(ast);
        if (!areTokensSeparatedByWhitespace(previousAst, ast) && isNestedArrayInitializer(ast)) {
            log(ast.getLineNo(), ast.getColumnNo(), MSG_KEY);
        }
    }

    /**
     * Checks if firstAst and secondAst are separated by whitespace
     * @param firstAST DetailAST
     * @param secondAST DetailAST
     * @return true if firstAST and secondAST are separated by whitespace,false otherwise
     */
    private static boolean areTokensSeparatedByWhitespace(DetailAST firstAST, DetailAST secondAST) {
        boolean isDistanceValid = true;
        int columnDistance = secondAST.getColumnNo() - firstAST.getColumnNo();
        if (columnDistance == 1) {
            int lineDistance = secondAST.getLineNo() - firstAST.getLineNo();
            if (lineDistance == 0) {
                isDistanceValid = false;
            }
        }
        return isDistanceValid;
    }

    /**
     * Checks whether inspected array initializer is nested in other array initializer.
     * @param ast {@link TokenTypes#ARRAY_INIT} token to inspect
     * @return true when this array initializer is nested in other initializer; false otherwise
     */
    private static boolean isNestedArrayInitializer(DetailAST ast) {
        return ast.getParent().getType() != TokenTypes.ARRAY_INIT;
    }

    /**
     * Calculate previous ast from given
     * @param ast given ast
     * @return previous ast
     */
    private static DetailAST getPreviousAst(DetailAST ast) {
        DetailAST previousAst;
        if (ast.getPreviousSibling() != null) {
            DetailAST previousSibling = ast.getPreviousSibling();
            if (previousSibling.getChildCount() > 0) {
                previousAst = previousSibling.getLastChild();
            } else {
                previousAst = previousSibling;
            }
        } else {
            previousAst = ast.getParent();
        }
        return previousAst;
    }
}
