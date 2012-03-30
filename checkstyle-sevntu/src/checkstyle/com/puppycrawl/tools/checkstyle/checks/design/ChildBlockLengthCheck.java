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

import java.util.LinkedList;
import java.util.List;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * This check detects the child blocks, which length is more then 80% of parent
 * block length. <br> <p>
 * Supported keywords are used to detect blocks: <br>
 * "if", "else", "for", "switch", "do", "while", "try", "catch".
 * </p><p><i>Rationale:</i></p><p>
 * Length of child block that is more then 80% of parent block is usually hard
 * to read in case child block is long(few display screens). Such child blocks
 * should be refactored or moved to separate method.</p>
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil
 *         Yaroslavtsev</a>
 */
public class ChildBlockLengthCheck extends Check
{

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    private static final String WARNING_MSG_KEY =
            "child.block.length";

    /**
     * The number used to convert to percentages.
     */
    private static final double PERCENTS_FACTOR = 100.0;

    /**
     * Default value of maximum percentage ratio between the child and the
     * parent block.
     */
    private static final int DEFAULT_MAX_CHILD_BLOCK_PERCENTAGE = 80;

    /**
     * Default number of lines of which block body may consist to be
     * skipped by check.
     */
    private static final int DEFAULT_IGNORE_BLOCK_LINESCOUNT = 50;

    /**
     * Array contains all allowed block types to be checked. Supported block
     * types: LITERAL_IF, LITERAL_SWITCH, LITERAL_FOR, LITERAL_DO,
     * LITERAL_WHILE, LITERAL_TRY, LITERAL_ELSE, LITERAL_CATCH.
     */
    private int [] mBlockTypes;

    /**
     * Maximum percentage ratio between the child block and the parent block.
     * Default value = 80%;
     */
    private int mMaxChildBlockPercentage = DEFAULT_MAX_CHILD_BLOCK_PERCENTAGE;

    /**
     * Maximum number of lines of which block body may consist to
     * be skipped by check.
     */
    private int mIgnoreBlockLinesCount = DEFAULT_IGNORE_BLOCK_LINESCOUNT;

    /**
     * Sets allowed types of blocks to be checked. Supported block types:
     * LITERAL_IF, LITERAL_SWITCH, LITERAL_FOR, LITERAL_DO, LITERAL_WHILE,
     * LITERAL_TRY, LITERAL_ELSE, LITERAL_CATCH.
     * @param aBlockTypes
     *        - DetailAST tokenTypes that are related to the types which are
     *        allowed by user in check preferences.
     **/
    public void setBlockTypes(final String[] aBlockTypes)
    {
        mBlockTypes = new int[aBlockTypes.length];
        for (int i = 0; i < aBlockTypes.length; i++) {
            mBlockTypes[i] = TokenTypes
                    .getTokenId(aBlockTypes[i]);
        }
    }

    /**
     * Sets the maximum percentage ratio between child and parent
     * block. (sets "maxChildBlockPercentage" option value)
     * @param aMaxChildBlockPercentage
     *        the new "maxChildBlockPercentage" option value.
     */
    public void setMaxChildBlockPercentage(int aMaxChildBlockPercentage)
    {
        this.mMaxChildBlockPercentage = aMaxChildBlockPercentage;
    }

    /**
     * Sets the maximum linelength of blocks that will be ignored by check.
     * @param aIgnoreBlockLinesCount
     *        the maximum linelength of the block to be ignored.
     */
    public void setIgnoreBlockLinesCount(int aIgnoreBlockLinesCount)
    {
        this.mIgnoreBlockLinesCount = aIgnoreBlockLinesCount;
    }

    @Override
    public int[] getDefaultTokens()
    {
        return mBlockTypes;
    }

    @Override
    public void visitToken(DetailAST aAst)
    {
        final DetailAST aBlockOpeningBrace = getOpeningBrace(aAst);

        if (aBlockOpeningBrace != null) { // if the block has braces

            final DetailAST aBlockClosingBrace = getClosingBrace(aAst);
            final int parentBlockLinesCount = getLinesCount(aBlockOpeningBrace,
                    aBlockClosingBrace);

            if (parentBlockLinesCount > mIgnoreBlockLinesCount) {

                final List<DetailAST> childBlocks = getChildBlocks(
                        aBlockOpeningBrace, aBlockClosingBrace);

                final List<DetailAST> wrongChildBlocks = getWrongChildBlocks(
                        childBlocks, parentBlockLinesCount);

                if (wrongChildBlocks.size() == 0) {
                    for (DetailAST childBlock : childBlocks) {
                        visitToken(childBlock);
                    }
                }
                else {
                    for (DetailAST wrongChildBlock : wrongChildBlocks) {
                        final double allowedChildBlockSize =
                                (double) (parentBlockLinesCount
                                * mMaxChildBlockPercentage) / PERCENTS_FACTOR;
                        log(wrongChildBlock, WARNING_MSG_KEY,
                                (int) (allowedChildBlockSize));
                    }
                }
            }
        }
    }

    /**
     * Gets all the child blocks for given parent block. Uses an iterative
     * algorithm such as default visitToken method.
     * @param aBlockOpeningBrace
     *        a DetailAST node that points to the current method`s opening
     *        brace.
     * @param aBlockClosingBrace
     *        the a block closing brace
     * @return "return" literals count for given method.
     */
    private List<DetailAST> getChildBlocks(
            DetailAST aBlockOpeningBrace, DetailAST aBlockClosingBrace)
    {
        final List<DetailAST> childBlocks = new LinkedList<DetailAST>();

        DetailAST curNode = aBlockOpeningBrace;

        while (curNode != null) {
            // before node visiting
            if (curNode == aBlockClosingBrace) {
                break; // stop at closing brace
            }
            else {
                if (isAllowedChildBlockType(curNode.getType())) {
                    childBlocks.add(curNode);
                }
            }
            // before node leaving
            DetailAST nextNode = curNode.getFirstChild();

            final int type = curNode.getType();
            // and anonimous classes and nested methods
            if (type == TokenTypes.METHOD_DEF
                    || type == TokenTypes.CLASS_DEF)
            {
                nextNode = curNode.getNextSibling();
            }

            while ((curNode != null) && (nextNode == null)) {
                // leave the visited Node
                nextNode = curNode.getNextSibling();
                if (nextNode == null) {
                    curNode = curNode.getParent();
                }
            }
            curNode = nextNode;
        }
        return childBlocks;
    }

    /**
     * Checks that given child block type is allowed.
     *
     * @param aBlockType the token type ID for the given block.
     * @return true, if the given child block type is allowed.
     */
    private boolean isAllowedChildBlockType(int aBlockType)
    {
        boolean result = false;
        for (int type : mBlockTypes) {
            if (type == aBlockType) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Gets the child blocks which .
     *
     * @param aBlocksList the blocks
     * @param aParentBlockSize the a parent block size
     * @return the wrong child blocks
     */
    private List<DetailAST> getWrongChildBlocks(
            List<DetailAST> aBlocksList, int aParentBlockSize)
    {
        final List<DetailAST> result = new LinkedList<DetailAST>();
        for (DetailAST block: aBlocksList) {
            if (isChildBlockWrong(block, aParentBlockSize)) {
                result.add(block);
            }
        }
        return result;
    }

    /**
     * Checks if is child block wrong.
     *
     * @param aChildBlockNode the a child block node
     * @param aParentBlockSize the a parent block size
     * @return true, if is child block wrong
     */
    private boolean isChildBlockWrong(
            DetailAST aChildBlockNode, int aParentBlockSize)
    {
        boolean result = false;
        final DetailAST childBlockOpeningBrace =
                getOpeningBrace(aChildBlockNode);
        if (childBlockOpeningBrace != null) {
            final DetailAST childBlockClosingBrace =
                    getClosingBrace(aChildBlockNode);
            final int childBlockSize = getLinesCount(childBlockOpeningBrace,
                    childBlockClosingBrace);
            final double percentage = ((double) childBlockSize
                    / (double) aParentBlockSize) * 100.0;
            result = percentage > mMaxChildBlockPercentage;
        }
        return result;
    }

    /**
     * Gets the opening brace for the given block.
     * @param aParentBlockNode
     *        the DetailAST node is related to the given parent block.
     * @return the DetailAST node is related to the given block opening brace
     */
    private static DetailAST getOpeningBrace(final DetailAST aParentBlockNode)
    {
        return (aParentBlockNode.getType() == TokenTypes.LITERAL_SWITCH)
                ? aParentBlockNode.findFirstToken(TokenTypes.LCURLY)
                : aParentBlockNode.findFirstToken(TokenTypes.SLIST);
    }

    /**
     * Gets the closing brace for the given block.
     * @param aParentBlockNode
     *        the DetailAST node is related to the given parent block.
     * @return the DetailAST node is related to the given block closing brace
     */
    private static DetailAST getClosingBrace(DetailAST aParentBlockNode)
    {
        final int aParentBlockType = aParentBlockNode.getType();
        return (aParentBlockType == TokenTypes.LITERAL_SWITCH)
                ? aParentBlockNode.getLastChild()
                : getOpeningBrace(aParentBlockNode).getLastChild();
    }

    /**
     * Gets the lines count between the given block opening and closing braces.
     * @param aOpeningBrace
     *        the block opening brace DetailAST (LCURLY or SLIST type)
     * @param aClosingBrace
     *        the block closing brace DetailAST (RCURLY type)
     * @return the lines count between the given block braces.
     */
    private static int getLinesCount(
            DetailAST aOpeningBrace, DetailAST aClosingBrace)
    {
        int result = aClosingBrace.getLineNo() - aOpeningBrace.getLineNo();
        if (result != 0) {
            result--;
        }
        return result;
    }

}
