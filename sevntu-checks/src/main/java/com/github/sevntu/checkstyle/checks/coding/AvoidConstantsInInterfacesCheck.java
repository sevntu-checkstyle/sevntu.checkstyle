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
 *Interfaces should be used only to define types.
 *They should not be used to export constants.
 *Developer should avoid declaration of constants in interfaces.
 *For more information read:
 *"Effective Java" chapter "Item 19: Use interfaces only to define types"
 *This check reports if the interface contains the declaration of constants.
 * </p>
 *
 * @author <a href="mailto:go.indieman@gmail.com">Svinukhov Vladimir</a>
 */

public class AvoidConstantsInInterfacesCheck extends Check 
{
	@Override
	public int[] getDefaultTokens() 
	{
		return new int[] { TokenTypes.INTERFACE_DEF };
	}

	@Override
	public void visitToken(DetailAST aInterface) 
	{
		final DetailAST body = aInterface.findFirstToken(TokenTypes.OBJBLOCK);
		final int varCount = body.getChildCount(TokenTypes.VARIABLE_DEF);
		if (varCount > 0) 
		{
			log(aInterface.getLineNo(), "avoid.declare.constants");
		}
	}

}
