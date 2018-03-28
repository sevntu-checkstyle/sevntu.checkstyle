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

import static com.github.sevntu.checkstyle.checks.coding.AvoidDefaultSerializableInInnerClassesCheck.MSG_KEY;

import org.junit.Test;

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
            "33: " + warningMessage,
            "45: " + warningMessage,
            "52: " + warningMessage,
            "59: " + warningMessage,
            "67: " + warningMessage,
            "74: " + warningMessage,
            "97: " + warningMessage,
            "104: " + warningMessage,
            "121: " + warningMessage,
            "134: " + warningMessage,
            "145: " + warningMessage,
            "159: " + warningMessage,
            "171: " + warningMessage,
            "179: " + warningMessage,
            "187: " + warningMessage,
        };
        verify(checkConfig, getPath("InputAvoidDefaultSerializableInInnerClassesCheck1.java"),
                expected);
    }

    @Test
    public void testPrivateNotRealReadObject()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(AvoidDefaultSerializableInInnerClassesCheck.class);
        checkConfig.addAttribute("allowPartialImplementation", "true");

        final String[] expected = {
            "10: " + warningMessage,
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
        checkConfig.addAttribute("allowPartialImplementation", "true");
        final String[] expected = {
            "33: " + warningMessage,
            "59: " + warningMessage,
            "67: " + warningMessage,
            "74: " + warningMessage,
            "97: " + warningMessage,
            "104: " + warningMessage,
            "121: " + warningMessage,
            "134: " + warningMessage,
            "145: " + warningMessage,
            "171: " + warningMessage,
            "187: " + warningMessage,
        };
        verify(checkConfig, getPath("InputAvoidDefaultSerializableInInnerClassesCheck1.java"),
                expected);
    }

}
