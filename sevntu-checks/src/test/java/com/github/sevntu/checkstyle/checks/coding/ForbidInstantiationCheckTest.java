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

import static com.github.sevntu.checkstyle.checks.coding.ForbidInstantiationCheck.MSG_KEY;
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
public class ForbidInstantiationCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/coding";
    }

    @Test
    public void testNullProperties() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidInstantiationCheck.class);
        checkConfig.addAttribute("forbiddenClasses", null);

        final String[] expected = {};

        verify(checkConfig, getPath("InputForbidInstantiationCheck.java"), expected);
    }

    @Test
    public void testNullPointerException() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidInstantiationCheck.class);
        checkConfig.addAttribute("forbiddenClasses", "java.lang.NullPointerException");

        final String[] expected = {
            "9:35: " + getCheckMessage(MSG_KEY, "NullPointerException"),
            "12:36: " + getCheckMessage(MSG_KEY, "NullPointerException"),
        };

        verify(checkConfig, getPath("InputForbidInstantiationCheck.java"), expected);
    }

    @Test
    public void testNormalWork() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidInstantiationCheck.class);
        checkConfig.addAttribute("forbiddenClasses", "java.io.File , java.lang.String , ");

        final String[] expected = {
            "13:21: " + getCheckMessage(MSG_KEY, "File"),
            "14:22: " + getCheckMessage(MSG_KEY, "File"),
            "15:20: " + getCheckMessage(MSG_KEY, "String"),
        };

        verify(checkConfig, getPath("InputForbidInstantiationCheck.java"), expected);
    }

    @Test
    public void testNormalWork2() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidInstantiationCheck.class);
        checkConfig.addAttribute("forbiddenClasses",
                "com.github.sevntu.checkstyle.checks.coding.InputForbidInstantiationCheck");

        final String[] expected = {
            "12:89: " + getCheckMessage(MSG_KEY, "InputForbidInstantiationCheck"),
        };

        verify(checkConfig, getPath("InputForbidInstantiationCheckWithoutDots.java"), expected);
    }

    @Test
    public void testNormalWork3() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidInstantiationCheck.class);
        checkConfig.addAttribute("forbiddenClasses", "java.io.File , java.lang.String , ");

        final String[] expected = {
            "13:22: " + getCheckMessage(MSG_KEY, "File"),
            "14:20: " + getCheckMessage(MSG_KEY, "String"),
        };

        verify(checkConfig, getPath("InputForbidInstantiationCheckWithoutDots.java"), expected);
    }

    @Test
    public void testAsteriskInInput() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidInstantiationCheck.class);
        checkConfig.addAttribute("forbiddenClasses", "java.io.File , java.lang.String , ");

        final String[] expected = {
            "13:21: " + getCheckMessage(MSG_KEY, "File"),
            "14:22: " + getCheckMessage(MSG_KEY, "File"),
            "15:20: " + getCheckMessage(MSG_KEY, "String"),
        };

        verify(checkConfig, getPath("InputForbidInstantiationCheckWithAsterisk.java"), expected);
    }

    @Test
    public void testUnsupportedNode() {
        final DetailAST sync = new DetailAST();
        sync.setType(TokenTypes.LITERAL_SYNCHRONIZED);

        try {
            final ForbidInstantiationCheck check = new ForbidInstantiationCheck();
            check.visitToken(sync);

            fail();
        }
        catch (IllegalArgumentException ex) {
            Assert.assertEquals("Found unsupported token: LITERAL_SYNCHRONIZED", ex.getMessage());
        }
    }

}
