////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2017 the original author or authors.
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

import static com.github.sevntu.checkstyle.checks.coding.ForbidThrowAnonymousExceptionsCheck.MSG_KEY;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class ForbidThrowAnonymousExceptionsCheckTest extends BaseCheckTestSupport {

    @Test
    public final void anonymousExceptionTest() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidThrowAnonymousExceptionsCheck.class);
        final String[] expected = {
            "8: " + getCheckMessage(MSG_KEY),
        };

        verify(checkConfig,
                getPath("InputForbidThrowAnonymousExceptionsCheck.java"), expected);
    }

    @Test
    public final void preDefinedAnonymousExceptionTest() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidThrowAnonymousExceptionsCheck.class);
        final String[] expected = {
            "8: " + getCheckMessage(MSG_KEY),
            "30: " + getCheckMessage(MSG_KEY),
        };

        verify(checkConfig,
                getPath("InputForbidThrowAnonymousExceptionsCheck2.java"), expected);
    }

    @Test
    public final void sameNameExceptionsTest() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidThrowAnonymousExceptionsCheck.class);
        final String[] expected = {
            "12: " + getCheckMessage(MSG_KEY),
            "23: " + getCheckMessage(MSG_KEY),
            "66: " + getCheckMessage(MSG_KEY),
            "88: " + getCheckMessage(MSG_KEY),
            "107: " + getCheckMessage(MSG_KEY),
            "127: " + getCheckMessage(MSG_KEY),
        };

        verify(checkConfig,
                getPath("InputForbidThrowAnonymousExceptionsCheck3.java"), expected);
    }

    @Test
    public final void nonStandardExceptionClassNameTest() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidThrowAnonymousExceptionsCheck.class);
        checkConfig.addAttribute("exceptionClassNameRegex", "^.*bla");
        final String[] expected = {
            "10: " + getCheckMessage(MSG_KEY),
            "12: " + getCheckMessage(MSG_KEY),
        };

        verify(checkConfig,
                getPath("InputForbidThrowAnonymousExceptionsCheckAnotherClassName.java"), expected);
    }

}
