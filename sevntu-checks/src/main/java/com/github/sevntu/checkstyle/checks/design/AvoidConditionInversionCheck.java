///////////////////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code and other text files for adherence to a set of rules.
// Copyright (C) 2001-2026 the original author or authors.
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
///////////////////////////////////////////////////////////////////////////////////////////////

package com.github.sevntu.checkstyle.checks.design;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.sevntu.checkstyle.SevntuUtil;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
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
 * @since 1.13.0
 */
public class AvoidConditionInversionCheck extends AbstractCheck {

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY = "avoid.condition.inversion";

    /**
     * Contains
     * <a href="https://docs.oracle.com/javase/tutorial/java/nutsandbolts/opsummary.html">
     * relational</a> operators.
     */
    private static final Set<Integer> RELATIONAL_OPERATORS_SET = Set.of(
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
    private static final Set<Integer> RELATIONAL_AND_CONDITIONAL_OPERATORS_SET = Stream.concat(
            RELATIONAL_OPERATORS_SET.stream(),
            Stream.of(TokenTypes.LOR, TokenTypes.LAND))
        .collect(Collectors.toUnmodifiableSet());

    /**
     * If <b>true</b> - Check only puts violation on conditions with
     * <a href="https://docs.oracle.com/javase/tutorial/java/nutsandbolts/opsummary.html">
     * relational</a> operands.
     */
    private boolean applyOnlyToRelationalOperands;

    /**
     * Setter for applyOnlyToRelationalOperands.
     *
     * @param applyOnlyToRelationalOperands The new value for the field.
     */
    public void setApplyOnlyToRelationalOperands(boolean applyOnlyToRelationalOperands) {
        this.applyOnlyToRelationalOperands = applyOnlyToRelationalOperands;
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.LITERAL_RETURN,
            TokenTypes.LITERAL_IF,
            TokenTypes.LITERAL_WHILE,
            TokenTypes.LITERAL_DO,
            TokenTypes.FOR_CONDITION,
        };
    }

    @Override
    public int[] getAcceptableTokens() {
        return getDefaultTokens();
    }

    @Override
    public int[] getRequiredTokens() {
        return getDefaultTokens();
    }

    @Override
    public void visitToken(DetailAST ast) {
        final DetailAST expressionAst = ast.findFirstToken(TokenTypes.EXPR);

        switch (ast.getType()) {
            case TokenTypes.LITERAL_RETURN:
                if (!isEmptyReturn(ast)) {
                    final DetailAST inversionAst = getInversion(expressionAst);

                    if (isAvoidableInversion(inversionAst)) {
                        log(inversionAst);
                    }
                }
                break;

            case TokenTypes.LITERAL_WHILE:
            case TokenTypes.LITERAL_DO:
            case TokenTypes.LITERAL_IF:
                final DetailAST invertedAst = getInversion(expressionAst);
                if (isAvoidableInversion(invertedAst)) {
                    log(invertedAst);
                }
                break;

            case TokenTypes.FOR_CONDITION:
                if (!isEmptyForCondition(ast)) {
                    final DetailAST inversionAst = getInversion(expressionAst);

                    if (isAvoidableInversion(inversionAst)) {
                        log(inversionAst);
                    }
                }
                break;

            default:
                SevntuUtil.reportInvalidToken(ast.getType());
                break;
        }
    }

    /**
     * Checks if return statement is not empty.
     *
     * @param returnAst
     *             Node of type
     *             {@link com.puppycrawl.tools.checkstyle.api.TokenTypes#LITERAL_RETURN}
     * @return true if the return is empty.
     */
    private static boolean isEmptyReturn(DetailAST returnAst) {
        return returnAst.findFirstToken(TokenTypes.EXPR) == null;
    }

    /**
     * Checks if condition in for-loop is not empty.
     *
     * @param forConditionAst
     *             Node of type {@link com.puppycrawl.tools.checkstyle.api.TokenTypes#FOR_CONDITION}
     * @return true if the for condition is empty.
     */
    private static boolean isEmptyForCondition(DetailAST forConditionAst) {
        return forConditionAst.getFirstChild() == null;
    }

    /**
     * Gets inversion node of condition if one exists.
     *
     * @param expressionAst
     *             Node of type {@link com.puppycrawl.tools.checkstyle.api.TokenTypes#EXPR}
     * @return Node of type {@link com.puppycrawl.tools.checkstyle.api.TokenTypes#LNOT}
     *     if exists, else - null
     */
    private static DetailAST getInversion(DetailAST expressionAst) {
        return expressionAst.findFirstToken(TokenTypes.LNOT);
    }

    /**
     * Checks if current inversion is avoidable according to Check's properties.
     *
     * @param inversionAst
     *             Node of type {@link com.puppycrawl.tools.checkstyle.api.TokenTypes#LNOT}
     * @return true if the inversion is avoidable.
     */
    private boolean isAvoidableInversion(DetailAST inversionAst) {
        return inversionAst != null && !isSkipCondition(inversionAst);
    }

    /**
     * Checks if current inverted condition has to be skipped by Check,
     * it depends from user-defined property <b>"applyOnlyToRelationalOperands"</b>.
     * if it's <b>true</b> - Check will ignore inverted conditions with
     * non-relational operands
     *
     * @param inversionConditionAst
     *             Node of type {@link com.puppycrawl.tools.checkstyle.api.TokenTypes#LNOT}
     * @return true if token can be skipped.
     */
    private boolean isSkipCondition(DetailAST inversionConditionAst) {
        return applyOnlyToRelationalOperands
                    && !containsRelationalOperandsOnly(inversionConditionAst)
                || !containsConditionalOrRelationalOperands(inversionConditionAst);
    }

    /**
     * Checks if current inverted condition contains only
     * <a href="https://docs.oracle.com/javase/tutorial/java/nutsandbolts/opsummary.html">
     * relational</a> operands.
     *
     * @param inversionConditionAst
     *             Node of type {@link com.puppycrawl.tools.checkstyle.api.TokenTypes#LNOT}
     * @return true if the node contains only relation operands.
     */
    private static boolean containsRelationalOperandsOnly(DetailAST inversionConditionAst) {
        boolean result = true;

        final DetailAST operatorInInversionAst = inversionConditionAst.getFirstChild()
                .getNextSibling();

        if (operatorInInversionAst != null
                && !RELATIONAL_OPERATORS_SET.contains(operatorInInversionAst.getType())) {
            DetailAST currentNode = operatorInInversionAst.getFirstChild();

            while (currentNode != null) {
                if (currentNode.getType() == TokenTypes.IDENT
                        || !isRelationalOperand(currentNode)) {
                    result = false;
                }

                currentNode = currentNode.getNextSibling();
            }
        }

        return result;
    }

    /**
     * Checks if current operand is
     * <a href="https://docs.oracle.com/javase/tutorial/java/nutsandbolts/opsummary.html">
     * relational</a> operand.
     *
     * @param operandAst
     *             Child node of {@link com.puppycrawl.tools.checkstyle.api.TokenTypes#LNOT} node
     * @return true if the operand is relational.
     */
    private static boolean isRelationalOperand(DetailAST operandAst) {
        return operandAst.getFirstChild() == null
                || RELATIONAL_OPERATORS_SET.contains(operandAst.getType());
    }

    /**
     * Checks if current condition contains
     * <a href="https://docs.oracle.com/javase/tutorial/java/nutsandbolts/opsummary.html">
     * conditional</a> operators.
     *
     * @param inversionAst
     *             Node of type {@link com.puppycrawl.tools.checkstyle.api.TokenTypes#LNOT}
     * @return true if the node contains conditional or relational operands.
     */
    private static boolean containsConditionalOrRelationalOperands(DetailAST inversionAst) {
        boolean result = false;

        DetailAST currentNodeAst = inversionAst.getFirstChild();

        while (currentNodeAst != null) {
            if (RELATIONAL_AND_CONDITIONAL_OPERATORS_SET.contains(currentNodeAst.getType())) {
                result = true;
            }

            currentNodeAst = currentNodeAst.getNextSibling();
        }

        return result;
    }

    /**
     * Logs message on line where inverted condition is used.
     *
     * @param inversionAst
     *             Node of type {@link com.puppycrawl.tools.checkstyle.api.TokenTypes#LNOT}
     */
    private void log(DetailAST inversionAst) {
        log(inversionAst, MSG_KEY);
    }

}
