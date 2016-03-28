////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2016 the original author or authors.
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

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import static com.github.sevntu.checkstyle.checks.design.ConstructorWithoutParamsCheck.MSG_KEY;

import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

import org.junit.Before;
import org.junit.Test;

public class ConstructorWithoutParamsCheckTest extends BaseCheckTestSupport {

    private DefaultConfiguration defaultConfig;

    @Before
    public void getDefaultConfiguration() throws Exception {
        defaultConfig = createCheckConfig(ConstructorWithoutParamsCheck.class);
    }

    @Test
    public void testDefaultConfigProhibitsExceptionsWithoutParams() throws Exception {
        String[] expectedViolationMsg = {"30:37: " + getCheckMessage(MSG_KEY, "RuntimeException")};
        verify(defaultConfig , getPath("InputConstructorWithoutParamsCheck.java"), expectedViolationMsg);
    }

    @Test
    public void testUserDefinedConfigProhibitsCustomClasses() throws Exception {
        defaultConfig.addAttribute("classNameFormat", "Clazz[1-9]");
        defaultConfig.addAttribute("ignoredClassNameFormat", "Clazz4");
        String[] expectedViolationMsg = {"64:27: " + getCheckMessage(MSG_KEY, "Clazz1"),
                                         "67:27: " + getCheckMessage(MSG_KEY, "Clazz2")};
        verify(defaultConfig , getPath("InputConstructorWithoutParamsCheck.java"), expectedViolationMsg);
    }

    /*
     Added to comply with the sevntu.checkstyle regulation of 100% code coverage.
     */
    @Test
    public void testGetAcceptableTokens(){
        ConstructorWithoutParamsCheck check =  new ConstructorWithoutParamsCheck();
        final int[] expectedAcceptableTokens = {TokenTypes.LITERAL_NEW};
        assertArrayEquals(expectedAcceptableTokens, check.getAcceptableTokens());
    }

}
