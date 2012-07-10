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

/**
 * <p>
 * This check prevents the placement of local variables and fields after calling
 * methods in '&&' and '||' conditions.
 * </p>
 * <p>
 * For example: if(getProperty() && property) ==> if(property && getProperty()),
 * and similarly for any expression.
 * </p>
 * @author <a href="mailto:IliaDubinin91@gmail.com">Ilia Dubinin</a>
 */
public class LogicConditionNeedOptimizationCheck extends Check
{

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] {TokenTypes.LAND, TokenTypes.LOR };
    }

    @Override
    public void visitToken(DetailAST aDetailAST)
    {
        if (needOptimization(aDetailAST)) {
            log(aDetailAST.getLineNo(), "logic.condition.need.optimization",
                    aDetailAST.getText(), aDetailAST.getLineNo(),
                    aDetailAST.getColumnNo());
        }
    }

    /**
     * <p>
     * Return true, if current expression part need optimization.
     * </p>
     * @param aLogicNode - current logic operator node
     * @return - boolean variable
     */
    private boolean needOptimization(DetailAST aLogicNode)
    {
        final DetailAST firstOperand = aLogicNode.getFirstChild();
        final DetailAST secondOperand = aLogicNode.getLastChild();
        if (!secondOperand.branchContains(TokenTypes.METHOD_CALL)
                && firstOperand.branchContains(TokenTypes.METHOD_CALL))
        {
            return true;
        }
        return false;
    }
}
