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

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
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
 *
 * @author <a href="mailto:IliaDubinin91@gmail.com">Ilia Dubinin</a>
 * @since 1.8.0
 */
public class AvoidDefaultSerializableInInnerClassesCheck extends AbstractCheck {

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY = "avoid.default.serializable.in.inner.classes";

    /**
     * <b>
     * Option, that allow partial implementation of serializable interface.
     * </b>
     */
    private boolean allowPartialImplementation;

    /**
     * <p>
     * Set allow partly implementation serializable interface.
     * </p>
     *
     * @param allow - Option, that allow partial implementation
     *        of serializable interface.
     */
    public void setAllowPartialImplementation(boolean allow) {
        allowPartialImplementation = allow;
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {TokenTypes.CLASS_DEF };
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
    public void visitToken(DetailAST detailAST) {
        final boolean topLevelClass =
                detailAST.getParent().getType() == TokenTypes.COMPILATION_UNIT;
        if (!topLevelClass && isSerializable(detailAST)
                && !isStatic(detailAST)
                && !hasSerialazableMethods(detailAST)) {
            final DetailAST implementsBlock = detailAST
                    .findFirstToken(TokenTypes.IMPLEMENTS_CLAUSE);
            log(implementsBlock,
                    MSG_KEY);
        }
    }

    /**
     * <p>
     * Return true if it is nested class. Terminology is here :
     * http://download.oracle.com/javase/tutorial/java/javaOO/nested.html
     * </p>
     *
     * @param classNode - class node
     * @return - boolean variable
     */
    private static boolean isStatic(DetailAST classNode) {
        boolean result = false;
        DetailAST modifiers = classNode.findFirstToken(TokenTypes.MODIFIERS);
        modifiers = modifiers.getFirstChild();
        while (!result && modifiers != null) {
            result = "static".equals(modifiers.getText());
            modifiers = modifiers.getNextSibling();
        }
        return result;
    }

    /**
     * Return {@code true}, if inner class contain override method {@code readObject()} and
     * {@code writeObject()}.
     *
     * @param classNode
     *        the start node of class definition.
     * @return The boolean value. True, if method was override.
     */
    private boolean hasSerialazableMethods(DetailAST classNode) {
        final DetailAST objectBody =
                classNode.findFirstToken(TokenTypes.OBJBLOCK);
        int numberOfSerializationMethods = 0;

        final SiblingIterator methodsIter = new SiblingIterator(objectBody);
        boolean result = false;
        while (methodsIter.hasNextSibling()) {
            final DetailAST methodNode = methodsIter.nextSibling();
            if (isPrivateMethod(methodNode)
                        && isVoidMethod(methodNode)
                        && (hasCorrectParameter(methodNode, "ObjectInputStream")
                        || hasCorrectParameter(methodNode, "ObjectOutputStream")
                        )) {
                numberOfSerializationMethods++;
            }
            if (numberOfSerializationMethods == 1
                && allowPartialImplementation
                || numberOfSerializationMethods == 2) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * <p>
     * Return true, if methods readObject() and writeObject() have correct
     * modifiers.
     * </p>
     *
     * @param methodNode
     *        - current method node;
     * @return boolean value;
     */
    private static boolean isPrivateMethod(DetailAST methodNode) {
        DetailAST modifiers = methodNode.findFirstToken(TokenTypes.MODIFIERS);
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
     *
     * @param methodNode - method node
     * @return boolean variable
     */
    private static boolean isVoidMethod(DetailAST methodNode) {
        DetailAST type = methodNode.findFirstToken(TokenTypes.TYPE);
        type = type.getFirstChild();
        return TokenTypes.LITERAL_VOID == type.getType();
    }

    /**
     * <p>
     * Return true, if method has correct parameters (ObjectInputStream for
     * readObject() and ObjectOutputStream for writeObject()).
     * </p>
     *
     * @param methodNode - method node.
     * @param parameterText - correct parameter text.
     * @return boolean variable.
     */
    private static boolean hasCorrectParameter(DetailAST methodNode,
            String parameterText) {
        DetailAST parameters =
            methodNode.findFirstToken(TokenTypes.PARAMETERS);
        boolean result = false;
        if (parameters.getChildCount(TokenTypes.PARAMETER_DEF) == 1) {
            parameters = parameters.findFirstToken(TokenTypes.PARAMETER_DEF);
            parameters = parameters.findFirstToken(TokenTypes.TYPE);
            parameters = parameters.getFirstChild();
            result = parameterText.equals(parameters.getText());
        }
        return result;
    }

    /**
     * Return {@code true}, if class implement Serializable interface.
     *
     * @param classDefNode
     *        - the start node for class definition.
     * @return boolean value. True, if class implements Serializable interface.
     */
    private static boolean isSerializable(DetailAST classDefNode) {
        DetailAST implementationsDef = classDefNode
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

    /**
     * <b>
     * Nested class, that implements custom iterator for DetailAST method nodes.
     * </b>
     */
    private final class SiblingIterator {

        /**
         * <b>
         * Next.
         * </b>
         */
        private DetailAST next;

        /**
         * <b>
         * Children Iterator constructor.
         * </b>
         *
         * @param parent - child parent.
         */
        /* package */ SiblingIterator(DetailAST parent) {
            next = parent.findFirstToken(TokenTypes.METHOD_DEF);
        }

        /**
         * <b>
         * Return boolean value, if has next element.
         * </b>
         *
         * @return boolean value
         */
        public boolean hasNextSibling() {
            return next != null;
        }

        /**
         * <b>
         * Return next DetailAST element.
         * </b>
         *
         * @return next DetailAST.
         */

        public DetailAST nextSibling() {
            final DetailAST result = next;
            while (next != null) {
                next = next.getNextSibling();
                if (next != null && next.getType() == TokenTypes.METHOD_DEF) {
                    break;
                }
            }
            return result;
        }

    }

}
