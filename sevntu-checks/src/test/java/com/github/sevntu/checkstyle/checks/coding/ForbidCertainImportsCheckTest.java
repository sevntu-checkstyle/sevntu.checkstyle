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

import static com.github.sevntu.checkstyle.checks.coding.ForbidCertainImportsCheck.*;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;


/**
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil
 *         Yaroslavtsev</a>
 */
public class ForbidCertainImportsCheckTest extends BaseCheckTestSupport
{
	private final DefaultConfiguration checkConfig = createCheckConfig(ForbidCertainImportsCheck.class);

    @Test
    public void testNormalWork() throws Exception
    {
        String importRegexp = ".+\\.api\\..+";

        checkConfig.addAttribute("packageNameRegexp", ".+\\.sevntu\\..+");
        checkConfig.addAttribute("forbiddenImportsRegexp", importRegexp);
        checkConfig.addAttribute("forbiddenImportsExcludesRegexp", "");

        String[] expected = {
        	"3: " + getCheckMessage(MSG_KEY, importRegexp, "com.puppycrawl.tools.checkstyle.api.AutomaticBean"),
        	"9: " + getCheckMessage(MSG_KEY, importRegexp, "com.puppycrawl.tools.checkstyle.api.AutomaticBean"),
        	"21: " + getCheckMessage(MSG_KEY, importRegexp, "com.puppycrawl.tools.checkstyle.api.AutomaticBean"),
        };

        verify(checkConfig, getPath("InputForbidsCertainImports.java"), expected);
    }

	@Test
	public void testNormalWorkWithExcludes() throws Exception
	{
		String importRegexp = ".+\\.api\\..+";

		checkConfig.addAttribute("packageNameRegexp", ".+\\.sevntu\\..+");
		checkConfig.addAttribute("forbiddenImportsRegexp", importRegexp);
		checkConfig.addAttribute("forbiddenImportsExcludesRegexp", "com.puppycrawl.+");

		String[] expected = {};

		verify(checkConfig, getPath("InputForbidsCertainImports.java"), expected);
	}
    
    @Test
    public void testEmptyImportsAndDefaultPackage() throws Exception
    {
        checkConfig.addAttribute("packageNameRegexp", ".+\\.old\\..+");
        checkConfig.addAttribute("forbiddenImportsRegexp", ".+\\.api\\..+");
        checkConfig.addAttribute("forbiddenImportsExcludesRegexp", "");

        String[] expected = {};

        verify(checkConfig, getPath("InputForbidCertainImportsDefaultPackageWithoutImports.java"), expected);
    }

    @Test
    public void testEmptyParams() throws Exception
    {
        checkConfig.addAttribute("packageNameRegexp", "");
        checkConfig.addAttribute("forbiddenImportsRegexp", "");
        checkConfig.addAttribute("forbiddenImportsExcludesRegexp", "");

        String[] expected = {};

        verify(checkConfig, getPath("InputForbidCertainImportsDefaultPackageWithoutImports.java"), expected);
    }

    @Test
    public void testNoImports() throws Exception
    {
        checkConfig.addAttribute("packageNameRegexp", "");
        checkConfig.addAttribute("forbiddenImportsRegexp", "");
        checkConfig.addAttribute("forbiddenImportsExcludesRegexp", "");

        String[] expected = {};

        verify(checkConfig, getPath("InputForbidCertainImportsDefaultPackageWithoutImports.java"), expected);
    }
}
