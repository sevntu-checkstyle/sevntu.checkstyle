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

package com.github.sevntu.checkstyle.checks.coding;

import static com.github.sevntu.checkstyle.checks.coding.AvoidNotShortCircuitOperatorsForBooleanCheck.MSG_KEY;

import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.utils.CommonUtil;

public class AvoidNotShortCircuitOperatorsForBooleanCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/coding";
    }

    @Test
    public final void testAll() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(AvoidNotShortCircuitOperatorsForBooleanCheck.class);

        final String[] expected = {
            "6:23: " + getCheckMessage(MSG_KEY, "|"),
            "25:20: " + getCheckMessage(MSG_KEY, "|"),
            "35:30: " + getCheckMessage(MSG_KEY, "|"),
            "43:21: " + getCheckMessage(MSG_KEY, "|"),
            "48:29: " + getCheckMessage(MSG_KEY, "|"),
            "53:17: " + getCheckMessage(MSG_KEY, "&"),
            "64:17: " + getCheckMessage(MSG_KEY, "|"),
            "71:9: " + getCheckMessage(MSG_KEY, "|"),
            "79:9: " + getCheckMessage(MSG_KEY, "|"),
            "88:17: " + getCheckMessage(MSG_KEY, "|"),
            "94:22: " + getCheckMessage(MSG_KEY, "|"),
            "95:14: " + getCheckMessage(MSG_KEY, "|"),
            "96:14: " + getCheckMessage(MSG_KEY, "|"),
            "97:11: " + getCheckMessage(MSG_KEY, "|="),
        };

        verify(checkConfig, getPath("InputAvoidNotShortCircuitOperatorsForBooleanCheck.java"),
                expected);
    }

    @Test
    public final void testNonClasses() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(AvoidNotShortCircuitOperatorsForBooleanCheck.class);
        verify(checkConfig,
                getPath("InputAvoidNotShortCircuitOperatorsForBooleanCheckNonClasses.java"),
                CommonUtil.EMPTY_STRING_ARRAY);
    }

    @Test
    public final void testExpressionBeforeTry() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(AvoidNotShortCircuitOperatorsForBooleanCheck.class);
        verify(checkConfig,
            getPath("InputAvoidNotShortCircuitOperatorsForBooleanCheckLambdaWithMultiCatch.java"),
            CommonUtil.EMPTY_STRING_ARRAY);
    }

    @Test
    public final void testBitwiseOrAfterArrayIndex() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(AvoidNotShortCircuitOperatorsForBooleanCheck.class);
        final String[] expected = {
            "6:19: " + getCheckMessage(MSG_KEY, "|="),
            "8:20: " + getCheckMessage(MSG_KEY, "|="),
        };
        verify(checkConfig,
                getPath("InputAvoidNotShortCircuitOperatorsForBooleanCheckBitwiseAfterArray.java"),
                expected);
    }
}
