///////////////////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code and other text files for adherence to a set of rules.
// Copyright (C) 2001-2026 the original author or authors.
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
///////////////////////////////////////////////////////////////////////////////////////////////

package com.github.sevntu.checkstyle.checks.design;

import static com.github.sevntu.checkstyle.checks.design.CauseParameterInExceptionCheck.MSG_KEY;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.DetailAstImpl;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class CauseParameterInExceptionCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/design";
    }

    @Test
    public void testNormalWork() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(CauseParameterInExceptionCheck.class);
        checkConfig.addProperty("classNamesRegexp", ".+Exception");
        checkConfig.addProperty("ignoredClassNamesRegexp", null);
        checkConfig.addProperty("allowedCauseTypes", "Throwable, Exception");

        final String[] expected = {
            "5:5: " + getCheckMessage(MSG_KEY, "TestException"),
        };

        verify(checkConfig, getPath("InputCauseParameterInExceptionCheck.java"), expected);
    }

    @Test
    public void testNormalWork2() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(CauseParameterInExceptionCheck.class);
        checkConfig.addProperty("classNamesRegexp", ".+Exception2");
        checkConfig.addProperty("ignoredClassNamesRegexp", null);
        checkConfig.addProperty("allowedCauseTypes", "Throwable, Exception");

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
        checkConfig.addProperty("classNamesRegexp", ".+Exception2");
        checkConfig.addProperty("ignoredClassNamesRegexp", "Test.+");
        checkConfig.addProperty("allowedCauseTypes", "Throwable, Exception");

        final String[] expected = {
            "16:5: " + getCheckMessage(MSG_KEY, "MyException2"),
        };

        verify(checkConfig, getPath("InputCauseParameterInExceptionCheck2.java"), expected);
    }

    @Test
    public void testIgnorePattern2() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(CauseParameterInExceptionCheck.class);
        checkConfig.addProperty("classNamesRegexp", ".+Exception2");
        checkConfig.addProperty("ignoredClassNamesRegexp", "My.+");
        checkConfig.addProperty("allowedCauseTypes", "Throwable, Exception");

        final String[] expected = {
            "5:5: " + getCheckMessage(MSG_KEY, "TestException2"),
        };

        verify(checkConfig, getPath("InputCauseParameterInExceptionCheck2.java"), expected);
    }

    @Test
    public void testStrangeSituation() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(CauseParameterInExceptionCheck.class);
        checkConfig.addProperty("classNamesRegexp", ".+Exception");
        checkConfig.addProperty("ignoredClassNamesRegexp", "");
        checkConfig.addProperty("allowedCauseTypes", "Throwable, Exception");

        final String[] expected = {
        };

        verify(checkConfig, getPath("InputCauseParameterInExceptionCheck3.java"), expected);
    }

    @Test
    public void testStrangeSituation2() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(CauseParameterInExceptionCheck.class);
        checkConfig.addProperty("classNamesRegexp", null);
        checkConfig.addProperty("ignoredClassNamesRegexp", null);
        checkConfig.addProperty("allowedCauseTypes", "Throwable, Exception");

        final String[] expected = {
        };

        verify(checkConfig, getPath("InputCauseParameterInExceptionCheck3.java"), expected);
    }

    @Test
    public void testStrangeSituation3() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(CauseParameterInExceptionCheck.class);
        checkConfig.addProperty("classNamesRegexp", null);
        checkConfig.addProperty("ignoredClassNamesRegexp", null);
        checkConfig.addProperty("allowedCauseTypes", "Throwable, Exception");

        final String[] expected = {
        };

        verify(checkConfig, getPath("InputCauseParameterInExceptionCheck4.java"), expected);
    }

    @Test
    public void testStrangeSituation4() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(CauseParameterInExceptionCheck.class);
        checkConfig.addProperty("classNamesRegexp", null);
        checkConfig.addProperty("ignoredClassNamesRegexp", null);
        checkConfig.addProperty("allowedCauseTypes", "Throwable, Exception");

        final String[] expected = {
        };

        verify(checkConfig, getPath("InputCauseParameterInExceptionCheck5.java"), expected);
    }

    @Test
    public void testUnsupportedNode() {
        final DetailAstImpl sync = new DetailAstImpl();
        sync.setType(TokenTypes.LITERAL_SYNCHRONIZED);

        try {
            final CauseParameterInExceptionCheck check = new CauseParameterInExceptionCheck();
            check.visitToken(sync);

            fail("exception expected");
        }
        catch (IllegalArgumentException exc) {
            Assertions.assertEquals("Found unsupported token: LITERAL_SYNCHRONIZED",
                    exc.getMessage());
        }
    }

}
