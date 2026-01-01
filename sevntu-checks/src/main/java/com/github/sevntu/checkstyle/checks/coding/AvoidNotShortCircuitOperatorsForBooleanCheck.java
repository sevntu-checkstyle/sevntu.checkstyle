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

package com.github.sevntu.checkstyle.checks.coding;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * This check limits using of not short-circuit operators
 * ("|", "&amp;", "|=", "&amp;=") in boolean expressions.
 * <br>
 * Reason: <br>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; Short-circuit operators ("||", "&amp;&amp;") are more
 * safer and can accelerate the evaluation of complex boolean expressions.
 * Check identifies an expression as a boolean if it contains at least one
 * boolean operand or if result of expression evaluation sets the value of a
 * boolean variable.
 * <br><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Using boolean variables that do not belong
 * to the current class and all calls to boolean methods are not handled by
 * this check. <br><br> Examples: <br>
 * <br>
 * 1. Using of not short-circuit operators while determining a Boolean variable
 * <pre>
 * boolean x = true;
 * boolean result = true | x || false; // a warning here
 * </pre>
 * 2. Using of not short-circuit operators while overriding a Boolean variable.
 * <pre>
 * boolean x = true;
 * boolean result = false;
 * // any code
 * result &amp;= true | x || false; // a warning here
 * </pre>
 * 3. Expression calculated with not short-circuit operators contains at least
 * one boolean operand.
 * <pre>
 * public boolean isTrue() {
 *     return this.z | MyObject.is() // no warnings here
 *             || isModifier() &amp;&amp; isNotTrue();
 * }
 * ...
 * boolean r=true;
 * public boolean isTrue() {
 *     return this.z | true &amp;&amp; r // a warning here
 *             || isModifier() &amp;&amp; isNotTrue();
 * }
 * </pre>
 *
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil
 *         Yaroslavtsev</a>
 * @since 1.8.0
 */
public class AvoidNotShortCircuitOperatorsForBooleanCheck extends AbstractCheck {

    /**
     * A key to search the warning message text in "messages.properties" file.
     * */
    public static final String MSG_KEY = "avoid.not.short.circuit.operators.for.boolean";

    /**
     * Pattern to match boolean types, including array types.
     */
    private static final Pattern BOOLEAN_TYPE_PATTERN = Pattern.compile("^boolean(\\[[^]]*])*");

    /**
     * A list contains all names of operands, which are used in the current
     * expression, which calculates with using "|", "&", "|=", "&=" operators.
     * */
    private final List<String> supportedOperands = new LinkedList<>();

    /**
     * Variable, that indicates keywords "true" or "false" in current
     * expression.
     * */
    private boolean hasTrueOrFalseLiteralInExpression;

    @Override
    public final int[] getDefaultTokens() {
        return new int[] {TokenTypes.BOR, TokenTypes.BAND,
            TokenTypes.BOR_ASSIGN, TokenTypes.BAND_ASSIGN, };
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
    public final void visitToken(final DetailAST detailAST) {
        DetailAST currentNode = detailAST;
        // look for EXPR which is always around BOR/BAND... operators
        while (currentNode != null && currentNode.getType() != TokenTypes.EXPR) {
            currentNode = currentNode.getParent();

            if (currentNode.getType() == TokenTypes.PARAMETER_DEF) {
                currentNode = null;
            }
        }

        if (currentNode != null && isBooleanExpression(currentNode)) {
            log(detailAST, MSG_KEY, detailAST.getText());
        }

        supportedOperands.clear();
        hasTrueOrFalseLiteralInExpression = false;
    }

    /**
     * Checks whether the current method/variable definition type
     * is "Boolean".
     *
     * @param node - current method or variable definition node.
     * @return "true" if current method or variable has a Boolean type.
     */
    private static boolean isBooleanType(final DetailAST node) {
        final FullIdent methodOrVariableType =
                FullIdent.createFullIdent(node.findFirstToken(TokenTypes.TYPE).getFirstChild());
        return BOOLEAN_TYPE_PATTERN
                .matcher(methodOrVariableType.getText())
                .find();
    }

    /**
     * Checks that current expression is calculated using "|", "&amp;", "|=", "&amp;="
     * operators contains at least one Boolean operand.
     *
     * @param node - current TokenTypes.EXPR node to check.
     * @return "true" if current expression is calculated using "|", "&amp;",
     *     "|=". "&amp;=" operators contains at least one Boolean operand or false
     *     otherwise.
     */
    public final boolean isBooleanExpression(final DetailAST node) {
        DetailAST curNode = node;

        final List<String> childNames = getSupportedOperandsNames(curNode);
        final List<String> booleanVariablesNames = new LinkedList<>();

        while (curNode.getType() != TokenTypes.CTOR_DEF
                && curNode.getType() != TokenTypes.METHOD_DEF
                && curNode.getType() != TokenTypes.CLASS_DEF
                && curNode.getType() != TokenTypes.INTERFACE_DEF
                && curNode.getType() != TokenTypes.ANNOTATION_DEF
                && curNode.getType() != TokenTypes.ENUM_DEF) {
            curNode = curNode.getParent();
        }

        final int line = node.getLineNo();
        for (DetailAST currentNode : getChildren(curNode.getLastChild())) {
            if (currentNode.getLineNo() < line
                    && currentNode.getType() == TokenTypes.VARIABLE_DEF
                    && isBooleanType(currentNode)) {
                booleanVariablesNames.add(currentNode.findFirstToken(
                        TokenTypes.IDENT).getText());
            }
        }

        boolean result = false;
        for (String name : childNames) {
            if (booleanVariablesNames.contains(name)) {
                result = true;
                break;
            }
        }
        result = result || hasTrueOrFalseLiteral(node);
        return result;
    }

    /**
     * Searches for all supported operands names in current expression.
     * When checking, treatments to external class variables, method calls,
     * etc are not considered as expression operands.
     *
     * @param exprParentAST - the current TokenTypes.EXPR parent node.
     * @return List of supported operands contained in current expression.
     */
    public final List<String> getSupportedOperandsNames(
            final DetailAST exprParentAST) {
        for (DetailAST currentNode : getChildren(exprParentAST)) {
            if (currentNode.getFirstChild() != null
                    && currentNode.getType() != TokenTypes.METHOD_CALL) {
                getSupportedOperandsNames(currentNode);
            }

            if (currentNode.getType() == TokenTypes.IDENT
                    && currentNode.getParent().getType() != TokenTypes.DOT) {
                supportedOperands.add(currentNode.getText());
            }
        }
        return supportedOperands;
    }

    /**
     * Checks is the current expression has
     * keywords "true" or "false".
     *
     * @param parentAST - the current TokenTypes.EXPR parent node.
     * @return true if the current processed expression contains
     *     "true" or "false" keywords and false otherwise.
     */
    public final boolean hasTrueOrFalseLiteral(final DetailAST parentAST) {
        for (DetailAST currentNode : getChildren(parentAST)) {
            if (currentNode.getFirstChild() != null) {
                hasTrueOrFalseLiteral(currentNode);
            }

            final int type = currentNode.getType();
            if (type == TokenTypes.LITERAL_TRUE
                    || type == TokenTypes.LITERAL_FALSE) {
                hasTrueOrFalseLiteralInExpression = true;
            }

            if (hasTrueOrFalseLiteralInExpression) {
                break;
            }
        }
        return hasTrueOrFalseLiteralInExpression;
    }

    /**
     * Gets all the children one level below on the current top node.
     *
     * @param node - current parent node.
     * @return an array of children one level below on the current parent node
     *         aNode.
     */
    private static List<DetailAST> getChildren(final DetailAST node) {
        final List<DetailAST> result = new LinkedList<>();

        DetailAST currNode = node.getFirstChild();

        while (currNode != null) {
            result.add(currNode);
            currNode = currNode.getNextSibling();
        }

        return result;
    }

}
