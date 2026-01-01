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

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * This Check highlights variable definition statements where <a href=
 * "http://docs.oracle.com/javase/7/docs/technotes/guides/language/type-inference-generic-instance-creation.html">
 * diamond operator</a> could be used.<br>
 * <b>Rationale</b>: using diamond operator (introduced in Java 1.7) leads to shorter code<br>
 * and better code readability. It is suggested by Oracle that the diamond primarily using<br>
 * for variable declarations.<br><br>
 * E.g. of statements:
 *
 * <p><b>Without diamond operator:</b><br><code>
 * Map&lt;String, Map&lt;String, Integer&gt;&gt; someMap =
 *     new HashMap&lt;String, Map&lt;String, Integer&gt;&gt;();</code><br>
 * <b>With diamond operator:</b><br>
 * <code>
 * Map&lt;String, Map&lt;String, Integer&gt;&gt; someMap = new HashMap&lt;&gt;();
 * </code>
 * </p>
 *
 * @author <a href="mailto:nesterenko-aleksey@list.ru">Aleksey Nesterenko</a>
 * @since 1.12.0
 */
public class DiamondOperatorForVariableDefinitionCheck extends AbstractCheck {

    /** A key is pointing to the warning message text in "messages.properties" file. */
    public static final String MSG_KEY = "diamond.operator.for.variable.definition";

    @Override
    public int[] getDefaultTokens() {
        return new int[] {TokenTypes.VARIABLE_DEF};
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
    public void visitToken(DetailAST variableDefNode) {
        final DetailAST assignNode = variableDefNode.findFirstToken(TokenTypes.ASSIGN);

        if (assignNode != null) {
            final DetailAST newNode = assignNode.getFirstChild().getFirstChild();

            // we check only creation by NEW
            if (newNode.getType() == TokenTypes.LITERAL_NEW) {
                final DetailAST variableDefNodeType =
                        variableDefNode.findFirstToken(TokenTypes.TYPE);
                final DetailAST varDefArguments = getFirstTypeArgumentsToken(variableDefNodeType);

                // generics has to be on left side
                if (varDefArguments != null
                        && newNode.getLastChild().getType() != TokenTypes.OBJBLOCK
                        // arrays can not be generics
                        && newNode.findFirstToken(TokenTypes.ARRAY_DECLARATOR) == null) {
                    final DetailAST typeArgs = getFirstTypeArgumentsToken(newNode);

                    if (typeArgs != null && isTreeEqual(varDefArguments, typeArgs)) {
                        log(typeArgs, MSG_KEY);
                    }
                }
            }
        }
    }

    /**
     * Get first occurrence of TYPE_ARGUMENTS if exists.
     *
     * @param rootToken the token to start search from.
     * @return TYPE_ARGUMENTS token if found.
     */
    private static DetailAST getFirstTypeArgumentsToken(DetailAST rootToken) {
        DetailAST resultNode = rootToken.getFirstChild();

        if (resultNode != null) {
            if (resultNode.getType() == TokenTypes.DOT) {
                resultNode = resultNode.getFirstChild().getNextSibling();
            }
            final DetailAST childNode = getFirstTypeArgumentsToken(resultNode);

            if (childNode == null) {
                resultNode = resultNode.getNextSibling();
            }
        }

        return resultNode;
    }

    /**
     * Checks if the 2 given trees have the same children, type, and text.
     *
     * @param left One of the trees to compare.
     * @param right The other tree to compare.
     * @return {@code true} if the trees are equal.
     */
    private static boolean isTreeEqual(DetailAST left, DetailAST right) {
        boolean result;

        if (isAstEqual(left, right)) {
            result = true;

            DetailAST leftChild = left.getFirstChild();
            DetailAST rightChild = right.getFirstChild();

            while (leftChild != rightChild) {
                if (!isTreeEqual(leftChild, rightChild)) {
                    result = false;
                    break;
                }

                leftChild = leftChild.getNextSibling();
                rightChild = rightChild.getNextSibling();
            }
        }
        else {
            result = false;
        }

        return result;
    }

    /**
     * Checks if the 2 given ASTs have the same type and text.
     *
     * @param left One of the ASTs to compare.
     * @param right The other AST to compare.
     * @return {@code true} if the ASTs are equal.
     */
    private static boolean isAstEqual(DetailAST left, DetailAST right) {
        return left.getType() == right.getType() && left.getText().equals(right.getText());
    }

}
