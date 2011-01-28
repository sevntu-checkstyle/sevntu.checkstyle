////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2010  Oliver Burn
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

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.checks.CheckUtils;

/**
 * <p>
 * This check prevents ...
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil
 *         Yaroslavtsev</a>
 */
public class AvoidNotShortCircuitOperatorsForBooleanCheck extends Check
{

    /** Creates new instance of the check. */
    public AvoidNotShortCircuitOperatorsForBooleanCheck()
    {
    }

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] {TokenTypes.BOR, TokenTypes.BAND,
           TokenTypes.BOR_ASSIGN, TokenTypes.BAND_ASSIGN, };
    }

    @Override
    public void visitToken(DetailAST aDetailAST)
    {
        DetailAST currentNode = aDetailAST;
        while (currentNode != null
                && currentNode.getType() != TokenTypes.LITERAL_IF
                && currentNode.getType() != TokenTypes.LITERAL_FOR
                && currentNode.getType() != TokenTypes.LITERAL_WHILE
                && currentNode.getType() != TokenTypes.VARIABLE_DEF
                && currentNode.getType() != TokenTypes.CLASS_DEF)
        {
            currentNode = currentNode.getParent();
        }

        if (!(currentNode.getType() == TokenTypes.VARIABLE_DEF && "int".equals(CheckUtils
                        .createFullType(currentNode.findFirstToken(TokenTypes.TYPE))
                        .getText()))) {
            log(aDetailAST, "avoid.not.short.circuit.operators.for.boolean",
                    aDetailAST.getText());
        }

    }

}