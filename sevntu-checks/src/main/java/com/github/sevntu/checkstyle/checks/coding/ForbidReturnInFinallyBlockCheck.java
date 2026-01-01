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

import java.util.ArrayList;
import java.util.List;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * The finally block is always executed unless there is abnormal program termination, either
 * resulting from a JVM crash or from a call to System.exit(0). On top of that, any value returned
 * from within the finally block will override the value returned prior to execution of the finally
 * block. This check reports if the finally block contains a return statement.
 * </p>
 *
 * @author <a href="mailto:andrew.uljanenko@gmail.com">Andrew Uljanenko</a>
 * @since 1.13.0
 */

public class ForbidReturnInFinallyBlockCheck extends AbstractCheck {

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY = "forbid.return.in.finally.block";

    @Override
    public final int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.LITERAL_FINALLY,
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
    public void visitToken(DetailAST finallyNode) {
        final DetailAST firstSlistNode = finallyNode.findFirstToken(TokenTypes.SLIST);

        final List<DetailAST> listOfReturnNodes = getReturnNodes(firstSlistNode);

        for (DetailAST returnNode : listOfReturnNodes) {
            if (!isReturnInMethodDefinition(returnNode)) {
                log(finallyNode, MSG_KEY);
            }
        }
    }

    /**
     * Retrieves the list of return nodes inside the given node.
     *
     * @param node The token to examine.
     * @return The list of return nodes.
     */
    private List<DetailAST> getReturnNodes(DetailAST node) {
        final List<DetailAST> result = new ArrayList<>();
        DetailAST child = node.getFirstChild();
        while (child != null) {
            if (child.getType() == TokenTypes.LITERAL_RETURN) {
                result.add(child);
                break;
            }
            result.addAll(getReturnNodes(child));
            child = child.getNextSibling();
        }
        return result;
    }

    /**
     * Checks if there is a method definition in the node.
     *
     * @param returnNode The token to examine.
     * @return true if a method definition was found.
     */
    private static boolean isReturnInMethodDefinition(DetailAST returnNode) {
        Boolean result = false;
        DetailAST node = returnNode;
        while (node.getType() != TokenTypes.LITERAL_FINALLY) {
            if (node.getType() == TokenTypes.METHOD_DEF) {
                result = true;
                break;
            }
            node = node.getParent();
        }
        return result;
    }

}
