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
 * One return per method is a good practice as its ease understanding of method
 * logic. <br>
 * Strict structured programmers believe that every method should have only one
 * return statement in the end. <br>
 * <br>
 * Reasoning is that:
 * <dl>
 * <li>It is easier to understand control flow when you know exactly where the
 * method returns.
 * <li>Methods with 2-3 or many "return" statements are much more difficult to
 * understand, debug and refactor.
 * </dl>
 * Using this check you can find methods that have more "return" statement count
 * than value is given by "MaxReturnCount" property. <br>
 * <br>
 * Setting up another options will make check to ignore:
 * <ol>
 * <li>Methods which linelength less than given value ("linesLimit" property).
 * <li>"return" statements which depth is greater or equal to the given value
 * ("returnDepthLimit" property). There are few supported <br>
 * coding blocks when depth counting: "if-else", "for", "while"/"do-while" and
 * "switch".
 * <li>"Empty" return statements = return statements in void methods and ctors
 * that have not any expression ("ignoreEmptyReturns" property).
 * </ol>
 * So, this is much improved version of the existing
 * {@link ReturnCountCheck}.<br>
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil
 *         Yaroslavtsev</a>
 */
public class ReturnCountExtendedCheck extends Check
{

    /**
     * Default maximum allowed "return" literals count per method/ctor.
     */
    private static final int MAX_DEFAULT_RETURN_COUNT = 1;

    /**
     * Default number of lines of which method/ctor body may consist to be
     * skipped by check.
     */
    private static final int DEFAULT_LINES_LIMIT = 20;

    /**
     * Default minimum "return" statement depth when current "return statement"
     * will be skipped by check.
     */
    private static final int DEFAULT_RETURN_DEPTH_LIMIT = 4;

    /**
     * Number which defines, how many lines of code on the top of current
     * processed method/ctor will be ignored by check. (It is equal to max
     * allowed distance (in rows) from the line with current method`s opening
     * brace to "return" statement which will be ignored by check).
     */
    private static final int DEFAULT_RETURN_ROW_DISTANCE_LIMIT = 5;
    
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
    private int mMaxReturnCount = MAX_DEFAULT_RETURN_COUNT;

    /**
     * Limit the number of lines of which method/ctor body may consist to be
     * skipped by check. If method/ctor has the lines number greater than this
     * limit, it will be processed. 20 by default.
     */
    private int mLinesLimit = DEFAULT_LINES_LIMIT;

    /**
     * Minimum "return" statement depth to be skipped by check.
     * Option to ignore methods/ctors that have return statement(s) with depth
     * lesser than N levels(scopes): 4 by default.
     */
    private int mReturnDepthLimit = DEFAULT_RETURN_DEPTH_LIMIT;

    /**
     * Option to ignore "empty" return statements in void methods and ctors.
     * 'False' by default.
     */
    private boolean mIgnoreEmptyReturns;

    /**
     * Number which defines, how many lines of code on the top of current
     * processed method/ctor will be ignored by check. (It is equal to max
     * allowed distance (in rows) from the line with current method`s opening
     * brace to "return" statement which will be ignored by check). 5 by
     * default.
     */
    private int mRowsToIgnoreCount = DEFAULT_RETURN_ROW_DISTANCE_LIMIT;

    /**
     * The current "return" literal DetailAST node for given method/ctor body
     * which does not conform to the verification.
     */
    private DetailAST mCurReturnLiteral;

    /**
     * DetailAST node is pointing to current method definition is being
     * processed.
     */
    private DetailAST mCurMethodDefNode;

    /**
     * Gets maximum allowed "return" literals count per method/ctor.
     * @return the current "maxReturnCount" property value is used by this
     *         check.
     * @see ReturnCountExtendedCheck#mMaxReturnCount
     */
    public int getMaxReturnCount()
    {
        return mMaxReturnCount;
    }

    /**
     * Sets maximum allowed "return" literals count per method/ctor.
     * @param aMaxReturnCount - the new maximum allowed "return" literals value.
     * @see ReturnCountExtendedCheck#mMaxReturnCount
     */
    public void setMaxReturnCount(int aMaxReturnCount)
    {
        mMaxReturnCount = aMaxReturnCount;
    }

    /**
     * Gets the number of lines of which method/ctor body may consist to be
     * skipped by check.
     * @return the current "linesLimit" property value is used by this check.
     * @see ReturnCountExtendedCheck#mLinesLimit
     */
    public int getLinesLimit()
    {
        return mLinesLimit;
    }

    /**
     * Sets the number of lines of which method/ctor body may consist to be
     * skipped by check.
     * @param aLinesLimit - the new value of maximum method/ctor body linelentgh
     * to be skipped.
     * @see ReturnCountExtendedCheck#mLinesLimit
     */
    public void setLinesLimit(int aLinesLimit)
    {
        mLinesLimit = aLinesLimit;
    }

    /**
     * Gets the minimum "return" statement depth with that it will be skipped by
     * check.
     * @return the current "maxReturnDepth" property value is used by this
     *         check.
     * @see ReturnDepthCheck#mReturnDepthLimit
     */
    public int getReturnDepthLimit()
    {
        return mReturnDepthLimit;
    }

    /**
     * Sets the minimum "return" statement depth with that it will be skipped by
     * check.
     * @param aReturnDepthLimit
     *        - the new
     * @see ReturnDepthCheck#mReturnDepthLimit
     */
    public void setReturnDepthLimit(int aReturnDepthLimit)
    {
        mReturnDepthLimit = aReturnDepthLimit;
    }

    /**
     * Gets the "ignoring empty return statements in void methods and ctors"
     * option state.
     * @return the current "allowEmptyReturns" property value.
     * @see ReturnCountExtendedCheck#mIgnoreEmptyReturns
     */
    public boolean getIgnoreEmptyReturns()
    {
        return mIgnoreEmptyReturns;
    }

    /**
     * Sets the "ignoring empty return statements in void methods and ctors"
     * option state.
     * @param aIgnoreEmptyReturns
     *        the new "allowEmptyReturns" property value.
     * @see ReturnCountExtendedCheck#mIgnoreEmptyReturns
     */
    public void setIgnoreEmptyReturns(boolean aIgnoreEmptyReturns)
    {
        mIgnoreEmptyReturns = aIgnoreEmptyReturns;
    }

    /**
     * Gets the count of code lines on the top of current
     * processed method/ctor that will be ignored by check.
     * @return the current "rowsToIgnoreCount" property value.
     * @see ReturnCountExtendedCheck#mRowsToIgnoreCount
     */
    public int getRowsToIgnoreCount()
    {
        return mRowsToIgnoreCount;
    }

    /**
     * Sets the count of code lines on the top of current
     * processed method/ctor that will be ignored by check.
     * @param aRowsToIgnoreCount
     *        the new "rowsToIgnoreCount" property value.
     * @see ReturnCountExtendedCheck#mRowsToIgnoreCount
     */
    public void setRowsToIgnoreCount(int aRowsToIgnoreCount)
    {
        mRowsToIgnoreCount = aRowsToIgnoreCount;
    }


    @Override
    public int[] getDefaultTokens()
    {
        return new int[] {TokenTypes.METHOD_DEF, TokenTypes.CTOR_DEF, };
    }

    @Override
    public void visitToken(final DetailAST aMethodDefNode)
    {
        mCurMethodDefNode = aMethodDefNode;

        final DetailAST openingBrace = aMethodDefNode
                .findFirstToken(TokenTypes.SLIST);

        if (openingBrace != null) {
            final DetailAST closingBrace = openingBrace.getLastChild();

            final int curMethodLinesCount = getLinesCount(openingBrace,
                    closingBrace);

            if (curMethodLinesCount >= mLinesLimit) {

                final int mCurReturnCount = getReturnCount(openingBrace);

                if (mCurReturnCount > mMaxReturnCount) {
                    final String mKey = (aMethodDefNode.getType()
                            == TokenTypes.METHOD_DEF) ? mKeyMethod : mKeyCtor;

                    log(mCurReturnLiteral.getLineNo(), mKey,
                            getMethodName(aMethodDefNode), mCurReturnCount,
                            mMaxReturnCount);
                }
            }
        }
    }

    /**
     * Gets the "return" statements count for given method/ctor and saves the
     * last "return" statement DetailAST node for given method/ctor body. Uses
     * an iterative algorithm.
     * @param aMethodOpeningBrace
     *        a DetailAST node that points to the current method`s opening
     *        brace.
     * @return "return" literals count for given method.
     */
    private int getReturnCount(final DetailAST aMethodOpeningBrace)
    {
        int result = 0;

        DetailAST curNode = aMethodOpeningBrace;

        while (curNode != null) {

            // before new node visiting
            if (curNode.getType() == TokenTypes.RCURLY
                    && curNode.getParent() == aMethodOpeningBrace)
            {
                break; // stop at closing brace
            }
            else {
                if (curNode.getType() == TokenTypes.LITERAL_RETURN
                        && isReturnDepthBad(curNode)
                        && !(mIgnoreEmptyReturns
                        && isReturnStatementEmpty(curNode))
                        && getReturnPositionRowNumber(aMethodOpeningBrace,
                                curNode) > mRowsToIgnoreCount)
                {
                    result++;
                    mCurReturnLiteral = curNode;
                }
            }

            // after new node visiting
            DetailAST toVisit = curNode.getFirstChild();

            final int type = curNode.getType();
            // skip nested methods (UI listeners, Runnable.run(), etc.)
            if (type == TokenTypes.METHOD_DEF
                  || type == TokenTypes.CLASS_DEF) // skip anonimous classes
            {
                toVisit = curNode.getNextSibling();
            }

            while ((curNode != null) && (toVisit == null)) {
                // leave the visited Node
                toVisit = curNode.getNextSibling();
                if (toVisit == null) {
                    curNode = curNode.getParent();
                }
            }
            curNode = toVisit;
        }
        return result;
    }

    /**
     * Checks that current processed "return" statement depth level is less than
     * specified depth limit.
     * @param aReturnNode
     *        the DetailAST node is pointing to the current "return" statement
     *        is being processed.
     * @return true if current processed "return" statement depth level is less
     *         than specified "return" statement depth limit and false
     *         otherwise.
     * @see ReturnCountExtendedCheck#mReturnDepthLimit
     */
    private boolean isReturnDepthBad(DetailAST aReturnNode)
    {
        return getDepth(mCurMethodDefNode, aReturnNode) < mReturnDepthLimit;
    }

    /**
     * Checks that current processed "return" statement is "empty" (checks that
     * current processed "return" statement is located in void method or ctor
     * and has not any expression).
     * @param aReturnNode
     *        the DetailAST node is pointing to the current "return" statement.
     *        is being processed.
     * @return true if current processed "return" statement is empty.
     */
    private static boolean isReturnStatementEmpty(DetailAST aReturnNode)
    {
        boolean result = false;
        final DetailAST returnChildNode = aReturnNode.getFirstChild();
        if (returnChildNode != null
                && returnChildNode.getType() == TokenTypes.SEMI)
        {
            result = true;
        }
        return result;
    }

    /**
     * Gets the depth level of given "return" statement. There are few supported
     * coding blocks when depth counting: "if-else", "for", "while"/"do-while"
     * and "switch".
     * @param aMethodDefNode
     *        a DetailAST node that points to the current method`s definition.
     * @param aReturnStmtNode
     *        given "return" statement node.
     * @return the depth of given
     */
    private static int getDepth(DetailAST aMethodDefNode,
            DetailAST aReturnStmtNode)
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
                    || type == TokenTypes.LITERAL_TRY)
            {
                result++;
            }
        }
        return result;
    }

    /**
     * Gets the name of given method by DetailAST node is pointing to it`s
     * definition.
     * @param aMethodDefNode
     *        a DetailAST node that points to the current method`s definition.
     * @return the method name.
     */
    private static String getMethodName(DetailAST aMethodDefNode)
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
     * Gets the linelength of given method/ctor body. Line which contains method
     * opening or method closing brace will not be counted.
     * @param aMethodOpeningBrace
     *        a DetailAST node that points to the current method`s opening
     *        brace.
     * @param aMethodClosingBrace
     *        a DetailAST node that points to the current method`s opening
     *        brace.
     * @return - 0 if method/ctor 'open' and 'close" braces are in the same line
     *         braces;<br>
     *         - Method/ctor body linelenght otherwise.
     */
    private static int getLinesCount(DetailAST aMethodOpeningBrace,
            DetailAST aMethodClosingBrace)
    {
        final int closingBraceLineNo = aMethodClosingBrace.getLineNo();
        final int openingBraceLineNo = aMethodOpeningBrace.getLineNo();
        if (closingBraceLineNo == openingBraceLineNo) {
            return 0;
        }
        else {
            return closingBraceLineNo - openingBraceLineNo - 1;
        }
    }

    /**
     * Gets the line number for given "return" statement in current processed
     * method/ctor.
     * @param aMethodOpeningBrace
     *        opening brace for the current method/ctor is being processed by
     *        check.
     * @param aReturnStmtNode
     *        - DetailAST node is pointing to the current processed "return"
     *        statement.
     * @return Distance (in rows) from the line with current method`s opening
     *         brace to the given "return" statement.
     */
    private static int getReturnPositionRowNumber(DetailAST aMethodOpeningBrace,
            DetailAST aReturnStmtNode)
    {
        final int openingBraceLineNo = aMethodOpeningBrace.getLineNo();
        final int returnStmtLineNo = aReturnStmtNode.getLineNo();
        if (returnStmtLineNo == openingBraceLineNo) {
            return 0;
        }
        else {
            return returnStmtLineNo - openingBraceLineNo;
        }
    }

    /**
     * Gets all the children one level below on the current DetailAST parent
     * node.
     * @param aNode
     *        Current parent node.
     * @return The list of children one level below on the current parent node.
     */
    private static List<DetailAST> getChildren(final DetailAST aNode)
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
