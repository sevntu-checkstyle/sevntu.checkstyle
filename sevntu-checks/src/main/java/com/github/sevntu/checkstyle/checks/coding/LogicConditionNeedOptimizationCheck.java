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

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * This check prevents the placement of local variables and fields after calling
 * methods and instanceof in '&amp;&amp;' and '||' conditions.
 * </p>
 * <p>
 * For example: if(getProperty() &amp;&amp; property) ==&gt; if(property &amp;&amp; getProperty()),
 * and similarly for any expression.
 * </p>
 *
 * @author <a href="mailto:IliaDubinin91@gmail.com">Ilia Dubinin</a>
 * @since 1.8.0
 */
public class LogicConditionNeedOptimizationCheck extends AbstractCheck {

    /**
     * The key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY = "logic.condition.need.optimization";

    /** Integer for the 3rd position. */
    private static final int THIRD_POSITION = 3;
    /** Number of operands positions in start/stop array. */
    private static final int OPERAND_SIZE = 4;

    @Override
    public int[] getDefaultTokens() {
        return new int[] {TokenTypes.LAND, TokenTypes.LOR };
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
    public void visitToken(DetailAST detailAST) {
        if (needOptimization(detailAST)) {
            log(detailAST, MSG_KEY,
                    detailAST.getText(), detailAST.getLineNo(),
                    detailAST.getColumnNo());
        }
    }

    /**
     * <p>
     * Return true, if current expression part need optimization.
     * </p>
     *
     * @param logicNode
     *        - current logic operator node
     * @return - boolean variable
     */
    private static boolean needOptimization(DetailAST logicNode) {
        final DetailAST[] operands = getOperands(logicNode);
        final boolean firstInstanceOf = branchContains(operands, 1, TokenTypes.LITERAL_INSTANCEOF);
        final boolean secondTypeCast = branchContains(operands, 2, TokenTypes.TYPECAST);
        final boolean result;

        if (firstInstanceOf && secondTypeCast) {
            result = false;
        }
        else {
            result = !branchContains(operands, 2, TokenTypes.METHOD_CALL)
                && !branchContains(operands, 2, TokenTypes.LITERAL_INSTANCEOF)
                && (firstInstanceOf
                        || branchContains(operands, 1, TokenTypes.METHOD_CALL));
        }

        return result;
    }

    /**
     * <p>
     * Return operands of current logic operator.
     * </p>
     *
     * @param logicNode - current logic operator
     * @return operands
     */
    private static DetailAST[] getOperands(DetailAST logicNode) {
        final DetailAST[] results = new DetailAST[OPERAND_SIZE];
        DetailAST node = logicNode.getFirstChild();

        // start of first
        results[0] = node;

        int parenthesis = 0;

        do {
            if (node.getType() == TokenTypes.LPAREN && node.getFirstChild() == null) {
                parenthesis++;
            }
            else {
                if (node.getType() == TokenTypes.RPAREN) {
                    parenthesis--;
                }
                if (parenthesis == 0) {
                    // end of first
                    results[1] = node;
                }
            }

            node = node.getNextSibling();
        } while (parenthesis > 0);

        // start of second
        results[2] = node;
        results[THIRD_POSITION] = logicNode.getLastChild();

        return results;
    }

    /**
     * Checks if the node range contains a token of the provided type.
     *
     * @param operands the list operands in order of start and stop
     * @param setNumber to retrieve the 1st or 2nd operand
     * @param type a TokenType
     * @return true if and only if the node range
     *     contains a token of type {@code type}.
     */
    private static boolean branchContains(DetailAST[] operands, int setNumber, int type) {
        final boolean result;

        if (setNumber == 1) {
            result = branchContains(operands[0], operands[1], type);
        }
        else {
            result = branchContains(operands[2], operands[THIRD_POSITION], type);
        }

        return result;
    }

    /**
     * Checks if the node range contains a token of the provided type.
     *
     * @param start the token to start checking with (inclusive)
     * @param end the token to end with (inclusive)
     * @param type a TokenType
     * @return true if and only if the node range
     *     contains a token of type {@code type}.
     */
    private static boolean branchContains(DetailAST start, DetailAST end, int type) {
        boolean result;
        DetailAST current = start;

        while (true) {
            result = current.branchContains(type);

            if (current == end || result) {
                break;
            }

            current = current.getNextSibling();
        }

        return result;
    }

}
