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
package com.puppycrawl.tools.checkstyle.checks.design;

import java.util.ArrayList;
import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * Check nested (internal) classes to be declared at the bottom of the class
 * after all methods (fields) declaration.
 * </p>
 *
 * @author <a href="mailto:ryly@mail.ru">Ruslan Dyachenko</a>
 */
public class InnerClassCheck extends Check
{
    @Override
    public int[] getDefaultTokens()
    {
        return new int[]{TokenTypes.CLASS_DEF, TokenTypes
                .VARIABLE_DEF, TokenTypes.METHOD_DEF, TokenTypes
                .SLIST, TokenTypes.RCURLY, TokenTypes
                .LCURLY, };
    }

    /** Meet a root class. */
    private boolean mRootClass = true;
    /** Flag - if you meet internal class at first. */
    private ArrayList<Boolean> mIsInnerClass = new ArrayList<Boolean>();
    /** Index in Stack. */
    private int mStackIndex;

    @Override
    public void visitToken(DetailAST aAST)
    {
        /** First root class */
        if (mRootClass) {
            mRootClass = false;
        }
        else if (aAST.getType() == TokenTypes.SLIST
                || aAST.getType() == TokenTypes.LCURLY)
        {
            mIsInnerClass.add(false);
            mStackIndex++;
        }
        else if (aAST.getType() == TokenTypes.RCURLY) {
            mIsInnerClass.remove(--mStackIndex);
        }
        else if ((aAST.getType() == TokenTypes.VARIABLE_DEF
                || aAST.getType() == TokenTypes.METHOD_DEF)
                && !mIsInnerClass.get(mStackIndex - 1))
        {
            ;// no code
        }
        else if (aAST.getType() == (TokenTypes.CLASS_DEF)) {
            mIsInnerClass.set(mStackIndex - 1, true);
        }
        else {
            log(aAST.getLineNo(), aAST.getColumnNo(),
                "arrangement.members.before.inner");
        }
    }
}