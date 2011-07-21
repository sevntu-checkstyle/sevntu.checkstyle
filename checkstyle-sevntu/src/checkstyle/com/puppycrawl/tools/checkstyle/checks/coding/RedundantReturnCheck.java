package com.puppycrawl.tools.checkstyle.checks.coding;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * highlight the redundant returns in constructors and void methods, 'return' in
 * empty void method and constructors may be allowed
 * 
 * @author <a href="mailto:fishh1991@gmail.com">Troshin Sergey</a>
 */
public class RedundantReturnCheck extends Check {

    /**
     * true, if 'return' in empty constructors and void methods is allowed
     * default value - true
     */
    private boolean ignoreEmptyConstructors = true;

    public void setIgnoreEmptyConstructors(String aIgnoreEmptyConstructors) {
	ignoreEmptyConstructors = "true"
		.equalsIgnoreCase(aIgnoreEmptyConstructors);
    }

    @Override
    public int[] getDefaultTokens() {
	return new int[] { TokenTypes.CTOR_DEF, TokenTypes.METHOD_DEF };
    }

    @Override
    public void visitToken(DetailAST aConstructorOrMethod) {
	final DetailAST methodObjectBlock = aConstructorOrMethod.getLastChild();
	final DetailAST methodReturnType = aConstructorOrMethod.getFirstChild()
		.getNextSibling();

	switch (aConstructorOrMethod.getType()) {
	case TokenTypes.CTOR_DEF:
	    checkForRedundantReturn(methodObjectBlock);
	    break;

	case TokenTypes.METHOD_DEF:
	    if (methodReturnType.findFirstToken(TokenTypes.LITERAL_VOID) != null) {
		checkForRedundantReturn(methodObjectBlock);
	    }
	    break;

	default:
	    throw new IllegalStateException(aConstructorOrMethod.getText());
	}
    }

    /**
     * return is redundant if he is on the end of objectBlock and the
     * objectBlock of the method divided into several tokens
     */
    public void checkForRedundantReturn(DetailAST aConstructorOrMethodObjectBlock) {

	final DetailAST rCurlyOfObjectBlock = aConstructorOrMethodObjectBlock.getLastChild();
	final int methodChildCount = aConstructorOrMethodObjectBlock.getChildCount();
	final int placeForRedundantReturn = rCurlyOfObjectBlock.getPreviousSibling()
		.getType();

	if (methodChildCount > 2) {
	    handlePlacesForRedundantReturn(placeForRedundantReturn, aConstructorOrMethodObjectBlock, rCurlyOfObjectBlock);
	} else {
	    if (!ignoreEmptyConstructors) {
		handlePlacesForRedundantReturn(placeForRedundantReturn, aConstructorOrMethodObjectBlock, rCurlyOfObjectBlock);
	    }
		if (placeForRedundantReturn == TokenTypes.LITERAL_TRY) {
		    submitRedundantReturnInTryCatch(aConstructorOrMethodObjectBlock);
		}
	}

    }


    public void handlePlacesForRedundantReturn(int aPlaceForRedundantReturn,
	    DetailAST aConstructorOrMethodObjectBlock, DetailAST rCurlyOfObjectBlock) {

	if (aPlaceForRedundantReturn == TokenTypes.LITERAL_RETURN) {
	    log(rCurlyOfObjectBlock.getPreviousSibling().getLineNo(), "redundant.return",
		    "");
	    return;
	}

	if (aPlaceForRedundantReturn == TokenTypes.LITERAL_TRY) {
	    submitRedundantReturnInTryCatch(aConstructorOrMethodObjectBlock);
	}

    }

    public void submitRedundantReturnInTryCatch(DetailAST aConstructorOrMethodObjectBlock) {
	
	final DetailAST nodeTry = aConstructorOrMethodObjectBlock.getFirstChild();
	final DetailAST objectBlockTRY = nodeTry.getFirstChild();
	final DetailAST objectBlockCatch = nodeTry
		.findFirstToken(TokenTypes.LITERAL_CATCH);
	final DetailAST objectBlockFinally = nodeTry
		.findFirstToken(TokenTypes.LITERAL_FINALLY);
	DetailAST placeForRedundantReturn;

	if (objectBlockTRY != null) {
	    placeForRedundantReturn = objectBlockTRY.getLastChild().getPreviousSibling();

	    if (placeForRedundantReturn != null) {
		if (verifyTryCatchFinallyBlocks(objectBlockTRY.getChildCount(),
			placeForRedundantReturn.getType())) {
		    log(placeForRedundantReturn.getLineNo(), "redundant.return", "");
		}
	    }
	}

	if (objectBlockCatch != null) {
	    placeForRedundantReturn = objectBlockCatch.getLastChild().getLastChild()
		    .getPreviousSibling();

	    if (placeForRedundantReturn != null) {
		if (verifyTryCatchFinallyBlocks(
			objectBlockCatch.getChildCount(), placeForRedundantReturn.getType())) {
		    		log(placeForRedundantReturn.getLineNo(),"redundant.return", "");
		}
	    }
	}

	if (objectBlockFinally != null) {
	    placeForRedundantReturn = objectBlockFinally.getLastChild().getLastChild()
		    .getPreviousSibling();

	    if (placeForRedundantReturn != null) {
		if (verifyTryCatchFinallyBlocks(
			objectBlockFinally.getChildCount(), placeForRedundantReturn.getType())) {
		    log(placeForRedundantReturn.getLineNo(),"redundant.return", "");
		}
	    }
	}
    }

    public boolean verifyTryCatchFinallyBlocks(int count,
	    int placeForRedundantReturn) {

	if ((count > 2)
		&& (placeForRedundantReturn == TokenTypes.LITERAL_RETURN)) {
	    return true;
	}

	if ((!ignoreEmptyConstructors)
		&& (placeForRedundantReturn == TokenTypes.LITERAL_RETURN)) {
	    return true;
	} else {
	    return false;
	}
    }

}
