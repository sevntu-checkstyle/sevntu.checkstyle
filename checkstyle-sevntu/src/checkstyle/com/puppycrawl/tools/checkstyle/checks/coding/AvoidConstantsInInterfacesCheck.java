package com.puppycrawl.tools.checkstyle.checks.coding;

import com.puppycrawl.tools.checkstyle.api.*;

public class AvoidConstantsInInterfacesCheck extends Check {

	public int[] getDefaultTokens() {
		return new int[] { TokenTypes.INTERFACE_DEF };

	}

	@Override
	public void visitToken(DetailAST ast) {
		DetailAST modifiers = ast.findFirstToken(TokenTypes.OBJBLOCK);
		int decCount = modifiers.getChildCount(TokenTypes.VARIABLE_DEF);
		if (decCount > 0) {
			log(ast.getLineNo(),
					"avoid.declare.constants");

		}
	}
}
