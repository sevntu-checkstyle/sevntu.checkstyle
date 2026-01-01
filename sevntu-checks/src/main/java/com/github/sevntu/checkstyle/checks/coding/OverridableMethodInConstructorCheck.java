///////////////////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code and other text files for adherence to a set of rules.
// Copyright (C) 2001-2026 the original author or authors.
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
///////////////////////////////////////////////////////////////////////////////////////////////

package com.github.sevntu.checkstyle.checks.coding;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.github.sevntu.checkstyle.SevntuUtil;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Detects overridable methods in constructors.
 * <p>
 * This check prevents any calls to overridable methods that are take place in:
 * </p>
 * <ol><li>
 * Any constructor body (verification is always done by default and not
 * configurable).
 * <li>
 * Any method which works same as a constructor: clone() method from Cloneable
 * interface and readObject() method from Serializable interface (you can
 * individually switch on/off these methods verification by changing
 * CheckCloneMethod and CheckReadObjectMethod properties).</li>
 * </ol>
 * Rationale:
 * <ol>
 * <li>Constructors must not invoke overridable methods, directly or
 * indirectly. If you violate this rule, program failure will result. The
 * superclass constructor runs before the subclass constructor, so the
 * overriding method in the subclass will be invoked before the subclass
 * constructor has run. If the overriding method depends on any
 * initialization performed by the subclass constructor, the method will
 * not behave as expected.
 * <li>If you do decide to implement Cloneable or Serializable in a class
 * designed for inheritance, you should be aware that because the clone and
 * readObject methods behave a lot like constructors, a similar restriction
 * applies: neither clone nor readObject may invoke an overridable method,
 * directly or indirectly.
 * </ol>
 * [Joshua Bloch - Effective Java 2nd Edition, Chapter 4, Item 17]
 * <br> Here's an example to illustrate: <pre>
 * public class Example {
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
 * }</pre>
 * <p>
 * Here, when Base constructor calls overrideMe, Child has not finished
 * initializing the final int x, and the method gets the wrong value. This will
 * almost certainly lead to bugs and errors.
 * </p>
 * <p>
 * <i><b>Notes:</b><br><br>This check doesn`t handle the situation when there
 * is a call to an overloaded method(s).</i><br>Here`s an example:
 * </p>
 *
 * <pre> public class Test {
 *
 *   public static void main(String[] args) {
 *
 *       class Base {
 *           Base() {
 *               System.out.println("Base C-tor ");
 *               overrideMe("Foo!"); // no warnings here, because the method
 *                                   // named "overrideMe" is overloaded.
 *           }
 *           void overrideMe() { }
 *           void overrideMe(String str) {
 *               System.out.println("Base overrideMe(String str) ");
 *           }
 *       }
 *
 *       class Child extends Base {
 *           final int x;
 *           Child(int x) {
 *               this.x = x;
 *           }
 *           void overrideMe(String str) {
 *               System.out.println("Child`s overrideMe(): " + x);
 *           }
 *       }
 *     new Child(999);
 *   }
 * } </pre>
 *
 * <p><br>
 * <i>Some specific method call types that aren`t supported by check:</i>
 * </p>
 * <ul>
 * <li>BaseClass.InnerClass.this.methodName();</li>
 * <li>InnerClass.this.methodName();</li>
 * <li>and so on, using a similar hierarchy</li>
 * </ul>
 * <br>
 *
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil
 *         Yaroslavtsev</a>
 * @author <a href="mailto:IliaDubinin91@gmail.com">Ilja Dubinin</a>
 * @since 1.8.0
 */
public class OverridableMethodInConstructorCheck extends AbstractCheck {

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     * */
    public static final String MSG_KEY = "overridable.method";

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     * */
    public static final String MSG_KEY_LEADS = "overridable.method.leads";

    /**
     * A key is using to build a warning message about calls of an overridable
     * methods from any constructor body.
     * */
    private static final String KEY_CTOR = "constructor";

    /**
     * A key is using to build a warning message about calls of an overridable
     * methods from any clone() method is implemented from Cloneable interface.
     * */
    private static final String KEY_CLONE = "'clone()' method";

    /**
     * A key is using to build a warning message about calls of an overridable
     * methods from any readObject() method is implemented from Serializable
     * interface.
     * */
    private static final String KEY_READ_OBJECT = "'readObject()' method";

    /** String representation of this keyword. */
    private static final String LITERAL_THIS = "this";

    /** Path string to separate layers of packages. */
    private static final String PATH_SEPARATOR = ".";

    /**
     * A list contains all METHOD_CALL DetailAST nodes that have been already
     * visited by check.
     * */
    private final List<DetailAST> visitedMethodCalls = new LinkedList<>();

    /**
     * A current MethodDef AST is being processed by check.
     * */
    private DetailAST curMethodDef;

    /**
     * A current root of the syntax tree is being processed.
     * */
    private DetailAST treeRootAST;

    /**
     * A boolean check box that enables the searching of calls to overridable
     * methods from the body of any clone() method is implemented from Cloneable
     * interface.
     * */
    private boolean checkCloneMethod;

    /**
     * A boolean check box that enables the searching of calls to overridable
     * methods from the body of any readObject() method is implemented from
     * Serializable interface.
     */
    private boolean checkReadObjectMethod;

    /**
     * A boolean check box that enables the matching methods by number of
     * their parameters.
     */
    private boolean matchMethodsByArgCount;

    /**
     * The name of current overridable method is being processed.
     */
    private String curOverridableMetName;

    /**
     * Method definitions counter for class is currently being processed.
     */
    private int curMethodDefCount;

    /**
     * Enable|Disable searching of calls to overridable methods from body of any
     * clone() method is implemented from Cloneable interface.
     *
     * @param value
     *            The state of a boolean check box that enables the searching of
     *            calls to overridable methods from body of any clone() method
     *            is implemented from Cloneable interface.
     */
    public void setCheckCloneMethod(final boolean value) {
        checkCloneMethod = value;
    }

    /**
     * Enable|Disable matching methods by arguments count.
     *
     * @param value
     *            The state of a boolean check box that enables the matching of
     *            methods by arguments count.
     */
    public void setMatchMethodsByArgCount(final boolean value) {
        matchMethodsByArgCount = value;
    }

    /**
     * Enable|Disable searching of calls to overridable methods from body of any
     * readObject() method is implemented from Serializable interface.
     *
     * @param value
     *            The state of a boolean check box that enables the searching of
     *            calls to overridable methods from body of any readObject()
     *            method is implemented from Serializable interface.
     */
    public void setCheckReadObjectMethod(final boolean value) {
        checkReadObjectMethod = value;
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {TokenTypes.CTOR_DEF, TokenTypes.METHOD_DEF};
    }

    @Override
    public int[] getAcceptableTokens() {
        return getDefaultTokens();
    }

    @Override
    public int[] getRequiredTokens() {
        return getDefaultTokens();
    }

    @Override
    public void beginTree(DetailAST rootAST) {
        treeRootAST = rootAST;
    }

    @Override
    public void visitToken(final DetailAST detailAST) {
        final DetailAST classDef = getClassDef(detailAST);

        if (classDef != null && !hasModifier(classDef, TokenTypes.FINAL)) {
            switch (detailAST.getType()) {
                case TokenTypes.CTOR_DEF:
                    logWarnings(detailAST, KEY_CTOR);
                    break;

                case TokenTypes.METHOD_DEF:

                    final String methodName = detailAST.findFirstToken(
                        TokenTypes.IDENT).getText();

                    if (checkCloneMethod && "clone".equals(methodName)
                        && realizesAnInterface(classDef, Cloneable.class.getSimpleName())) {
                        logWarnings(detailAST, KEY_CLONE);
                    }
                    else if (checkReadObjectMethod
                        && "readObject".equals(methodName)
                        && realizesAnInterface(classDef, Serializable.class.getSimpleName())) {
                        logWarnings(detailAST, KEY_READ_OBJECT);
                    }
                    break;

                default:
                    SevntuUtil.reportInvalidToken(detailAST.getType());
                    break;
            }
        }
    }

    /**
     * Gets all METHOD_CALL DetailASTs that are pointing to overridable methods
     * calls from current method or c-tor body and logs them.
     *
     * @param detailAST
     *            A DetailAST node which is pointing to current method or c-tor
     *            body is being processed to retrieve overridable method calls
     *            list.
     * @param key
     *            A string is using to retrieve the warning message text from
     *            "messages.properties" file.
     */
    private void logWarnings(final DetailAST detailAST, final String key) {
        final List<OverridableMetCall> methodCallsToWarnList =
            getOverridables(detailAST);

        for (OverridableMetCall omc : methodCallsToWarnList) {
            final DetailAST methodDef = getMethodDef(omc.metCallAST);
            if (hasModifier(methodDef, TokenTypes.LITERAL_PRIVATE)
                    || hasModifier(methodDef, TokenTypes.FINAL)) {
                log(omc.metCallAST, MSG_KEY_LEADS, getMethodName(omc.metCallAST),
                        key, omc.overridableMetName);
            }
            else {
                log(omc.metCallAST, MSG_KEY, getMethodName(omc.metCallAST),
                    key, omc.overridableMetName);
            }
        }
    }

    /**
     * Searches for all METHOD_CALL DetailASTs that are pointing to overridable
     * methods calls in current method or c-tor body and generates a list of
     * them.
     *
     * @param parentAST
     *            A DetailAST METHOD_DEF of CTOR_DEF node which is pointing to
     *            the current method or c-tor body is being processed to
     *            retrieve overridable method calls.
     * @return A list of overridable methods calls for current method or
     *         constructor body.
     */
    private List<OverridableMetCall> getOverridables(final DetailAST parentAST) {
        final List<OverridableMetCall> result =
            new LinkedList<>();
        final List<DetailAST> methodCallsList = getMethodCallsList(parentAST);

        for (DetailAST curNode : methodCallsList) {
            visitedMethodCalls.clear();
            final DetailAST methodDef = getMethodDef(curNode);
            if (methodDef != null
                    && getMethodParamsCount(curNode)
                        == getMethodParamsCount(methodDef)
                    && isOverridableMethodCall(curNode)) {
                result.add(new OverridableMetCall(curNode,
                        curOverridableMetName));
            }
        }
        return result;
    }

    /**
     * Checks that current processed METHOD_CALL DetailAST is pointing to
     * overridable method call.
     *
     * @param methodCallAST
     *            A METHOD_CALL DetailAST is currently being processed.
     * @return true if current processed METHOD_CALL node is pointing to the
     *         overridable method call and false otherwise.
     */
    private boolean isOverridableMethodCall(final DetailAST methodCallAST) {
        boolean result = false;
        visitedMethodCalls.add(methodCallAST);

        final String methodName = getMethodName(methodCallAST);
        final DetailAST methodDef = getMethodDef(methodCallAST);

        if (methodName != null
                && methodDef != null && !hasModifier(methodDef, TokenTypes.LITERAL_STATIC)) {
            if (hasModifier(methodDef, TokenTypes.LITERAL_PRIVATE)
                || hasModifier(methodDef, TokenTypes.FINAL)) {
                final List<DetailAST> methodCallsList = getMethodCallsList(
                        methodDef);
                for (DetailAST curNode : methodCallsList) {
                    if (!visitedMethodCalls.contains(curNode)
                            && isOverridableMethodCall(curNode)) {
                        result = true;
                        break;
                    }
                }
            }
            else {
                curOverridableMetName = methodName;
                result = true;
            }
        }
        return result;
    }

    /**
     * Gets all METHOD_CALL nodes which are below on the current parent
     * METHOD_DEF or CTOR_DEF node.
     *
     * @param parentAST
     *            The current parent METHOD_DEF or CTOR_DEF node.
     * @return List contains all METHOD_CALL nodes which are below on the
     *         current parent node.
     */
    private List<DetailAST> getMethodCallsList(final DetailAST parentAST) {
        final List<DetailAST> result = new LinkedList<>();

        for (DetailAST curNode : getChildren(parentAST)) {
            if (curNode.getFirstChild() != null) {
                if (curNode.getType() == TokenTypes.METHOD_CALL) {
                    result.add(curNode);
                }
                else {
                    result.addAll(getMethodCallsList(curNode));
                }
            }
        }
        return result;
    }

    /**
     * Gets the method name is related to the current METHOD_CALL DetailAST.
     *
     * @param methodCallAST
     *            A METHOD_CALL DetailAST node is currently being processed.
     * @return The method name is related to the current METHOD_CALL DetailAST.
     */
    private String getMethodName(final DetailAST methodCallAST) {
        String result = null;

        final DetailAST ident = methodCallAST.findFirstToken(TokenTypes.IDENT);

        if (ident == null) {
            final DetailAST childAST = methodCallAST.getFirstChild();

            if (childAST != null && childAST.getType() == TokenTypes.DOT) {
                final DetailAST firstChild = childAST.getFirstChild();
                final DetailAST lastChild = childAST.getLastChild();

                if (firstChild.getType() == TokenTypes.LITERAL_THIS
                        || firstChild.getType() == TokenTypes.LPAREN
                        || firstChild.getType() == TokenTypes.DOT) {
                    result = lastChild.getText();
                }
                else if (firstChild.getType() == TokenTypes.IDENT
                        && lastChild.getType() == TokenTypes.IDENT) {
                    final String curClassName = getClassDef(methodCallAST)
                            .findFirstToken(TokenTypes.IDENT).getText();
                    if (firstChild.getText().equals(curClassName)
                            || getClassDef(treeRootAST,
                                    firstChild.getText()) != null) {
                        result = lastChild.getText();
                    }
                }
            }
        }
        else {
            result = ident.getText();
        }
        return result;
    }

    /**
     * Gets the method definition is related to the current METHOD_CALL
     * DetailAST node. If method definition doesn't find, will returned null.
     *
     * @param methodCallAST
     *            A METHOD_CALL DetailAST node is currently being processed.
     * @return the METHOD_DEF DetailAST node is pointing to the method
     *         definition which is related to the current METHOD_CALL DetailAST
     *         node.
     */
    private DetailAST getMethodDef(final DetailAST methodCallAST) {
        DetailAST result = null;

        curMethodDef = null;
        curMethodDefCount = 0;

        final String methodName = getMethodName(methodCallAST);
        if (methodName != null) {
            final DetailAST curClassAST = getClassDef(methodCallAST);
            final DetailAST callsChild = methodCallAST.getFirstChild();
            final String variableTypeName = getVariableType(methodCallAST);

            if (variableTypeName == null
                    || callsChild.getType() != TokenTypes.DOT
                    || isItTypeOfCurrentClass(variableTypeName, curClassAST)
                    || isItCallMethodViaKeywordThis(variableTypeName, curClassAST)) {
                getMethodDef(curClassAST, methodName);
            }

            if (curMethodDefCount == 0) {
                final List<DetailAST> baseClasses = getBaseClasses(curClassAST);

                for (DetailAST curBaseClass : baseClasses) {
                    curMethodDef = null;
                    curMethodDefCount = 0;
                    getMethodDef(curBaseClass, methodName);
                    if (curMethodDefCount == 1) {
                        result = curMethodDef;
                        break;
                    }
                }
            }
            else if (curMethodDefCount == 1) {
                result = curMethodDef;
            }
            else {
                if (matchMethodsByArgCount) {
                    int sameDefinitionCounter = 0;
                    final int curMethodParamCount =
                            getMethodParamsCount(methodCallAST);
                    for (DetailAST currentDefinition : getMethodDef(curClassAST, methodName)) {
                        if (getMethodParamsCount(currentDefinition) == curMethodParamCount) {
                            result = currentDefinition;
                            sameDefinitionCounter++;
                        }
                    }
                    // you have a lot same method definitions and you can't
                    // select one of them and be sure that you are right
                    if (sameDefinitionCounter > 1) {
                        result = null;
                    }
                }
            }
        }
        return result;
    }

    /**
     * Gets the method definition is related to the current METHOD_CALL
     * DetailAST using the name of method to be searched. Ignores overloaded
     * methods (retrieves only one METHOD_DEF node).
     *
     * @param parentAST
     *            A parent CLASS_DEF DetailAST node which uses as a start point
     *            when searching.
     * @param methodName
     *            String containing the name of method is currently being
     *            searched.
     * @return a List of method definition
     */
    private List<DetailAST> getMethodDef(final DetailAST parentAST,
            final String methodName) {
        List<DetailAST> definitionsList = new LinkedList<>();

        for (DetailAST curNode : getChildren(parentAST)) {
            if (curNode.getFirstChild() != null) {
                if (curNode.getType() == TokenTypes.METHOD_DEF) {
                    final String curMethodName = curNode.findFirstToken(
                            TokenTypes.IDENT).getText();
                    if (methodName.equals(curMethodName)) {
                        curMethodDef = curNode;
                        definitionsList.add(0, curNode);
                        curMethodDefCount++;
                    }
                }

                final int type = curNode.getType();

                if (type != TokenTypes.CLASS_DEF && type != TokenTypes.CTOR_DEF
                        && type != TokenTypes.MODIFIERS
                        && type != TokenTypes.IMPLEMENTS_CLAUSE
                        && type != TokenTypes.METHOD_DEF) {
                    definitionsList = getMethodDef(curNode, methodName);
                }
            }
        }
        return definitionsList;
    }

    /**
     * Return type of the variable, if it is declaration procedure.
     *
     * @param methodCall The token to examine.
     * @return variables type name
     */
    private static String getVariableType(DetailAST methodCall) {
        final DetailAST callsChild = methodCall.getFirstChild();
        String typeName = "";
        if (callsChild.getType() == TokenTypes.DOT) {
            final DetailAST dotChild = callsChild.getFirstChild();
            if (dotChild.getType() == TokenTypes.LITERAL_THIS) {
                typeName = LITERAL_THIS;
            }
            else if (callsChild.getChildCount(TokenTypes.TYPECAST) > 0) {
                final DetailAST typeCast = callsChild
                        .findFirstToken(TokenTypes.TYPECAST);
                final DetailAST type = typeCast.getFirstChild().getFirstChild();
                typeName = type.getText();
            }
            else if (dotChild.getType() == TokenTypes.DOT) {
                typeName = dotChild.getFirstChild().getText()
                        + PATH_SEPARATOR + dotChild.getLastChild().getText();
            }
        }
        return typeName;
    }

    /**
     * Return true when usedIbjectName contains current class name or base class
     * name.
     *
     * @param objectTypeName The object type name to check against.
     * @param classDefNode The token to examine.
     * @return true if the type is the same as the current class.
     */
    private static boolean
            isItTypeOfCurrentClass(String objectTypeName, DetailAST classDefNode) {
        final DetailAST className = classDefNode.findFirstToken(TokenTypes.IDENT);
        boolean result = false;
        if (objectTypeName.equals(className.getText())) {
            result = true;
        }
        else {
            DetailAST baseClass = classDefNode.findFirstToken(
                    TokenTypes.EXTENDS_CLAUSE);
            if (baseClass != null) {
                baseClass = baseClass.getFirstChild();

                if (objectTypeName.equals(baseClass.getText())) {
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * Return true when the method called via keyword "this" (this.methodName
     * or ClassName.this.methodName)
     *
     * @param firstPartOfTheMethodCall The first part of the method call.
     * @param classDefNode The token to examine.
     * @return If the method is called via keyword "this".
     */
    private static boolean
            isItCallMethodViaKeywordThis(String firstPartOfTheMethodCall, DetailAST classDefNode) {
        final String className = classDefNode.findFirstToken(TokenTypes.IDENT).getText();
        // -@cs[EqualsAvoidNull] need parenthesis around '+' otherwise PMD will complain
        return LITERAL_THIS.equals(firstPartOfTheMethodCall)
                || firstPartOfTheMethodCall.equals(className + PATH_SEPARATOR + LITERAL_THIS);
    }

    /**
     * Gets the count of parameters for current method definition or
     * method call.
     *
     * @param methodDefOrCallAST METHOD_DEF or METHOD_CALL
     *     DetailAST node
     * @return the count of parameters for current method.
     */
    private static int getMethodParamsCount(DetailAST methodDefOrCallAST) {
        int result = 0;
        DetailAST paramsParentAST = null;

        if (methodDefOrCallAST.getType() == TokenTypes.METHOD_CALL) {
            paramsParentAST = methodDefOrCallAST
                    .findFirstToken(TokenTypes.ELIST);
        }
        else if (methodDefOrCallAST.getType() == TokenTypes.METHOD_DEF) {
            paramsParentAST = methodDefOrCallAST
                    .findFirstToken(TokenTypes.PARAMETERS);
        }

        if (paramsParentAST != null && paramsParentAST.getChildCount() != 0) {
            for (DetailAST curNode : getChildren(paramsParentAST)) {
                if (curNode.getType() == TokenTypes.COMMA) {
                    result++;
                }
            }
            result++;
        }
        return result;
    }

    /**
     * Checks that method or class is related to the current METHOD_DEF or
     * CLASS_DEF DetailAST node has a specified modifier (private, final etc).
     *
     * @param methodOrClassDefAST
     *            A METHOD_DEF or CLASS_DEF DetailAST node is currently being
     *            processed.
     * @param modifierType
     *            desired modifier type.
     * @return true if method is related to current aMethodDefAST METHOD_DEF
     *         node has "private" or "final" modifier and false otherwise.
     */
    private static boolean hasModifier(final DetailAST methodOrClassDefAST,
        int modifierType) {
        boolean result = false;
        final DetailAST modifiers = methodOrClassDefAST
                .findFirstToken(TokenTypes.MODIFIERS);

        if (modifiers != null && modifiers.getChildCount() != 0) {
            for (DetailAST curNode : getChildren(modifiers)) {
                if (curNode.getType() == modifierType) {
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
     * @param methodNode
     *            A METHOD_DEF or METHOD_CALL DetailAST node for current method.
     * @return The parent CLASS_DEF node for the class that owns a METHOD_CALL
     *         node named aMethodNode.
     * */
    private static DetailAST getClassDef(final DetailAST methodNode) {
        DetailAST curNode = methodNode;

        while (curNode != null && curNode.getType() != TokenTypes.CLASS_DEF) {
            curNode = curNode.getParent();
        }

        return curNode;
    }

    /**
     * Gets the CLASS_DEF DetailAST node for the class is named "aClassName".
     *
     * @param rootNode
     *            A root node of syntax tree is being processed.
     * @param className
     *            The name of class to search.
     * @return The CLASS_DEF DetailAST node which is related to the class is
     *         named "aClassName".
     */
    private static DetailAST getClassDef(DetailAST rootNode, String className) {
        DetailAST curNode = rootNode;

        while (curNode != null) {
            DetailAST toVisit = curNode.getFirstChild();
            while (curNode != null && toVisit == null) {
                toVisit = curNode.getNextSibling();
                if (toVisit == null) {
                    curNode = curNode.getParent();
                }
            }

            curNode = toVisit;

            if (curNode != null
                    && curNode.getType() == TokenTypes.CLASS_DEF
                    && curNode.findFirstToken(TokenTypes.IDENT).getText()
                            .equals(className)) {
                break;
            }
        }
        return curNode;
    }

    /**
     * Checks that class realizes "anInterfaceName" interface (checks that class
     * implements this interface or has at least one parent class which
     * implements this interface).
     *
     * @param classDefNode
     *            A CLASS_DEF DetailAST for class is currently being checked.
     * @param interfaceName
     *            The name of the interface to check.
     * @return true if class realizes "anInterfaceName" interface and false
     *         otherwise.
     */
    private boolean realizesAnInterface(final DetailAST classDefNode,
            final String interfaceName) {
        boolean result = false;
        final List<DetailAST> classWithBaseClasses =
                getBaseClasses(classDefNode);

        classWithBaseClasses.add(classDefNode);

        for (DetailAST classAST : classWithBaseClasses) {
            if (implementsAnInterface(classAST, interfaceName)) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Checks that class implements "anInterfaceName" interface.
     *
     * @param classDefAST
     *            A CLASS_DEF DetailAST for class is currently being checked.
     * @param interfaceName
     *            The name of the interface to check.
     * @return true if class is related to the current CLASS_DEF DetailAST is
     *         being processed implements "anInterfaceName" interface and false
     *         otherwise.
     */
    private static boolean implementsAnInterface(final DetailAST classDefAST,
            final String interfaceName) {
        boolean result = false;
        final DetailAST implClause = classDefAST
                .findFirstToken(TokenTypes.IMPLEMENTS_CLAUSE);

        if (implClause != null) {
            for (DetailAST ident : getChildren(implClause)) {
                if (ident.getText().equals(interfaceName)) {
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
     * @param classDefNode
     *            A CLASS_DEF DetailAST is related to the class is currently
     *            being processed.
     * @return a list of CLASS_DEF DetailAST nodes for class is currently being
     *         processed and all it`s base classes.
     */
    private List<DetailAST> getBaseClasses(final DetailAST classDefNode) {
        final List<DetailAST> result = new LinkedList<>();
        String baseClassName = getBaseClassName(classDefNode);

        if (baseClassName != null) {
            DetailAST curClass = getClassDef(treeRootAST, baseClassName);
            while (curClass != null) {
                result.add(curClass);
                baseClassName = getBaseClassName(curClass);
                if (baseClassName == null) {
                    break;
                }

                final DetailAST nextClass = getClassDef(treeRootAST, baseClassName);

                // prevent infinite loop with similar named classes
                if (nextClass == curClass) {
                    curClass = null;
                }
                else {
                    curClass = nextClass;
                }
            }
        }
        return result;
    }

    /**
     * Gets the the base class name for current class.
     *
     * @param classDefNode
     *            A CLASS_DEF DetailAST.
     * @return The name of a base class for current class.
     */
    private static String getBaseClassName(final DetailAST classDefNode) {
        String result = null;
        final DetailAST extendsClause = classDefNode
                .findFirstToken(TokenTypes.EXTENDS_CLAUSE);

        if (extendsClause != null) {
            final DetailAST dot = extendsClause.findFirstToken(TokenTypes.DOT);
            if (dot == null) {
                result = extendsClause.findFirstToken(TokenTypes.IDENT)
                        .getText();
            }
            else {
                result = dot.findFirstToken(TokenTypes.IDENT).getText();
            }
        }
        return result;
    }

    /**
     * Gets all the children one level below on the current DetailAST parent
     * node.
     *
     * @param node
     *            Current parent node.
     * @return An array of children one level below on the current parent node
     *         aNode.
     */
    private static List<DetailAST> getChildren(final DetailAST node) {
        final List<DetailAST> result = new LinkedList<>();
        DetailAST curNode = node.getFirstChild();
        while (curNode != null) {
            result.add(curNode);
            curNode = curNode.getNextSibling();
        }
        return result;
    }

    /**
     * Class that encapsulates the DetailAST node related to the method call
     * that leads to call of the overridable method and the name of
     * overridable method.
     */
    private final class OverridableMetCall {

        /**
         * DetailAST node is related to the method call that leads to
         *           call of the overridable method.
         */
        private final DetailAST metCallAST;
        /**
         * The name of an overridable method.
         */
        private final String overridableMetName;

        /**
         * Creates an instance of OverridableMetCall and initializes fields.
         *
         * @param methodCallAST
         *            DetailAST node related to the method call that leads
         *            to call of the overridable method.
         * @param overridableMetName
         *            The name of an overridable method.
         */
        /* package */ OverridableMetCall(DetailAST methodCallAST,
                String overridableMetName) {
            metCallAST = methodCallAST;
            this.overridableMetName = overridableMetName;
        }

    }

}
