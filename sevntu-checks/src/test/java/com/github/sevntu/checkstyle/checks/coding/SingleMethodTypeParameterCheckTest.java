////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2020 the original author or authors.
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

import static com.github.sevntu.checkstyle.checks.coding.SingleMethodTypeParameterCheck.MSG_KEY;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

/**
 * @author <a href="mailto:yasser.aziza@gmail.com"> Yasser Aziza </a>
 */
public class SingleMethodTypeParameterCheckTest extends AbstractModuleTestSupport {

    /**
     * An error message for current check.
     */
    private final String warningMessage = getCheckMessage(MSG_KEY);

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/coding";
    }

    @Test
    public void testDefault() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(SingleMethodTypeParameterCheck.class);

        final String[] expected = {
            "11:32: " + warningMessage,
            "13:34: " + warningMessage,
            "28:44: " + warningMessage,
            "30:46: " + warningMessage,
            "32:66: " + warningMessage,
            "42:40: " + warningMessage,
            "46:48: " + warningMessage,
            "50:48: " + warningMessage,
            "52:43: " + warningMessage,
        };

        verify(checkConfig, getPath("InputSingleMethodTypeParameterCheck.java"), expected);
    }

}
