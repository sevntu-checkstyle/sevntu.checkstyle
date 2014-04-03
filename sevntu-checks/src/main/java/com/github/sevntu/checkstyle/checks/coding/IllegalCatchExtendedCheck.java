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
package com.github.sevntu.checkstyle.checks.coding;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.CheckUtils;
import com.puppycrawl.tools.checkstyle.checks.coding.AbstractIllegalCheck;

/**
 * Catching java.lang.Exception, java.lang.Error or java.lang.RuntimeException
 * is almost never acceptable.
 * @author <a href="mailto:simon@redhillconsulting.com.au">Simon Harris</a>
 */
public final class IllegalCatchExtendedCheck extends AbstractIllegalCheck
{
	/**
	 * Warning message key.
	 */
	public static final String MSG_KEY = "illegal.catch";

    /** disable warnings for "catch" blocks containing
     * throwing an exception. */
    private boolean mAllowThrow = true;

    /** disable warnings for "catch" blocks containing
     * rethrowing an exception. */
    private boolean mAllowRethrow = true;

    /**
     * Enable(false) | Disable(true) warnings for "catch" blocks containing
     * throwing an exception.
     * @param aValue Disable warning for throwing
     */
    public void setAllowThrow(final boolean aValue)
    {
        mAllowThrow = aValue;
    }

    /**
     * Enable(false) | Disable(true) warnings for "catch" blocks containing
     * rethrowing an exception.
     * @param aValue Disable warnings for rethrowing
     */
    public void setAllowRethrow(final boolean aValue)
    {
        mAllowRethrow = aValue;
    }

    /** Creates new instance of the check. */
    public IllegalCatchExtendedCheck()
    {
        super(new String[]{"Exception", "Error", "RuntimeException",
            "Throwable", "java.lang.Error", "java.lang.Exception",
            "java.lang.RuntimeException", "java.lang.Throwable", });
    }

    @Override
    public int[] getDefaultTokens()
    {
        return new int[]{TokenTypes.LITERAL_CATCH};
    }

    @Override
    public int[] getRequiredTokens()
    {
        return getDefaultTokens();
    }

    @Override
    public void visitToken(DetailAST aDetailAST)
    {
        final DetailAST paramDef = aDetailAST
                .findFirstToken(TokenTypes.PARAMETER_DEF);
        final DetailAST throwAST = getThrowAST(aDetailAST);


        DetailAST firstLvlChild = null;
        if (throwAST != null) {
            firstLvlChild = throwAST.getFirstChild();
        }

        DetailAST secondLvlChild = null;
        if (firstLvlChild != null) {
            secondLvlChild = firstLvlChild.getFirstChild();
        }

        // For warnings disable first lvl child must be an EXPR and
        // second lvl child must be IDENT or LITERAL_NEW with
        // appropriate boolean flags.
        final boolean noWarning = (throwAST != null
                && firstLvlChild != null
                && secondLvlChild != null
             && firstLvlChild.getType() == TokenTypes.EXPR
             && ((mAllowThrow && secondLvlChild.getType()
                     == TokenTypes.IDENT)
             || (mAllowRethrow && secondLvlChild.getType()
                     == TokenTypes.LITERAL_NEW)));

        final DetailAST excType = paramDef.findFirstToken(TokenTypes.TYPE);
        final FullIdent ident = CheckUtils.createFullType(excType);

        if (!noWarning && isIllegalClassName(ident.getText())) {
            log(aDetailAST, MSG_KEY, ident.getText());
        }
    }

    /** Looking for the keyword "throw" among current (aParentAST) node childs.
     * @param aParentAST - the current parent node.
     * @return null if the "throw" keyword was not found
     * or the LITERAL_THROW DetailAST otherwise
     */
    public DetailAST getThrowAST(DetailAST aParentAST)
    {

        final DetailAST asts[] = getChilds(aParentAST);

        for (DetailAST currentNode : asts) {

            if (currentNode.getType() != TokenTypes.PARAMETER_DEF
                    && currentNode.getNumberOfChildren() > 0)
            {
                final DetailAST astResult = (getThrowAST(currentNode));
                if (astResult != null) {
                    return astResult;
                }
            }

            if (currentNode.getType() == TokenTypes.LITERAL_THROW) {
                return currentNode;
            }

            if (currentNode.getNextSibling() != null) {
                currentNode = currentNode.getNextSibling();
            }
        }
        return null;
    }

    /** Gets all the children one level below on the current top node.
     * @param aNode - current parent node.
     * @return an array of childs one level below
     * on the current parent node aNode. */
    public static DetailAST[] getChilds(DetailAST aNode)
    {
        final DetailAST[] result = new DetailAST[aNode.getChildCount()];

        DetailAST currNode = aNode.getFirstChild();

        for (int i = 0; i < aNode.getNumberOfChildren(); i++) {
            result[i] = currNode;
            currNode = currNode.getNextSibling();
        }

        return result;
    }

}