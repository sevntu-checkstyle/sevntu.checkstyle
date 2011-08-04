////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2010  Oliver Burn
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////
package com.puppycrawl.tools.checkstyle.checks.design;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import antlr.collections.ASTEnumeration;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * Checks distance between declaration of variable and its first usage.
 * </p>
 * <p>
 * Example1:
 * </p>
 * <pre>
 *      <code>int count;
 *      a = a + b;
 *      b = a + a;
 *      count = b; // DECLARATION OF VARIABLE 'count' SHOULD BE HERE (distance = 3)</code>
 * </pre>
 * Example2:
 * <pre>
 *     <code>int count;
 *     {
 *         a = a + b;
 *         count = b; // DECLARATION OF VARIABLE 'count' SHOULD BE HERE (distance = 2)
 *     }</code>
 * </pre>
 * <p>
 * There is an additional option to ignore distance calculation for variables listed in RegExp.
 * </p>
 *
 * @author <a href="mailto:rd.ryly@gmail.com">Ruslan Diachenko</a>
 */
public class VariableDeclarationUsageDistanceCheck extends Check {

	/**	Allowed distance between declaration of variable and its first usage. */
	private int mAllowedDistance = 1;

	/** RegExp pattern to ignore distance calculation for variables listed in this pattern. */
	private Pattern mIgnoreVariablePattern = Pattern.compile("");

	/** Identifies if variable was used after its declaration. */
	private boolean mVariableFound;

	/**
	 * Sets an allowed distance between declaration of variable and its first usage.
	 * @param aAllowedDistance Allowed distance between declaration of variable and its first usage.
	 */
	public void setAllowedDistance(int aAllowedDistance) {
		this.mAllowedDistance = aAllowedDistance;
	}

	/**
	 * Sets RegExp pattern to ignore distance calculation for variables listed in this pattern.
	 * @param aIgnorePattern Pattern contains ignored variables.
	 */
	public void setIgnoreVariablePattern(String aIgnorePattern) {
		mIgnoreVariablePattern = Pattern.compile(aIgnorePattern);
	}

	@Override
	public int[] getDefaultTokens() {
		return new int[] { TokenTypes.VARIABLE_DEF, };
	}

	@Override
	public void visitToken(DetailAST aAST) {
		mVariableFound = false;
		int parentType = aAST.getParent().getType();
		DetailAST nextSibling = aAST.getNextSibling();
		if (parentType != TokenTypes.OBJBLOCK && nextSibling != null && nextSibling.getType() == TokenTypes.SEMI) {
			DetailAST variable = aAST.findFirstToken(TokenTypes.IDENT);
			if (!isVariableMatchesPattern(variable.getText())) {
				int dist = calculateDistance(nextSibling, variable);
				if (mVariableFound) {
					dist++;
					if (dist > mAllowedDistance && dist > 0) {
						log(variable.getLineNo(), "variable.declaration.usage.distance", dist, mAllowedDistance);
//						System.out.println("var = " + variable.getText() + "; dist = " + dist + "; error = " + variable.getLineNo());
					}
				}
			}
		}
	}

	/**
	 * Calculates distance between declaration of variable and its first usage.
	 * @param aAST Regular node of AST which is checked for content of checking variable.
	 * @param aVariable Variable which distance is calculated for.
	 * @return Distance between declaration of variable and its first usage.
	 */
	private int calculateDistance(DetailAST aAST, DetailAST aVariable) {
		int dist = 0;
		boolean variableFirstFound = false;
		DetailAST nextSibling = aAST;
		int variableNumInForBlock = 0;
		boolean forBlockMeet = false;
		List<DetailAST> exprWithVariableList = new ArrayList<DetailAST>();
		while (nextSibling != null && nextSibling.getType() != TokenTypes.RCURLY) {
			switch (nextSibling.getType()) {
			case TokenTypes.CASE_GROUP:
				break;
			case TokenTypes.FOR_INIT:
			case TokenTypes.FOR_CONDITION:
			case TokenTypes.FOR_ITERATOR:
			case TokenTypes.FOR_EACH_CLAUSE:
				forBlockMeet = true;
				if (isASTContainsElement(nextSibling, aVariable)) {
					variableNumInForBlock++;
				}
				break;
			default:
				if (nextSibling.getFirstChild() != null) {
					if (isASTContainsElement(nextSibling, aVariable)) {
						exprWithVariableList.add(nextSibling);
						mVariableFound = true;
						variableFirstFound = true;
					} else {
						if (!variableFirstFound) {
							dist++;
						}
					}
				}
			}
			nextSibling = nextSibling.getNextSibling();
		}

		if (forBlockMeet && variableNumInForBlock == 0) {
			dist++;
		}

		if (exprWithVariableList.size() != 0) {
			DetailAST blockWithVariable = exprWithVariableList.get(0);

			if (exprWithVariableList.size() == 1) {
				if (blockWithVariable.getType() != TokenTypes.VARIABLE_DEF
						&& blockWithVariable.getType() != TokenTypes.EXPR) {
					dist += calculateDistance(blockWithVariable.getFirstChild(), aVariable);
				}
			}
		} else {
			if (!variableFirstFound) {
				dist = 0;
			}
		}
		return dist;
	}

	/**
	 * Checks if AST node contains given element.
	 * @param aAST Node of AST.
	 * @param aElement AST element which is checked for content in AST node.
	 * @return true if AST element was found in AST node, otherwise - false.
	 */
	private boolean isASTContainsElement(DetailAST aAST, DetailAST aElement) {
		boolean isASTContainsElement = false;
		ASTEnumeration astList = aAST.findAllPartial(aElement);
		while (astList.hasMoreNodes()) {
			DetailAST astElement = (DetailAST) astList.nextNode();
			DetailAST astElementParent = astElement.getParent();
			while (astElementParent != null) {
				if (astElementParent.equals(aAST) && astElementParent.getLineNo() == aAST.getLineNo()) {
					isASTContainsElement = true;
					break;
				}
				astElementParent = astElementParent.getParent();
			}
		}
		return isASTContainsElement;
	}

	/**
	 * Checks if entrance variable is contained in ignored pattern.
	 * @param aVariable Variable which is checked for content in ignored pattern.
	 * @return true if variable was found, otherwise - false.
	 */
	private boolean isVariableMatchesPattern(String aVariable) {
		Matcher matcher = mIgnoreVariablePattern.matcher(aVariable);
		return matcher.matches();
	}
}
