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

import java.util.ArrayList;
import java.util.List;

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
	private boolean allowReturnInEmptyMethodsAndConstructors = false;

	public void setAllowReturnInEmptyMethodsAndConstructors(boolean allowEmptyBlocks) {
		allowReturnInEmptyMethodsAndConstructors = allowEmptyBlocks;
	}

	@Override
	public int[] getDefaultTokens() {
		return new int[] { TokenTypes.CTOR_DEF, TokenTypes.METHOD_DEF };
	}

	@Override
	public void visitToken(DetailAST ast) {

		final DetailAST blockAst = ast.getLastChild();

		switch (ast.getType()) {
			case TokenTypes.CTOR_DEF:
				if (!ignoreLonelyReturn(blockAst) && hasNonEmptyBody(ast)) {
					List<DetailAST> redundantReturns = getRedundantReturns(blockAst);
					log(redundantReturns);
				}
				break;

			case TokenTypes.METHOD_DEF:
				if (!ignoreLonelyReturn(blockAst) && isVoidMethodWithNonEmptyBody(ast)) {
					List<DetailAST> redundantReturns = getRedundantReturns(blockAst);
					log(redundantReturns);
				}
				break;

			default:
				final String exceptionMsg = "Unexpected TokenType - " 
						+ TokenTypes.getTokenName(ast.getType());
				throw new IllegalStateException(exceptionMsg);
			}
	}

	/**
	 * Ignores method or constructor if it contains <b>only</> return statement
	 * in its body
	 * @param objectBlockAst
	 */
	private boolean ignoreLonelyReturn(DetailAST objectBlockAst) {
		
		return allowReturnInEmptyMethodsAndConstructors 
					&& objectBlockAst.getFirstChild().getType() 
						== TokenTypes.LITERAL_RETURN;
	}
	
	/**
	 * Checks if method's or ctor's body is not empty
	 * @param defAst
	 */
	private static boolean hasNonEmptyBody(DetailAST defAst) {
		return defAst.getLastChild().getChildCount() > 1;
	}
	
	/**
	 * Checks if method is void and has a body
	 * @param methodDefAst
	 */
	private static boolean isVoidMethodWithNonEmptyBody(DetailAST methodDefAst) {
		
		return methodDefAst.getLastChild().getType() == TokenTypes.SLIST 
					&& methodDefAst.findFirstToken(TokenTypes.TYPE)
						.findFirstToken(TokenTypes.LITERAL_VOID) != null
					&& hasNonEmptyBody(methodDefAst);
	}

	/**
	 * Puts violation on each redundant return met in object block of 
	 * method or ctor
	 * @param redundantReturnsAst
	 */
	private void log(List<DetailAST> redundantReturnsAst) {
		for (DetailAST redundantLiteralReturnAst : redundantReturnsAst) {
			log(redundantLiteralReturnAst.getLineNo(), MSG_KEY);
		}
	}

	/**
	 * Returns the list of redundant returns found in method's or ctor's
	 * object block
	 * @param objectBlockAst
	 *            - a method or constructor object block
	 * @return list of redundant returns or empty list if none were found
	 */
	private static List<DetailAST> getRedundantReturns(DetailAST objectBlockAst) {
		
		List<DetailAST> redundantReturns = new ArrayList<DetailAST>();
		
		final int placeForRedundantReturn = objectBlockAst
			.getLastChild().getPreviousSibling().getType();

		if (placeForRedundantReturn == TokenTypes.LITERAL_RETURN) {

			final DetailAST lastChildAst = objectBlockAst.getLastChild();

			final DetailAST redundantReturnAst = lastChildAst.getPreviousSibling();
			
			redundantReturns.add(redundantReturnAst);
			
		} else if (placeForRedundantReturn == TokenTypes.LITERAL_TRY 
				&& !getRedundantReturnsInTryCatchBlock(objectBlockAst
					.findFirstToken(TokenTypes.LITERAL_TRY)).isEmpty()) {
			
			final List<DetailAST> redundantsAst 
				= getRedundantReturnsInTryCatchBlock(objectBlockAst
				    .findFirstToken(TokenTypes.LITERAL_TRY));
			
			redundantReturns.addAll(redundantsAst);
		}
		
		return redundantReturns;
	}

	/**
	 * Returns the list of redundant returns found in try, catch, finally object blocks.
	 * @param tryAst
	 *            - Ast that contain a try node.
	 * @return list of redundant returns or empty list if none were found
	 */
	private static List<DetailAST> getRedundantReturnsInTryCatchBlock(DetailAST tryAst) {

		List<DetailAST> redundantReturns = new ArrayList<DetailAST>();
		
		DetailAST tryBlockAst = null;
		
		if (tryAst.getFirstChild().getType() != TokenTypes.RESOURCE_SPECIFICATION) {
			tryBlockAst = tryAst.getFirstChild();
		} else {
			tryBlockAst = tryAst.getFirstChild().getNextSibling();
		}
		
		DetailAST redundantReturnAst =
			getRedundantReturnInBlock(tryBlockAst.getLastChild().getPreviousSibling());
			
		// if redundant return is in try block
		if (redundantReturnAst != null) {
			redundantReturns.add(redundantReturnAst);
		}

		DetailAST blockAst = tryBlockAst;
		
		// look for redundant returns in all catches
		for (DetailAST catchBlockAst = getNextCatchBlock(blockAst);
				catchBlockAst != null;) {
			DetailAST lastStatementOfCatchBlockAst = catchBlockAst
				.getLastChild().getLastChild().getPreviousSibling();
				
			if (lastStatementOfCatchBlockAst != null) {
				redundantReturnAst 
					= getRedundantReturnInBlock(lastStatementOfCatchBlockAst);
				
				if (redundantReturnAst != null) {
					redundantReturns.add(redundantReturnAst);
				}
			}
			blockAst = blockAst.getNextSibling();
			catchBlockAst = getNextCatchBlock(blockAst);
		}

		// if redundant return is in finally block
		if (blockAst.getNextSibling() != null) {
				
			DetailAST afterCatchBlockAst = blockAst.getNextSibling().getLastChild()
				.getLastChild();
				
			redundantReturnAst
				= getRedundantReturnInBlock(afterCatchBlockAst.getPreviousSibling());
				
			if (redundantReturnAst != null) {
				redundantReturns.add(redundantReturnAst);
			}
		}
			
		return redundantReturns;
	}

	/**
	 * Gets next catch block in try block if exists
	 * @param blockAst
	 * @return next found catchBlockAst, if no catch was found - returns null
	 */
	private static DetailAST getNextCatchBlock(DetailAST blockAst) {
		DetailAST catchBlockAst = null;
		if (blockAst.getNextSibling() != null 
				&& blockAst.getNextSibling().getType() == TokenTypes.LITERAL_CATCH) {
			
			catchBlockAst = blockAst.getNextSibling();
		}
		return catchBlockAst;
	}

	/**
	 * Returns redundant return from try-catch-finally block.
	 * @param statementAst
	 *            - a place where the redundantReturn is expected.
	 * @return redundant literal return if found, else null.
	 */
	private static DetailAST getRedundantReturnInBlock(DetailAST statementAst) {

		DetailAST redundantReturnAst = null;
		
		if (statementAst != null) {

			if (statementAst.getType() == TokenTypes.LITERAL_RETURN) {
				redundantReturnAst = statementAst;
			}
			else {
				if (statementAst.getFirstChild() != null) {
					DetailAST foundRedundantReturnAst = findRedundantReturnInCatch(statementAst);
					if (foundRedundantReturnAst != null) {
						redundantReturnAst = foundRedundantReturnAst;
					}
				}
			}
		}
		
		return redundantReturnAst;
	}

	/**
	 * Looks for literal return in the last statement of a catch block
	 * @param lastStatementInCatchBlockAst
	 * @return redundant literal return, if there's no one - returns null
	 */
	private static DetailAST findRedundantReturnInCatch(DetailAST lastStatementInCatchBlockAst) {
		DetailAST redundantReturnAst = null;
		DetailAST currentNodeAst = lastStatementInCatchBlockAst;
		DetailAST toVisitAst = currentNodeAst;
		DetailAST returnAst = null;
		while (toVisitAst != null) {
			
			toVisitAst = getNextSubTreeNode(toVisitAst, currentNodeAst);
			
			if (toVisitAst != null 
					&& (toVisitAst.getParent().getParent().getNextSibling() == null 
						|| toVisitAst.getParent().getParent().getNextSibling().getType()
							== TokenTypes.RCURLY)
					&& toVisitAst.getType() == TokenTypes.LITERAL_RETURN
					&& toVisitAst.getParent().getNextSibling()== null) {
				
				returnAst = toVisitAst;
				
				while (toVisitAst != null 
							&& toVisitAst.getParent() != currentNodeAst.getLastChild()) {
					
					toVisitAst = toVisitAst.getParent();
				}
				
				if (toVisitAst != null) {
					redundantReturnAst = returnAst;
				}
				
				toVisitAst = returnAst;
			}
		}
		
		currentNodeAst = getNextSubTreeNode(currentNodeAst, lastStatementInCatchBlockAst);
		return redundantReturnAst;
			
	}

	/**
	 * Gets the next node of a syntactical tree (child of a current node or
	 * sibling of a current node, or sibling of a parent of a current node)
	 * @param currentNodeAst
	 *        Current node in considering
	 * @return Current node after bypassing, if current node reached the root of a subtree
	 * 		  method returns null
	 */
	private static DetailAST 
		getNextSubTreeNode(DetailAST currentNodeAst, DetailAST subTreeRootAst) {
		
		DetailAST toVisitAst = currentNodeAst.getFirstChild();
		while (toVisitAst == null) {
			toVisitAst = currentNodeAst.getNextSibling();
			if (toVisitAst == null) {
				if (currentNodeAst.getParent().equals(subTreeRootAst)) {
					break;
				}
				currentNodeAst = currentNodeAst.getParent();
			}
		}
		currentNodeAst = toVisitAst;
		return currentNodeAst;
	}

}
