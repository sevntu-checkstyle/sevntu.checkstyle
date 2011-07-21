package com.puppycrawl.tools.checkstyle.checks.annotation;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * 
 * @author <a href="mailto:hidoyatov.v.i@gmail.com">Hidoyatov Victor</a> 
 */
/**
 * It would be nice to have a ability to forbid specific annotation of
 * variable,methods,class,package and other. If you want to forbid use 'XXX'
 * annotation with methods and class, you mast write: <module
 * name="ForbidAnnotation"> <property name="annotation" value="XXX"/> <property
 * name="target" value="METHOD_DEF,CLASS_DEF"/> </module>
 */

public class ForbidAnnotationCheck extends Check {
	private String[] annotationName;
	private int[] annotationTarget;

	public void setAnnotation(final String[] aName) {
		if (aName != null) {
			annotationName = aName;
		}
	}

	public void setTarget(String[] aTarget) {
		if (aTarget != null) {
			annotationTarget = new int[aTarget.length];
			for (int i = 0; i < aTarget.length; i++) {
				annotationTarget[i] = TokenTypes.getTokenId(aTarget[i]);
			}
		}
	}

	@Override
	public int[] getDefaultTokens() {
		return new int[] { TokenTypes.ANNOTATION };
	}

	public void visitToken(DetailAST aAST) {
		int currentAnnotetion = 0;
		String currentTarget = "";
		boolean isValidAnnotation = false;
		boolean isValidTarget = false;

		int type = aAST.getParent().getParent().getType();
		for (int i = 0; i < annotationTarget.length; i++) {
			if (type == annotationTarget[i]) {
				currentTarget = aAST.getParent().getParent().getText();
				isValidTarget = true;
			}
		}

		String name = aAST.findFirstToken(TokenTypes.IDENT).getText();
		for (int i = 0; i < annotationName.length; i++) {
			if (name.equals(annotationName[i])) {
				currentAnnotetion = i;
				isValidAnnotation = true;
			}
		}

		if (isValidAnnotation && isValidTarget) {
			log(aAST.getLineNo(), "annotation.incorrect.target", currentTarget,
					annotationName[currentAnnotetion]);
		}
	}
}