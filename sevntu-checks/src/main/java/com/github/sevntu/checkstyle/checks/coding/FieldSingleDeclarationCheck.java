////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2016 the original author or authors.
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

import com.github.sevntu.checkstyle.Utils;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>This checks ensures that classes have at most 1 field of the given className.</p>
 *
 * <p>This can be useful for example to ensure that only one Logger is used:</p>
 *
 * <pre>
 * &lt;module name="TreeWalker"&gt;
 *   &lt;module name="com.github.sevntu.checkstyle.checks.coding.FieldSingleDeclarationCheck"&gt;
 *       &lt;property name="className" value="org.slf4j.Logger" /&gt;
 *   &lt;/module&gt;
 * &lt;/module&gt;
 *
 * class Example {
 *     private static final org.slf4j.Logger logger = LoggerFactory.getLogger(Example.class); // OK
 *     private static       org.slf4j.Logger logger2; // NOK!
 * }
 * </pre>
 *
 * @author Milos Fabian, Pantheon Technologies - original author (in OpenDaylight.org)
 * @author Michael Vorburger.ch - refactored and made more generalized for contribution to Sevntu
 */
public class FieldSingleDeclarationCheck extends AbstractCheck {

    /**
     * Violation message key.
     */
    public static final String MSG_KEY = "field.count";

    /**
     * Configuration property with class name check for.
     */
    private String fullyQualifiedClassName;

    /**
     * Field to remember if class was previously seen in current File.
     */
    private boolean hasPreviouslySeenClass;

    /**
     * Set Class of which there should only be max. 1 field per class.
     * @param className the fully qualified name (FQN) of the class
     */
    public void setClassName(String className) {
        this.fullyQualifiedClassName = className;
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {TokenTypes.VARIABLE_DEF };
    }

    @Override
    public int[] getRequiredTokens() {
        return getDefaultTokens();
    }

    @Override
    public int[] getAcceptableTokens() {
        return getDefaultTokens();
    }

    @Override
    public void beginTree(DetailAST rootAST) {
        this.hasPreviouslySeenClass = false;
    }

    @Override
    public void visitToken(DetailAST ast) {
        if (fullyQualifiedClassName == null) {
            throw new IllegalStateException("Must set mandatory className property in"
                    + getClass().getSimpleName());
        }
        if (Utils.matchesFullyQualifiedName(ast, fullyQualifiedClassName)) {
            if (hasPreviouslySeenClass) {
                log(ast.getLineNo(), MSG_KEY, fullyQualifiedClassName);
            }
            this.hasPreviouslySeenClass = true;
        }
    }

}
