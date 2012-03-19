package com.puppycrawl.tools.checkstyle.checks.design;

import java.util.LinkedList;
import java.util.List;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 *
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
     * Default value of maximum percentage ratio between the child block and the
     * parent block.
     */
    private static final int DEFAULT_MAX_CHILD_BLOCK_PERCENTAGE = 90;

    /**
     * Array contains all allowed types of blocks to be checked. Supported block
     * types: LITERAL_IF, LITERAL_SWITCH, LITERAL_FOR, LITERAL_DO,
     * LITERAL_WHILE, LITERAL_TRY, LITERAL_ELSE, LITERAL_CATCH.
     */
    private int [] mAllowedBlockTypes;

    /**
     * Maximum percentage ratio between the child block and the parent block.
     * Default value = 90%;
     */
    private int mMaxChildBlockPercentage = DEFAULT_MAX_CHILD_BLOCK_PERCENTAGE;

    /**
     * Sets allowed types of blocks to be checked. Supported block types:
     * LITERAL_IF, LITERAL_SWITCH, LITERAL_FOR, LITERAL_DO, LITERAL_WHILE,
     * LITERAL_TRY, LITERAL_ELSE, LITERAL_CATCH.
     * @param aAllowedBlockTypes
     *        - DetailAST tokenTypes that are related to the types which are
     *        allowed by user in check preferences.
     **/
    public void setAllowedBlockTypes(final String[] aAllowedBlockTypes)
    {
        mAllowedBlockTypes = new int[aAllowedBlockTypes.length];
        for (int i = 0; i < aAllowedBlockTypes.length; i++) {
            mAllowedBlockTypes[i] = TokenTypes.getTokenId(aAllowedBlockTypes[i]);
        }
    }

    /**
     * Sets the maximum percentage ratio between the child block and the parent
     * block.
     * @param aMaxChildBlockPercentage - new value for "maxChildBlockPercentage" option.
     */
    public void setMaxChildBlockPercentage(int aMaxChildBlockPercentage) {
        this.mMaxChildBlockPercentage = aMaxChildBlockPercentage;
    }

    @Override
    public int[] getDefaultTokens()
    {
        return mAllowedBlockTypes;
    }

    @Override
    public void visitToken(DetailAST aAst)
    {

        final DetailAST aBlockOpeningBrace = getOpeningBrace(aAst);

        if (aBlockOpeningBrace != null) { // if the block has braces

            final DetailAST aBlockClosingBrace = getClosingBrace(aAst);
            final int parentBlockLinesCount = getLinesCount(aBlockOpeningBrace,
                    aBlockClosingBrace);

            final List<DetailAST> childBlocks = getChildBlocks(aBlockOpeningBrace,
                    aBlockClosingBrace);

            final List<DetailAST> wrongChildBlocks = getWrongChildBlocks(childBlocks,
                    parentBlockLinesCount);

            if(wrongChildBlocks.size() == 0) {
                for(DetailAST childBlock: childBlocks) {
                    visitToken(childBlock);
                }
            }
            else {
                for (DetailAST wrongChildBlock : wrongChildBlocks) {
                    final double allowedChildBlockSize =
                            (double) (parentBlockLinesCount * mMaxChildBlockPercentage) / 100.0;
                    log(wrongChildBlock, WARNING_MSG_KEY,
                            (int) (allowedChildBlockSize));
                }
            }

        }

    }

    /**
     * @param blocks
     * @return
     */
    private List<DetailAST> getWrongChildBlocks(List<DetailAST> blocks, int aParentBlockSize)
    {
        List<DetailAST> result = new LinkedList<DetailAST>();
        for(DetailAST block: blocks) {
            if(isChildBlockWrong(block, aParentBlockSize)) {
                result.add(block);
            }
        }
        return result;
    }

    /**
     * Gets all the child blocks for given parent block. Uses an iterative
     * algorithm.
     * @param aBlockOpeningBrace
     *        a DetailAST node that points to the current method`s opening
     *        brace.
     * @param aParentBlockNode
     *        DetailAST node is pointing to current method definition is being
     *        processed.
     * @return "return" literals count for given method.
     */
    private List<DetailAST> getChildBlocks(DetailAST aBlockOpeningBrace, DetailAST aBlockClosingBrace)
    {
        List<DetailAST> childBlocks = new LinkedList<DetailAST>();

        DetailAST curNode = aBlockOpeningBrace;

        while (curNode != null) {

            // before node visiting
            if (curNode == aBlockClosingBrace) {
                break; // stop at closing brace
            }
            else {
                if (isAllowedChildBlock(curNode.getType())) {
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
     * @param curNode
     * @return
     */
    private boolean isChildBlockWrong(DetailAST aChildBlockNode, int aParentBlockSize)
    {
        boolean result = false;
        final DetailAST childBlockOpeningBrace = getOpeningBrace(aChildBlockNode);
        if (childBlockOpeningBrace != null) {
            final DetailAST childBlockClosingBrace = getClosingBrace(aChildBlockNode);
            int childBlockSize = getLinesCount(childBlockOpeningBrace,
                    childBlockClosingBrace);
            final double percentage = ((double) childBlockSize / (double) aParentBlockSize) * 100.0;
            result = percentage >= mMaxChildBlockPercentage;
        }
        return result;
    }


    /**
     * @param aBlockOpeningBrace
     * @param aBlockClosingBrace
     * @return the line count between  tokens.
     */
    private static int getLinesCount(DetailAST aBlockOpeningBrace, DetailAST aBlockClosingBrace)
    {
        int result = aBlockClosingBrace.getLineNo()
                - aBlockOpeningBrace.getLineNo();
        if (result != 0) {
            result--;
        }
        return result;
    }


    /**
     * 
     * @param aParentBlockNode
     * @return
     */
    private DetailAST getOpeningBrace(final DetailAST aParentBlockNode)
    {
        return (aParentBlockNode.getType() == TokenTypes.LITERAL_SWITCH) ?
                aParentBlockNode.findFirstToken(TokenTypes.LCURLY)
                : aParentBlockNode.findFirstToken(TokenTypes.SLIST);
    }

    /**
     * @param aParentBlockNode
     * @return
     */
    private DetailAST getClosingBrace(DetailAST aParentBlockNode)
    {
        int aParentBlockType = aParentBlockNode.getType();
        return (aParentBlockType == TokenTypes.LITERAL_SWITCH) ?
                aParentBlockNode.getLastChild()
                : getOpeningBrace(aParentBlockNode).getLastChild();
    }

    /**
     * @param blockType
     * @return
     */
    private boolean isAllowedChildBlock(int blockType)
    {
        for (int type : mAllowedBlockTypes) {
            if (type == blockType) {
                return true;
            }
        }
        return false;
    }

}
