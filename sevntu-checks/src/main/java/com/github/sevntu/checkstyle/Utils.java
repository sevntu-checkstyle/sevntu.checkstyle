////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2018 the original author or authors.
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

package com.github.sevntu.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.utils.TokenUtils;

/**
 * Simple utility class for all sevntu checks.
 *
 * @author Damian Szczepanik
 */
public final class Utils {

    /** Prevent instances. */
    private Utils() {
    }

    /**
     * Reports passed token as unsupported by throwing {@link IllegalArgumentException} exception.
     * This utility method if used to mark that token passed to
     * {@link AbstractCheck#visitToken(com.puppycrawl.tools.checkstyle.api.DetailAST)} is not
     * supported by this method.
     * @param token
     *        token/type that is not supported
     * @throws IllegalArgumentException
     *         always
     */
    public static void reportInvalidToken(int token) {
        throw new IllegalArgumentException("Found unsupported token: "
                + TokenUtils.getTokenName(token));
    }

    /**
     * Gets the next node of a syntactical tree (child of a current node or sibling of a current
     * node, or sibling of a parent of a current node).
     *
     * @param node
     *            Current node in considering
     * @param subTreeRoot
     *            The root node of the subtree. Can be a top level root node
     * @return Current node after bypassing, if current node reached the root of a subtree
     *     method returns null
     */
    public static DetailAST getNextSubTreeNode(DetailAST node, DetailAST subTreeRoot) {
        DetailAST toVisitAst = node.getFirstChild();
        DetailAST currentNode = node;

        while (toVisitAst == null) {
            toVisitAst = currentNode.getNextSibling();
            if (toVisitAst == null) {
                if (currentNode.getParent().equals(subTreeRoot)) {
                    break;
                }
                currentNode = currentNode.getParent();
            }
        }
        return toVisitAst;
    }

}
