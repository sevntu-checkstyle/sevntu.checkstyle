////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2011  Oliver Burn
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

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class ReturnCountExtendedCheckTest extends BaseCheckTestSupport
{

    private final DefaultConfiguration checkConfig = createCheckConfig(ReturnCountExtendedCheck.class);

    @Test
    public void testMethodsMaxReturnLiteralsIsOne() throws Exception
    {

        checkConfig.addAttribute("maxReturnCount", "1");
        checkConfig.addAttribute("ignoreMethodLinesCount", "0"); // swithed off
        checkConfig.addAttribute("minIgnoreReturnDepth", "5");
        checkConfig.addAttribute("ignoreEmptyReturns", "false");
        checkConfig.addAttribute("topLinesToIgnoreCount", "0");

        String[] expected = {
            "25:16: " + createMsg("twoReturnsInMethod", "method", 2, 1),
            "37:16: " + createMsg("threeReturnsInMethod", "method", 3, 1),
            "57:16: " + createMsg("fourReturnsInMethod", "method", 4, 1),
        };

        verify(checkConfig, getPath("InputReturnCountExtendedCheckMethods.java"), expected);
    }

    @Test
    public void testConstructorsMaxReturnLiteralsIsOne() throws Exception
    {

        checkConfig.addAttribute("maxReturnCount", "1");
        checkConfig.addAttribute("ignoreMethodLinesCount", "0");
        checkConfig.addAttribute("minIgnoreReturnDepth", "5");
        checkConfig.addAttribute("ignoreEmptyReturns", "false");
        checkConfig.addAttribute("topLinesToIgnoreCount", "0");

        String[] expected = {
            "28:12: " + createMsg("InputReturnCountExtendedCheckCtors", "constructor", 2, 1),
            "41:12: " + createMsg("InputReturnCountExtendedCheckCtors", "constructor", 3, 1),
            "63:12: " + createMsg("InputReturnCountExtendedCheckCtors", "constructor", 4, 1),
        };

        verify(checkConfig, getPath("InputReturnCountExtendedCheckCtors.java"), expected);
    }

    @Test
    public void testignoreMethodLinesCount() throws Exception
    {

        checkConfig.addAttribute("maxReturnCount", "1");
        checkConfig.addAttribute("ignoreMethodLinesCount", "10");
        checkConfig.addAttribute("minIgnoreReturnDepth", "5");
        checkConfig.addAttribute("ignoreEmptyReturns", "false");
        checkConfig.addAttribute("topLinesToIgnoreCount", "0");

        String[] expected = {
            "37:16: " + createMsg("threeReturnsInMethod", "method", 3, 1),
            "57:16: " + createMsg("fourReturnsInMethod", "method", 4, 1),
        };

        verify(checkConfig, getPath("InputReturnCountExtendedCheckMethods.java"), expected);
    }

    @Test
    public void testignoreMethodLinesCount2() throws Exception
    {
        checkConfig.addAttribute("maxReturnCount", "1");
        checkConfig.addAttribute("ignoreMethodLinesCount", "20");
        checkConfig.addAttribute("minIgnoreReturnDepth", "5");
        checkConfig.addAttribute("ignoreEmptyReturns", "false");
        checkConfig.addAttribute("topLinesToIgnoreCount", "0");

        String[] expected = {
            "57:16: " + createMsg("fourReturnsInMethod", "method", 4, 1),
        };

        verify(checkConfig, getPath("InputReturnCountExtendedCheckMethods.java"), expected);
    }

    @Test
    public void testminIgnoreReturnDepth() throws Exception
    {
        checkConfig.addAttribute("maxReturnCount", "0");
        checkConfig.addAttribute("ignoreMethodLinesCount", "0");
        checkConfig.addAttribute("minIgnoreReturnDepth", "1");
        checkConfig.addAttribute("ignoreEmptyReturns", "false");
        checkConfig.addAttribute("topLinesToIgnoreCount", "0");

        String[] expected = {
            "6:16: " + createMsg("oneReturnInMethod", "method", 1, 0),
            "10:16: " + createMsg("oneReturnInMethod2", "method", 1, 0),
        };

        verify(checkConfig, getPath("InputReturnCountExtendedCheckMethods.java"), expected);
    }

    @Test
    public void testIgnoreEmptyReturns() throws Exception
    {
        checkConfig.addAttribute("maxReturnCount", "1");
        checkConfig.addAttribute("ignoreMethodLinesCount", "0");
        checkConfig.addAttribute("minIgnoreReturnDepth", "5");
        checkConfig.addAttribute("ignoreEmptyReturns", "true");
        checkConfig.addAttribute("topLinesToIgnoreCount", "0");

        String[] expected = {
            "28:12: " + createMsg("InputReturnCountExtendedCheckCtors", "constructor", 2, 1),
            "41:12: " + createMsg("InputReturnCountExtendedCheckCtors", "constructor", 3, 1),
            "63:12: " + createMsg("InputReturnCountExtendedCheckCtors", "constructor", 3, 1),
        };

        verify(checkConfig, getPath("InputReturnCountExtendedCheckCtors.java"), expected);
    }

    @Test
    public void testMethodsInMethods() throws Exception
    {
        checkConfig.addAttribute("maxReturnCount", "1");
        checkConfig.addAttribute("ignoreMethodLinesCount", "0");
        checkConfig.addAttribute("minIgnoreReturnDepth", "5");
        checkConfig.addAttribute("ignoreEmptyReturns", "false");
        checkConfig.addAttribute("topLinesToIgnoreCount", "0");

        String[] expected = {
            "100:24: " + createMsg("handleEvent", "method", 3, 1),
        };

        verify(checkConfig, getPath("InputReturnCountExtendedCheckMethodsInMethods.java"),
                expected);
    }

    @Test
    public void testIgnoreMethodsNamesProperty() throws Exception
    {
        checkConfig.addAttribute("maxReturnCount", "1");
        checkConfig.addAttribute("ignoreMethodLinesCount", "0"); // swithed off
        checkConfig.addAttribute("minIgnoreReturnDepth", "5");
        checkConfig.addAttribute("ignoreEmptyReturns", "false");
        checkConfig.addAttribute("topLinesToIgnoreCount", "0");
        checkConfig.addAttribute("ignoreMethodsNames", "threeReturnsInMethod, twoReturnsInMethod");

        String[] expected = {
            "57:16: " + createMsg("fourReturnsInMethod", "method", 4, 1),
        };

        verify(checkConfig, getPath("InputReturnCountExtendedCheckMethods.java"), expected);
    }

    private static String createMsg(String methodName, String methodType, int is,
            int max)
    {
        return "Return count for '" + methodName + "' " + methodType + " is "
                + is + " (max allowed is " + max + ").";
    }

	@Test
	public void testRegexIgnoreMethodsNamesProperty() throws Exception
	{
		checkConfig.addAttribute("maxReturnCount", "1");
		checkConfig.addAttribute("ignoreMethodLinesCount", "0"); // swithed off
		checkConfig.addAttribute("minIgnoreReturnDepth", "5");
		checkConfig.addAttribute("ignoreEmptyReturns", "false");
		checkConfig.addAttribute("topLinesToIgnoreCount", "0");
		checkConfig.addAttribute("ignoreMethodsNames", "(?iu)(?:TwO|Four)(?-iu)ReturnsInMethod");

		String[] expected = {
				"37:16: " + createMsg("threeReturnsInMethod", "method", 3, 1),
		};

		verify(checkConfig, getPath("InputReturnCountExtendedCheckMethods.java"), expected);
	}
}
