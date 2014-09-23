////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2012 Oliver Burn
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
////////////////////////////////////////////////////////////////////////////////

package com.github.sevntu.checkstyle.checks.coding;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * Highlights usage of redundant returns inside constructors and methods with void
 * result.
 * </p>
 * <p>
 * For example:
 * </p>
 * <p>
 * 1. Non-empty constructor
 * </p>
 * <code><pre>
 *    public HelloWorld(){
 *        doStuff();
 *        return;
 *    }</pre></code>
 * <p>
 * 2. Method with void result
 * </p>
 * <code><pre>
 *    public void testMethod1(){
 *        doStuff();
 *        return;
 *    }</pre></code>
 * <p>
 * However, if your IDE does not support breakpoints on the method entry, you
 * can allow the use of redundant returns in constructors and methods with void
 * result without code except for 'return;'.
 * </p>
 * <p>
 * For example:
 * </p>
 * <p>
 * 1. Empty constructor
 * </p>
 * <code><pre>
 *    public HelloWorld(){
 *        return;
 *    }</pre></code>
 * <p>
 * 2. Method with void result and empty body
 * </p>
 * <code><pre>
 *    public void testMethod1(){
 *        return;
 *    }</pre></code>
 * @author <a href="mailto:fishh1991@gmail.com">Troshin Sergey</a>
 * @author <a href="mailto:maxvetrenko2241@gmail.com">Max Vetrenko</a>
 * @author <a href="mailto:nesterenko-aleksey@list.ru">Alexey Nesterenko</a>
 */
public class RedundantReturnCheck extends Check {

	public static final String MSG_KEY = "redundant.return.check";

	/**
	 *  If True, allow 'return' in empty constructors and methods that return void.
	 */
	private boolean mAllowReturnInEmptyMethodsAndConstructors = false;

	public void setAllowReturnInEmptyMethodsAndConstructors(boolean aAllowEmptyBlocks) {
		mAllowReturnInEmptyMethodsAndConstructors = aAllowEmptyBlocks;
	}

	@Override
	public int[] getDefaultTokens() {
		return new int[] { TokenTypes.CTOR_DEF, TokenTypes.METHOD_DEF };
	}

	@Override
	public void visitToken(DetailAST aAst) {

		final DetailAST blockAst = aAst.getLastChild();

		switch (aAst.getType()) {
			case TokenTypes.CTOR_DEF:
				if (hasNonEmptyBody(aAst)) {
					findPlacesForRedundantReturn(blockAst);
				}
				break;

			case TokenTypes.METHOD_DEF:
				if (isVoidMethodWithNonEmptyBody(aAst)) {
					findPlacesForRedundantReturn(blockAst);
				}
				break;

			default:
				final String exceptionMsg = "Unexpected TokenType - " 
					+ TokenTypes.getTokenName(aAst.getType());
				throw new IllegalStateException(exceptionMsg);
			}
	}

	/**
	 * Checks if method is void and has a body
	 * @param aMethodDefAst
	 */
	private static boolean isVoidMethodWithNonEmptyBody(DetailAST aMethodDefAst) {
		
		return aMethodDefAst.getLastChild().getType() == TokenTypes.SLIST 
				&& aMethodDefAst.findFirstToken(TokenTypes.TYPE).findFirstToken(
				TokenTypes.LITERAL_VOID) != null
				&& hasNonEmptyBody(aMethodDefAst);
	}

	/**
	 * Checks if method's or ctor's body is not empty
	 * @param aDefAst
	 */
	private static boolean hasNonEmptyBody(DetailAST aDefAst) {
		return aDefAst.getLastChild().getChildCount() > 1;
	}

	/**
	 * Return is redundant if it is on the end of objectBlock and the
	 * objectBlock of the method divided into several tokens.
	 * @param aObjectBlockAst
	 *            - a method or constructor object block
	 */
	private void findPlacesForRedundantReturn(DetailAST aObjectBlockAst) {
		
		final int placeForRedundantReturn = aObjectBlockAst
				.getLastChild().getPreviousSibling().getType();

		if (!ignoreLonelyReturn(aObjectBlockAst)) {
			handlePlacesForRedundantReturn(placeForRedundantReturn,
					aObjectBlockAst);
		}

	}

	/**
	 * Ignores method or constructor if it contains <b>only</> return statement
	 * in its body
	 * @param aObjectBlockAst
	 */
	private boolean ignoreLonelyReturn(DetailAST aObjectBlockAst) {
		
		return mAllowReturnInEmptyMethodsAndConstructors 
				&& aObjectBlockAst.getFirstChild().getType() 
				== TokenTypes.LITERAL_RETURN;
	}

	/**
	 * Handle and analyzes places for redundant return, if found - logging,
	 * else if there's try-catch block - looking for redundant return in that block
	 * @param aType
	 *            - Type of token, where redundant return is expected.
	 * @param aObjectBlockAst
	 *            - A method or constructor object block.
	 */
	private void handlePlacesForRedundantReturn(int aType,
			DetailAST aObjectBlockAst) {

		if (aType == TokenTypes.LITERAL_RETURN) {

			final DetailAST lastChildAst = aObjectBlockAst.getLastChild();

			final DetailAST redundantLiteralReturnAst = lastChildAst.getPreviousSibling();
			
			log(redundantLiteralReturnAst.getLineNo(), MSG_KEY);
		} else if (aType == TokenTypes.LITERAL_TRY) {
			checkTryCatchForRedundantReturn(aObjectBlockAst
					.findFirstToken(TokenTypes.LITERAL_TRY));
		}
	}

	/**
	 * Check the try, catch, finally object blocks on redundant return content.
	 * @param aTryAst
	 *            - Ast that contain a try node.
	 */
	private void checkTryCatchForRedundantReturn(DetailAST aTryAst) {

		DetailAST tryBlockAst = null;
		
		if (aTryAst.getFirstChild().getType() != TokenTypes.RESOURCE_SPECIFICATION) {
			tryBlockAst = aTryAst.getFirstChild();
		} else {
			tryBlockAst = aTryAst.getFirstChild().getNextSibling();
		}
		// if redundant return is in try block
		handleBlocksTryCatchFinally(tryBlockAst.getLastChild()
				.getPreviousSibling());

		while (getNextCatchBlock(tryBlockAst) != null) {
			DetailAST lastStatementOfCatchBlockAst = getNextCatchBlock(tryBlockAst)
					.getLastChild().getLastChild().getPreviousSibling();
			
			if (lastStatementOfCatchBlockAst != null) {
				// if redundant return is somewhere in catch blocks
				handleBlocksTryCatchFinally(lastStatementOfCatchBlockAst);
			}
			tryBlockAst = tryBlockAst.getNextSibling();
		}

		if (tryBlockAst.getNextSibling() != null) {
			
			DetailAST afterCatchBlockAst = tryBlockAst.getNextSibling().getLastChild()
					.getLastChild();
			// if redundant return is in finally block
			handleBlocksTryCatchFinally(afterCatchBlockAst.getPreviousSibling());
		}
		
	}
	
	/**
	 * Gets next catch block in try block if exists
	 * @param aTryBlockAst
	 * @return next found catchBlockAst, if no catch was found - returns null
	 */
	private static DetailAST getNextCatchBlock(DetailAST aTryBlockAst) {
		DetailAST catchBlockAst = null;
		if (aTryBlockAst.getNextSibling() != null 
				&& aTryBlockAst.getNextSibling().getType() == TokenTypes.LITERAL_CATCH) {
			catchBlockAst = aTryBlockAst.getNextSibling();
		}
		return catchBlockAst;
	}

	/**
	 * Submits a mistake if the try or catch or finally blocks has redundant
	 * return.
	 * @param aStatementAst
	 *            - a place where the redundantReturn is expected.
	 */
	private void handleBlocksTryCatchFinally(DetailAST aStatementAst) {

		if (aStatementAst != null) {

			if (aStatementAst.getType() == TokenTypes.LITERAL_RETURN) {
				log(aStatementAst.getLineNo(), MSG_KEY);
			}
			else {
				if (aStatementAst.getFirstChild() != null
						&& findRedundantLiteralReturn(aStatementAst) != null) {
					
					final DetailAST redundantLiteralReturnAst 
							= findRedundantLiteralReturn(aStatementAst);
					log(redundantLiteralReturnAst.getLineNo(),MSG_KEY);
				}
			}
		}
		
	}

	/**
	 * Looks for literal return in the last statement of a catch block
	 * @param aLastStatementInCatchBlockAst
	 * @return redundant literal return, if there's no one - returns null
	 */
	private static DetailAST findRedundantLiteralReturn(DetailAST aLastStatementInCatchBlockAst) {
		DetailAST redundantReturnAst = null;
		DetailAST currentNodeAst = aLastStatementInCatchBlockAst;
		DetailAST toVisitAst = currentNodeAst;
		DetailAST literalReturnAst = null;
		while (toVisitAst != null) {
			
			toVisitAst = getNextSubTreeNode(toVisitAst, currentNodeAst);
			
			if (toVisitAst != null 
					&& (toVisitAst.getParent().getParent().getNextSibling() == null 
					|| toVisitAst.getParent().getParent().getNextSibling().getType() 
						== TokenTypes.RCURLY)  
					&& toVisitAst.getType() == TokenTypes.LITERAL_RETURN
					&& toVisitAst.getParent().getNextSibling() == null) {
				literalReturnAst = toVisitAst;
				
				while (toVisitAst != null 
						&& toVisitAst.getParent() != currentNodeAst.getLastChild()) {
					toVisitAst = toVisitAst.getParent();
				}
				
				if (toVisitAst != null) {
					redundantReturnAst = literalReturnAst;
				}
				
				toVisitAst = literalReturnAst;
			}
		}
		
		currentNodeAst = getNextSubTreeNode(currentNodeAst, aLastStatementInCatchBlockAst);
		return redundantReturnAst;
		
	}

	/**
	 * Gets the next node of a syntactical tree (child of a current node or
	 * sibling of a current node, or sibling of a parent of a current node)
	 * @param aCurrentNodeAst
	 *        Current node in considering
	 * @return Current node after bypassing, if current node reached the root of a subtree
	 * 		  method returns null
	 */
	private static DetailAST 
				getNextSubTreeNode(DetailAST aCurrentNodeAst, DetailAST aSubTreeRootAst) {
		
		DetailAST toVisitAst = aCurrentNodeAst.getFirstChild();
		while (toVisitAst == null) {
			toVisitAst = aCurrentNodeAst.getNextSibling();
			if (toVisitAst == null) {
				if (aCurrentNodeAst.getParent().equals(aSubTreeRootAst)) {
					break;
				}
				aCurrentNodeAst = aCurrentNodeAst.getParent();
			}
		}
		aCurrentNodeAst = toVisitAst;
		return aCurrentNodeAst;
	}

}
