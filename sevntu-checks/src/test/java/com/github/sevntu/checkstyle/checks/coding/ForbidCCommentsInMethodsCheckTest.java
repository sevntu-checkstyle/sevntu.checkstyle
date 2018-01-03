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

package com.github.sevntu.checkstyle.checks.coding;

import static com.github.sevntu.checkstyle.checks.coding.ForbidCCommentsInMethodsCheck.MSG_KEY;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class ForbidCCommentsInMethodsCheckTest extends AbstractModuleTestSupport {

    private final String warningMessage = getCheckMessage(MSG_KEY);

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/coding";
    }

    @Test
    public void defaultTest()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidCCommentsInMethodsCheck.class);
        final String[] expected = {
            "10: " + warningMessage,
            "17: " + warningMessage,
            "26: " + warningMessage,
            "33: " + warningMessage,
            "45: " + warningMessage,
            "52: " + warningMessage,
            "61: " + warningMessage,
            "68: " + warningMessage,
        };
        verify(checkConfig, getPath("InputForbidCCommentsInMethodsCheck.java"), expected);
    }

    @Test
    public void testFileWithoutComments()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidCCommentsInMethodsCheck.class);
        final String[] expected = {};
        verify(checkConfig, getPath("InputForbidCCommentsInMethodsCheck2.java"), expected);
    }

    @Test
    public void testInterface()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidCCommentsInMethodsCheck.class);
        final String[] expected = {};
        verify(checkConfig, getPath("InputForbidCCommentsInMethodsCheck3.java"), expected);
    }

}
