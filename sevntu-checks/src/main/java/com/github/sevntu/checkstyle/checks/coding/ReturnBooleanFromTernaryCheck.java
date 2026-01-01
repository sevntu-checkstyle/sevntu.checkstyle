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
 * It is a bad practice to return boolean values from ternary operations. Just use the value
 * inside branch instead.
 * </p>
 *
 * @author Ivan Sopov
 * @since 1.8.0
 */
public class ReturnBooleanFromTernaryCheck extends AbstractCheck {

    /**
     * Warning message key.
     */
    public static final String MSG_KEY = "return.boolean.ternary";

    /** String representation of true keyword. */
    private static final String LITERAL_TRUE = "true";

    /** String representation of false keyword. */
    private static final String LITERAL_FALSE = "false";

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.QUESTION,
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
        final DetailAST secondBranch = ast.getLastChild();
        final DetailAST firstBranch = secondBranch.getPreviousSibling().getPreviousSibling();
        if (LITERAL_TRUE.equals(secondBranch.getText())
                || LITERAL_FALSE.equals(secondBranch.getText())
                || LITERAL_TRUE.equals(firstBranch.getText())
                || LITERAL_FALSE.equals(firstBranch.getText())) {
            log(ast, MSG_KEY);
        }
    }

}
