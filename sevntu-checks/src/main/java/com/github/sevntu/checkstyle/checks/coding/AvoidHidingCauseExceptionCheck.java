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

package com.github.sevntu.checkstyle.checks.coding;

import java.util.LinkedList;
import java.util.List;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>This check prevents new exception throwing inside try/catch
 * blocks without providing current exception cause.
 * New exception should propagate up to a higher-level handler with exact
 * cause to provide a full stack trace for the problem.</p>
 * <p>
 * Rationale: When handling exceptions using try/catch blocks junior developers
 * may lose the original/cause exception object and information associated
 * with it.
 * </p>
 * Examples:
 * <br> <br>
 * 1. Cause exception will be lost because current catch block
 * contains another exception throwing.
 *      <pre>
 *       public void foo() {
 *          RuntimeException r;
 *         catch (java.lang.Exception e) {
 *           //your code
 *           throw r;
 *         }
 *       }</pre>
 * 2. Cause exception will be lost because current catch block
 * doesn`t contains another exception throwing.
 * <pre>
 *      catch (IllegalStateException e) {
 *        //your code
 *        throw new RuntimeException();
 *      }
 *      catch (IllegalStateException e) {
 *        //your code
 *        throw new RuntimeException("Runtime Exception!");
 *      }
 * </pre>
 *
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil
 *         Yaroslavtsev</a>
 * @author <a href="mailto:IliaDubinin91@gmail.com">Ilja Dubinin</a>
 * @since 1.8.0
 */
public class AvoidHidingCauseExceptionCheck extends AbstractCheck {

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY = "avoid.hiding.cause.exception";

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.LITERAL_CATCH,
        };
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
    public void visitToken(DetailAST detailAST) {
        final String originExcName = detailAST
                .findFirstToken(TokenTypes.PARAMETER_DEF).getLastChild()
                .getText();

        final List<DetailAST> throwList = makeThrowList(detailAST);

        final List<String> wrapExcNames = new LinkedList<>();
        wrapExcNames.add(originExcName);
        wrapExcNames.addAll(makeExceptionsList(detailAST, detailAST,
                            originExcName));

        for (DetailAST throwAST : throwList) {
            final List<DetailAST> throwParamNamesList = new LinkedList<>();
            buildThrowParamNamesList(throwAST, throwParamNamesList);
            if (!isContainsCaughtExc(throwParamNamesList, wrapExcNames)) {
                log(throwAST, MSG_KEY, originExcName);
            }
        }
    }

    /**
     * Returns true when aThrowParamNamesList contains caught exception.
     *
     * @param throwParamNamesList List of throw parameter names.
     * @param wrapExcNames List of caught exception names.
     * @return true when aThrowParamNamesList contains caught exception
     */
    private static boolean isContainsCaughtExc(List<DetailAST> throwParamNamesList,
                                    List<String> wrapExcNames) {
        boolean result = false;
        for (DetailAST currentNode : throwParamNamesList) {
            if (currentNode.getParent().getType() != TokenTypes.DOT
                    && wrapExcNames.contains(currentNode.getText())) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Returns a List of<code>DetailAST</code> that contains the names of
     * parameters  for current "throw" keyword.
     *
     * @param startNode The start node for exception name searching.
     * @param paramNamesAST The list, that will be contain names of the
     *     parameters
     * @return A null-safe list of tokens (<code>DetailAST</code>) contains the
     *     thrown exception name if it was found or null otherwise.
     */
    private List<DetailAST> buildThrowParamNamesList(DetailAST startNode,
                            List<DetailAST> paramNamesAST) {
        for (DetailAST currentNode : getChildNodes(startNode)) {
            if (currentNode.getType() == TokenTypes.IDENT) {
                paramNamesAST.add(currentNode);
            }

            if (currentNode.getType() != TokenTypes.PARAMETER_DEF
                    && currentNode.getType() != TokenTypes.LITERAL_TRY
                    && currentNode.getFirstChild() != null) {
                buildThrowParamNamesList(currentNode, paramNamesAST);
            }
        }
        return paramNamesAST;
    }

    /**
     * Recursive method which searches for the <code>LITERAL_THROW</code>
     * DetailASTs all levels below on the current <code>aParentAST</code> node
     * without entering into nested try/catch blocks.
     *
     * @param parentAST A start node for "throw" keyword <code>DetailASTs
     *     </code> searching.
     * @return null-safe list of <code>LITERAL_THROW</code> literals
     */
    private List<DetailAST> makeThrowList(DetailAST parentAST) {
        final List<DetailAST> throwList = new LinkedList<>();
        for (DetailAST currentNode : getChildNodes(parentAST)) {
            if (currentNode.getType() == TokenTypes.LITERAL_THROW) {
                throwList.add(currentNode);
            }

            if (currentNode.getType() != TokenTypes.PARAMETER_DEF
                    && currentNode.getType() != TokenTypes.LITERAL_THROW
                    && currentNode.getType() != TokenTypes.LITERAL_TRY
                    && currentNode.getFirstChild() != null) {
                throwList.addAll(makeThrowList(currentNode));
            }
        }
        return throwList;
    }

    /**
     * Searches for all exceptions that wraps the original exception
     * object (only in current "catch" block).
     *
     * @param currentCatchAST A LITERAL_CATCH node of the
     *     current "catch" block.
     * @param parentAST Current parent node to start search.
     * @param currentExcName The name of exception handled by
     *     current "catch" block.
     * @return List contains exceptions that wraps the original
     *     exception object.
     */
    private List<String> makeExceptionsList(DetailAST currentCatchAST,
            DetailAST parentAST, String currentExcName) {
        final List<String> wrapExcNames = new LinkedList<>();

        for (DetailAST currentNode : getChildNodes(parentAST)) {
            if (currentNode.getType() == TokenTypes.IDENT
                    && currentNode.getText().equals(currentExcName)
                    && currentNode.getParent().getType() != TokenTypes.DOT) {
                DetailAST temp = currentNode;

                while (!temp.equals(currentCatchAST)
                        && temp.getType() != TokenTypes.ASSIGN) {
                    temp = temp.getParent();
                }

                if (temp.getType() == TokenTypes.ASSIGN) {
                    final DetailAST convertedExc;
                    if (temp.getParent().getType() == TokenTypes.VARIABLE_DEF) {
                        convertedExc = temp.getParent().findFirstToken(TokenTypes.IDENT);
                    }
                    else {
                        convertedExc = temp.findFirstToken(TokenTypes.IDENT);
                    }
                    if (convertedExc != null) {
                        wrapExcNames.add(convertedExc.getText());
                    }
                }
            }

            if (currentNode.getType() != TokenTypes.PARAMETER_DEF
                    && currentNode.getFirstChild() != null) {
                wrapExcNames.addAll(makeExceptionsList(currentCatchAST,
                        currentNode, currentExcName));
            }
        }
        return wrapExcNames;
    }

    /**
     * Gets all the children one level below on the current parent node.
     *
     * @param node Current parent node.
     * @return List of children one level below on the current
     *         parent node (aNode).
     */
    private static List<DetailAST> getChildNodes(DetailAST node) {
        final List<DetailAST> result = new LinkedList<>();

        DetailAST currNode = node.getFirstChild();

        while (currNode != null) {
            result.add(currNode);
            currNode = currNode.getNextSibling();
        }

        return result;
    }

}
