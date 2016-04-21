////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2016 the original author or authors.
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
package com.github.sevntu.checkstyle.checks.design;

import java.util.Set;

import com.github.sevntu.checkstyle.Utils;
import com.google.common.collect.ImmutableSet;
import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * This Check helps to catch condition inversion cases which could be rewritten in a more<br>
 * readable manner<br>
 * There're cases where it's justified to get rid of such inversion without changing<br>
 * the main logic. E.g.:
 * <p>
 * <code>
 * if (!(( a &gt;= 8) &amp;&amp; ( b &gt;= 5))) { ... }
 * </code>
 * </p>
 * <p>
 * It can be rewritten as:
 * </p>
 * <p>
 * <code>
 * if ((a &lt; 8) &amp;&amp; (b &lt; 5)) { ... }
 * </code>
 * </p>
 * <p>
 * <code>
 * if (!(a != b)) { ... }
 * </code>
 * </p>
 * <p>
 * as
 * </p>
 * <code>
 * if (a == b) { ... } 
 * </code>
 * Sure, there're cases where we can't get rid of inversion without changing the main logic, e.g.:
 * <p>
 * <code>
 * return !(list.isEmpty());
 * </code>
 * </p>
 * <p>
 * <code>
 * return !(obj instanceof SomeClass);
 * </code>
 * </p>
 * That's why Check has following property:<br>
 * <b>applyOnlyToRelationalOperands</b> - if true Check will only put violation on<br>
 * condition inversions with
 * <a href="https://docs.oracle.com/javase/tutorial/java/nutsandbolts/opsummary.html">
 * relational</a> operands.<br>
 * This option makes Check less strict, e.g.:<br>
 * Using with value <b>true</b> does not put violation on code below:<br>
 * <p>
 * <code>
 * if (! (obj instanceof SomeClass || obj1.isValid())) { ... }
 * </code>
 * </p> 
 *
 * @author <a href="mailto:nesterenko-aleksey@list.ru">Aleksey Nesterenko</a>
 */
public class AvoidConditionInversionCheck extends Check {
	
	public static final String MSG_KEY = "avoid.condition.inversion";
	
	/**
	 * Contains
	 * <a href="https://docs.oracle.com/javase/tutorial/java/nutsandbolts/opsummary.html">
	 * relational</a> operators.
	 */
	private static final Set<Integer> relationalOperatorsSet = ImmutableSet.of(
			TokenTypes.LT,
			TokenTypes.LE,
			TokenTypes.GT,
			TokenTypes.GE,
			TokenTypes.EQUAL,
			TokenTypes.NOT_EQUAL);
	
	/**
	 * Contains
	 * <a href="https://docs.oracle.com/javase/tutorial/java/nutsandbolts/opsummary.html">
	 * relational</a> and conditional operators.
	 */
	private static final Set<Integer> relationalAndConditionalOperatorsSet = new ImmutableSet
			.Builder<Integer>()
				.addAll(relationalOperatorsSet)
				.add(TokenTypes.LOR)
				.add(TokenTypes.LAND)
				.build();
	
	/**
	 * If <b>true</b> - Check only puts violation on conditions with
	 * <a href="https://docs.oracle.com/javase/tutorial/java/nutsandbolts/opsummary.html">
	 * relational</a> operands
	 */
	private boolean applyOnlyToRelationalOperands = false;
	
	public void setApplyOnlyToRelationalOperands(boolean applyOnlyToRelationalOperands) {
		
		this.applyOnlyToRelationalOperands = applyOnlyToRelationalOperands;
	}
	
	@Override
	public int[] getDefaultTokens() {
		
		return new int[] { TokenTypes.LITERAL_RETURN, TokenTypes.LITERAL_IF,
			TokenTypes.LITERAL_WHILE, TokenTypes.LITERAL_DO, TokenTypes.FOR_CONDITION };
	}
	
	@Override
	public void visitToken(DetailAST ast) {
		
		DetailAST expressionAst = ast.findFirstToken(TokenTypes.EXPR);
		
		switch (ast.getType()) {
		
			case TokenTypes.LITERAL_RETURN:
				
				if (!isEmptyReturn(ast)) {

					DetailAST inversionAst = getInversion(expressionAst);
						
					if (isAvoidableInversion(inversionAst)) {
						log(inversionAst);
					}
				}
				break;
			case TokenTypes.LITERAL_WHILE:
			case TokenTypes.LITERAL_DO:
			case TokenTypes.LITERAL_IF:
				
				DetailAST invertedAst = getInversion(expressionAst);
					
				if (isAvoidableInversion(invertedAst)) {
					
					log(invertedAst);
				}
				break;
			case TokenTypes.FOR_CONDITION:
				
				if (!isEmptyForCondition(ast)) {
					
					DetailAST inversionAst = getInversion(expressionAst);
					
					if (isAvoidableInversion(inversionAst)) {
						
						log(inversionAst);
					}
				}
				break;
			default:
				Utils.reportInvalidToken(ast.getType());
				break;
		}
	}

	/**
	 * Checks if return statement is not empty
	 * @param returnAst
	 * 			Node of type {@link com.puppycrawl.tools.checkstyle.api.TokenTypes#LITERAL_RETURN}
	 */
	private static boolean isEmptyReturn(DetailAST returnAst) {
		
		return returnAst.findFirstToken(TokenTypes.EXPR) == null;
	}
	
	/**
	 * Checks if condition in for-loop is not empty
	 * @param forConditionAst
	 * 			Node of type {@link com.puppycrawl.tools.checkstyle.api.TokenTypes#FOR_CONDITION}
	 */
	private static boolean isEmptyForCondition(DetailAST forConditionAst) {
		
		return forConditionAst.getFirstChild() == null;
	}
	
	/**
	 * Gets inversion node of condition if one exists
	 * @param expressionAst
	 * 			Node of type {@link com.puppycrawl.tools.checkstyle.api.TokenTypes#EXPR}
	 * @return Node of type {@link com.puppycrawl.tools.checkstyle.api.TokenTypes#LNOT}
	 * if exists, else - null
	 */
	private static DetailAST getInversion(DetailAST expressionAst) {
		
		return expressionAst.findFirstToken(TokenTypes.LNOT);
	}
	
	/**
	 * Checks if current inversion is avoidable according to Check's properties
	 * @param inversionAst
	 * 			Node of type {@link com.puppycrawl.tools.checkstyle.api.TokenTypes#LNOT}
	 */
	private boolean isAvoidableInversion(DetailAST inversionAst) { 
		
		return inversionAst != null && !isSkipCondition(inversionAst);
	}
	
	/**
	 * Checks if current inverted condition has to be skipped by Check,
	 * it depends from user-defined property <b>"applyOnlyToRelationalOperands"</b>
	 * if it's <b>true</b> - Check will ignore inverted conditions with
	 * non-relational operands
	 * @param inversionConditionAst
	 * 			Node of type {@link com.puppycrawl.tools.checkstyle.api.TokenTypes#LNOT}
	 */
	private boolean isSkipCondition(DetailAST inversionConditionAst) {
		
		return (applyOnlyToRelationalOperands 
					&& !containsRelationalOperandsOnly(inversionConditionAst))
				|| !containsConditionalOrRelationalOperands(inversionConditionAst);
	}
	
	/**
	 * Checks if current inverted condition contains only
	 * <a href="https://docs.oracle.com/javase/tutorial/java/nutsandbolts/opsummary.html">
	 * relational</a> operands
	 * @param inversionConditionAst
	 * 			Node of type {@link com.puppycrawl.tools.checkstyle.api.TokenTypes#LNOT}
	 */
	private static boolean containsRelationalOperandsOnly(DetailAST inversionConditionAst) {
		
		boolean result = true;
		
		DetailAST operatorInInversionAst = inversionConditionAst.getFirstChild().getNextSibling();
		
		if (operatorInInversionAst != null) {
			
			if (!relationalOperatorsSet.contains(operatorInInversionAst.getType())) {
				
				DetailAST currentNode = operatorInInversionAst.getFirstChild();
		
				while (currentNode != null) {
						
					if ((currentNode.getType() == TokenTypes.IDENT)
							|| (!isRelationalOperand(currentNode))) {
					
						result = false;
					}
				
					currentNode = currentNode.getNextSibling();
				}
			}
		}
		
		return result;
	}
	
	/**
	 * Checks if current operand is
	 * <a href="https://docs.oracle.com/javase/tutorial/java/nutsandbolts/opsummary.html">
	 * relational</a> operand
	 * @param operandAst
	 * 			Child node of {@link com.puppycrawl.tools.checkstyle.api.TokenTypes#LNOT} node
	 */
	private static boolean isRelationalOperand(DetailAST operandAst) {
		
		return operandAst.getFirstChild() == null
				|| relationalOperatorsSet.contains(operandAst.getType());
	}

	/**
	 * Checks if current condition contains
	 * <a href="https://docs.oracle.com/javase/tutorial/java/nutsandbolts/opsummary.html">
	 * conditional</a> operators
	 * @param inversionAst
	 * 			Node of type {@link com.puppycrawl.tools.checkstyle.api.TokenTypes#LNOT}
	 */
	private static boolean containsConditionalOrRelationalOperands(DetailAST inversionAst) {
		
		boolean result = false;
		
		DetailAST currentNodeAst = inversionAst.getFirstChild();
		
		while (currentNodeAst != null) {
			
			if (relationalAndConditionalOperatorsSet.contains(currentNodeAst.getType())) {
				
				result = true;
			}
			
			currentNodeAst = currentNodeAst.getNextSibling();
		}
		
		return result;
	}
	
	/**
	 * Logs message on line where inverted condition is used
	 * @param inversionAst
	 * 			Node of type {@link com.puppycrawl.tools.checkstyle.api.TokenTypes#LNOT}
	 */
	private void log(DetailAST inversionAst) {
		
		log(inversionAst.getLineNo(), MSG_KEY);
	}
	
}
