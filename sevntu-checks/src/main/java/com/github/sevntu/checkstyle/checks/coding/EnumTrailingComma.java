////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2020 the original author or authors.
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

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * Checks that enums contains a trailing comma.
 * </p>
 * <pre>
 * enum MovieType {
 *  GOOD,
 *  BAD,
 *  UGLY,
 *  ;
 * }
 * </pre>
 * <p>
 * The check demands a comma at the end if neither left nor right curly braces are on the same line
 * as the last element of the enum.
 * </p>
 * <pre>
 * enum MovieType {GOOD, BAD, UGLY;}
 * enum MovieType {GOOD, BAD, UGLY
 *  ;}
 * enum MovieType {GOOD, BAD,
 *  UGLY;}
 * enum MovieType {GOOD, BAD,
 *  UGLY; // Violation
 * }
 * </pre>
 * <p>
 * Putting this comma in makes it easier to change the order of the elements or add new elements
 * at the end. Main benefit of a trailing comma is that when you add new entry to an enum, no
 * surrounding lines are changed.
 * </p>
 * <pre>
 * enum MovieType {
 *   GOOD,
 *   BAD, //OK
 * }
 *
 * enum MovieType {
 *   GOOD,
 *   BAD,
 *   UGLY,  // Just this line added, no other changes
 * }
 * </pre>
 * <p>
 * If closing brace is on the same line as trailing comma, this benefit is gone (as the check does
 * not demand a certain location of curly braces the following two cases will not produce a
 * violation):
 * </p>
 * <pre>
 * enum MovieType {GOOD,
 *  BAD,} // Trailing comma not needed, line needs to be modified anyway
 *
 * enum MovieType {GOOD,
 *  BAD, // Modified line
 *  UGLY,} // Added line
 * </pre>
 *
 * @author <a href="mailto:yasser.aziza@gmail.com"> Yasser Aziza </a>
 */
public class EnumTrailingComma extends AbstractCheck {

    /**
     * Warning message key pointing to the warning message text in "messages.properties" file.
     */
    public static final String MSG_KEY = "enum.trailing.comma";

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.ENUM_CONSTANT_DEF,
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
        final DetailAST nextSibling = ast.getNextSibling();

        if (TokenTypes.COMMA != nextSibling.getType() && isSeparateLine(ast)) {
            log(ast, MSG_KEY);
        }
    }

    /**
     * Checks if the given {@link TokenTypes#ENUM_CONSTANT_DEF} element is in the same line with
     * the {@link TokenTypes#LCURLY} or {@link TokenTypes#RCURLY}.
     *
     * @param ast the enum node
     * @return {@code true} if the element is on the same line with {@link TokenTypes#LCURLY} or
     * {@link TokenTypes#RCURLY}, {@code false} otherwise.
     */
    private static boolean isSeparateLine(DetailAST ast) {
        final DetailAST objBlock = ast.getParent();
        final DetailAST leftCurly = objBlock.findFirstToken(TokenTypes.LCURLY);
        final DetailAST rightCurly = objBlock.findFirstToken(TokenTypes.RCURLY);

        return !areOnSameLine(leftCurly, ast)
                && !areOnSameLine(ast, rightCurly);
    }

    /**
     * Determines if two ASTs are on the same line.
     *
     * @param ast1   the first AST
     * @param ast2   the second AST
     *
     * @return true if they are on the same line.
     */
    private static boolean areOnSameLine(DetailAST ast1, DetailAST ast2) {
        return ast1.getLineNo() == ast2.getLineNo();
    }

}
