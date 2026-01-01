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

import static com.github.sevntu.checkstyle.checks.coding.WhitespaceBeforeArrayInitializerCheck.MSG_KEY;

import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class WhitespaceBeforeArrayInitializerCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/coding";
    }

    @Test
    public void testWhitespaceBeforeArrayInitializer() throws Exception {
        final DefaultConfiguration mDefaultConfig =
                createModuleConfig(WhitespaceBeforeArrayInitializerCheck.class);
        final String[] expected = {
            "5:28: " + getCheckMessage(MSG_KEY),
            "13:32: " + getCheckMessage(MSG_KEY),
            "16:32: " + getCheckMessage(MSG_KEY),
            "17:21: " + getCheckMessage(MSG_KEY),
            "24:56: " + getCheckMessage(MSG_KEY),
        };
        verify(mDefaultConfig, getPath("InputWhitespaceBeforeArrayInitializerCheck.java"),
                expected);
    }

}
