////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2012  Oliver Burn
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

import java.io.File;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class LogicConditionNeedOptimizationCheckTest extends BaseCheckTestSupport
{

    @Test
    public void test() throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(LogicConditionNeedOptimizationCheck.class);
        final String[] expected = {
            "26: Condition with && at line 26 position 27 need optimization. All method calls are advised to move to end of logic expression.",
            "28: Condition with && at line 28 position 21 need optimization. All method calls are advised to move to end of logic expression.",
            "28: Condition with || at line 28 position 31 need optimization. All method calls are advised to move to end of logic expression.",
            "38: Condition with && at line 38 position 21 need optimization. All method calls are advised to move to end of logic expression.",
            "40: Condition with && at line 40 position 38 need optimization. All method calls are advised to move to end of logic expression.",
            "45: Condition with && at line 45 position 25 need optimization. All method calls are advised to move to end of logic expression.",
            "47: Condition with && at line 47 position 42 need optimization. All method calls are advised to move to end of logic expression.",
            "57: Condition with && at line 57 position 18 need optimization. All method calls are advised to move to end of logic expression.",
            "59: Condition with && at line 59 position 35 need optimization. All method calls are advised to move to end of logic expression.",
            "60: Condition with && at line 60 position 19 need optimization. All method calls are advised to move to end of logic expression.",
            "60: Condition with && at line 60 position 29 need optimization. All method calls are advised to move to end of logic expression.",
            "77: Condition with && at line 77 position 47 need optimization. All method calls are advised to move to end of logic expression.",
            "81: Condition with && at line 81 position 19 need optimization. All method calls are advised to move to end of logic expression.",
            "82: Condition with && at line 82 position 39 need optimization. All method calls are advised to move to end of logic expression.",
            "84: Condition with && at line 84 position 36 need optimization. All method calls are advised to move to end of logic expression.",
            "87: Condition with && at line 87 position 19 need optimization. All method calls are advised to move to end of logic expression.",
        };
        verify(checkConfig, getPath("coding" + File.separator
            + "InputLogicConditionsNeedOptimizationCheck.java"), expected);
    }
}
