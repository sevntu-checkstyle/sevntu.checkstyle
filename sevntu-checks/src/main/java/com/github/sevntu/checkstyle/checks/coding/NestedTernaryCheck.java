////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2012  Oliver Burn
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
package com.github.sevntu.checkstyle.checks.coding;

import java.util.ArrayList;
import java.util.List;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * This check highlights the usage of nested ternary operators. <br>
 * Example of nested ternary operator: <br>
 * <p>
 * <code>final int d = (a == b) ? (a == b) ? 5 : 6 : 6;</code>
 * </p>
 * 
 * This check also have an option which allows to ignore nested ternary operators in case if they are used for
 * initialization of final variables. But this option doesn`t ignore nested ternary operators which initializes final
 * variables in constructors. <br>
 * <br>
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com">Daniil Yaroslavstev</a>
 */
public class NestedTernaryCheck extends Check {

	/**
	 * A key is pointing to the warning message text in "messages.properties" file.
	 * */
	public static final String MSG_KEY = "nested.ternary";

	/** The 'Ignore Final' option. */
	private boolean mIgnoreFinal;

	/**
	 * Sets the value for 'Ignore Final' option.
	 * 
	 * @param aValue
	 *            the new 'ignore final' option value
	 */
	public void setIgnoreFinal(final boolean aValue) {
		mIgnoreFinal = aValue;
	}

	@Override
	public int[] getDefaultTokens() {
		return new int[] { TokenTypes.QUESTION };
	}

	@Override
	public void visitToken(DetailAST questionAst) {
		if (isTernaryOperator(questionAst)) {

			final DetailAST nestedQuestionAst = getChildNode(questionAst, TokenTypes.QUESTION);

			if (nestedQuestionAst != null && isTernaryOperator(nestedQuestionAst)) {
				if (mIgnoreFinal && isFinalVariableAssignment(questionAst)) {
					if (isInCtor(questionAst)) {
						log(nestedQuestionAst, MSG_KEY);
					}
				} else {
					log(nestedQuestionAst, MSG_KEY);
				}
			}
		}
	}

	/**
	 * Checks if the question AST node is a part of ternary operator.
	 * 
	 * @param questionAst
	 *            the question AST node
	 * @return true, if is ternary operator
	 */
	private static boolean isTernaryOperator(DetailAST questionAst) {
		return (getChildNode(questionAst, TokenTypes.COLON)) != null;
	}

	/**
	 * Checks (by the question AST node) if the ternary operator is used for final variable inline assignment.
	 * 
	 * @param questionAst
	 *            the question AST node for given ternary operator
	 * @return true, if the ternary operator is used for final variable inline assignment and false otherwise
	 */
	private static boolean isFinalVariableAssignment(DetailAST questionAst) {
		DetailAST variableDefAst = getParent(questionAst, TokenTypes.VARIABLE_DEF);
		if (variableDefAst != null) {
			return hasModifier(variableDefAst, TokenTypes.FINAL);
		} else {
			return false;
		}
	}

	/**
	 * Checks (by the question AST node) if the ternary operator is located in c-tor.
	 * 
	 * @param questionAst
	 *            the question AST node for given ternary operator
	 * @return true, if the ternary operator is present in ctor and false otherwise
	 */
	private static boolean isInCtor(DetailAST questionAst) {
		return getParent(questionAst, TokenTypes.CTOR_DEF) != null;
	}

	/**
	 * Checks if method or class has a specified modifier (private, final etc).
	 * 
	 * @param aMethodOrClassDefAST
	 *            A METHOD_DEF (or CLASS_DEF) DetailAST node.
	 * @param aModifierType
	 *            desired modifier type.
	 * @return true if method is related to current aMethodDefAST METHOD_DEF node has a modifier of given type and false
	 *         otherwise.
	 */
	private static boolean hasModifier(final DetailAST aMethodOrClassDefAST, int aModifierType) {

		boolean result = false;

		final DetailAST modifiers = aMethodOrClassDefAST.findFirstToken(TokenTypes.MODIFIERS);

		if (modifiers != null && modifiers.getChildCount() != 0) {
			for (DetailAST curNode : getChildNodes(modifiers)) {
				if (curNode.getType() == aModifierType) {
					result = true;
					break;
				}
			}
		}

		return result;
	}

	/**
	 * Gets the parent node of given type for given child node. Searches among all parents on all levels.
	 * 
	 * @param aNode
	 *            the child node
	 * @param parentNodeType
	 *            the parent node type
	 * @return the parent node of given type for given DetailAST node or null if this node hasn`t a parent with given
	 *         type.
	 */
	private static DetailAST getParent(final DetailAST aNode, int parentNodeType) {

		DetailAST curNode = aNode;

		while (curNode != null && curNode.getType() != parentNodeType) {
			curNode = curNode.getParent();
		}
		return curNode;
	}

	/**
	 * Gets the child node of given type for given DetailAST node.
	 * 
	 * @param aAst
	 *            the parent node
	 * @param tokenType
	 *            the given token type
	 * @return the child node of given type
	 */
	private static DetailAST getChildNode(DetailAST aAst, int tokenType) {
		for (DetailAST childNode : getChildNodes(aAst)) {
			if (childNode.getType() == tokenType) {
				return childNode;
			}
		}
		return null;
	}

	/**
	 * Gets all the children one level below on the current parent node.
	 * 
	 * @param aNode
	 *            the parent node.
	 * @return list of children one level below on the current parent node (aNode).
	 */
	private static List<DetailAST> getChildNodes(DetailAST aNode) {

		final ArrayList<DetailAST> result = new ArrayList<DetailAST>(aNode.getChildCount());

		DetailAST currNode = aNode.getFirstChild();
		while (currNode != null) {
			result.add(currNode);
			currNode = currNode.getNextSibling();
		}
		return result;
	}

}
