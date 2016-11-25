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

import static com.github.sevntu.checkstyle.checks.coding.TernaryPerExpressionCountCheck.MSG_KEY;

import org.junit.Assert;
import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;

/**
 * @author <a href="mailto:nesterenko-aleksey@list.ru"> Aleksey Nesterenko</a>
 */
public class TernaryPerExpressionCountCheckTest extends BaseCheckTestSupport {

    private final DefaultConfiguration checkConfig = createCheckConfig(TernaryPerExpressionCountCheck.class);

    @Test
    public void testWithDefaultTernaryPerExpressionCountValue()
            throws Exception {
        int maxTernaryOperatorsCount = 1;
        boolean ternaryInBraces = true;
        boolean oneLine = true;
        checkConfig.addAttribute("ignoreTernaryOperatorsInBraces",
                Boolean.toString(ternaryInBraces));
        checkConfig.addAttribute("ignoreIsolatedTernaryOnLine",
                Boolean.toString(oneLine));
        checkConfig.addAttribute("maxTernaryPerExpressionCount",
                Integer.toString(maxTernaryOperatorsCount));
        String[] expected = {
                "14:26: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "18:32: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "20:33: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "21:26: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "25:56: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "26:50: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "30:47: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "47:36: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                };
        verify(checkConfig,
                getPath("InputTernaryPerExpressionCountCheck.java"), expected);
    }

    @Test
    public void testWithNegativeTernaryPerExpressionCountValue()
            throws Exception {
        int maxTernaryOperatorsCount = -1;
        boolean ternaryInBraces = true;
        boolean oneLine = false;
        checkConfig.addAttribute("ignoreTernaryOperatorsInBraces",
                Boolean.toString(ternaryInBraces));
        checkConfig.addAttribute("ignoreIsolatedTernaryOnLine",
                Boolean.toString(oneLine));
        checkConfig.addAttribute("maxTernaryPerExpressionCount",
                Integer.toString(maxTernaryOperatorsCount));
        String[] expected = {};

        try {
            verify(checkConfig,
                    getPath("InputTernaryPerExpressionCountCheck.java"),
                    expected);
            Assert.fail();
        } catch (CheckstyleException e) {
            String errorMsg = e.getMessage();
            Assert.assertTrue(errorMsg
                    .contains("Cannot set property 'maxTernaryPerExpressionCount' to '-1' in module "
                            + "com.github.sevntu.checkstyle.checks.coding.TernaryPerExpressionCountCheck"));
        }
    }

    @Test
    public void testWithDifferentTernaryPerExpressionCountValue()
            throws Exception {
        boolean ternaryInBraces = true;
        boolean oneLine = false;
        int maxTernaryOperatorsCount = 2;
        checkConfig.addAttribute("ignoreTernaryOperatorsInBraces",
                Boolean.toString(ternaryInBraces));
        checkConfig.addAttribute("ignoreIsolatedTernaryOnLine",
                Boolean.toString(oneLine));
        checkConfig.addAttribute("maxTernaryPerExpressionCount",
                Integer.toString(maxTernaryOperatorsCount));
        String[] expected = { "14:26: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount) };
        verify(checkConfig,
                getPath("InputTernaryPerExpressionCountCheck.java"), expected);
    }

    @Test
    public void testWithZeroValue() throws Exception {
        boolean ternaryInBraces = false;
        boolean oneLine = true;
        int maxTernaryOperatorsCount = 0;
        checkConfig.addAttribute("ignoreTernaryOperatorsInBraces",
                Boolean.toString(ternaryInBraces));
        checkConfig.addAttribute("ignoreIsolatedTernaryOnLine",
                Boolean.toString(oneLine));
        checkConfig.addAttribute("maxTernaryPerExpressionCount",
                Integer.toString(maxTernaryOperatorsCount));
        String[] expected = {
                "14:26: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "18:32: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "20:33: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "21:26: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "25:56: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "26:50: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "30:47: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "31:52: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "35:38: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "36:38: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "37:31: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "38:23: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "47:36: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "101:48: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount)
                };
        verify(checkConfig,
                getPath("InputTernaryPerExpressionCountCheck.java"), expected);
    }

    @Test
    public void testWithoutIgnoringExpressionInBraces() throws Exception {
        boolean ternaryInBraces = false;
        boolean oneLine = true;
        int maxTernaryOperatorsCount = 1;
        checkConfig.addAttribute("ignoreTernaryOperatorsInBraces",
                Boolean.toString(ternaryInBraces));
        checkConfig.addAttribute("ignoreIsolatedTernaryOnLine",
                Boolean.toString(oneLine));
        String[] expected = {
                "14:26: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "18:32: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "20:33: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "21:26: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "25:56: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "26:50: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "30:47: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "31:52: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "35:38: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "36:38: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "37:31: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "38:23: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "47:36: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "101:48: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount)
                };
        verify(checkConfig,
                getPath("InputTernaryPerExpressionCountCheck.java"), expected);
    }

    @Test
    public void testWithoutIgnoringSingleTernariesPerLine() throws Exception {
        boolean ternaryInBraces = false;
        boolean oneLine = false;
        int maxTernaryOperatorsCount = 1;
        checkConfig.addAttribute("ignoreTernaryOperatorsInBraces",
                Boolean.toString(ternaryInBraces));
        checkConfig.addAttribute("ignoreIsolatedTernaryOnLine",
                Boolean.toString(oneLine));
        String[] expected = {
                "14:26: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "18:32: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "20:33: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "21:26: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "25:56: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "26:50: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "30:47: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "31:52: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "35:38: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "36:38: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "37:31: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "38:23: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "47:36: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "49:47: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "57:41: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "67:39: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "79:41: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "91:29: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "94:29: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "99:29: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "101:48: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount)
                };
        verify(checkConfig,
                getPath("InputTernaryPerExpressionCountCheck.java"), expected);
    }
    
    @Test
    public void testWithIgnoringOneTernaryPerLine() throws Exception {
        boolean ternaryInBraces = false;
        boolean oneLine = true;
        int maxTernaryOperatorsCount = 0;
        checkConfig.addAttribute("ignoreTernaryOperatorsInBraces",
                Boolean.toString(ternaryInBraces));
        checkConfig.addAttribute("ignoreIsolatedTernaryOnLine",
                Boolean.toString(oneLine));
        checkConfig.addAttribute("maxTernaryPerExpressionCount",
                Integer.toString(maxTernaryOperatorsCount));
        String[] expected = { 
                "14:26: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "18:32: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "20:33: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "21:26: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "25:56: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "26:50: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "30:47: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "31:52: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "35:38: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "36:38: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "37:31: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "38:23: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "47:36: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount),
                "101:48: " + getCheckMessage(MSG_KEY, maxTernaryOperatorsCount)
                };
        verify(checkConfig,
                getPath("InputTernaryPerExpressionCountCheck.java"), expected);
    }

}
