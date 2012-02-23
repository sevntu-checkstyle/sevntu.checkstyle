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
public class ReturnDepthCheck extends Check
{

    /**
     * A key is pointing to the check`s warning message text in
     * "messages.properties" file.
     */
    private final String mKey = "return.depth";

    /**
     * Option to ignore methods/ctors that have return statement(s) with depth
     * lesser than N levels(scopes): 0 by default.
     */
    private int mReturnDepthLimit = 0;

    /**
     * Current found maximum "return" statement depth for current method/ctor is
     * being processed.
     */
    private int mCurMethodMaxReturnDepth;

    /**
     * Limit the number of lines of which method/ctor body may consist to skip
     * this check. If method/ctor has the lines number greater than this limit,
     * it will be processed. 20 by default.
     */
    private int mLinesLimit = 20;
    
    /**
     * The last found "return" literal DetailAST node for given method/ctor
     * body.
     */
    private DetailAST mCurReturnLiteralAST;

    /**
     * DetailAST node is pointing to current method definition is being
     * processed.
     */
    private DetailAST mCurMethodDefNode;

    /**
     * Getter for "Max return depth" property.
     * @return "Max return depth" property value.
     * @see ReturnDepthCheck#mReturnDepthLimit
     */
    public int getReturnDepthLimit()
    {
        return mReturnDepthLimit;
    }

    /**
     * Setter for "Max return depth" property.
     * @param aReturnDepthLimit - current "Max return depth" value.
     * @see ReturnDepthCheck#mReturnDepthLimit
     */
    public void setReturnDepthLimit(int aReturnDepthLimit)
    {
        mReturnDepthLimit = aReturnDepthLimit;
    }

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
        mLinesLimit = aLinesLimit;
    }
    
    @Override
    public int[] getDefaultTokens()
    {
        return new int[] {TokenTypes.METHOD_DEF, TokenTypes.CTOR_DEF, };
    }

    @Override
    public void visitToken(final DetailAST aMethodDefNode)
    {     
        final int curMethodLinesCount = getLinesCount(aMethodDefNode);

        if (curMethodLinesCount < mLinesLimit) {
            return;
        }
        
        mCurMethodMaxReturnDepth = 0;
        mCurMethodDefNode = aMethodDefNode;

        workOnMethod(aMethodDefNode);
        if (mCurMethodMaxReturnDepth > mReturnDepthLimit) {
            log(mCurReturnLiteralAST, mKey, mCurMethodMaxReturnDepth,
                    mReturnDepthLimit);
        }
    }

    /**
     * @param aMethodDefNode
     *        - a DetailAST node that points to the current method`s definition.
     */
    private void workOnMethod(DetailAST aMethodDefNode)
    {
        for (DetailAST curNode : getChildren(aMethodDefNode)) {
            if (curNode.getNumberOfChildren() > 0) {
                if (curNode.getType() == TokenTypes.LITERAL_RETURN) {
                    final int returnDepth = getDepth(mCurMethodDefNode,
                            curNode);
                    if (this.mCurMethodMaxReturnDepth < returnDepth) {
                        this.mCurMethodMaxReturnDepth = returnDepth;
                        this.mCurReturnLiteralAST = curNode;
                    }
                }
                else {
                    if (curNode.getType() != TokenTypes.METHOD_DEF) {
                        workOnMethod(curNode);
                    }
                }
            }
        }
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
        int curReturnDepth = 0;

        DetailAST curNode = aReturnStmtNode;

        while (!curNode.equals(aMethodDefNode)) {
            curNode = curNode.getParent();
            final int type = curNode.getType();
            if (type == TokenTypes.LITERAL_IF
                    || type == TokenTypes.LITERAL_SWITCH
                    || type == TokenTypes.LITERAL_FOR
                    || type == TokenTypes.LITERAL_WHILE)
            {
                curReturnDepth++;
            }
        }
        return curReturnDepth;
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
