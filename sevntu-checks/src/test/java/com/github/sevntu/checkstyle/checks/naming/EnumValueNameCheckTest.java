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

package com.github.sevntu.checkstyle.checks.naming;

import static com.github.sevntu.checkstyle.checks.naming.EnumValueNameCheck.DEFAULT_PATTERN;
import static com.puppycrawl.tools.checkstyle.checks.naming.AbstractNameCheck.MSG_INVALID_PATTERN;

import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class EnumValueNameCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/naming";
    }

    @Test
    public void testDefault()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(EnumValueNameCheck.class);
        final String[] expected = {
            "35:9: " + getCheckMessage(MSG_INVALID_PATTERN, "FirstSimple", DEFAULT_PATTERN),
            "43:9: " + getCheckMessage(MSG_INVALID_PATTERN, "FirstComplex", DEFAULT_PATTERN),
            "66:19: " + getCheckMessage(MSG_INVALID_PATTERN, "MoSecond", DEFAULT_PATTERN),
            "76:19: " + getCheckMessage(MSG_INVALID_PATTERN, "FoSecond", DEFAULT_PATTERN),
        };
        verify(checkConfig, getPath("InputEnumValueNameCheck.java"), expected);
    }

    @Test
    public void testCustomFormat()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(EnumValueNameCheck.class);

        checkConfig.addProperty("format", "[a-z]+");

        final String[] expected = {
            "35:22: " + getCheckMessage(MSG_INVALID_PATTERN, "SECOND_SIMPLE", "[a-z]+"),
            "35:37: " + getCheckMessage(MSG_INVALID_PATTERN, "DB2", "[a-z]+"),
            "35:42: " + getCheckMessage(MSG_INVALID_PATTERN, "V1", "[a-z]+"),
            "43:26: " + getCheckMessage(MSG_INVALID_PATTERN, "SECOND_COMPLEX", "[a-z]+"),
            "43:45: " + getCheckMessage(MSG_INVALID_PATTERN, "V2", "[a-z]+"),
            "66:9: " + getCheckMessage(MSG_INVALID_PATTERN, "MO_FIRST", "[a-z]+"),
            "76:9: " + getCheckMessage(MSG_INVALID_PATTERN, "FO_FIRST", "[a-z]+"),
        };
        verify(checkConfig, getPath("InputEnumValueNameCheck.java"), expected);
    }

}
