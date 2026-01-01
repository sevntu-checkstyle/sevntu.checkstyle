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
import com.puppycrawl.tools.checkstyle.utils.CheckUtil;

/**
 * <p>
 * Checks that each variable declaration is in its own statement and on its own line.
 * </p>
 * <p>
 * Rationale: <a href= "http://java.sun.com/docs/codeconv/html/CodeConventions.doc5.html#2991">
 * the SUN Code conventions chapter 6.1</a> recommends that declarations should be one per line.
 * </p>
 * <p>
 * An example of how to configure the check is:
 * </p>
 *
 * <pre>
 * &lt;module name="MultipleVariableDeclarations"/&gt;
 * </pre>
 *
 * @author o_sukhodolsky
 * @since 1.5.3
 */

public class MultipleVariableDeclarationsExtendedCheck extends AbstractCheck {

    /**
     * Warning message key.
     */
    public static final String MSG_VAR_DECLARATIONS_COMMA = "multiple.variable.declarations.comma";

    /**
     * Warning message key.
     */
    public static final String MSG_VAR_DECLARATIONS = "multiple.variable.declarations";

    /** Check declaration in cycles. */
    private boolean ignoreCycles;

    /** Check declaration in methods. */
    private boolean ignoreMethods;

    /**
     * Enable|Disable declaration checking in cycles.
     *
     * @param value
     *            check declaration in Methods
     */
    public void setIgnoreCycles(final boolean value) {
        ignoreCycles = value;
    }

    /**
     * Enable|Disable declaration checking in Methods. *
     *
     * @param value
     *            check declaration in Methods
     */
    public void setIgnoreMethods(final boolean value) {
        ignoreMethods = value;
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.VARIABLE_DEF,
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

    /**
     * Searches for wrong declarations and checks the their type.
     *
     * @param ast
     *            uses to get the parent or previous sibling token.
     */
    public void work(DetailAST ast) {
        DetailAST nextNode = ast.getNextSibling();

        if (nextNode != null) {
            // -@cs[MoveVariableInsideIf] assignment value is modified later so
            // it can't be moved
            final boolean isCommaSeparated = nextNode.getType() == TokenTypes.COMMA;

            if (nextNode.getType() == TokenTypes.COMMA
                    || nextNode.getType() == TokenTypes.SEMI) {
                nextNode = nextNode.getNextSibling();
            }

            if (nextNode != null
                    && nextNode.getType() == TokenTypes.VARIABLE_DEF) {
                final DetailAST firstNode = CheckUtil.getFirstNode(ast);
                if (isCommaSeparated) {
                    log(firstNode, MSG_VAR_DECLARATIONS_COMMA);
                }
                else {
                    final DetailAST lastNode = getLastNode(ast);
                    final DetailAST firstNextNode = CheckUtil.getFirstNode(nextNode);

                    if (firstNextNode.getLineNo() == lastNode.getLineNo()) {
                        log(firstNode, MSG_VAR_DECLARATIONS);
                    }
                }
            }
        }
    }

    @Override
    public void visitToken(DetailAST ast) {
        final DetailAST token = ast;
        final boolean inFor = ast.getParent().getType() == TokenTypes.FOR_INIT;
        final boolean inClass = ast.getParent().getParent().getType() == TokenTypes.CLASS_DEF;

        if (inClass) {
            work(token);
        }
        else if (!ignoreCycles && inFor) {
            work(token);
        }

        else if (!ignoreMethods && !inFor) {
            work(token);
        }
    }

    /**
     * Finds sub-node for given node maximum (line, column) pair.
     *
     * @param node
     *            the root of tree for search.
     * @return sub-node with maximum (line, column) pair.
     */
    private static DetailAST getLastNode(final DetailAST node) {
        DetailAST currentNode = node;
        DetailAST child = node.getFirstChild();
        while (child != null) {
            final DetailAST newNode = getLastNode(child);
            if (newNode.getLineNo() > currentNode.getLineNo()
                    && newNode.getColumnNo() > currentNode.getColumnNo()) {
                currentNode = newNode;
            }
            child = child.getNextSibling();
        }

        return currentNode;
    }

}
