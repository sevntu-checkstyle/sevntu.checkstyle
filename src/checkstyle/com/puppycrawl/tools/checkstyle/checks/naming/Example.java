package com.puppycrawl.tools.checkstyle.checks.naming;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class Example extends Check {
	//private static final int DEFAULT_MAX = 30;
	private int max = 30; //DEFAULT_MAX;

	public void setMax(int limit) {
		max = limit;
	}

	@Override
	public int[] getDefaultTokens() {
		return new int[] { TokenTypes.CLASS_DEF, TokenTypes.INTERFACE_DEF };
	}

	@Override
	public void visitToken(DetailAST ast) {
		// find the OBJBLOCK node below the CLASS_DEF/INTERFACE_DEF
		DetailAST objBlock = ast.findFirstToken(TokenTypes.OBJBLOCK);
		// count the number of direct children of the OBJBLOCK
		// that are METHOD_DEFS
		int methodDefs = objBlock.getChildCount(TokenTypes.METHOD_DEF);
		// report error if limit is reached
		if (methodDefs > this.max) {
			log(ast.getLineNo(),
					"too many methods, only " + this.max + " are allowed");
		}
	}
}
