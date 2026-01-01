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

import static com.github.sevntu.checkstyle.checks.design.CheckstyleTestMakeupCheck.MSG_KEY_CONFIG_NOT_ASSIGNED;
import static com.github.sevntu.checkstyle.checks.design.CheckstyleTestMakeupCheck.MSG_KEY_CONFIG_NOT_ASSIGNED_PROPERLY;
import static com.github.sevntu.checkstyle.checks.design.CheckstyleTestMakeupCheck.MSG_KEY_CONFIG_NOT_ASSIGNED_WITH;
import static com.github.sevntu.checkstyle.checks.design.CheckstyleTestMakeupCheck.MSG_KEY_CONFIG_NOT_FOUND;
import static com.github.sevntu.checkstyle.checks.design.CheckstyleTestMakeupCheck.MSG_KEY_UNKNOWN_PROPERTY;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.DetailAstImpl;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class CheckstyleTestMakeupCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/design";
    }

    @Test
    public void testMiscFile() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(CheckstyleTestMakeupCheck.class);

        final String[] expected = {};

        verify(checkConfig, getPath("InputCheckstyleTestMakeupCheck.java"), expected);
    }

    @Test
    public void testValidFile() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(CheckstyleTestMakeupCheck.class);

        final String[] expected = {};

        verify(checkConfig, getPath("InputCheckstyleTestMakeupCheckValid.java"), expected);
    }

    @Test
    public void testInvalidFile() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(CheckstyleTestMakeupCheck.class);

        final String[] expected = {
            "8:5: " + getCheckMessage(MSG_KEY_CONFIG_NOT_FOUND),
            "15:9: " + getCheckMessage(MSG_KEY_CONFIG_NOT_ASSIGNED),
            "21:37: " + getCheckMessage(MSG_KEY_CONFIG_NOT_ASSIGNED_WITH),
            "27:9: " + getCheckMessage(MSG_KEY_CONFIG_NOT_ASSIGNED_PROPERLY),
            "36:45: " + getCheckMessage(MSG_KEY_UNKNOWN_PROPERTY),
            "37:53: " + getCheckMessage(MSG_KEY_UNKNOWN_PROPERTY),
            "38:44: " + getCheckMessage(MSG_KEY_UNKNOWN_PROPERTY),
            "39:50: " + getCheckMessage(MSG_KEY_UNKNOWN_PROPERTY),
            "40:48: " + getCheckMessage(MSG_KEY_UNKNOWN_PROPERTY),
            "41:55: " + getCheckMessage(MSG_KEY_UNKNOWN_PROPERTY),
            "42:33: " + getCheckMessage(MSG_KEY_UNKNOWN_PROPERTY),
            "43:36: " + getCheckMessage(MSG_KEY_UNKNOWN_PROPERTY),
            "44:35: " + getCheckMessage(MSG_KEY_UNKNOWN_PROPERTY),
        };

        verify(checkConfig, getPath("InputCheckstyleTestMakeupCheckInvalid.java"), expected);
    }

    @Test
    public void testInvalidFileIgnoreVerifies() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(CheckstyleTestMakeupCheck.class);
        checkConfig.addProperty("verifyMethodRegexp", "BAD");

        final String[] expected = {};

        verify(checkConfig, getPath("InputCheckstyleTestMakeupCheckInvalid.java"), expected);
    }

    @Test
    public void testInvalidFileIgnoreCreates() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(CheckstyleTestMakeupCheck.class);
        checkConfig.addProperty("createMethodRegexp", "BAD");

        final String[] expected = {
            "16:36: " + getCheckMessage(MSG_KEY_CONFIG_NOT_ASSIGNED_WITH),
            "22:43: " + getCheckMessage(MSG_KEY_CONFIG_NOT_ASSIGNED_WITH),
            "28:36: " + getCheckMessage(MSG_KEY_CONFIG_NOT_ASSIGNED_WITH),
            "53:36: " + getCheckMessage(MSG_KEY_CONFIG_NOT_ASSIGNED_WITH),
        };

        verify(checkConfig, getPath("InputCheckstyleTestMakeupCheckValid.java"), expected);
    }

    @Test
    public void testInvalidToken() {
        final DetailAstImpl parent = new DetailAstImpl();
        parent.setType(TokenTypes.OBJBLOCK);

        final CheckstyleTestMakeupCheck check = new CheckstyleTestMakeupCheck();
        try {
            check.visitToken(parent);
            fail("exception expected");
        }
        catch (IllegalArgumentException exc) {
            Assertions.assertEquals("Found unsupported token: OBJBLOCK", exc.getMessage());
        }
    }

}
