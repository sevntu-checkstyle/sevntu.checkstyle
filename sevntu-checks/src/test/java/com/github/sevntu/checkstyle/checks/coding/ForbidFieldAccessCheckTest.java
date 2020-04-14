////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2020 the original author or authors.
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

import static com.github.sevntu.checkstyle.checks.coding.ForbidFieldAccessCheck.MSG_KEY;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

/**
 * @author <a href="mailto:yasser.aziza@gmail.com"> Yasser Aziza </a>
 */
public class ForbidFieldAccessCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/coding";
    }

    @Test
    public void testImportDefaultField() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(ForbidFieldAccessCheck.class);

        final String[] expected = {
            "12:36: " + getCheckMessage(MSG_KEY, "java.util.Locale.ROOT"),
        };

        verify(checkConfig, getPath("InputForbidFieldAccessCheckTest.java"), expected);
    }

    @Test
    public void testStaticImportDefaultField() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(ForbidFieldAccessCheck.class);

        final String[] expected = {
            "5:1: " + getCheckMessage(MSG_KEY, "java.util.Locale.ROOT"),
        };

        verify(checkConfig, getPath("InputForbidFieldAccessCheckStaticTest.java"), expected);
    }

    @Test
    public void testImportInteger() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(ForbidFieldAccessCheck.class);

        checkConfig.addAttribute("packageName", "java.lang");
        checkConfig.addAttribute("className", "Integer");
        checkConfig.addAttribute("fieldName", "MAX_VALUE");

        final String[] expected = {
            "17:20: " + getCheckMessage(MSG_KEY, "java.lang.Integer.MAX_VALUE"),
        };

        verify(checkConfig, getPath("InputForbidFieldAccessCheckTest.java"), expected);
    }

    @Test
    public void testStaticImportInteger() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(ForbidFieldAccessCheck.class);

        checkConfig.addAttribute("packageName", "java.lang");
        checkConfig.addAttribute("className", "Integer");
        checkConfig.addAttribute("fieldName", "MAX_VALUE");

        final String[] expected = {
            "6:1: " + getCheckMessage(MSG_KEY, "java.lang.Integer.MAX_VALUE"),
        };

        verify(checkConfig, getPath("InputForbidFieldAccessCheckStaticTest.java"), expected);
    }

}
