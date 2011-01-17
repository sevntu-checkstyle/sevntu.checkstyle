package com.puppycrawl.tools.checkstyle.checks.naming;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class AbstractCheckExample extends Check {

	private boolean abstractNameWithAbstractType = true;
	private boolean abstractTypeWithAnyName = false;

	public void setAbstractNameWithAbstractType(boolean value) {
		abstractNameWithAbstractType = value;
	}

	public void setAbstractTypeWithAnyName(boolean value) {
		abstractTypeWithAnyName = value;
	}

	@Override
	public int[] getDefaultTokens() {
		return new int[] { TokenTypes.CLASS_DEF };
	}

	@Override
	public void visitToken(DetailAST ast) {
		DetailAST ident = ast.findFirstToken(TokenTypes.IDENT);
		String className = ident.getText();
		DetailAST modifiers = ast.findFirstToken(TokenTypes.MODIFIERS);
		if (abstractNameWithAbstractType)
			if (modifiers.findFirstToken(TokenTypes.ABSTRACT) == null
					&& className.startsWith("Abstract")) {
				log(ast.getLineNo(), " class doesn't have abstract modifier");
			}
		if (abstractTypeWithAnyName)
			if (modifiers.findFirstToken(TokenTypes.ABSTRACT) != null
					&& !className.startsWith("Abstract"))
				log(ast.getLineNo(), " class name doesn't begin with 'AbstractXYZ'");
	}
}
