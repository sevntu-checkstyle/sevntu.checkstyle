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
 * Example #1:
 * 
 * <pre>
 *      <code>int count;
 *      a = a + b;
 *      b = a + a;
 *      count = b; // DECLARATION OF VARIABLE 'count' SHOULD BE HERE (distance = 3)</code>
 * </pre>
 * 
 * Example #2:
 * 
 * <pre>
 *     <code>int count;
 *     {
 *         a = a + b;
 *         count = b; // DECLARATION OF VARIABLE 'count' SHOULD BE HERE (distance = 2)
 *     }</code>
 * </pre>
 * <p>
 * There is an additional option to ignore distance calculation for variables
 * listed in RegExp.
 * </p>
 * ATTENTION!! (Not supported cases)
 * 
 * <pre>
 * Case #1:
 * 
 * <code>{
 * int c;
 * int a = 3;
 * int b = 2;
 *     {
 *     a = a + b;
 *     c = b;
 *     }
 * }</code>
 * 
 * Distance for variable 'a' = 1;
 * Distance for variable 'b' = 1;
 * Distance for variable 'c' = 2.
 * </pre>
 * 
 * As distance by default is 1 the Check doesn't raise warning for variables 'a'
 * and 'b' to move them into the block.
 * 
 * <pre>
 * Case #2:
 * 
 * <code>int sum = 0;
 * for (int i = 0; i < 20; i++) {
 *     a++;
 *     b--;
 *     sum++;
 *     if (sum > 10) {
 *         res = true;
 *     }
 * }</code>
 * 
 * Distance for variable 'sum' = 3.
 * </pre>
 * <p>
 * As distance more then default one, the Check raises warning for variable
 * 'sum' to move it into the 'for(...)' block. But there is situation when
 * variable 'sum' hasn't to be 0 within each iteration. So, to avoid such
 * warnings you can use Suppression Filter, provided by Checkstyle, for the
 * whole class.
 * </p>
 * 
 * @author <a href="mailto:rd.ryly@gmail.com">Ruslan Diachenko</a>
 */
public class VariableDeclarationUsageDistanceCheck extends Check
{
    // Allowed distance between declaration of variable and its first usage.
    private int mAllowedDistance = 1;

    // RegExp pattern to ignore distance calculation for variables listed in
    // this pattern.
    private Pattern mIgnoreVariablePattern = Pattern.compile("");

    private boolean validateBetweenScopes;

    /**
     * Sets an allowed distance between declaration of variable and its first
     * usage.
     * 
     * @param aAllowedDistance Allowed distance between declaration of variable
     *            and its first usage.
     */
    public void setAllowedDistance(int aAllowedDistance)
    {
        this.mAllowedDistance = aAllowedDistance;
    }

    /**
     * Sets RegExp pattern to ignore distance calculation for variables listed
     * in this pattern.
     * 
     * @param aIgnorePattern Pattern contains ignored variables.
     */
    public void setIgnoreVariablePattern(String aIgnorePattern)
    {
        mIgnoreVariablePattern = Pattern.compile(aIgnorePattern);
    }

    public void setValidateBetweenScopes(boolean validateBetweenScopes)
    {
        this.validateBetweenScopes = validateBetweenScopes;
    }

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] { TokenTypes.VARIABLE_DEF, };
    }

    @Override
    public void visitToken(DetailAST aAST)
    {
        int parentType = aAST.getParent().getType();
        DetailAST nextSibling = aAST.getNextSibling();
        if (parentType != TokenTypes.OBJBLOCK && nextSibling != null
                && nextSibling.getType() == TokenTypes.SEMI) {
            DetailAST variable = aAST.findFirstToken(TokenTypes.IDENT);
            if (mAllowedDistance > 0) {
                if (!isVariableMatchesPattern(variable.getText())) {
                    int dist = 0;
                    if (validateBetweenScopes) {
                        dist = calculateDistanceBetweenScopes(nextSibling,
                                variable);
                        dist++;
                    }
                    else {
                        dist = calculateDistanceInSingleScope(nextSibling,
                                variable);
                    }
                    if (dist > mAllowedDistance) {
                        log(variable.getLineNo(),
                                "variable.declaration.usage.distance",
                                variable.getText(), dist, mAllowedDistance);
//						System.out.println(variable.getLineNo() + ": var = "
//								+ variable.getText() + "; dist = " + dist);
                    }
                }
            }
        }
    }

    private int calculateDistanceInSingleScope(DetailAST aAST, DetailAST aVariable)
    {
        int dist = 0;
        boolean variableFirstFound = false;
        DetailAST nextSibling = aAST;

        while (nextSibling != null
                && nextSibling.getType() != TokenTypes.RCURLY) {
            if (nextSibling.getType() == TokenTypes.VARIABLE_DEF) {
                if (isASTContainsElement(nextSibling, aVariable)) {
                    dist++;
                    variableFirstFound = true;
                    break;
                }
            }
            else {
                if (nextSibling.getFirstChild() != null) {
                    if (isASTContainsElement(nextSibling, aVariable)) {
                        DetailAST astSlistIdent = nextSibling;
                        if (astSlistIdent.getType() != TokenTypes.SLIST) {
                            astSlistIdent = nextSibling
                                    .findFirstToken(TokenTypes.SLIST);
                        }
                        if (astSlistIdent == null) { // If another scope then break
                            dist++;
                            variableFirstFound = true;
                        }
                        else {
                            variableFirstFound = false;
                        }
                        break;
                    }
                    else {
                        dist++;
                    }
                }
            }
            nextSibling = nextSibling.getNextSibling();
        }

        if (!variableFirstFound) {
            dist = 0;
        }

        return dist;
    }

    /**
     * Calculates distance between declaration of variable and its first usage.
     * 
     * @param aAST Regular node of AST which is checked for content of checking
     *            variable.
     * @param aVariable Variable which distance is calculated for.
     * @return Distance between declaration of variable and its first usage.
     */
    private int calculateDistanceBetweenScopes(DetailAST aAST, DetailAST aVariable)
    {
        int dist = 0;
        boolean variableFirstFound = false;
        DetailAST nextSibling = aAST;
        List<DetailAST> exprWithVariableList = new ArrayList<DetailAST>();

        while (nextSibling != null
                && nextSibling.getType() != TokenTypes.RCURLY) {
            if (nextSibling.getType() == TokenTypes.VARIABLE_DEF) {
                if (isASTContainsElement(nextSibling, aVariable)) {
                    exprWithVariableList.add(nextSibling);
                    variableFirstFound = true;
                }
            }
            else {
                if (nextSibling.getFirstChild() != null) {
                    if (isASTContainsElement(nextSibling, aVariable)) {
                        exprWithVariableList.add(nextSibling);
                        variableFirstFound = true;
                    }
                    else {
                        if (!variableFirstFound) {
                            dist++;
                        }
                    }
                }
            }
            nextSibling = nextSibling.getNextSibling();
        }

        if (exprWithVariableList.size() == 1) {
            DetailAST blockWithVariable = exprWithVariableList.get(0);
            DetailAST childInBlock;

            switch (blockWithVariable.getType()) {
            case TokenTypes.VARIABLE_DEF:
            case TokenTypes.EXPR:
                break;

            case TokenTypes.LITERAL_FOR:
            case TokenTypes.LITERAL_WHILE:
                if (!isVariableInOperatorDeclaration(blockWithVariable,
                        aVariable)) {
                    childInBlock = blockWithVariable.getFirstChild();
                    while (childInBlock != null
                            && childInBlock.getType() != TokenTypes.RPAREN) {
                        childInBlock = childInBlock.getNextSibling();
                    }
                    if (childInBlock != null) {
                        childInBlock = childInBlock.getNextSibling();
                        int childInBlockType = childInBlock.getType();
                        if (childInBlockType == TokenTypes.SLIST) {
                            dist += calculateDistanceBetweenScopes(
                                    childInBlock.getFirstChild(),
                                    aVariable);
                        }
                        else {
                            if (childInBlockType != TokenTypes.VARIABLE_DEF
                                    && childInBlockType != TokenTypes.EXPR) {
                                dist += calculateDistanceBetweenScopes(
                                        childInBlock,
                                        aVariable);
                            }
                        }
                    }
                }
                break;

            case TokenTypes.LITERAL_DO:
                if (!isVariableInOperatorDeclaration(blockWithVariable,
                        aVariable)) {
                    childInBlock = blockWithVariable.getFirstChild();
                    int childInBlockType = childInBlock.getType();
                    if (childInBlockType == TokenTypes.SLIST) {
                        dist += calculateDistanceBetweenScopes(
                                childInBlock.getFirstChild(),
                                aVariable);
                    }
                    else {
                        if (childInBlockType != TokenTypes.VARIABLE_DEF
                                && childInBlockType != TokenTypes.EXPR) {
                            dist += calculateDistanceBetweenScopes(
                                    childInBlock,
                                    aVariable);
                        }
                    }
                }
                break;

            case TokenTypes.LITERAL_IF:
                if (!isVariableInOperatorDeclaration(blockWithVariable,
                        aVariable)) {
                    childInBlock = blockWithVariable.getLastChild();
                    exprWithVariableList = new ArrayList<DetailAST>();

                    while (childInBlock != null
                            && childInBlock.getType() == TokenTypes.LITERAL_ELSE) {
                        DetailAST previousChild = childInBlock
                                .getPreviousSibling();
                        if (isASTContainsElement(previousChild, aVariable)) {
                            exprWithVariableList.add(previousChild);
                        }
                        childInBlock = childInBlock.getFirstChild();

                        if (childInBlock.getType() == TokenTypes.LITERAL_IF) {
                            childInBlock = childInBlock.getLastChild();
                        }
                        else {
                            if (isASTContainsElement(childInBlock, aVariable)) {
                                exprWithVariableList.add(childInBlock);
                                childInBlock = null;
                            }
                        }
                    }

                    if (childInBlock != null) {
                        if (isASTContainsElement(childInBlock, aVariable)) {
                            exprWithVariableList.add(childInBlock);
                        }
                    }

                    if (exprWithVariableList.size() == 1) {
                        dist += calculateDistanceBetweenScopes(
                                exprWithVariableList.get(0),
                                aVariable);
                    }
                }
                break;

            case TokenTypes.LITERAL_SWITCH:
                if (!isVariableInOperatorDeclaration(blockWithVariable,
                        aVariable)) {
                    childInBlock = blockWithVariable
                            .findFirstToken(TokenTypes.CASE_GROUP);
                    exprWithVariableList = new ArrayList<DetailAST>();

                    while (childInBlock != null
                            && childInBlock.getType() == TokenTypes.CASE_GROUP) {
                        DetailAST nextSiblingChild = childInBlock
                                .getLastChild();
                        if (isASTContainsElement(nextSiblingChild, aVariable)) {
                            exprWithVariableList.add(nextSiblingChild);
                        }
                        childInBlock = childInBlock.getNextSibling();
                    }

                    if (exprWithVariableList.size() == 1) {
                        dist += calculateDistanceBetweenScopes(
                                exprWithVariableList.get(0),
                                aVariable);
                    }
                }
                break;

            case TokenTypes.LITERAL_TRY:
                childInBlock = blockWithVariable.getFirstChild();
                exprWithVariableList = new ArrayList<DetailAST>();

                if (isASTContainsElement(childInBlock, aVariable)) {
                    exprWithVariableList.add(childInBlock);
                }

                childInBlock = childInBlock.getNextSibling();
                while (childInBlock != null
                        && childInBlock.getType() == TokenTypes.LITERAL_CATCH) {
                    DetailAST nextSiblingChild = childInBlock.getLastChild();
                    if (isASTContainsElement(nextSiblingChild, aVariable)) {
                        exprWithVariableList.add(nextSiblingChild);
                    }
                    childInBlock = childInBlock.getNextSibling();
                }

                if (childInBlock != null) {
                    DetailAST nextSiblingChild = childInBlock.getLastChild();
                    if (isASTContainsElement(nextSiblingChild, aVariable)) {
                        exprWithVariableList.add(nextSiblingChild);
                    }
                }

                if (exprWithVariableList.size() == 1) {
                    dist += calculateDistanceBetweenScopes(
                            exprWithVariableList.get(0).getFirstChild(),
                            aVariable);
                }
                break;

            default:
                dist += calculateDistanceBetweenScopes(
                        blockWithVariable.getFirstChild(),
                        aVariable);
            }
        }
        else {
            if (!variableFirstFound) {
                dist = 0;
            }
        }

        return dist;
    }

    private boolean isVariableInOperatorDeclaration(DetailAST aASTSibling, DetailAST aVariable)
    {
        boolean isVarInOperatorDeclr = false;
        DetailAST nextSibling = aASTSibling.getFirstChild();

        while (nextSibling != null
                && nextSibling.getType() != TokenTypes.LPAREN) {
            nextSibling = nextSibling.getNextSibling();
        }

        if (nextSibling != null) {
            nextSibling = nextSibling.getNextSibling(); // Get EXPR between braces
            if (isASTContainsElement(nextSibling, aVariable)) {
                isVarInOperatorDeclr = true;
            }
            else {
                switch (aASTSibling.getType()) {
                case TokenTypes.LITERAL_IF:
                    nextSibling = aASTSibling.getLastChild();
                    if (nextSibling.getType() == TokenTypes.LITERAL_ELSE) {
                        nextSibling = nextSibling.getFirstChild(); // Get IF followed by ELSE
                        if (nextSibling.getType() == TokenTypes.LITERAL_IF) {
                            isVarInOperatorDeclr |= isVariableInOperatorDeclaration(
                                    nextSibling, aVariable);
                        }
                    }
                    break;

                case TokenTypes.LITERAL_SWITCH:
                    nextSibling = aASTSibling
                            .findFirstToken(TokenTypes.CASE_GROUP);
                    while (nextSibling != null
                            && nextSibling.getType() == TokenTypes.CASE_GROUP) {
                        DetailAST nextSiblingChild = nextSibling
                                .getFirstChild();
                        if (isASTContainsElement(nextSiblingChild, aVariable)) {
                            isVarInOperatorDeclr = true;
                            break;
                        }
                        nextSibling = nextSibling.getNextSibling();
                    }
                    break;

                default:
                }
            }
        }

        return isVarInOperatorDeclr;
    }

    /**
     * Checks if AST node contains given element.
     * @param aAST Node of AST.
     * @param aElement AST element which is checked for content in AST node.
     * @return true if AST element was found in AST node, otherwise - false.
     */
    private boolean isASTContainsElement(DetailAST aAST, DetailAST aElement)
    {
        boolean isASTContainsElement = false;
        ASTEnumeration astList = aAST.findAllPartial(aElement);
        while (astList.hasMoreNodes()) {
            DetailAST astElement = (DetailAST) astList.nextNode();
            DetailAST astElementParent = astElement.getParent();
            while (astElementParent != null) {
                if (astElementParent.equals(aAST)
                        && astElementParent.getLineNo() == aAST.getLineNo()) {
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
     * 
     * @param aVariable Variable which is checked for content in ignored
     *            pattern.
     * @return true if variable was found, otherwise - false.
     */
    private boolean isVariableMatchesPattern(String aVariable)
    {
        Matcher matcher = mIgnoreVariablePattern.matcher(aVariable);
        return matcher.matches();
    }
}
