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
 * This checks enforces whitespace before array initializer.
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
 * <p>
 * This example is valid too:
 * </p>
 *
 * <pre>
 * int[] tab = new int[]
 *                 {0, 1, 2, 3}
 * </pre>
 *
 * <p>
 * But this violates check:
 * </p>
 *
 * <pre>
 * int[] ints = new int[]{0, 1, 2, 3};
 * </pre>
 *
 *
 * @author <a href="mailto:piotr.listkiewicz@gmail.com">liscju</a>
 * @since 1.14.0
 */
public class WhitespaceBeforeArrayInitializerCheck extends AbstractCheck {

    /**
     * Violation message key.
     */
    public static final String MSG_KEY = "whitespace.before.array.initializer";

    @Override
    public int[] getDefaultTokens() {
        return new int[] {TokenTypes.ARRAY_INIT};
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
        final DetailAST previousAst = getPreviousAst(ast);
        if (!areTokensSeparatedByWhitespace(previousAst, ast) && isNestedArrayInitializer(ast)) {
            log(ast, MSG_KEY);
        }
    }

    /**
     * Checks if firstAst and secondAst are separated by whitespace.
     *
     * @param firstAST DetailAST
     * @param secondAST DetailAST
     * @return true if firstAST and secondAST are separated by whitespace,false otherwise
     */
    private static boolean areTokensSeparatedByWhitespace(DetailAST firstAST, DetailAST secondAST) {
        boolean isDistanceValid = true;
        final int columnDistance = secondAST.getColumnNo() - firstAST.getColumnNo();
        if (columnDistance == 1) {
            final int lineDistance = secondAST.getLineNo() - firstAST.getLineNo();
            if (lineDistance == 0) {
                isDistanceValid = false;
            }
        }
        return isDistanceValid;
    }

    /**
     * Checks whether inspected array initializer is nested in other array initializer.
     *
     * @param ast {@link TokenTypes#ARRAY_INIT} token to inspect
     * @return true when this array initializer is nested in other initializer; false otherwise
     */
    private static boolean isNestedArrayInitializer(DetailAST ast) {
        return ast.getParent().getType() != TokenTypes.ARRAY_INIT;
    }

    /**
     * Calculate previous ast from given.
     *
     * @param ast given ast
     * @return previous ast
     */
    private static DetailAST getPreviousAst(DetailAST ast) {
        final DetailAST previousAst;
        if (ast.getPreviousSibling() == null) {
            previousAst = ast.getParent();
        }
        else {
            final DetailAST previousSibling = ast.getPreviousSibling();
            if (previousSibling.getChildCount() > 0) {
                previousAst = previousSibling.getLastChild();
            }
            else {
                previousAst = previousSibling;
            }
        }
        return previousAst;
    }

}
