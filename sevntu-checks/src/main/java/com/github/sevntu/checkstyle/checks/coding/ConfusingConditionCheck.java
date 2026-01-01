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
 * This check prevents negation within an "if" expression if "else" is present.
 * <br>
 * For example, rephrase: <br>
 * if (x != y) smth1(); else smth2(); as: if (x == y) smth2(); else smth1();
 *
 * <p>
 * Examples:<br>
 * "if" expression contains negation
 * </p>
 * <pre>
 *  if (a != b &amp;&amp; c != d)
 *      {
 *          smth1();
 *      }
 *      else
 *      {
 *          smth2();
 *      }
 * </pre>
 * <p>
 * You can escape of negation in "if" expression<br>
 * and swapped code in "if" and "else" block:
 * </p>
 * <pre>
 *  if (a == b || c == d)
 *      {
 *          smth2();
 *      }
 *      else
 *      {
 *          smth1();
 *      }
 * </pre>
 *
 * @author <a href="mailto:vadim.panasiuk@gmail.com">Vadim Panasiuk</a>
 * @since 1.9.0
 */
public class ConfusingConditionCheck extends AbstractCheck {

    /**
     * The key is pointing to the message text String in
     * "messages.properties file".This message used for common cases.
     */
    public static final String MSG_KEY = "confusing.condition.check";

    /**
     * Number which defines, how many lines of code in "if" block must be exceed
     * line of code in "else" block for this check was ignored.
     */
    private static final int MULTIPLY_FACTOR_FOR_ELSE_BLOCK = 4;

    /**
     * Allow to ignore "else" block if its length is in
     * "multiplyFactorForElseBlocks" time less then "if" block.
     */
    private int multiplyFactorForElseBlocks = MULTIPLY_FACTOR_FOR_ELSE_BLOCK;

    /**
     * Disable warnings for all "if" that follows the "else". It is useful for
     * save similarity with all "if-then-else" statement.
     */
    private boolean ignoreInnerIf = true;

    /**
     * Disable warnings for all sequential "if".
     */
    private boolean ignoreSequentialIf = true;

    /**
     * Disable warnings for "if" if it condition contains "null".
     */
    private boolean ignoreNullCaseInIf = true;

    /**
     * Disable warnings for "if" if "else" block contain "throw".
     */
    private boolean ignoreThrowInElse = true;

    /**
     * Enable(true) | Disable(false) warnings for all inner "if".
     *
     * @param aIgnoreInnerIf ignore inner if
     */
    public void setIgnoreInnerIf(final boolean aIgnoreInnerIf) {
        ignoreInnerIf = aIgnoreInnerIf;
    }

    /**
     * Enable(true) | Disable(false) warnings for all "if" that follows the
     * "else".
     *
     * @param ignoreSequentialIf ignore sequential if
     */
    public void setIgnoreSequentialIf(final boolean ignoreSequentialIf) {
        this.ignoreSequentialIf = ignoreSequentialIf;
    }

    /**
     * Disable(true) | Enable(false) warnings.
     *
     * @param ignoreNullCaseInIf
     *            if true disable warnings for "if".
     */
    public void setIgnoreNullCaseInIf(final boolean ignoreNullCaseInIf) {
        this.ignoreNullCaseInIf = ignoreNullCaseInIf;
    }

    /**
     * Disable(true) | Enable(false) warnings.
     *
     * @param ignoreThrowInElse
     *            if true disable warnings for "if".
     */
    public void setIgnoreThrowInElse(final boolean ignoreThrowInElse) {
        this.ignoreThrowInElse = ignoreThrowInElse;
    }

    /**
     * Sets multiplyFactorForElseBlocks field.
     *
     * @param multiplyFactorForElseBlocks
     *            define multiplyFactorForElseBlocks field.
     * @see ConfusingConditionCheck#multiplyFactorForElseBlocks
     */
    public void setMultiplyFactorForElseBlocks(int multiplyFactorForElseBlocks) {
        this.multiplyFactorForElseBlocks = multiplyFactorForElseBlocks;
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.LITERAL_IF,
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
    public void visitToken(DetailAST literalIf) {
        if (isIfEndsWithElse(literalIf)
                && !canIgnore(literalIf)
                && isRatioBetweenIfAndElseBlockSuitable(literalIf)
                && isConditionAllNegative(literalIf)) {
            log(literalIf, MSG_KEY);
        }
    }

    /**
     * Checks if the given AST can be ignored.
     *
     * @param literalIf The AST to check.
     * @return {@code true} if it can be ignored.
     */
    private boolean canIgnore(DetailAST literalIf) {
        return ignoreSequentialIf && isSequentialIf(literalIf)
            || ignoreInnerIf && isInnerIf(literalIf)
            || ignoreThrowInElse && isElseWithThrow(literalIf)
            || ignoreNullCaseInIf && isIfWithNull(literalIf);
    }

    /**
     * If ELSE following the IF block.
     *
     * @param literalIf The token to examine.
     * @return true if ELSE is following the IF block.
     */
    private static boolean isIfEndsWithElse(DetailAST literalIf) {
        final DetailAST lastChildAfterIf = literalIf.getLastChild();
        return lastChildAfterIf.getType() == TokenTypes.LITERAL_ELSE;
    }

    /**
     * Check the sequential IF or not.
     *
     * @param literalIf The token to examine.
     * @return true if the IF is sequential.
     */
    private static boolean isSequentialIf(DetailAST literalIf) {
        final DetailAST lastChildAfterIf = literalIf.getLastChild();
        return lastChildAfterIf.getFirstChild()
                .getType() == TokenTypes.LITERAL_IF;
    }

    /**
     * Check the inner IF or not.
     *
     * @param literalIf The token to examine.
     * @return true if the if is inner.
     */
    private static boolean isInnerIf(DetailAST literalIf) {
        final DetailAST childIf = literalIf.getFirstChild().getNextSibling()
                .getNextSibling().getNextSibling();
        return childIf.branchContains(TokenTypes.LITERAL_IF);
    }

    /**
     * Check IF - ELSE or not that contained THROW in the expression in a block
     * ELSE.
     *
     * @param literalIf The token to examine.
     * @return true if the ELSE contains a THROW.
     */
    private static boolean isElseWithThrow(DetailAST literalIf) {
        final DetailAST lastChildAfterIf = literalIf.getLastChild();
        return lastChildAfterIf.getFirstChild().branchContains(
                TokenTypes.LITERAL_THROW);
    }

    /**
     * Display if the ratio of the number of rows in an IF and ELSE. If the
     * condition is met, checkIfElseCodeLinesRatio = true.
     *
     * @param literalIf The token to examine.
     * @return If the condition is met (true) |Isn't men (false).
     */
    private boolean isRatioBetweenIfAndElseBlockSuitable(DetailAST literalIf) {
        boolean result = true;

        final DetailAST lastChildAfterIf = literalIf.getLastChild();
        final int linesOfCodeInElseBlock = getAmountOfCodeRowsInBlock(lastChildAfterIf);
        if (linesOfCodeInElseBlock > 0) {
            final int linesOfCodeInIfBlock = getAmountOfCodeRowsInBlock(literalIf);
            result = linesOfCodeInIfBlock / linesOfCodeInElseBlock < multiplyFactorForElseBlocks;
        }
        return result;
    }

    /**
     * Counts code lines in block IF or ELSE tree.
     *
     * @param detailAST The token to examine.
     * @return linesOfCodeInIfBlock line of code in block.
     */
    private static int getAmountOfCodeRowsInBlock(DetailAST detailAST) {
        final DetailAST firstBrace = getFirstBrace(detailAST);
        int linesOfCodeInIfBlock;

        if (firstBrace == null) {
            linesOfCodeInIfBlock = 0;
        }
        else {
            final DetailAST lastBrace = firstBrace.getLastChild();
            linesOfCodeInIfBlock = lastBrace.getLineNo()
                    - firstBrace.getLineNo();
            // If the closing brace on a separate line - ignore this line.
            if (lastBrace.getLineNo() != lastBrace.getParent().getLineNo()) {
                linesOfCodeInIfBlock -= 1;
            }
        }

        return linesOfCodeInIfBlock;
    }

    /**
     * Retrieves the first, opening brace of an {@code if} or {@code else} statement.
     *
     * @param detailAST The token to examine.
     * @return The opening brace token or {@code null} if it doesn't exist.
     */
    private static DetailAST getFirstBrace(DetailAST detailAST) {
        DetailAST firstBrace;

        if (detailAST.getType() == TokenTypes.LITERAL_ELSE) {
            firstBrace = detailAST.getFirstChild();

            if (firstBrace.getType() == TokenTypes.LITERAL_IF) {
                firstBrace = getFirstBrace(firstBrace);
            }
        }
        else {
            firstBrace = detailAST.getFirstChild().getNextSibling()
                    .getNextSibling().getNextSibling();
        }

        if (firstBrace != null && firstBrace.getType() != TokenTypes.SLIST) {
            firstBrace = null;
        }

        return firstBrace;
    }

    /**
     * Number of comparison operators in IF must be one less than negative
     * symbols.
     *
     * @param literalIf The token to examine.
     * @return true
     */
    private static boolean isConditionAllNegative(DetailAST literalIf) {
        boolean result = false;

        final DetailAST ifExpr = literalIf.getFirstChild().getNextSibling();
        final int countOfLnot = getCountOfToken(ifExpr, TokenTypes.LNOT);
        final int countOfNotequal = getCountOfToken(ifExpr,
                TokenTypes.NOT_EQUAL);
        final int countOfNegativeSymbolInIf = countOfLnot + countOfNotequal;
        if (countOfNegativeSymbolInIf > 0) {
            final int countOfLand = getCountOfToken(ifExpr, TokenTypes.LAND);
            final int countOfLor = getCountOfToken(ifExpr, TokenTypes.LOR);
            final int countOfComparisonOperators = countOfLand + countOfLor;
            if (countOfNegativeSymbolInIf - countOfComparisonOperators == 1) {
                result = true;
            }
        }
        return result;
    }

    /**
     * Check IF or not that contained NULL in the expression IF.
     *
     * @param literalIf The token to examine.
     * @return true if the IF contains a NULL.
     * @see ignoreNullCaseInIf
     */

    private static boolean isIfWithNull(DetailAST literalIf) {
        return literalIf.getFirstChild().getNextSibling()
                .branchContains(TokenTypes.LITERAL_NULL);
    }

    /**
     * Recursive method which counts a tokens of the provided type in detAst
     * tree.
     *
     * @param detAst a tree for "atype" tokens searching.
     * @param type a TokenType
     * @return The number of tokens found.
     */
    private static int getCountOfToken(DetailAST detAst, int type) {
        int count = 0;
        if (detAst.branchContains(type)) {
            DetailAST node = detAst;
            while (node != null) {
                count += node.getChildCount(type);
                final DetailAST detAstChild = node.getFirstChild();
                if (detAstChild != null) {
                    count += getCountOfToken(detAstChild, type);
                }
                node = node.getNextSibling();
            }
        }
        return count;
    }

}
