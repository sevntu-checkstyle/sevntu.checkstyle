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

import static com.github.sevntu.checkstyle.checks.coding.SimpleAccessorNameNotationCheck.MSG_KEY_GETTER;
import static com.github.sevntu.checkstyle.checks.coding.SimpleAccessorNameNotationCheck.MSG_KEY_SETTER;

import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class SimpleAccessorNameNotationCheckTest extends AbstractModuleTestSupport {

    private final String warningGetterMessage = getCheckMessage(MSG_KEY_GETTER);
    private final String warningSetterMessage = getCheckMessage(MSG_KEY_SETTER);

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/coding";
    }

    @Test
    public void test() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(SimpleAccessorNameNotationCheck.class);

        checkConfig.addProperty("prefix", "m");

        final String[] expected = {
            "7:9: " + warningSetterMessage,
            "10:9: " + warningGetterMessage,
            "13:9: " + warningSetterMessage,
            "16:9: " + warningGetterMessage,
            "25:9: " + warningSetterMessage,
            "28:9: " + warningGetterMessage,
        };

        verify(checkConfig, getPath("InputSimpleAccessorNameNotationCheck.java"), expected);
    }

    @Test
    public void correctWithoutPrefix() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(SimpleAccessorNameNotationCheck.class);
        final String[] expected = {
            "17:5: " + warningSetterMessage,
            "22:5: " + warningGetterMessage,
        };
        verify(checkConfig, getPath("InputSimpleAccessorNameNotationCheck1.java"), expected);
    }

    @Test
    public void correctWithPrefix() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(SimpleAccessorNameNotationCheck.class);
        checkConfig.addProperty("prefix", "m");
        final String[] expected = {
            "7:5: " + warningSetterMessage,
            "12:5: " + warningGetterMessage,
        };
        verify(checkConfig, getPath("InputSimpleAccessorNameNotationCheck1.java"), expected);
    }

    @Test
    public void partlyWithoutPrefix() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(SimpleAccessorNameNotationCheck.class);
        final String[] expected = {
            "15:5: " + warningGetterMessage,
            "20:5: " + warningSetterMessage,
            "30:5: " + warningSetterMessage,
            "35:5: " + warningGetterMessage,
            "40:5: " + warningSetterMessage,
            "45:5: " + warningGetterMessage,
        };
        verify(checkConfig, getPath("InputSimpleAccessorNameNotationCheck2.java"), expected);
    }

    @Test
    public void partlyWithPrefix() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(SimpleAccessorNameNotationCheck.class);
        checkConfig.addProperty("prefix", "m");
        final String[] expected = {
            "10:5: " + warningSetterMessage,
            "15:5: " + warningGetterMessage,
            "20:5: " + warningSetterMessage,
            "25:5: " + warningGetterMessage,
            "35:5: " + warningGetterMessage,
            "40:5: " + warningSetterMessage,
        };
        verify(checkConfig, getPath("InputSimpleAccessorNameNotationCheck2.java"), expected);
    }

    @Test
    public void errorsWithoutPrefix() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(SimpleAccessorNameNotationCheck.class);
        final String[] expected = {
            "7:5: " + warningSetterMessage,
            "12:5: " + warningGetterMessage,
            "17:5: " + warningSetterMessage,
            "22:5: " + warningGetterMessage,
        };
        verify(checkConfig, getPath("InputSimpleAccessorNameNotationCheck3.java"), expected);
    }

    @Test
    public void errorsWithPrefix() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(SimpleAccessorNameNotationCheck.class);
        checkConfig.addProperty("prefix", "m");
        final String[] expected = {
            "7:5: " + warningSetterMessage,
            "12:5: " + warningGetterMessage,
            "17:5: " + warningSetterMessage,
            "22:5: " + warningGetterMessage,
        };
        verify(checkConfig, getPath("InputSimpleAccessorNameNotationCheck3.java"), expected);
    }

    @Test
    public void cleanWithoutPrefix() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(SimpleAccessorNameNotationCheck.class);
        final String[] expected = {};
        verify(checkConfig, getPath("InputSimpleAccessorNameNotationCheck4.java"), expected);
    }

    @Test
    public void cleanWithPrefix() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(SimpleAccessorNameNotationCheck.class);
        checkConfig.addProperty("prefix", "m");
        final String[] expected = {};
        verify(checkConfig, getPath("InputSimpleAccessorNameNotationCheck4.java"), expected);
    }

    @Test
    public void testBoolean() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(SimpleAccessorNameNotationCheck.class);
        final String[] expected = {
            "28:5: " + warningSetterMessage,
            "33:5: " + warningGetterMessage,
        };
        verify(checkConfig, getPath("InputSimpleAccessorNameNotationCheck5.java"), expected);
    }

    @Test
    public void testAnonymousCases() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(SimpleAccessorNameNotationCheck.class);
        final String[] expected = {};
        verify(checkConfig, getPath("InputSimpleAccessorNameNotationCheck6.java"), expected);
    }

    @Test
    public void testInterface() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(SimpleAccessorNameNotationCheck.class);
        final String[] expected = {};
        verify(checkConfig, getPath("InputSimpleAccessorNameNotationCheck7.java"), expected);
    }

    @Test
    public void testOverride() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(SimpleAccessorNameNotationCheck.class);
        final String[] expected = {};
        verify(checkConfig, getPath("InputSimpleAccessorNameNotationCheckOverride.java"), expected);
    }

}
