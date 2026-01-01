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
 * Checks for useless "super()" calls in ctors.
 * </p>
 * <p>
 * "super()" call could be considered by Check as "useless" in two cases:
 * </p>
 * <p>
 * Case 1. no-argument "super()" is called from class ctor if class is not derived, for example:
 * </p>
 * <pre>
 * <code>
 * class Dummy {
 *     Dummy() {
 *             super();
 *     }
 * }
 * </code>
 * </pre>
 * "super()" call is useless because class "Dummy" is not derived.
 * <p>
 * Case 2. no-argument "super()" is called without parameters from class ctor if class is
 * derived, for example:
 * </p>
 * <pre>
 * <code>
 * class Derived extends Base {
 *     Derived() {
 *             super();
 *     }
 * }
 * </code>
 * </pre>
 * Java compiler automatically inserts a call to the no-args constructor of the superclass,
 * so there is no need to call super ctor explicitly. Check has options "allowCallToNoArgsSuperCtor"
 * and "allowCallToNoArgsSuperCtorIfMultiplePublicCtor" to adjust check behavior for such cases(
 * see Check`s options description for details).
 *
 * <p>
 * Check has following options:
 * </p>
 * <p>
 * "allowCallToNoArgsSuperCtor" - if this option set to true, Check will not generate
 * violations when "super()" called inside derived class. This option defaults to "false".
 * If for example this option set to "true", then Check will not generate violation for
 * cases like following:
 * </p>
 * <pre>
 * <code>
 * class Base {
 *     public Base() {
 *     }
 * }
 *
 * class Derived extends Base {
 *     public Derived() {
 *         super();
 *     }
 * }
 * </code>
 * </pre>
 *
 * <p>
 * "allowCallToNoArgsSuperCtorIfMultiplePublicCtor" - if this option set to "true", then
 * Check will not generate violation when "super()" called inside class ctor when class
 * has multiple public ctors(however, setting this option to "true" will not prevent Check
 * from logging violation if class does not extend anything). This option defaults to "false".
 * This option may be useful for cases in which class`s ctors just forward its arguments to
 * super ctors, thus removing "super()" in this case will make default ctors look not like
 * others. For example:
 * </p>
 * <pre>
 * <code>
 * class Base {
 *     public Base() {
 *     }
 *
 *     public Base(int i) {
 *     }
 * }
 *
 * class Derived extends Base {
 *     public Derived() {
 *         super(); // this "super()" will not be considered useless if option is set to true,
 *                  // because "Derived" has multiple public ctors.
 *     }
 *
 *     public Derived(int i) {
 *         super(i); // this "super()" will not be considered useless if option is set to true,
 *                   // because "Derived" has multiple public ctors.
 *     }
 * }
 *
 * class NotDerived {
 *     public NotDerived() {
 *         super(); // this "super()" will be considered useless regardless of option value,
 *                  // because "NotDerived" does not extend anything.
 *     }
 *     public NotDerived(int i) {
 *         super(); // this "super()" will be considered useless regardless of option value,
 *                  // because "NotDerived" does not extend anything.
 *     }
 * }
 * </code>
 * </pre>
 *
 * <p>
 * Checkstyle configuration example with options "allowCallToNoArgsSuperCtor" and
 * "allowCallToNoArgsSuperCtorIfMultiplePublicCtor" set to true.
 * </p>
 * <pre>
 *   &lt;module name="UselessSuperCtorCallCheck"&gt;
 *     &lt;property name="allowCallToNoArgsSuperCtor" value="true"/&gt;
 *     &lt;property name="allowCallToNoArgsSuperCtorIfMultiplePublicCtor" value="true"/&gt;
 *   &lt;/module&gt;
 * </pre>
 *
 * @author <a href="mailto:zuy_alexey@mail.ru">Zuy Alexey</a>
 * @since 1.13.0
 */
public class UselessSuperCtorCallCheck extends AbstractCheck {

    /**
     * Violation message key.
     */
    public static final String MSG_IN_NOT_DERIVED_CLASS =
            "useless.super.ctor.call.in.not.derived.class";

    /**
     * Violation message key.
     */
    public static final String MSG_WITHOUT_ARGS =
            "useless.super.ctor.call.without.args";

    /**
     * Used to allow calls to no-arguments super constructor from derived class.
     * By default check will log this case.
     */
    private boolean allowCallToNoArgsSuperCtor;

    /**
     * Used to allow calls to no-arguments super constructor from derived class
     * if it has multiple public constructors.
     */
    private boolean allowCallToNoArgsSuperCtorIfMultiplePublicCtor;

    /**
     * Sets flag to allowCallToNoArgsSuperCtor.
     *
     * @param aAllowCallToNoArgsSuperCtor
     *        if true, check will allow super() calls without arguments
     */
    public void setAllowCallToNoArgsSuperCtor(boolean aAllowCallToNoArgsSuperCtor) {
        allowCallToNoArgsSuperCtor = aAllowCallToNoArgsSuperCtor;
    }

    /**
     * Sets flag to allowCallToNoArgsSuperCtorIfMultiplePublicCtor.
     *
     * @param aAllowCall
     *        if true, check will allow super() calls without arguments if class
     *        has multiple public constructors
     */
    public void setAllowCallToNoArgsSuperCtorIfMultiplePublicCtor(boolean aAllowCall) {
        allowCallToNoArgsSuperCtorIfMultiplePublicCtor = aAllowCall;
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.SUPER_CTOR_CALL,
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
    public void visitToken(DetailAST aSuperCallNode) {
        if (getSuperCallArgsCount(aSuperCallNode) == 0) {
            final DetailAST classDefNode = getClassDefinitionNode(aSuperCallNode);
            final String className = getClassName(classDefNode);

            if (isClassDerived(classDefNode)) {
                if (!allowCallToNoArgsSuperCtor && (!allowCallToNoArgsSuperCtorIfMultiplePublicCtor
                        || getClassPublicCtorCount(classDefNode) <= 1)) {
                    log(aSuperCallNode, MSG_WITHOUT_ARGS, className);
                }
            }
            else {
                log(aSuperCallNode, MSG_IN_NOT_DERIVED_CLASS, className);
            }
        }
    }

    /**
     * Returns class name for given class definition node.
     *
     * @param aClassDefNode
     *          a class definition node(TokenTypes.CLASS_DEF)
     * @return class name for given class definition
     */
    private static String getClassName(DetailAST aClassDefNode) {
        return aClassDefNode.findFirstToken(TokenTypes.IDENT).getText();
    }

    /**
     * Returns arguments count for super ctor call.
     *
     * @param aMethodCallNode
     *        a super ctor call node(TokenTypes.SUPER_CTOR_CALL)
     * @return arguments count for super ctor call
     */
    private static int getSuperCallArgsCount(DetailAST aMethodCallNode) {
        final DetailAST argsListNode = aMethodCallNode.findFirstToken(TokenTypes.ELIST);

        return argsListNode.getChildCount();
    }

    /**
     * Returns class definition node for class, which contains given AST node.
     *
     * @param aNode
     *        AST node inside class
     * @return class definition node
     */
    private static DetailAST getClassDefinitionNode(DetailAST aNode) {
        DetailAST result = aNode;

        while (result.getType() != TokenTypes.CLASS_DEF) {
            result = result.getParent();
        }

        return result;
    }

    /**
     * Calculates public constructor count for given class.
     *
     * @param aClassDefNode
     *          a class definition node(TokenTypes.CLASS_DEF)
     * @return public constructor count for given class
     */
    private static int getClassPublicCtorCount(DetailAST aClassDefNode) {
        int publicCtorCount = 0;
        DetailAST classMemberNode = aClassDefNode.findFirstToken(TokenTypes.OBJBLOCK)
                .getFirstChild();

        while (classMemberNode != null) {
            if (classMemberNode.getType() == TokenTypes.CTOR_DEF && isCtorPublic(classMemberNode)) {
                ++publicCtorCount;
            }

            classMemberNode = classMemberNode.getNextSibling();
        }

        return publicCtorCount;
    }

    /**
     * Checks whether given ctor is public.
     *
     * @param aCtorDefNode
     *          a ctor definition node(TokenTypes.CTOR_DEF)
     * @return true, if given ctor is public
     */
    private static boolean isCtorPublic(DetailAST aCtorDefNode) {
        return aCtorDefNode
                .findFirstToken(TokenTypes.MODIFIERS)
                .findFirstToken(TokenTypes.LITERAL_PUBLIC) != null;
    }

    /**
     * Checks whether this class is derived from other class.
     *
     * @param aClassDefNode
     *        class definition node
     * @return true, if this class extends anything
     */
    private static boolean isClassDerived(DetailAST aClassDefNode) {
        return aClassDefNode.findFirstToken(TokenTypes.EXTENDS_CLAUSE) != null;
    }

}
