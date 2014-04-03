////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2010  Oliver Burn
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

import static com.github.sevntu.checkstyle.checks.coding.UnnecessaryParenthesesExtendedCheck.*;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

/**
 * Test fixture for the UnnecessaryParenthesesCheck.
 * 
 * @author Eric K. Roe
 * @author Antonenko Dmitriy
 */
public class UnnecessaryParenthesesExtendedCheckTest extends BaseCheckTestSupport
{
	private static final String TEST_FILE = "InputUnnecessaryParentheses.java";

	@Test
	public void testDefault() throws Exception
	{
		final DefaultConfiguration checkConfig =
				createCheckConfig(UnnecessaryParenthesesExtendedCheck.class);
		checkConfig.addAttribute("ignoreCalculationOfBooleanVariables", "false");
		checkConfig.addAttribute("ignoreCalculationOfBooleanVariablesWithReturn", "false");
		checkConfig.addAttribute("ignoreCalculationOfBooleanVariablesWithAssert", "false");
		final String[] expected = {
				"4:22: " + getCheckMessage(MSG_KEY_ASSIGN),
				"4:29: " + getCheckMessage(MSG_KEY_EXPR),
				"4:31: " + getCheckMessage(MSG_KEY_IDENT, "i"),
				"4:46: " + getCheckMessage(MSG_KEY_ASSIGN),
				"5:15: " + getCheckMessage(MSG_KEY_ASSIGN),
				"6:14: " + getCheckMessage(MSG_KEY_IDENT, "x"),
				"6:17: " + getCheckMessage(MSG_KEY_ASSIGN),
				"7:15: " + getCheckMessage(MSG_KEY_ASSIGN),
				"8:14: " + getCheckMessage(MSG_KEY_IDENT, "x"),
				"8:17: " + getCheckMessage(MSG_KEY_ASSIGN),
				"11:22: " + getCheckMessage(MSG_KEY_ASSIGN),
				"11:30: " + getCheckMessage(MSG_KEY_IDENT, "i"),
				"11:46: " + getCheckMessage(MSG_KEY_ASSIGN),
				"15:17: " + getCheckMessage(MSG_KEY_LITERAL, "0"),
				"25:11: " + getCheckMessage(MSG_KEY_ASSIGN),
				"29:11: " + getCheckMessage(MSG_KEY_ASSIGN),
				"31:11: " + getCheckMessage(MSG_KEY_ASSIGN),
				"33:11: " + getCheckMessage(MSG_KEY_ASSIGN),
				"34:16: " + getCheckMessage(MSG_KEY_IDENT, "a"),
				"35:14: " + getCheckMessage(MSG_KEY_IDENT, "a"),
				"35:20: " + getCheckMessage(MSG_KEY_IDENT, "b"),
				"35:26: " + getCheckMessage(MSG_KEY_LITERAL, "600"),
				"35:40: " + getCheckMessage(MSG_KEY_LITERAL, "12.5f"),
				"35:56: " + getCheckMessage(MSG_KEY_IDENT, "arg2"),
				"36:14: " + getCheckMessage(MSG_KEY_STRING, "\"this\""),
				"36:25: " + getCheckMessage(MSG_KEY_STRING, "\"that\""),
				"37:11: " + getCheckMessage(MSG_KEY_ASSIGN),
				"37:14: " + getCheckMessage(MSG_KEY_STRING, "\"this is a really, really...\""),
				"39:16: " + getCheckMessage(MSG_KEY_RETURN),
				"43:21: " + getCheckMessage(MSG_KEY_LITERAL, "1"),
				"43:26: " + getCheckMessage(MSG_KEY_LITERAL, "13.5"),
				"44:22: " + getCheckMessage(MSG_KEY_LITERAL, "true"),
				"45:17: " + getCheckMessage(MSG_KEY_IDENT, "b"),
				"49:17: " + getCheckMessage(MSG_KEY_ASSIGN),
				"51:11: " + getCheckMessage(MSG_KEY_ASSIGN),
				"53:16: " + getCheckMessage(MSG_KEY_RETURN),
				"63:13: " + getCheckMessage(MSG_KEY_EXPR),
				"67:16: " + getCheckMessage(MSG_KEY_EXPR),
				"72:19: " + getCheckMessage(MSG_KEY_EXPR),
				"73:23: " + getCheckMessage(MSG_KEY_LITERAL, "4000"),
				"78:19: " + getCheckMessage(MSG_KEY_ASSIGN),
				"80:11: " + getCheckMessage(MSG_KEY_ASSIGN),
				"80:16: " + getCheckMessage(MSG_KEY_LITERAL, "3"),
				"81:27: " + getCheckMessage(MSG_KEY_ASSIGN),
		};

		verify(checkConfig, getPath(TEST_FILE), expected);
	}

	@Test
	public void test15Extensions() throws Exception
	{
		final DefaultConfiguration checkConfig = createCheckConfig(UnnecessaryParenthesesExtendedCheck.class);
		final String[] expected = {};
		checkConfig.addAttribute("ignoreCalculationOfBooleanVariables", "false");
		checkConfig.addAttribute("ignoreCalculationOfBooleanVariablesWithReturn", "false");
		checkConfig.addAttribute("ignoreCalculationOfBooleanVariablesWithAssert", "false");
		verify(checkConfig, getPath("Input15Extensions.java"), expected);
	}

	@Test
	public void testUBV() throws Exception
	{
		final DefaultConfiguration checkConfig = createCheckConfig(UnnecessaryParenthesesExtendedCheck.class);
		final String[] expected = {
				"4:22: " + getCheckMessage(MSG_KEY_ASSIGN),
				"4:29: " + getCheckMessage(MSG_KEY_EXPR),
				"4:31: " + getCheckMessage(MSG_KEY_IDENT, "i"),
				"4:46: " + getCheckMessage(MSG_KEY_ASSIGN),
				"5:15: " + getCheckMessage(MSG_KEY_ASSIGN),
				"6:14: " + getCheckMessage(MSG_KEY_IDENT, "x"),
				"6:17: " + getCheckMessage(MSG_KEY_ASSIGN),
				"7:15: " + getCheckMessage(MSG_KEY_ASSIGN),
				"8:14: " + getCheckMessage(MSG_KEY_IDENT, "x"),
				"8:17: " + getCheckMessage(MSG_KEY_ASSIGN),
				"11:22: " + getCheckMessage(MSG_KEY_ASSIGN),
				"11:30: " + getCheckMessage(MSG_KEY_IDENT, "i"),
				"11:46: " + getCheckMessage(MSG_KEY_ASSIGN),
				"15:17: " + getCheckMessage(MSG_KEY_LITERAL, "0"),
				"25:11: " + getCheckMessage(MSG_KEY_ASSIGN),
				"29:11: " + getCheckMessage(MSG_KEY_ASSIGN),
				"31:11: " + getCheckMessage(MSG_KEY_ASSIGN),
				"33:11: " + getCheckMessage(MSG_KEY_ASSIGN),
				"34:16: " + getCheckMessage(MSG_KEY_IDENT, "a"),
				"35:14: " + getCheckMessage(MSG_KEY_IDENT, "a"),
				"35:20: " + getCheckMessage(MSG_KEY_IDENT, "b"),
				"35:26: " + getCheckMessage(MSG_KEY_LITERAL, "600"),
				"35:40: " + getCheckMessage(MSG_KEY_LITERAL, "12.5f"),
				"35:56: " + getCheckMessage(MSG_KEY_IDENT, "arg2"),
				"36:14: " + getCheckMessage(MSG_KEY_STRING, "\"this\""),
				"36:25: " + getCheckMessage(MSG_KEY_STRING, "\"that\""),
				"37:11: " + getCheckMessage(MSG_KEY_ASSIGN),
				"37:14: " + getCheckMessage(MSG_KEY_STRING, "\"this is a really, really...\""),
				"39:16: " + getCheckMessage(MSG_KEY_RETURN),
				"43:21: " + getCheckMessage(MSG_KEY_LITERAL, "1"),
				"43:26: " + getCheckMessage(MSG_KEY_LITERAL, "13.5"),
				"44:22: " + getCheckMessage(MSG_KEY_LITERAL, "true"),
				"45:17: " + getCheckMessage(MSG_KEY_IDENT, "b"),
				"49:17: " + getCheckMessage(MSG_KEY_ASSIGN),
				"51:11: " + getCheckMessage(MSG_KEY_ASSIGN),
				"53:16: " + getCheckMessage(MSG_KEY_RETURN),
				"63:13: " + getCheckMessage(MSG_KEY_EXPR),
				"67:16: " + getCheckMessage(MSG_KEY_EXPR),
				"72:19: " + getCheckMessage(MSG_KEY_EXPR),
				"73:23: " + getCheckMessage(MSG_KEY_LITERAL, "4000"),
				"78:19: " + getCheckMessage(MSG_KEY_ASSIGN),
				"80:11: " + getCheckMessage(MSG_KEY_ASSIGN),
				"80:16: " + getCheckMessage(MSG_KEY_LITERAL, "3"),
				"81:27: " + getCheckMessage(MSG_KEY_ASSIGN),
				"95:19: " + getCheckMessage(MSG_KEY_ASSIGN),
				};
		checkConfig.addAttribute("ignoreCalculationOfBooleanVariables", "true");
		checkConfig.addAttribute("ignoreCalculationOfBooleanVariablesWithReturn", "true");
		checkConfig.addAttribute("ignoreCalculationOfBooleanVariablesWithAssert", "true");
		verify(checkConfig, getPath("testUBV.java"), expected);
	}
}
