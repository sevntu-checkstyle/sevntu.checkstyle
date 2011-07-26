package com.puppycrawl.tools.checkstyle.checks.coding;

import java.math.BigInteger;

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
	return new int[] { 
		TokenTypes.CTOR_DEF, 
		TokenTypes.METHOD_DEF 
		};
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
     * @param aAst  - a method or constructor object block
     */
    private void checkForRedundantReturn(DetailAST aAst) {

	final int placeForRedundantReturn = aAst.getLastChild()
		.getPreviousSibling().getType();

	final int methodWithSingleChild = 2;

	if (aAst.getChildCount() > methodWithSingleChild) {
	    
	    handlePlacesForRedundantReturn(placeForRedundantReturn, aAst,
		    aAst.getLastChild());
	} else {

	    if (mAvoidEmptyMethodsAndConstructors) {
		handlePlacesForRedundantReturn(placeForRedundantReturn, aAst,
			aAst.getLastChild());
	    }

	    if (placeForRedundantReturn == TokenTypes.LITERAL_TRY) {
		submitRedundantReturnInTryCatch(aAst);
	    }
	}
    }

    /**
     * @param aType - Type of token, where redundant return is expected.
     * @param aAst - A method or constructor object block.
     * @param aLastChild - A last child of method or constructor object block.
     */
    private void handlePlacesForRedundantReturn(int aType, DetailAST aAst,
	    DetailAST aLastChild) {

	if (aType == TokenTypes.LITERAL_RETURN) {
	    log(aLastChild.getPreviousSibling().getLineNo());
	} else {
	    if (aType == TokenTypes.LITERAL_TRY) {
		submitRedundantReturnInTryCatch(aAst);
	    }
	}
    }

    /**
     * Check the try, catch, finally object blocks on redundant return content.
     * 
     * @param aAst - A method or constructor object block.
     */
    private void submitRedundantReturnInTryCatch(DetailAST aAst) {

	final DetailAST nodeTry = aAst.getFirstChild();
	
	DetailAST astBlockTry = nodeTry.getFirstChild();
	
	final boolean isblockTry = true;
	
	handleBlocksTryCatchFinally(astBlockTry, isblockTry);
	
	final int catchBlocksCount = nodeTry.getChildCount(TokenTypes.LITERAL_CATCH);	
	
	for (int i = 0; i < catchBlocksCount; i++) {
	    astBlockTry =  astBlockTry.getNextSibling();
	    handleBlocksTryCatchFinally(astBlockTry, !isblockTry);
	}
		
	handleBlocksTryCatchFinally(astBlockTry.getNextSibling(), !isblockTry);
    }

    /**
     * Submit a mistake if the try block have redundant return.
     * 
     * @param aAst - object block of operator try.
     */
    private void handleBlocksTryCatchFinally(DetailAST aAst, boolean blockTry) {

	if (aAst != null) {
	    
	    final DetailAST placeForRedundantReturn;
	    
	    if (blockTry){
		
		placeForRedundantReturn = aAst.getLastChild()
			.getPreviousSibling();
	    } else {
		
		placeForRedundantReturn = aAst.getLastChild()
			    .getLastChild().getPreviousSibling();
	    }

	    if (placeForRedundantReturn != null) {

		if (verifyBlocks(placeForRedundantReturn.getType())) {

		    log(placeForRedundantReturn.getLineNo());
		}
	    }
	}
    }


    private void log(int lineNumber){
	log(lineNumber,"redundant.return");
    }

    /**
     * Verify the try or catch or finally blocks on the content of
     * RedundantReturn.
     * 
     * @param aType - Type of token, where redundant return is expected.
     * @return - true if checked block contains a redundant return.
     */
    private boolean verifyBlocks(int aType) {
	return aType == TokenTypes.LITERAL_RETURN;
    }
}