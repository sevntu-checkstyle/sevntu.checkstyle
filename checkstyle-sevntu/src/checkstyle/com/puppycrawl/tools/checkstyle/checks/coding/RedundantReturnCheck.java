package com.puppycrawl.tools.checkstyle.checks.coding;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Highlight the redundant returns in constructors and void methods, 'return' in
 * empty void method and constructors may be allowed
 * 
 * @author <a href="mailto:fishh1991@gmail.com">Troshin Sergey</a>
 */
public class RedundantReturnCheck extends Check {

    // True, if 'return' in empty constructors and void methods is forbid.
    private boolean mAvoidEmptyMethodsAndConstructors = true;

    public void setAvoidEmptyMethodsAndConstructors(boolean aAvoidEmptyBlocks) {
	mAvoidEmptyMethodsAndConstructors = aAvoidEmptyBlocks;
    }

    @Override
    public int[] getDefaultTokens() {
	return new int[] { TokenTypes.CTOR_DEF, TokenTypes.METHOD_DEF };
    }

    @Override
    public void visitToken(DetailAST aAst) {

	final DetailAST methodObjectBlock = aAst.getLastChild();

	switch (aAst.getType()) {
	case TokenTypes.CTOR_DEF:
	    checkForRedundantReturn(methodObjectBlock);
	    break;

	case TokenTypes.METHOD_DEF:
	    if (aAst.findFirstToken(TokenTypes.TYPE).findFirstToken(
		    TokenTypes.LITERAL_VOID) != null) {

		checkForRedundantReturn(methodObjectBlock);
	    }
	    break;

	default:
	    final String exception = " Unexpected TokenType -  ";
	    throw new IllegalStateException(exception + aAst.getText());
	}
    }

    /**
     * Return is redundant if he is on the end of objectBlock and the
     * objectBlock of the method divided into several tokens.
     * 
     * @param aMethodObjectBlock
     *            - a method or constructor object block
     */
    private void checkForRedundantReturn(DetailAST aMethodObjectBlock) {
    	
    final int methodChildCount = aMethodObjectBlock.getChildCount();
    	
    if (!(methodChildCount == 1)) {
    	
    	final int placeForRedundantReturn = aMethodObjectBlock.getLastChild()
    			.getPreviousSibling().getType();

    	final int methodWithSingleChild = 2;

    	if (methodChildCount > methodWithSingleChild) {

    		handlePlacesForRedundantReturn(placeForRedundantReturn, aMethodObjectBlock);
    	} else {

    		if (mAvoidEmptyMethodsAndConstructors) {
    			handlePlacesForRedundantReturn(placeForRedundantReturn, aMethodObjectBlock);
    		}

    		if (placeForRedundantReturn == TokenTypes.LITERAL_TRY) {
    			submitRedundantReturnInTryCatch(aMethodObjectBlock.getFirstChild());
    		}
    	}
    }
    }

    /**
     * @param aType
     *            - Type of token, where redundant return is expected.
     * @param aMethodObjectBlock
     *            - A method or constructor object block.
     */
    private void handlePlacesForRedundantReturn(int aType, DetailAST aMethodObjectBlock) {

	if (aType == TokenTypes.LITERAL_RETURN) {
	    
	    final DetailAST aLastChild = aMethodObjectBlock.getLastChild();
	    
	    log(aLastChild.getPreviousSibling().getLineNo());
	} else if (aType == TokenTypes.LITERAL_TRY) {
	    
	    submitRedundantReturnInTryCatch(aMethodObjectBlock.getFirstChild());
	}
    }

    /**
     * Check the try, catch, finally object blocks on redundant return content.
     * 
     * @param aTryAst
     *            - Ast that contain a try node.
     */
    private void submitRedundantReturnInTryCatch(DetailAST aTryAst) {

	DetailAST astBlockTry = aTryAst.getFirstChild();

	handleBlocksTryCatchFinally(astBlockTry.getLastChild()
		.getPreviousSibling());

	final int catchBlocksAmount = aTryAst
		.getChildCount(TokenTypes.LITERAL_CATCH);

	for (int i = 0; i < catchBlocksAmount; i++) {
	    
	    astBlockTry = astBlockTry.getNextSibling();
	    handleBlocksTryCatchFinally(astBlockTry.getLastChild().getLastChild()
			.getPreviousSibling());
	}
	
	if (astBlockTry.getNextSibling() != null){
	    
	    handleBlocksTryCatchFinally(astBlockTry.getNextSibling().getLastChild()
		    .getLastChild().getPreviousSibling());
    
	}
    }

    /**
     * Submit a mistake if the try or catch or finally blocks have redundant
     * return.
     * 
     * @param aAstReturn
     *            - a place where the redundantReturn is expected.
     */
    private void handleBlocksTryCatchFinally(DetailAST aAstReturn) {

	    if (aAstReturn != null) {

		if (aAstReturn.getType() == TokenTypes.LITERAL_RETURN) {

		    log(aAstReturn.getLineNo());
		}
	    }
    }

 
    private void log(int lineNumber) {
	log(lineNumber, "redundant.return");
    }
}
