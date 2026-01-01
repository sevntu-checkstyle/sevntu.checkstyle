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

import static com.github.sevntu.checkstyle.checks.coding.ConfusingConditionCheck.MSG_KEY;

import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class ConfusingConditionCheckTest extends AbstractModuleTestSupport {

    private final String warningMessage = getCheckMessage(MSG_KEY);

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/coding";
    }

    @Test
    public void testDefault() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(ConfusingConditionCheck.class);

        final String[] expected = {
            "10:9: " + warningMessage,
            "13:9: " + warningMessage,
            "16:9: " + warningMessage,
            "19:9: " + warningMessage,
            "22:9: " + warningMessage,
            "105:9: " + warningMessage,
            "108:9: " + warningMessage,
            "111:9: " + warningMessage,
            "149:9: " + warningMessage,
            "166:9: " + warningMessage,
            "177:14: " + warningMessage,
            "181:9: " + warningMessage,
            "200:13: " + warningMessage,
            "215:9: " + warningMessage,
            "231:13: " + warningMessage,
            "250:14: " + warningMessage,
            "259:14: " + warningMessage,
        };

        verify(checkConfig, getPath("InputConfusingConditionCheck.java"),
                expected);
    }

    @Test
    public void testFalseProperties() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(ConfusingConditionCheck.class);

        checkConfig.addProperty("ignoreInnerIf", "false");
        checkConfig.addProperty("ignoreNullCaseInIf", "false");
        checkConfig.addProperty("ignoreSequentialIf", "false");
        checkConfig.addProperty("ignoreThrowInElse", "false");

        final String[] expected = {
            "10:9: " + warningMessage,
            "13:9: " + warningMessage,
            "16:9: " + warningMessage,
            "19:9: " + warningMessage,
            "22:9: " + warningMessage,
            "105:9: " + warningMessage,
            "108:9: " + warningMessage,
            "111:9: " + warningMessage,
            "127:21: " + warningMessage,
            "134:9: " + warningMessage,
            "138:9: " + warningMessage,
            "145:9: " + warningMessage,
            "149:9: " + warningMessage,
            "157:9: " + warningMessage,
            "160:9: " + warningMessage,
            "162:9: " + warningMessage,
            "166:9: " + warningMessage,
            "177:14: " + warningMessage,
            "181:9: " + warningMessage,
            "199:9: " + warningMessage,
            "200:13: " + warningMessage,
            "215:9: " + warningMessage,
            "227:9: " + warningMessage,
            "231:13: " + warningMessage,
            "247:9: " + warningMessage,
            "250:14: " + warningMessage,
            "257:9: " + warningMessage,
            "259:14: " + warningMessage,
        };

        verify(checkConfig, getPath("InputConfusingConditionCheck.java"),
                expected);
    }

    @Test
    public void testMultiplyFactor() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(ConfusingConditionCheck.class);

        checkConfig.addProperty("multiplyFactorForElseBlocks", "0");

        final String[] expected = {
            "10:9: " + warningMessage,
            "13:9: " + warningMessage,
            "16:9: " + warningMessage,
            "19:9: " + warningMessage,
            "22:9: " + warningMessage,
            "105:9: " + warningMessage,
            "108:9: " + warningMessage,
            "111:9: " + warningMessage,
            "177:14: " + warningMessage,
            "259:14: " + warningMessage,
        };

        verify(checkConfig, getPath("InputConfusingConditionCheck.java"),
                expected);
    }

    @Test
    public void testExceptions() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(ConfusingConditionCheck.class);

        final String[] expected = {};

        verify(checkConfig, getPath("InputConfusingConditionCheck2.java"),
                expected);
    }

}
