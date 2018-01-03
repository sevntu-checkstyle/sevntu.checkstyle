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

import static com.github.sevntu.checkstyle.checks.coding.ForbidCertainImportsCheck.MSG_KEY;
import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil
 *         Yaroslavtsev</a>
 */
public class ForbidCertainImportsCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/coding";
    }

    @Test
    public void testNormalWork() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidCertainImportsCheck.class);
        checkConfig.addAttribute("packageNameRegexp", ".+\\.sevntu\\..+");
        checkConfig.addAttribute("forbiddenImportsRegexp", ".+\\.api\\..+");
        checkConfig.addAttribute("forbiddenImportsExcludesRegexp", "");

        final String[] expected = {
            "3: " + getCheckMessage(MSG_KEY, ".+\\.api\\..+",
                    "com.puppycrawl.tools.checkstyle.api.AutomaticBean"),
            "9: " + getCheckMessage(MSG_KEY, ".+\\.api\\..+",
                    "com.puppycrawl.tools.checkstyle.api.AutomaticBean"),
            "21: " + getCheckMessage(MSG_KEY, ".+\\.api\\..+",
                    "com.puppycrawl.tools.checkstyle.api.AutomaticBean"),
        };

        verify(checkConfig, getPath("InputForbidCertainImportsCheck.java"), expected);
    }

    @Test
    public void testNoPackageMatch() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidCertainImportsCheck.class);
        checkConfig.addAttribute("packageNameRegexp", ".+\\.BAD\\..+");
        checkConfig.addAttribute("forbiddenImportsRegexp", ".+\\.api\\..+");
        checkConfig.addAttribute("forbiddenImportsExcludesRegexp", "");

        final String[] expected = {};

        verify(checkConfig, getPath("InputForbidCertainImportsCheck.java"), expected);
    }

    @Test
    public void testNormalWorkWithExcludes() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidCertainImportsCheck.class);
        checkConfig.addAttribute("packageNameRegexp", ".+\\.sevntu\\..+");
        checkConfig.addAttribute("forbiddenImportsRegexp", ".+\\.api\\..+");
        checkConfig.addAttribute("forbiddenImportsExcludesRegexp", "com.puppycrawl.+");

        final String[] expected = {};

        verify(checkConfig, getPath("InputForbidCertainImportsCheck.java"), expected);
    }

    @Test
    public void testEmptyImportsAndDefaultPackage() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidCertainImportsCheck.class);
        checkConfig.addAttribute("packageNameRegexp", ".+\\.old\\..+");
        checkConfig.addAttribute("forbiddenImportsRegexp", ".+\\.api\\..+");
        checkConfig.addAttribute("forbiddenImportsExcludesRegexp", "");

        final String[] expected = {};

        verify(checkConfig,
                getPath("InputForbidCertainImportsCheckDefaultPackageWithoutImports.java"),
                expected);
    }

    @Test
    public void testEmptyParams() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidCertainImportsCheck.class);
        checkConfig.addAttribute("packageNameRegexp", "");
        checkConfig.addAttribute("forbiddenImportsRegexp", "");
        checkConfig.addAttribute("forbiddenImportsExcludesRegexp", "");

        final String[] expected = {};

        verify(checkConfig,
                getPath("InputForbidCertainImportsCheckDefaultPackageWithoutImports.java"),
                expected);
    }

    @Test
    public void testNullParams() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidCertainImportsCheck.class);
        checkConfig.addAttribute("packageNameRegexp", null);
        checkConfig.addAttribute("forbiddenImportsRegexp", null);
        checkConfig.addAttribute("forbiddenImportsExcludesRegexp", null);

        final String[] expected = {};

        verify(checkConfig,
                getPath("InputForbidCertainImportsCheckDefaultPackageWithoutImports.java"),
                expected);
    }

    @Test
    public void testPackageMatchButNullParams() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidCertainImportsCheck.class);
        checkConfig.addAttribute("packageNameRegexp", ".+\\.sevntu\\..+");
        checkConfig.addAttribute("forbiddenImportsRegexp", null);
        checkConfig.addAttribute("forbiddenImportsExcludesRegexp", null);

        final String[] expected = {};

        verify(checkConfig, getPath("InputForbidCertainImportsCheck.java"), expected);
    }

    @Test
    public void testPackageForbiddenImportMatchButNullExclude() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidCertainImportsCheck.class);
        checkConfig.addAttribute("packageNameRegexp", ".+\\.sevntu\\..+");
        checkConfig.addAttribute("forbiddenImportsRegexp", ".+\\.api\\..+");
        checkConfig.addAttribute("forbiddenImportsExcludesRegexp", null);

        final String[] expected = {
            "3: " + getCheckMessage(MSG_KEY, ".+\\.api\\..+",
                    "com.puppycrawl.tools.checkstyle.api.AutomaticBean"),
            "9: " + getCheckMessage(MSG_KEY, ".+\\.api\\..+",
                    "com.puppycrawl.tools.checkstyle.api.AutomaticBean"),
            "21: " + getCheckMessage(MSG_KEY, ".+\\.api\\..+",
                    "com.puppycrawl.tools.checkstyle.api.AutomaticBean"),
        };

        verify(checkConfig, getPath("InputForbidCertainImportsCheck.java"), expected);
    }

    @Test
    public void testNoImports() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidCertainImportsCheck.class);
        checkConfig.addAttribute("packageNameRegexp", "");
        checkConfig.addAttribute("forbiddenImportsRegexp", "");
        checkConfig.addAttribute("forbiddenImportsExcludesRegexp", "");

        final String[] expected = {};

        verify(checkConfig,
                getPath("InputForbidCertainImportsCheckDefaultPackageWithoutImports.java"),
                expected);
    }

    @Test
    public void testSinglePackage() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidCertainImportsCheck.class);
        checkConfig.addAttribute("packageNameRegexp", "sevntu");
        checkConfig.addAttribute("forbiddenImportsRegexp", ".+\\.api\\..+");
        checkConfig.addAttribute("forbiddenImportsExcludesRegexp", "");

        final String[] expected = {
            "3: " + getCheckMessage(MSG_KEY, ".+\\.api\\..+",
                    "com.puppycrawl.tools.checkstyle.api.AutomaticBean"),
            "10: " + getCheckMessage(MSG_KEY, ".+\\.api\\..+",
                    "com.puppycrawl.tools.checkstyle.api.AutomaticBean"),
        };

        verify(checkConfig, getPath("InputForbidCertainImportsCheckSinglePackage.java"), expected);
    }

    @Test
    public void testUnsupportedNode() {
        final DetailAST sync = new DetailAST();
        sync.setType(TokenTypes.LITERAL_SYNCHRONIZED);

        try {
            final ForbidCertainImportsCheck check = new ForbidCertainImportsCheck();
            check.visitToken(sync);

            fail();
        }
        catch (IllegalArgumentException ex) {
            Assert.assertEquals("Found unsupported token: LITERAL_SYNCHRONIZED", ex.getMessage());
        }
    }

}
