////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2020 the original author or authors.
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

import com.github.sevntu.checkstyle.SevntuUtil;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * Checks that certain fields are not used. This can be used to enforce that fields like e.g.
 * {@link java.util.Locale#ROOT}.
 * </p>
 *
 * <p>
 * Parameters are:
 * </p>
 *
 * <ul>
 * <li><b>packageName</b> - The field package name to be forbidden.</li>
 * <li><b>className</b> - The field class name to be forbidden.</li>
 * <li><b>fieldName</b> - The field name to be forbidden.</li>
 * </ul>
 *
 * <p>
 * Together, these three parameters forms the field fully qualified name.
 * </p>
 *
 * <p>
 * Default parameters are:
 * </p>
 *
 * <ul>
 * <li><b>packageName</b>java.util</li>
 * <li><b>className</b>Locale</li>
 * <li><b>fieldName</b>ROOT</li>
 * </ul>
 *
 * <p>
 * which forbids the usage of {@link java.util.Locale#ROOT}.
 * </p>
 *
 * @author <a href="mailto:yasser.aziza@gmail.com">Yasser Aziza</a>
 * @since 1.38.0
 */
public class ForbidFieldAccessCheck extends AbstractCheck {

    /**
     * Warning message key.
     */
    public static final String MSG_KEY = "forbid.field.access";

    /**
     * '.' character used as separator between FQN elements.
     */
    private static final char DOT = '.';

    /**
     * Package name.
     */
    private String packageName = "java.util";

    /**
     * Class name.
     */
    private String className = "Locale";

    /**
     * Field name.
     */
    private String fieldName = "ROOT";

    /**
     * Whether the field class was imported.
     */
    private boolean wasPackageImported;

    /**
     * Sets the package name, in which the field is declared.
     * @param packageName the field name
     */
    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    /**
     * Sets the class name, which declares the field.
     * @param className the class name
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Sets the Field name, which should be forbidden to use.
     * @param fieldName the field name
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * Gets the field fully qualified name.
     * @return {@link String} containing the field FQN
     */
    private String getFieldFullyQualifiedName() {
        return packageName + DOT + className + DOT + fieldName;
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.STATIC_IMPORT,
            TokenTypes.IMPORT,
            TokenTypes.IDENT,
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
        if (isStaticImported(ast)) {
            log(ast, MSG_KEY, getFieldFullyQualifiedName());
        }
        else if (isPackageImported(ast)) {
            // Mark forbidden field package as imported
            wasPackageImported = true;
        }
        else if (wasPackageImported && TokenTypes.IDENT == ast.getType() && isSameType(ast)) {
            log(ast.getPreviousSibling(), MSG_KEY, getFieldFullyQualifiedName());
        }
    }

    /**
     * Checks whether the field is static imported.
     *
     * @param ast the {@link TokenTypes#STATIC_IMPORT} node to be checked
     * @return {@code true} if the field was static imported, {@code false} otherwise
     */
    private boolean isStaticImported(DetailAST ast) {
        return TokenTypes.STATIC_IMPORT == ast.getType()
            && getFieldFullyQualifiedName().equals(SevntuUtil.getText(ast));
    }

    /**
     * Checks whether the field package is imported.
     *
     * @param ast the {@link TokenTypes#IMPORT} node to be checked
     * @return {@code true} if the field was imported, {@code false} otherwise
     */
    private boolean isPackageImported(DetailAST ast) {
        final String importName = packageName + DOT + className;

        return TokenTypes.IMPORT == ast.getType()
                && importName.equals(FullIdent.createFullIdentBelow(ast).getText());
    }

    /**
     * Checks if the given {@link TokenTypes#IDENT} node has the same type as the forbidden field.
     *
     * @param ast the {@link TokenTypes#IDENT} node to be checked
     * @return {@code true} if the field has the same FQN, {@code false} otherwise
     */
    private boolean isSameType(DetailAST ast) {
        return fieldName.equals(ast.getText())
                && ast.getPreviousSibling() != null
                && className.equals(ast.getPreviousSibling().getText());
    }

}
