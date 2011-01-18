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
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.CheckUtils;
import com.puppycrawl.tools.checkstyle.api.Check;

/**
 * описание чека =) Появится, когда я немного отдохну ) * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com">
 * Daniil Yaroslavtsev</a>
 */
public class AvoidHidingCauseExceptionCheck extends Check {

    /** Creates new instance of the check. */
    public AvoidHidingCauseExceptionCheck() {

    }

    public int[] getDefaultTokens() {
        return new int[] { TokenTypes.LITERAL_CATCH };
    }

    public int[] getRequiredTokens() {
        return getDefaultTokens();
    }

    public void visitToken(DetailAST aDetailAST) {
        // retrieve an exception name from current "catch" block parameters definition
        final String necessaryExcName = aDetailAST
                .findFirstToken(TokenTypes.PARAMETER_DEF).getLastChild()
                .getText();

        DetailAST throwAST = getChildTokenAST(aDetailAST,
                TokenTypes.LITERAL_THROW, "throw");
        DetailAST foundExcNameAST = null;
        DetailAST newAST = null;

        if (throwAST != null) { // throw found?            
            newAST = getChildTokenAST(throwAST, TokenTypes.LITERAL_NEW, "new");
            foundExcNameAST = getChildTokenAST(throwAST, TokenTypes.IDENT,
                    necessaryExcName);
        }

        if (throwAST != null
                && (newAST != null && (foundExcNameAST == null || !necessaryExcName
                        .equals(foundExcNameAST.getText())))) {
            // if found rethrow without saving a catched ecxeption
            log(throwAST, "avoid.hiding.cause.exception", necessaryExcName);
        }

    }

    /**
     * Looking for the keyword "throw" among current (aParentAST) node childs.
     * 
     * @param aParentAST - the current parent node.
     * @return null if the "throw" keyword was not found or the LITERAL_THROW
     *         DetailAST otherwise
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
     * Gets all the children one level below on the current top node.
     * 
     * @param aNode - current parent node.
     * @return an array of childs one level below on the current parent node
     *         aNode.
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
