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

package com.github.sevntu.checkstyle.checks.design;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.github.sevntu.checkstyle.SevntuUtil;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * This Check warns on propagation of inner private types to outer classes:<br>
 * - Externally accessible method if it returns private inner type.<br>
 * - Externally accessible field if its type is a private inner type.<br>
 * These types could be <a href=
 * 'http://docs.oracle.com/javase/tutorial/java/javaOO/nested.html'>private
 * inner classes</a>, interfaces or enumerations.<br>
 * <br>
 * Examples:
 * <pre>
 * class OuterClass {
 *  public InnerClass innerFromMain = new InnerClass(); //WARNING
 *  private class InnerClass { ... }
 *  public InnerClass  getValue() { //WARNING
 *      return new InnerClass();
 *  }
 *  <br>
 *  private interface InnerInterface { ... }
 *  public Set&lt;InnerInterface&gt; getValue() { //WARNING
 *      return new TreeSet&lt;InnerInterface&gt;
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
 * <b>Rationale:</b> it is possible to return<br>
 * private inner type or use it as the parameter of non-private method, but it
 * is impossible<br>
 * to use it in other classes (besides inner classes)<br>
 * unless it extends or implements at least one <u>non-private</u> class or
 * interface.<br>
 * Such situation usually happens after bulk refactoring and usually means
 * dead/useless code<br>
 * <br>
 *
 * @author <a href="mailto:nesterenko-aleksey@list.ru">Aleksey Nesterenko</a>
 * @since 1.12.0
 */
public class PublicReferenceToPrivateTypeCheck extends AbstractCheck {

    /**
     * Check message key for private types.
     */
    public static final String MSG_KEY = "public.reference.to.private.type";

    /**
     * List containing names of private types (classes, interfaces or enums).
     */
    private final Set<DetailAST> privateTypes = new HashSet<>();

    /**
     * List containing the names of types returned by public methods or fields.
     */
    private final Set<DetailAST> externallyReferencedTypes = new HashSet<>();

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.CLASS_DEF,
            TokenTypes.METHOD_DEF,
            TokenTypes.INTERFACE_DEF,
            TokenTypes.ENUM_DEF,
            TokenTypes.VARIABLE_DEF,
        };
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
        privateTypes.clear();
        externallyReferencedTypes.clear();
    }

    @Override
    public void visitToken(DetailAST defAst) {
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
                SevntuUtil.reportInvalidToken(defAst.getType());
                break;
        }
    }

    @Override
    public void finishTree(DetailAST rootAst) {
        for (DetailAST privateType : privateTypes) {
            for (DetailAST outReturnedType : externallyReferencedTypes) {
                if (privateType.getText().equals(
                        outReturnedType.getText())
                        && !isExtendsOrImplementsSmth(privateType
                                .getParent())) {
                    log(outReturnedType, MSG_KEY,
                            outReturnedType.getText());
                }
            }
        }
    }

    /**
     * Adds type to the list of private types.
     *
     * @param classOrInterfaceOrEnumDefAst
     *        AST subtree that represent inner private type definition.
     */
    private void addPrivateTypes(DetailAST classOrInterfaceOrEnumDefAst) {
        final DetailAST definitionAst = classOrInterfaceOrEnumDefAst
                .findFirstToken(TokenTypes.IDENT);
        privateTypes.add(definitionAst);
    }

    /**
     * Appends non-private, defined in top-level class method's returned or
     * parameter type, or field's type to general list of out referenced types.
     *
     * @param methodDefAst
     *        AST subtree that represent method definition.
     */
    private void addExternallyAccessibleMethodTypes(DetailAST methodDefAst) {
        final DetailAST typeDefAst = methodDefAst
                .findFirstToken(TokenTypes.TYPE);
        final DetailAST parametersDefAst = methodDefAst
                .findFirstToken(TokenTypes.PARAMETERS);
        externallyReferencedTypes.addAll(getMethodOrFieldReferencedTypes(typeDefAst));
        externallyReferencedTypes.addAll(getMethodParameterTypes(parametersDefAst));
    }

    /**
     * Appends non-private, defined in top-level class field's type to general
     * list of out referenced types.
     *
     * @param fieldDefAst
     *        AST subtree that represent field definition.
     */
    private void addExternallyAccessibleFieldTypes(DetailAST fieldDefAst) {
        final DetailAST typeDefAst = fieldDefAst.findFirstToken(TokenTypes.TYPE);
        externallyReferencedTypes.addAll(getMethodOrFieldReferencedTypes(typeDefAst));
    }

    /**
     * Gets the return type of method or field type.
     *
     * @param typeAst
     *        AST subtree to process.
     * @return the return types of the token.
     */
    private static List<DetailAST>
            getMethodOrFieldReferencedTypes(DetailAST typeAst) {
        DetailAST returnedType;
        final List<DetailAST> returnedTypes = new ArrayList<>();
        DetailAST currentNode = typeAst;
        while (currentNode != null) {
            if (currentNode.getType() == TokenTypes.IDENT) {
                returnedType = currentNode;
                returnedTypes.add(returnedType);
            }
            currentNode = SevntuUtil.getNextSubTreeNode(currentNode, typeAst);
        }
        return returnedTypes;
    }

    /**
     * Gets method's parameters types.
     *
     * @param parametersDefAst The token to examine.
     * @return The parameter types of the method.
     */
    private static List<DetailAST>
            getMethodParameterTypes(DetailAST parametersDefAst) {
        final List<DetailAST> parameterTypes = new ArrayList<>();

        if (parametersDefAst.getFirstChild() != null) {
            DetailAST currentNode = parametersDefAst;
            DetailAST parameterType;

            while (currentNode != null) {
                if (currentNode.getType() == TokenTypes.PARAMETER_DEF) {
                    parameterType = currentNode;

                    while (parameterType != null) {
                        parameterType = SevntuUtil.getNextSubTreeNode(parameterType,
                                currentNode);
                        if (parameterType != null
                                && parameterType.getType() == TokenTypes.IDENT) {
                            parameterTypes.add(parameterType);
                        }
                    }
                }
                currentNode = SevntuUtil.getNextSubTreeNode(currentNode, parametersDefAst);
            }
        }

        return parameterTypes;
    }

    /**
     * Checks if defined type or interface extends or implements any
     * <u>non-private type</u>.
     *
     * @param classOrInterfaceDefAst The token to examine.
     * @return Method returns true if class extends or implements something.
     */
    private boolean isExtendsOrImplementsSmth(DetailAST classOrInterfaceDefAst) {
        return (classOrInterfaceDefAst
                .findFirstToken(TokenTypes.EXTENDS_CLAUSE) != null
                || classOrInterfaceDefAst
                .findFirstToken(TokenTypes.IMPLEMENTS_CLAUSE) != null)
                && !isExtendsOrImplementsPrivate(classOrInterfaceDefAst);
    }

    /**
     * Checks if inner class or interface extends or implements <u>inner private
     * type</u>.
     *
     * @param classOrInterfaceDefAst The token to examine.
     * @return true if extending or implementing type is in collection of inner
     *         private types
     */
    private boolean
            isExtendsOrImplementsPrivate(DetailAST classOrInterfaceDefAst) {
        boolean result = false;

        final Set<String> inheritedTypesNamesSet = new HashSet<>();
        DetailAST currentNode = classOrInterfaceDefAst;

        while (currentNode != null) {
            if (currentNode.getType() == TokenTypes.EXTENDS_CLAUSE
                    || currentNode.getType() == TokenTypes.IMPLEMENTS_CLAUSE) {
                DetailAST implementingOrExtendingAst = currentNode;

                while (implementingOrExtendingAst != null) {
                    implementingOrExtendingAst = SevntuUtil.getNextSubTreeNode(
                            implementingOrExtendingAst, currentNode);
                    if (implementingOrExtendingAst != null
                            && implementingOrExtendingAst.getType() == TokenTypes.IDENT) {
                        inheritedTypesNamesSet
                                .add(implementingOrExtendingAst.getText());
                    }
                }
            }
            currentNode = SevntuUtil.getNextSubTreeNode(currentNode, classOrInterfaceDefAst);
        }

        final Set<String> existingPrivateTypes = new HashSet<>();
        for (DetailAST privateType : privateTypes) {
            existingPrivateTypes.add(privateType.getText());
        }
        if (existingPrivateTypes.containsAll(inheritedTypesNamesSet)) {
            result = true;
        }

        return result;
    }

    /**
     * Checks if class, interface, enumeration, method or field definition has an
     * access modifier of specified type.
     *
     * @param modifierType modifier type
     * @param defAst definition ast (METHOD_DEF, FIELD_DEF, etc.)
     * @return true if class, interface, enumeration, method or field definition has an
     *     access modifier of specified type
     */
    public static boolean
            hasModifier(int modifierType, DetailAST defAst) {
        final DetailAST modifiersToken = defAst.getFirstChild();
        return modifiersToken.findFirstToken(modifierType) != null;
    }

    /**
     * Checks if method or field is defined in top-level class.
     *
     * @param methodOrFieldDefAst The token to examine.
     * @return true if method is defined in top-level class
     */
    private static boolean
            isDefinedInTopLevelClass(DetailAST methodOrFieldDefAst) {
        return methodOrFieldDefAst.getParent().getParent()
                .getParent().getType() == TokenTypes.COMPILATION_UNIT;
    }

}
