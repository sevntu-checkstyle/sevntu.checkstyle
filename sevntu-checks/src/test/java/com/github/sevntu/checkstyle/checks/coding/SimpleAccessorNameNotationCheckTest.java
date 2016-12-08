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

import static com.github.sevntu.checkstyle.checks.coding.SimpleAccessorNameNotationCheck.MSG_KEY_GETTER;
import static com.github.sevntu.checkstyle.checks.coding.SimpleAccessorNameNotationCheck.MSG_KEY_SETTER;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class SimpleAccessorNameNotationCheckTest extends BaseCheckTestSupport {

    private final String warningGetterMessage = getCheckMessage(MSG_KEY_GETTER);
    private final String warningSetterMessage = getCheckMessage(MSG_KEY_SETTER);

    @Test
    public void test() throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(SimpleAccessorNameNotationCheck.class);

        checkConfig.addAttribute("prefix", "m");

        final String[] expected = {
            "7: " + warningSetterMessage,
            "10: " + warningGetterMessage,
            "13: " + warningSetterMessage,
            "16: " + warningGetterMessage,
            "25: " + warningSetterMessage,
            "28: " + warningGetterMessage,
        };

        verify(checkConfig, getPath("InputSimpleAccessorNameNotation.java"), expected);
    }

    @Test
    public void correctWithoutPrefix() throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(SimpleAccessorNameNotationCheck.class);
        final String[] expected = {
            "17: " + warningSetterMessage,
            "22: " + warningGetterMessage,
        };
        verify(checkConfig, getPath("InputSimpleAccessorNameNotation1.java"), expected);
    }

    @Test
    public void correctWithtPrefix() throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(SimpleAccessorNameNotationCheck.class);
        checkConfig.addAttribute("prefix", "m");
        final String[] expected = {
            "7: " + warningSetterMessage,
            "12: " + warningGetterMessage,
        };
        verify(checkConfig, getPath("InputSimpleAccessorNameNotation1.java"), expected);
    }

    @Test
    public void partlyWithoutPrefix() throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(SimpleAccessorNameNotationCheck.class);
        final String[] expected = {
            "15: " + warningGetterMessage,
            "20: " + warningSetterMessage,
            "30: " + warningSetterMessage,
            "35: " + warningGetterMessage,
            "40: " + warningSetterMessage,
            "45: " + warningGetterMessage,
        };
        verify(checkConfig, getPath("InputSimpleAccessorNameNotation2.java"), expected);
    }

    @Test
    public void partlyWithPrefix() throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(SimpleAccessorNameNotationCheck.class);
        checkConfig.addAttribute("prefix", "m");
        final String[] expected = {
            "10: " + warningSetterMessage,
            "15: " + warningGetterMessage,
            "20: " + warningSetterMessage,
            "25: " + warningGetterMessage,
            "35: " + warningGetterMessage,
            "40: " + warningSetterMessage,
        };
        verify(checkConfig, getPath("InputSimpleAccessorNameNotation2.java"), expected);
    }

    @Test
    public void errorsWithoutPrefix() throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(SimpleAccessorNameNotationCheck.class);
        final String[] expected = {
            "7: " + warningSetterMessage,
            "12: " + warningGetterMessage,
            "17: " + warningSetterMessage,
            "22: " + warningGetterMessage,
        };
        verify(checkConfig, getPath("InputSimpleAccessorNameNotation3.java"), expected);
    }

    @Test
    public void errorsWithPrefix() throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(SimpleAccessorNameNotationCheck.class);
        checkConfig.addAttribute("prefix", "m");
        final String[] expected = {
            "7: " + warningSetterMessage,
            "12: " + warningGetterMessage,
            "17: " + warningSetterMessage,
            "22: " + warningGetterMessage,
        };
        verify(checkConfig, getPath("InputSimpleAccessorNameNotation3.java"), expected);
    }

    @Test
    public void cleanWithoutPrefix() throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(SimpleAccessorNameNotationCheck.class);
        final String[] expected = {};
        verify(checkConfig, getPath("InputSimpleAccessorNameNotation4.java"), expected);
    }

    @Test
    public void cleanWithPrefix() throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(SimpleAccessorNameNotationCheck.class);
        checkConfig.addAttribute("prefix", "m");
        final String[] expected = {};
        verify(checkConfig, getPath("InputSimpleAccessorNameNotation4.java"), expected);
    }

    @Test
    public void testBoolean() throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(SimpleAccessorNameNotationCheck.class);
        final String[] expected = {
            "28: " + warningSetterMessage,
            "33: " + warningGetterMessage,
        };
        verify(checkConfig, getPath("InputSimpleAccessorNameNotation5.java"), expected);
    }

    @Test
    public void testAnonymousCases() throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(SimpleAccessorNameNotationCheck.class);
        final String[] expected = {};
        verify(checkConfig, getPath("InputSimpleAccessorNameNotation6.java"), expected);
    }

    @Test
    public void testInterface() throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(SimpleAccessorNameNotationCheck.class);
        final String[] expected = {};
        verify(checkConfig, getPath("InputSimpleAccessorNameNotation7.java"), expected);
    }
}
