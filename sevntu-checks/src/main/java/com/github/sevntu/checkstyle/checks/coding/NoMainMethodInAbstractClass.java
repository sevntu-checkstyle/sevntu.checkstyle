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
 * Checks if an abstract class does not have "main()" method, because it can
 * mislead a developer to consider this class as a ready-to-use implementation.
 * @author Vadym Chekrii
 */
public class NoMainMethodInAbstractClass extends Check
{
    @Override
    public int[] getDefaultTokens()
    {
        return new int[] {TokenTypes.METHOD_DEF};
    }

    @Override
    public void visitToken(DetailAST aAST)
    {
        final DetailAST objBlock = aAST.getParent();
        final DetailAST astClass = objBlock.getParent();
        final DetailAST modifiersBlock =
                astClass.findFirstToken(TokenTypes.MODIFIERS);
        final DetailAST abstractMod =
                modifiersBlock.findFirstToken(TokenTypes.ABSTRACT);
        final String methodName =
                aAST.findFirstToken(TokenTypes.IDENT).getText();
        if (abstractMod != null && ("main").equals(methodName)) {
            log(aAST.getLineNo(), "avoid.main.method");
        }
    }
}
