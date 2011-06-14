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
package com.puppycrawl.tools.checkstyle.checks.coding;

import java.util.LinkedList;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.api.Check;

/**<p> This check prevents calls of overridable methods from constructor body.
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil
 *         Yaroslavtsev</a>
 */
public class OverridableMethodInConstructorCheck extends Check {

    /**
     * A key to search the warning message text in "messages.properties" file.
     * */
    private final String mKey = "overridable.method.in.constructor";

    /**
     * A list contains DetailAST nodes for all methods that called
     * from C-tors in current class.
     * */
    private LinkedList<DetailAST> mOverridableMethodsList =
        new LinkedList<DetailAST>();
    /**
     * A list of objects that contain a DetailAST node for each method is
     * invoked in C-tor and related DetailAST node for the class that owns
     * this C-tor.
     * */
    private LinkedList<MethodCalledFromCtor> mMethodCalledInCtorList =
        new LinkedList<MethodCalledFromCtor>();

    @Override
    public final int[] getDefaultTokens()
    {
        return new int[] {TokenTypes.METHOD_CALL, TokenTypes.METHOD_DEF};
    }

    @Override
    public final void visitToken(final DetailAST aDetailAST)
    {
     // for all METHOD_CALL nodes
        if (aDetailAST.getType() == TokenTypes.METHOD_CALL) {

            if (aDetailAST.getFirstChild() != null
                    && aDetailAST.getFirstChild().getType() != TokenTypes.DOT)
            {

                DetailAST currentNode = aDetailAST;

                while (currentNode != null
                        && currentNode.getType() != TokenTypes.METHOD_DEF
                        && currentNode.getType() != TokenTypes.CTOR_DEF
                        && currentNode.getType() != TokenTypes.CLASS_DEF)
                {
                    currentNode = currentNode.getParent();
                }

                if (currentNode.getType() == TokenTypes.CTOR_DEF) {
                    final DetailAST curClass = currentNode.getParent()
                            .getParent();
                    mMethodCalledInCtorList.add(new MethodCalledFromCtor(
                            aDetailAST, curClass));
                }
            }
        }

        else { // for all METHOD_DEF nodes

            final DetailAST modifiers = aDetailAST
                    .findFirstToken(TokenTypes.MODIFIERS);

            boolean hasPrivateModifier = false;
            if (modifiers != null && modifiers.getChildCount() != 0) {

                for (DetailAST curNode : getChildren(modifiers)) {

                    if (curNode.getType() == TokenTypes.LITERAL_PRIVATE) {
                        hasPrivateModifier = true;
                    }

                }

            }

            if (!hasPrivateModifier) {
                mOverridableMethodsList.add(aDetailAST);
            }

        }

    }

    @Override
    public final void finishTree(final DetailAST aRootAST)
    {

        for (MethodCalledFromCtor curMethod : mMethodCalledInCtorList) {

            final String curMethodName = curMethod.mMethodNode.findFirstToken(
                    TokenTypes.IDENT).getText();

            for (DetailAST curOverridableMethod : mOverridableMethodsList) {

                final String curOverridableMethodName = curOverridableMethod
                        .findFirstToken(TokenTypes.IDENT).getText();

                final String a = curMethod.mClassNode.toString();
                final String b = getClass(curOverridableMethod).toString();

                if (curMethodName.equals(curOverridableMethodName)
                        && a.equals(b))
                {
                    log(curMethod.mMethodNode, mKey, curMethodName);
                }
            }
        }
    }


    /**
     * Method that returns a root CLASS_DEF DetailAST for the class that owns
     * a method for aMethodNode METHOD_CALL node.
     * @return a DetailAST node for the class that owns
     * aMethodNode node.
     * @param aMethodNode - a current method DetailAST.
     * */
    public final DetailAST getClass(final DetailAST aMethodNode)
    {
        DetailAST result = null;
        DetailAST currentNode = aMethodNode;

        while (currentNode != null
                && currentNode.getType() != TokenTypes.CLASS_DEF)
        {
            currentNode = currentNode.getParent();
        }

        if (currentNode != null
                && currentNode.getType() == TokenTypes.CLASS_DEF)
        {
            result = currentNode;
        }

        return result;
    }

    /**
     * Class, an object of which contains a DetailAST node for
     * current method that called from C-tor and a DetailAST node for
     * the class that owns that C-tor.
     * */
    public class MethodCalledFromCtor
    {

        /**
         * A DetailAST node for current method that called from C-tor.
         * */
        private DetailAST mMethodNode;
        /**
         * A CLASS_DEF DetailAST node for the class that owns the C-tor
         * contains the call of method named methodNode.
         * */
        private DetailAST mClassNode;

        /**
         * C-tor of "MethodCalledFromCtor" object.
         * @param aMethodNode DetailAST node for current method that called from
         * C-tor.
         * @param aClassNode A CLASS_DEF DetailAST node for the class that owns
         * the C-tor contains the call of method named methodNode.
         * */
        MethodCalledFromCtor(final DetailAST aMethodNode,
                final DetailAST aClassNode)
        {
            this.mMethodNode = aMethodNode;
            this.mClassNode = aClassNode;
        }

    }

    /**
     * Gets all the children one level below on the current top node.
     * @param aNode - current parent node.
     * @return an array of children one level below on the current parent node
     *         aNode.
     */
    public final LinkedList<DetailAST> getChildren(final DetailAST aNode)
    {
        final LinkedList<DetailAST> result = new LinkedList<DetailAST>();

        DetailAST currNode = aNode.getFirstChild();

        while (currNode != null) {
            result.add(currNode);
            currNode = currNode.getNextSibling();
        }

        return result;
    }

}