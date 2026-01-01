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

import static com.github.sevntu.checkstyle.checks.coding.IllegalCatchExtendedCheck.MSG_KEY;

import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class IllegalCatchExtendedCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/coding";
    }

    @Test
    public final void testDefault() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(IllegalCatchExtendedCheck.class);
        final String[] expected = {
            "9:9: " + getCheckMessage(MSG_KEY, "RuntimeException"),
            "11:9: " + getCheckMessage(MSG_KEY, "java.lang.Exception"),
            "13:9: " + getCheckMessage(MSG_KEY, "Throwable"),
            "24:9: " + getCheckMessage(MSG_KEY, "RuntimeException"),
            "29:9: " + getCheckMessage(MSG_KEY, "java.lang.Exception"),
            "34:9: " + getCheckMessage(MSG_KEY, "Throwable"),
        };

        checkConfig.addProperty("allowThrow", "false");
        checkConfig.addProperty("allowRethrow", "false");

        verify(checkConfig, getPath("InputIllegalCatchExtendedCheckNew.java"), expected);
    }

    @Test
    public final void testThrowPermit() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(IllegalCatchExtendedCheck.class);
        final String[] expected = {
            "9:9: " + getCheckMessage(MSG_KEY, "RuntimeException"),
            "11:9: " + getCheckMessage(MSG_KEY, "java.lang.Exception"),
            "13:9: " + getCheckMessage(MSG_KEY, "Throwable"),
            "29:9: " + getCheckMessage(MSG_KEY, "java.lang.Exception"),
            "34:9: " + getCheckMessage(MSG_KEY, "Throwable"),
        };

        checkConfig.addProperty("allowThrow", "true");
        checkConfig.addProperty("allowRethrow", "false");

        verify(checkConfig, getPath("InputIllegalCatchExtendedCheckNew.java"), expected);
    }

    @Test
    public final void testReThrowPermit() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(IllegalCatchExtendedCheck.class);
        checkConfig.addProperty("illegalClassNames",
                                 "java.lang.Error, java.lang.Exception, java.lang.Throwable");

        final String[] expected = {
            "11:9: " + getCheckMessage(MSG_KEY, "java.lang.Exception"),
            "13:9: " + getCheckMessage(MSG_KEY, "Throwable"),
        };

        checkConfig.addProperty("allowThrow", "false");
        checkConfig.addProperty("allowRethrow", "true");

        verify(checkConfig, getPath("InputIllegalCatchExtendedCheckNew.java"), expected);
    }

    @Test
    public void testIllegalClassNames() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(IllegalCatchExtendedCheck.class);
        checkConfig.addProperty("illegalClassNames",
                                 "java.lang.Error, java.lang.Exception, NullPointerException");

        // check that incorrect names don't break the Check
        checkConfig.addProperty("illegalClassNames",
                "java.lang.IOException.");

        final String[] expected = {
            "11:9: " + getCheckMessage(MSG_KEY, "java.lang.Exception"),
        };

        verify(checkConfig, getPath("InputIllegalCatchExtendedCheckNew.java"), expected);
    }

}
