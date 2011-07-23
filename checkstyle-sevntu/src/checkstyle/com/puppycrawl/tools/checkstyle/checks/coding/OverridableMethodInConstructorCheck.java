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

import java.util.LinkedList;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.api.Check;

/**
 * <p>
 * This check prevents any calls to overridable methods: from constructor body,
 * from clone() method (when implementing Cloneable interface) and from
 * readObject() method (when implementing Serializable interface).
 *
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil
 *         Yaroslavtsev</a>
 */
public class OverridableMethodInConstructorCheck extends Check
{

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file. This string is using to get a warning message about overridable
     * methods calls from any constructor body.
     * */
    private final String mKeyCtor = "overridable.method.in.constructor";

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file. This string is using to get a warning message about overridable
     * methods calls from any clone() method is implemented from Cloneable
     * interface.
     * */
    private final String mKeyClone = "overridable.method.in.clone";

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file. This string is using to get a warning message about calls of an
     * overridable methods from any readObject() method is implemented from
     * Serializable interface.
     * */
    private final String mKeyReadObject = "overridable.method.in.readobject";

    /**
     * A current MethodDef AST is being processed by check.
     * */
    private DetailAST mCurMethodDef;

    /**
     * A current root of the synthax tree is being processed.
     * */
    private DetailAST mTreeRootAST;

    /**
     * A boolean check box that enables the searching of overridable methods
     * calls in any clone() method is implemented from Cloneable interface.
     * */
    private boolean mCheckCloneMethod = true;

    /**
     * A boolean check box that enables the searching of overridable methods
     * calls in any readObject() method is implemented froml Serializable
     * interface.
     */
    private boolean mCheckReadObjectMethod = true;

    /**
     * Counter for the number of method definitions in current class.
     */
    private int mCurMethodDefCount;

    /**
     * Enable|Disable searching calls of overridable methods in clone() method
     * from Cloneable interface.
     *
     * @param aValue
     *            The state of a boolean check box that enables the searching of
     *            overridable methods calls in any clone() method is implemented
     *            from Cloneable interface.
     */
    public void setCheckCloneMethod(final boolean aValue)
    {
        mCheckCloneMethod = aValue;
    }

    /**
     * Enable|Disable searching calls of overridable methods in readObject()
     * method from Serializable interface.
     *
     * @param aValue
     *            The state of a boolean check box that enables the searching of
     *            overridable methods calls in any readObject() method is
     *            implemented from Serializable interface.
     */
    public void setCheckReadObjectMethod(final boolean aValue)
    {
        mCheckReadObjectMethod = aValue;
    }

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] {TokenTypes.CTOR_DEF, TokenTypes.METHOD_DEF };
    }

    @Override
    public void beginTree(DetailAST aRootAST)
    {
        mTreeRootAST = aRootAST;
    }

    @Override
    public void visitToken(final DetailAST aDetailAST)
    {
        switch (aDetailAST.getType()) {

        case TokenTypes.CTOR_DEF:
            verify(aDetailAST, mKeyCtor);
            break;

        case TokenTypes.METHOD_DEF:

            final String methodName =
                aDetailAST.findFirstToken(TokenTypes.IDENT).getText();

			if (mCheckCloneMethod
					&& "clone".equals(methodName)
					&& !isWithinTheInterface(aDetailAST)
					&& realizesAnInterface(getClassDef(aDetailAST), "Cloneable"))
            {
                verify(aDetailAST, mKeyClone);

            }
            else if (mCheckReadObjectMethod
					&& "readObject".equals(methodName)
					&& !isWithinTheInterface(aDetailAST)
					&& realizesAnInterface(getClassDef(aDetailAST),
                        "Serializable"))
            {
                verify(aDetailAST, mKeyReadObject);
            }
            break;

        default:
		    break;
	    }
	}

    /**
     * Gets all METHOD_CALL DetailASTs (in current method or c-tor body) that
     * are pointing to overridable methods calls and adds them to the warnings
     * list.
     *
     * @param aDetailAST
     *            A DetailAST pointing to current method or c-tor body is being
     *            processed to retrieve overridable method calls.
     * @param aKey
     *            A string is using to find the warning message text in
     *            "messages.properties" file.
     */
    private void verify(final DetailAST aDetailAST, final String aKey)
    {

        final LinkedList<DetailAST> methodCallsToWarnList =
            getOverridables(aDetailAST);

        for (DetailAST methodCallAST : methodCallsToWarnList) {
            final String curMethodName = getMethodName(methodCallAST); //
            log(methodCallAST, aKey, curMethodName);
        }
    }

    /**
     * Searches all METHOD_CALL DetailASTs (in current method or c-tor body)
     * that are pointing to overridable methods calls and generates a list of
     * them.
     *
     * @param aParentAST
     *            A DetailAST (METHOD_DEF of CTOR_DEF) pointing to current
     *            method or c-tor body is being processed to retrieve
     *            overridable method calls.
     * @return A list of overridable methods calls from current method or
     *         constructor body.
     */
    private LinkedList<DetailAST> getOverridables(
        final DetailAST aParentAST)
    {

        final LinkedList<DetailAST> result = new LinkedList<DetailAST>();
        final LinkedList<DetailAST> methodCallsList =
             getMethodCallsList(aParentAST);

        for (DetailAST curNode : methodCallsList) {
            if (isCallToOverridableMethod(curNode)) {
                result.add(curNode);
            }
        }
        return result;
    }

    /**
     * Checks that current METHOD_CALL DetailAST is being processed is pointing
     * to overridable method call.
     *
     * @param aMethodCallAST
     *            A METHOD_CALL DetailAST is currently being processed.
     * @return true if current DetailAST is being processed is pointing to the
     *         overridable method call and false otherwise.
     */
    private boolean isCallToOverridableMethod(
        final DetailAST aMethodCallAST)
    {

        boolean result = false;

        final String methodName = getMethodName(aMethodCallAST);

        if (methodName != null) {

            final DetailAST methodDef = getMethodDef(aMethodCallAST);

            if (methodDef != null) {

                if (isPrivateOrFinal(methodDef)) {
                    final LinkedList<DetailAST> methodCallsList =
                         getMethodCallsList(methodDef);
                    for (DetailAST curNode : methodCallsList) {
                        if (isCallToOverridableMethod(curNode)) {
                            result = true;
                            break;
                        }
                    }
                }
                else {
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * @param aParentAST The
     * @return The
     */
    private LinkedList<DetailAST> getMethodCallsList(
        final DetailAST aParentAST)
    {
        return getMethodCalls(aParentAST, new LinkedList<DetailAST>());
    }

    /**
     * @param aParentAST The
     * @param aCurResultList The
     * @return The
     */
    private LinkedList<DetailAST> getMethodCalls(
        final DetailAST aParentAST, LinkedList<DetailAST> aCurResultList)
    {
        for (DetailAST curNode : getChildren(aParentAST)) {
            if (curNode.getNumberOfChildren() > 0) {
                if (curNode.getType() == TokenTypes.METHOD_CALL && !isCallingItself(curNode)) {
                        aCurResultList.add(curNode);
                }
                else {
                    getMethodCalls(curNode, aCurResultList);
                }
            }
        }
        return aCurResultList;
    }

    /**
     * Gets the method name is related to the current METHOD_CALL DetailAST.
     *
     * @param aMethodCallAST
     *            A METHOD_CALL DetailAST is currently being processed.
     * @return the method name is related to the current METHOD_CALL DetailAST.
     */
    private String getMethodName(final DetailAST aMethodCallAST)
    {

        String result = null;

        final DetailAST ident = aMethodCallAST.findFirstToken(TokenTypes.IDENT);

        if (ident != null) {
            result = ident.getText();
        }

        else {

            final DetailAST childAST = aMethodCallAST.getFirstChild();

            if (childAST != null && childAST.getType() == TokenTypes.DOT) {

                final DetailAST firstChild = childAST.getFirstChild();
                final DetailAST lastChild = childAST.getLastChild();

                if (firstChild.getType() == TokenTypes.LITERAL_THIS
                        || firstChild.getType() == TokenTypes.LPAREN)
                {
                    result = lastChild.getText();

                }
                else if (firstChild.getType() == TokenTypes.IDENT
                        && lastChild.getType() == TokenTypes.IDENT)
                {
                    final String curClassName = getClassDef(aMethodCallAST)
                            .findFirstToken(TokenTypes.IDENT).getText();
                    if (firstChild.getText().equals(curClassName)) {
                        result = lastChild.getText();
                    }
                }
            }
        }
        return result;
    }

    /**
     * Gets the method definition is related to the current METHOD_CALL
     * DetailAST.
     *
     * @param aMethodCallAST
     *            A METHOD_CALL DetailAST is currently being processed.
     * @return the METHOD_DEF DetailAST is pointing to the method definition
     *         which is related to the current METHOD_CALL DetailAST.
     */
    private DetailAST getMethodDef(final DetailAST aMethodCallAST)
    {
        mCurMethodDef = null;
        mCurMethodDefCount = 0;

        final String methodName = getMethodName(aMethodCallAST);
        if (methodName != null) {

            DetailAST curClassAST = getClassDef(aMethodCallAST);

            getMethodDefCount(curClassAST, methodName);

            if (mCurMethodDefCount == 0) {

                final LinkedList<DetailAST> baseClasses =
                     getBaseClasses(curClassAST);

                for (DetailAST curBaseClass : baseClasses) {
                    getMethodDefCount(curBaseClass, methodName);
                    if (mCurMethodDefCount == 1) {
                        curClassAST = curBaseClass;
                        break;
                    }
                }
            }

            if (mCurMethodDefCount == 1) {
                getMethodDef(curClassAST, methodName);
            }
        }
        return mCurMethodDef;
    }

    /**
     * Gets the method definitions is related to the current METHOD_CALL
     * DetailAST using the name of method to be searched.
     *
     * @param aParentAST
     *            A parent CLASS_DEF DetailAST node which is the start point
     *            when searching.
     * @param aMethodName
     *            String containing a method name is currently being searched.
     */
    private void getMethodDef(final DetailAST aParentAST,
        final String aMethodName)
    {

        for (DetailAST curNode : getChildren(aParentAST)) {

            if (curNode.getNumberOfChildren() > 0) {
                if (curNode.getType() == TokenTypes.METHOD_DEF) {
                    final String curMethodName = curNode.findFirstToken(
                            TokenTypes.IDENT).getText();
                    if (aMethodName.equals(curMethodName)) {
                        mCurMethodDef = curNode;
                        break;
                    }
                }

                final int type = curNode.getType();

                if (type != TokenTypes.CLASS_DEF && type != TokenTypes.CTOR_DEF
                        && type != TokenTypes.MODIFIERS
                        && type != TokenTypes.IMPLEMENTS_CLAUSE
                        && type != TokenTypes.METHOD_DEF)
                {
                    getMethodDef(curNode, aMethodName);
                }
            }
        }
    }

    /**
     * Gets the the count of method definitions that are related to the current
     * METHOD_CALL DetailAST. The search is performed by name of the called
     * method.
     *
     * @param aParentAST
     *            A parent CLASS_DEF DetailAST node which is the start point
     *            when searching.
     * @param aMethodName
     *            String containing a method name is currently being searched.
     */
    private void getMethodDefCount(final DetailAST aParentAST,
        final String aMethodName)
    {

        for (DetailAST curNode : getChildren(aParentAST)) {

            if (curNode.getNumberOfChildren() > 0) {
                if (curNode.getType() == TokenTypes.METHOD_DEF) {
                    final String curMethodName = curNode.findFirstToken(
                            TokenTypes.IDENT).getText();
                    if (aMethodName.equals(curMethodName)) {
                        ++mCurMethodDefCount;
                    }
                }

                final int type = curNode.getType();

                if (type != TokenTypes.CLASS_DEF && type != TokenTypes.CTOR_DEF
                        && type != TokenTypes.MODIFIERS
                        && type != TokenTypes.IMPLEMENTS_CLAUSE
                        && type != TokenTypes.METHOD_DEF)
                {
                    getMethodDefCount(curNode, aMethodName);
                }
            }
        }
    }

    /**
     * Checks that method is related to the current METHOD_DEF node has private
     * or final modifier.
     *
     * @param aMethodDefAST
     *            A METHOD_DEF DetailAST node is currently being processed.
     * @return true if method is related to current aMethodDefAST METHOD_DEF
     *         node has private or final modifier and false otherwise.
     */
    private boolean isPrivateOrFinal(final DetailAST aMethodDefAST)
    {

        boolean result = false;
        final DetailAST modifiers = aMethodDefAST
                .findFirstToken(TokenTypes.MODIFIERS);

        if (modifiers != null && modifiers.getChildCount() != 0) {
            for (DetailAST curNode : getChildren(modifiers)) {
                if (curNode.getType() == TokenTypes.LITERAL_PRIVATE
                        || curNode.getType() == TokenTypes.FINAL)
                {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Gets a parent CLASS_DEF DetailAST node for current METHOD_CALL DetailAST
     * node.
     *
     * @return a parent CLASS_DEF node for the class that owns a METHOD_CALL
     *         node named aMethodNode.
     * @param aMethodNode
     *            - a current method DetailAST.
     * */
	private DetailAST getClassDef(final DetailAST aMethodNode) {

		DetailAST result = null;
		DetailAST curNode = aMethodNode;

		while (curNode != null && curNode.getType() != TokenTypes.CLASS_DEF) {
			curNode = curNode.getParent();
		}
		result = curNode;

		return result;
	}

    /** !!!!!!!!
     * Gets a parent CLASS_DEF DetailAST node for current METHOD_CALL DetailAST
     * node.
     *
     * @return a parent CLASS_DEF node for the class that owns a METHOD_CALL
     *         node named aMethodNode.
     * @param aMethodDefNode
     *            - a current method DetailAST.
     * */
	private boolean isWithinTheInterface(final DetailAST aMethodDefNode) {

		boolean result = false;
		DetailAST curNode = aMethodDefNode;

		while (curNode != null && curNode.getType() != TokenTypes.INTERFACE_DEF) {
			curNode = curNode.getParent();
		}

		if (curNode != null) {
			result = (curNode.getType() == TokenTypes.INTERFACE_DEF);
		}
		return result;
	}
    
    
    /**
     * Checks that class realizes "anInterfaceName" interface (checks that class
     * implements this interface or that it extends at least one class which
     * implements it).
     *
     * @param aClassDefNode
     *            A CLASS_DEF DetailAST for class is currently being checked.
     * @param aInterfaceName
     *            The name of the interface to check.
     * @return true if lass realizes "anInterfaceName" interface and false
     *         otherwise.
     */
    private boolean realizesAnInterface(final DetailAST aClassDefNode,
        final String aInterfaceName)
    {

        boolean result = false;
        final LinkedList<DetailAST> classWithBaseClasses =
             getBaseClasses(aClassDefNode);

        classWithBaseClasses.add(aClassDefNode);

        for (DetailAST classAST : classWithBaseClasses) {
            if (implementsAnInterface(classAST, aInterfaceName)) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Checks that class implements "anInterfaceName" interface.
     *
     * @param aClassDefNode
     *            A CLASS_DEF DetailAST for class is currently being checked.
     * @param aInterfaceName
     *            The name of the interface to check.
     * @return true if class implements "anInterfaceName" interface and false
     *         otherwise.
     */
    private boolean implementsAnInterface(final DetailAST aClassDefNode,
        final String aInterfaceName)
    {

        boolean result = false;
        final DetailAST implementsClause = aClassDefNode
                .findFirstToken(TokenTypes.IMPLEMENTS_CLAUSE);

        if (implementsClause != null) {
            for (DetailAST ident : getChildren(implementsClause)) {
                if (ident.getText().equals(aInterfaceName)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Gets a list of CLASS_DEF DetailAST nodes that are associated with class
     * is currently being processed and all it`s base classes.
     *
     * @param aClassDefNode
     *            A CLASS_DEF DetailAST is related to the class is currently
     *            being processed.
     * @return a list of CLASS_DEF DetailAST nodes for class is currently being
     *         processed and all it`s base classes.
     */
    private LinkedList<DetailAST> getBaseClasses(
        final DetailAST aClassDefNode)
    {

        final LinkedList<DetailAST> result = new LinkedList<DetailAST>();
        String baseClassName = getBaseClassName(aClassDefNode);

        if (baseClassName != null) {
            DetailAST curClass = getClassDefByName(mTreeRootAST, baseClassName);
            while (curClass != null) {
                result.add(curClass);
                baseClassName = getBaseClassName(curClass);
                if (baseClassName != null) {
                    curClass = getClassDefByName(mTreeRootAST, baseClassName);
                }
                else {
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Gets the base class for current class is named "aClassName".
     *
     * @param aRootNode
     *            A root node of current synthax tree is being processed.
     * @param aClassName
     *            name of the class for base class searching.
     * @return a CLASS_DEF DetailAST node which is related to the base class for
     *         current class is named "aClassName".
     */
    private DetailAST getClassDefByName(DetailAST aRootNode, String aClassName)
    {

        DetailAST result = null;
        DetailAST curNode = aRootNode;

        while (curNode != null) {
            DetailAST toVisit = curNode.getFirstChild();
            while ((curNode != null) && (toVisit == null)) {
                toVisit = curNode.getNextSibling();
                if (toVisit == null) {
                    curNode = curNode.getParent();
                }
            }
            curNode = toVisit;

            if(curNode == null)
            {
            	break;
            }

            if (curNode.getType() == TokenTypes.CLASS_DEF
                    && curNode.findFirstToken(TokenTypes.IDENT).getText()
                            .equals(aClassName))
            {
                result = curNode;
                break;
            }
        }
        return result;
    }

    /**
     * Gets the the name of base class for current class.
     *
     * @param aClassDefNode
     *            A CLASS_DEF DetailAST.
     * @return the name of a base class for current class.
     */
    private String getBaseClassName(final DetailAST aClassDefNode)
    {

        String result = null;
        final DetailAST extendsClause = aClassDefNode
                .findFirstToken(TokenTypes.EXTENDS_CLAUSE);

        if (extendsClause != null) {
            final DetailAST dot = extendsClause.findFirstToken(TokenTypes.DOT);
            if (dot != null) {
                result = dot.findFirstToken(TokenTypes.IDENT).getText();
            }
            else {
                result = extendsClause.findFirstToken(TokenTypes.IDENT)
                        .getText();
            }
        }
        return result;
    }

    /**
     * Checks that the method calls himself.
     *
     * @param aMethodCallNode
     *            A METHOD_CALL DetailAST for method is currently being checked.
     * @return true if the method is related to the current METHOD_CALL node
     *         calls himself and false otherwise.
     */
    private boolean isCallingItself(final DetailAST aMethodCallNode)
    {

        boolean result = false;
        final DetailAST methodDef = getMethodDef(aMethodCallNode);
        final DetailAST ownerMethodDef = getOwnerMethodDef(aMethodCallNode);

        if (methodDef != null && ownerMethodDef != null) {
            result = (methodDef == ownerMethodDef);
        }
        return result;
    }

    /**
     * Gets the the METHOD_DEF node for the method which body contains
     * the current method call is related to the METHOD_CALL node is
     * being processed.
     *
     * @param aMethodCallNode
     *            A METHOD_CALL node is currently being processed.
     * @return the METHOD_DEF node for the method which body contains
     *            the current method call.
     */
    private DetailAST getOwnerMethodDef(final DetailAST aMethodCallNode)
    {

        DetailAST result = null;
        DetailAST curNode = aMethodCallNode;

        while (curNode != null && curNode.getType() != TokenTypes.CLASS_DEF
                && curNode.getType() != TokenTypes.METHOD_DEF)
        {
            curNode = curNode.getParent();
        }

        if (curNode != null && curNode.getType() == TokenTypes.METHOD_DEF) {
            result = curNode;
        }

        return result;
    }

    /**
     * Gets all the children one level below on the current top node.
     *
     * @param aNode
     *            - current parent node.
     * @return an array of children one level below on the current parent node
     *         aNode.
     */
    private LinkedList<DetailAST> getChildren(final DetailAST aNode)
    {
        final LinkedList<DetailAST> result = new LinkedList<DetailAST>();
        DetailAST curNode = aNode.getFirstChild();
        while (curNode != null) {
            result.add(curNode);
            curNode = curNode.getNextSibling();
        }
        return result;
    }

}