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

import static com.github.sevntu.checkstyle.checks.coding.MultipleVariableDeclarationsExtendedCheck.MSG_VAR_DECLARATIONS;
import static com.github.sevntu.checkstyle.checks.coding.MultipleVariableDeclarationsExtendedCheck.MSG_VAR_DECLARATIONS_COMMA;

import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

/** Test class. */
public class MultipleVariableDeclarationsExtendedCheckTest extends AbstractModuleTestSupport {

    private final String msgVarDeclarationsComa = getCheckMessage(MSG_VAR_DECLARATIONS_COMMA);
    private final String msgVarDeclarations = getCheckMessage(MSG_VAR_DECLARATIONS);

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/coding";
    }

    @Test
    public void testStandartSituation() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(MultipleVariableDeclarationsExtendedCheck.class);

        final String[] expected = {
            "3:5: " + msgVarDeclarationsComa,
            "4:5: " + msgVarDeclarations,
            "7:9: " + msgVarDeclarationsComa,
            "8:9: " + msgVarDeclarations,
            "12:5: " + msgVarDeclarations,
            "15:5: " + msgVarDeclarations,
            "20:14: " + msgVarDeclarationsComa,
        };

        checkConfig.addProperty("ignoreCycles", "false");
        checkConfig.addProperty("ignoreMethods", "false");

        verify(checkConfig, getPath("InputMultipleVariableDeclarationsExtendedCheck.java"),
                expected);
    }

    @Test
    public void testIgnoreCycles() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(MultipleVariableDeclarationsExtendedCheck.class);

        final String[] expected = {
            "3:5: " + msgVarDeclarationsComa,
            "4:5: " + msgVarDeclarations,
            "7:9: " + msgVarDeclarationsComa,
            "8:9: " + msgVarDeclarations,
            "12:5: " + msgVarDeclarations,
            "15:5: " + msgVarDeclarations,
        };

        checkConfig.addProperty("ignoreCycles", "true");
        checkConfig.addProperty("ignoreMethods", "false");
        verify(checkConfig, getPath("InputMultipleVariableDeclarationsExtendedCheck.java"),
                expected);
    }

    @Test
    public void testIgnoreMethods() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(MultipleVariableDeclarationsExtendedCheck.class);

        final String[] expected = {
            "3:5: " + msgVarDeclarationsComa,
            "4:5: " + msgVarDeclarations,
            "12:5: " + msgVarDeclarations,
            "15:5: " + msgVarDeclarations,
            "20:14: " + msgVarDeclarationsComa,
        };

        checkConfig.addProperty("ignoreCycles", "false");
        checkConfig.addProperty("ignoreMethods", "true");
        verify(checkConfig, getPath("InputMultipleVariableDeclarationsExtendedCheck.java"),
                expected);
    }

    @Test
    public void testIgnoreMethodsAndIgnoreCycles() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(MultipleVariableDeclarationsExtendedCheck.class);

        final String[] expected = {
            "3:5: " + msgVarDeclarationsComa,
            "4:5: " + msgVarDeclarations,
            "12:5: " + msgVarDeclarations,
            "15:5: " + msgVarDeclarations,
        };

        checkConfig.addProperty("ignoreCycles", "true");
        checkConfig.addProperty("ignoreMethods", "true");
        verify(checkConfig, getPath("InputMultipleVariableDeclarationsExtendedCheck.java"),
                expected);
    }

}
