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

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

/** Test class. */
public class MultipleVariableDeclarationsCheckTest extends BaseCheckTestSupport
{
	@Test
	public void testStandartSituation() throws Exception
	{
		DefaultConfiguration checkConfig = createCheckConfig(MultipleVariableDeclarationsExtendedCheck.class);

		final String[] expected = {
				"3:5: Each variable declaration must be in its own statement.",
				"4:5: Only one variable definition per line allowed.",
				"7:9: Each variable declaration must be in its own statement.",
				"8:9: Only one variable definition per line allowed.",
				"12:5: Only one variable definition per line allowed.",
				"15:5: Only one variable definition per line allowed.",
				"20:14: Each variable declaration must be in its own statement.", };

		checkConfig.addAttribute("ignoreCycles", "false");
		checkConfig.addAttribute("ignoreMethods", "false");

		verify(checkConfig, getPath("InputMultipleVariableDeclarations.java"), expected);
	}

	@Test
	public void testIgnoreCycles() throws Exception
	{
		DefaultConfiguration checkConfig = createCheckConfig(MultipleVariableDeclarationsExtendedCheck.class);

		final String[] expected = {
				"3:5: Each variable declaration must be in its own statement.",
				"4:5: Only one variable definition per line allowed.",
				"7:9: Each variable declaration must be in its own statement.",
				"8:9: Only one variable definition per line allowed.",
				"12:5: Only one variable definition per line allowed.",
				"15:5: Only one variable definition per line allowed.",
				//   "20:14: Each variable declaration must be in its own statement.",
		};

		checkConfig.addAttribute("ignoreCycles", "true");
		checkConfig.addAttribute("ignoreMethods", "false");
		verify(checkConfig, getPath("InputMultipleVariableDeclarations.java"), expected);
	}

	@Test
	public void testIgnoreMethods() throws Exception
	{
		DefaultConfiguration checkConfig = createCheckConfig(MultipleVariableDeclarationsExtendedCheck.class);

		final String[] expected = {
				"3:5: Each variable declaration must be in its own statement.",
				"4:5: Only one variable definition per line allowed.",
				// "7:9: Each variable declaration must be in its own statement.",
				// "8:9: Only one variable definition per line allowed.",
				"12:5: Only one variable definition per line allowed.",
				"15:5: Only one variable definition per line allowed.",
				"20:14: Each variable declaration must be in its own statement.", };

		checkConfig.addAttribute("ignoreCycles", "false");
		checkConfig.addAttribute("ignoreMethods", "true");
		verify(checkConfig, getPath("InputMultipleVariableDeclarations.java"), expected);
	}

	@Test
	public void testIgnoreMethodsAndIgnoreCycles() throws Exception
	{
		DefaultConfiguration checkConfig = createCheckConfig(MultipleVariableDeclarationsExtendedCheck.class);

		final String[] expected = {
				"3:5: Each variable declaration must be in its own statement.",
				"4:5: Only one variable definition per line allowed.",
				// "7:9: Each variable declaration must be in its own statement.",
				// "8:9: Only one variable definition per line allowed.",
				"12:5: Only one variable definition per line allowed.",
				"15:5: Only one variable definition per line allowed.",
				// "20:14: Each variable declaration must be in its own statement.",
		};

		checkConfig.addAttribute("ignoreCycles", "true");
		checkConfig.addAttribute("ignoreMethods", "true");
		verify(checkConfig, getPath("InputMultipleVariableDeclarations.java"), expected);
	}

}