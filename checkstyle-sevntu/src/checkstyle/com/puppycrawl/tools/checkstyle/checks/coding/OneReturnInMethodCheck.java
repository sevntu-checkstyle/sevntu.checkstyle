////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2011  Oliver Burn
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

import java.util.LinkedList;
import java.util.List;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil
 *         Yaroslavtsev</a>
 */
public class OneReturnInMethodCheck extends Check
{

    /**
     * A key is pointing to the check`s warning message text in
     * "messages.properties"
     * file.
     */
    private final String mKey = "one.return.in.method";

    /**
     * Maximum allowed "return" literals count.
     */
    private final int mMaxReturnCountPerMethod = 1;

    /**
     * The current "return" literals count for current method body is being
     * processed by check.
     */
    private int mCurReturnCount;

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] {TokenTypes.METHOD_DEF };
    }

    @Override
    public void visitToken(final DetailAST aDetailAST)
    {
        mCurReturnCount = 0;
        getReturnCount(aDetailAST);
        if (mCurReturnCount != mMaxReturnCountPerMethod) {
            log(aDetailAST, mKey, getMethodName(aDetailAST));
        }
    }

    /**
     * Gets the "return" literals count for given method`s body.
     * @param aMethodDefNode
     *        - a DetailAST node that points to the current method`s definition.
     */
    private void getReturnCount(DetailAST aMethodDefNode)
    {
        for (DetailAST curNode : getChildren(aMethodDefNode)) {
            if (curNode.getNumberOfChildren() > 0) {
                if (curNode.getType() == TokenTypes.LITERAL_RETURN) {
                    mCurReturnCount++;
                }
                else {
                    getReturnCount(curNode);
                }
            }
        }
    }

    /**
     * Gets all the children one level below on the current DetailAST parent
     * node.
     * @param aNode
     *        Current parent node.
     * @return An array of children one level below on the current parent node
     *         aNode.
     */
    private List<DetailAST> getChildren(final DetailAST aNode)
    {
        final List<DetailAST> result = new LinkedList<DetailAST>();
        DetailAST curNode = aNode.getFirstChild();
        while (curNode != null) {
            result.add(curNode);
            curNode = curNode.getNextSibling();
        }
        return result;
    }

    /**
     * Gets the name of given method by DetailAST node is pointing to it`s
     * definition.
     * @param aMethodDefAST
     *        - a DetailAST node that points to the current method`s definition.
     * @return the method name.
     */
    private String getMethodName(DetailAST aMethodDefAST)
    {
        String result = null;
        for (DetailAST curNode : getChildren(aMethodDefAST)) {
            if (curNode.getType() == TokenTypes.IDENT) {
                result = curNode.getText();
                break;
            }
        }
        return result;
    }

}
