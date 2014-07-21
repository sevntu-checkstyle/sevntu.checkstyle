////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2012 Oliver Burn
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
package com.github.sevntu.checkstyle.checks.design;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * This Check warns on propagation of inner private types to outer classes:<br>
 * - Externally accessible method if it returns private inner type.<br>
 * - Externally accessible field if it's type is a private inner type.<br>
 * These types could be <a href=
 * 'http://docs.oracle.com/javase/tutorial/java/javaOO/nested.html'>private
 * inner classes</a>, interfaces or enumerations.<br>
 * <br>
 * Examples: <code>
 * <pre> 
 * class OuterClass {
 *  public InnerClass innerFromMain = new InnerClass(); //WARNING
 *  private class InnerClass { ... } 
 *  public InnerClass  getValue() { //WARNING
 *      return new InnerClass(); 
 *  }
 *  <br> 
 *  private interface InnerInterface { ... }
 *  public Set&ltInnerInterface&gt getValue() { //WARNING
 *      return new TreeSet&ltInnerInterface&gt;;
 *  }
 *  <br>
 *  private Enum Fruit {Apple, Pear}
 *  public Fruit getValue() { //WARNING
 *      return Fruit.Apple;
 *  }
 *  <br>
 *  public someMethod(InnerClass innerClass) { ... }  //WARNING
 *  <br>
 * }
 * </pre>
 * </code>
 * <b>Rationale:</b> it is possible to return<br>
 * private inner type or use it as the parameter of non-private method, but it
 * is impossible<br>
 * to use it in other classes (besides inner classes)<br>
 * unless it extends or implements at least one <u>non-private</u> class or
 * interface.<br>
 * Such situation usually happens after bulk refactoring and usually means
 * dead/useless code<br>
 * </p>
 * <br>
 * @author <a href="mailto:nesterenko-aleksey@list.ru">Aleksey Nesterenko</a>
 */
public class PublicReferenceToPrivateTypeCheck extends Check
{
    /**
     * Check message key for private types
     */
    public final static String MSG_KEY = "public.reference.to.private.type";

    /**
     * List containing names of private types (classes, interfaces or enums).
     */
    private final Set<DetailAST> privateTypes = new HashSet<DetailAST>();

    /**
     * List containing the names of types returned by public methods or fields.
     */
    private final Set<DetailAST> externallyReferencedTypes = new HashSet<DetailAST>();

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] { TokenTypes.CLASS_DEF, TokenTypes.METHOD_DEF,
                TokenTypes.INTERFACE_DEF, TokenTypes.ENUM_DEF,
                TokenTypes.VARIABLE_DEF };
    }

    @Override
    public void beginTree(DetailAST rootAST)
    {
        privateTypes.clear();
        externallyReferencedTypes.clear();
    }

    @Override
    public void visitToken(DetailAST defAst)
    {
        switch (defAst.getType()) {
        case TokenTypes.CLASS_DEF:
        case TokenTypes.INTERFACE_DEF:
        case TokenTypes.ENUM_DEF:
            if (hasModifier(TokenTypes.LITERAL_PRIVATE, defAst)) {
                addPrivateTypes(defAst);
            }
            break;
        case TokenTypes.METHOD_DEF:
            if (isDefinedInTopLevelClass(defAst)
                    && !hasModifier(TokenTypes.LITERAL_PRIVATE, defAst)) {
                addExternallyAccessibleMethodTypes(defAst);
            }
            break;
        case TokenTypes.VARIABLE_DEF:
            if (isDefinedInTopLevelClass(defAst)
                    && !hasModifier(TokenTypes.LITERAL_PRIVATE, defAst)) {
                addExternallyAccessibleFieldTypes(defAst);
            }
            break;
        default:
            throw new IllegalArgumentException("Unsupported token type: "
                    + defAst.getType());
        }
    }

    @Override
    public void finishTree(DetailAST rootAst)
    {
        if (!privateTypes.isEmpty()) {
            for (DetailAST privateType : privateTypes) {
                for (DetailAST outReturnedType : externallyReferencedTypes) {
                    if (privateType.getText().equals(
                            outReturnedType.getText())
                            && !isExtendsOrImplementsSmth(privateType
                                    .getParent())) {
                        log(outReturnedType.getLineNo(), MSG_KEY,
                                outReturnedType.getText());
                    }
                }
            }
        }
    }

    /**
     * Adds type to the list of private types.
     * @param classOrInterfaceOrEnumDefAst
     *        AST subtree that represent inner private type definition.
     */
    private void addPrivateTypes(DetailAST classOrInterfaceOrEnumDefAst)
    {
        final DetailAST definitionAst = classOrInterfaceOrEnumDefAst
                .findFirstToken(TokenTypes.IDENT);
        privateTypes.add(definitionAst);
    }

    /**
     * Appends non-private, defined in top-level class method's returned or
     * parameter type, or field's type to general list of out referenced types.
     * @param methodDefAst
     *        AST subtree that represent method definition.
     */
    private void addExternallyAccessibleMethodTypes(DetailAST methodDefAst)
    {
        DetailAST typeDefAst = methodDefAst
                .findFirstToken(TokenTypes.TYPE);
        DetailAST parametersDefAst = methodDefAst
                .findFirstToken(TokenTypes.PARAMETERS);
        externallyReferencedTypes.addAll(getMethodOrFieldReferencedTypes(typeDefAst));
        externallyReferencedTypes.addAll(getMethodParameterTypes(parametersDefAst));
    }

    /**
     * Appends non-private, defined in top-level class field's type to general
     * list of out referenced types.
     * @param fieldDefAst
     *        AST subtree that represent field definition.
     */
    private void addExternallyAccessibleFieldTypes(DetailAST fieldDefAst)
    {
        DetailAST typeDefAst = fieldDefAst.findFirstToken(TokenTypes.TYPE);
        externallyReferencedTypes.addAll(getMethodOrFieldReferencedTypes(typeDefAst));
    }

    /**
     * Gets the return type of method or field type.
     * @param typeAst
     *        AST subtree to process.
     */
    private static List<DetailAST>
            getMethodOrFieldReferencedTypes(DetailAST typeAst)
    {

        DetailAST returnedType = null;
        List<DetailAST> returnedTypes = new ArrayList<DetailAST>();
        DetailAST currentNode = typeAst;
        while (currentNode != null) {
            if (currentNode.getType() == TokenTypes.IDENT) {
                returnedType = currentNode;
                returnedTypes.add(returnedType);
            }
            currentNode = getNextSubTreeNode(currentNode, typeAst);
        }
        return returnedTypes;
    }

    /**
     * Gets method's parameters types
     * @param parametersDefAst
     */
    private static List<DetailAST>
            getMethodParameterTypes(DetailAST parametersDefAst)
    {
        DetailAST parameterType = null;
        List<DetailAST> parameterTypes = new ArrayList<DetailAST>();

        if (parametersDefAst.getFirstChild() != null) {
            DetailAST currentNode = parametersDefAst;

            while (currentNode != null) {
                if (currentNode.getType() == TokenTypes.PARAMETER_DEF) {
                    parameterType = currentNode;

                    while (parameterType != null) {
                        parameterType = getNextSubTreeNode(parameterType,
                                currentNode);
                        if (parameterType != null
                                && parameterType.getType() == TokenTypes.IDENT) {
                            parameterTypes.add(parameterType);
                        }
                    }

                }
                currentNode = getNextSubTreeNode(currentNode, parametersDefAst);
            }

        }

        return parameterTypes;
    }

    /**
     * Checks if defined type or interface extends or implements any
     * <u>non-private type</u>.
     * @param classOrInterfaceDefAst
     * @return Method returns true if class extends or implements something.
     */
    private boolean isExtendsOrImplementsSmth(DetailAST classOrInterfaceDefAst)
    {
        return (classOrInterfaceDefAst
                .branchContains(TokenTypes.EXTENDS_CLAUSE)
                || classOrInterfaceDefAst
                .branchContains(TokenTypes.IMPLEMENTS_CLAUSE))
                && !isExtendsOrImplementsPrivate(classOrInterfaceDefAst);
    }

    /**
     * Checks if inner class or interface extends or implements <u>inner private
     * type</u>
     * @param classOrInterfaceDefAst
     * @return true if extending or implementing type is in collection of inner
     *         private types
     */
    private boolean
            isExtendsOrImplementsPrivate(DetailAST classOrInterfaceDefAst)
    {
        boolean result = false;

        Set<String> inheritedTypesNamesSet = new HashSet<String>();
        DetailAST currentNode = classOrInterfaceDefAst;

        while (currentNode != null) {
            if (currentNode.getType() == TokenTypes.EXTENDS_CLAUSE
                    || currentNode.getType() == TokenTypes.IMPLEMENTS_CLAUSE) {
                DetailAST implementingOrExtendingAst = currentNode;

                while (implementingOrExtendingAst != null) {
                    implementingOrExtendingAst = getNextSubTreeNode(
                            implementingOrExtendingAst, currentNode);
                    if (implementingOrExtendingAst != null
                            && implementingOrExtendingAst.getType() == TokenTypes.IDENT) {
                        inheritedTypesNamesSet
                                .add(implementingOrExtendingAst.getText());
                    }
                }

            }
            currentNode = getNextSubTreeNode(currentNode,
                    classOrInterfaceDefAst);
        }

        Set<String> existingPrivateTypes = new HashSet<String>();
        for (DetailAST privateType : privateTypes) {
            existingPrivateTypes.add(privateType.getText());
        }
        if (existingPrivateTypes.containsAll(inheritedTypesNamesSet)) {
            result = true;
        }

        return result;
    }

    /**
     * Checks, class, interface, enumeration, method or field has "private"
     * access modifier.
     * @param modifierType
     * @param defAst
     */
    public static boolean
            hasModifier(int modifierType, DetailAST defAst)
    {
        DetailAST modifiersToken = defAst.getFirstChild();
        return modifiersToken.findFirstToken(modifierType) != null;
    }

    /**
     * Checks if method or field is defined in top-level class
     * @param methodOrFieldDefAst
     * @return true if method is defined in top-level class
     */
    private static boolean
            isDefinedInTopLevelClass(DetailAST methodOrFieldDefAst)
    {
        return methodOrFieldDefAst.getParent().getParent().getParent() == null;
    }

    /**
     * Gets the next node of a syntactical tree (child of a current node or
     * sibling of a current node, or sibling of a parent of a current node)
     * @param currentNode
     *        Current node in considering
     * @return Current node after bypassing
     */
    private static DetailAST
            getNextSubTreeNode(DetailAST currentNode, DetailAST subTreeRoot)
    {
        DetailAST toVisit = currentNode.getFirstChild();
        while (toVisit == null) {
            toVisit = currentNode.getNextSibling();
            if (toVisit == null) {
                if (currentNode.getParent().equals(subTreeRoot)) {
                    break;
                }
                currentNode = currentNode.getParent();
            }
        }
        currentNode = toVisit;
        return currentNode;
    }

}
