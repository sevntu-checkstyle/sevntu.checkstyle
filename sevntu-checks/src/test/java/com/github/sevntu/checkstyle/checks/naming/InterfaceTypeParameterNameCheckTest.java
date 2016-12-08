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

package com.github.sevntu.checkstyle.checks.naming;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class InterfaceTypeParameterNameCheckTest
    extends BaseCheckTestSupport {
    /** Warning message key. */
    private static final String MSG_KEY = "name.invalidPattern";

    @Test
    public void testInterfaceDefault()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createCheckConfig(InterfaceTypeParameterNameCheck.class);

        final String[] expected = {
            "5:51: " + getCheckMessage(MSG_KEY, "it", "^[A-Z]$"),
            "9:27: " + getCheckMessage(MSG_KEY, "foo", "^[A-Z]$"),
            "18:34: " + getCheckMessage(MSG_KEY, "Taa", "^[A-Z]$"),
            "18:52: " + getCheckMessage(MSG_KEY, "Vaa", "^[A-Z]$"),
        };
        verify(checkConfig, getPath("InputInterfaceTypeParameterName.java"), expected);
    }

    @Test
    public void testInterfaceFooName()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createCheckConfig(InterfaceTypeParameterNameCheck.class);
        checkConfig.addAttribute("format", "^foo$");
        final String[] expected = {
            "5:51: " + getCheckMessage(MSG_KEY, "it", "^foo$"),
            "13:27: " + getCheckMessage(MSG_KEY, "A", "^foo$"),
            "18:34: " + getCheckMessage(MSG_KEY, "Taa", "^foo$"),
            "18:52: " + getCheckMessage(MSG_KEY, "Vaa", "^foo$"),
        };
        verify(checkConfig, getPath("InputInterfaceTypeParameterName.java"), expected);
    }
}
