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

import static com.github.sevntu.checkstyle.checks.design.InnerClassCheck.MSG_KEY;

import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class InnerClassCheckTest extends AbstractModuleTestSupport {

    private final String warningMessage = getCheckMessage(MSG_KEY);

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/design";
    }

    @Test
    public void testMembersBeforeInner() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(InnerClassCheck.class);
        final String[] expected = {
            "15:17: " + warningMessage,
            "25:17: " + warningMessage,
            "26:17: " + warningMessage,
            "39:25: " + warningMessage,
            "40:25: " + warningMessage,
            "44:9: " + warningMessage,
            "60:25: " + warningMessage,
            "61:25: " + warningMessage,
            "65:9: " + warningMessage,
            "69:9: " + warningMessage,
            "78:5: " + warningMessage,
        };
        verify(checkConfig, getPath("InputInnerClassCheck.java"), expected);
    }

}
