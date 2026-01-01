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

import static com.github.sevntu.checkstyle.checks.coding.MoveVariableInsideIfCheck.MSG_KEY;

import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.utils.CommonUtil;

public class MoveVariableInsideIfCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/coding";
    }

    @Test
    public final void testNoViolations() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(MoveVariableInsideIfCheck.class);
        verify(checkConfig, getPath("InputMoveVariableInsideIfCheckNoViolations.java"),
                CommonUtil.EMPTY_STRING_ARRAY);
    }

    @Test
    public final void testViolations() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(MoveVariableInsideIfCheck.class);
        final String[] expected = {
            "5:9: " + getCheckMessage(MSG_KEY, "variable", "7"),
            "13:9: " + getCheckMessage(MSG_KEY, "variable", "15"),
            "24:9: " + getCheckMessage(MSG_KEY, "variable", "26"),
            "33:9: " + getCheckMessage(MSG_KEY, "variable", "38"),
            "44:9: " + getCheckMessage(MSG_KEY, "variable", "48"),
        };
        verify(checkConfig, getPath("InputMoveVariableInsideIfCheckViolations.java"), expected);
    }

    @Test
    public final void testFalsePositives() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(MoveVariableInsideIfCheck.class);
        final String[] expected = {
            "9:9: " + getCheckMessage(MSG_KEY, "variable", "12"),
            "18:9: " + getCheckMessage(MSG_KEY, "variable", "24"),
            "30:9: " + getCheckMessage(MSG_KEY, "variable", "34"),
        };
        verify(checkConfig, getPath("InputMoveVariableInsideIfCheckFalsePositives.java"),
                expected);
    }

}
