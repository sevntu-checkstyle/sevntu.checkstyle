////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2014 Oliver Burn
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
////////////////////////////////////////////////////////////////////////////////

package com.github.sevntu.checkstyle.checks.coding;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Checks for useless "super()" calls in c-tors.
 * </p>
 * <p>
 * "super()" call could be considered by Check as "useless" in two cases:
 * <li>Case 1. Example:<br/>
 * <pre>
 * class Dummy {<br/>      Dummy() {<br/>          super();<br/>      }<br/>}<br/>
 * </pre>"super()" call is redundant. This class does not extend
 * anything.</li><br/><br/>
 * <li>Case 2. Example:<br/>
 * <pre>
 * class Derived extends Base {<br/>        Derived() {<br/>            super();<br/>         }<br/>}<br/>
 * </pre> "super()" is called without parameters. Java compiler
 * automatically inserts a call to the no-argument constructor of the
 * superclass. Check has an option "ignoreSuperCtorCallWithoutArgs" to skip this
 * case.</li>
 * </p>
 */
public class UselessSuperCtorCallCheck extends Check
{
    /**
     * Warning message key.
     */
    public static final String MSG_KEY_SUPER_CALL_IN_NOT_DERIVED_CLASS = "useless.super.ctor.call.in.not.derived.class";

    /**
     * Warning message key.
     */
    public static final String MSG_KEY_SUPER_CALL_WITHOUT_ARGS = "useless.super.ctor.call.without.args";

    /**
     * Used to ignore "super()" calls without arguments.
     */
    private boolean mIgnoreSuperCtorCallWithoutArgs;

    /**
     * Sets flag to IgnoreSuperCallWithoutArgs.
     * @param aIgnoreSuperCallWithoutArgs
     *        if true, check will ignore super() calls without arguments
     */
    public void setIgnoreSuperCtorCallWithoutArgs(boolean aIgnoreSuperCtorCallWithoutArgs)
    {
        mIgnoreSuperCtorCallWithoutArgs = aIgnoreSuperCtorCallWithoutArgs;
    }

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] { TokenTypes.SUPER_CTOR_CALL };
    }

    @Override
    public void visitToken(DetailAST aSuperCallNode)
    {
        if (isSuperArgListEmpty(aSuperCallNode))
        {
            DetailAST classDefNode = getClassDefinitionNode(aSuperCallNode);

            if (isClassDerived(classDefNode))
            {
                if (!mIgnoreSuperCtorCallWithoutArgs)
                {
                    log(aSuperCallNode.getLineNo(), MSG_KEY_SUPER_CALL_WITHOUT_ARGS);
                }
            }
            else
            {
                String className = classDefNode.findFirstToken(TokenTypes.IDENT).getText();
                log(aSuperCallNode.getLineNo(), MSG_KEY_SUPER_CALL_IN_NOT_DERIVED_CLASS, className);
            }
        }
    }

    /**
     * Checks whether this super c-tor call has no arguments
     * @param aSuperCallNode
     *        node of type TokenTypes.SUPER_CTOR_CALL
     * @return true, if super constructor call has no arguments
     */
    private static boolean isSuperArgListEmpty(DetailAST aSuperCallNode)
    {
        DetailAST argsListNode = aSuperCallNode.findFirstToken(TokenTypes.ELIST);

        return argsListNode.getChildCount() == 0;
    }

    /**
     * Returns class definition node which contains c-tor with given super c-tor
     * call node
     * @param aSuperCallNode
     *        node of type TokenTypes.SUPER_CTOR_CALL
     * @return class definition node
     */
    private static DetailAST getClassDefinitionNode(DetailAST aSuperCallNode)
    {
        DetailAST result = aSuperCallNode;

        while (result.getType() != TokenTypes.CLASS_DEF)
        {
            result = result.getParent();
        }

        return result;
    }

    /**
     * Checks whether this class is derived from other class
     * @param aClassDefNode
     *        class definition node
     * @return true, if this class extends anything
     */
    private static boolean isClassDerived(DetailAST aClassDefNode)
    {
        return aClassDefNode.findFirstToken(TokenTypes.EXTENDS_CLAUSE) != null;
    }

}
