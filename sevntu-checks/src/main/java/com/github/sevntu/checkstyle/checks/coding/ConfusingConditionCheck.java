////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2013  Oliver Burn
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

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * This check prevents negation within an "if" expression if "else" is present.
 * <br>
 * For example, rephrase: <br>
 * if (x != y) smth1(); else smth2(); as: if (x == y) smth2(); else smth1();
 * </p>
 * <p>
 * Examples:<br>
 * "if" expression contains negation<br>
 * <code>
 *  <pre>
 *  if (a != b && c != d)
 *      {
 *          smth1();
 *      }
 *      else
 *      {
 *          smth2();
 *      }
 *  </pre>
 * </code> You can escape of negation in "if" expression<br>
 * and swapped code in "if" and "else" block:<br>
 * <code>
 *  <pre>
 *  if (a == b && c == d)
 *      {
 *          smth2();
 *      }
 *      else
 *      {
 *          smth1();
 *      }
 *  </pre>
 * </code>
 * </p>
 * 
 * @author <a href="mailto:vadim.panasiuk@gmail.com">Vadim Panasiuk</a>
 */
public class ConfusingConditionCheck extends Check {
	/**
	 * The key is pointing to the message text String in
	 * "messages.properties file".This message used for common cases.
	 */
	public static final String MSG_KEY = "confusing.condition.check";

	/**
	 * Number which defines, how many lines of code in "if" block must be exceed
	 * line of code in "else" block for this check was ignored.
	 */
	private static final int MULTIPLY_FACTOR_FOR_ELSE_BLOCK = 4;

	/**
	 * Allow to ignore "else" block if its length is in
	 * "multiplyFactorForElseBlocks" time less then "if" block.
	 */
	private int multiplyFactorForElseBlocks = MULTIPLY_FACTOR_FOR_ELSE_BLOCK;

	/**
	 * Disable warnings for all "if" that follows the "else". It is useful for
	 * save similarity with all "if-then-else" statement.
	 */
	private boolean ignoreInnerIf = true;

	/**
	 * Enable(true) | Disable(false) warnings for all inner "if".
	 * 
	 * @param aIgnoreInnerIf
	 */
	public void setIgnoreInnerIf(final boolean aIgnoreInnerIf) {
		ignoreInnerIf = aIgnoreInnerIf;
	}

	/**
	 * Disable warnings for all sequential "if".
	 */
	private boolean ignoreSequentialIf = true;

	/**
	 * Enable(true) | Disable(false) warnings for all "if" that follows the
	 * "else".
	 * 
	 * @param ignoreSequentialIf
	 */
	public void setIgnoreSequentialIf(final boolean ignoreSequentialIf) {
		this.ignoreSequentialIf = ignoreSequentialIf;
	}

	/**
	 * Disable warnings for "if" if it condition contains "null".
	 */
	private boolean ignoreNullCaseInIf = true;

	/**
	 * Disable(true) | Enable(false) warnings.
	 * 
	 * @param ignoreNullCaseInIf
	 *            if true disable warnings for "if".
	 */
	public void setIgnoreNullCaseInIf(final boolean ignoreNullCaseInIf) {
		this.ignoreNullCaseInIf = ignoreNullCaseInIf;
	}

	/**
	 * Disable warnings for "if" if "else" block contain "throw".
	 */
	private boolean ignoreThrowInElse = true;

	/**
	 * Disable(true) | Enable(false) warnings.
	 * 
	 * @param ignoreThrowInElse
	 *            if true disable warnings for "if".
	 */
	public void setIgnoreThrowInElse(final boolean ignoreThrowInElse) {
		this.ignoreThrowInElse = ignoreThrowInElse;
	}

	/**
	 * Sets multiplyFactorForElseBlocks field.
	 * 
	 * @param aMultiplyFactorForElseBlocks
	 *            define multiplyFactorForElseBlocks field.
	 * @see ConfusingConditionCheck#MultiplyFactorForElseBlocks
	 */
	public void setMultiplyFactorForElseBlocks(int multiplyFactorForElseBlocks) {
		multiplyFactorForElseBlocks = multiplyFactorForElseBlocks;
	}

	@Override
	public int[] getDefaultTokens() {
		return new int[] { TokenTypes.LITERAL_IF };
	}

	@Override
	public void visitToken(DetailAST literalIf) {
		if (isIfEndsWithElse(literalIf)
				&& !(ignoreSequentialIf && isSequentialIf(literalIf))
				&& !(ignoreInnerIf && isInnerIf(literalIf))
				&& !(ignoreThrowInElse && isElseWithThrow(literalIf))) {
			if (isRatioBetweenIfAndElseBlockSuitable(literalIf)
					&& !(ignoreNullCaseInIf && isIfWithNull(literalIf))
					&& isConditionAllNegative(literalIf)) {
				log(literalIf.getLineNo(), MSG_KEY);
			}
		}
	}

	/**
	 * If ELSE following the IF block.
	 * 
	 * @param aLastChildAfterIf
	 * @return
	 */
	private static boolean isIfEndsWithElse(DetailAST literalIf) {
		final DetailAST lastChildAfterIf = literalIf.getLastChild();
		return lastChildAfterIf.getType() == TokenTypes.LITERAL_ELSE;
	}

	/**
	 * Check the sequential IF or not.
	 * 
	 * @param literalIf
	 * @param ignoreSequentialIf
	 * @return
	 */
	private static boolean isSequentialIf(DetailAST literalIf) {
		final DetailAST lastChildAfterIf = literalIf.getLastChild();
		final boolean isSequentialIf = lastChildAfterIf.getFirstChild()
				.getType() == (TokenTypes.LITERAL_IF);
		return isSequentialIf;
	}

	/**
	 * Check the inner IF or not.
	 * 
	 * @param literalIf
	 * @param ignoreInnerIf
	 * @return
	 */
	private static boolean isInnerIf(DetailAST literalIf) {
		final DetailAST childIf = literalIf.getFirstChild().getNextSibling()
				.getNextSibling().getNextSibling();
		return childIf.branchContains(TokenTypes.LITERAL_IF);
	}

	/**
	 * Check IF - ELSE or not that contained THROW in the expression in a block
	 * ELSE.
	 * 
	 * @param literalIf
	 * @param ignoreThrowInElse
	 * @return
	 */
	private static boolean isElseWithThrow(DetailAST literalIf) {
		final DetailAST lastChildAfterIf = literalIf.getLastChild();
		return lastChildAfterIf.getFirstChild().branchContains(
				TokenTypes.LITERAL_THROW);
	}

	/**
	 * Display if the ratio of the number of rows in an IF and ELSE. If the
	 * condition is met, checkIfElseCodeLinesRatio = true.
	 * 
	 * @param literalIf
	 * @return If the condition is met (true) |Isn't men (false).
	 */
	private boolean isRatioBetweenIfAndElseBlockSuitable(DetailAST literalIf) {
		boolean result = true;

		final DetailAST lastChildAfterIf = literalIf.getLastChild();
		final int linesOfCodeInIfBlock = getAmounOfCodeRowsInBlock(literalIf);
		final int linesOfCodeInElseBlock = getAmounOfCodeRowsInBlock(lastChildAfterIf);
		if (linesOfCodeInElseBlock > 0) {
			result = linesOfCodeInIfBlock / linesOfCodeInElseBlock < multiplyFactorForElseBlocks;
		}
		return result;
	}

	/**
	 * Counts code lines in block IF or ELSE tree.
	 * 
	 * @param DetailAST
	 *            aDetailAST.
	 * @return linesOfCodeInIfBlock line of code in block.
	 */
	private static int getAmounOfCodeRowsInBlock(DetailAST detailAST) {
		DetailAST firstBrace = null;
		if (detailAST.getType() == TokenTypes.LITERAL_ELSE) {
			firstBrace = detailAST.getFirstChild();
		} else if (detailAST.getType() == TokenTypes.LITERAL_IF) {
			firstBrace = detailAST.getFirstChild().getNextSibling()
					.getNextSibling().getNextSibling();
		}
		DetailAST lastBrace = firstBrace.getLastChild();
		int linesOfCodeInIfBlock = lastBrace.getLineNo()
				- firstBrace.getLineNo();
		// If the closing brace on a separate line - ignore this line.
		if (lastBrace.getLineNo() != lastBrace.getParent().getLineNo()) {
			linesOfCodeInIfBlock -= 1;
		}
		return linesOfCodeInIfBlock;
	}

	/**
	 * Number of comparison operators in IF must be one less than negative
	 * symbols
	 * 
	 * @param literalIf
	 * @return
	 */
	private static boolean isConditionAllNegative(DetailAST literalIf) {
		boolean result = false;

		final DetailAST ifExpr = literalIf.getFirstChild().getNextSibling();
		final int countOfLnot = getCountOfToken(ifExpr, TokenTypes.LNOT);
		final int countOfNotequal = getCountOfToken(ifExpr,
				TokenTypes.NOT_EQUAL);
		int countOfNegativeSymbolInIf = countOfLnot + countOfNotequal;
		if (countOfNegativeSymbolInIf > 0) {
			final int countOfLand = getCountOfToken(ifExpr, TokenTypes.LAND);
			final int countOfLor = getCountOfToken(ifExpr, TokenTypes.LOR);
			int countOfComparisonOperators = countOfLand + countOfLor;
			if (countOfNegativeSymbolInIf - countOfComparisonOperators == 1) {
				result = true;
			}
		}
		return result;
	}

	/**
	 * Check IF or not that contained NULL in the expression IF.
	 * 
	 * @param literalIf
	 * @see ignoreNullCaseInIf
	 * @return
	 */

	private static boolean isIfWithNull(DetailAST literalIf) {
		return literalIf.getFirstChild().getNextSibling()
				.branchContains(TokenTypes.LITERAL_NULL);
	}

	/**
	 * Recursive method which counts a tokens of the provided type in detAst
	 * tree.
	 * 
	 * @param DetailAST
	 *            detAst a tree for "atype" tokens searching.
	 * @param aType
	 *            a TokenType
	 */
	private static int getCountOfToken(DetailAST detAst, int type) {
		int count = 0;
		if (detAst.branchContains(type)) {
			while (detAst != null) {
				count += detAst.getChildCount(type);
				final DetailAST detAstChild = detAst.getFirstChild();
				if (detAstChild == null) {
					detAst = detAst.getNextSibling();
				} else {
					count += getCountOfToken(detAstChild, type);
					detAst = detAst.getNextSibling();
				}

			}
		}
		return count;
	}

}
