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

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.Arrays;

/**
 * If comparing values, C(C++) developers prefer to put the constant first in the equality check,
 * to prevent situations of assignment rather than equality checking.
 *
 * But in Java, in IF condition it is impossible to use assignment,
 * so that habit become unnecessary and do damage readability of code.
 *
 * In C(C++), comparison for null is tricky, and it is easy to write "=" instead of "==",
 * and no complication error will be but condition will work in different way
 *
 * Example:
 * <code>if (null == variable)</code>
 * rather than
 * <code>if (variable == null)</code>
 * because if you forget one (typo mistake) of the equal sign, you end up with
 * <code>if (variable = null)</code>
 * which assigns null to variable and IF always evaluate to true.
 *
 * @author Sergey Burtsev
 */
public class AvoidConstantAsFirstOperandInConditionCheck extends Check {

	public final static String MSG_KEY = "avoid.constant.as.first.operand.in.condition";
	
    /**
     * mTargetConstantTypes is array of default target constant types.
     */
    private int[] mTargetConstantTypes = new int[]{
            TokenTypes.LITERAL_TRUE,
            TokenTypes.LITERAL_FALSE,
            TokenTypes.LITERAL_NULL,
            TokenTypes.NUM_INT,
            TokenTypes.NUM_FLOAT,
            TokenTypes.NUM_LONG,
            TokenTypes.NUM_DOUBLE,
    };

    /**
     * Set target constant types
     *
     * @param aTargets
     */
    public void setTargetConstantTypes(String[] aTargets) {
        if (aTargets != null) {
            mTargetConstantTypes = new int[aTargets.length];
            for (int i = 0; i < aTargets.length; i++) {
                mTargetConstantTypes[i] = TokenTypes.getTokenId(aTargets[i]);
            }
            Arrays.sort(mTargetConstantTypes);
        }
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[]{TokenTypes.EQUAL, TokenTypes.NOT_EQUAL};
    }

    @Override
    public void visitToken(DetailAST aDetailAST) {
        if (isRefactoringRequired(aDetailAST)) {
            log(aDetailAST.getLineNo(), MSG_KEY,
                    aDetailAST.getText());
        }
    }

    /**
     * Return true if current expression part required refactoring.
     *
     * @param aLogicNode Current logic operator node
     * @return Boolean variable
     */
    private boolean isRefactoringRequired(DetailAST aLogicNode) {
        final DetailAST[] children = getBothChildren(aLogicNode);
        final DetailAST firstOperand = children[0];
        final DetailAST secondOperand = children[1];

        final int constantType = firstOperand.getType();

        return isTargetConstantType(constantType) && firstOperand.branchContains(constantType) && !secondOperand.branchContains(constantType);
    }

    /**
     * Return both operators children.
     *
     * @param aLogicNode Current logic operator node
     * @return Array with children
     */
    private static DetailAST[] getBothChildren(DetailAST aLogicNode) {
        final DetailAST[] children = new DetailAST[2];
        int i = 0;
        for (DetailAST child = aLogicNode.getFirstChild(); child != null; child = child.getNextSibling()) {
            if (child.getType() != TokenTypes.LPAREN && child.getType() != TokenTypes.RPAREN) {
                children[i++] = child;
            }
        }

        return children;
    }

    /**
     * Return true if isTargetConstantType contains aTargetType.
     *
     * @param aTargetType - type of current constant
     * @return boolean
     */
    private boolean isTargetConstantType(int aTargetType) {
        return Arrays.binarySearch(mTargetConstantTypes, aTargetType) > -1;
    }
}