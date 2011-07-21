package com.puppycrawl.tools.checkstyle.checks.annotation;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import java.util.HashSet;
import java.util.Set;


/**
 * 
 * @author <a href="mailto:hidoyatov.v.i@gmail.com">Hidoyatov Victor</a> 
 */
/**
 * Forbid specific annotation of variable,methods,class,package and other. If
 * you want to forbid use 'XXX' annotation with methods and class, you mast
 * write: <module name="ForbidAnnotation"> <property name="annotation"
 * value="XXX"/> <property name="target" value="METHOD_DEF,CLASS_DEF"/>
 * </module>
 */

public class ForbidAnnotationCheck extends Check {
	private Set<String> annotationName = new HashSet<String>();
	private Set<Integer> annotationTarget = new HashSet<Integer>();

	public void setAnnotation(final String[] aNames) {
		if (aNames != null) {
			for(String aName:aNames){
				annotationName.add(aName);
			}
		}
	}

	public void setTarget(String[] aTargets) {
		if (aTargets != null) {
			for (String aTarget: aTargets) {
				annotationTarget.add(TokenTypes.getTokenId(aTarget));
			}
		}
	}

	@Override
	public int[] getDefaultTokens() {
		return new int[] { TokenTypes.ANNOTATION };
	}

	public void visitToken(DetailAST aAnnotation) {
		String aAnnotationName = aAnnotation.findFirstToken(TokenTypes.IDENT).getText();
		int aTargetType = aAnnotation.getParent().getParent().getType();
		if (isAnnotation(aAnnotationName) && isForbidden(aTargetType)) {
			String currentTarget = aAnnotation.getParent().getParent().getText();
			log(aAnnotation.getLineNo(), "annotation.incorrect.target", currentTarget,
					aAnnotationName);
		}
	}
	
	private boolean isAnnotation(String aAnnotationName){
			return aAnnotationName != null && annotationName.contains(aAnnotationName);
	}
	
	private boolean isForbidden(int aTargetType){
		return annotationTarget.contains(aTargetType);
	}
}