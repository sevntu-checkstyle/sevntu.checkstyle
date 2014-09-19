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

import java.util.Iterator;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * This check prevents the default implementation Serializable interface in
 * inner classes (Serializable interface are default if methods readObject() or
 * writeObject() are not override in class). Check has option, that allow
 * implementation only one method, if it true, but if it false - class must
 * implement both methods. For more information read
 * "Effective Java (2nd edition)" chapter 11, item 74, page 294.
 * </p>
 * @author <a href="mailto:IliaDubinin91@gmail.com">Ilia Dubinin</a>
 */
public class AvoidDefaultSerializableInInnerClasses extends Check
{
	
	public static final String MSG_KEY  = "avoid.default.serializable.in.inner.classes";
	
    /**
    *<b>
    *Option, that allow partial implementation of serializable interface.
    *</b>
    */
    private boolean mAllowPartialImplementation;

    /**
     * <p>
     * Set allow partly implementation serializable interface.
     * </p>
     * @param aAllow - Option, that allow partial implementation
     *        of serializable interface.
     */
    public void setAllowPartialImplementation(boolean aAllow)
    {
        this.mAllowPartialImplementation = aAllow;
    }

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] {TokenTypes.CLASS_DEF };
    }

    @Override
    public void visitToken(DetailAST aDetailAST)
    {
        final boolean topLevelClass = aDetailAST.getParent() == null;
        if (!topLevelClass && isSerializable(aDetailAST)
                && !isStatic(aDetailAST)
                && !hasSerialazableMethods(aDetailAST))
        {
            final DetailAST implementsBlock = aDetailAST
                    .findFirstToken(TokenTypes.IMPLEMENTS_CLAUSE);
            log(implementsBlock.getLineNo(),
                    MSG_KEY);
        }
    }

    /**
     * <p>
     * Return true if it is nested class. Terminology is here :
     * http://download.oracle.com/javase/tutorial/java/javaOO/nested.html
     * </p>
     * @param aClassNode - class node
     * @return - boolean variable
     */
    private static boolean isStatic(DetailAST aClassNode)
    {
        boolean result = false;
        DetailAST modifiers = aClassNode.findFirstToken(TokenTypes.MODIFIERS);
        if (modifiers != null) {
            modifiers = modifiers.getFirstChild();
            while (!result && modifiers != null) {
                result = "static".equals(modifiers.getText());
                modifiers = modifiers.getNextSibling();
            }
        }
        return result;
    }

    /**
     * <p>
     * Return true, if inner class contain override method readObject() and
     * writeObject();
     * </p>
     * @param aClassNode
     *        the start node of class definition.
     * @return The boolean value. True, if method was override.
     */
    private boolean hasSerialazableMethods(DetailAST aClassNode)
    {
        final DetailAST objectBody =
                aClassNode.findFirstToken(TokenTypes.OBJBLOCK);
        int numberOfSerializationMethods = 0;
        for (final Iterator<DetailAST> methodsIter =
                ChildrenIterator.methodsIterator(objectBody);
                    methodsIter.hasNext();)
        {
            final DetailAST methodNode = methodsIter.next();
            if (isPrivateMethod(methodNode)
                        && isVoidMethod(methodNode)
                        && (hasCorrectParameter(methodNode, "ObjectInputStream")
                        || hasCorrectParameter(methodNode, "ObjectOutputStream")
                        ))
            {
                numberOfSerializationMethods++;
            }
            if (numberOfSerializationMethods == 1
                && mAllowPartialImplementation
                || numberOfSerializationMethods == 2)
            {
                return true;
            }
        }
        return false;
    }

    /**
     *<b>
     * Nested class, that implements custom iterator for DetailAST method nodes.
     *</b>
     */
    private static class ChildrenIterator implements Iterator<DetailAST>
    {
        /**
        *<b>
        *Type of child.
        *</b>
        */

        private final int mChildType;
        /**
        *<b>
        *Next
        *</b>
        */
        private DetailAST mNext;

        /**
        *<b>
        *Children Iterator constructor.
        *</b>
        *@param aParent - child parent.
        *@param aChildType - type of child.
        */
        public ChildrenIterator(DetailAST aParent, int aChildType)
        {
            this.mChildType = aChildType;
            mNext = aParent.findFirstToken(aChildType);
        }

        /**
        *<b>
        *Method iterator.
        *</b>
        *@param aParent - parent.
        *@return method iterator.
        */
        public static ChildrenIterator methodsIterator(DetailAST aParent)
        {
            return new ChildrenIterator(aParent, TokenTypes.METHOD_DEF);
        }

        /**
        *<b>
        *Return boolean value, if has next element.
        *</b>
        *@return boolean value
        */
		
		public boolean hasNext()
        {
            return mNext != null;
        }

        /**
        *<b>
        *Return next DetailAST element.
        *</b>
        *@return next DetailAST.
        */
		
		public DetailAST next()
        {
            final DetailAST result = mNext;
            while (mNext != null) {
                mNext = mNext.getNextSibling();
                if (mNext != null && mNext.getType() == mChildType) {
                    break;
                }
            }
            return result;
        }

        /**
        *<b>
        *Not implemented method.
        *</b>
        */
		
		public void remove()
        {
            throw new IllegalStateException("Not implemented");
        }
    }

    /**
     * <p>
     * Return true, if methods readObject() and writeObject() have correct
     * modifiers;
     * </p>
     * @param aMethodNode
     *        - current method node;
     * @return boolean value;
     */
    private static boolean isPrivateMethod(DetailAST aMethodNode)
    {
        DetailAST modifiers = aMethodNode.findFirstToken(TokenTypes.MODIFIERS);
        modifiers = modifiers.getFirstChild();
        boolean isPrivate = false;
        while (!isPrivate && modifiers != null) {
            isPrivate = "private".equals(modifiers.getText());
            modifiers = modifiers.getNextSibling();
        }
        return isPrivate;
    }

    /**
     * <p>
     * Return true, if method has void type.
     * </p>
     * @param aMethodNode - method node
     * @return boolean variable
     */
    private static boolean isVoidMethod(DetailAST aMethodNode)
    {
        DetailAST type = aMethodNode.findFirstToken(TokenTypes.TYPE);
        type = type.getFirstChild();
        return TokenTypes.LITERAL_VOID == type.getType();
    }

    /**
     * <p>
     * Return true, if method has correct parameters (ObjectInputStream for
     * readObject() and ObjectOutputStream for writeObject()).
     * </p>
     * @param aMethodNode - method node.
     * @param aParameterText - correct parameter text.
     * @return boolean variable.
     */
    private static boolean hasCorrectParameter(DetailAST aMethodNode,
            String aParameterText)
    {
        DetailAST parameters =
            aMethodNode.findFirstToken(TokenTypes.PARAMETERS);
        boolean result = false;
        if (parameters.getChildCount(TokenTypes.PARAMETER_DEF) == 1) {
            parameters = parameters.findFirstToken(TokenTypes.PARAMETER_DEF);
            parameters = parameters.findFirstToken(TokenTypes.TYPE);
            parameters = parameters.getFirstChild();
            result = aParameterText.equals(parameters.getText());
        }
        return result;
    }

    /**
     * <p>
     * Return true, if class implement Serializable interface;
     * </p>
     * @param aClassDefNode
     *        - the start node for class definition.
     * @return boolean value. True, if class implements Serializable interface.
     */
    private static boolean isSerializable(DetailAST aClassDefNode)
    {
        DetailAST implementationsDef = aClassDefNode
                .findFirstToken(TokenTypes.IMPLEMENTS_CLAUSE);
        boolean result = false;
        if (implementationsDef != null) {
            implementationsDef = implementationsDef.getFirstChild();
            while (!result && implementationsDef != null) {
                if (implementationsDef.getType() == TokenTypes.DOT) {
                    implementationsDef = implementationsDef.getLastChild();
                }
                result = "Serializable".equals(implementationsDef.getText());
                implementationsDef = implementationsDef.getNextSibling();
            }
        }
        return result;
    }
}
