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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.coding.ReturnCountCheck;

/**
 * Checks that method/ctor "return" literal count is not greater than the given value
 * ("maxReturnCount" property).<br>
 * <br>
 * Rationale:<br>
 * <br>
 * One return per method is a good practice as its ease understanding of method logic. <br>
 * <br>
 * Reasoning is that:
 * <ul>
 * <li>It is easier to understand control flow when you know exactly where the method returns.
 * <li>Methods with 2-3 or many "return" statements are much more difficult to understand,
 * debug and refactor.
 * </ul>
 * Setting up the check options will make it to ignore:
 * <ol>
 * <li>Methods by name ("ignoreMethodsNames" property). Note, that the "ignoreMethodsNames"
 * property type is a RegExp:
 * using this property you can list the names of ignored methods separated by comma (but you
 * can also use '|' to separate different method names in usual for RegExp style).
 * If the violation is on a lambda, since it has no method name, you can specify the string
 * {@code null} to ignore all lambda violations for now. It should be noted, that ignoring lambdas
 * this way may not always be supported as it is a hack and giving all lambdas the same name. It
 * could be changed if a better way to single out individual lambdas if found.
 * </li>
 * <li>Methods which linelength less than given value ("linesLimit" property).
 * <li>"return" statements which depth is greater or equal to the given value ("returnDepthLimit"
 * property). There are few supported <br>
 * coding blocks when depth counting: "if-else", "for", "while"/"do-while" and "switch".
 * <li>"Empty" return statements = return statements in void methods and ctors that have not
 * any expression ("ignoreEmptyReturns" property).
 * <li>Return statements, which are located in the top lines of method/ctor (you can specify
 * the count of top method/ctor lines that will be ignored using "rowsToIgnoreCount" property).
 * </ol>
 * So, this is much improved version of the existing {@link ReturnCountCheck}. <br>
 * <br>
 *
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 * @since 1.8.0
 */
public class ReturnCountExtendedCheck extends AbstractCheck {

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY_METHOD =
            "return.count.extended.method";

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY_CTOR =
            "return.count.extended.ctor";

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY_LAMBDA =
            "return.count.extended.lambda";

    /**
     * Default maximum allowed "return" literals count per method/ctor/lambda.
     */
    private static final int DEFAULT_MAX_RETURN_COUNT = 1;

    /**
     * Default number of lines of which method/ctor/lambda body may consist to be
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
     * processed method/ctor/lambda will be ignored by check.
     */
    private static final int DEFAULT_TOP_LINES_TO_IGNORE_COUNT = 5;

    /**
     * List contains RegExp patterns for methods' names which would be ignored by check.
     */
    private final Set<String> ignoreMethodsNames = new HashSet<>();

    /**
     * Maximum allowed "return" literals count per method/ctor/lambda (1 by default).
     */
    private int maxReturnCount = DEFAULT_MAX_RETURN_COUNT;

    /**
     * Maximum number of lines of which method/ctor/lambda body may consist to be
     * skipped by check. 20 by default.
     */
    private int ignoreMethodLinesCount = DEFAULT_IGNORE_METHOD_LINES_COUNT;

    /**
     * Minimum "return" statement depth to be skipped by check. 4 by default.
     */
    private int minIgnoreReturnDepth = DEFAULT_MIN_IGNORE_RETURN_DEPTH;

    /**
     * Option to ignore "empty" return statements in void methods and ctors and lambdas.
     * "true" by default.
     */
    private boolean ignoreEmptyReturns = true;

    /**
     * Number which defines, how many lines of code on the top of each
     * processed method/ctor/lambda will be ignored by check. 5 by default.
     */
    private int topLinesToIgnoreCount = DEFAULT_TOP_LINES_TO_IGNORE_COUNT;

    /**
     * Creates the new check instance.
     */
    public ReturnCountExtendedCheck() {
        ignoreMethodsNames.add("equals");
    }

    /**
     * Sets the RegExp patterns for methods' names which would be ignored by check.
     *
     * @param ignoreMethodNames
     *            list of the RegExp patterns for methods' names which should be ignored by check
     */
    public void setIgnoreMethodsNames(String... ignoreMethodNames) {
        ignoreMethodsNames.clear();
        if (ignoreMethodNames != null) {
            for (String name : ignoreMethodNames) {
                ignoreMethodsNames.add(name);
            }
        }
    }

    /**
     * Sets maximum allowed "return" literals count per method/ctor/lambda.
     *
     * @param maxReturnCount - the new "maxReturnCount" property value.
     * @see ReturnCountExtendedCheck#maxReturnCount
     */
    public void setMaxReturnCount(int maxReturnCount) {
        this.maxReturnCount = maxReturnCount;
    }

    /**
     * Sets the maximum number of lines of which method/ctor/lambda body may consist to
     * be skipped by check.
     *
     * @param ignoreMethodLinesCount
     *        - the new value of "ignoreMethodLinesCount" property.
     * @see ReturnCountExtendedCheck#ignoreMethodLinesCount
     */
    public void setIgnoreMethodLinesCount(int ignoreMethodLinesCount) {
        this.ignoreMethodLinesCount = ignoreMethodLinesCount;
    }

    /**
     * Sets the minimum "return" statement depth with that will be skipped by
     * check.
     *
     * @param minIgnoreReturnDepth
     *        - the new "minIgnoreReturnDepth" property value.
     */
    public void setMinIgnoreReturnDepth(int minIgnoreReturnDepth) {
        this.minIgnoreReturnDepth = minIgnoreReturnDepth;
    }

    /**
     * Sets the "ignoring empty return statements in void methods and ctors and lambdas"
     * option state.
     *
     * @param ignoreEmptyReturns
     *        the new "allowEmptyReturns" property value.
     * @see ReturnCountExtendedCheck#ignoreEmptyReturns
     */
    public void setIgnoreEmptyReturns(boolean ignoreEmptyReturns) {
        this.ignoreEmptyReturns = ignoreEmptyReturns;
    }

    /**
     * Sets the count of code lines on the top of each
     * processed method/ctor that will be ignored by check.
     *
     * @param topLinesToIgnoreCount
     *        the new "rowsToIgnoreCount" property value.
     * @see ReturnCountExtendedCheck#topLinesToIgnoreCount
     */
    public void setTopLinesToIgnoreCount(int topLinesToIgnoreCount) {
        this.topLinesToIgnoreCount = topLinesToIgnoreCount;
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.METHOD_DEF,
            TokenTypes.CTOR_DEF,
            TokenTypes.LAMBDA,
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
    public void visitToken(final DetailAST node) {
        final DetailAST openingBrace = node
                .findFirstToken(TokenTypes.SLIST);
        final String nodeName = getMethodName(node);
        if (openingBrace != null
                && !matches(nodeName, ignoreMethodsNames)) {
            final DetailAST closingBrace = openingBrace.getLastChild();

            int curMethodLinesCount = getLinesCount(openingBrace,
                    closingBrace);

            if (curMethodLinesCount != 0) {
                curMethodLinesCount--;
            }

            if (curMethodLinesCount >= ignoreMethodLinesCount) {
                final int mCurReturnCount = getReturnCount(node,
                        openingBrace);

                if (mCurReturnCount > maxReturnCount) {
                    logViolation(node, nodeName, mCurReturnCount);
                }
            }
        }
    }

    /**
     * Reports violation to user based on the parameters given.
     *
     * @param node The node that the violation is on.
     * @param nodeName The name given to the node.
     * @param mCurReturnCount The return count violation amount.
     */
    private void logViolation(DetailAST node, String nodeName, int mCurReturnCount) {
        if (node.getType() == TokenTypes.LAMBDA) {
            // lambdas have no name
            log(node, MSG_KEY_LAMBDA, mCurReturnCount, maxReturnCount);
        }
        else {
            final DetailAST nodeNameToken = node
                    .findFirstToken(TokenTypes.IDENT);
            final String mKey;

            if (node.getType() == TokenTypes.METHOD_DEF) {
                mKey = MSG_KEY_METHOD;
            }
            else {
                mKey = MSG_KEY_CTOR;
            }

            log(nodeNameToken, mKey,
                    nodeName, mCurReturnCount,
                    maxReturnCount);
        }
    }

    /**
     * Gets the "return" statements count for given method/ctor/lambda and saves the
     * last "return" statement DetailAST node for given method/ctor/lambda body. Uses
     * an iterative algorithm.
     *
     * @param methodDefNode
     *        DetailAST node is pointing to current method definition is being
     *        processed.
     * @param methodOpeningBrace
     *        a DetailAST node that points to the current method`s opening
     *        brace.
     * @return "return" literals count for given method.
     */
    private int getReturnCount(final DetailAST methodDefNode,
            final DetailAST methodOpeningBrace) {
        int result = 0;

        DetailAST curNode = methodOpeningBrace;

        // stop at closing brace
        while (curNode.getType() != TokenTypes.RCURLY
                || curNode.getParent() != methodOpeningBrace) {
            if (curNode.getType() == TokenTypes.LITERAL_RETURN
                    && getDepth(methodDefNode, curNode) < minIgnoreReturnDepth
                    && shouldEmptyReturnStatementBeCounted(curNode)
                    && getLinesCount(methodOpeningBrace,
                            curNode) > topLinesToIgnoreCount) {
                result++;
            }

            // before node leaving
            DetailAST nextNode = curNode.getFirstChild();

            final int type = curNode.getType();
            // skip nested methods (UI listeners, Runnable.run(), etc.)
            if (type == TokenTypes.METHOD_DEF
                  // skip anonymous classes
                  || type == TokenTypes.CLASS_DEF
                  // skip lambdas which is like an anonymous class/method
                  || type == TokenTypes.LAMBDA) {
                nextNode = curNode.getNextSibling();
            }

            while (nextNode == null) {
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
     *
     * @param returnNode
     *        the DetailAST node is pointing to the current "return" statement.
     *        is being processed.
     * @return true if current processed "return" statement is empty or if
     *         mIgnoreEmptyReturns option has "false" value.
     */
    private boolean shouldEmptyReturnStatementBeCounted(DetailAST returnNode) {
        final DetailAST returnChildNode = returnNode.getFirstChild();
        return !ignoreEmptyReturns
                || returnChildNode.getType() != TokenTypes.SEMI;
    }

    /**
     * Gets the depth level of given "return" statement. There are few supported
     * coding blocks when depth counting: "if-else", "for", "while"/"do-while"
     * and "switch".
     *
     * @param methodDefNode
     *        a DetailAST node that points to the current method`s definition.
     * @param returnStmtNode
     *        given "return" statement node.
     * @return the depth of given
     */
    private static int getDepth(DetailAST methodDefNode,
            DetailAST returnStmtNode) {
        int result = 0;

        DetailAST curNode = returnStmtNode;

        while (!curNode.equals(methodDefNode)) {
            curNode = curNode.getParent();
            final int type = curNode.getType();
            if (type == TokenTypes.LITERAL_IF
                    || type == TokenTypes.LITERAL_SWITCH
                    || type == TokenTypes.LITERAL_FOR
                    || type == TokenTypes.LITERAL_DO
                    || type == TokenTypes.LITERAL_WHILE
                    || type == TokenTypes.LITERAL_TRY) {
                result++;
            }
        }
        return result;
    }

    /**
     * Gets the name of given method by DetailAST node is pointing to desired
     * method definition.
     *
     * @param methodDefNode
     *        a DetailAST node that points to the current method`s definition.
     * @return the method name.
     */
    private static String getMethodName(DetailAST methodDefNode) {
        String result = null;
        final DetailAST ident = methodDefNode.findFirstToken(TokenTypes.IDENT);

        // lambdas don't have a name
        if (ident != null && methodDefNode.getType() != TokenTypes.LAMBDA) {
            result = ident.getText();
        }

        return result;
    }

    /**
     * Gets the line count between the two DetailASTs which are related to the
     * given "begin" and "end" tokens.
     *
     * @param beginAst
     *        the "begin" token AST node.
     * @param endAST
     *        the "end" token AST node.
     * @return the line count between "begin" and "end" tokens.
     */
    private static int getLinesCount(DetailAST beginAst, DetailAST endAST) {
        return endAST.getLineNo() - beginAst.getLineNo();
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
        String match = string;

        if (match == null) {
            match = "null";
        }

        boolean result = false;
        if (!patterns.isEmpty()) {
            for (String pattern : patterns) {
                if (match.matches(pattern)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

}
