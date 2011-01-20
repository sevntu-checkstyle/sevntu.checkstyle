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
package com.puppycrawl.tools.checkstyle.checks.coding;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.api.Check;

/**
 * This check prevents exceptions rethrow if you lose the original exception
 * object. *
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil
 *         Yaroslavtsev</a>
 */
public class AvoidHidingCauseExceptionCheck extends Check {

    /** Creates new instance of the check. */
    public AvoidHidingCauseExceptionCheck() {

    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] { TokenTypes.LITERAL_CATCH };
    }

    @Override
    public void visitToken(DetailAST aDetailAST) {
        // retrieve an exception name from current "catch" block parameters definition
        final String originExcName = aDetailAST
                .findFirstToken(TokenTypes.PARAMETER_DEF).getLastChild()
                .getText();

        DetailAST throwAST = getChildTokenAST(aDetailAST,
                TokenTypes.LITERAL_THROW, "throw");
        DetailAST rethrowExcNameAST = null;
        DetailAST aNewAST = null;

        // retrieve a DetailAST which contains the name of rethrown exception 
        // or null if rethrow does not exist in current "catch" block
        if (throwAST != null) {
            aNewAST = getChildTokenAST(throwAST, TokenTypes.LITERAL_NEW, "new");
            rethrowExcNameAST = getChildTokenAST(throwAST, TokenTypes.IDENT,
                    originExcName);
        }

        if (throwAST != null
                && (aNewAST != null && (rethrowExcNameAST == null || !originExcName
                        .equals(rethrowExcNameAST.getText())))) {
            log(throwAST, "avoid.hiding.cause.exception", originExcName);
        }

    }

    /**
     * Looking for the certain token (TokenType) which has some appropriate text
     * obtained by DetailAST.getText() method among current (aParentAST) node
     * childs.
     * 
     * @param aParentAST The current parent node.
     * @param TokenType Allowable type of token, which you want to search.
     * @param TokenText Specific text that matches the desired token.
     * @return The DetailAST of desired this token if it was found or null
     *         otherwise.
     */
    public DetailAST getChildTokenAST(DetailAST aParentAST, int TokenType,
            String TokenText) {

        final DetailAST asts[] = getChilds(aParentAST);

        for (DetailAST currentNode : asts) {

            if (currentNode.getType() != TokenTypes.PARAMETER_DEF
                    && currentNode.getNumberOfChildren() > 0) {
                final DetailAST astResult = (getChildTokenAST(currentNode,
                        TokenType, TokenText));
                if (astResult != null) {
                    return astResult;
                }
            }

            if (currentNode.getType() == TokenType
                    && currentNode.getText().equals(TokenText)) {
                return currentNode;
            }

            if (currentNode.getNextSibling() != null) {
                currentNode = currentNode.getNextSibling();
            }
        }
        return null;
    }

    /**
     * Gets all the children one level below on the current top node. *     * 
     * @param aNode Current parent node.
     * @return New DetailAST[] array of childs one level below on the current
     *         parent node (aNode).
     */
    public DetailAST[] getChilds(DetailAST aNode) {
        final DetailAST[] result = new DetailAST[aNode.getChildCount()];

        DetailAST currNode = aNode.getFirstChild();

        for (int i = 0; i < aNode.getNumberOfChildren(); i++) {
            result[i] = currNode;
            currNode = currNode.getNextSibling();
        }

        return result;
    }

}
