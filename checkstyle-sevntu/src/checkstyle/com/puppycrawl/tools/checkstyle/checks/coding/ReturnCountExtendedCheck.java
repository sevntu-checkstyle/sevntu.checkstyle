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
public class ReturnCountExtendedCheck extends Check
{

    /**
     * The default "Maximum return count" property value.
     */
    private static final int DEFAULT_MAX_RETURN_COUNT = 1;

    /**
     * The default "lines limit" property value.
     */
    private static final int DEFAULT_LINES_LIMIT = 30;

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    private final String mKey = "return.count.extended";

    /**
     * Maximum allowed "return" literals count per method/ctor.
     */
    private int mMaxReturnCount = DEFAULT_MAX_RETURN_COUNT;

    /**
     * Limit the number of lines of which method/ctor body may consist to skip
     * this check. If method/ctor has the lines number greater than this limit,
     * it will be processed. 30 by default.
     */
    private int mLinesLimit =  DEFAULT_LINES_LIMIT;

    /**
     * The "return" literals count for current method/ctor is currently being
     * processed.
     */
    private int mCurReturnCount;

    /**
     * The "return" literal DetailAST node for given method/ctor body which does
     * not conform to the verification.
     */
    private DetailAST mCurReturnLiteral;

    /**
     * Getter for "lines limit" property.
     * @return maximum allowed number of return statements.
     * @see ReturnCountExtendedCheck#mLinesLimit
     */
    public int getLinesLimit()
    {
        return mLinesLimit;
    }

    /**
     * Setter for "lines limit" property.
     * @param aLinesLimit new lines limit value.
     * @see ReturnCountExtendedCheck#mLinesLimit
     */
    public void setLinesLimit(int aLinesLimit)
    {
        this.mLinesLimit = aLinesLimit;
    }

    /**
     * Getter for "max" property.
     * @return maximum allowed number of return statements.
     * @see ReturnCountExtendedCheck#mMaxReturnCount
     */
    public int getMax()
    {
        return mMaxReturnCount;
    }

    /**
     * Setter for "max" property.
     * @param aMax new "max return count" value.
     * @see ReturnCountExtendedCheck#mMaxReturnCount
     */
    public void setMax(int aMax)
    {
        this.mMaxReturnCount = aMax;
    }

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] {TokenTypes.METHOD_DEF, TokenTypes.CTOR_DEF, };
    }

    @Override
    public void visitToken(final DetailAST aMethodDefNode)
    {
        mCurReturnCount = 0;

        final int curMethodLinesCount = getLinesCount(aMethodDefNode);

        if (curMethodLinesCount < mLinesLimit) {
            return;
        }

        getReturnCount(aMethodDefNode);

        if (mCurReturnCount > mMaxReturnCount) {

            final String methodType = (aMethodDefNode.getType()
                    == TokenTypes.METHOD_DEF) ? "method" : "constructor";

            log(mCurReturnLiteral.getLineNo(), mKey,
                    getMethodName(aMethodDefNode), methodType, mCurReturnCount,
                    mMaxReturnCount);

        }
    }

    /**
     * Gets the lines count for given method/ctor body.
     * @param aMethodDefNode
     *        - a DetailAST node that points to the current method`s definition.
     * @return - 0 if method hasn`t open and close;<br>
     *         - 0 if method/ctor 'open' and 'close" braces are in the same line
     *         braces;<br>
     *         - Method/ctor body linelenght otherwise.
     */
    private int getLinesCount(DetailAST aMethodDefNode)
    {
        int result = 0;
        final DetailAST openingBrace = aMethodDefNode
                .findFirstToken(TokenTypes.SLIST);
        if (openingBrace != null) {
            final DetailAST closingBrace = openingBrace
                    .findFirstToken(TokenTypes.RCURLY);
            final int closingBraceLineNo = closingBrace.getLineNo();
            final int openingBraceLineNo = openingBrace.getLineNo();

            if (closingBraceLineNo == openingBraceLineNo) {
                result = 0;
            }
            else {
                result = closingBraceLineNo - openingBraceLineNo - 1;
            }
        }
        return result;
    }

    /**
     * Gets the "return" literals count for given method`s body and saves the
     * last "return" literal DetailAST node for given method/ctor body.
     * @param aMethodDefNode
     *        - a DetailAST node that points to the current method`s definition.
     */
    private void getReturnCount(DetailAST aMethodDefNode)
    {
        for (DetailAST curNode : getChildren(aMethodDefNode)) {
            if (curNode.getNumberOfChildren() > 0) {
                if (curNode.getType() == TokenTypes.LITERAL_RETURN) {
                    this.mCurReturnCount++;
                    this.mCurReturnLiteral = curNode;
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
     * @param aMethodDefNode
     *        - a DetailAST node that points to the current method`s definition.
     * @return the method name.
     */
    private String getMethodName(DetailAST aMethodDefNode)
    {
        String result = null;
        for (DetailAST curNode : getChildren(aMethodDefNode)) {
            if (curNode.getType() == TokenTypes.IDENT) {
                result = curNode.getText();
                break;
            }
        }
        return result;
    }

}
