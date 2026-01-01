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

import static com.github.sevntu.checkstyle.checks.design.ConstructorWithoutParamsCheck.MSG_KEY;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class ConstructorWithoutParamsCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/design";
    }

    @Test
    public void testDefaultConfigProhibitsExceptionsWithoutParams() throws Exception {
        final DefaultConfiguration defaultConfig =
                createModuleConfig(ConstructorWithoutParamsCheck.class);
        final String[] expectedViolationMsg = {
            "36:37: " + getCheckMessage(MSG_KEY, "RuntimeException"),
        };
        verify(defaultConfig, getPath("InputConstructorWithoutParamsCheck.java"),
                expectedViolationMsg);
    }

    @Test
    public void testUserDefinedConfigProhibitsCustomClasses() throws Exception {
        final DefaultConfiguration defaultConfig =
                createModuleConfig(ConstructorWithoutParamsCheck.class);
        defaultConfig.addProperty("classNameFormat", "Clazz[1-9]");
        defaultConfig.addProperty("ignoredClassNameFormat", "Clazz4");
        final String[] expectedViolationMsg = {
            "71:27: " + getCheckMessage(MSG_KEY, "Clazz1"),
            "74:27: " + getCheckMessage(MSG_KEY, "Clazz2"),
        };
        verify(defaultConfig, getPath("InputConstructorWithoutParamsCheck.java"),
                expectedViolationMsg);
    }

    /*
     Added to comply with the sevntu.checkstyle regulation of 100% code coverage.
     */
    @Test
    public void testGetAcceptableTokens() {
        final ConstructorWithoutParamsCheck check = new ConstructorWithoutParamsCheck();
        final int[] expectedAcceptableTokens = {TokenTypes.LITERAL_NEW};
        assertArrayEquals(expectedAcceptableTokens, check.getAcceptableTokens(), "invalid tokens");
    }

}
