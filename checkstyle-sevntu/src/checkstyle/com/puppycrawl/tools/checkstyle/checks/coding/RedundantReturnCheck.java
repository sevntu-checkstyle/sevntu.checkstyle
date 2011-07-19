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
	
	public void setIgnoreEmptyConstructors(String aIgnoreEmptyConstructors)
	{
		ignoreEmptyConstructors = "true".equalsIgnoreCase(aIgnoreEmptyConstructors);
	}
	
	@Override
	public int[] getDefaultTokens() 
	{		
		return new int []
		{
				TokenTypes.CTOR_DEF,
				TokenTypes.METHOD_DEF
		};	
	}
	
	@Override
	public void visitToken(DetailAST aAST)
	{
		final DetailAST methodObjectBlock=aAST.getLastChild();
		final DetailAST methodReturnType=aAST.getFirstChild().getNextSibling();
		
		switch	(aAST.getType())
		{
			case TokenTypes.CTOR_DEF:
				checkForRedundantReturn(methodObjectBlock);
				break;
				
			case TokenTypes.METHOD_DEF:
				if (methodReturnType.findFirstToken(TokenTypes.LITERAL_VOID)!=null)
				{
					checkForRedundantReturn(methodObjectBlock);
				}
				break;
				
			default: 
				throw new IllegalStateException(aAST.getText());
		}
	}
	
	//return is redundant if he is on the end of objectBlock and
	//	the objectBlock of the method divided into several tokens
	public void checkForRedundantReturn(DetailAST aAST)
	{
		
		final DetailAST tempNode=aAST.getLastChild();
		final int methodChildCount=aAST.getChildCount();
		final int placeForRedundantReturn=tempNode.getPreviousSibling().getType();
		
		if (methodChildCount>2)
		{
			handlePlacesForRedundantReturn(placeForRedundantReturn, aAST);
		}
		else
		{
			//allowing the return in empty methods and constructors
			if (!ignoreEmptyConstructors)
			{
				handlePlacesForRedundantReturn(placeForRedundantReturn, aAST);
			}
		}
		
		if (placeForRedundantReturn == TokenTypes.LITERAL_TRY)
		{
			checkForRedundantReturnInTryCatch(aAST);
		}
	}
	
	public void handlePlacesForRedundantReturn(int aPlaceForRedundantReturn,DetailAST aAST)
	{
		final DetailAST tempNode=aAST.getLastChild();
		
		if (aPlaceForRedundantReturn==TokenTypes.LITERAL_RETURN)
		{
			log(tempNode.getPreviousSibling().getLineNo(),"redundant.return","");
			return;
		}
	
		if (aPlaceForRedundantReturn==TokenTypes.LITERAL_TRY)
		{
			checkForRedundantReturnInTryCatch(aAST);
		}
		
	}
	
	public void checkForRedundantReturnInTryCatch(DetailAST aAST)
	{
		DetailAST tempNode = aAST.getFirstChild();
		final DetailAST objectBlockTRY=tempNode.getFirstChild();
		final DetailAST objectBlockCatch=tempNode.findFirstToken(TokenTypes.LITERAL_CATCH);
		final DetailAST objectBlockFinally=tempNode.findFirstToken(TokenTypes.LITERAL_FINALLY);		
		
		if (objectBlockTRY != null)
		{	
			tempNode =  objectBlockTRY.getLastChild().getPreviousSibling();
			
			if (tempNode != null)
			{			
				if (verifyTryCatchFinallyBlocks(objectBlockTRY.getChildCount(), tempNode.getType()))
				{
					log(objectBlockTRY.getLastChild().getPreviousSibling().getLineNo(),"redundant.return","");
				}
			}
		}
		
		if (objectBlockCatch!=null)
		{
			tempNode =objectBlockCatch.getLastChild().getLastChild().getPreviousSibling();
			
			if (tempNode !=null)
			{
				if (verifyTryCatchFinallyBlocks(objectBlockCatch.getChildCount(), tempNode.getType()))
				{
					log(objectBlockCatch.getLastChild().getLastChild().getPreviousSibling().getLineNo(),"redundant.return","");
				}
			}
		}
		
		if (objectBlockFinally!=null)
		{
			tempNode = objectBlockFinally.getLastChild().getLastChild().getPreviousSibling();

			if (tempNode != null)
			{			
				if (verifyTryCatchFinallyBlocks(objectBlockFinally.getChildCount(), tempNode.getType()))
				{
					log(objectBlockFinally.getLastChild().getLastChild().getPreviousSibling().getLineNo(),"redundant.return","");
				}
			}
		}
	}
	
	
	public boolean verifyTryCatchFinallyBlocks (int count,int placeForRedundantReturn)
	{
		
		if ( (count > 2) && (placeForRedundantReturn == TokenTypes.LITERAL_RETURN) )
		{
			return true;
		}
		
		if ( (!ignoreEmptyConstructors) && (placeForRedundantReturn == TokenTypes.LITERAL_RETURN))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
}
 


