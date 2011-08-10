////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2011  Oliver Burn
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
 * <pre>
 *      <code>int count;
 *      a = a + b;
 *      b = a + a;
 *      count = b; // DECLARATION OF VARIABLE 'count'
 *                 // SHOULD BE HERE (distance = 3)</code>
 * </pre>
 * Example #2:
 * <pre>
 *     <code>int count;
 *     {
 *         a = a + b;
 *         count = b; // DECLARATION OF VARIABLE 'count'
 *                    // SHOULD BE HERE (distance = 2)
 *     }</code>
 * </pre>
 * There are several additional options to configure check:
 * <pre>
 * 1. allowedDistance - allows to set distance between declaration
 * of variable and its first usage.
 * 2. ignoreVariablePattern - allows to set RegEx pattern for ignoring
 * distance calculation for variables listed in this pattern.
 * 3. validateBetweenScopes - allows to calculate distance between declaration
 * of variable and its first usage in different scopes.
 * 4. ignoreFinal - allows to ignore variables with 'final' modifier.
 * </pre>
 * ATTENTION!! (Not supported cases)
 * <pre>
 * Case #1:
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
 * As distance by default is 1 the Check doesn't raise warning for variables 'a'
 * and 'b' to move them into the block.
 * <pre>
 * Case #2:
 * <code>int sum = 0;
 * for (int i = 0; i < 20; i++) {
 *     a++;
 *     b--;
 *     sum++;
 *     if (sum > 10) {
 *         res = true;
 *     }
 * }</code>
 * Distance for variable 'sum' = 3.
 * </pre>
 * <p>
 * As distance more then default one, the Check raises warning for variable
 * 'sum' to move it into the 'for(...)' block. But there is situation when
 * variable 'sum' hasn't to be 0 within each iteration. So, to avoid such
 * warnings you can use Suppression Filter, provided by Checkstyle, for the
 * whole class.
 * </p>
 * @author <a href="mailto:rd.ryly@gmail.com">Ruslan Diachenko</a>
 */
public class VariableDeclarationUsageDistanceCheck extends Check
{
    /**
     * Default value of distance between declaration of variable and its first
     * usage.
     */
    private static final int DEFAULT_DISTANCE = 3;

    /** Allowed distance between declaration of variable and its first usage. */
    private int mAllowedDistance = DEFAULT_DISTANCE;

    /**
     * RegExp pattern to ignore distance calculation for variables listed in
     * this pattern.
     */
    private Pattern mIgnoreVariablePattern = Pattern.compile("");

    /**
     * Allows to calculate distance between declaration of variable and its
     * first usage in different scopes.
     */
    private boolean mValidateBetweenScopes;

    /** Allows to ignore variables with 'final' modifier. */
    private boolean mIgnoreFinal = true;

    /**
     * Sets an allowed distance between declaration of variable and its first
     * usage.
     * @param aAllowedDistance
     *        Allowed distance between declaration of variable and its first
     *        usage.
     */
    public void setAllowedDistance(int aAllowedDistance)
    {
        this.mAllowedDistance = aAllowedDistance;
    }

    /**
     * Sets RegExp pattern to ignore distance calculation for variables listed
     * in this pattern.
     * @param aIgnorePattern
     *        Pattern contains ignored variables.
     */
    public void setIgnoreVariablePattern(String aIgnorePattern)
    {
        this.mIgnoreVariablePattern = Pattern.compile(aIgnorePattern);
    }

    /**
     * Sets option which allows to calculate distance between declaration of
     * variable and its first usage in different scopes.
     * @param aValidateBetweenScopes
     *        Defines if allow to calculate distance between declaration of
     *        variable and its first usage in different scopes or not.
     */
    public void setValidateBetweenScopes(boolean aValidateBetweenScopes)
    {
        this.mValidateBetweenScopes = aValidateBetweenScopes;
    }

    /**
     * Sets ignore option for variables with 'final' modifier.
     * @param aIgnoreFinal
     *        Defines if ignore variables with 'final' modifier or not.
     */
    public void setIgnoreFinal(boolean aIgnoreFinal)
    {
        this.mIgnoreFinal = aIgnoreFinal;
    }

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] {TokenTypes.VARIABLE_DEF};
    }

    @Override
    public void visitToken(DetailAST aAST)
    {
        final int parentType = aAST.getParent().getType();
        final DetailAST modifiers = aAST.getFirstChild();

        if ((mIgnoreFinal && modifiers.branchContains(TokenTypes.FINAL))
                || parentType == TokenTypes.OBJBLOCK)
        {
            ;// No code!
        }
        else {
            final DetailAST currentAST = aAST.getNextSibling();
            final DetailAST variable =
                    aAST.findFirstToken(TokenTypes.IDENT);

            if (!isVariableMatchesIgnorePattern(variable.getText())) {
                int dist = 0;

                if (mValidateBetweenScopes) {
                    dist = calculateDistanceBetweenScopes(currentAST,
                            variable);
                    dist++;
                }
                else {
                    dist = calculateDistanceInSingleScope(currentAST,
                            variable);
                }
                if (dist > mAllowedDistance) {
                    log(variable.getLineNo(),
                            "variable.declaration.usage.distance",
                            variable.getText(), dist, mAllowedDistance);
                }
            }
        }
    }

    /**
     * Calculates distance between declaration of variable and its first usage
     * in single scope.
     * @param aAST
     *        Regular node of AST which is checked for content of checking
     *        variable.
     * @param aVariable
     *        Variable which distance is calculated for.
     * @return Distance between declaration of variable and its first usage.
     */
    private int calculateDistanceInSingleScope(
            DetailAST aAST, DetailAST aVariable)
    {
        int dist = 0;
        boolean firstUsageFound = false;
        DetailAST currentAST = aAST;

        while (currentAST != null
                && currentAST.getType() != TokenTypes.RCURLY)
        {
            if (currentAST.getType() == TokenTypes.VARIABLE_DEF) {
                firstUsageFound = isASTContainsElement(currentAST, aVariable);

                if (firstUsageFound) {
                    dist++;
                    break;
                }
            }
            else if (currentAST.getFirstChild() != null) {

                if (isASTContainsElement(currentAST, aVariable)) {

                    switch (currentAST.getType()) {
                    case TokenTypes.SLIST:
                        firstUsageFound = false;
                        break;
                    case TokenTypes.LITERAL_FOR:
                    case TokenTypes.LITERAL_WHILE:
                    case TokenTypes.LITERAL_DO:
                    case TokenTypes.LITERAL_IF:
                    case TokenTypes.LITERAL_SWITCH:
                        firstUsageFound = isVariableInOperatorDeclaration(
                                currentAST,
                                aVariable);
                        if (firstUsageFound) {
                            dist++;
                        }
                        break;
                    default:
                        if (currentAST.branchContains(TokenTypes.SLIST)) {
                            firstUsageFound = false;
                        }
                        else {
                            dist++;
                            firstUsageFound = true;
                        }
                    }
                    break;
                }
                else {
                    dist++;
                }
            }
            currentAST = currentAST.getNextSibling();
        }

        if (!firstUsageFound) {
            dist = 0;
        }

        return dist;
    }

    /**
     * Calculates distance between declaration of variable and its first usage
     * in multiple scopes.
     * @param aAST
     *        Regular node of AST which is checked for content of checking
     *        variable.
     * @param aVariable
     *        Variable which distance is calculated for.
     * @return Distance between declaration of variable and its first usage.
     */
    private int calculateDistanceBetweenScopes(
            DetailAST aAST, DetailAST aVariable)
    {
        int dist = 0;
        boolean firstUsageFound = false;
        DetailAST currentAST = aAST;
        final List<DetailAST> exprWithVariableList = new ArrayList<DetailAST>();

        while (currentAST != null
                && currentAST.getType() != TokenTypes.RCURLY)
        {
            if (currentAST.getFirstChild() != null) {

                if (isASTContainsElement(currentAST, aVariable)) {
                    exprWithVariableList.add(currentAST);
                    firstUsageFound = true;
                }
                else if (currentAST.getType() != TokenTypes.VARIABLE_DEF
                        && !firstUsageFound)
                {
                    dist++;
                }
            }
            currentAST = currentAST.getNextSibling();
        }

        if (exprWithVariableList.size() == 1) {
            final DetailAST blockWithVariableUsage = exprWithVariableList
                    .get(0);
            DetailAST variableUsageNode;

            switch (blockWithVariableUsage.getType()) {
            case TokenTypes.VARIABLE_DEF:
            case TokenTypes.EXPR:
                break;

            case TokenTypes.LITERAL_FOR:
            case TokenTypes.LITERAL_WHILE:
                variableUsageNode = findVariableUsageNodeInsideForWhileBlocks(
                        blockWithVariableUsage, aVariable);
                if (variableUsageNode != null) {
                    dist += calculateDistanceBetweenScopes(
                            variableUsageNode, aVariable);
                }
                break;

            case TokenTypes.LITERAL_DO:
                variableUsageNode = findVariableUsageNodeInsideDoWhileBlock(
                        blockWithVariableUsage, aVariable);
                if (variableUsageNode != null) {
                    dist += calculateDistanceBetweenScopes(
                            variableUsageNode, aVariable);
                }
                break;

            case TokenTypes.LITERAL_IF:
                variableUsageNode = findVariableUsageNodeInsideIfBlock(
                        blockWithVariableUsage, aVariable);
                if (variableUsageNode != null) {
                    dist += calculateDistanceBetweenScopes(
                            variableUsageNode, aVariable);
                }
                break;

            case TokenTypes.LITERAL_SWITCH:
                variableUsageNode = findVariableUsageNodeInsideSwitchBlock(
                        blockWithVariableUsage, aVariable);
                if (variableUsageNode != null) {
                    dist += calculateDistanceBetweenScopes(
                            variableUsageNode, aVariable);
                }
                break;

            case TokenTypes.LITERAL_TRY:
                variableUsageNode =
                        findVariableUsageNodeInsideTryCatchFinallyBlocks(
                                blockWithVariableUsage, aVariable);
                if (variableUsageNode != null) {
                    dist += calculateDistanceBetweenScopes(
                            variableUsageNode, aVariable);
                }
                break;

            default:
                dist += calculateDistanceBetweenScopes(
                        blockWithVariableUsage.getFirstChild(),
                        aVariable);
            }
        }
        else if (!firstUsageFound) {
            dist = 0;
        }

        return dist;
    }

    /**
     * Finds AST node inside FOR or WHILE blocks contained variable usage.
     * @param aBlock
     *        AST node represents FOR or WHILE block.
     * @param aVariable
     *        Variable which is checked for content in block.
     * @return If AST node, contained variable usage, was found inside block,
     *         return this node, otherwise - null.
     */
    private DetailAST findVariableUsageNodeInsideForWhileBlocks(
            DetailAST aBlock, DetailAST aVariable)
    {
        DetailAST variableUsageNode = null;

        if (!isVariableInOperatorDeclaration(aBlock, aVariable)) {
            variableUsageNode = aBlock.getFirstChild();

            while (variableUsageNode != null
                    && variableUsageNode.getType() != TokenTypes.RPAREN)
            {
                variableUsageNode = variableUsageNode.getNextSibling();
            }
            if (variableUsageNode != null) {
                variableUsageNode = variableUsageNode.getNextSibling();
                final int variableUsageNodeType = variableUsageNode.getType();

                if (variableUsageNodeType == TokenTypes.SLIST) {
                    variableUsageNode = variableUsageNode.getFirstChild();
                }
                else if (variableUsageNodeType == TokenTypes.VARIABLE_DEF
                        || variableUsageNodeType == TokenTypes.EXPR)
                {
                    variableUsageNode = null;
                }
            }
        }

        return variableUsageNode;
    }

    /**
     * Finds AST node inside DO-WHILE block contained variable usage.
     * @param aBlock
     *        AST node represents DO-WHILE block.
     * @param aVariable
     *        Variable which is checked for content in block.
     * @return If AST node, contained variable usage, was found inside block,
     *         return this node, otherwise - null.
     */
    private DetailAST findVariableUsageNodeInsideDoWhileBlock(
            DetailAST aBlock, DetailAST aVariable)
    {
        DetailAST variableUsageNode = null;

        if (!isVariableInOperatorDeclaration(aBlock, aVariable)) {
            variableUsageNode = aBlock.getFirstChild();
            final int variableUsageNodeType = variableUsageNode.getType();

            if (variableUsageNodeType == TokenTypes.SLIST) {
                variableUsageNode = variableUsageNode.getFirstChild();
            }
            else if (variableUsageNodeType == TokenTypes.VARIABLE_DEF
                    || variableUsageNodeType == TokenTypes.EXPR)
            {
                variableUsageNode = null;
            }
        }

        return variableUsageNode;
    }

    /**
     * Finds AST node inside IF block contained variable usage.
     * @param aBlock
     *        AST node represents IF block.
     * @param aVariable
     *        Variable which is checked for content in block.
     * @return If AST node, contained variable usage, was found inside block,
     *         return this node, otherwise - null.
     */
    private DetailAST findVariableUsageNodeInsideIfBlock(
            DetailAST aBlock, DetailAST aVariable)
    {
        DetailAST variableUsageNode = null;

        if (!isVariableInOperatorDeclaration(aBlock, aVariable)) {
            variableUsageNode = aBlock.getLastChild();
            final List<DetailAST> exprWithVariableList =
                    new ArrayList<DetailAST>();

            while (variableUsageNode != null
                    && variableUsageNode.getType()
                        == TokenTypes.LITERAL_ELSE)
            {
                final DetailAST previousNode =
                        variableUsageNode.getPreviousSibling();

                if (isASTContainsElement(previousNode, aVariable)) {
                    exprWithVariableList.add(previousNode);
                }
                variableUsageNode = variableUsageNode.getFirstChild();

                if (variableUsageNode.getType() == TokenTypes.LITERAL_IF) {
                    variableUsageNode = variableUsageNode.getLastChild();
                }
                else if (isASTContainsElement(variableUsageNode, aVariable)) {
                    exprWithVariableList.add(variableUsageNode);
                    variableUsageNode = null;
                }
            }

            if (variableUsageNode != null
                    && isASTContainsElement(variableUsageNode, aVariable))
            {
                exprWithVariableList.add(variableUsageNode);
            }

            if (exprWithVariableList.size() == 1) {
                variableUsageNode = exprWithVariableList.get(0);
            }
            else {
                variableUsageNode = null;
            }
        }

        return variableUsageNode;
    }

    /**
     * Finds AST node inside SWITCH block contained variable usage.
     * @param aBlock
     *        AST node represents SWITCH block.
     * @param aVariable
     *        Variable which is checked for content in block.
     * @return If AST node, contained variable usage, was found inside block,
     *         return this node, otherwise - null.
     */
    private DetailAST findVariableUsageNodeInsideSwitchBlock(
            DetailAST aBlock, DetailAST aVariable)
    {
        DetailAST variableUsageNode = null;

        if (!isVariableInOperatorDeclaration(aBlock, aVariable)) {
            variableUsageNode = aBlock
                    .findFirstToken(TokenTypes.CASE_GROUP);
            final List<DetailAST> exprWithVariableList =
                    new ArrayList<DetailAST>();

            while (variableUsageNode != null
                    && variableUsageNode.getType() == TokenTypes.CASE_GROUP)
            {
                final DetailAST lastNode = variableUsageNode.getLastChild();

                if (isASTContainsElement(lastNode, aVariable)) {
                    exprWithVariableList.add(lastNode);
                }
                variableUsageNode = variableUsageNode.getNextSibling();
            }

            if (exprWithVariableList.size() == 1) {
                variableUsageNode = exprWithVariableList.get(0);
            }
            else {
                variableUsageNode = null;
            }
        }

        return variableUsageNode;
    }

    /**
     * Finds AST node inside TRY-CATCH-FINALLY blocks contained variable usage.
     * @param aBlock
     *        AST node represents TRY-CATCH-FINALLY blocks.
     * @param aVariable
     *        Variable which is checked for content in block.
     * @return If AST node, contained variable usage, was found inside block,
     *         return this node, otherwise - null.
     */
    private DetailAST findVariableUsageNodeInsideTryCatchFinallyBlocks(
            DetailAST aBlock, DetailAST aVariable)
    {
        DetailAST variableUsageNode = null;

        variableUsageNode = aBlock.getFirstChild();
        final List<DetailAST> exprWithVariableList = new ArrayList<DetailAST>();

        if (isASTContainsElement(variableUsageNode, aVariable)) {
            exprWithVariableList.add(variableUsageNode);
        }

        variableUsageNode = variableUsageNode.getNextSibling();

        while (variableUsageNode != null
                && variableUsageNode.getType() == TokenTypes.LITERAL_CATCH)
        {
            final DetailAST currentNode = variableUsageNode.getLastChild();

            if (isASTContainsElement(currentNode, aVariable)) {
                exprWithVariableList.add(currentNode);
            }
            variableUsageNode = variableUsageNode.getNextSibling();
        }

        if (variableUsageNode != null) {
            final DetailAST currentNode = variableUsageNode.getLastChild();

            if (isASTContainsElement(currentNode, aVariable)) {
                exprWithVariableList.add(currentNode);
            }
        }

        if (exprWithVariableList.size() == 1) {
            variableUsageNode = exprWithVariableList.get(0).getFirstChild();
        }
        else {
            variableUsageNode = null;
        }

        return variableUsageNode;
    }

    /**
     * Checks if variable is in operator declaration. For instance:
     * <pre>
     * boolean b = true;
     * if (b) {...}
     * </pre>
     * Variable 'b' is in declaration of operator IF.
     * @param aASTSibling
     *        AST node which represents operator.
     * @param aVariable
     *        Variable which is checked for content in operator.
     * @return true if operator contains variable in its declaration, otherwise
     *         - false.
     */
    private boolean isVariableInOperatorDeclaration(
            DetailAST aASTSibling, DetailAST aVariable)
    {
        boolean isVarInOperatorDeclr = false;
        DetailAST currentAST = aASTSibling.getFirstChild();

        while (currentAST != null
                && currentAST.getType() != TokenTypes.LPAREN)
        {
            currentAST = currentAST.getNextSibling();
        }

        if (currentAST != null) {
            currentAST = currentAST
                    .getNextSibling(); // Get EXPR between braces

            if (isASTContainsElement(currentAST, aVariable)) {
                isVarInOperatorDeclr = true;
            }
            else {
                switch (aASTSibling.getType()) {
                case TokenTypes.LITERAL_IF:
                    currentAST = aASTSibling.getLastChild();

                    if (currentAST.getType() == TokenTypes.LITERAL_ELSE) {
                        currentAST = currentAST
                                .getFirstChild(); // Get IF followed by ELSE

                        if (currentAST.getType() == TokenTypes.LITERAL_IF) {
                            isVarInOperatorDeclr |=
                                    isVariableInOperatorDeclaration(
                                            currentAST, aVariable);
                        }
                    }
                    break;

                case TokenTypes.LITERAL_SWITCH:
                    currentAST = aASTSibling
                            .findFirstToken(TokenTypes.CASE_GROUP);
                    while (currentAST != null
                            && currentAST.getType() == TokenTypes.CASE_GROUP)
                    {
                        final DetailAST currentASTChild = currentAST
                                .getFirstChild();

                        if (isASTContainsElement(currentASTChild, aVariable)) {
                            isVarInOperatorDeclr = true;
                            break;
                        }
                        currentAST = currentAST.getNextSibling();
                    }
                    break;

                default:
                    ;// No code!
                }
            }
        }

        return isVarInOperatorDeclr;
    }

    /**
     * Checks if AST node contains given element.
     * @param aAST
     *        Node of AST.
     * @param aElement
     *        AST element which is checked for content in AST node.
     * @return true if AST element was found in AST node, otherwise - false.
     */
    private boolean isASTContainsElement(DetailAST aAST, DetailAST aElement)
    {
        boolean isASTContainsElement = false;
        final ASTEnumeration astList = aAST.findAllPartial(aElement);

        while (astList.hasMoreNodes()) {
            final DetailAST astElement = (DetailAST) astList.nextNode();
            DetailAST astElementParent = astElement.getParent();

            while (astElementParent != null) {

                if (astElementParent.equals(aAST)
                        && astElementParent.getLineNo() == aAST.getLineNo())
                {
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
     * @param aVariable
     *        Variable which is checked for content in ignored pattern.
     * @return true if variable was found, otherwise - false.
     */
    private boolean isVariableMatchesIgnorePattern(String aVariable)
    {
        final Matcher matcher = mIgnoreVariablePattern.matcher(aVariable);
        return matcher.matches();
    }
}
