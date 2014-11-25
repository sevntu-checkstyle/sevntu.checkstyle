////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2010  Oliver Burn
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

import com.puppycrawl.tools.checkstyle.api.Check;
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
 */
public class UselessSingleCatchCheck extends Check
{
    public final static String MSG_KEY = "useless.single.catch.check";

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] { TokenTypes.LITERAL_TRY };
    }

    @Override
    public void visitToken(DetailAST aTryBlockNode)
    {
        int catchBlocksCount = aTryBlockNode.getChildCount(TokenTypes.LITERAL_CATCH);

        if (catchBlocksCount == 1)
        {
            DetailAST catchNode = aTryBlockNode.findFirstToken(TokenTypes.LITERAL_CATCH);

            DetailAST catchStatementListNode= catchNode.findFirstToken(TokenTypes.SLIST);

            DetailAST firstStatementNode = catchStatementListNode.getFirstChild();

            if (firstStatementNode.getType() == TokenTypes.LITERAL_THROW
                    && isSimpleRethrow(firstStatementNode))
            {
                String catchParameterName = getCatchParameterName(catchNode);

                String throwParameterName = getThrowParameterName(firstStatementNode);

                if (catchParameterName.equals(throwParameterName))
                    log(catchNode, MSG_KEY);
            }
        }
    }

    /**
     * Determines whether throw node is of form
     * <code>throw exceptionObject;</code>
     * @param aThrowNode
     *        node of type TokenTypes.LITERAL_THROW
     * @return wheather this throw node is of specified form
     */
    private static boolean isSimpleRethrow(DetailAST aThrowNode)
    {
        DetailAST exprNode = aThrowNode.findFirstToken(TokenTypes.EXPR);

        return exprNode.getChildCount() == 1
                && exprNode.getFirstChild().getType() == TokenTypes.IDENT;
    }

    /**
     * Gets catch parameter name
     * @param aCatchNode
     *        node of type TokenTypes.LITERAL_CATCH
     * @return catch parameter name
     */
    private static String getCatchParameterName(DetailAST aCatchNode)
    {
        DetailAST parameterDefNode = aCatchNode.findFirstToken(TokenTypes.PARAMETER_DEF);

        return parameterDefNode.findFirstToken(TokenTypes.IDENT).getText();
    }

    /**
     * Gets throw parameter name. throw node must be of the form
     * <code>throw exceptionObject;</code>
     * @param aThrowNode
     *        node of type TokenTypes.LITERAL_THROW
     * @return throw parameter name
     */
    private static String getThrowParameterName(DetailAST aThrowNode)
    {
        return aThrowNode.findFirstToken(TokenTypes.EXPR).getFirstChild().getText();
    }

}
