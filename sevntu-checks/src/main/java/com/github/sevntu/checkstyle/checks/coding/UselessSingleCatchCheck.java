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
 * Checks for the presence of useless single catch blocks. Catch block can be
 * considered useless if it is the only catch block for try, contains only one
 * statement which rethrows catched exception. Fox example:
 * <pre>
 *  try {
 *      ...
 *  }
 *  catch(Exception e) {
 *      throw e;
 *  }
 *  </pre>
 *
 * @author <a href="mailto:zuy_alexey@mail.ru">Zuy Alexey</a>
 * @since 1.13.0
 */
public class UselessSingleCatchCheck extends AbstractCheck {

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY = "useless.single.catch.check";

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.LITERAL_TRY,
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
    public void visitToken(DetailAST tryBlockNode) {
        final int catchBlocksCount = tryBlockNode.getChildCount(TokenTypes.LITERAL_CATCH);

        if (catchBlocksCount == 1) {
            final DetailAST catchNode = tryBlockNode.findFirstToken(TokenTypes.LITERAL_CATCH);

            final DetailAST catchStatementListNode = catchNode.findFirstToken(TokenTypes.SLIST);

            final DetailAST firstStatementNode = catchStatementListNode.getFirstChild();

            if (firstStatementNode.getType() == TokenTypes.LITERAL_THROW
                    && isSimpleRethrow(firstStatementNode)) {
                final String catchParameterName = getCatchParameterName(catchNode);

                final String throwParameterName = getThrowParameterName(firstStatementNode);

                if (catchParameterName.equals(throwParameterName)) {
                    log(catchNode, MSG_KEY);
                }
            }
        }
    }

    /**
     * Determines whether throw node is of form
     * <code>throw exceptionObject;</code>.
     *
     * @param throwNode
     *        node of type TokenTypes.LITERAL_THROW
     * @return whether this throw node is of specified form
     */
    private static boolean isSimpleRethrow(DetailAST throwNode) {
        final DetailAST exprNode = throwNode.findFirstToken(TokenTypes.EXPR);

        return exprNode.getChildCount() == 1
                && exprNode.getFirstChild().getType() == TokenTypes.IDENT;
    }

    /**
     * Gets catch parameter name.
     *
     * @param catchNode
     *        node of type TokenTypes.LITERAL_CATCH
     * @return catch parameter name
     */
    private static String getCatchParameterName(DetailAST catchNode) {
        final DetailAST parameterDefNode = catchNode.findFirstToken(TokenTypes.PARAMETER_DEF);

        return parameterDefNode.findFirstToken(TokenTypes.IDENT).getText();
    }

    /**
     * Gets throw parameter name. throw node must be of the form
     * <code>throw exceptionObject;</code>
     *
     * @param throwNode
     *        node of type TokenTypes.LITERAL_THROW
     * @return throw parameter name
     */
    private static String getThrowParameterName(DetailAST throwNode) {
        return throwNode.findFirstToken(TokenTypes.EXPR).getFirstChild().getText();
    }

}
