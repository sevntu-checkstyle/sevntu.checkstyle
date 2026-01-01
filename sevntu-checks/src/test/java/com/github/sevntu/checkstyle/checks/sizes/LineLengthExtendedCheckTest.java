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

package com.github.sevntu.checkstyle.checks.sizes;

import static com.github.sevntu.checkstyle.checks.sizes.LineLengthExtendedCheck.MSG_KEY;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;

public class LineLengthExtendedCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/sizes";
    }

    @Test
    public void testSimple()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(LineLengthExtendedCheck.class);
        checkConfig.addProperty("max", "80");
        checkConfig.addProperty("ignorePattern", "^.*is OK.*regexp.*$");
        final String[] expected = {
            "18: " + getCheckMessage(MSG_KEY, 80, 81),
            "145: " + getCheckMessage(MSG_KEY, 80, 83),
        };
        verify(checkConfig, getPath("InputLineLengthExtendedCheck.java"), expected);
    }

    @Test
    public void testSimpleIgnore()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(LineLengthExtendedCheck.class);
        checkConfig.addProperty("max", "40");
        checkConfig.addProperty("ignorePattern", "^.*is OK.*regexp.*$");
        final String[] expected = {
            "1: " + getCheckMessage(MSG_KEY, 40, 80),
            "5: " + getCheckMessage(MSG_KEY, 40, 80),
            "6: " + getCheckMessage(MSG_KEY, 40, 50),
            "18: " + getCheckMessage(MSG_KEY, 40, 81),
            "101: " + getCheckMessage(MSG_KEY, 40, 45),
            "125: " + getCheckMessage(MSG_KEY, 40, 42),
            "128: " + getCheckMessage(MSG_KEY, 40, 43),
            "132: " + getCheckMessage(MSG_KEY, 40, 43),
            "145: " + getCheckMessage(MSG_KEY, 40, 83),
            "146: " + getCheckMessage(MSG_KEY, 40, 78),
            "148: " + getCheckMessage(MSG_KEY, 40, 57),
            "151: " + getCheckMessage(MSG_KEY, 40, 74),
            "152: " + getCheckMessage(MSG_KEY, 40, 75),
            "192: " + getCheckMessage(MSG_KEY, 40, 66),
            "200: " + getCheckMessage(MSG_KEY, 40, 58),
            "207: " + getCheckMessage(MSG_KEY, 40, 50),
        };
        checkConfig.addProperty("ignoreClass", "true");
        checkConfig.addProperty("ignoreConstructor", "true");
        checkConfig.addProperty("ignoreField", "true");
        checkConfig.addProperty("ignoreMethod", "true");
        verify(checkConfig, getPath("InputLineLengthExtendedCheck.java"), expected);
    }

    @Test
    public void testProperty() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(LineLengthExtendedCheck.class);

        checkConfig.addProperty("ignorePattern", "[");

        final String[] expected = {};

        try {
            verify(checkConfig, getPath("InputLineLengthExtendedCheck.java"), expected);
            fail("exception expected");
        }
        catch (CheckstyleException exc) {
            final String messagePrefix = "cannot initialize module"
                    + " com.puppycrawl.tools.checkstyle.TreeWalker - cannot initialize module"
                    + " com.github.sevntu.checkstyle.checks.sizes.LineLengthExtendedCheck -"
                    + " Cannot set property 'ignorePattern' to '['";
            Assertions.assertTrue(exc.getMessage().startsWith(messagePrefix),
                "Exception's message does not start with: " + messagePrefix);
        }
    }

}
