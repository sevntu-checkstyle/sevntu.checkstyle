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
 * This check prevents using of short-circuit operators ("|", "&", "|=", "&=") for variables
 *  is calculated using  operators.
     * operators.
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil
 *         Yaroslavtsev</a>
 */
public class AvoidNotShortCircuitOperatorsForBooleanCheck extends Check
{

    private static final String BOOLEAN = "boolean";
    private static final String INT = "int";
    private String aKey = "avoid.not.short.circuit.operators.for.boolean";

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] {
            TokenTypes.BOR,
            TokenTypes.BAND,
            TokenTypes.BOR_ASSIGN,
            TokenTypes.BAND_ASSIGN, };
    }

    @Override
    public void visitToken(final DetailAST aDetailAST)
    {

        DetailAST currentNode = aDetailAST;
        while (currentNode != null
                && currentNode.getType() != TokenTypes.LITERAL_IF
                && currentNode.getType() != TokenTypes.FOR_CONDITION
                && currentNode.getType() != TokenTypes.LITERAL_WHILE
                && currentNode.getType() != TokenTypes.LITERAL_RETURN
                && currentNode.getType() != TokenTypes.VARIABLE_DEF
                && currentNode.getType() != TokenTypes.METHOD_DEF
                && currentNode.getType() != TokenTypes.CLASS_DEF)
        {
            currentNode = currentNode.getParent();
        }

        final int type = currentNode.getType();

        if (type != TokenTypes.METHOD_DEF
                && type != TokenTypes.CLASS_DEF)
        {
            
            if (type == TokenTypes.VARIABLE_DEF && isBooleanType(currentNode)) {
                log(aDetailAST,
                        aKey,
                        aDetailAST.getText());
            }

            else if (type == TokenTypes.LITERAL_RETURN) { // "return" situation

                while (currentNode != null
                        && currentNode.getType() != TokenTypes.METHOD_DEF
                        && currentNode.getType() != TokenTypes.CLASS_DEF)
                {
                    currentNode = currentNode.getParent();
                }

                if (currentNode.getType() == TokenTypes.METHOD_DEF
                        && isBooleanType(currentNode))
                {
                    log(aDetailAST,
                            aKey,
                            aDetailAST.getText());
                }
            }

            else if (type == TokenTypes.LITERAL_IF
                 || type == TokenTypes.LITERAL_WHILE
                 || type == TokenTypes.FOR_CONDITION)
            {

                if (calculatedUsingBooleanType(aDetailAST)) {
                    log(aDetailAST,
                            aKey,
                            aDetailAST.getText());
                }
            }
        }
    }

    /**
     * Checks the type of current method or variable definition.
     * @param aNode - current method or variable definition node.
     * @return "true" if current method or variable has a Boolean type.
     */
    public boolean isBooleanType(DetailAST aNode)
    {
        return BOOLEAN.equals(CheckUtils.createFullType(
                aNode.findFirstToken(TokenTypes.TYPE)).getText());
    }

    /**
     * Checks the type of current method or variable definition.
     * @param aNode - current method or variable definition node.
     * @return "true" if current method or variable has an Integer type.
     */
    public boolean isIntegerType(DetailAST aNode)
    {
        return INT.equals(CheckUtils.createFullType(
                aNode.findFirstToken(TokenTypes.TYPE)).getText());
    }

    /**
     * Checks the type of variables is calculated using "|", "&", "|=", "&="
     * operators.
     * @param aNode - current processed node (Supported token types: BOR, BAND,
     *            BOR_ASSIGN, BAND_ASSIGN).
     * @return "true" if current variables value is calculated using "|", "&",
     *         "|=". "&=" operators but has a Boolean type and "false"
     *         otherwise.
     */
    public boolean calculatedUsingBooleanType(DetailAST aNode)
    {

        final LinkedList<String> childNames = new LinkedList<String>();
        final LinkedList<String> booleanVariablesNames =
            new LinkedList<String>();

        for (DetailAST child : getChildren(aNode)) {
            childNames.add(child.getText());
        }

        while (aNode != null
                && aNode.getType() != TokenTypes.CTOR_DEF
                && aNode.getType() != TokenTypes.METHOD_DEF
                && aNode.getType() != TokenTypes.CLASS_DEF)
        {
            aNode = aNode.getParent();
        }

        for (DetailAST currentNode : getChildren(aNode.getLastChild())) {
            if (currentNode.getType() == TokenTypes.VARIABLE_DEF) {

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

        return result;
    }

    /** Gets all the children one level below on the current top node.
     * @param aNode - current parent node.
     * @return an array of children one level below
     * on the current parent node aNode. */
    public LinkedList<DetailAST> getChildren(DetailAST aNode)
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
