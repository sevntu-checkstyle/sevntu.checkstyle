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

import static com.github.sevntu.checkstyle.checks.coding.NumericLiteralNeedsUnderscoreCheck.MSG_KEY;
import static org.junit.Assert.fail;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.internal.WhiteboxImpl;

import com.github.sevntu.checkstyle.checks.coding.NumericLiteralNeedsUnderscoreCheck.NumericType;
import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

@RunWith(PowerMockRunner.class)
@PrepareForTest(NumericType.class)
public class NumericLiteralNeedsUnderscoreCheckTest extends AbstractModuleTestSupport {

    private static final String EXCEPTION_MESSAGE = "Unexpected numeric type ";

    private final String warningMessage = getCheckMessage(MSG_KEY);

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/coding";
    }

    @Test
    public void test() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(NumericLiteralNeedsUnderscoreCheck.class);
        final String[] expected = {
            "27:22: " + warningMessage,
            "28:25: " + warningMessage,
            "29:25: " + warningMessage,
            "30:26: " + warningMessage,
            "31:28: " + warningMessage,
            "32:22: " + warningMessage,
            "33:25: " + warningMessage,
            "50:23: " + warningMessage,
            "51:23: " + warningMessage,
            "52:23: " + warningMessage,
            "53:24: " + warningMessage,
            "54:24: " + warningMessage,
            "55:24: " + warningMessage,
            "56:24: " + warningMessage,
            "57:24: " + warningMessage,
            "75:23: " + warningMessage,
            "76:23: " + warningMessage,
            "77:23: " + warningMessage,
            "78:23: " + warningMessage,
            "79:23: " + warningMessage,
            "80:25: " + warningMessage,
            "81:25: " + warningMessage,
            "82:27: " + warningMessage,
            "83:27: " + warningMessage,
            "84:29: " + warningMessage,
            "85:29: " + warningMessage,
            "99:23: " + warningMessage,
            "100:23: " + warningMessage,
            "101:25: " + warningMessage,
            "102:25: " + warningMessage,
            "103:25: " + warningMessage,
            "104:25: " + warningMessage,
        };
        verify(checkConfig, getPath("InputNumericLiteralNeedsUnderscoreCheck.java"),
                expected);
    }

    @Test
    public void testWithConfig() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(NumericLiteralNeedsUnderscoreCheck.class);
        checkConfig.addAttribute("minDecimalSymbolLength", "1");
        checkConfig.addAttribute("maxDecimalSymbolsUntilUnderscore", "3");
        checkConfig.addAttribute("minHexSymbolLength", "1");
        checkConfig.addAttribute("maxHexSymbolsUntilUnderscore", "2");
        checkConfig.addAttribute("minBinarySymbolLength", "1");
        checkConfig.addAttribute("maxBinarySymbolsUntilUnderscore", "4");
        final String[] expected = {
            "23:22: " + warningMessage,
            "24:24: " + warningMessage,
            "25:26: " + warningMessage,
            "26:28: " + warningMessage,
            "27:22: " + warningMessage,
            "28:25: " + warningMessage,
            "44:23: " + warningMessage,
            "45:23: " + warningMessage,
            "46:23: " + warningMessage,
            "47:24: " + warningMessage,
            "48:24: " + warningMessage,
            "49:24: " + warningMessage,
            "50:24: " + warningMessage,
            "67:23: " + warningMessage,
            "68:23: " + warningMessage,
            "69:23: " + warningMessage,
            "70:25: " + warningMessage,
            "71:27: " + warningMessage,
            "72:27: " + warningMessage,
            "73:29: " + warningMessage,
            "74:29: " + warningMessage,
            "86:23: " + warningMessage,
            "87:23: " + warningMessage,
            "88:25: " + warningMessage,
            "89:25: " + warningMessage,
        };
        verify(checkConfig, getPath("InputNumericLiteralNeedsUnderscoreCheck2.java"),
                expected);
    }

    @Test
    public void testIgnore() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(NumericLiteralNeedsUnderscoreCheck.class);
        final String[] expected = {
            "7:36: " + warningMessage,
            "8:37: " + warningMessage,
            "10:30: " + warningMessage,
            "15:44: " + warningMessage,
            "16:44: " + warningMessage,
            "17:39: " + warningMessage,
            "22:28: " + warningMessage,
            "24:24: " + warningMessage,
            "30:19: " + warningMessage,
            "31:19: " + warningMessage,
            "32:19: " + warningMessage,
            "33:19: " + warningMessage,
            "40:32: " + warningMessage,
        };
        verify(checkConfig,
                new File("src/test/resources-noncompilable/com/github/sevntu/"
                        + "checkstyle/checks/coding/InputNumericLiteralNeedsUnderscoreCheck3.java")
                        .getCanonicalPath(), expected);
    }

    @Test
    public void testConfiguredIgnore() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(NumericLiteralNeedsUnderscoreCheck.class);
        checkConfig.addAttribute("ignoreFieldNamePattern", "RED");
        checkConfig.addAttribute("minDecimalSymbolLength", "1");
        final String[] expected = {
            "7:36: " + warningMessage,
            "8:37: " + warningMessage,
            "10:30: " + warningMessage,
            "14:59: " + warningMessage,
            "15:44: " + warningMessage,
            "17:39: " + warningMessage,
            "22:28: " + warningMessage,
            "24:24: " + warningMessage,
            "30:19: " + warningMessage,
            "31:19: " + warningMessage,
            "32:19: " + warningMessage,
            "33:19: " + warningMessage,
            "38:55: " + warningMessage,
        };
        verify(checkConfig,
                new File("src/test/resources-noncompilable/com/github/sevntu/"
                        + "checkstyle/checks/coding/InputNumericLiteralNeedsUnderscoreCheck3.java")
                        .getCanonicalPath(), expected);
    }

    @Test
    public void testMinSymbolsBeforeCheckingSwitchReflection() throws Exception {
        try {
            final NumericLiteralNeedsUnderscoreCheck check =
                    new NumericLiteralNeedsUnderscoreCheck();
            final NumericType mockType = PowerMockito.mock(NumericType.class);
            WhiteboxImpl.invokeMethod(check, "minSymbolsBeforeChecking", mockType);
            fail();
        }
        catch (IllegalStateException ex) {
            Assert.assertTrue("Exception's message does not start with: " + EXCEPTION_MESSAGE,
                ex.getMessage().startsWith(EXCEPTION_MESSAGE));
        }
    }

    @Test
    public void testMaxSymbolsUntilUnderscoreSwitchReflection() throws Exception {
        try {
            final NumericLiteralNeedsUnderscoreCheck check =
                    new NumericLiteralNeedsUnderscoreCheck();
            final NumericType mockType = PowerMockito.mock(NumericType.class);
            WhiteboxImpl.invokeMethod(check, "maxSymbolsUntilUnderscore", mockType);
            fail();
        }
        catch (IllegalStateException ex) {
            Assert.assertTrue("Exception's message does not start with: " + EXCEPTION_MESSAGE,
                ex.getMessage().startsWith(EXCEPTION_MESSAGE));
        }
    }

    @Test
    public void testGetNumericSegmentsSwitchReflection() throws Exception {
        try {
            final NumericLiteralNeedsUnderscoreCheck check =
                    new NumericLiteralNeedsUnderscoreCheck();
            final NumericType mockType = PowerMockito.mock(NumericType.class);
            WhiteboxImpl.invokeMethod(check, "getNumericSegments", "", mockType);
            fail();
        }
        catch (IllegalStateException ex) {
            Assert.assertTrue("Exception's message does not start with: " + EXCEPTION_MESSAGE,
                ex.getMessage().startsWith(EXCEPTION_MESSAGE));
        }
    }

    @Test
    public void testRemovePrePostfixByTypeSwitchReflection() throws Exception {
        try {
            final NumericLiteralNeedsUnderscoreCheck check =
                    new NumericLiteralNeedsUnderscoreCheck();
            final NumericType mockType = PowerMockito.mock(NumericType.class);
            WhiteboxImpl.invokeMethod(check, "removePrePostfixByType", "", mockType);
            fail();
        }
        catch (IllegalStateException ex) {
            Assert.assertTrue("Exception's message does not start with: " + EXCEPTION_MESSAGE,
                ex.getMessage().startsWith(EXCEPTION_MESSAGE));
        }
    }

}
