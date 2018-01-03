////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2018 the original author or authors.
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

import java.util.Arrays;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.TokenUtils;

/**
 * If comparing values, C(C++) developers prefer to put the constant first in the equality check,
 * to prevent situations of assignment rather than equality checking.
 *
 * <p>
 * But in Java, in IF condition it is impossible to use assignment,
 * so that habit become unnecessary and do damage readability of code.
 * </p>
 *
 * <p>
 * In C(C++), comparison for null is tricky, and it is easy to write "=" instead of "==",
 * and no complication error will be but condition will work in different way
 * </p>
 *
 * <p>
 * Example:
 * <code>if (null == variable)</code>
 * rather than
 * <code>if (variable == null)</code>
 * because if you forget one (typo mistake) of the equal sign, you end up with
 * <code>if (variable = null)</code>
 * which assigns null to variable and IF always evaluate to true.
 * </p>
 *
 * @author Sergey Burtsev
 */
public class AvoidConstantAsFirstOperandInConditionCheck extends AbstractCheck {

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY = "avoid.constant.as.first.operand.in.condition";

    /**
     * Field is array of default target constant types.
     */
    private int[] targetConstantTypes = new int[] {
        TokenTypes.LITERAL_TRUE,
        TokenTypes.LITERAL_FALSE,
        TokenTypes.LITERAL_NULL,
        TokenTypes.NUM_INT,
        TokenTypes.NUM_FLOAT,
        TokenTypes.NUM_LONG,
        TokenTypes.NUM_DOUBLE,
    };

    /**
     * Set target constant types.
     *
     * @param targets target constant types
     */
    public void setTargetConstantTypes(String[] targets) {
        if (targets != null) {
            targetConstantTypes = new int[targets.length];
            for (int i = 0; i < targets.length; i++) {
                targetConstantTypes[i] = TokenUtils.getTokenId(targets[i]);
            }
            Arrays.sort(targetConstantTypes);
        }
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.EQUAL,
            TokenTypes.NOT_EQUAL,
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
    public void visitToken(DetailAST detailAST) {
        if (isRefactoringRequired(detailAST)) {
            log(detailAST.getLineNo(), MSG_KEY,
                    detailAST.getText());
        }
    }

    /**
     * Return true if current expression part required refactoring.
     *
     * @param logicNode Current logic operator node
     * @return Boolean variable
     */
    private boolean isRefactoringRequired(DetailAST logicNode) {
        final DetailAST[] children = getBothChildren(logicNode);
        final DetailAST firstOperand = children[0];
        final DetailAST secondOperand = children[1];

        final int constantType = firstOperand.getType();

        return isTargetConstantType(constantType)
                && !secondOperand.branchContains(constantType);
    }

    /**
     * Return both operators children.
     *
     * @param logicNode Current logic operator node
     * @return Array with children
     */
    private static DetailAST[] getBothChildren(DetailAST logicNode) {
        final DetailAST[] children = new DetailAST[2];
        int index = 0;
        for (DetailAST child = logicNode.getFirstChild(); child != null; child = child
                .getNextSibling()) {
            if (child.getType() != TokenTypes.LPAREN && child.getType() != TokenTypes.RPAREN) {
                children[index++] = child;
            }
        }

        return children;
    }

    /**
     * Return true if isTargetConstantType contains aTargetType.
     *
     * @param targetType - type of current constant
     * @return boolean
     */
    private boolean isTargetConstantType(int targetType) {
        return Arrays.binarySearch(targetConstantTypes, targetType) > -1;
    }

}
