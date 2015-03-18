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
 * <p>
 * <code>
 * <pre>
 * int[] ints = new int[] {
 *     0, 1, 2, 3
 * };
 * </pre>
 * </code>
 * </p>
 * This example is valid too:
 * <p>
 * <code>
 * <pre>
 * int[] tab = new int[]
 *                 {0, 1, 2, 3}
 * </pre>
 * </code>
 * </p>
 * But this violates check:
 * <p>
 * <code>
 * <pre>
 * int[] ints = new int[]{0, 1, 2, 3};
 * </pre>
 * </code>
 * </p>
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
        if (!areTokensSeparatedByWhitespace(previousAst, ast)) {
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
