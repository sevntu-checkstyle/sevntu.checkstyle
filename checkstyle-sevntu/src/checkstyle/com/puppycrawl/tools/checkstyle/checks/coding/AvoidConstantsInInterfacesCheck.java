package com.puppycrawl.tools.checkstyle.checks.coding;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class AvoidConstantsInInterfacesCheck extends Check {

	public int[] getDefaultTokens() {
		return new int[] { TokenTypes.INTERFACE_DEF };

	}

	@Override
	public void visitToken(DetailAST aInterface) {
		DetailAST body = aInterface.findFirstToken(TokenTypes.OBJBLOCK);
		int varCount = body.getChildCount(TokenTypes.VARIABLE_DEF);
		if (varCount > 0) {
			log(aInterface.getLineNo(), "avoid.declare.constants");

		}
	}
}
