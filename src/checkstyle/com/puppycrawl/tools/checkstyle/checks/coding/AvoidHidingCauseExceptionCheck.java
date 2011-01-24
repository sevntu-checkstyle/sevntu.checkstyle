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
 * 
 * <pre>
 * Examples:
 * </pre>
 * <ol>
 * <li>Cause exception was lost while exception throwing.</li>
 * <code>
 *      <pre>
 *       public void foo() {
 *          RuntimeException r;
 *         catch (java.lang.Exception e) {
 *           //your code
 *           throw r;
 *         }
 *       }</pre> </code>
 * <li>Cause exception was lost while exception re-throwing.</li>
 * <code> <pre>
 *      catch (IllegalStateException e) {
 *        //your code
 *        throw new RuntimeException();
 *      }
 * 
 *      catch (IllegalStateException e) {
 *        //your code
 *        throw new RuntimeException("Runtime Ecxeption!");
 *      } </pre> </code>
 * </ol>
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil
 *         Yaroslavtsev</a>
 */
public class AvoidHidingCauseExceptionCheck extends Check {

    /** List containing all throw keyword DetailASTs for current catch block.*/
    LinkedList<DetailAST> aThrowList = new LinkedList<DetailAST>();

    /** A list containing the names of all the exceptions that override the original.*/
    LinkedList<String> aConvertedExceptionsNames = new LinkedList<String>();

    /** Creates new instance of the check. */
    public AvoidHidingCauseExceptionCheck() {
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] { TokenTypes.LITERAL_CATCH };
    }

    @Override
    public void visitToken(DetailAST aDetailAST) {

        final String originExcName = aDetailAST
                .findFirstToken(TokenTypes.PARAMETER_DEF).getLastChild()
                .getText();

        if (!aThrowList.isEmpty())
            aThrowList.clear();
        if (!aConvertedExceptionsNames.isEmpty())
            aConvertedExceptionsNames.clear();
        aConvertedExceptionsNames.add(originExcName); // изначально сверке с бросаемым именем Exception-а будет подлежать только имя Exception-а из parameters def 

        makeThrowList(aDetailAST); // составить список всех внутренних ключевых слов throw для текущего catch
        makeExceptionsList(aDetailAST, aDetailAST, originExcName); // получить список всех имен Exception-ов, которые переопределяют оригинальный Exception в текущем блоке catch

        for (DetailAST throwAST : aThrowList) { // для каждого throw

            DetailAST rethrowExcNameAST = null; // cleanup

            if (throwAST.getType() == TokenTypes.LITERAL_THROW) {

                // Lets retrieve a DetailAST which contains the name
                // of rethrown exception or null if rethrow does not
                // exist in current "catch" block
                rethrowExcNameAST = findThrowExcName(throwAST); // получаем имя Exception-a, кот. бросается текущим throw

                if (rethrowExcNameAST != null) { // если найдено имя бросаемого Exception-a

                    if (rethrowExcNameAST.getParent().getType() == TokenTypes.DOT) {
                        log(throwAST, "avoid.hiding.cause.exception",
                                originExcName);
                    } else {

                        // если ни одно из имен в списке не подходит - ошибка
                        if (!aConvertedExceptionsNames
                                .contains(rethrowExcNameAST.getText()))
                            log(throwAST, "avoid.hiding.cause.exception",
                                    originExcName);

                    }

                }

                else { // если по любым причинам не найдено имя исключения
                    System.out.println("Не найдено имя исключения в: ");

                }

            }
        }
    }

    /**
     * Returns a DetailAST contains name of throwing Exception for current throw keyword.
     * @param aStartNode The DetailAST for current throw keyword.
     * This is a start node for exception name searching.
     * @return The DetailAST of desired token which contains thrown exception name
     * if it was found or null otherwise.
     */
    public DetailAST findThrowExcName(DetailAST aStartNode)
    {

        final DetailAST asts[] = getChildNodes(aStartNode);

        for (int i = asts.length - 1; i >= 0; i--)
        {
            DetailAST currentNode = asts[i];
            System.out.println("currentNode: col:" + currentNode.getColumnNo()
                    + " line" + currentNode.getLineNo() + " text:"
                    + currentNode.getText());

            if (currentNode.getType() == TokenTypes.IDENT)
            {
                return currentNode;
            }

            if (currentNode.getType() != TokenTypes.PARAMETER_DEF
                    && currentNode.getType() != TokenTypes.LITERAL_TRY
                    && currentNode.getNumberOfChildren() > 0)
            {

                final DetailAST astResult = (findThrowExcName(currentNode));
                if (astResult != null)
                {
                    return astResult;
                }
            }
        }
        return null;
    }

    /**
     * Searches for the LITERAL_THROW without entering into nested try/catch blocks.
     * @param aStartNode DetailAST 
     * @return List contains all "throw" keyword nodes (LITERAL_THROW)
     * for certain parent node (aParentAST) except those that are in nested try/catch blocks.
     */
    public LinkedList<DetailAST> makeThrowList(DetailAST aParentAST) {

        for (DetailAST currentNode : getChildNodes(aParentAST)) {

            System.out.println("Throw(s) searching. currentNode: col:"
                    + currentNode.getColumnNo() + " line"
                    + currentNode.getLineNo() + " text:"
                    + currentNode.getText());

            if (currentNode.getType() == TokenTypes.LITERAL_THROW) {
                aThrowList.add(currentNode);
            }

            if (currentNode.getType() == TokenTypes.LITERAL_CATCH) {
                return aThrowList;
            }

            if (currentNode.getType() != TokenTypes.PARAMETER_DEF
                    && currentNode.getType() != TokenTypes.LITERAL_THROW
                    && currentNode.getType() != TokenTypes.LITERAL_TRY
                    && currentNode.getNumberOfChildren() > 0) {
                makeThrowList(currentNode);
            }

        }
        return aThrowList;
    }

    /**
     * Searches for all exceptions that override the original exception object of the current "catch" block.
     * @param aCurrentCatchAST A LITERAL_CATCH node of the current "catch" block.
     * @param aParentAST Current parent node to start search.
     * @param aCurrentExcName The name of Exception handled by current "catch" block.
     * @return Array contains exceptions that override the original exception object of the current "catch" block.
     */
    public LinkedList<String> makeExceptionsList(DetailAST aCurrentCatchAST,
            DetailAST aParentAST, String aCurrentExcName) {

        for (DetailAST currentNode : getChildNodes(aParentAST)) {

            System.out.println("Exceptions(s) searching. currentNode: col:"
                    + currentNode.getColumnNo() + " line"
                    + currentNode.getLineNo() + " text:"
                    + currentNode.getText());

            if (currentNode.getType() == TokenTypes.IDENT
                    && currentNode.getText().equals(aCurrentExcName)
                    && currentNode.getParent() != null
                    && currentNode.getParent().getType() != TokenTypes.DOT) { // для всех найденных имен Exception - ов в текущем блоке catch

                DetailAST temp = currentNode;
                // пока мы не дошли до самого начала блока catch или не встретили AST знак равенства, идем вверх по дереву к следующему предку
                while (!temp.equals(aCurrentCatchAST)
                        && temp.getType() != TokenTypes.ASSIGN) {
                    temp = temp.getParent();
                }

                // если был найден знак равенства (присвоения), относящийся к нашему изначальному имени исключения
                // и у него в ПОЛИЗ - потомках присутствует IDENT, не равный изначальному имени

                if (temp.getType() == TokenTypes.ASSIGN) {
                    DetailAST convertedExc = null;

                    if (temp.getParent().getType() == TokenTypes.VARIABLE_DEF) {

                        convertedExc = temp.getParent().findFirstToken(
                                TokenTypes.IDENT);

                    } else {

                        convertedExc = temp.findFirstToken(TokenTypes.IDENT);

                    }

                    if (convertedExc != null
                            && !convertedExc.getText().equals(aCurrentExcName)) {
                        if (!aConvertedExceptionsNames.contains(convertedExc
                                .getText())) {
                            aConvertedExceptionsNames.add(convertedExc
                                    .getText());
                        }
                    }

                }

            }

            if (currentNode.getType() != TokenTypes.PARAMETER_DEF
                    && currentNode.getType() != TokenTypes.LITERAL_TRY
                    && currentNode.getNumberOfChildren() > 0) {
                makeExceptionsList(aCurrentCatchAST, currentNode,
                        aCurrentExcName);
            }

        }
        return aConvertedExceptionsNames;
    }

    /**
     * Gets all the children one level below on the current top node. 
     * @param aNode Current parent node.
     * @return New DetailAST[] array of childs one level below on the current
     *         parent node (aNode).
     */
    public DetailAST[] getChildNodes(DetailAST aNode) {
        final DetailAST[] result = new DetailAST[aNode.getChildCount()];

        DetailAST currNode = aNode.getFirstChild();

        for (int i = 0; i < aNode.getNumberOfChildren(); i++) {
            result[i] = currNode;
            currNode = currNode.getNextSibling();
        }

        return result;
    }

}
