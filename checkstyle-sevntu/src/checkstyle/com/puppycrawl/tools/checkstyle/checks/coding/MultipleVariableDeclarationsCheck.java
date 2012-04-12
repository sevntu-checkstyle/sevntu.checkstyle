////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2011  Oliver Burn
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

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import com.puppycrawl.tools.checkstyle.checks.CheckUtils;

/**
 * <p>
 * Checks that each variable declaration is in its own statement and on its own
 * line.
 * </p>
 * <p>
 * Rationale: <a href=
 * "http://java.sun.com/docs/codeconv/html/CodeConventions.doc5.html#2991"> the
 * SUN Code conventions chapter 6.1</a> recommends that declarations should be
 * one per line.
 * </p>
 * <p>
 * An example of how to configure the check is:
 * </p>
 * <pre>
 * &lt;module name="MultipleVariableDeclarations"/&gt;
 * </pre> *
 * @author o_sukhodolsky
 */

public class MultipleVariableDeclarationsCheck extends Check
{

    /** check declaration in cycles. */
    private boolean mIgnoreCycles;

    /** check declaration in methods. */
    private boolean mIgnoreMethods;

    /**
     * Enable|Disable declaration checking in cycles.
     * @param aValue check declaration in Methods
     */
    public void setIgnoreCycles(final boolean aValue)
    {
        mIgnoreCycles = aValue;
    }

    /**Enable|Disable declaration checking in Methods. *
     * @param aValue check declaration in Methods
     */
    public void setIgnoreMethods(final boolean aValue)
    {
        mIgnoreMethods = aValue;
    }

    /** Creates new instance of the check. */
    public MultipleVariableDeclarationsCheck()
    {
    }

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] {TokenTypes.VARIABLE_DEF};
    }

    /** Searches for wrong declarations and checks the their type.
    * @param aAST uses to get the parent or previous sibling token.
    */
    public void work(DetailAST aAST)
    {

        DetailAST nextNode = aAST.getNextSibling();
        final boolean isCommaSeparated = ((nextNode != null) && (nextNode
                .getType() == TokenTypes.COMMA));

        if (nextNode == null) {
            // no nextMethods statement - no check
            return;
        }

        if ((nextNode.getType() == TokenTypes.COMMA)
                || (nextNode.getType() == TokenTypes.SEMI))
        {
            nextNode = nextNode.getNextSibling();
        }

        if ((nextNode != null)
                && (nextNode.getType() == TokenTypes.VARIABLE_DEF))
        {
            final DetailAST firstNode = CheckUtils.getFirstNode(aAST);
            if (isCommaSeparated) {
                log(firstNode, "multiple.variable.declarations.comma");
                return;
            }

            final DetailAST lastNode = getLastNode(aAST);
            final DetailAST firstNextNode = CheckUtils.getFirstNode(nextNode);

            if (firstNextNode.getLineNo() == lastNode.getLineNo()) {
                log(firstNode, "multiple.variable.declarations");
            }
        }

    }

    @Override
    public void visitToken(DetailAST aAST)
    {

        final DetailAST token = aAST;
        final boolean inFor = (aAST.getParent().getType()
                == TokenTypes.FOR_INIT);
        final boolean inClass = (aAST.getParent().getParent().getType()
                == TokenTypes.CLASS_DEF);

        if (inClass) {
            work(token);
        }
        else if (!mIgnoreCycles & inFor) {
            work(token);
        }

        else if (!mIgnoreMethods & !inClass & !inFor) {
            work(token);
        }

    }

    /**
     * Finds sub-node for given node maximum (line, column) pair.
     * @param aNode the root of tree for search.
     * @return sub-node with maximum (line, column) pair.
     */
    private static DetailAST getLastNode(final DetailAST aNode)
    {
        DetailAST currentNode = aNode;
        DetailAST child = aNode.getFirstChild();
        while (child != null) {
            final DetailAST newNode = getLastNode(child);
            if ((newNode.getLineNo() > currentNode.getLineNo())
                    || ((newNode.getLineNo()
                         == currentNode.getLineNo()) && (newNode
                            .getColumnNo() > currentNode.getColumnNo())))
            {
                currentNode = newNode;
            }
            child = child.getNextSibling();
        }

        return currentNode;
    }
}