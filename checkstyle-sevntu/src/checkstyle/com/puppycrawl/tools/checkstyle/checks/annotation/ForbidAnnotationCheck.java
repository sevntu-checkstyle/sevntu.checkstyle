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
			switch(target[i].toUpperCase().hashCode()){
				case -236608633://method_def
					annotationTarget[i]=TokenTypes.METHOD_DEF;
					break;
				case 1945721762: //variable_def
					annotationTarget[i]=TokenTypes.VARIABLE_DEF;
					break;
				case -1556869585: //parameter_def
					annotationTarget[i]=TokenTypes.PARAMETER_DEF;
					break;
				case 385402682: //ctor_def
					annotationTarget[i]=TokenTypes.CTOR_DEF;
					break;
				case -1231784884: //package_def
					annotationTarget[i]=TokenTypes.PACKAGE_DEF;
					break;
				case -1516798850: //class_def
					annotationTarget[i]=TokenTypes.CLASS_DEF;
					break;
				case -734470443: //annotation_def
					annotationTarget[i]=TokenTypes.ANNOTATION_DEF;
					break;
				case -661422145: //interface_def
					annotationTarget[i]=TokenTypes.INTERFACE_DEF;
					break;
				case -1286841433: //enum_def
					annotationTarget[i]=TokenTypes.ENUM_DEF;
					break;
				case 1162436424: //enum_constant_def
					annotationTarget[i]=TokenTypes.ENUM_CONSTANT_DEF;
					break;
				default: 
					annotationTarget[i]=-1;
			}
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
		int name=aAST.getText().hashCode();
		for(int i=0;i<annotationName.length;i++){
			if(name==annotationName[i].hashCode()){
				currentAnnotetion=i;
				return true;
			}
		}
		return false;
	}
}
