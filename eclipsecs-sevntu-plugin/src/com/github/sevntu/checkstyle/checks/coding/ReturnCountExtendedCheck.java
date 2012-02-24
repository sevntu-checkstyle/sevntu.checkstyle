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

package com.github.sevntu.checkstyle.checks.coding;

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
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    private final String mKeyMethod = "return.count.extended.method";

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    private final String mKeyCtor = "return.count.extended.ctor";
    
    /**
     * Maximum allowed "return" literals count per method/ctor (1 by default).
     */
    private int mMaxReturnCount = 1;

    /**
     * Limit the number of lines of which method/ctor body may consist to skip
     * this check. If method/ctor has the lines number greater than this limit,
     * it will be processed. 20 by default.
     */
    private int mLinesLimit = 20;

    /**
     * Option to ignore methods/ctors that have return statement(s) with depth
     * lesser than N levels(scopes): 0 by default.
     */
    private int mReturnDepthLimit = 0;
    
    /**
     * Option to ignore empty return statements in void methods and ctors. 'False' by default.
     */
    private boolean mIgnoreEmptyReturns = false;
    
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
     * DetailAST node is pointing to current method definition is being
     * processed.
     */
    private DetailAST mCurMethodDefNode;

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
     * @param aMax - new "max return count" value.
     * @see ReturnCountExtendedCheck#mMaxReturnCount
     */
    public void setMax(int aMax)
    {
        mMaxReturnCount = aMax;
    }
    
    /**
     * Getter for "lines limit" property.
     * @return "lines limit" property value.
     * @see ReturnCountExtendedCheck#mLinesLimit
     */
    public int getLinesLimit()
    {
        return mLinesLimit;
    }

    /**
     * Setter for "lines limit" property.
     * @param aLinesLimit - new "lines limit" property value.
     * @see ReturnCountExtendedCheck#mLinesLimit
     */
    public void setLinesLimit(int aLinesLimit)
    {
        mLinesLimit = aLinesLimit;
    }
   
    /**
     * Getter for "max return depth" property.
     * @return "max return depth" property value.
     * @see ReturnDepthCheck#mReturnDepthLimit
     */
    public int getReturnDepthLimit()
    {
        return mReturnDepthLimit;
    }

    /**
     * Setter for "Max return depth" property.
     * @param aReturnDepthLimit - new "max return depth" property value.
     * @see ReturnDepthCheck#mReturnDepthLimit
     */
    public void setReturnDepthLimit(int aReturnDepthLimit)
    {
        mReturnDepthLimit = aReturnDepthLimit;
    }

    /**
     * Getter for "allow empty returns" property.
     * @return "allow empty returns" property value.
     * @see ReturnCountExtendedCheck#mIgnoreEmptyReturns
     */
    public boolean getIgnoreEmptyReturns()
    {
        return mIgnoreEmptyReturns;
    }
    
    /**
     * Setter for "allow empty returns" property.
     * @param aIgnoreEmptyReturns the new "allow empty returns" property value.
     * @see ReturnCountExtendedCheck#mIgnoreEmptyReturns
     */
    public void setIgnoreEmptyReturns(boolean aIgnoreEmptyReturns)
    {
        mIgnoreEmptyReturns = aIgnoreEmptyReturns;
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
        mCurMethodDefNode = aMethodDefNode;
        
        
        final int curMethodLinesCount = getLinesCount(aMethodDefNode);

        if (curMethodLinesCount < mLinesLimit) {
            return;
        }

        getReturnCount(aMethodDefNode);

        if (mCurReturnCount > mMaxReturnCount) {

            final String mKey = (aMethodDefNode.getType()
                    == TokenTypes.METHOD_DEF) ? mKeyMethod : mKeyCtor;

            log(mCurReturnLiteral.getLineNo(), mKey,
                    getMethodName(aMethodDefNode), mCurReturnCount,
                    mMaxReturnCount);

        }
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
            if (curNode.getType() == TokenTypes.LITERAL_RETURN &&
                    isReturnDepthBad(curNode)) {
                if (mIgnoreEmptyReturns && isReturnStatementEmpty(curNode)) {
                    // no code
                } else {
                    mCurReturnCount++;
                    mCurReturnLiteral = curNode;
                }
            }
            else {
                if (curNode.getType() != TokenTypes.METHOD_DEF) {
                    getReturnCount(curNode);
                }
            }
        }
    }
    
    /**
     * 
     * @param aReturnNode
     * @return
     */
    private boolean isReturnDepthBad(DetailAST aReturnNode)
    {        
        return mReturnDepthLimit > getDepth(mCurMethodDefNode, aReturnNode);
    }

    /**
     * 
     * @param aReturnNode
     * @return
     */
    private boolean isReturnStatementEmpty(DetailAST aReturnNode)
    {
        boolean result = false;
        DetailAST returnChildNode = aReturnNode.getFirstChild();
        if (returnChildNode != null
                && returnChildNode.getType() == TokenTypes.SEMI) {
            result = true;
        }
        return result;
    }

    /**
     * Gets the depth level of given "return" statement from it`s DetailAST
     * node. So far, this method considers only if-else, for and while
     * constructions.
     * @param aMethodDefNode
     *        - a DetailAST node that points to the current method`s definition.
     * @param aReturnStmtNode
     *        - given "return" statement node.
     * @return the depth of given
     */
    private int getDepth(DetailAST aMethodDefNode, DetailAST aReturnStmtNode)
    {
        int result = 0;

        DetailAST curNode = aReturnStmtNode;
       
        while (!curNode.equals(aMethodDefNode)) {
            curNode = curNode.getParent();
            final int type = curNode.getType();
            if (type == TokenTypes.LITERAL_IF
                    || type == TokenTypes.LITERAL_SWITCH
                    || type == TokenTypes.LITERAL_FOR
                    || type == TokenTypes.LITERAL_DO
                    || type == TokenTypes.LITERAL_WHILE
                    || type == TokenTypes.LITERAL_TRY
                    )
            {
                result++;
            }
        }        
        // System.out.println("Method: "+getMethodName(aMethodDefNode)+" = "+result);
        return result;
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

}
