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

/**
 * <p>
 * This check prevents exception throwing and re-throwing inside try/catch
 * blocks, if object is initially caused by the exception will be lost after
 * this action and the error will not propagate up to a higher-level handler.
 * </p>
 * <p>
 * Rationale: When handling exceptions using try/catch blocks junior developers
 * may simply lose the original exception object and information associated with
 * it.
 * </p>
 * Examples:
 * <br> <br>
 * <ol>
 * <li>Cause exception can be lost when exception throwing.</li>
 * <code>
 *      <pre>
 *       public void foo() {
 *          RuntimeException r;
 *         catch (java.lang.Exception e) {
 *           //your code
 *           throw r;
 *         }
 *       }</pre> </code>
 * <li>Cause exception can be lost when exception re-throwing.</li>
 * <code> <pre>
 *      catch (IllegalStateException e) {
 *        //your code
 *        throw new RuntimeException();
 *      }
 *      catch (IllegalStateException e) {
 *        //your code
 *        throw new RuntimeException("Runtime Ecxeption!");
 *      } </pre> </code>
 * </ol>
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil
 *         Yaroslavtsev</a>
 */
public class AvoidHidingCauseExceptionCheck extends Check
{

    /** List containing all throw keyword DetailASTs for current catch block.*/
    private LinkedList<DetailAST> mThrowList = new LinkedList<DetailAST>();

    /** DetailAST contains the name of current thrown exception.*/
    private DetailAST mExceptionNameAST;

    /** A list containing the names of all the exceptions
     * that override the original.*/
    private LinkedList<String> mOverridingExcNames =
        new LinkedList<String>();

    /** Creates new instance of the check. */
    public AvoidHidingCauseExceptionCheck()
    {
    }

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] {TokenTypes.LITERAL_CATCH};
    }

    @Override
    public void visitToken(DetailAST aDetailAST)
    {

        final String originExcName = aDetailAST
                .findFirstToken(TokenTypes.PARAMETER_DEF).getLastChild()
                .getText();

        mOverridingExcNames.add(originExcName);

        makeThrowList(aDetailAST);
        makeExceptionsList(aDetailAST, aDetailAST, originExcName);

        for (DetailAST throwAST : mThrowList) {

            mExceptionNameAST = null;

            if (throwAST.getType() == TokenTypes.LITERAL_THROW) {

                mExceptionNameAST = findThrowExcName(throwAST);

                if (mExceptionNameAST != null
                        && mExceptionNameAST.getParent()
                            .getType() == TokenTypes.DOT
                        || !mOverridingExcNames.contains(mExceptionNameAST
                                .getText()))
                {
                    log(throwAST, "avoid.hiding.cause.exception",
                            originExcName);
                }
            }
        }

        if (!mThrowList.isEmpty()) {
            mThrowList.clear();
        }
        if (!mOverridingExcNames.isEmpty()) {
            mOverridingExcNames.clear();
        }
    }

    /**
     * Returns a DetailAST contains the name of throwing Exception for
     * current throw keyword.
     * @param aStartNode The start node for exception name searching.
     * @return The DetailAST of desired token which contains thrown
     * exception name if it was found or null otherwise.
     */
    public DetailAST findThrowExcName(DetailAST aStartNode)
    {
        for (DetailAST currentNode : getChildNodes(aStartNode)) {

            if (currentNode.getType() == TokenTypes.IDENT) {
                mExceptionNameAST = currentNode;
            }

            if (currentNode.getType() != TokenTypes.PARAMETER_DEF
                    && currentNode.getType() != TokenTypes.LITERAL_TRY
                    && currentNode.getNumberOfChildren() > 0)
            {
                findThrowExcName(currentNode);
            }

        }
        return mExceptionNameAST;
    }

    /**
     * Recursive method which searches for the <code>LITERAL_THROW</code>
     * DetailASTs all levels below current <code>aParentAST</code> node
     * without entering into nested try/catch blocks.
     * @param aParentAST A start node for "throw" keywords <code>DetailASTs
     * </code> searching.
     * @return List contains all "throw" keyword nodes
     * (<code>LITERAL_THROW</code>) for certain parent node (<code>
     * aParentAST</code>) except those that are in nested try/catch blocks.
     */
    public LinkedList<DetailAST> makeThrowList(DetailAST aParentAST)
    {

        for (DetailAST currentNode : getChildNodes(aParentAST)) {

            if (currentNode.getType() == TokenTypes.LITERAL_THROW) {
                mThrowList.add(currentNode);
            }

            if (currentNode.getType() != TokenTypes.PARAMETER_DEF
                    && currentNode.getType() != TokenTypes.LITERAL_THROW
                    && currentNode.getType() != TokenTypes.LITERAL_TRY
                    && currentNode.getNumberOfChildren() > 0)
            {
                makeThrowList(currentNode);
            }

        }
        return mThrowList;
    }

    /**
     * Searches for all exceptions which overrides the original exception
     * object (only in current "catch" block).
     * @param aCurrentCatchAST A LITERAL_CATCH node of the
     * current "catch" block.
     * @param aParentAST Current parent node to start search.
     * @param aCurrentExcName The name of Exception handled by
     * current "catch" block.
     * @return An array contains exceptions that override the original
     * exception object of the current "catch" block.
     */
    public LinkedList<String> makeExceptionsList(DetailAST aCurrentCatchAST,
            DetailAST aParentAST, String aCurrentExcName)
    {

        for (DetailAST currentNode : getChildNodes(aParentAST)) {

            if (currentNode.getType() == TokenTypes.IDENT
                    && currentNode.getText().equals(aCurrentExcName)
                    && currentNode.getParent() != null
                    && currentNode.getParent().getType() != TokenTypes.DOT)
            {

                DetailAST temp = currentNode;

                while (!temp.equals(aCurrentCatchAST)
                        && temp.getType() != TokenTypes.ASSIGN)
                {
                    temp = temp.getParent();
                }

                if (temp.getType() == TokenTypes.ASSIGN) {
                    DetailAST convertedExc = null;
                    if (temp.getParent().getType() == TokenTypes.VARIABLE_DEF) {
                        convertedExc = temp.getParent().findFirstToken(
                                TokenTypes.IDENT);
                    }
                    else {
                        convertedExc = temp.findFirstToken(TokenTypes.IDENT);
                    }

                    if (convertedExc != null
                            && !convertedExc.getText().equals(aCurrentExcName))
                    {
                        if (!mOverridingExcNames.contains(convertedExc
                                .getText()))
                        {
                            mOverridingExcNames.add(convertedExc
                                    .getText());
                        }
                    }

                }

            }

            if (currentNode.getType() != TokenTypes.PARAMETER_DEF
                    && currentNode.getNumberOfChildren() > 0)
            {
                makeExceptionsList(aCurrentCatchAST, currentNode,
                        aCurrentExcName);
            }

        }
        return mOverridingExcNames;
    }

    /**
     * Gets all the children one level below on the current top node.
     * @param aNode Current parent node.
     * @return New DetailAST[] array of children one level below on the current
     *         parent node (aNode).
     */
    public LinkedList<DetailAST> getChildNodes(DetailAST aNode)
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
