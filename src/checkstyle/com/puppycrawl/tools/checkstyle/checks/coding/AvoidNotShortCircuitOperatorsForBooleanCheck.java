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
package com.puppycrawl.tools.checkstyle.checks.coding;

import java.util.LinkedList;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.checks.CheckUtils;

/**
 * <p>
 * This check prevents using of short-circuit operators ("|", "&", "|=", "&=")
 * for boolean expressions.
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil
 *         Yaroslavtsev</a>
 */
public class AvoidNotShortCircuitOperatorsForBooleanCheck extends Check
{

    /**
     * Variable which contains a String "boolean".
     * */
    private static final String BOOLEAN = "boolean";

    /**
     * A key to search the warning message text in "messages.properties" file.
     * */
    private String mKey = "avoid.not.short.circuit.operators.for.boolean";

    /**
     * A list that contains all names of operands, which are used in the current
     * expression, if it calculates with using "|", "&", "|=", "&=" operators.
     * */
    private final LinkedList<String> mSupportedOperands =
        new LinkedList<String>();

    /**
     * Variable, that indicates in current
     * expression keywords "true" or "false".
     * */
    private boolean mHasTrueOrFalseLiteral;

    @Override
    public final int[] getDefaultTokens()
    {
        return new int[] {TokenTypes.BOR, TokenTypes.BAND,
            TokenTypes.BOR_ASSIGN, TokenTypes.BAND_ASSIGN, };
    }

    @Override
    public final void visitToken(final DetailAST aDetailAST)
    {

        DetailAST currentNode = aDetailAST;
        while (currentNode != null
                && currentNode.getType() != TokenTypes.EXPR
                && currentNode.getType() != TokenTypes.METHOD_DEF
                && currentNode.getType() != TokenTypes.CTOR_DEF
                && currentNode.getType() != TokenTypes.CLASS_DEF)
        {
            currentNode = currentNode.getParent();
        }

        final int type = currentNode.getType();

        if (type == TokenTypes.EXPR) {

            if (isBooleanExpression(currentNode)) {
                log(aDetailAST, mKey, aDetailAST.getText());
            }

            mSupportedOperands.clear();
            mHasTrueOrFalseLiteral = false;
        }

    }

    /**
     * Checks the type of current method or variable definition.
     * @param aNode - current method or variable definition node.
     * @return "true" if current method or variable has a Boolean type.
     */
    public final boolean isBooleanType(final DetailAST aNode)
    {
        return BOOLEAN.equals(CheckUtils.createFullType(
                aNode.findFirstToken(TokenTypes.TYPE)).getText());
    }

    /**
     * Checks current expression is calculated using "|", "&", "|=", "&="
     * operators.
     * @param aNode - current EXPR node to check.
     * @return "true" if current expression is calculated using "|", "&",
     * "|=". "&=" operators contains at least one Boolean variable or false
     * otherwise.
     */
    public final boolean isBooleanExpression(final DetailAST aNode)
    {

        DetailAST curNode = aNode;

        final LinkedList<String> childNames =
            getSupportedOperandsNames(curNode);
        final LinkedList<String> booleanVariablesNames =
            new LinkedList<String>();

        while (curNode != null
                && curNode.getType() != TokenTypes.CTOR_DEF
                && curNode.getType() != TokenTypes.METHOD_DEF
                && curNode.getType() != TokenTypes.CLASS_DEF)
        {
            curNode = curNode.getParent();
        }

        final int line = aNode.getLineNo();
        for (DetailAST currentNode : getChildren(curNode.getLastChild())) {
            if (currentNode.getLineNo() < line
                    && currentNode.getType() == TokenTypes.VARIABLE_DEF)
            {

                if (isBooleanType(currentNode)) {
                    booleanVariablesNames.add(currentNode.findFirstToken(
                            TokenTypes.IDENT).getText());
                }
            }
        }

        boolean result = false;

        for (String name : childNames) {
            if (booleanVariablesNames.contains(name)) {
                result = true;
                break;
            }
        }
        result = result || hasTrueOrFalseLiteral(aNode);
        return result;
    }

    /** Searches for all supported operands names in current expression.
     * When checking, treatments to external class variables, method calls,
     * etc are not considered as operands.
     * @param aEXPRParentAST - the current EXPR parent node.
     * @return List of supported operands contained in current expression.
     */
    public final LinkedList<String> getSupportedOperandsNames(
            final DetailAST aEXPRParentAST)
    {

        for (DetailAST currentNode : getChildren(aEXPRParentAST)) {

            if (currentNode.getNumberOfChildren() > 0
                    && currentNode.getType() != TokenTypes.METHOD_CALL)
            {
                getSupportedOperandsNames(currentNode);
            }

            if (currentNode.getType() == TokenTypes.IDENT
                    && currentNode.getParent() != null
                    && currentNode.getParent().getType() != TokenTypes.DOT)
            {
                mSupportedOperands.add(currentNode.getText());
            }

            if (currentNode.getNextSibling() != null) {
                currentNode = currentNode.getNextSibling();
            }
        }
        return mSupportedOperands;
    }


    /**
     * Checks is the current expression has
     * keywords "true" or "false".
     * @param aParentAST - the current EXPR parent node.
     * @return true if the current processed expression contains
     * "true" or "false" keywords and false otherwise.
     */
    public final boolean hasTrueOrFalseLiteral(final DetailAST aParentAST)
    {

        for (DetailAST currentNode : getChildren(aParentAST)) {

            if (currentNode.getNumberOfChildren() > 0) {
                hasTrueOrFalseLiteral(currentNode);
            }

            final int type = currentNode.getType();
            if (type == TokenTypes.LITERAL_TRUE
                    || type == TokenTypes.LITERAL_FALSE)
            {
                mHasTrueOrFalseLiteral = true;
            }

            if (currentNode.getNextSibling() != null) {
                currentNode = currentNode.getNextSibling();
            }

            if (mHasTrueOrFalseLiteral) {
                break;
            }
        }
        return mHasTrueOrFalseLiteral;
    }

    /**
     * Gets all the children one level below on the current top node.
     * @param aNode - current parent node.
     * @return an array of children one level below on the current parent node
     *         aNode.
     */
    public final LinkedList<DetailAST> getChildren(final DetailAST aNode)
    {
        final LinkedList<DetailAST> result = new LinkedList<DetailAST>();

        DetailAST currNode = aNode.getFirstChild();

        while (currNode != null) {
            result.add(currNode);
            currNode = currNode.getNextSibling();
        }

        return result;
    }

}