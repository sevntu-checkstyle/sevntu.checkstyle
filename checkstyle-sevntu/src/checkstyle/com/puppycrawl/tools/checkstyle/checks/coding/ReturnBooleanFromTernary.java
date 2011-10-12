package com.puppycrawl.tools.checkstyle.checks.coding;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * It is a bad practice to return boolean values from ternary operations. Just use the value inside branch instead.
 * </p>
 * 
 * @author Ivan Sopov
 */
public class ReturnBooleanFromTernary extends Check {

	@Override
	public int[] getDefaultTokens() {
		return new int[] { TokenTypes.QUESTION };
	}

	@Override
	public void visitToken(DetailAST aAST) {
		DetailAST secondBranch = aAST.getLastChild();
		DetailAST firstBranch = secondBranch.getPreviousSibling().getPreviousSibling();
		if ("true".equals(secondBranch.getText()) || "false".equals(secondBranch.getText())
				|| "true".equals(firstBranch.getText()) || "false".equals(firstBranch.getText())) {
			log(aAST, "return.boolean.ternary");
		}
	}
}
