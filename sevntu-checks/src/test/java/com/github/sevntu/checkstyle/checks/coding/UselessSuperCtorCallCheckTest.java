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

package com.github.sevntu.checkstyle.checks.coding;

import static com.github.sevntu.checkstyle.checks.coding.UselessSuperCtorCallCheck.MSG_IN_NOT_DERIVED_CLASS;
import static com.github.sevntu.checkstyle.checks.coding.UselessSuperCtorCallCheck.MSG_WITHOUT_ARGS;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class UselessSuperCtorCallCheckTest extends BaseCheckTestSupport {
    private final DefaultConfiguration mDefaultConfig = createCheckConfig(UselessSuperCtorCallCheck.class);

    @Test
    public void testSingleCtorWithSuperWithinNotDerivedClass()
            throws Exception {
        final String[] expected = {
            "7:9: " + getCheckMessage(MSG_IN_NOT_DERIVED_CLASS, "InputUselessSuperCtorCall1"),
        };

        verify(mDefaultConfig, getPath("InputUselessSuperCtorCall1.java"), expected);
    }

    @Test
    public void testSingleCtorWithSuperWithinDerivedClass()
            throws Exception {
        final String[] expected = {
            "7:9: " + getCheckMessage(MSG_WITHOUT_ARGS, "InputUselessSuperCtorCall2"),
        };

        verify(mDefaultConfig, getPath("InputUselessSuperCtorCall2.java"), expected);
    }

    @Test
    public void testMultipleCtorsWithSuperWithinNotDerivedClass()
            throws Exception {
        final String[] expected = {
            "7:9: " + getCheckMessage(MSG_IN_NOT_DERIVED_CLASS, "InputUselessSuperCtorCall3"),
            "12:9: " + getCheckMessage(MSG_IN_NOT_DERIVED_CLASS, "InputUselessSuperCtorCall3"),
        };

        verify(mDefaultConfig, getPath("InputUselessSuperCtorCall3.java"), expected);
    }

    @Test
    public void testInnerClassWithCtorWithSuper()
            throws Exception {
        final String[] expected = {
            "9:13: " + getCheckMessage(MSG_IN_NOT_DERIVED_CLASS, "Inner"),
        };

        verify(mDefaultConfig, getPath("InputUselessSuperCtorCall4.java"), expected);
    }

    @Test
    public void testClassWithSuperCtorWithArgs()
            throws Exception {
        final String[] expected = {};

        verify(mDefaultConfig, getPath("InputUselessSuperCtorCall5.java"), expected);
    }

    @Test
    public void testOptionAllowCallToNoArgsSuperCtor()
            throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(UselessSuperCtorCallCheck.class);
        checkConfig.addAttribute("allowCallToNoArgsSuperCtor", "true");

        final String[] expected = {};

        verify(checkConfig, getPath("InputUselessSuperCtorCall6.java"), expected);
    }

    @Test
    public void testOptionAllowCallToNoArgsSuperCtorIfMultiplePublicCtor()
            throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(UselessSuperCtorCallCheck.class);
        checkConfig.addAttribute("allowCallToNoArgsSuperCtorIfMultiplePublicCtor", "true");

        final String[] expected = {
            "26:13: " + getCheckMessage(MSG_WITHOUT_ARGS, "DerivedTwo"),
        };

        verify(checkConfig, getPath("InputUselessSuperCtorCall7.java"), expected);
    }
}
