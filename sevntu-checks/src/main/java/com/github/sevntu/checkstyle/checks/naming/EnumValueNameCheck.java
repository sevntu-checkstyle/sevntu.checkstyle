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

package com.github.sevntu.checkstyle.checks.naming;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.naming.AbstractNameCheck;

/**
 * <p>
 * Checks that enumeration value names conform to a format specified
 * by the format property. The format is a
 * {@link java.util.regex.Pattern regular expression} and defaults to
 * <strong>^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$</strong>.
 * </p>
 * <p>
 * An example of how to configure the check is:
 * </p>
 * <pre>
 * &lt;module name="EnumValueName"/&gt;
 * </pre>
 * <p>
 * An example of how to configure the check for names that requires all names to be lowercase
 * with underscores and digits is:
 * </p>
 * <pre>
 * &lt;module name="EnumValueName"&gt;
 *    &lt;property name="format" value="^[a-z_0-9]+*$"/&gt;
 * &lt;/module&gt;
 * </pre>
 *
 * @author Pavel Baranchikov
 * @since 1.24.0
 */
public class EnumValueNameCheck extends AbstractNameCheck {

    /**
     * Default pattern for Values Enumeration names.
     */
    public static final String DEFAULT_PATTERN =
            "^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$";

    /**
     * Creates a new {@code EnumValueNameCheck} instance.
     */
    public EnumValueNameCheck() {
        super(DEFAULT_PATTERN);
    }

    @Override
    public int[] getDefaultTokens() {
        return getAcceptableTokens();
    }

    @Override
    public int[] getAcceptableTokens() {
        return new int[] {TokenTypes.ENUM_CONSTANT_DEF};
    }

    @Override
    public int[] getRequiredTokens() {
        return getDefaultTokens();
    }

    @Override
    protected boolean mustCheckName(DetailAST ast) {
        return true;
    }

}
