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

import static com.github.sevntu.checkstyle.checks.coding.UselessSuperCtorCallCheck.MSG_IN_NOT_DERIVED_CLASS;
import static com.github.sevntu.checkstyle.checks.coding.UselessSuperCtorCallCheck.MSG_WITHOUT_ARGS;

import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class UselessSuperCtorCallCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/coding";
    }

    @Test
    public void testSingleCtorWithSuperWithinNotDerivedClass()
            throws Exception {
        final DefaultConfiguration mDefaultConfig =
                createModuleConfig(UselessSuperCtorCallCheck.class);
        final String[] expected = {
            "7:9: " + getCheckMessage(MSG_IN_NOT_DERIVED_CLASS, "InputUselessSuperCtorCallCheck1"),
        };

        verify(mDefaultConfig, getPath("InputUselessSuperCtorCallCheck1.java"), expected);
    }

    @Test
    public void testSingleCtorWithSuperWithinDerivedClass()
            throws Exception {
        final DefaultConfiguration mDefaultConfig =
                createModuleConfig(UselessSuperCtorCallCheck.class);
        final String[] expected = {
            "7:9: " + getCheckMessage(MSG_WITHOUT_ARGS, "InputUselessSuperCtorCallCheck2"),
        };

        verify(mDefaultConfig, getPath("InputUselessSuperCtorCallCheck2.java"), expected);
    }

    @Test
    public void testMultipleCtorsWithSuperWithinNotDerivedClass()
            throws Exception {
        final DefaultConfiguration mDefaultConfig =
                createModuleConfig(UselessSuperCtorCallCheck.class);
        final String[] expected = {
            "7:9: " + getCheckMessage(MSG_IN_NOT_DERIVED_CLASS, "InputUselessSuperCtorCallCheck3"),
            "12:9: " + getCheckMessage(MSG_IN_NOT_DERIVED_CLASS, "InputUselessSuperCtorCallCheck3"),
        };

        verify(mDefaultConfig, getPath("InputUselessSuperCtorCallCheck3.java"), expected);
    }

    @Test
    public void testInnerClassWithCtorWithSuper()
            throws Exception {
        final DefaultConfiguration mDefaultConfig =
                createModuleConfig(UselessSuperCtorCallCheck.class);
        final String[] expected = {
            "9:13: " + getCheckMessage(MSG_IN_NOT_DERIVED_CLASS, "Inner"),
        };

        verify(mDefaultConfig, getPath("InputUselessSuperCtorCallCheck4.java"), expected);
    }

    @Test
    public void testClassWithSuperCtorWithArgs()
            throws Exception {
        final DefaultConfiguration mDefaultConfig =
                createModuleConfig(UselessSuperCtorCallCheck.class);
        final String[] expected = {};

        verify(mDefaultConfig, getPath("InputUselessSuperCtorCallCheck5.java"), expected);
    }

    @Test
    public void testOptionAllowCallToNoArgsSuperCtor()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(UselessSuperCtorCallCheck.class);
        checkConfig.addProperty("allowCallToNoArgsSuperCtor", "true");

        final String[] expected = {};

        verify(checkConfig, getPath("InputUselessSuperCtorCallCheck6.java"), expected);
    }

    @Test
    public void testOptionAllowCallToNoArgsSuperCtorIfMultiplePublicCtor()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(UselessSuperCtorCallCheck.class);
        checkConfig.addProperty("allowCallToNoArgsSuperCtorIfMultiplePublicCtor", "true");

        final String[] expected = {
            "26:13: " + getCheckMessage(MSG_WITHOUT_ARGS, "DerivedTwo"),
        };

        verify(checkConfig, getPath("InputUselessSuperCtorCallCheck7.java"), expected);
    }

}
