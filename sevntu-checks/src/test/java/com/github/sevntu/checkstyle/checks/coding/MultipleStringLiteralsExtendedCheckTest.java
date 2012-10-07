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

public class MultipleStringLiteralsExtendedCheckTest extends BaseCheckTestSupport
{
	@Test
	public void testIt() throws Exception
	{
		DefaultConfiguration checkConfig =
				createCheckConfig(MultipleStringLiteralsExtendedCheck.class);
		checkConfig.addAttribute("allowedDuplicates", "2");
		checkConfig.addAttribute("ignoreStringsRegexp", "");
		checkConfig.addAttribute("highlightAllDuplicates", "false");

		final String[] expected = {
				"5:16: The String \"StringContents\" appears 3 times in the file.",
				"8:17: The String \"\" appears 4 times in the file.",
				"10:23: The String \", \" appears 3 times in the file.",
		};

		verify(checkConfig, getPath("InputMultipleStringLiterals.java"), expected);
	}

	@Test
	public void testItAndShowAllWarnings()
			throws Exception
	{
		DefaultConfiguration checkConfig = createCheckConfig(MultipleStringLiteralsExtendedCheck.class);
		checkConfig.addAttribute("allowedDuplicates", "2");
		checkConfig.addAttribute("ignoreStringsRegexp", "");
		checkConfig.addAttribute("highlightAllDuplicates", "true");

		final String[] expected = {
				"5:16: The String \"StringContents\" appears 3 times in the file.",
				"8:17: The String \"\" appears 4 times in the file.",
				"8:22: The String \"\" appears 4 times in the file.",
				"9:17: The String \"\" appears 4 times in the file.",
				"9:22: The String \"\" appears 4 times in the file.",
				"10:23: The String \", \" appears 3 times in the file.",
				"10:30: The String \", \" appears 3 times in the file.",
				"10:37: The String \", \" appears 3 times in the file.",
				"13:21: The String \"StringContents\" appears 3 times in the file.",
				"14:28: The String \"StringContents\" appears 3 times in the file.", };

		verify(checkConfig, getPath("InputMultipleStringLiterals.java"), expected);
	}

	@Test
	public void testItIgnoreEmpty() throws Exception
	{
		DefaultConfiguration checkConfig =
				createCheckConfig(MultipleStringLiteralsExtendedCheck.class);
		checkConfig.addAttribute("allowedDuplicates", "2");
		checkConfig.addAttribute("highlightAllDuplicates", "false");

		final String[] expected = {
				"5:16: The String \"StringContents\" appears 3 times in the file.",
				"10:23: The String \", \" appears 3 times in the file.",
		};

		verify(checkConfig, getPath("InputMultipleStringLiterals.java"), expected);
	}

	@Test
	public void testItIgnoreEmptyAndComaSpace() throws Exception
	{
		DefaultConfiguration checkConfig =
				createCheckConfig(MultipleStringLiteralsExtendedCheck.class);
		checkConfig.addAttribute("allowedDuplicates", "2");
		checkConfig.addAttribute("ignoreStringsRegexp", "^((\"\")|(\", \"))$");
		checkConfig.addAttribute("highlightAllDuplicates", "false");

		final String[] expected = {
				"5:16: The String \"StringContents\" appears 3 times in the file.",
		};

		verify(checkConfig, getPath("InputMultipleStringLiterals.java"), expected);
	}

	@Test
	public void testItWithoutIgnoringAnnotations() throws Exception
	{
		DefaultConfiguration checkConfig =
				createCheckConfig(MultipleStringLiteralsExtendedCheck.class);
		checkConfig.addAttribute("allowedDuplicates", "3");
		checkConfig.addAttribute("ignoreOccurrenceContext", "");
		checkConfig.addAttribute("highlightAllDuplicates", "false");

		final String[] expected = {
				"19:23: The String \"unchecked\" appears 4 times in the file.",
		};

		verify(checkConfig, getPath("InputMultipleStringLiterals.java"), expected);
	}

}
