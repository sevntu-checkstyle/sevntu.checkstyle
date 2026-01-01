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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.TokenUtil;

/**
 * This check detects the child blocks, which length is more then 80% of parent
 * block length. <br>
 * <p>
 * Supported keywords are used to detect blocks: <br>
 * "if", "else", "for", "switch", "do", "while", "try", "catch".
 * </p>
 * <p>
 * <i>Rationale:</i>
 * </p>
 * <p>
 * Length of child block that is more then 80% of parent block is usually hard
 * to read in case child block is long(few display screens). Such child blocks
 * should be refactored or moved to separate method.
 * </p>
 *
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil
 *         Yaroslavtsev</a>
 * @since 1.8.0
 */
public class ChildBlockLengthCheck extends AbstractCheck {

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY = "child.block.length";

    /**
     * The constant is used in percentage arithmetic operations. Represents
     * '100%'
     */
    private static final double PERCENTS_FACTOR = 100.0;

    /**
     * Default value of maximum percentage ratio between the child and the
     * parent block.
     */
    private static final double DEFAULT_MAX_CHILD_BLOCK_PERCENTAGE = 80.0;

    /**
     * Default number of lines of which block body may consist to be skipped by
     * check.
     */
    private static final int DEFAULT_IGNORE_BLOCK_LINESCOUNT = 50;

    /**
     * Array contains all allowed block types to be checked. Supported block
     * types: LITERAL_IF, LITERAL_SWITCH, LITERAL_FOR, LITERAL_DO,
     * LITERAL_WHILE, LITERAL_TRY, LITERAL_ELSE, LITERAL_CATCH.
     */
    private int[] blockTypes;

    /**
     * Maximum percentage ratio between the child block and the parent block.
     */
    private double maxChildBlockPercentage = DEFAULT_MAX_CHILD_BLOCK_PERCENTAGE;

    /**
     * Maximum number of lines of which block body may consist to be skipped by
     * check.
     */
    private int ignoreBlockLinesCount = DEFAULT_IGNORE_BLOCK_LINESCOUNT;

    /**
     * Sets allowed types of blocks to be checked. Supported block types:
     * LITERAL_IF, LITERAL_SWITCH, LITERAL_FOR, LITERAL_DO, LITERAL_WHILE,
     * LITERAL_TRY, LITERAL_ELSE, LITERAL_CATCH.
     *
     * @param blockTypes
     *        - DetailAST tokenTypes that are related to the types which are
     *        allowed by user in check preferences.
     **/
    public void setBlockTypes(final String... blockTypes) {
        this.blockTypes = new int[blockTypes.length];
        for (int index = 0; index < blockTypes.length; index++) {
            this.blockTypes[index] = TokenUtil.getTokenId(blockTypes[index]);
        }
    }

    /**
     * Sets the maximum percentage ratio between child and parent block. (sets
     * "maxChildBlockPercentage" option value)
     *
     * @param maxChildBlockPercentage
     *        the new "maxChildBlockPercentage" option value.
     */
    public void setMaxChildBlockPercentage(int maxChildBlockPercentage) {
        this.maxChildBlockPercentage = maxChildBlockPercentage;
    }

    /**
     * Sets the maximum linelength of blocks that will be ignored by check.
     *
     * @param ignoreBlockLinesCount
     *        the maximum linelength of the block to be ignored.
     */
    public void setIgnoreBlockLinesCount(int ignoreBlockLinesCount) {
        this.ignoreBlockLinesCount = ignoreBlockLinesCount;
    }

    // -@cs[SimpleAccessorNameNotation] Overrides method from the base class.
    // Issue: https://github.com/sevntu-checkstyle/sevntu.checkstyle/issues/166
    @Override
    public int[] getDefaultTokens() {
        final int[] result;
        if (blockTypes == null) {
            result = null;
        }
        else {
            result = Arrays.copyOf(blockTypes, blockTypes.length);
        }
        return result;
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
        final DetailAST aOpeningBrace = openingBrace(ast);

        // if the block has braces at all
        if (aOpeningBrace != null) {
            final DetailAST aClosingBrace = closingBrace(ast);
            final int parentBlockSize =
                    linesCount(aOpeningBrace, aClosingBrace);

            if (parentBlockSize > ignoreBlockLinesCount) {
                final List<DetailAST> childBlocks = getChildBlocks(
                        aOpeningBrace, aClosingBrace);

                final List<DetailAST> badChildBlocks = getBadChildBlocks(
                        childBlocks, parentBlockSize);

                if (badChildBlocks.isEmpty()) {
                    for (DetailAST childBlock : childBlocks) {
                        visitToken(childBlock);
                    }
                }
                else {
                    for (DetailAST badBlock : badChildBlocks) {
                        final int blockSize = linesCount(badBlock);

                        final double allowedBlockSize = (int) (parentBlockSize
                                * maxChildBlockPercentage / PERCENTS_FACTOR);

                        log(badBlock, MSG_KEY, blockSize, allowedBlockSize);
                    }
                }
            }
        }
    }

    /**
     * Gets all the child blocks for given parent block. Uses an iterative
     * algorithm.
     *
     * @param blockOpeningBrace
     *        a DetailAST node that points to the current method`s opening
     *        brace.
     * @param blockClosingBrace
     *        the a block closing brace
     * @return all child blocks that have braces.
     */
    private List<DetailAST> getChildBlocks(DetailAST blockOpeningBrace,
            DetailAST blockClosingBrace) {
        final List<DetailAST> childBlocks = new LinkedList<>();

        DetailAST curNode = blockOpeningBrace;

        while (curNode != blockClosingBrace) {
            if (isAllowedBlockType(curNode.getType())) {
                childBlocks.add(curNode);
            }
            // before node leaving
            DetailAST nextNode = curNode.getFirstChild();

            final int type = curNode.getType();
            // skip anonymous classes and nested methods
            if (type == TokenTypes.METHOD_DEF
                    || type == TokenTypes.CLASS_DEF) {
                nextNode = curNode.getNextSibling();
            }

            while (nextNode == null) {
                // leave the visited node
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
     * @param blockType
     *        the token type ID for the given block.
     * @return true, if the given child block type is allowed.
     */
    private boolean isAllowedBlockType(int blockType) {
        boolean result = false;
        for (int type : blockTypes) {
            if (type == blockType) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Gets the child blocks which occupies too much size (in percentage) of
     * given parent block size.
     *
     * @param blocksList
     *        the blocks
     * @param parentBlockSize
     *        the a parent block size
     * @return the wrong child blocks
     */
    private List<DetailAST> getBadChildBlocks(List<DetailAST> blocksList,
            int parentBlockSize) {
        final List<DetailAST> result = new LinkedList<>();
        for (DetailAST block : blocksList) {
            if (isChildBlockBad(block, parentBlockSize)) {
                result.add(block);
            }
        }
        return result;
    }

    /**
     * Checks if the child block size percentage from parent block is greater
     * than.
     *
     * @param childBlock
     *        the a child block node
     * @param parentBlockSize
     *        the a parent block size
     * @return true, if is child block wrong
     */
    private boolean isChildBlockBad(DetailAST childBlock,
            int parentBlockSize) {
        boolean result = false;
        final DetailAST openingBrace = openingBrace(childBlock);
        if (openingBrace != null) {
            final DetailAST closingBrace = closingBrace(childBlock);
            final int childBlockSize = linesCount(openingBrace, closingBrace);
            result = getPercentage(parentBlockSize, childBlockSize);
        }
        return result;
    }

    /**
     * Gets the percentage which the child block occupies inside the parent
     * block.
     *
     * @param parentBlockSize
     *        the parent block size in lines
     * @param childBlockSize
     *        the child block size in lines
     * @return the percentage value.
     */
    private boolean getPercentage(int parentBlockSize,
            final int childBlockSize) {
        final double percentage =
                ((double) childBlockSize / (double) parentBlockSize) * 100.0;
        return percentage > maxChildBlockPercentage;
    }

    /**
     * Gets the opening brace for the given block.
     *
     * @param parentBlock
     *        the DetailAST node is related to the given parent block.
     * @return the DetailAST node is related to the given block opening brace
     */
    private static DetailAST openingBrace(final DetailAST parentBlock) {
        final int searchType;
        if (parentBlock.getType() == TokenTypes.LITERAL_SWITCH) {
            searchType = TokenTypes.LCURLY;
        }
        else {
            searchType = TokenTypes.SLIST;
        }
        return parentBlock.findFirstToken(searchType);
    }

    /**
     * Gets the closing brace for the given block.
     *
     * @param parentBlockNode
     *        the DetailAST node is related to the given parent block.
     * @return the DetailAST node is related to the given block closing brace
     */
    private static DetailAST closingBrace(DetailAST parentBlockNode) {
        final int aParentBlockType = parentBlockNode.getType();
        final DetailAST result;
        if (aParentBlockType == TokenTypes.LITERAL_SWITCH) {
            result = parentBlockNode.getLastChild();
        }
        else {
            result = openingBrace(parentBlockNode).getLastChild();
        }
        return result;
    }

    /**
     * Gets the lines count between braces of the given block.
     *
     * @param blockAst
     *        - the DetailAST node is related to the given block (the block
     *        should have braces!).
     * @return the lines count between the given block braces.
     */
    private static int linesCount(DetailAST blockAst) {
        return linesCount(openingBrace(blockAst), closingBrace(blockAst));
    }

    /**
     * Gets the lines count between the given block opening and closing braces.
     *
     * @param openingBrace
     *        the block opening brace DetailAST (LCURLY or SLIST type)
     * @param closingBrace
     *        the block closing brace DetailAST (RCURLY type)
     * @return the lines count between the given block braces.
     */
    private static int linesCount(DetailAST openingBrace,
            DetailAST closingBrace) {
        int result = closingBrace.getLineNo() - openingBrace.getLineNo();
        if (result != 0) {
            result--;
        }
        return result;
    }

}
