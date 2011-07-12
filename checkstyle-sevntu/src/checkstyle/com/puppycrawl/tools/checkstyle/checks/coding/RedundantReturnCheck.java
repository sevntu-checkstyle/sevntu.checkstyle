package com.puppycrawl.tools.checkstyle.checks.coding;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FastStack;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.sun.tools.javac.util.Context;

public class RedundantReturnCheck extends Check
{
	private boolean ignoreEmptyConstructors=true;
	
//	public boolean getFlag(){
//		return flag;
//	}//getter
	
	public void setFlag(String aFlag){
		
		ignoreEmptyConstructors=aFlag.equalsIgnoreCase("true");
	}//setter
	
	@Override
	public int[] getDefaultTokens  () 
	{		
		return new int []{
				TokenTypes.CTOR_DEF,
				TokenTypes.METHOD_DEF
		};	
	}//getDefaultTokens 
	
	@Override
	public void visitToken (DetailAST curNode){
		int y=curNode.getChildCount();
		switch	(curNode.getType()){
			case TokenTypes.CTOR_DEF:
				//get the objectBlock and check the pre-last token
				submit(curNode.getLastChild());
				break;
			case TokenTypes.METHOD_DEF:
				//check for method type
				if (curNode.getFirstChild().getNextSibling().findFirstToken(TokenTypes.LITERAL_VOID)!=null){
					submit(curNode.getLastChild());
				}
				break;
				default: 
					throw new IllegalStateException(curNode.getText());
		}
	}//visitToken
	
	public void submit(DetailAST curNode){
		DetailAST tempNode=curNode.getLastChild();
		if ((curNode.getChildCount()>2)&&(tempNode.getPreviousSibling().getType()==TokenTypes.LITERAL_RETURN)){
			log(tempNode.getPreviousSibling().getLineNo(),"redundant.return","");
			return;
		}
		//if programm already here, then we check the flag
		if ((!ignoreEmptyConstructors)&&(tempNode.getPreviousSibling().getType()==TokenTypes.LITERAL_RETURN)){
			log(tempNode.getPreviousSibling().getLineNo(),"redundant.return","");
		}		
	}//submit
	
}//class RedundantReturn
 


