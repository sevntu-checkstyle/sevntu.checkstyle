package com.github.sevntu.checkstyle.checks.annotation;
package com.github.sevntu.checkstyle.checks.javadoc;

import antlr.collections.AST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.Scope;
import com.puppycrawl.tools.checkstyle.api.TextBlock;



public class ImproveJavaDockMethodCheck extends Check {
	
	
	@Override
	public int[] getDefaultTokens() {
		return new int[] {TokenTypes.PACKAGE_DEF, TokenTypes.IMPORT,
						TokenTypes.CLASS_DEF, TokenTypes.ENUM_DEF,
						TokenTypes.METHOD_DEF, TokenTypes.CTOR_DEF,
						TokenTypes.ANNOTATION_FIELD_DEF,
		};
		}
	
	@Override
	public int[] getAcceptableTokens() {
		return new int[] {TokenTypes.METHOD_DEF, TokenTypes.CTOR_DEF,
							TokenTypes.ANNOTATION_FIELD_DEF,};
		}
	
	@Override
	protected final void processAST (DetailAST aAST) {
		final Scope theScope = calculateScoope(aAST);
		if (shouldCheck(aAST, theScope)){
			final FileContents contents = getFileContents();
			final TextBlock cmt = contents.getJavadocBefore(aAST.getLineNo());
			
			if (cmt == null) {
				if (!isMissingJavadocAllowed(aAST)){
					log(aAST, "javadoc.missing");
				}
			}
			else {
				checkComment(aAST, cmt, theScope);
			}
		}
	}
	
	
	

}
