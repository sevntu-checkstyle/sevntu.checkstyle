package com.puppycrawl.tools.checkstyle.checks.coding;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;


/**
 *  highlight the redundant returns in constructors and void methods,
 *  'return' in empty void method and constructors may be allowed
 * @author <a href="mailto:fishh1991@gmail.com">Troshin Sergey</a> 
 */
public class RedundantReturnCheck extends Check
{
	//true, if 'return' in empty constructors and void methods is allowed
	//default value - true
	private boolean ignoreEmptyConstructors=true;	
	
	//setter for ignoreEmptyConstructors
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
		switch	(curNode.getType()){
			case TokenTypes.CTOR_DEF:
				//get the objectBlock and check the pre-last token
				submit(curNode.getLastChild());
				break;
			case TokenTypes.METHOD_DEF:
				//check for method type, define the void methods
				if (curNode.getFirstChild().getNextSibling().findFirstToken(TokenTypes.LITERAL_VOID)!=null){
					submit(curNode.getLastChild());
				}
				break;
				default: 
					throw new IllegalStateException(curNode.getText());
		}
	}//visitToken
	
	//return is redundant if he is on the end of objectBlock and
	//	the objectBlock of the method divided into several tokens
	public void submit(DetailAST curNode){
		DetailAST tempNode=curNode.getLastChild();
		if ((curNode.getChildCount()>2)&&(tempNode.getPreviousSibling().getType()==TokenTypes.LITERAL_RETURN)){
			log(tempNode.getPreviousSibling().getLineNo(),"redundant.return","");
			return;
		}
		//allowing the return in empty methods and constructors
		if ((!ignoreEmptyConstructors)&&(tempNode.getPreviousSibling().getType()==TokenTypes.LITERAL_RETURN)){
			log(tempNode.getPreviousSibling().getLineNo(),"redundant.return","");
		}		
	}//submit
	
}//class RedundantReturn
 


