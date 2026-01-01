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

import java.util.LinkedList;
import java.util.List;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Restricts the number of ternary operators in expression to a specific limit.<br><br>
 * <b>Rationale:</b> This Check helps to improve code readability by pointing developer on<br>
 * expressions which contain more than user-defined count of ternary operators.<br><br>
 * It points to complicated ternary
 * <a href="http://docs.oracle.com/javase/tutorial/java/nutsandbolts/expressions.html">
 * expressions</a>.
 * Reason:<br>
 * - Complicated ternary expressions are not easy to read.<br>
 * - Complicated ternary expressions could lead to ambiguous result if user<br>
 * does not know Java's operators priority well, e.g.:<br>
 *
 * <pre>
 * String str = null;
 * String x = str != null ? "A" : "B" + str == null ? "C" : "D";
 * System.out.println(x);
 * </pre>
 *
 * <p>
 * Output for code above is "D", but more obvious would be "BC".
 * </p>
 * <p>
 * Check has following properties:
 * </p>
 * <ul>
 * <li><b>maxTernaryPerExpressionCount</b> - limit of ternary operators per
 * expression<br>
 * </li>
 * <li><b>ignoreTernaryOperatorsInBraces</b> - if true Check will ignore ternary operators<br>
 * in braces (braces explicitly set priority level)<br>
 * </li>
 * <li><b>ignoreIsolatedTernaryOnLine</b> - if true Check will ignore one line ternary operators,
 * <br>
 * if only it is places in line alone.<br>
 * </li>
 * </ul>
 * Options <b>ignoreTernaryOperatorsInBraces</b> and <b>ignoreIsolatedTernaryOnLine</b> can<br>
 * make Check less strict, e.g.:<br>
 * Using <b>ignoreTernaryOperatorsInBraces</b> option (value = <b>true</b>)<br>
 * does not put violation on code below:<br>
 *
 * <pre>
 * callString = "{? = call " +
 *   (StringUtils.hasLength(catalogNameToUse)
 *   ? catalogNameToUse + "." : "") +
 *   (StringUtils.hasLength(schemaNameToUse)
 *   ? schemaNameToUse + "." : "") +
 *   procedureNameToUse + "(";
 * </pre>
 *
 * <p>
 * When using <b>ignoreIsolatedTernaryOnLine</b> (value = <b>true</b>), even without<br>
 * <b>ignoreTernaryOperatorsInBraces</b> option Check won't warn on code below:
 * </p>
 *
 * <pre>
 * int a = (d == 5) ? d : f
 *   +
 *   ((d == 6) ? g : k);
 * </pre>
 *
 * @author <a href="mailto:nesterenko-aleksey@list.ru">Aleksey Nesterenko</a>
 * @since 1.12.0
 */

public class TernaryPerExpressionCountCheck extends AbstractCheck {

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY = "ternary.per.expression.count";

    /** Default limit of ternary operators per expression. */
    private static final int DEFAULT_MAX_TERNARY_PER_EXPRESSION_COUNT = 1;

    /** Limit of ternary operators per expression. */
    private int maxTernaryPerExpressionCount = DEFAULT_MAX_TERNARY_PER_EXPRESSION_COUNT;

    /**
     * If true Check will ignore ternary operators in braces (braces explicitly
     * set priority level).
     */
    private boolean ignoreTernaryOperatorsInBraces = true;

    /**
     * If true Check will ignore one line ternary operators, if only it is
     * places in line alone.
     */
    private boolean ignoreIsolatedTernaryOnLine = true;

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.EXPR,
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

    /**
     * Sets the maximum number of ternary operators, default value = 1.
     *
     * @param maxTernaryPerExpressionCount
     *            Number of ternary operators per expression
     * @throws IllegalArgumentException when maxTernaryPerExpressionCount less zero
     */
    public void setMaxTernaryPerExpressionCount(int maxTernaryPerExpressionCount) {
        if (maxTernaryPerExpressionCount < 0) {
            throw new IllegalArgumentException("Value should be 0 or more then 0");
        }
        this.maxTernaryPerExpressionCount = maxTernaryPerExpressionCount;
    }

    /**
     * Sets parameter to ignore ternary operators in braces, default value =
     * true.
     *
     * @param ignoreTernaryOperatorsInBraces ignore ternary operators in braces
     */
    public void setIgnoreTernaryOperatorsInBraces(boolean ignoreTernaryOperatorsInBraces) {
        this.ignoreTernaryOperatorsInBraces = ignoreTernaryOperatorsInBraces;
    }

    /**
     * Sets parameter to ignore expressions in case if ternary operator is isolated in line.
     *
     * @param ignoreIsolatedTernaryOnLine ignore expressions in case if ternary
     *     operator is isolated in line
     */
    public void setIgnoreIsolatedTernaryOnLine(boolean ignoreIsolatedTernaryOnLine) {
        this.ignoreIsolatedTernaryOnLine = ignoreIsolatedTernaryOnLine;
    }

    @Override
    public void visitToken(DetailAST expressionNode) {
        final List<DetailAST> questionNodes = getQuestionNodes(expressionNode);

        if (questionNodes.size() > maxTernaryPerExpressionCount) {
            final DetailAST firstQuestionNode = questionNodes.get(0);
            log(firstQuestionNode, MSG_KEY, maxTernaryPerExpressionCount);
        }
    }

    /**
     * Puts question nodes from current expression node into the list.
     *
     * @param expressionNode
     *          Globally considering expression node
     * @return
     *          List of question nodes
     */
    private List<DetailAST> getQuestionNodes(DetailAST expressionNode) {
        final List<DetailAST> questionNodes = new LinkedList<>();

        DetailAST currentNode = expressionNode;

        do {
            currentNode = getNextNode(expressionNode, currentNode);
            if (currentNode != null
                    && currentNode.getType() == TokenTypes.QUESTION
                    && !isSkipTernaryOperator(currentNode)) {
                questionNodes.add(currentNode);
            }
        } while (currentNode != null);

        return questionNodes;
    }

    /**
     * Checks if options <b>ignoreTernaryInBraces</b> or
     * <b>ignoreOneTernaryPerLine</b> were set, hence, count ternary
     * operators in current expression or not.
     *
     * @param questionAST The token to examine.
     * @return true if can skip ternary operator.
     */
    private boolean isSkipTernaryOperator(DetailAST questionAST) {
        return ignoreTernaryOperatorsInBraces && isTernaryOperatorInBraces(questionAST)
                || ignoreIsolatedTernaryOnLine && isIsolatedTernaryOnLine(questionAST);
    }

    /**
     * Checks ternary operator if it is in braces, which are explicitly setting
     * the priority level.
     *
     * @param questionAST The token to examine.
     * @return true if ternary operator is in braces.
     */
    private static boolean isTernaryOperatorInBraces(DetailAST questionAST) {
        return questionAST.getPreviousSibling() != null
                && questionAST.getPreviousSibling().getType() == TokenTypes.LPAREN;
    }

    /**
     * Checks if there's one ternary operator per line.
     *
     * @param questionAST The token to examine.
     * @return true if ternary is isolated on line.
     */
    private boolean isIsolatedTernaryOnLine(DetailAST questionAST) {
        final int lineNo = questionAST.getLineNo() - 1;
        final String line = getFileContents().getText().get(lineNo);

        return isSingleTernaryLine(line, lineNo);
    }

    /**
     * Checks line parameter on containing more than 1 ternary operator.
     *
     * @param line The line to examine.
     * @param lineNo The line number of the line.
     * @return true if line is single ternary.
     */
    private boolean isSingleTernaryLine(String line, int lineNo) {
        int questionsPerLine = 0;
        final char[] charArrayFromLine = line.toCharArray();
        for (int index = 0; index < line.length(); index++) {
            final char currentSymbol = charArrayFromLine[index];
            if (currentSymbol == '?' && !getFileContents().hasIntersectionWithComment(lineNo + 1,
                    index, lineNo + 1, index)) {
                questionsPerLine++;
            }
            if (questionsPerLine > 1) {
                break;
            }
        }

        return questionsPerLine == 1;
    }

    /**
     * Gets the next node of a syntactical tree (child of a current node or
     * sibling of a current node, or sibling of a parent of a current node).
     *
     * @param expressionNode
     *            Globally considering expression node
     * @param node
     *            Current node of syntactical tree
     * @return Next node after bypassing
     */
    private static DetailAST getNextNode(DetailAST expressionNode,
            DetailAST node) {
        DetailAST currentNode = node;
        DetailAST toVisit = currentNode.getFirstChild();
        while (toVisit == null && currentNode != expressionNode) {
            toVisit = currentNode.getNextSibling();
            if (toVisit == null) {
                currentNode = currentNode.getParent();
            }
        }

        return toVisit;
    }

}
