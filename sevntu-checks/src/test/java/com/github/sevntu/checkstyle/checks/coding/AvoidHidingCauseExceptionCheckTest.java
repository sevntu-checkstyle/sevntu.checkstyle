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

import static com.github.sevntu.checkstyle.checks.coding.AvoidHidingCauseExceptionCheck.MSG_KEY;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.utils.CommonUtils;

public class AvoidHidingCauseExceptionCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/coding";
    }

    @Test
    public final void issue52Test() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(AvoidHidingCauseExceptionCheck.class);

        final String[] expected = {};

        verify(checkConfig, getPath("InputAvoidHidingCauseExceptionCheck2.java"), expected);
    }

    @Test
    public final void test() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(AvoidHidingCauseExceptionCheck.class);

        final String[] expected = {
            "18:13: " + getCheckMessage(MSG_KEY, "e"),
            "23:13: " + getCheckMessage(MSG_KEY, "e"),
            "38:13: " + getCheckMessage(MSG_KEY, "e"),
            "55:21: " + getCheckMessage(MSG_KEY, "e"),
            "65:17: " + getCheckMessage(MSG_KEY, "e"),
            "85:17: " + getCheckMessage(MSG_KEY, "e"),
            "103:13: " + getCheckMessage(MSG_KEY, "e"),
            "110:17: " + getCheckMessage(MSG_KEY, "n"),
            "123:21: " + getCheckMessage(MSG_KEY, "x"),
            "129:13: " + getCheckMessage(MSG_KEY, "e"),
            "142:13: " + getCheckMessage(MSG_KEY, "e"),
            "152:13: " + getCheckMessage(MSG_KEY, "e"),
            "197:13: " + getCheckMessage(MSG_KEY, "fakeException"),
            "216:13: " + getCheckMessage(MSG_KEY, "e"),
            "221:13: " + getCheckMessage(MSG_KEY, "e"),
            "226:13: " + getCheckMessage(MSG_KEY, "e"),
        };

        verify(checkConfig, getPath("InputAvoidHidingCauseExceptionCheck.java"), expected);
    }

    @Test
    public final void testWrappingException() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(AvoidHidingCauseExceptionCheck.class);

        verify(checkConfig, getPath("InputAvoidHidingCauseExceptionCheckWrapping.java"),
                CommonUtils.EMPTY_STRING_ARRAY);
    }

}

