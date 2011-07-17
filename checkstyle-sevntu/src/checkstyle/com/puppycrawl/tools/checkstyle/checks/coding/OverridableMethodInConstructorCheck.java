package com.puppycrawl.tools.checkstyle.checks.coding;

import java.util.LinkedList;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.api.Check;

/**
 * <p>
 * This check prevents calls of overridable methods from constructor body, from
 * clone() method (when implementing Cloneable interface) and from readObject()
 * method (when implementing Serializable interface).
 *
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil
 *         Yaroslavtsev</a>
 */
public class OverridableMethodInConstructorCheck extends Check {

    /**
     * A key to search the warning message text for overridable methods calls from constructor body in "messages.properties" file.
     * */
    private final String mKeyCtor = "overridable.method.in.constructor";
    /**
     * A key to search the warning message text for overridable methods calls
     * from clone() method in "messages.properties" file.
     * */
    private final String mKeyClone = "overridable.method.in.clone";
    /**
     * A key to search the warning message text for overridable methods calls
     * from readObject() method in "messages.properties" file.
     * */
    private final String mKeyReadObject = "overridable.method.in.readobject";

    /**
     * MethodDef AST is currently being processed by check.
     * */
    private DetailAST mCurMethodDef;

    /**
     * A root AST for the current synthax tree.
     * */
    private DetailAST mTreeRootAST;

    /** Check overridable methods calls in clone() method
    * from Cloneable interface. */
    private boolean mCheckCloneMethod = true;

    /** Check overridable methods calls in readObject() method from
     *  Serializable interface.*/
    private boolean mCheckReadObjectMethod = true;

    /**
     * Enable|Disable searching calls of overridable methods in clone() method
     * from Cloneable interface.
     * 
     * @param aValue
     *            check calls of overridable methods in clone() method from
     *            Cloneable interface.
     */
    public void setCheckCloneMethod(final boolean aValue)
    {
        mCheckCloneMethod = aValue;
    }

    /**
     * Enable|Disable searching calls of overridable methods in readObject()
     * method from Serializable interface.
     * @param aValue
     *            check calls of overridable methods in readObject() method from
     *            Serializable interface.
     */
    public void setCheckReadObjectMethod(final boolean aValue)
    {
        mCheckReadObjectMethod = aValue;
    }

    @Override
    public final int[] getDefaultTokens() {

        if (!mCheckCloneMethod && !mCheckReadObjectMethod) {
            return new int[] { TokenTypes.CTOR_DEF };
        } else {
            return new int[] { TokenTypes.CTOR_DEF, TokenTypes.METHOD_DEF };
        }
    }

    @Override
    public final void beginTree(DetailAST aRootAST) {
        mTreeRootAST = aRootAST;
    }

    @Override
    public final void visitToken(final DetailAST aDetailAST) {

        switch (aDetailAST.getType()) {

        case TokenTypes.CTOR_DEF: // for all CTOR_DEF nodes
            verify(aDetailAST, mKeyCtor);
            break;

        case TokenTypes.METHOD_DEF: // for all METHOD_DEF nodes

            String methodName = aDetailAST.findFirstToken(TokenTypes.IDENT)
                    .getText();

            // if called in clone() from Cloneable i-face
            if ("clone".equals(methodName) && mCheckCloneMethod
                    && realizesAnInterface(getClass(aDetailAST), "Cloneable")) {
                verify(aDetailAST, mKeyClone);
            }

            // if called in readObject() from Serializable i-face
            else if ("readObject".equals(methodName)
                    && mCheckReadObjectMethod
                    && realizesAnInterface(getClass(aDetailAST), "Serializable")) {
                verify(aDetailAST, mKeyReadObject);
            }

            break;
        }
    }

    public final void verify(final DetailAST aDetailAST, final String aKey) {

        LinkedList<DetailAST> methodCallsToWarnList = getOverridables(aDetailAST);

        for (DetailAST methodCallAST : methodCallsToWarnList) {
            String curMethodName = getMethodName(methodCallAST);
            log(methodCallAST, aKey, curMethodName);
        }
    }

    // try to find overridable method calls
    // all levels below on the current parentNode.
    public final LinkedList<DetailAST> getOverridables(
            final DetailAST aParentAST) {

        LinkedList<DetailAST> result = new LinkedList<DetailAST>();
        LinkedList<DetailAST> methodCallsList = getMethodCallsList(aParentAST);

        for (DetailAST curNode : methodCallsList) {
            if (isCallToOverridableMethod(curNode)) {
                result.add(curNode);
            }
        }
        return result;
    }

    // gets a DetailAST for the first method_call, which calls an overridable
    // method
    public final boolean isCallToOverridableMethod(
            final DetailAST aMethodCallAST) {

        String methodName = getMethodName(aMethodCallAST);

        if (methodName != null) {
            // TODO: I must find all methods named 'methodname' in the same
            // class, not one instance

            DetailAST methodDef = getMethodDef(aMethodCallAST);

            if (methodDef != null) {

                if (isPrivateOrFinal(methodDef)) {
                    LinkedList<DetailAST> methodCallsList = getMethodCallsList(methodDef);
                    for (DetailAST curNode : methodCallsList) {
                        if (isCallToOverridableMethod(curNode)) {
                            return true;
                        }
                    }

                } else {
                    return true;
                }
            }
        }
        return false;
    }

    public final LinkedList<DetailAST> getMethodCallsList(
            final DetailAST aParentAST) {
        return getMethodCalls(aParentAST, new LinkedList<DetailAST>());
    }

    public final LinkedList<DetailAST> getMethodCalls(
            final DetailAST ParentAST, LinkedList<DetailAST> curResultList) {

        for (DetailAST curNode : getChildren(ParentAST)) {

            if (curNode.getNumberOfChildren() > 0) {

                if (curNode.getType() == TokenTypes.METHOD_CALL) {

                    if (!isCallingItself(curNode)) {
                        curResultList.add(curNode);
                    }

                } else {
                    getMethodCalls(curNode, curResultList);
                }
            }
        }
        return curResultList;
    }

    public final String getMethodName(final DetailAST aMethodCallAST) {

        String result = null;

        DetailAST ident = aMethodCallAST.findFirstToken(TokenTypes.IDENT);

        if (ident != null) { // explicit call to a method ?
            result = ident.getText();
        }

        else { // blablabla.methodCall() ?

            DetailAST childAST = aMethodCallAST.getFirstChild();

            if (childAST != null && childAST.getType() == TokenTypes.DOT) {

                DetailAST firstChild = childAST.getFirstChild();
                DetailAST lastChild = childAST.getLastChild();

                if (firstChild.getType() == TokenTypes.LITERAL_THIS
                        || firstChild.getType() == TokenTypes.LPAREN) { // this.methodCall()
                                                                        // ??
                    result = lastChild.getText();
                }

                else if (firstChild.getType() == TokenTypes.IDENT
                        && lastChild.getType() == TokenTypes.IDENT) { // thisClass.methodCall()
                                                                      // ??
                    String curClassName = getClass(aMethodCallAST)
                            .findFirstToken(TokenTypes.IDENT).getText();
                    if (firstChild.getText().equals(curClassName)) {
                        result = lastChild.getText();
                    }
                }
            }
        }

        return result;
    }

    public final DetailAST getMethodDef(final DetailAST aMethodCallAST) {
        mCurMethodDef = null;
        String methodName = getMethodName(aMethodCallAST);
        if (methodName != null) {
            getMethodDef(getClass(aMethodCallAST), methodName);
        }
        return mCurMethodDef;
    }

    private final void getMethodDef(final DetailAST aParentAST,
            final String aMethodName) {

        for (DetailAST curNode : getChildren(aParentAST)) {

            if (curNode.getNumberOfChildren() > 0) {
                if (curNode.getType() == TokenTypes.METHOD_DEF) {
                    String curMethodName = curNode.findFirstToken(
                            TokenTypes.IDENT).getText();
                    if (aMethodName.equals(curMethodName)) {
                        mCurMethodDef = curNode;
                        break;
                    }
                }

                int type = curNode.getType();

                if (type != TokenTypes.CTOR_DEF && type != TokenTypes.MODIFIERS
                        && type != TokenTypes.METHOD_DEF) {
                    getMethodDef(curNode, aMethodName);
                }
            }
        }
    }

    public final boolean isPrivateOrFinal(final DetailAST aMethodDefAST) {

        boolean result = false;
        final DetailAST modifiers = aMethodDefAST
                .findFirstToken(TokenTypes.MODIFIERS);

        if (modifiers != null && modifiers.getChildCount() != 0) {
            for (DetailAST curNode : getChildren(modifiers)) {
                if (curNode.getType() == TokenTypes.LITERAL_PRIVATE
                        || curNode.getType() == TokenTypes.FINAL) {
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * Returns a parent CLASS_DEF DetailAST node for aMethodNode METHOD_CALL
     * node.
     * 
     * @return a DetailAST node for the class that owns aMethodNode node.
     * @param aMethodNode
     *            - a current method DetailAST.
     * */
    public final DetailAST getClass(final DetailAST aMethodNode) {
        DetailAST result = null;
        DetailAST curNode = aMethodNode;

        while (curNode != null && curNode.getType() != TokenTypes.CLASS_DEF) {
            curNode = curNode.getParent();
        }

        if (curNode != null && curNode.getType() == TokenTypes.CLASS_DEF) {
            result = curNode;
        }

        return result;
    }

    public final boolean realizesAnInterface(final DetailAST aClassDefNode,
            final String anInterfaceName) {

        boolean result = false;
        LinkedList<DetailAST> classWithBaseClasses = getClassWithBaseClasses(aClassDefNode);

        for (DetailAST classAST : classWithBaseClasses) {
            if (implementsAnInterface(classAST, anInterfaceName)) {
                result = true;
                break;
            }
        }
        return result;
    }

    private final boolean implementsAnInterface(final DetailAST aClassDefNode,
            final String anInterfaceName) {

        boolean result = false;
        DetailAST implementsClause = aClassDefNode
                .findFirstToken(TokenTypes.IMPLEMENTS_CLAUSE);

        if (implementsClause != null) {
            for (DetailAST ident : getChildren(implementsClause)) {
                if (ident.getText().equals(anInterfaceName)) {
                    result = true;
                    break;
                }
            }
        }
        return result;
    }

    public final LinkedList<DetailAST> getClassWithBaseClasses(
            final DetailAST aClassDefNode) {
        LinkedList<DetailAST> result = new LinkedList<DetailAST>();

        DetailAST curClass = aClassDefNode;
        while (curClass != null) {
            result.add(curClass);
            String baseClassName = getBaseClassName(curClass);
            if (baseClassName != null) {
                curClass = getBaseClass(mTreeRootAST, baseClassName);
            } else {
                break;
            }
        }

        return result;
    }

    private DetailAST getBaseClass(DetailAST aRoot, String aClassName) {

        DetailAST curNode = aRoot;

        while (curNode != null) {
            DetailAST toVisit = curNode.getFirstChild();
            while ((curNode != null) && (toVisit == null)) {
                toVisit = curNode.getNextSibling();
                if (toVisit == null) {
                    curNode = curNode.getParent();
                }
            }
            curNode = toVisit;

            if (curNode.getType() == TokenTypes.CLASS_DEF
                    && curNode.findFirstToken(TokenTypes.IDENT).getText()
                            .equals(aClassName)) {
                return curNode;
            }
        }
        return null;
    }

    public final String getBaseClassName(final DetailAST aClassDefNode) {
        String result = null;
        DetailAST extendsClause = aClassDefNode
                .findFirstToken(TokenTypes.EXTENDS_CLAUSE);

        if (extendsClause != null) {
            DetailAST dot = extendsClause.findFirstToken(TokenTypes.DOT);

            if (dot != null) {
                result = dot.findFirstToken(TokenTypes.IDENT).getText();
            } else {
                result = extendsClause.findFirstToken(TokenTypes.IDENT)
                        .getText();
            }
        }

        return result;
    }

    public final boolean isCallingItself(final DetailAST aMethodCallNode) {

        DetailAST methodDef = getMethodDef(aMethodCallNode);
        final DetailAST b = getParentMethodDef(aMethodCallNode);

        if (methodDef == null || b == null) {
            return false;
        } else {
            return methodDef == b;
        }
    }

    public final DetailAST getParentMethodDef(final DetailAST aMethodCallNode) {
        DetailAST result = null;
        DetailAST curNode = aMethodCallNode;

        while (curNode != null && curNode.getType() != TokenTypes.CLASS_DEF
                && curNode.getType() != TokenTypes.METHOD_DEF) {
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
    public final LinkedList<DetailAST> getChildren(final DetailAST aNode) {
        final LinkedList<DetailAST> result = new LinkedList<DetailAST>();
        DetailAST curNode = aNode.getFirstChild();
        while (curNode != null) {
            result.add(curNode);
            curNode = curNode.getNextSibling();
        }
        return result;
    }

}
