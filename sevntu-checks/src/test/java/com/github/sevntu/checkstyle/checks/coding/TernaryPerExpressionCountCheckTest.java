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

import static com.github.sevntu.checkstyle.checks.coding.TernaryPerExpressionCountCheck.MSG_KEY;

import org.junit.Assert;
import org.junit.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;

/**
 * @author <a href="mailto:nesterenko-aleksey@list.ru"> Aleksey Nesterenko</a>
 */
public class TernaryPerExpressionCountCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/coding";
    }

    @Test
    public void testWithDefaultTernaryPerExpressionCountValue()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(TernaryPerExpressionCountCheck.class);
        checkConfig.addAttribute("ignoreTernaryOperatorsInBraces",
                "true");
        checkConfig.addAttribute("ignoreIsolatedTernaryOnLine",
                "true");
        checkConfig.addAttribute("maxTernaryPerExpressionCount",
                "1");
        final String[] expected = {
            "14:26: " + getCheckMessage(MSG_KEY, 1),
            "18:32: " + getCheckMessage(MSG_KEY, 1),
            "20:33: " + getCheckMessage(MSG_KEY, 1),
            "21:26: " + getCheckMessage(MSG_KEY, 1),
            "25:56: " + getCheckMessage(MSG_KEY, 1),
            "26:50: " + getCheckMessage(MSG_KEY, 1),
            "30:47: " + getCheckMessage(MSG_KEY, 1),
            "47:36: " + getCheckMessage(MSG_KEY, 1),
        };
        verify(checkConfig,
                getPath("InputTernaryPerExpressionCountCheck.java"), expected);
    }

    @Test
    public void testWithNegativeTernaryPerExpressionCountValue()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(TernaryPerExpressionCountCheck.class);
        checkConfig.addAttribute("ignoreTernaryOperatorsInBraces",
                "true");
        checkConfig.addAttribute("ignoreIsolatedTernaryOnLine",
                "false");
        checkConfig.addAttribute("maxTernaryPerExpressionCount",
                "-1");
        final String[] expected = {};

        try {
            verify(checkConfig,
                    getPath("InputTernaryPerExpressionCountCheck.java"),
                    expected);
            Assert.fail();
        }
        catch (CheckstyleException ex) {
            final String errorMsg = ex.getMessage();
            final String expectedMessage = "Cannot set property 'maxTernaryPerExpressionCount' "
                + "to '-1' in module com.github.sevntu.checkstyle.checks.coding."
                + "TernaryPerExpressionCountCheck";
            Assert.assertTrue("Expected error message is missing: " + expectedMessage,
                errorMsg.contains(expectedMessage));
        }
    }

    @Test
    public void testWithDifferentTernaryPerExpressionCountValue()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(TernaryPerExpressionCountCheck.class);
        checkConfig.addAttribute("ignoreTernaryOperatorsInBraces",
                "true");
        checkConfig.addAttribute("ignoreIsolatedTernaryOnLine",
                "false");
        checkConfig.addAttribute("maxTernaryPerExpressionCount",
                "2");
        final String[] expected = {"14:26: " + getCheckMessage(MSG_KEY, 2)};
        verify(checkConfig,
                getPath("InputTernaryPerExpressionCountCheck.java"), expected);
    }

    @Test
    public void testWithZeroValue() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(TernaryPerExpressionCountCheck.class);
        checkConfig.addAttribute("ignoreTernaryOperatorsInBraces",
                "false");
        checkConfig.addAttribute("ignoreIsolatedTernaryOnLine",
                "true");
        checkConfig.addAttribute("maxTernaryPerExpressionCount",
                "0");
        final String[] expected = {
            "14:26: " + getCheckMessage(MSG_KEY, 0),
            "18:32: " + getCheckMessage(MSG_KEY, 0),
            "20:33: " + getCheckMessage(MSG_KEY, 0),
            "21:26: " + getCheckMessage(MSG_KEY, 0),
            "25:56: " + getCheckMessage(MSG_KEY, 0),
            "26:50: " + getCheckMessage(MSG_KEY, 0),
            "30:47: " + getCheckMessage(MSG_KEY, 0),
            "31:52: " + getCheckMessage(MSG_KEY, 0),
            "35:38: " + getCheckMessage(MSG_KEY, 0),
            "36:38: " + getCheckMessage(MSG_KEY, 0),
            "37:31: " + getCheckMessage(MSG_KEY, 0),
            "38:23: " + getCheckMessage(MSG_KEY, 0),
            "47:36: " + getCheckMessage(MSG_KEY, 0),
            "101:48: " + getCheckMessage(MSG_KEY, 0),
        };
        verify(checkConfig,
                getPath("InputTernaryPerExpressionCountCheck.java"), expected);
    }

    @Test
    public void testWithoutIgnoringExpressionInBraces() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(TernaryPerExpressionCountCheck.class);
        checkConfig.addAttribute("ignoreTernaryOperatorsInBraces",
                "false");
        checkConfig.addAttribute("ignoreIsolatedTernaryOnLine",
                "true");
        final String[] expected = {
            "14:26: " + getCheckMessage(MSG_KEY, 1),
            "18:32: " + getCheckMessage(MSG_KEY, 1),
            "20:33: " + getCheckMessage(MSG_KEY, 1),
            "21:26: " + getCheckMessage(MSG_KEY, 1),
            "25:56: " + getCheckMessage(MSG_KEY, 1),
            "26:50: " + getCheckMessage(MSG_KEY, 1),
            "30:47: " + getCheckMessage(MSG_KEY, 1),
            "31:52: " + getCheckMessage(MSG_KEY, 1),
            "35:38: " + getCheckMessage(MSG_KEY, 1),
            "36:38: " + getCheckMessage(MSG_KEY, 1),
            "37:31: " + getCheckMessage(MSG_KEY, 1),
            "38:23: " + getCheckMessage(MSG_KEY, 1),
            "47:36: " + getCheckMessage(MSG_KEY, 1),
            "101:48: " + getCheckMessage(MSG_KEY, 1),
        };
        verify(checkConfig,
                getPath("InputTernaryPerExpressionCountCheck.java"), expected);
    }

    @Test
    public void testWithoutIgnoringSingleTernariesPerLine() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(TernaryPerExpressionCountCheck.class);
        checkConfig.addAttribute("ignoreTernaryOperatorsInBraces",
                "false");
        checkConfig.addAttribute("ignoreIsolatedTernaryOnLine",
                "false");
        final String[] expected = {
            "14:26: " + getCheckMessage(MSG_KEY, 1),
            "18:32: " + getCheckMessage(MSG_KEY, 1),
            "20:33: " + getCheckMessage(MSG_KEY, 1),
            "21:26: " + getCheckMessage(MSG_KEY, 1),
            "25:56: " + getCheckMessage(MSG_KEY, 1),
            "26:50: " + getCheckMessage(MSG_KEY, 1),
            "30:47: " + getCheckMessage(MSG_KEY, 1),
            "31:52: " + getCheckMessage(MSG_KEY, 1),
            "35:38: " + getCheckMessage(MSG_KEY, 1),
            "36:38: " + getCheckMessage(MSG_KEY, 1),
            "37:31: " + getCheckMessage(MSG_KEY, 1),
            "38:23: " + getCheckMessage(MSG_KEY, 1),
            "47:36: " + getCheckMessage(MSG_KEY, 1),
            "49:47: " + getCheckMessage(MSG_KEY, 1),
            "57:41: " + getCheckMessage(MSG_KEY, 1),
            "67:39: " + getCheckMessage(MSG_KEY, 1),
            "79:41: " + getCheckMessage(MSG_KEY, 1),
            "91:29: " + getCheckMessage(MSG_KEY, 1),
            "94:29: " + getCheckMessage(MSG_KEY, 1),
            "99:29: " + getCheckMessage(MSG_KEY, 1),
            "101:48: " + getCheckMessage(MSG_KEY, 1),
        };
        verify(checkConfig,
                getPath("InputTernaryPerExpressionCountCheck.java"), expected);
    }

    @Test
    public void testWithIgnoringOneTernaryPerLine() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(TernaryPerExpressionCountCheck.class);
        checkConfig.addAttribute("ignoreTernaryOperatorsInBraces",
                "false");
        checkConfig.addAttribute("ignoreIsolatedTernaryOnLine",
                "true");
        checkConfig.addAttribute("maxTernaryPerExpressionCount",
                "0");
        final String[] expected = {
            "14:26: " + getCheckMessage(MSG_KEY, 0),
            "18:32: " + getCheckMessage(MSG_KEY, 0),
            "20:33: " + getCheckMessage(MSG_KEY, 0),
            "21:26: " + getCheckMessage(MSG_KEY, 0),
            "25:56: " + getCheckMessage(MSG_KEY, 0),
            "26:50: " + getCheckMessage(MSG_KEY, 0),
            "30:47: " + getCheckMessage(MSG_KEY, 0),
            "31:52: " + getCheckMessage(MSG_KEY, 0),
            "35:38: " + getCheckMessage(MSG_KEY, 0),
            "36:38: " + getCheckMessage(MSG_KEY, 0),
            "37:31: " + getCheckMessage(MSG_KEY, 0),
            "38:23: " + getCheckMessage(MSG_KEY, 0),
            "47:36: " + getCheckMessage(MSG_KEY, 0),
            "101:48: " + getCheckMessage(MSG_KEY, 0),
        };
        verify(checkConfig,
                getPath("InputTernaryPerExpressionCountCheck.java"), expected);
    }

}
