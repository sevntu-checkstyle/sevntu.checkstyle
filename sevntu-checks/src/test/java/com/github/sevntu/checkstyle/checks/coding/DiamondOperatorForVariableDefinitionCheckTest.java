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

import static com.github.sevntu.checkstyle.checks.coding.DiamondOperatorForVariableDefinitionCheck.MSG_KEY;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class DiamondOperatorForVariableDefinitionCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/coding";
    }

    @Test
    public void testDefault() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(DiamondOperatorForVariableDefinitionCheck.class);
        final String[] expected = {
            "25:58: " + getCheckMessage(MSG_KEY),
            "27:26: " + getCheckMessage(MSG_KEY),
            "28:50: " + getCheckMessage(MSG_KEY),
            "29:41: " + getCheckMessage(MSG_KEY),
            "59:40: " + getCheckMessage(MSG_KEY),
            "60:83: " + getCheckMessage(MSG_KEY),
            "79:48: " + getCheckMessage(MSG_KEY),
            "92:78: " + getCheckMessage(MSG_KEY),
            "93:68: " + getCheckMessage(MSG_KEY),
        };
        verify(checkConfig,
                getPath("InputDiamondOperatorForVariableDefinitionCheck.java"), expected);
    }

}
