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
import com.puppycrawl.tools.checkstyle.utils.ScopeUtil;

/**
 * <p>
 * Checks if a variable is only used inside if statements and asks for its
 * declaration to be moved there too.
 * </p>
 * <p>
 * Rationale: Code inside if/else statements are only executed when those specific block
 * conditions evaluate to true. Moving variables inside these blocks prevents the code from being
 * executed when the value of the variable is not even being used. It also helps limit the scope
 * of the variables from being too broad to confuse new readers. Suppressing variables with false
 * violations because of the check's limitations (stated below) also help clearly state the
 * purpose of the variable as a temporary storage for a current/future changing value.
 * </p>
 * <p>
 * An example of how to configure the check is:
 * </p>
 * <pre>
 * &lt;module name="MoveVariableInsideIfCheck"/&gt;
 * </pre>
 * <p>
 * which will produce the following violation:
 * </p>
 * <pre>
 * String variable = input.substring(1); // violation - variable is only used inside if block
 *
 * if (condition) {
 *     return method(variable);
 * }
 *
 * return "";
 * </pre>
 * <p>
 * The code can be written as the following to avoid a violation:
 * </p>
 * <pre>
 * if (condition) {
 *     String variable = input.substring(1);
 *     return method(variable);
 * }
 *
 * return "";
 * </pre>
 * <p>
 * No violations will be produced if a variable is used in same scope as declaration, condition of
 * block, or if used in multiple blocks.
 * </p>
 * <pre>
 * String variable = input.substring(1);
 *
 * if (condition &amp;&amp; variable.charAt(0) == 'T') {
 *     return method(variable);
 * }
 * else {
 *     return method2(variable);
 * }
 *
 * return "";
 * </pre>
 * <p>
 * Limitations: The check can not determine if the value of variable being stored is changed after
 * the declaration. Variables like this can't be moved, or may be too complex to move, and thus
 * should be suppressed.
 * </p>
 * <p>
 * <b>Case #1:</b>
 * </p>
 * <pre>
 * final String variable = list.remove(0); // false positive - list is modified with storing value
 * final String next = list.get(0); // expecting above list modification
 *
 * if (next.equals(input)) {
 *     list.add(variable);
 * }
 * </pre>
 * <p>
 * <b>Case #2:</b>
 * </p>
 * <pre>
 * final String variable = field.get(0); // false positive - field is modified later, before block
 *
 * modifyField(); // field is modified inside this method
 *
 * if (condition) {
 *     field.add(variable);
 * }
 * </pre>
 *
 * @author Richard Veach
 * @since 1.24.0
 */
public class MoveVariableInsideIfCheck extends AbstractCheck {

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY = "move.variable.inside";

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
    public void visitToken(DetailAST ast) {
        if (ScopeUtil.isLocalVariableDef(ast)) {
            validateLocalVariable(ast);
        }
    }

    /**
     * Examines the local variable for violations to be moved inside an nest if
     * statement.
     *
     * @param ast The local variable to examine.
     */
    private void validateLocalVariable(DetailAST ast) {
        final Holder holder = new Holder(ast);

        for (DetailAST child = ast.getNextSibling(); !holder.exit && child != null;
                child = child.getNextSibling()) {
            if (child.getType() == TokenTypes.LITERAL_IF) {
                validateIf(holder, child);
            }
            else {
                validateOther(holder, child);
            }
        }

        if (holder.blockNode != null) {
            log(ast, MSG_KEY, holder.variableName, holder.blockNode.getLineNo());
        }
    }

    /**
     * Examines an if statement to see how many times the specified variable
     * identifier was used inside it.
     *
     * @param holder The object holder with the specified variable to check and
     *        its current state.
     * @param ifNodeGiven The current if node to examine.
     */
    private static void validateIf(Holder holder, DetailAST ifNodeGiven) {
        DetailAST ifNode = ifNodeGiven;

        // -@cs[SingleBreakOrContinue] Too complex to break apart
        while (true) {
            // check condition
            final DetailAST rparen = ifNode.findFirstToken(TokenTypes.RPAREN);
            final boolean usedInCondition = holder.hasIdent(
                    ifNode.findFirstToken(TokenTypes.LPAREN), rparen);

            if (usedInCondition) {
                holder.setExit();
                break;
            }

            final DetailAST elseNode = ifNode.getLastChild();

            // check body of if
            final DetailAST body = rparen.getNextSibling();
            final DetailAST bodyEnd;

            if (body.getType() == TokenTypes.SLIST) {
                bodyEnd = body.getLastChild();
            }
            else {
                bodyEnd = elseNode;
            }

            final boolean used = holder.hasIdent(body, bodyEnd);

            if (used) {
                holder.setBlockNode(ifNode);

                if (holder.exit) {
                    break;
                }
            }

            if (elseNode.getType() != TokenTypes.LITERAL_ELSE) {
                break;
            }

            ifNode = elseNode.getFirstChild();

            if (ifNode.getType() != TokenTypes.LITERAL_IF) {
                // check body of else

                validateElseOfIf(holder, ifNode, elseNode);
                break;
            }
        }
    }

    /**
     * Examines the else of an if statement to see how many times the specified
     * variable identifier was used inside it.
     *
     * @param holder The object holder with the specified variable to check and
     *        its current state.
     * @param ifNode The if node of the specified else.
     * @param elseNode The current else node to examine.
     */
    private static void validateElseOfIf(Holder holder, DetailAST ifNode, DetailAST elseNode) {
        final boolean used;

        if (ifNode.getType() == TokenTypes.SLIST) {
            used = holder.hasIdent(ifNode.getFirstChild(), ifNode.getLastChild());
        }
        else {
            used = holder.hasIdent(ifNode, elseNode.getLastChild());
        }

        if (used) {
            holder.setBlockNode(elseNode);
        }
    }

    /**
     * Examines other nodes to see how many times a variable was used inside it.
     * If the variable is used, no violations are reported for it.
     *
     * @param holder The object holder with the specified variable to check and
     *        its current state.
     * @param child The current node to examine.
     */
    private static void validateOther(Holder holder, DetailAST child) {
        final boolean used = holder.hasIdent(child, child.getNextSibling());

        if (used) {
            holder.setExit();
        }
    }

    /**
     * The holder of information for the specified variable.
     *
     * @author Richard Veach
     */
    private static class Holder {

        /** The name of the variable being examined. */
        private final String variableName;
        /** Switch to trigger ending examining more nodes. */
        private boolean exit;
        /** The node to report violations on. */
        private DetailAST blockNode;

        /**
         * Default constructor for the class.
         *
         * @param ast The variable the holder is for.
         */
        /* package */ Holder(DetailAST ast) {
            variableName = ast.findFirstToken(TokenTypes.IDENT).getText();
        }

        /**
         * Sets the specified node that is to be reported for the violation for
         * the block. If there is already a node being reported, then no nodes
         * are reported.
         *
         * @param blockNode The given block node to report for.
         */
        public void setBlockNode(DetailAST blockNode) {
            if (this.blockNode == null) {
                this.blockNode = blockNode;
            }
            else {
                setExit();
            }
        }

        /** Sets the state to exit examining further nodes. */
        public void setExit() {
            blockNode = null;
            exit = true;
        }

        /**
         * Checks if any of the nodes between the given start and end are an
         * identifier with the name of the variable.
         *
         * @param start The node to start examining from.
         * @param end The last node to stop examining once reached. If null,
         *        then the last node is when we leave the start node.
         * @return true if the identifier has been found, otherwise false.
         */
        public boolean hasIdent(DetailAST start, DetailAST end) {
            boolean found = false;
            DetailAST curNode = start;

            // -@cs[SingleBreakOrContinue] Too complex to break apart
            while (curNode != null) {
                if (curNode.getType() == TokenTypes.IDENT
                        && variableName.equals(curNode.getText())) {
                    found = true;
                    break;
                }

                if (curNode == end) {
                    break;
                }

                DetailAST toVisit = curNode.getFirstChild();

                while (toVisit == null) {
                    toVisit = curNode.getNextSibling();

                    if (toVisit == null) {
                        if (end == null) {
                            break;
                        }

                        curNode = curNode.getParent();
                    }
                }

                curNode = toVisit;
            }

            return found;
        }

    }

}
