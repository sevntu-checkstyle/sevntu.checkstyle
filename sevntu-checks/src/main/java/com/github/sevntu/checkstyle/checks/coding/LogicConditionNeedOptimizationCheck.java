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

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * This check prevents the placement of local variables and fields after calling
 * methods in '&amp;&amp;' and '||' conditions.
 * </p>
 * <p>
 * For example: if(getProperty() &amp;&amp; property) ==&gt; if(property &amp;&amp; getProperty()),
 * and similarly for any expression.
 * </p>
 * @author <a href="mailto:IliaDubinin91@gmail.com">Ilia Dubinin</a>
 */
public class LogicConditionNeedOptimizationCheck extends AbstractCheck {

    /**
     * The key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY = "logic.condition.need.optimization";

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
            log(detailAST.getLineNo(), MSG_KEY,
                    detailAST.getText(), detailAST.getLineNo(),
                    detailAST.getColumnNo());
        }
    }

    /**
     * <p>
     * Return true, if current expression part need optimization.
     * </p>
     * @param logicNode
     *        - current logic operator node
     * @return - boolean variable
     */
    private static boolean needOptimization(DetailAST logicNode) {
        final DetailAST secondOperand = getSecondOperand(logicNode);
        return !secondOperand.branchContains(TokenTypes.METHOD_CALL)
                && logicNode.branchContains(TokenTypes.METHOD_CALL);
    }

    /**
     * <p>
     * Return second operand of current logic operator.
     * </p>
     * @param logicNode - current logic operator
     * @return second operand
     */
    private static DetailAST getSecondOperand(DetailAST logicNode) {
        DetailAST child = logicNode.getLastChild();
        if (child.getType() == TokenTypes.RPAREN) {
            child = child.getPreviousSibling();
        }
        return child;
    }

}
