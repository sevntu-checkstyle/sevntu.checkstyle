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

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.api.Check;

/**
 * <p>
 * This check prevents exception throwing and re-throwing inside
 * try/catch blocks, if object is initially caused by the exception
 * will be lost after this action and the error will not propagate
 * up to a higher-level handler.
 * </p>
 * <p>
 * Rationale: When handling exceptions using try/catch blocks junior developers
 * may simply lose the original exception object and
 * information associated with it.
 * </p>
 * <pre> Examples: </pre>
 * <ol><li> Cause exception was lost while exception throwing. </li>
  *      <code>
  *      <pre>
 *       public void foo() {
 *          RuntimeException r;
 *         catch (java.lang.Exception e) {
 *           //your code
 *           throw r;
 *         }
 *       }</pre> </code>
 *    <li> Cause exception was lost while exception re-throwing. </li>
 *       <code> <pre>
 *      catch (IllegalStateException e) {
 *        //your code
 *        throw new RuntimeException();
 *      }
 *
 *      catch (IllegalStateException e) {
 *        //your code
 *        throw new RuntimeException("Runtime Ecxeption!");
 *      } </pre> </code> </ol>
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil
 *         Yaroslavtsev</a>
 */
public class AvoidHidingCauseExceptionCheck extends Check
{

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
        // retrieve an exception name from current
        // "catch" block parameters definition
        final String originExcName = aDetailAST
                .findFirstToken(TokenTypes.PARAMETER_DEF).getLastChild()
                .getText();

        final DetailAST throwAST = getChildTokenAST(aDetailAST,
                TokenTypes.LITERAL_THROW, "throw");
        DetailAST rethrowExcNameAST = null;

        // retrieve a DetailAST which contains the name
        // of rethrown exception or null if rethrow does not
        // exist in current "catch" block
        if (throwAST != null) {
            rethrowExcNameAST = getChildTokenAST(throwAST, TokenTypes.IDENT,
                    originExcName);
        }

        if (throwAST != null
                && ( rethrowExcNameAST == null
                     || rethrowExcNameAST.getParent().getType() == TokenTypes.DOT
                     || !originExcName.equals(rethrowExcNameAST.getText())))
        {
            log(throwAST, "avoid.hiding.cause.exception", originExcName);
        }
    }

    /**
     * Looking for the certain token (TokenType) which has some appropriate text
     * obtained by DetailAST.getText() method among current (aParentAST) node
     * children.
     * @param aParentAST The current parent node.
     * @param aTokenType Allowable type of token, which you want to search.
     * @param aTokenText Specific text that matches the desired token.
     * @return The DetailAST of desired this token if it was found or null
     *         otherwise.
     */
    public DetailAST getChildTokenAST(DetailAST aParentAST, int aTokenType,
            String aTokenText)
    {

        final DetailAST asts[] = getChildNodes(aParentAST);

        for (int i=asts.length-1;i>=0;i--) {
            DetailAST currentNode = asts[i];            
  System.out.println("currentNode: col:"+currentNode.getColumnNo()+" line"+currentNode.getLineNo()+" text:" +currentNode.getText());
            
              if (currentNode.getType() == aTokenType
                      && currentNode.getText().equals(aTokenText))
              {
                  return currentNode;
              }   
  
             if (currentNode.getType() != TokenTypes.PARAMETER_DEF
                    && currentNode.getType() != TokenTypes.LITERAL_TRY
                    && currentNode.getNumberOfChildren() > 0)
            {
               
                final DetailAST astResult = (getChildTokenAST(currentNode,
                        aTokenType, aTokenText));
                if (astResult != null) {
                    return astResult;
                }                
            }
        }
        return null;
    }

    /**
     * Gets all the children one level below on the current top node.
     * @param aNode Current parent node.
     * @return New DetailAST[] array of childs one level below on the current
     *         parent node (aNode).
     */
    public DetailAST[] getChildNodes(DetailAST aNode)
    {
        final DetailAST[] result = new DetailAST[aNode.getChildCount()];

        DetailAST currNode = aNode.getFirstChild();

        for (int i = 0; i < aNode.getNumberOfChildren(); i++) {
            result[i] = currNode;
            currNode = currNode.getNextSibling();
        }

        return result;
    }

}