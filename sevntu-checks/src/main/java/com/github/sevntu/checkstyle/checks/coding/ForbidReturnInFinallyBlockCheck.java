////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2012 Oliver Burn
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
package com.github.sevntu.checkstyle.checks.coding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * The finally block is always executed unless there is abnormal program termination, either
 * resulting from a JVM crash or from a call to System.exit(0). On top of that, any value returned
 * from within the finnally block will override the value returned prior to execution of the finally
 * block. This check reports if the finally block contains a return statement.
 * </p>
 * 
 * @author <a href="mailto:andrew.uljanenko@gmail.com">Andrew Uljanenko</a>
 */

public class ForbidReturnInFinallyBlockCheck extends Check
{

    public static final String MSG_KEY = "forbid.return.in.finally.block";

    @Override
    public final int[] getDefaultTokens()
    {
        return new int[] { TokenTypes.LITERAL_FINALLY };
    }

    @Override
    public void visitToken(DetailAST aFinallyNode)
    {
        final DetailAST firstSlistNode = aFinallyNode.findFirstToken(TokenTypes.SLIST);

        final List<DetailAST> listOfReturnNodes = getReturnNodes(firstSlistNode);

        for (DetailAST returnNode : listOfReturnNodes) {
            if (!isReturnInMethodDefinition(returnNode)) {
                log(aFinallyNode.getLineNo(), MSG_KEY);
            }
        }
    }

    private List<DetailAST> getReturnNodes(DetailAST aNode)
    {
        final List<DetailAST> result = new ArrayList<DetailAST>();
        DetailAST child = aNode.getFirstChild();
        while (child != null) {
            if (child.getType() == TokenTypes.LITERAL_RETURN) {
                return Collections.singletonList(child);
            }
            result.addAll(getReturnNodes(child));
            child = child.getNextSibling();
        }
        return result;
    }

    private boolean isReturnInMethodDefinition(DetailAST aReturnNode)
    {
        Boolean result = false;
        DetailAST node = aReturnNode;
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
