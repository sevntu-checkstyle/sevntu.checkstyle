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

import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class InterfaceTypeParameterNameCheckTest extends AbstractModuleTestSupport {

    /** Warning message key. */
    private static final String MSG_KEY = "name.invalidPattern";

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/naming";
    }

    @Test
    public void testInterfaceDefault()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(InterfaceTypeParameterNameCheck.class);

        final String[] expected = {
            "5:56: " + getCheckMessage(MSG_KEY, "it", "^[A-Z]$"),
            "9:27: " + getCheckMessage(MSG_KEY, "foo", "^[A-Z]$"),
            "18:34: " + getCheckMessage(MSG_KEY, "Taa", "^[A-Z]$"),
            "18:52: " + getCheckMessage(MSG_KEY, "Vaa", "^[A-Z]$"),
        };
        verify(checkConfig, getPath("InputInterfaceTypeParameterNameCheck.java"), expected);
    }

    @Test
    public void testInterfaceFooName()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(InterfaceTypeParameterNameCheck.class);
        checkConfig.addProperty("format", "^foo$");
        final String[] expected = {
            "5:56: " + getCheckMessage(MSG_KEY, "it", "^foo$"),
            "13:27: " + getCheckMessage(MSG_KEY, "A", "^foo$"),
            "18:34: " + getCheckMessage(MSG_KEY, "Taa", "^foo$"),
            "18:52: " + getCheckMessage(MSG_KEY, "Vaa", "^foo$"),
        };
        verify(checkConfig, getPath("InputInterfaceTypeParameterNameCheck.java"), expected);
    }

}
