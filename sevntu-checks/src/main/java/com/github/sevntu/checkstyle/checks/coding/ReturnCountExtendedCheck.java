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

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.coding.ReturnCountCheck;

/**
 * Checks that method/ctor "return" literal count is not greater than the given value ("maxReturnCount" property).<br>
 * <br>
 * Rationale:<br>
 * <br>
 * One return per method is a good practice as its ease understanding of method logic. <br>
 * <br>
 * Reasoning is that:
 * <dl>
 * <li>It is easier to understand control flow when you know exactly where the method returns.
 * <li>Methods with 2-3 or many "return" statements are much more difficult to understand, debug and refactor.
 * </dl>
 * Setting up the check options will make it to ignore:
 * <ol>
 * <li>Methods by name ("ignoreMethodsNames" property). Note, that the "ignoreMethodsNames" property type is a RegExp:
 * using this property you can list the names of ignored methods separated by comma (but you can also use '|' to
 * separate different method names in usual for RegExp style).</li>
 * <li>Methods which linelength less than given value ("linesLimit" property).
 * <li>"return" statements which depth is greater or equal to the given value ("returnDepthLimit" property). There are
 * few supported <br>
 * coding blocks when depth counting: "if-else", "for", "while"/"do-while" and "switch".
 * <li>"Empty" return statements = return statements in void methods and ctors that have not any expression
 * ("ignoreEmptyReturns" property).
 * <li>Return statements, which are located in the top lines of method/ctor (you can specify the count of top
 * method/ctor lines that will be ignored using "rowsToIgnoreCount" property).
 * </ol>
 * So, this is much improved version of the existing {@link ReturnCountCheck}. <br>
 * <br>
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public class ReturnCountExtendedCheck extends Check
{

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String WARNING_MSG_KEY_METHOD =
            "return.count.extended.method";

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String WARNING_MSG_KEY_CTOR =
            "return.count.extended.ctor";

    /**
     * Default maximum allowed "return" literals count per method/ctor.
     */
    private static final int DEFAULT_MAX_RETURN_COUNT = 1;

    /**
     * Default number of lines of which method/ctor body may consist to be
     * skipped by check.
     */
    private static final int DEFAULT_IGNORE_METHOD_LINES_COUNT = 20;

    /**
     * Default minimum "return" statement depth when current "return statement"
     * will be skipped by check.
     */
    private static final int DEFAULT_MIN_IGNORE_RETURN_DEPTH = 4;

    /**
     * Number which defines, how many lines of code on the top of current
     * processed method/ctor will be ignored by check.
     */
    private static final int DEFAULT_TOP_LINES_TO_IGNORE_COUNT = 5;

    /**
	 * List contains RegExp patterns for methods' names which would be ignored by check.
	 */
    private Set<String> mIgnoreMethodsNames = new HashSet<String>();

    /**
     * Maximum allowed "return" literals count per method/ctor (1 by default).
     */
    private int mMaxReturnCount = DEFAULT_MAX_RETURN_COUNT;

    /**
     * Maximum number of lines of which method/ctor body may consist to be
     * skipped by check. 20 by default.
     */
    private int mIgnoreMethodLinesCount = DEFAULT_IGNORE_METHOD_LINES_COUNT;

    /**
     * Minimum "return" statement depth to be skipped by check. 4 by default.
     */
    private int mMinIgnoreReturnDepth = DEFAULT_MIN_IGNORE_RETURN_DEPTH;

    /**
     * Option to ignore "empty" return statements in void methods and ctors.
     * "true" by default.
     */
    private boolean mIgnoreEmptyReturns = true;

    /**
     * Number which defines, how many lines of code on the top of each
     * processed method/ctor will be ignored by check. 5 by default.
     */
    private int mTopLinesToIgnoreCount = DEFAULT_TOP_LINES_TO_IGNORE_COUNT;

    /**
	 * Sets the RegExp patterns for methods' names which would be ignored by check.
	 * 
	 * @param aIgnoreMethodNames
	 *            list of the RegExp patterns for methods' names which should be ignored by check
	 */
    public void setIgnoreMethodsNames(String [] aIgnoreMethodNames)
    {
        mIgnoreMethodsNames.clear();
        if (aIgnoreMethodNames != null) {
            for (String name : aIgnoreMethodNames) {
                mIgnoreMethodsNames.add(name);
            }
        }
    }

    /**
     * Sets maximum allowed "return" literals count per method/ctor.
     * @param aMaxReturnCount - the new "maxReturnCount" property value.
     * @see ReturnCountExtendedCheck#mMaxReturnCount
     */
    public void setMaxReturnCount(int aMaxReturnCount)
    {
        mMaxReturnCount = aMaxReturnCount;
    }

    /**
     * Sets the maximum number of lines of which method/ctor body may consist to
     * be skipped by check.
     * @param aIgnoreMethodLinesCount
     *        - the new value of "ignoreMethodLinesCount" property.
     * @see ReturnCountExtendedCheck#mIgnoreMethodLinesCount
     */
    public void setIgnoreMethodLinesCount(int aIgnoreMethodLinesCount)
    {
        mIgnoreMethodLinesCount = aIgnoreMethodLinesCount;
    }

    /**
     * Sets the minimum "return" statement depth with that will be skipped by
     * check.
     * @param aMinIgnoreReturnDepth
     *        - the new "minIgnoreReturnDepth" property value.
     * @see ReturnDepthCheck#mMinIgnoreReturnDepth
     */
    public void setMinIgnoreReturnDepth(int aMinIgnoreReturnDepth)
    {
        mMinIgnoreReturnDepth = aMinIgnoreReturnDepth;
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
     * Sets the count of code lines on the top of each
     * processed method/ctor that will be ignored by check.
     * @param aTopLinesToIgnoreCount
     *        the new "rowsToIgnoreCount" property value.
     * @see ReturnCountExtendedCheck#mTopLinesToIgnoreCount
     */
    public void setTopLinesToIgnoreCount(int aTopLinesToIgnoreCount)
    {
        mTopLinesToIgnoreCount = aTopLinesToIgnoreCount;
    }

    /**
     * Creates the new check instance.
     */
    public ReturnCountExtendedCheck()
    {
        mIgnoreMethodsNames.add("equals");
    }

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] {TokenTypes.METHOD_DEF, TokenTypes.CTOR_DEF, };
    }

    @Override
    public void visitToken(final DetailAST aMethodDefNode)
    {
        final DetailAST openingBrace = aMethodDefNode
                .findFirstToken(TokenTypes.SLIST);
        String methodName = getMethodName(aMethodDefNode);
        if (openingBrace != null && (methodName == null 
                || !matches(methodName, mIgnoreMethodsNames)))
        {
            final DetailAST closingBrace = openingBrace.getLastChild();

            int curMethodLinesCount = getLinesCount(openingBrace,
                    closingBrace);

            if (curMethodLinesCount != 0) {
                curMethodLinesCount--;
            }

            if (curMethodLinesCount >= mIgnoreMethodLinesCount) {

                final int mCurReturnCount = getReturnCount(aMethodDefNode,
                        openingBrace);

                if (mCurReturnCount > mMaxReturnCount) {
                    final String mKey = (aMethodDefNode.getType()
                            == TokenTypes.METHOD_DEF)
                            ? WARNING_MSG_KEY_METHOD : WARNING_MSG_KEY_CTOR;

                    final DetailAST methodNameToken = aMethodDefNode
                            .findFirstToken(TokenTypes.IDENT);

                    log(methodNameToken, mKey,
                            methodName, mCurReturnCount,
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
     * @param aMethodDefNode
     *        DetailAST node is pointing to current method definition is being
     *        processed.
     * @return "return" literals count for given method.
     */
    private int getReturnCount(final DetailAST aMethodDefNode,
            final DetailAST aMethodOpeningBrace)
    {
        int result = 0;

        DetailAST curNode = aMethodOpeningBrace;

        while (curNode != null) {

            // before node visiting
            if (curNode.getType() == TokenTypes.RCURLY
                    && curNode.getParent() == aMethodOpeningBrace)
            {
                break; // stop at closing brace
            }
            else {
                if (curNode.getType() == TokenTypes.LITERAL_RETURN
                        && getDepth(aMethodDefNode
                                , curNode) < mMinIgnoreReturnDepth
                        && shouldEmptyReturnStatementBeCounted(curNode)
                        && getLinesCount(aMethodOpeningBrace,
                                curNode) > mTopLinesToIgnoreCount)
                {
                    result++;
                }
            }

            // before node leaving
            DetailAST nextNode = curNode.getFirstChild();

            final int type = curNode.getType();
            // skip nested methods (UI listeners, Runnable.run(), etc.)
            if (type == TokenTypes.METHOD_DEF
                  || type == TokenTypes.CLASS_DEF) // skip anonimous classes
            {
                nextNode = curNode.getNextSibling();
            }

            while ((curNode != null) && (nextNode == null)) {
                // leave the visited Node
                nextNode = curNode.getNextSibling();
                if (nextNode == null) {
                    curNode = curNode.getParent();
                }
            }
            curNode = nextNode;
        }
        return result;
    }

    /**
     * Checks that the current processed "return" statement is "empty" and
     * should to be counted.
     * @param aReturnNode
     *        the DetailAST node is pointing to the current "return" statement.
     *        is being processed.
     * @return true if current processed "return" statement is empty or if
     *         mIgnoreEmptyReturns option has "false" value.
     */
    private boolean shouldEmptyReturnStatementBeCounted(DetailAST aReturnNode)
    {
        final DetailAST returnChildNode = aReturnNode.getFirstChild();
        return !(mIgnoreEmptyReturns && returnChildNode != null
                && returnChildNode.getType() == TokenTypes.SEMI);
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
     * Gets the name of given method by DetailAST node is pointing to desired
     * method definition.
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
     * Gets the line count between the two DetailASTs which are related to the
     * given "begin" and "end" tokens.
     * @param aBeginAst
     *        the "begin" token AST node.
     * @param aEndAST
     *        the "end" token AST node.
     * @return the line count between "begin" and "end" tokens.
     */
    private static int getLinesCount(DetailAST aBeginAst, DetailAST aEndAST)
    {
        return aEndAST.getLineNo() - aBeginAst.getLineNo();
    }

    /**
     * Gets all the children which are one level below on the current DetailAST
     * parent node.
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
    
    /**
	 * Matches string to given list of RegExp patterns.
	 * 
	 * @param string
	 *            String to be matched.
	 * @param patterns
	 *            Collection of RegExp patterns to match with.
	 * @return true if given string could be fully matched by one of given patterns, false otherwise
	 */
	private static boolean matches(String string, Collection<String> patterns) {
        boolean result = false;
        if (string != null && patterns != null && patterns.size() > 0) {
            for (String pattern : patterns) {
                if (string.matches(pattern)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

}
