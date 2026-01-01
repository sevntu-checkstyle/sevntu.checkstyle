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
 * <p>
 * Nested switch block that checks <code>type</code> parameter should be converted into separate
 * method.<br>
 * To enable this check use following configuration:
 * </p>
 *
 * <pre>
 * &lt;module name=&quot;NestedSwitchCheck&quot;/&gt;
 * </pre>
 *
 * @author Damian Szczepanik (damianszczepanik@github)
 * @since 1.13.0
 */
public class NestedSwitchCheck extends AbstractCheck {

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY = "avoid.nested.switch";

    /** Maximum allowed nesting depth. */
    private int max;
    /** Current nesting depth. */
    private int depth;

    /**
     * Setter for maximum allowed nesting depth.
     *
     * @param max maximum allowed nesting depth.
     */
    public final void setMax(int max) {
        this.max = max;
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
    public final int[] getRequiredTokens() {
        return getDefaultTokens();
    }

    @Override
    public void beginTree(DetailAST rootAST) {
        depth = 0;
    }

    @Override
    public void visitToken(DetailAST aAST) {
        if (depth > max) {
            log(aAST, MSG_KEY, depth, max);
        }
        ++depth;
    }

    @Override
    public void leaveToken(DetailAST aAST) {
        --depth;
    }

}
