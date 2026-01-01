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

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Make sure that utility classes (classes that contain only static methods)
 * do not have a public constructor.
 * <p>
 * Rationale: Instantiating utility classes does not make sense.
 * A common mistake is forgetting to hide the default constructor.
 * </p>
 *
 * @author lkuehne
 * @version $Revision: 1.12 $
 * @since 1.8.0
 */
public class HideUtilityClassConstructorCheck extends AbstractCheck {

    /**
     * Warning message key.
     */
    public static final String MSG_KEY = "hide.utility.class";

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.CLASS_DEF,
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
    public void visitToken(DetailAST ast) {
        // abstract class could not have private constructor
        if (!isAbstract(ast)) {
            final DetailAST objBlock = ast.findFirstToken(TokenTypes.OBJBLOCK);
            DetailAST child = objBlock.getFirstChild();
            final boolean hasStaticModifier = isStatic(ast);
            boolean hasMethodOrField = false;
            boolean hasNonStaticMethodOrField = false;
            boolean hasNonPrivateStaticMethodOrField = false;
            boolean hasDefaultCtor = true;
            boolean hasPublicCtor = false;

            while (child != null) {
                final int type = child.getType();
                if (type == TokenTypes.METHOD_DEF
                        || type == TokenTypes.VARIABLE_DEF) {
                    hasMethodOrField = true;
                    final DetailAST modifiers =
                        child.findFirstToken(TokenTypes.MODIFIERS);
                    final boolean isStatic =
                        modifiers.findFirstToken(TokenTypes.LITERAL_STATIC) != null;
                    final boolean isPrivate =
                        modifiers.findFirstToken(TokenTypes.LITERAL_PRIVATE) != null;

                    if (!isStatic && !isPrivate) {
                        hasNonStaticMethodOrField = true;
                    }
                    if (isStatic && !isPrivate) {
                        hasNonPrivateStaticMethodOrField = true;
                    }
                }
                if (type == TokenTypes.CTOR_DEF) {
                    hasDefaultCtor = false;
                    final DetailAST modifiers =
                        child.findFirstToken(TokenTypes.MODIFIERS);
                    if (modifiers.findFirstToken(TokenTypes.LITERAL_PRIVATE) == null
                        && modifiers.findFirstToken(TokenTypes.LITERAL_PROTECTED) == null) {
                        // treat package visible as public
                        // for the purpose of this Check
                        hasPublicCtor = true;
                    }
                }
                child = child.getNextSibling();
            }

            final boolean hasAccessibleCtor = hasDefaultCtor || hasPublicCtor;

            // figure out if class extends java.lang.object directly
            // keep it simple for now and get a 99% solution
            // consider "import org.omg.CORBA.*"
            final boolean extendsJlo =
                // J.Lo even made it into in our sources :-)
                ast.findFirstToken(TokenTypes.EXTENDS_CLAUSE) == null;

            final boolean isUtilClass = extendsJlo && hasMethodOrField
                && !hasNonStaticMethodOrField
                && hasNonPrivateStaticMethodOrField;

            if (isUtilClass && hasAccessibleCtor && !hasStaticModifier) {
                log(ast, MSG_KEY);
            }
        }
    }

    /**
     * Test is AST object has abstract modifier.
     *
     * @param ast class definition for check.
     * @return true if a given class declared as abstract.
     */
    private static boolean isAbstract(DetailAST ast) {
        final DetailAST abstractAST = ast.findFirstToken(TokenTypes.MODIFIERS)
            .findFirstToken(TokenTypes.ABSTRACT);

        return abstractAST != null;
    }

    /**
     * Test is AST object has static modifier.
     *
     * @param ast class definition for check.
     * @return true if a given class declared as static.
     */
    private static boolean isStatic(DetailAST ast) {
        final DetailAST staticAST = ast.findFirstToken(TokenTypes.MODIFIERS)
            .findFirstToken(TokenTypes.LITERAL_STATIC);

        return staticAST != null;
    }

}
