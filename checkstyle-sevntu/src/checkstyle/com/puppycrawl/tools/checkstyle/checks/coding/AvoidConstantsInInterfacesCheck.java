package com.puppycrawl.tools.checkstyle.checks.coding;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 *Interfaces should be used only to define types. They should not be used to export constants.
 *Developer should avoid declaration of constants in interfaces. 
 *For more information read "Effective Java" chapter "Item 19: Use interfaces only to define types" 
 *This check reports if the interface contains the declaration of constants.
 * </p>
 * 
 * @author <a href="mailto:go.indieman@gmail.com">Svinukhov Vladimir</a>
 */

public class AvoidConstantsInInterfacesCheck extends Check 
{
	public int[] getDefaultTokens() 
	{
		return new int[] { TokenTypes.INTERFACE_DEF };
	}

	@Override
	public void visitToken(DetailAST aInterface) 
	{
		DetailAST body = aInterface.findFirstToken(TokenTypes.OBJBLOCK);
		int varCount = body.getChildCount(TokenTypes.VARIABLE_DEF);
		if (varCount > 0) 
		{
			log(aInterface.getLineNo(), "avoid.declare.constants");
		}
	}

}

