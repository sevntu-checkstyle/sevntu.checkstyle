////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2011  Oliver Burn
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
    public void testAll() throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(LogicConditionNeedOptimizationCheck.class);
        final String[] expected = {
            "26: Condition with && 26:27 need optimization.",
            "28: Condition with && 28:21 need optimization.",
            "28: Condition with || 28:31 need optimization.",
            "38: Condition with && 38:21 need optimization.",
            "40: Condition with && 40:38 need optimization.",
            "45: Condition with && 45:25 need optimization.",
            "47: Condition with && 47:42 need optimization.",
            "57: Condition with && 57:18 need optimization.",
            "59: Condition with && 59:35 need optimization.",
            "60: Condition with && 60:19 need optimization.",
            "60: Condition with && 60:29 need optimization.", };
        verify(checkConfig, getPath("coding" + File.separator
            + "InputLogicConditionsNeedOptimizationCheck.java"), expected);
    }
}
