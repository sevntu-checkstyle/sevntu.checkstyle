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
                .VARIABLE_DEF, TokenTypes.METHOD_DEF, };
    }

    /** Meet an internal class. */
    private boolean mFirstInternalClass;
    /** Meet a root class. */
    private boolean mRootClass = true;
    /** Meet an internal class in method. */
    private boolean mMethodClass = true;
    /** Flag - if you meet internal class at first. */
    private ArrayList<Boolean> mIsFirstInternalClass = new ArrayList<Boolean>();
    /** How many variables and methods in internal class. */
    private ArrayList<Integer> mCountVarMethInInternClass =
        new ArrayList<Integer>();
    /** How many internal classes in nodes. */
    private int mCountInternalClass;
    /** How many variables and inner classes in method. */
    private int mCountMethodChildren;

    @Override
    public void visitToken(DetailAST aAST)
    {
        int countVarMeth = 0;
        if (mRootClass) {
            mRootClass = false;
            mIsFirstInternalClass.add(false);
            countVarMeth = aAST.findFirstToken(TokenTypes.OBJBLOCK)
                    .getChildCount(TokenTypes.VARIABLE_DEF)
                    + aAST.findFirstToken(TokenTypes.OBJBLOCK).getChildCount(
                            TokenTypes.METHOD_DEF);
            mCountVarMethInInternClass.add(countVarMeth);
            return;
        }

        if (aAST.getParent().getType() == TokenTypes.SLIST) {
            mCountMethodChildren--;
            if (mCountMethodChildren == 0) {
                mMethodClass = true;
            }
            if ((mMethodClass) && (aAST.getParent()
                    .getChildCount(TokenTypes.CLASS_DEF) > 0))
            {
                mMethodClass = false;
                mIsFirstInternalClass.add(false);
                countVarMeth = aAST.getParent()
                        .getChildCount(TokenTypes.VARIABLE_DEF)
                        + aAST.getParent().getChildCount(
                                TokenTypes.METHOD_DEF);
                mCountMethodChildren = countVarMeth + aAST
                    .getParent().getChildCount(TokenTypes.CLASS_DEF);
                if (aAST.getType() == TokenTypes.VARIABLE_DEF) {
                    countVarMeth--;
                }
                mCountVarMethInInternClass.add(countVarMeth);
                mCountInternalClass++;
                return;
            }

            if ((aAST.getType() == TokenTypes.VARIABLE_DEF)
                    && (!mIsFirstInternalClass.get(mCountInternalClass)))
            {
                return;
            }
            else if (aAST.getType() == (TokenTypes.CLASS_DEF)) {
                mFirstInternalClass = true;
                mIsFirstInternalClass
                    .set(mCountInternalClass, mFirstInternalClass);
                countVarMeth = aAST.findFirstToken(TokenTypes.OBJBLOCK)
                            .getChildCount(TokenTypes.VARIABLE_DEF)
                            + aAST.findFirstToken(
                                    TokenTypes.OBJBLOCK).getChildCount(
                                            TokenTypes.METHOD_DEF);
                mCountVarMethInInternClass.add(countVarMeth);
                mIsFirstInternalClass.add(false);
                mCountInternalClass++;
                return;
            }
        }

        if ((!mIsFirstInternalClass.get(mCountInternalClass)
                && (mCountVarMethInInternClass.get(mCountInternalClass) > 0))
                && (aAST.getType() == (TokenTypes.VARIABLE_DEF)
                        || aAST.getType() == (TokenTypes.METHOD_DEF)))
        {
            mCountVarMethInInternClass.set(mCountInternalClass,
                    mCountVarMethInInternClass.get(mCountInternalClass) - 1);
            if (mCountVarMethInInternClass.get(mCountInternalClass) == 0) {
                mCountVarMethInInternClass.remove(mCountInternalClass);
                mIsFirstInternalClass.remove(mCountInternalClass);
                if (mCountInternalClass != 0) {
                    mCountInternalClass--;
                }
            }
            return;
        }

        if (aAST.getType() == (TokenTypes.CLASS_DEF)) {
            mFirstInternalClass = true;
            mIsFirstInternalClass.set(mCountInternalClass, mFirstInternalClass);
            countVarMeth = aAST.findFirstToken(TokenTypes.OBJBLOCK)
                    .getChildCount(TokenTypes.VARIABLE_DEF)
                    + aAST.findFirstToken(TokenTypes.OBJBLOCK).getChildCount(
                            TokenTypes.METHOD_DEF);
            mCountVarMethInInternClass.add(countVarMeth);
            mIsFirstInternalClass.add(false);
            mCountInternalClass++;
            return;
        }

        if ((aAST.getType() == (TokenTypes.VARIABLE_DEF)
                || aAST.getType() == (TokenTypes.METHOD_DEF)))
        {
            // error
            if (mCountVarMethInInternClass.get(mCountInternalClass) == 1) {
                mCountVarMethInInternClass.remove(mCountInternalClass);
                mIsFirstInternalClass.remove(mCountInternalClass);
                if (mCountInternalClass != 0) {
                    mCountInternalClass--;
                }
            }
            log(aAST.getLineNo(), aAST.getColumnNo(),
                    "arrangement.members.before.inner");
        }
    }
}
