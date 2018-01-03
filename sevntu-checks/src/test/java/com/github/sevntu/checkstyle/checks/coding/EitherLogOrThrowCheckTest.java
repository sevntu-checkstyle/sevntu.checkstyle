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

import static com.github.sevntu.checkstyle.checks.coding.EitherLogOrThrowCheck.MSG_KEY;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

/**
 * Test for EitherLogOrThrowCheck.
 * @author <a href="mailto:barataliba@gmail.com">Baratali Izmailov</a>
 */
public class EitherLogOrThrowCheckTest extends AbstractModuleTestSupport {

    private final String warningMessage = getCheckMessage(MSG_KEY);

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/coding";
    }

    @Test
    public void test() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(EitherLogOrThrowCheck.class);
        checkConfig.addAttribute("loggerFullyQualifiedClassName", "org.slf4j.Logger");
        checkConfig.addAttribute("loggingMethodNames", "error, warn");

        final String[] expected = {
            "19: " + warningMessage,
            "31: " + warningMessage,
            "43: " + warningMessage,
            "82: " + warningMessage,
            "93: " + warningMessage,
            "102: " + warningMessage,
            "112: " + warningMessage,
            "124: " + warningMessage,
            "154: " + warningMessage,
            "164: " + warningMessage,
            "207: " + warningMessage,
            "231: " + warningMessage,
            "241: " + warningMessage,
            "252: " + warningMessage,
            "262: " + warningMessage,
        };
        verify(checkConfig, getPath("InputEitherLogOrThrowCheck.java"),
                expected);
    }

}
