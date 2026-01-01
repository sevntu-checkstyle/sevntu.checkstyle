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

import static com.github.sevntu.checkstyle.checks.coding.AvoidDefaultSerializableInInnerClassesCheck.MSG_KEY;

import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class AvoidDefaultSerializableInInnerClassesCheckTest extends AbstractModuleTestSupport {

    private final String warningMessage = getCheckMessage(MSG_KEY);

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/coding";
    }

    @Test
    public void testWithAllowPartialFalse()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(AvoidDefaultSerializableInInnerClassesCheck.class);

        final String[] expected = {
            "33:48: " + warningMessage,
            "45:31: " + warningMessage,
            "52:31: " + warningMessage,
            "59:30: " + warningMessage,
            "67:9: " + warningMessage,
            "74:30: " + warningMessage,
            "97:30: " + warningMessage,
            "104:34: " + warningMessage,
            "121:30: " + warningMessage,
            "134:30: " + warningMessage,
            "145:36: " + warningMessage,
            "159:42: " + warningMessage,
            "171:39: " + warningMessage,
            "179:42: " + warningMessage,
            "187:35: " + warningMessage,
        };
        verify(checkConfig, getPath("InputAvoidDefaultSerializableInInnerClassesCheck1.java"),
                expected);
    }

    @Test
    public void testPrivateNotRealReadObject()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(AvoidDefaultSerializableInInnerClassesCheck.class);
        checkConfig.addProperty("allowPartialImplementation", "true");

        final String[] expected = {
            "10:22: " + warningMessage,
        };

        verify(checkConfig, getPath("InputAvoidDefaultSerializableInInnerClassesCheck2.java"),
                expected);
    }

    @Test
    public void testRealReadObjectNotRealReadObjectRealPrivate()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(AvoidDefaultSerializableInInnerClassesCheck.class);

        final String[] expected = {};

        verify(checkConfig, getPath("InputAvoidDefaultSerializableInInnerClassesCheck3.java"),
                expected);
    }

    @Test
    public void testWithAllowPartialTrue()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(AvoidDefaultSerializableInInnerClassesCheck.class);
        checkConfig.addProperty("allowPartialImplementation", "true");
        final String[] expected = {
            "33:48: " + warningMessage,
            "59:30: " + warningMessage,
            "67:9: " + warningMessage,
            "74:30: " + warningMessage,
            "97:30: " + warningMessage,
            "104:34: " + warningMessage,
            "121:30: " + warningMessage,
            "134:30: " + warningMessage,
            "145:36: " + warningMessage,
            "171:39: " + warningMessage,
            "187:35: " + warningMessage,
        };
        verify(checkConfig, getPath("InputAvoidDefaultSerializableInInnerClassesCheck1.java"),
                expected);
    }

}
