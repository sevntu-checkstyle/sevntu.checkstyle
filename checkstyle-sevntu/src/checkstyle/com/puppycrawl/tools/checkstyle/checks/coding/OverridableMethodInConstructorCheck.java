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
import java.util.List;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.api.Check;

/**
 * <p>
 * This check prevents any calls to overridable methods: from constructor body,
 * from clone() method (when implementing Cloneable interface) and from
 * readObject() method (when implementing Serializable interface).
 * </p>
 * <p>
 * Necessity:
 * <q>Constructors must not invoke overridable methods, directly or indirectly.
 * If you violate this rule, program failure will result. The superclass
 * constructor runs before the subclass constructor, so the overriding method in
 * the subclass will be invoked before the subclass constructor has run. If the
 * overriding method depends on any initialization performed by the subclass
 * constructor, the method will not behave as expected.</q> (a quote from
 * Effective Java 2nd Edition, Item 17)
 * </p>
 * Here's an example to illustrate: <code> <pre>
 * public class ConstructorCallsOverride {
 *    public static void main(String[] args) {
 *        abstract class Base {
 *            Base() { overrideMe(); }
 *            abstract void overrideMe(); 
 *        }
 *        class Child extends Base {
 *            final int x;
 *            Child(int x) { this.x = x; }
 *            void overrideMe() {
 *                System.out.println(x);
 *            }
 *        }
 *        new Child(42); // prints "0"
 *    }
 * }
 * </code> </pre>
 * <p>
 * Here, when Base constructor calls overrideMe, Child has not finished
 * initializing the final int x, and the method gets the wrong value. This will
 * almost certainly lead to bugs and errors.
 * </p>
 * <p>
 * Note: This check does not handle the situation when there is a call to an
 * overloaded method(s). Here`s an example:
 * 
 * <code> <pre> public class Test {
 * 
 *   public static void main(String[] args) {
 * 
 *       class Base {
 *           Base() {
 *               System.out.println("Base C-tor ");
 *               overrideMe("Foo!"); // no warnings here, because the method
 *                                   // named "overrideMe" is overloaded.
 *           }
 * 
 *           void overrideMe() {
 *               System.out.println("Base overrideMe() ");
 *           }
 *           
 *           void overrideMe(String str) {
 *               System.out.println("Base overrideMe(String str) ");
 *           }
 * 
 *       }
 * 
 *       class Child extends Base {
 *           final int x;
 * 
 *           Child(int x) {
 *               this.x = x;
 *           }
 * 
 *           void overrideMe(String str) {
 *               System.out.println("Child`s overrideMe(): "+x);
 *           }
 *       }
 * 
 *     new Child(999);
 *   }
 * 
 * } </pre> </code>
 * 
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil
 *         Yaroslavtsev</a>
 */
public class OverridableMethodInConstructorCheck extends Check {

	/**
	 * A key is pointing to the warning message text in "messages.properties"
	 * file.
	 * */
	private final String mKey = "overridable.method";

	/**
	 * A key is using to build a warning message about calls of an overridable
	 * methods from any constructor body.
	 * */
	private final String mKeyCtor = "constructor";

	/**
	 * A key is using to build a warning message about calls of an overridable
	 * methods from any clone() method is implemented from Cloneable interface.
	 * */
	private final String mKeyClone = "\"clone()\" method";

	/**
	 * A key is using to build a warning message about calls of an overridable
	 * methods from any readObject() method is implemented from Serializable
	 * interface.
	 * */
    private final String mKeyReadObject = "\"readObject()\" method";

    /**
     * A list contains all METHOD_CALL DetailAST nodes that have been already
     * visited by check.
     * */
    private List<DetailAST> mVisitedMetCalls = new LinkedList<DetailAST>();

    /**
     * A current MethodDef AST is being processed by check.
     * */
    private DetailAST mCurMethodDef;

    /**
     * A current root of the synthax tree is being processed.
     * */
    private DetailAST mTreeRootAST;

    /**
     * A boolean check box that enables the searching of calls to overridable
     * methods from body of any clone() method is implemented from Cloneable
     * interface.
     * */
    private boolean mCheckCloneMethod = true;

    /**
     * A boolean check box that enables the searching of calls to overridable
     * methods from body of any readObject() method is implemented from
     * Serializable interface.
     */
    private boolean mCheckReadObjectMethod = true;

    /**
     * Method definitions counter for current class.
     */
    private int mCurMethodDefCount;

    /**
     * Enable|Disable searching of calls to overridable methods from body of any
     * clone() method is implemented from Cloneable interface.
     * 
     * @param aValue
     *            The state of a boolean check box that enables the searching of
     *            calls to overridable methods from body of any clone() method
     *            is implemented from Cloneable interface.
     */
    public void setCheckCloneMethod(final boolean aValue) {
        mCheckCloneMethod = aValue;
    }

    /**
     * Enable|Disable searching of calls to overridable methods from body of any
     * readObject() method is implemented from Serializable interface.
     * 
     * @param aValue
     *            The state of a boolean check box that enables the searching of
     *            calls to overridable methods from body of any readObject()
     *            method is implemented from Serializable interface.
     */
    public void setCheckReadObjectMethod(final boolean aValue) {
        mCheckReadObjectMethod = aValue;
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] { TokenTypes.CTOR_DEF, TokenTypes.METHOD_DEF };
    }

    @Override
    public void beginTree(DetailAST aRootAST) {
        mTreeRootAST = aRootAST;
    }

	@Override
	public void visitToken(final DetailAST aDetailAST) {
		switch (aDetailAST.getType()) {

		case TokenTypes.CTOR_DEF:
			verify(aDetailAST, mKeyCtor);
			break;

		case TokenTypes.METHOD_DEF:

			final String methodName = aDetailAST.findFirstToken(
					TokenTypes.IDENT).getText();

			if (mCheckCloneMethod &&
				"clone".equals(methodName) &&
				!isWithinTheInterface(aDetailAST) &&
				realizesAnInterface(getClassDef(aDetailAST), "Cloneable")) {
				verify(aDetailAST, mKeyClone);

			} else if (mCheckReadObjectMethod &&
				"readObject".equals(methodName) &&
				!isWithinTheInterface(aDetailAST) &&
				realizesAnInterface(getClassDef(aDetailAST),"Serializable")) {
				verify(aDetailAST, mKeyReadObject);
			}
			break;

		default:
			break;
		}
	}

    /**
     * Gets all METHOD_CALL DetailASTs that are pointing to overridable methods
     * calls from current method or c-tor body and logs them.
     * 
     * @param aDetailAST
     *            A DetailAST node which is pointing to current method or c-tor
     *            body is being processed to retrieve overridable method calls
     *            list.
     * @param aKey
     *            A string is using to retrieve the warning message text from
     *            "messages.properties" file.
     */
    private void verify(final DetailAST aDetailAST, final String aKey) {

        final List<DetailAST> methodCallsToWarnList = getOverridables(aDetailAST);

        for (DetailAST methodCallAST : methodCallsToWarnList) {
            String msgKey = mKey;
            if (isLeadToOverridableMetCall(methodCallAST)) {
                msgKey += ".leads";
            }            
            log(methodCallAST, msgKey, getMethodName(methodCallAST), aKey);
        }
    }

    /**
     * Searches for all METHOD_CALL DetailASTs that are pointing to overridable
     * methods calls in current method or c-tor body and generates a list of
     * them.
     * 
     * @param aParentAST
     *            A DetailAST METHOD_DEF of CTOR_DEF node which is pointing to
     *            the current method or c-tor body is being processed to
     *            retrieve overridable method calls.
     * @return A list of overridable methods calls for current method or
     *         constructor body.
     */
    private List<DetailAST> getOverridables(final DetailAST aParentAST) {

        final List<DetailAST> result = new LinkedList<DetailAST>();
        final List<DetailAST> methodCallsList = getMethodCallsList(aParentAST);

        for (DetailAST curNode : methodCallsList) {
            mVisitedMetCalls.clear();
            if (isCallToOverridableMethod(curNode)) {
                result.add(curNode);
            }
        }
        return result;
    }

    /**
     * Checks that current processed METHOD_CALL DetailAST is pointing to
     * overridable method call.
     * 
     * @param aMethodCallAST
     *            A METHOD_CALL DetailAST is currently being processed.
     * @return true if current processed METHOD_CALL node is pointing to the
     *         overridable method call and false otherwise.
     */
	private boolean isCallToOverridableMethod(final DetailAST aMethodCallAST) {

		boolean result = false;
		mVisitedMetCalls.add(aMethodCallAST);

		final String methodName = getMethodName(aMethodCallAST);

		if (methodName != null) {
			final DetailAST methodDef = getMethodDef(aMethodCallAST);
			if (methodDef != null) {
				if (isPrivateOrFinal(methodDef)) {
					final List<DetailAST> methodCallsList = getMethodCallsList(methodDef);
					for (DetailAST curNode : methodCallsList) {
						if (mVisitedMetCalls.contains(curNode)) {
							result = false;
							break;
						} else if (isCallToOverridableMethod(curNode)) {
							result = true;
							break;
						}
					}
				} else {
					result = true;
				}
			}
		}
		return result;
	}

    /**
     * Gets all METHOD_CALL nodes which are below on the current parent
     * METHOD_DEF or CTOR_DEF node.
     * 
     * @param aParentAST
     *            The current parent METHOD_DEF or CTOR_DEF node.
     * @return List contains all METHOD_CALL nodes which are below on the
     *         current parent node.
     */
    private List<DetailAST> getMethodCallsList(final DetailAST aParentAST) {
        return getMethodCalls(aParentAST, new LinkedList<DetailAST>());
    }

    /**
     * Gets all METHOD_CALL nodes which are below on the current parent
     * METHOD_DEF or CTOR_DEF node.
     * 
     * @param aParentAST
     *            The current parent METHOD_DEF or CTOR_DEF node.
     * @param aCurResultList
     *            The initial state of result list.
     * @return List contains all METHOD_CALL nodes which are below on the
     *         current parent node.
     */
    private List<DetailAST> getMethodCalls(final DetailAST aParentAST,
            LinkedList<DetailAST> aCurResultList) {
        for (DetailAST curNode : getChildren(aParentAST)) {
            if (curNode.getNumberOfChildren() > 0) {
                if (curNode.getType() == TokenTypes.METHOD_CALL) {
                    aCurResultList.add(curNode);
                } else {
                    getMethodCalls(curNode, aCurResultList);
                }
            }
        }
        return aCurResultList;
    }

    /**
     * Checks that current method call leads to overridable method(s) call(s).
     * 
     * @param aMethodCallAST
     *            The current METHOD_CALL DetailAST node is being processed.
     * @return true if current method call leads to overridable method(s)
     *         call(s) and false otherwise.
     */
    private boolean isLeadToOverridableMetCall(DetailAST aMethodCallAST) {
        boolean result = false;
        if (getMethodName(aMethodCallAST) != null) {
            final DetailAST methodDef = getMethodDef(aMethodCallAST);
            if (methodDef != null && isPrivateOrFinal(methodDef)) {
                result = true;
            }
        }
        return result;
    }

	/**
	 * Gets the method name is related to the current METHOD_CALL DetailAST.
	 * 
	 * @param aMethodCallAST
	 *            A METHOD_CALL DetailAST node is currently being processed.
	 * @return The method name is related to the current METHOD_CALL DetailAST.
	 */
	private String getMethodName(final DetailAST aMethodCallAST) {

		String result = null;

		final DetailAST ident = aMethodCallAST.findFirstToken(TokenTypes.IDENT);

		if (ident != null) {
			result = ident.getText();
		} else {
			final DetailAST childAST = aMethodCallAST.getFirstChild();

			if (childAST != null && childAST.getType() == TokenTypes.DOT) {

				final DetailAST firstChild = childAST.getFirstChild();
				final DetailAST lastChild = childAST.getLastChild();

				if (firstChild.getType() == TokenTypes.LITERAL_THIS
						|| firstChild.getType() == TokenTypes.LPAREN) {
					result = lastChild.getText();

				} else if (firstChild.getType() == TokenTypes.IDENT
						&& lastChild.getType() == TokenTypes.IDENT) {
					final String curClassName = getClassDef(aMethodCallAST)
							.findFirstToken(TokenTypes.IDENT).getText();
					if (firstChild.getText().equals(curClassName)) {
						result = lastChild.getText();
					} else {
						DetailAST classDef = getClassDefByName(mTreeRootAST,
								firstChild.getText());
						if (classDef != null) {
							result = lastChild.getText();
						}
					}
				}
			}
		}
		return result;
	}

    /**
     * Gets the method definition is related to the current METHOD_CALL
     * DetailAST node.
     * 
     * @param aMethodCallAST
     *            A METHOD_CALL DetailAST node is currently being processed.
     * @return the METHOD_DEF DetailAST node is pointing to the method
     *         definition which is related to the current METHOD_CALL DetailAST
     *         node.
     */
	private DetailAST getMethodDef(final DetailAST aMethodCallAST) {

		DetailAST result = null;

		mCurMethodDef = null;
		mCurMethodDefCount = 0;

		final String methodName = getMethodName(aMethodCallAST);

		if (methodName != null) {

			DetailAST curClassAST = getClassDef(aMethodCallAST);

			getMethodDef(curClassAST, methodName);

			if (mCurMethodDefCount == 0) {

				final List<DetailAST> baseClasses = getBaseClasses(curClassAST);

				for (DetailAST curBaseClass : baseClasses) {
					mCurMethodDef = null;
					mCurMethodDefCount = 0;
					getMethodDef(curBaseClass, methodName);
					if (mCurMethodDefCount == 1) {
						result = mCurMethodDef;
						break;
					}
				}
			} else if (mCurMethodDefCount == 1) {
				getMethodDef(curClassAST, methodName);
				result = mCurMethodDef;
			}
		}
		return result;
	}

    /**
     * Gets the method definition is related to the current METHOD_CALL
     * DetailAST using the name of method to be searched. Ignores overloaded
     * methods (retrieves only one METHOD_DEF node).
     * 
     * @param aParentAST
     *            A parent CLASS_DEF DetailAST node which uses as a start point
     *            when searching.
     * @param aMethodName
     *            String containing the name of method is currently being
     *            searched.
     */
    private void getMethodDef(final DetailAST aParentAST,
            final String aMethodName) {

        for (DetailAST curNode : getChildren(aParentAST)) {

            if (curNode.getNumberOfChildren() > 0) {
                if (curNode.getType() == TokenTypes.METHOD_DEF) {
                    final String curMethodName = curNode.findFirstToken(
                            TokenTypes.IDENT).getText();
                    if (aMethodName.equals(curMethodName)) {
                        mCurMethodDef = curNode;
                        ++mCurMethodDefCount;
                    }
                }

                final int type = curNode.getType();

                if (type != TokenTypes.CLASS_DEF && type != TokenTypes.CTOR_DEF
                        && type != TokenTypes.MODIFIERS
                        && type != TokenTypes.IMPLEMENTS_CLAUSE
                        && type != TokenTypes.METHOD_DEF) {
                    getMethodDef(curNode, aMethodName);
                }
            }
        }
    }

	/**
	 * Checks that method is related to the current METHOD_DEF DetailAST node is
	 * private or final.
	 * 
	 * @param aMethodDefAST
	 *            A METHOD_DEF DetailAST node is currently being processed.
	 * @return true if method is related to current aMethodDefAST METHOD_DEF
	 *         node has "private" or "final" modifier and false otherwise.
	 */
	private boolean isPrivateOrFinal(final DetailAST aMethodDefAST) {

		boolean result = false;
		final DetailAST modifiers = aMethodDefAST
				.findFirstToken(TokenTypes.MODIFIERS);

		if (modifiers != null && modifiers.getChildCount() != 0) {
			for (DetailAST curNode : getChildren(modifiers)) {
				if (curNode.getType() == TokenTypes.LITERAL_PRIVATE
						|| curNode.getType() == TokenTypes.FINAL) {
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
     * @param aMethodNode
     *            A METHOD_DEF or METHOD_CALL DetailAST node for current method.
     * @return The parent CLASS_DEF node for the class that owns a METHOD_CALL
     *         node named aMethodNode.
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

    /**
     * Checks that the class which owns a current processed METHOD_CALL
     * DetailAST node is an interface.
     * 
     * @param aMethodDefNode
     *            A METHOD_DEF DetailAST node to check.
     * @return true if the class which owns a current processed METHOD_CALL
     *         DetailAST node is an interface and false otherwise.
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
     * implements this interface).
     * 
     * @param aClassDefNode
     *            A CLASS_DEF DetailAST for class is currently being checked.
     * @param aInterfaceName
     *            The name of the interface to check.
     * @return true if lass realizes "anInterfaceName" interface and false
     *         otherwise.
     */
    private boolean realizesAnInterface(final DetailAST aClassDefNode,
            final String aInterfaceName) {

        boolean result = false;
        final List<DetailAST> classWithBaseClasses = getBaseClasses(aClassDefNode);

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
     * @return true if class is related to the current CLASS_DEF DetailAST is
     *         being processed implements "anInterfaceName" interface and false
     *         otherwise.
     */
    private boolean implementsAnInterface(final DetailAST aClassDefNode,
            final String aInterfaceName) {

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
     * Gets the list of CLASS_DEF DetailAST nodes that are associated with class
     * is currently being processed and all it`s base classes.
     * 
     * @param aClassDefNode
     *            A CLASS_DEF DetailAST is related to the class is currently
     *            being processed.
     * @return a list of CLASS_DEF DetailAST nodes for class is currently being
     *         processed and all it`s base classes.
     */
    private List<DetailAST> getBaseClasses(final DetailAST aClassDefNode) {

        final List<DetailAST> result = new LinkedList<DetailAST>();
        String baseClassName = getBaseClassName(aClassDefNode);

        if (baseClassName != null) {
            DetailAST curClass = getClassDefByName(mTreeRootAST, baseClassName);
            while (curClass != null) {
                result.add(curClass);
                baseClassName = getBaseClassName(curClass);
                if (baseClassName != null) {
                    curClass = getClassDefByName(mTreeRootAST, baseClassName);
                } else {
                    break;
                }
            }
        }
        return result;
    }

    /**
     * Gets the CLASS_DEF DetailAST node for the class is named "aClassName".
     * 
     * @param aRootNode
     *            A root node of synthax tree is being processed.
     * @param aClassName
     *            Name of the class to search.
     * @return The CLASS_DEF DetailAST node which is related to the class is
     *         named "aClassName".
     */
    private DetailAST getClassDefByName(DetailAST aRootNode, String aClassName) {

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
            
            if (curNode == null) {
                break;
            }
            
            if (curNode.getType() == TokenTypes.CLASS_DEF
                    && curNode.findFirstToken(TokenTypes.IDENT).getText()
                            .equals(aClassName)) {
                result = curNode;
                break;
            }
        }
        return result;
    }

    /**
     * Gets the the base class name for current class.
     * 
     * @param aClassDefNode
     *            A CLASS_DEF DetailAST.
     * @return The name of a base class for current class.
     */
    private String getBaseClassName(final DetailAST aClassDefNode) {

        String result = null;
        final DetailAST extendsClause = aClassDefNode
                .findFirstToken(TokenTypes.EXTENDS_CLAUSE);

        if (extendsClause != null) {
            final DetailAST dot = extendsClause.findFirstToken(TokenTypes.DOT);
            if (dot != null) {
                result = dot.findFirstToken(TokenTypes.IDENT).getText();
            } else {
                result = extendsClause.findFirstToken(TokenTypes.IDENT)
                        .getText();
            }
        }
        return result;
    }

    /**
     * Gets all the children one level below on the current DetailAST parent
     * node.
     * 
     * @param aNode
     *            Current parent node.
     * @return An array of children one level below on the current parent node
     *         aNode.
     */
    private List<DetailAST> getChildren(final DetailAST aNode) {
        final List<DetailAST> result = new LinkedList<DetailAST>();
        DetailAST curNode = aNode.getFirstChild();
        while (curNode != null) {
            result.add(curNode);
            curNode = curNode.getNextSibling();
        }
        return result;
    }

}
