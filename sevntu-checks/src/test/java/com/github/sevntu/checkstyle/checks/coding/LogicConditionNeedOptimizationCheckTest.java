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

import static com.github.sevntu.checkstyle.checks.coding.LogicConditionNeedOptimizationCheck.*;

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
        	"77: " + getCheckMessage(MSG_KEY, "&&", 77, 47),
        	"81: " + getCheckMessage(MSG_KEY, "&&", 81, 19),
        	"82: " + getCheckMessage(MSG_KEY, "&&", 82, 39),
        	"84: " + getCheckMessage(MSG_KEY, "&&", 84, 36),
        	"87: " + getCheckMessage(MSG_KEY, "&&", 87, 19),
        };
        verify(checkConfig, getPath("InputLogicConditionsNeedOptimizationCheck.java"), expected);
    }
}
