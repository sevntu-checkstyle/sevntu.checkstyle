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

package com.github.sevntu.checkstyle.checks.design;

import static com.github.sevntu.checkstyle.checks.design.CauseParameterInExceptionCheck.MSG_KEY;
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
public class CauseParameterInExceptionCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/design";
    }

    @Test
    public void testNormalWork() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(CauseParameterInExceptionCheck.class);
        checkConfig.addAttribute("classNamesRegexp", ".+Exception");
        checkConfig.addAttribute("ignoredClassNamesRegexp", null);
        checkConfig.addAttribute("allowedCauseTypes", "Throwable, Exception");

        final String[] expected = {
            "5:5: " + getCheckMessage(MSG_KEY, "TestException"),
        };

        verify(checkConfig, getPath("InputCauseParameterInExceptionCheck.java"), expected);
    }

    @Test
    public void testNormalWork2() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(CauseParameterInExceptionCheck.class);
        checkConfig.addAttribute("classNamesRegexp", ".+Exception2");
        checkConfig.addAttribute("ignoredClassNamesRegexp", null);
        checkConfig.addAttribute("allowedCauseTypes", "Throwable, Exception");

        final String[] expected = {
            "5:5: " + getCheckMessage(MSG_KEY, "TestException2"),
            "16:5: " + getCheckMessage(MSG_KEY, "MyException2"),
        };

        verify(checkConfig, getPath("InputCauseParameterInExceptionCheck2.java"), expected);
    }

    @Test
    public void testIgnorePattern() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(CauseParameterInExceptionCheck.class);
        checkConfig.addAttribute("classNamesRegexp", ".+Exception2");
        checkConfig.addAttribute("ignoredClassNamesRegexp", "Test.+");
        checkConfig.addAttribute("allowedCauseTypes", "Throwable, Exception");

        final String[] expected = {
            "16:5: " + getCheckMessage(MSG_KEY, "MyException2"),
        };

        verify(checkConfig, getPath("InputCauseParameterInExceptionCheck2.java"), expected);
    }

    @Test
    public void testIgnorePattern2() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(CauseParameterInExceptionCheck.class);
        checkConfig.addAttribute("classNamesRegexp", ".+Exception2");
        checkConfig.addAttribute("ignoredClassNamesRegexp", "My.+");
        checkConfig.addAttribute("allowedCauseTypes", "Throwable, Exception");

        final String[] expected = {
            "5:5: " + getCheckMessage(MSG_KEY, "TestException2"),
        };

        verify(checkConfig, getPath("InputCauseParameterInExceptionCheck2.java"), expected);
    }

    @Test
    public void testStrangeSituation() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(CauseParameterInExceptionCheck.class);
        checkConfig.addAttribute("classNamesRegexp", ".+Exception");
        checkConfig.addAttribute("ignoredClassNamesRegexp", "");
        checkConfig.addAttribute("allowedCauseTypes", "Throwable, Exception");

        final String[] expected = {
        };

        verify(checkConfig, getPath("InputCauseParameterInExceptionCheck3.java"), expected);
    }

    @Test
    public void testStrangeSituation2() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(CauseParameterInExceptionCheck.class);
        checkConfig.addAttribute("classNamesRegexp", null);
        checkConfig.addAttribute("ignoredClassNamesRegexp", null);
        checkConfig.addAttribute("allowedCauseTypes", "Throwable, Exception");

        final String[] expected = {
        };

        verify(checkConfig, getPath("InputCauseParameterInExceptionCheck3.java"), expected);
    }

    @Test
    public void testStrangeSituation3() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(CauseParameterInExceptionCheck.class);
        checkConfig.addAttribute("classNamesRegexp", null);
        checkConfig.addAttribute("ignoredClassNamesRegexp", null);
        checkConfig.addAttribute("allowedCauseTypes", "Throwable, Exception");

        final String[] expected = {
        };

        verify(checkConfig, getPath("InputCauseParameterInExceptionCheck4.java"), expected);
    }

    @Test
    public void testStrangeSituation4() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(CauseParameterInExceptionCheck.class);
        checkConfig.addAttribute("classNamesRegexp", null);
        checkConfig.addAttribute("ignoredClassNamesRegexp", null);
        checkConfig.addAttribute("allowedCauseTypes", "Throwable, Exception");

        final String[] expected = {
        };

        verify(checkConfig, getPath("InputCauseParameterInExceptionCheck5.java"), expected);
    }

    @Test
    public void testUnsupportedNode() {
        final DetailAST sync = new DetailAST();
        sync.setType(TokenTypes.LITERAL_SYNCHRONIZED);

        try {
            final CauseParameterInExceptionCheck check = new CauseParameterInExceptionCheck();
            check.visitToken(sync);

            fail();
        }
        catch (IllegalArgumentException ex) {
            Assert.assertEquals("Found unsupported token: LITERAL_SYNCHRONIZED", ex.getMessage());
        }
    }

}
