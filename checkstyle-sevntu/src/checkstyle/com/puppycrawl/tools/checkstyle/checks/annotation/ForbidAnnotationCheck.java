package com.puppycrawl.tools.checkstyle.checks.annotation;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Forbid specific annotation of variable,methods,class,package and other. If
 * you want to forbid use '@XXX' annotation with methods and class, you must
 * write: <module name="ForbidAnnotation"> <property name="annotationNames"
 * value="XXX"/> <property name="annotationTargets"
 * value="METHOD_DEF,CLASS_DEF"/> </module>
 * 
 * @author <a href="mailto:hidoyatov.v.i@gmail.com">Hidoyatov Victor</a>
 */

public class ForbidAnnotationCheck extends Check {
	private Set<String> annotationNames = new HashSet<String>();
	private int[] annotationTargets;

	public void setAnnotationNames(final String[] aNames) {
		if (aNames != null) {
			for (String name : aNames) {
				annotationNames.add(name);
			}
		}
	}

	public void setAnnotationTargets(String[] aTargets) {
		if (aTargets != null) {
			annotationTargets = new int[aTargets.length];
			for (int i = 0; i < aTargets.length; i++) {
				annotationTargets[i] = TokenTypes.getTokenId(aTargets[i]);
			}
			Arrays.sort(annotationTargets);
		}
	}

	@Override
	public void beginTree(DetailAST aRootAST) {
		if (annotationTargets == null) {
			annotationTargets = new int[0];
		}
	}

	@Override
	public int[] getDefaultTokens() {
		return new int[] { TokenTypes.ANNOTATION };
	}

	@Override
	public void visitToken(DetailAST aAnnotation) {

		String annotationName = aAnnotation.findFirstToken(TokenTypes.IDENT)
				.getText();
		// first parent - 'MODIFIERS', second parent - annotation's target
		DetailAST annotationTarget = aAnnotation.getParent().getParent();

		int targetType = annotationTarget.getType();

		if (isRequiredAnnotationName(annotationName)
				&& isForbiddenAnnotationTarget(targetType)) {

			String currentTarget = annotationTarget.getText();

			log(aAnnotation.getLineNo(), "annotation.incorrect.target",
					currentTarget, annotationName);
		}
	}

	private boolean isRequiredAnnotationName(String aAnnotationName) {
		return aAnnotationName != null
				&& annotationNames.contains(aAnnotationName);
	}

	private boolean isForbiddenAnnotationTarget(int aTargetType) {
		return Arrays.binarySearch(annotationTargets, aTargetType) > -1;
	}
}