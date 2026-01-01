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
 * <p>
 * Checks if unnecessary parentheses are used in a statement or expression.
 * The check will flag the following with warnings:
 * </p>
 * <pre>
 *     return (x);          // parens around identifier
 *     return (x + 1);      // parens around return value
 *     int x = (y / 2 + 1); // parens around assignment rhs
 *     for (int i = (0); i &lt; 10; i++) {  // parens around literal
 *     t -= (z + 1);        // parens around assignment rhs</pre>
 * <p>
 * The check is not "type aware", that is to say, it can't tell if parentheses
 * are unnecessary based on the types in an expression.  It also doesn't know
 * about operator precedence and associativity; therefore it won't catch
 * something like
 * </p>
 * <pre>
 *     int x = (a + b) + c;</pre>
 * <p>
 * In the above case, given that <em>a</em>, <em>b</em>, and <em>c</em> are
 * all <code>int</code> variables, the parentheses around <code>a + b</code>
 * are not needed.
 * </p>
 *
 * @author Eric Roe
 * @author Antonenko Dmitriy
 * @since 1.8.0
 */
public class UnnecessaryParenthesesExtendedCheck extends AbstractCheck {

    /** Warning message key.*/
    public static final String MSG_KEY_ASSIGN = "unnecessary.paren.assign";
    /** Warning message key.*/
    public static final String MSG_KEY_EXPR = "unnecessary.paren.expr";
    /** Warning message key.*/
    public static final String MSG_KEY_IDENT = "unnecessary.paren.ident";
    /** Warning message key.*/
    public static final String MSG_KEY_LITERAL = "unnecessary.paren.literal";
    /** Warning message key.*/
    public static final String MSG_KEY_RETURN = "unnecessary.paren.return";
    /** Warning message key.*/
    public static final String MSG_KEY_STRING = "unnecessary.paren.string";
    /** The minimum number of child nodes to consider for a match. */
    private static final int MIN_CHILDREN_FOR_MATCH = 3;
    /** The maximum string length before we chop the string. */
    private static final int MAX_QUOTED_LENGTH = 25;

    /** Token types for literals. */
    private static final int[] LITERALS = {
        TokenTypes.NUM_DOUBLE,
        TokenTypes.NUM_FLOAT,
        TokenTypes.NUM_INT,
        TokenTypes.NUM_LONG,
        TokenTypes.STRING_LITERAL,
        TokenTypes.LITERAL_NULL,
        TokenTypes.LITERAL_FALSE,
        TokenTypes.LITERAL_TRUE,
    };

    /** Token types for assignment operations. */
    private static final int[] ASSIGNMENTS = {
        TokenTypes.ASSIGN,
        TokenTypes.BAND_ASSIGN,
        TokenTypes.BOR_ASSIGN,
        TokenTypes.BSR_ASSIGN,
        TokenTypes.BXOR_ASSIGN,
        TokenTypes.DIV_ASSIGN,
        TokenTypes.MINUS_ASSIGN,
        TokenTypes.MOD_ASSIGN,
        TokenTypes.PLUS_ASSIGN,
        TokenTypes.SL_ASSIGN,
        TokenTypes.SR_ASSIGN,
        TokenTypes.STAR_ASSIGN,
    };

    /** Token types equals operations. */
    private static final int[] EQUALS = {
        TokenTypes.EQUAL,
        TokenTypes.NOT_EQUAL,
        TokenTypes.LOR,
        TokenTypes.LAND,
        TokenTypes.BOR,
    };

    /**
     * Used to ignore unnecessary parentheses check
     * in calculation of boolean.
     */
    private boolean ignoreCalculationOfBooleanVariables;

    /**
     * Used to ignore unnecessary parentheses check
     * in calculation of boolean with return state.
     */
    private boolean ignoreCalculationOfBooleanVariablesWithReturn;

    /**
     * Used to ignore unnecessary parentheses check
     * in calculation of boolean with assert state.
     */
    private boolean ignoreCalculationOfBooleanVariablesWithAssert;

    /**
     * Used to test if logging a warning in a parent node may be skipped
     * because a warning was already logged on an immediate child node.
     */
    private DetailAST parentToSkip;
    /** Depth of nested assignments.  Normally this will be 0 or 1. */
    private int assignDepth;

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.EXPR,
            TokenTypes.IDENT,
            TokenTypes.NUM_DOUBLE,
            TokenTypes.NUM_FLOAT,
            TokenTypes.NUM_INT,
            TokenTypes.NUM_LONG,
            TokenTypes.STRING_LITERAL,
            TokenTypes.LITERAL_NULL,
            TokenTypes.LITERAL_FALSE,
            TokenTypes.LITERAL_TRUE,
            TokenTypes.ASSIGN,
            TokenTypes.BAND_ASSIGN,
            TokenTypes.BOR_ASSIGN,
            TokenTypes.BSR_ASSIGN,
            TokenTypes.BXOR_ASSIGN,
            TokenTypes.DIV_ASSIGN,
            TokenTypes.MINUS_ASSIGN,
            TokenTypes.MOD_ASSIGN,
            TokenTypes.PLUS_ASSIGN,
            TokenTypes.SL_ASSIGN,
            TokenTypes.SR_ASSIGN,
            TokenTypes.STAR_ASSIGN,
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
    public void visitToken(DetailAST ast) {
        final int type = ast.getType();
        final DetailAST parent = ast.getParent();

        // shouldn't process assign in annotation pairs
        if (type != TokenTypes.ASSIGN
            || parent.getType() != TokenTypes.ANNOTATION_MEMBER_VALUE_PAIR) {
            final boolean surrounded = isSurrounded(ast);

            // An identifier surrounded by parentheses.
            if (surrounded && type == TokenTypes.IDENT) {
                parentToSkip = ast.getParent();
                log(ast, MSG_KEY_IDENT, ast.getText());
            }
            // A literal (numeric or string) surrounded by parentheses.
            else if (surrounded && inTokenList(type, LITERALS)) {
                parentToSkip = ast.getParent();
                if (type == TokenTypes.STRING_LITERAL) {
                    log(ast, MSG_KEY_STRING,
                        chopString(ast.getText()));
                }
                else {
                    log(ast, MSG_KEY_LITERAL, ast.getText());
                }
            }
            // The rhs of an assignment surrounded by parentheses.
            else if (inTokenList(type, ASSIGNMENTS)) {
                assignDepth++;
                final DetailAST last = ast.getLastChild();
                if (last.getType() == TokenTypes.RPAREN) {
                    final DetailAST subtree = ast.getFirstChild().getNextSibling()
                        .getNextSibling();
                    final int subtreeType = subtree.getType();
                    if (!ignoreCalculationOfBooleanVariables || !inTokenList(
                        subtreeType, EQUALS)) {
                        log(ast, MSG_KEY_ASSIGN);
                    }
                }
            }
        }
    }

    @Override
    public void leaveToken(DetailAST ast) {
        final int type = ast.getType();
        final DetailAST parent = ast.getParent();

        // shouldn't process assign in annotation pairs
        if (type != TokenTypes.ASSIGN
            || parent.getType() != TokenTypes.ANNOTATION_MEMBER_VALUE_PAIR) {
            // An expression is surrounded by parentheses.
            if (type == TokenTypes.EXPR) {
                leaveTokenExpression(ast);

                parentToSkip = null;
            }
            else if (inTokenList(type, ASSIGNMENTS)) {
                assignDepth--;
            }

            super.leaveToken(ast);
        }
    }

    /**
     * Examines the expression AST for violations.
     *
     * @param ast The AST to examine.
     */
    private void leaveTokenExpression(DetailAST ast) {
        // If 'mParentToSkip' == 'aAST', then we've already logged a
        // warning about an immediate child node in visitToken, so we don't
        // need to log another one here.

        if (parentToSkip != ast && exprSurrounded(ast)) {
            if (assignDepth >= 1) {
                if (!ignoreCalculationOfBooleanVariables || !inTokenList(
                    subtreeType(ast), EQUALS)) {
                    log(ast, MSG_KEY_ASSIGN);
                }
            }
            else if (ast.getParent().getType()
                == TokenTypes.LITERAL_RETURN) {
                if (!ignoreCalculationOfBooleanVariablesWithReturn
                        || !inTokenList(subtreeType(ast), EQUALS)) {
                    log(ast, MSG_KEY_RETURN);
                }
            }
            else if (ast.getParent().getType()
                    == TokenTypes.LITERAL_ASSERT) {
                if (!ignoreCalculationOfBooleanVariablesWithAssert
                        || !inTokenList(subtreeType(ast), EQUALS)) {
                    log(ast, MSG_KEY_EXPR);
                }
            }
            else {
                if (!ignoreCalculationOfBooleanVariables || !inTokenList(
                    subtreeType(ast), EQUALS)) {
                    log(ast, MSG_KEY_EXPR);
                }
            }
        }
    }

    /**
     * Tests if the given <code>DetailAST</code> is surrounded by parentheses.
     * In short, does <code>aAST</code> have a previous sibling whose type is
     * <code>TokenTypes.LPAREN</code> and a next sibling whose type is <code>
     * TokenTypes.RPAREN</code>.
     *
     * @param ast the <code>DetailAST</code> to check if it is surrounded by
     *        parentheses.
     * @return <code>true</code> if <code>aAST</code> is surrounded by
     *         parentheses.
     */
    private static boolean isSurrounded(DetailAST ast) {
        final DetailAST prev = ast.getPreviousSibling();
        final DetailAST next = ast.getNextSibling();

        return prev != null && next != null
            && prev.getType() == TokenTypes.LPAREN;
    }

    /**
     * Tests if the given expression node is surrounded by parentheses.
     *
     * @param ast a <code>DetailAST</code> whose type is
     *        <code>TokenTypes.EXPR</code>.
     * @return <code>true</code> if the expression is surrounded by
     *         parentheses.
     * @throws IllegalArgumentException if <code>aAST.getType()</code> is not
     *         equal to <code>TokenTypes.EXPR</code>.
     */
    private static boolean exprSurrounded(DetailAST ast) {
        return ast.getChildCount() >= MIN_CHILDREN_FOR_MATCH;
    }

    /**
     * Check if the given token type can be found in an array of token types.
     *
     * @param type the token type.
     * @param tokens an array of token types to search.
     * @return <code>true</code> if <code>aType</code> was found in <code>
     *         aTokens</code>.
     */
    private static boolean inTokenList(int type, int... tokens) {
        // NOTE: Given the small size of the two arrays searched, I'm not sure
        //       it's worth bothering with doing a binary search or using a
        //       HashMap to do the searches.

        boolean found = false;
        for (int index = 0; index < tokens.length && !found; index++) {
            found = tokens[index] == type;
        }
        return found;
    }

    /**
     * Returns the specified string chopped to <code>MAX_QUOTED_LENGTH</code>
     * plus an ellipsis (...) if the length of the string exceeds <code>
     * MAX_QUOTED_LENGTH</code>.
     *
     * @param string the string to potentially chop.
     * @return the chopped string if <code>aString</code> is longer than
     *         <code>MAX_QUOTED_LENGTH</code>; otherwise <code>aString</code>.
     */
    private static String chopString(String string) {
        final String result;
        if (string.length() > MAX_QUOTED_LENGTH) {
            result = string.substring(0, MAX_QUOTED_LENGTH) + "...\"";
        }
        else {
            result = string;
        }
        return result;
    }

    /**
     * Returns the type of the subtree, witch need to detect equals
     * in boolean calculation.
     *
     * @param ast the <code>DetailAST</code>
     * @return integer value of subtree
     */
    private static int subtreeType(DetailAST ast) {
        final DetailAST subtree = ast.getFirstChild()
            .getNextSibling();
        return subtree.getType();
    }

    /**
     * Sets flag to IgnoreCalculationOfBooleanVariables.
     *
     * @param ignoreCalculationOfBooleanVariables
     *            if true ignore unnecessary parentheses check in calculation of
     *            boolean.
     */

    public final void setIgnoreCalculationOfBooleanVariables(
            final boolean ignoreCalculationOfBooleanVariables) {
        this.ignoreCalculationOfBooleanVariables =
            ignoreCalculationOfBooleanVariables;
    }

    /**
     * Sets flag to IgnoreCalculationOfBooleanVariablesWithReturn.
     *
     * @param ignoreCalculationOfBooleanVariablesWithReturn
     *            if true ignore unnecessary parentheses check in calculation of
     *            boolean with return state.
     */
    public final void setIgnoreCalculationOfBooleanVariablesWithReturn(
            final boolean ignoreCalculationOfBooleanVariablesWithReturn) {
        this.ignoreCalculationOfBooleanVariablesWithReturn =
            ignoreCalculationOfBooleanVariablesWithReturn;
    }

    /**
     * Sets flag to IgnoreCalculationOfBooleanVariablesWithAssert.
     *
     * @param ignoreCalculationOfBooleanVariablesWithAssert
     *            if true ignore unnecessary parentheses check in calculation of
     *            boolean with assert state
     */
    public final void setIgnoreCalculationOfBooleanVariablesWithAssert(
            final boolean ignoreCalculationOfBooleanVariablesWithAssert) {
        this.ignoreCalculationOfBooleanVariablesWithAssert =
            ignoreCalculationOfBooleanVariablesWithAssert;
    }

}
