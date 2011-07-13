package com.puppycrawl.tools.checkstyle.checks.annotation;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
/**
* 
* @author <a href="mailto:hidoyatov.v.i@gmail.com">Hidoyatov Victor</a> 
*/ 

public class ForbidAnnotationCheck extends Check{
	private String [] annotationName;
	private int [] annotationTarget;
	private int currentAnnotetion;
	private String currentTarget="";
	public void setAnotation(final String [] name){
		annotationName=new String[name.length];
		for(int i=0;i<name.length;i++){
			annotationName[i]=name[i];
		}
	}
	public void setTarget(String[]target){
		annotationTarget=new int[target.length];
		for(int i=0;i<target.length;i++){
			annotationTarget[i]=TokenTypes.getTokenId(target[i]);
		}
	}
	@Override
	public int[] getDefaultTokens() {
		return new int[]{TokenTypes.ANNOTATION};
	}
	public void visitToken(DetailAST aAST){
		if(isAnnotation(aAST.findFirstToken(TokenTypes.IDENT))&&isForbid(aAST)){
			log(aAST.getLineNo(), "annotation.incorrect.target",currentTarget,annotationName[currentAnnotetion]);
		}
	}
	private boolean isForbid(DetailAST aAST){//is current annotation's target forbid?
		int type=aAST.getParent().getParent().getType();
		for(int i=0;i<annotationTarget.length;i++){
			if(type==annotationTarget[i]){
				currentTarget=aAST.getParent().getParent().getText();
				return true;
			}
		}
		return false;
	}
	private boolean isAnnotation(DetailAST aAST){//is current annotation forbid?
		String name=aAST.getText();
		for(int i=0;i<annotationName.length;i++){
			if(name.equals(annotationName[i])){
				currentAnnotetion=i;
				return true;
			}
		}
		return false;
	}
}
