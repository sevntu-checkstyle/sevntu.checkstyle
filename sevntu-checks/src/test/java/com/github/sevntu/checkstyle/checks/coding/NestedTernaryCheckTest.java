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

import java.io.IOException;
import java.util.Properties;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

/**
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public class NestedTernaryCheckTest extends BaseCheckTestSupport {

	private final DefaultConfiguration checkConfig = createCheckConfig(NestedTernaryCheck.class);

	private final String msg = getCheckMessage(NestedTernaryCheck.MSG_KEY);

	@Test
	public void test() throws Exception
	{
		String[] expected = {
				"18:43: " + msg,
				"19:41: " + msg,
				"23:81: " + msg,
				"24:82: " + msg,
				"28:72: " + msg,
				"29:101: " + msg,
				"34:49: " + msg,
				"35:55: " + msg,
				"44:61: " + msg, // C-tor final
				"45:96: " + msg, // C-tor final
		};

		verify(checkConfig, getPath("InputNestedTernaryCheck.java"), expected);
	}

	@Test
	public void testIgnoreCtor() throws Exception
	{
		checkConfig.addAttribute("ignoreFinal", "true");

		String[] expected = {
				"19:41: " + msg,
				"24:82: " + msg,
				"29:101: " + msg,
				"34:49: " + msg,
				"35:55: " + msg,
				"44:61: " + msg, // C-tor final - always warn, no matter what the value of "ignoreFinal"
				"45:96: " + msg, // C-tor final - always warn, no matter what the value of "ignoreFinal"
		};

		verify(checkConfig, getPath("InputNestedTernaryCheck.java"), expected);
	}

}
