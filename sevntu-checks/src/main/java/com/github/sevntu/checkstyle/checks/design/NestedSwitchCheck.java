////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2017 the original author or authors.
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

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.coding.AbstractNestedDepthCheck;

/**
 * <p>
 * This check ensures that there is no switch block inside other switch block. In such case nested
 * block should be exposed into new method.
 * </p>
 * Assuming following block:
 *
 * <pre>
 *      switch (color) {
 *      case GREEN:
 *          switch (type) {
 *          case MEDIUM:
 *          }
 *      case BLUE:
 *      }
 * </pre>
 *
 * Nested switch block that checks <code>type</code> parameter should be converted into separate
 * method.<br>
 * To enable this check use following configuration:<br>
 * <br>
 * <code>&lt;module name=&quot;NestedSwitchCheck&quot;/&gt;</code>
 * <br><br>
 * @author Damian Szczepanik (damianszczepanik@github)
 */
public class NestedSwitchCheck extends AbstractNestedDepthCheck {
    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY = "avoid.nested.switch";

    /** Default allowed nesting depth. */
    private static final int DEFAULT_MAX = 0;

    /** The default constructor. */
    public NestedSwitchCheck() {
        super(DEFAULT_MAX);
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.LITERAL_SWITCH,
        };
    }

    @Override
    public int[] getAcceptableTokens() {
        return getDefaultTokens();
    }

    @Override
    public void visitToken(DetailAST aAST) {
        nestIn(aAST, MSG_KEY);
    }

    @Override
    public void leaveToken(DetailAST aAST) {
        nestOut();
    }
}
