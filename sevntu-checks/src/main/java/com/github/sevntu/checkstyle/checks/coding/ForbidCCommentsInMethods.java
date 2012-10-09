////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2012  Oliver Burn
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

import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * This check forbid to use C style comments into the method body. If you use
 * class declaration into the method body you will get an error.
 * </p>
 * @author <a href="mailto:IliaDubinin91@gmail.com">Ilia Dubinin</a>
 */
public class ForbidCCommentsInMethods extends Check
{
    /**
     * Set contains C style comments from current file
     */
    private Set<Integer> mCComments;

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] {TokenTypes.METHOD_DEF };
    }

    @Override
    public void beginTree(DetailAST aRootAST)
    {
        mCComments = getFileContents().getCComments().keySet();
    }

    @Override
    public void visitToken(DetailAST aMethodNode)
    {
        if (!mCComments.isEmpty()) {
            final DetailAST borders =
                    aMethodNode.findFirstToken(TokenTypes.SLIST);
            //Could be null when aMethodNode doesn't have body 
            //(into interface for example)
            if (borders != null)    
            {
                final int methodBodyBegin = borders.getLineNo();
                final int methodBodyEnd = borders.getLastChild().getLineNo();
                for (final int commentLineNo : mCComments) {
                    if (commentLineNo > methodBodyBegin
                            && commentLineNo < methodBodyEnd)
                    {
                        log(commentLineNo,
                                "forbid.c.comments.in.the.method.body");
                    }
                }
            }
        }
    }
}
