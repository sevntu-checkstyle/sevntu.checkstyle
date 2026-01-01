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

package com.github.sevntu.checkstyle.checks.design;

import static com.github.sevntu.checkstyle.checks.design.NestedSwitchCheck.MSG_KEY;

import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.utils.CommonUtil;

public class NestedSwitchCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/design";
    }

    @Test
    public void testSimple() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(NestedSwitchCheck.class);
        final String[] expected = {
            "11:13: " + getCheckMessage(MSG_KEY),
            "23:13: " + getCheckMessage(MSG_KEY),
            "28:13: " + getCheckMessage(MSG_KEY),
            "35:13: " + getCheckMessage(MSG_KEY),
            "37:17: " + getCheckMessage(MSG_KEY),
            "50:25: " + getCheckMessage(MSG_KEY),
            "52:29: " + getCheckMessage(MSG_KEY),
            "63:25: " + getCheckMessage(MSG_KEY),
            "65:29: " + getCheckMessage(MSG_KEY),
        };

        verify(checkConfig, getPath("InputNestedSwitchCheck.java"),
                expected);
    }

    @Test
    public void testMax() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(NestedSwitchCheck.class);
        checkConfig.addProperty("max", "99");
        final String[] expected = CommonUtil.EMPTY_STRING_ARRAY;

        verify(checkConfig, getPath("InputNestedSwitchCheck.java"),
                expected);
    }

}
