///////////////////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code and other text files for adherence to a set of rules.
// Copyright (C) 2001-2025 the original author or authors.
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

package com.github.sevntu.checkstyle.checks.naming;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.naming.AbstractNameCheck;

/**
 * <p>
 * Checks that interface type parameter names conform to a format specified
 * by the format property.  The format is a
 * {@link java.util.regex.Pattern regular expression} and defaults to
 * <strong>^[A-Z]$</strong>.
 * </p>
 * <p>
 * An example of how to configure the check is:
 * </p>
 * <pre>
 * &lt;module name="InterfaceTypeParameterName"/&gt;
 * </pre>
 * <p>
 * An example of how to configure the check for names that are only a single
 * letter is
 * </p>
 * <pre>
 * &lt;module name="InterfaceTypeParameterName"&gt;
 *    &lt;property name="format" value="^[a-zA-Z]$"/&gt;
 * &lt;/module&gt;
 * </pre>
 *
 * @author Dmitry Gridyushko
 * @version 1.0
 * @since 1.8.0
 */
public class InterfaceTypeParameterNameCheck
        extends AbstractNameCheck {

    /** Creates a new <code>InterfaceTypeParameterNameCheck</code> instance. */
    public InterfaceTypeParameterNameCheck() {
        super("^[A-Z]$");
    }

    @Override
    public final int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.TYPE_PARAMETER,
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
    protected final boolean mustCheckName(DetailAST ast) {
        final DetailAST location = ast.getParent().getParent();

        return location.getType() == TokenTypes.INTERFACE_DEF;
    }

}
