////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2018 the original author or authors.
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

package com.github.sevntu.checkstyle.checks.sizes;

import static com.github.sevntu.checkstyle.checks.sizes.LineLengthExtendedCheck.MSG_KEY;
import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Test;

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
        checkConfig.addAttribute("max", "80");
        checkConfig.addAttribute("ignorePattern", "^.*is OK.*regexp.*$");
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
        checkConfig.addAttribute("max", "40");
        checkConfig.addAttribute("ignorePattern", "^.*is OK.*regexp.*$");
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
        checkConfig.addAttribute("ignoreClass", "true");
        checkConfig.addAttribute("ignoreConstructor", "true");
        checkConfig.addAttribute("ignoreField", "true");
        checkConfig.addAttribute("ignoreMethod", "true");
        verify(checkConfig, getPath("InputLineLengthExtendedCheck.java"), expected);
    }

    @Test
    public void testProperty() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(LineLengthExtendedCheck.class);

        checkConfig.addAttribute("ignorePattern", "[");

        final String[] expected = {};

        try {
            verify(checkConfig, getPath("InputLineLengthExtendedCheck.java"), expected);
            fail();
        }
        catch (CheckstyleException ex) {
            final String messagePrefix = "cannot initialize module "
                + "com.puppycrawl.tools.checkstyle.TreeWalker - "
                + "Cannot set property 'ignorePattern' to '[' in module ";
            Assert.assertTrue("Exception's message does not start with: " + messagePrefix,
                ex.getMessage().startsWith(messagePrefix));
        }
    }

}
