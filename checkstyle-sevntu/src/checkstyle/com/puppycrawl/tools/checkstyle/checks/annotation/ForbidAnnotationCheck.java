package com.puppycrawl.tools.checkstyle.checks.annotation;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Forbid specific annotation of variable,methods,class,package and other. If
 * you want to forbid use 'XXX' annotation with methods and class, you mast
 * write: <module name="ForbidAnnotation"> <property name="annotation"
 * value="XXX"/> <property name="target" value="METHOD_DEF,CLASS_DEF"/>
 * </module>
 * 
 * @author <a href="mailto:hidoyatov.v.i@gmail.com">Hidoyatov Victor</a>
 */

public class ForbidAnnotationCheck extends Check {
	private Set<String> annotationNames = new HashSet<String>();
	private int[] annotationTargets;

	public void setAnnotation(final String[] aNames) {
		if (aNames != null) {
			for (String aName : aNames) {
				annotationNames.add(aName);
			}
		}
	}

	public void setTarget(String[] aTargets) {
		if (aTargets != null) {
			annotationTargets = new int[aTargets.length];
			for (int i = 0; i < aTargets.length; i++) {
				annotationTargets[i] = TokenTypes.getTokenId(aTargets[i]);
			}
			Arrays.sort(annotationTargets);
		}
	}

	@Override
	public int[] getDefaultTokens() {
		return new int[] { TokenTypes.ANNOTATION };
	}

	public void visitToken(DetailAST aAnnotation) {

		String annotationName = aAnnotation.findFirstToken(TokenTypes.IDENT).getText();

		DetailAST annotationTarget = aAnnotation.getParent().getParent();

		int targetType = annotationTarget.getType();

		if (isAnnotation(annotationName) && isForbidden(targetType)) {
			
			String currentTarget = annotationTarget.getText();
			
			log(aAnnotation.getLineNo(), "annotation.incorrect.target",
					currentTarget, annotationName);
		}
	}

	private boolean isAnnotation(String aAnnotationName) {
		return aAnnotationName != null && annotationNames.contains(aAnnotationName);
	}

	private boolean isForbidden(int aTargetType) {
		return Arrays.binarySearch(annotationTargets, aTargetType) > -1;
	}
}