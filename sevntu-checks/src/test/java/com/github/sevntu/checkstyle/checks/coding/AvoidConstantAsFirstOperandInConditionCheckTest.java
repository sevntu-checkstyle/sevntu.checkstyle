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

import static com.github.sevntu.checkstyle.checks.coding.AvoidConstantAsFirstOperandInConditionCheck.MSG_KEY;

import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class AvoidConstantAsFirstOperandInConditionCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/coding";
    }

    @Test
    public void testAll() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(AvoidConstantAsFirstOperandInConditionCheck.class);
        final String[] expected = {
            "24:20: " + getCheckMessage(MSG_KEY, "=="),
            "25:15: " + getCheckMessage(MSG_KEY, "=="),
            "27:18: " + getCheckMessage(MSG_KEY, "=="),
            "28:18: " + getCheckMessage(MSG_KEY, "=="),
            "29:16: " + getCheckMessage(MSG_KEY, "=="),
            "30:18: " + getCheckMessage(MSG_KEY, "=="),
            "31:19: " + getCheckMessage(MSG_KEY, "=="),
            "46:18: " + getCheckMessage(MSG_KEY, "=="),
            "47:18: " + getCheckMessage(MSG_KEY, "!="),
            "52:18: " + getCheckMessage(MSG_KEY, "=="),
            "53:18: " + getCheckMessage(MSG_KEY, "!="),
            "58:18: " + getCheckMessage(MSG_KEY, "=="),
            "59:18: " + getCheckMessage(MSG_KEY, "!="),
            "67:30: " + getCheckMessage(MSG_KEY, "=="),
            "71:36: " + getCheckMessage(MSG_KEY, "=="),
            "72:37: " + getCheckMessage(MSG_KEY, "=="),
            "73:19: " + getCheckMessage(MSG_KEY, "=="),
            "74:37: " + getCheckMessage(MSG_KEY, "=="),
            "76:42: " + getCheckMessage(MSG_KEY, "=="),
            "77:18: " + getCheckMessage(MSG_KEY, "=="),
            "78:18: " + getCheckMessage(MSG_KEY, "=="),
            "78:42: " + getCheckMessage(MSG_KEY, "=="),
            "84:25: " + getCheckMessage(MSG_KEY, "=="),
            "85:23: " + getCheckMessage(MSG_KEY, "=="),
            "86:33: " + getCheckMessage(MSG_KEY, "=="),
            "97:21: " + getCheckMessage(MSG_KEY, "=="),
            "101:23: " + getCheckMessage(MSG_KEY, "=="),
            "111:19: " + getCheckMessage(MSG_KEY, "=="),
            "112:40: " + getCheckMessage(MSG_KEY, "=="),
            "118:15: " + getCheckMessage(MSG_KEY, "=="),
            "119:15: " + getCheckMessage(MSG_KEY, "!="),
            "120:15: " + getCheckMessage(MSG_KEY, "!="),
            "121:18: " + getCheckMessage(MSG_KEY, "!="),
            "129:15: " + getCheckMessage(MSG_KEY, "<"),
            "130:15: " + getCheckMessage(MSG_KEY, "<="),
            "131:15: " + getCheckMessage(MSG_KEY, ">"),
            "132:15: " + getCheckMessage(MSG_KEY, ">="),
        };
        verify(checkConfig, getPath("InputAvoidConstantAsFirstOperandInConditionCheck.java"),
                expected);
    }

    @Test
    public void testAttributes() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(AvoidConstantAsFirstOperandInConditionCheck.class);
        checkConfig.addProperty("targetConstantTypes", "LITERAL_FALSE,NUM_INT,NUM_FLOAT");
        final String[] expected = {
            "25:15: " + getCheckMessage(MSG_KEY, "=="),
            "28:18: " + getCheckMessage(MSG_KEY, "=="),
            "31:19: " + getCheckMessage(MSG_KEY, "=="),
            "118:15: " + getCheckMessage(MSG_KEY, "=="),
            "119:15: " + getCheckMessage(MSG_KEY, "!="),
            "120:15: " + getCheckMessage(MSG_KEY, "!="),
            "129:15: " + getCheckMessage(MSG_KEY, "<"),
            "130:15: " + getCheckMessage(MSG_KEY, "<="),
            "131:15: " + getCheckMessage(MSG_KEY, ">"),
            "132:15: " + getCheckMessage(MSG_KEY, ">="),
        };
        verify(checkConfig, getPath("InputAvoidConstantAsFirstOperandInConditionCheck.java"),
                expected);
    }

    @Test
    public void testNullProperties() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(AvoidConstantAsFirstOperandInConditionCheck.class);

        checkConfig.addProperty("targetConstantTypes", null);

        final String[] expected = {
            "24:20: " + getCheckMessage(MSG_KEY, "=="),
            "25:15: " + getCheckMessage(MSG_KEY, "=="),
            "27:18: " + getCheckMessage(MSG_KEY, "=="),
            "28:18: " + getCheckMessage(MSG_KEY, "=="),
            "29:16: " + getCheckMessage(MSG_KEY, "=="),
            "30:18: " + getCheckMessage(MSG_KEY, "=="),
            "31:19: " + getCheckMessage(MSG_KEY, "=="),
            "46:18: " + getCheckMessage(MSG_KEY, "=="),
            "47:18: " + getCheckMessage(MSG_KEY, "!="),
            "52:18: " + getCheckMessage(MSG_KEY, "=="),
            "53:18: " + getCheckMessage(MSG_KEY, "!="),
            "58:18: " + getCheckMessage(MSG_KEY, "=="),
            "59:18: " + getCheckMessage(MSG_KEY, "!="),
            "67:30: " + getCheckMessage(MSG_KEY, "=="),
            "71:36: " + getCheckMessage(MSG_KEY, "=="),
            "72:37: " + getCheckMessage(MSG_KEY, "=="),
            "73:19: " + getCheckMessage(MSG_KEY, "=="),
            "74:37: " + getCheckMessage(MSG_KEY, "=="),
            "76:42: " + getCheckMessage(MSG_KEY, "=="),
            "77:18: " + getCheckMessage(MSG_KEY, "=="),
            "78:18: " + getCheckMessage(MSG_KEY, "=="),
            "78:42: " + getCheckMessage(MSG_KEY, "=="),
            "84:25: " + getCheckMessage(MSG_KEY, "=="),
            "85:23: " + getCheckMessage(MSG_KEY, "=="),
            "86:33: " + getCheckMessage(MSG_KEY, "=="),
            "97:21: " + getCheckMessage(MSG_KEY, "=="),
            "101:23: " + getCheckMessage(MSG_KEY, "=="),
            "111:19: " + getCheckMessage(MSG_KEY, "=="),
            "112:40: " + getCheckMessage(MSG_KEY, "=="),
            "118:15: " + getCheckMessage(MSG_KEY, "=="),
            "119:15: " + getCheckMessage(MSG_KEY, "!="),
            "120:15: " + getCheckMessage(MSG_KEY, "!="),
            "121:18: " + getCheckMessage(MSG_KEY, "!="),
            "129:15: " + getCheckMessage(MSG_KEY, "<"),
            "130:15: " + getCheckMessage(MSG_KEY, "<="),
            "131:15: " + getCheckMessage(MSG_KEY, ">"),
            "132:15: " + getCheckMessage(MSG_KEY, ">="),
        };

        verify(checkConfig, getPath("InputAvoidConstantAsFirstOperandInConditionCheck.java"),
                expected);
    }

}
