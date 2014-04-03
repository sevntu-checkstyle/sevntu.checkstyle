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

import static com.github.sevntu.checkstyle.checks.coding.NestedTernaryCheck.*;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

/**
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public class NestedTernaryCheckTest extends BaseCheckTestSupport {

	private final DefaultConfiguration checkConfig = createCheckConfig(NestedTernaryCheck.class);

	private final String warningMessage = getCheckMessage(MSG_KEY);

	@Test
	public void test() throws Exception
	{
		String[] expected = {
				"18:43: " + warningMessage,
				"19:41: " + warningMessage,
				"23:81: " + warningMessage,
				"24:82: " + warningMessage,
				"28:72: " + warningMessage,
				"29:101: " + warningMessage,
				"34:49: " + warningMessage,
				"35:55: " + warningMessage,
				"44:61: " + warningMessage, // C-tor final
				"45:96: " + warningMessage, // C-tor final
		};

		verify(checkConfig, getPath("InputNestedTernaryCheck.java"), expected);
	}

	@Test
	public void testIgnoreCtor() throws Exception
	{
		checkConfig.addAttribute("ignoreFinal", "true");

		String[] expected = {
				"19:41: " + warningMessage,
				"24:82: " + warningMessage,
				"29:101: " + warningMessage,
				"34:49: " + warningMessage,
				"35:55: " + warningMessage,
				"44:61: " + warningMessage, // C-tor final - always warn, no matter what the value of "ignoreFinal"
				"45:96: " + warningMessage, // C-tor final - always warn, no matter what the value of "ignoreFinal"
		};

		verify(checkConfig, getPath("InputNestedTernaryCheck.java"), expected);
	}

}
