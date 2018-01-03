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

import static com.github.sevntu.checkstyle.checks.coding.LogicConditionNeedOptimizationCheck.MSG_KEY;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class LogicConditionNeedOptimizationCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/coding";
    }

    @Test
    public void test() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(LogicConditionNeedOptimizationCheck.class);
        final String[] expected = {
            "26: " + getCheckMessage(MSG_KEY, "&&", 26, 27),
            "28: " + getCheckMessage(MSG_KEY, "&&", 28, 21),
            "28: " + getCheckMessage(MSG_KEY, "||", 28, 31),
            "38: " + getCheckMessage(MSG_KEY, "&&", 38, 21),
            "40: " + getCheckMessage(MSG_KEY, "&&", 40, 38),
            "45: " + getCheckMessage(MSG_KEY, "&&", 45, 25),
            "47: " + getCheckMessage(MSG_KEY, "&&", 47, 42),
            "57: " + getCheckMessage(MSG_KEY, "&&", 57, 18),
            "59: " + getCheckMessage(MSG_KEY, "&&", 59, 35),
            "60: " + getCheckMessage(MSG_KEY, "&&", 60, 19),
            "60: " + getCheckMessage(MSG_KEY, "&&", 60, 29),
            "62: " + getCheckMessage(MSG_KEY, "&&", 62, 21),
            "66: " + getCheckMessage(MSG_KEY, "&&", 66, 19),
            "67: " + getCheckMessage(MSG_KEY, "&&", 67, 38),
            "69: " + getCheckMessage(MSG_KEY, "&&", 69, 35),
            "72: " + getCheckMessage(MSG_KEY, "&&", 72, 19),
            "93: " + getCheckMessage(MSG_KEY, "||", 93, 33),
        };
        verify(checkConfig, getPath("InputLogicConditionNeedOptimizationCheck.java"), expected);
    }

}
