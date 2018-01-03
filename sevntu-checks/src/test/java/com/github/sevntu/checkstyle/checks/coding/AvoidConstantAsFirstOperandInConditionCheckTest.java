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

import static com.github.sevntu.checkstyle.checks.coding.AvoidConstantAsFirstOperandInConditionCheck.MSG_KEY;

import org.junit.Test;

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
            "24: " + getCheckMessage(MSG_KEY, "=="),
            "25: " + getCheckMessage(MSG_KEY, "=="),
            "27: " + getCheckMessage(MSG_KEY, "=="),
            "28: " + getCheckMessage(MSG_KEY, "=="),
            "29: " + getCheckMessage(MSG_KEY, "=="),
            "30: " + getCheckMessage(MSG_KEY, "=="),
            "31: " + getCheckMessage(MSG_KEY, "=="),
            "46: " + getCheckMessage(MSG_KEY, "=="),
            "47: " + getCheckMessage(MSG_KEY, "!="),
            "52: " + getCheckMessage(MSG_KEY, "=="),
            "53: " + getCheckMessage(MSG_KEY, "!="),
            "58: " + getCheckMessage(MSG_KEY, "=="),
            "59: " + getCheckMessage(MSG_KEY, "!="),
            "67: " + getCheckMessage(MSG_KEY, "=="),
            "71: " + getCheckMessage(MSG_KEY, "=="),
            "72: " + getCheckMessage(MSG_KEY, "=="),
            "73: " + getCheckMessage(MSG_KEY, "=="),
            "74: " + getCheckMessage(MSG_KEY, "=="),
            "76: " + getCheckMessage(MSG_KEY, "=="),
            "77: " + getCheckMessage(MSG_KEY, "=="),
            "78: " + getCheckMessage(MSG_KEY, "=="),
            "84: " + getCheckMessage(MSG_KEY, "=="),
            "85: " + getCheckMessage(MSG_KEY, "=="),
            "86: " + getCheckMessage(MSG_KEY, "=="),
            "97: " + getCheckMessage(MSG_KEY, "=="),
            "101: " + getCheckMessage(MSG_KEY, "=="),
            "111: " + getCheckMessage(MSG_KEY, "=="),
            "112: " + getCheckMessage(MSG_KEY, "=="),
        };
        verify(checkConfig, getPath("InputAvoidConstantAsFirstOperandInConditionCheck.java"),
                expected);
    }

    @Test
    public void testAttributes() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(AvoidConstantAsFirstOperandInConditionCheck.class);
        checkConfig.addAttribute("targetConstantTypes", "LITERAL_FALSE,NUM_INT,NUM_FLOAT");
        final String[] expected = {
            "25: " + getCheckMessage(MSG_KEY, "=="),
            "28: " + getCheckMessage(MSG_KEY, "=="),
            "31: " + getCheckMessage(MSG_KEY, "=="),
        };
        verify(checkConfig, getPath("InputAvoidConstantAsFirstOperandInConditionCheck.java"),
                expected);
    }

    @Test
    public void testNullProperties() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(AvoidConstantAsFirstOperandInConditionCheck.class);

        checkConfig.addAttribute("targetConstantTypes", null);

        final String[] expected = {
            "24: " + getCheckMessage(MSG_KEY, "=="),
            "25: " + getCheckMessage(MSG_KEY, "=="),
            "27: " + getCheckMessage(MSG_KEY, "=="),
            "28: " + getCheckMessage(MSG_KEY, "=="),
            "29: " + getCheckMessage(MSG_KEY, "=="),
            "30: " + getCheckMessage(MSG_KEY, "=="),
            "31: " + getCheckMessage(MSG_KEY, "=="),
            "46: " + getCheckMessage(MSG_KEY, "=="),
            "47: " + getCheckMessage(MSG_KEY, "!="),
            "52: " + getCheckMessage(MSG_KEY, "=="),
            "53: " + getCheckMessage(MSG_KEY, "!="),
            "58: " + getCheckMessage(MSG_KEY, "=="),
            "59: " + getCheckMessage(MSG_KEY, "!="),
            "67: " + getCheckMessage(MSG_KEY, "=="),
            "71: " + getCheckMessage(MSG_KEY, "=="),
            "72: " + getCheckMessage(MSG_KEY, "=="),
            "73: " + getCheckMessage(MSG_KEY, "=="),
            "74: " + getCheckMessage(MSG_KEY, "=="),
            "76: " + getCheckMessage(MSG_KEY, "=="),
            "77: " + getCheckMessage(MSG_KEY, "=="),
            "78: " + getCheckMessage(MSG_KEY, "=="),
            "84: " + getCheckMessage(MSG_KEY, "=="),
            "85: " + getCheckMessage(MSG_KEY, "=="),
            "86: " + getCheckMessage(MSG_KEY, "=="),
            "97: " + getCheckMessage(MSG_KEY, "=="),
            "101: " + getCheckMessage(MSG_KEY, "=="),
            "111: " + getCheckMessage(MSG_KEY, "=="),
            "112: " + getCheckMessage(MSG_KEY, "=="),
        };

        verify(checkConfig, getPath("InputAvoidConstantAsFirstOperandInConditionCheck.java"),
                expected);
    }

}
