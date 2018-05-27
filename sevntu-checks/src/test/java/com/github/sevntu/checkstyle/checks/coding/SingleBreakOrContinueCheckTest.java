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

import static com.github.sevntu.checkstyle.checks.coding.SingleBreakOrContinueCheck.MSG_KEY;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

/**
 * @author <a href="mailto:yasser.aziza@gmail.com"> Yasser Aziza </a>
 */
public class SingleBreakOrContinueCheckTest extends AbstractModuleTestSupport {

    /**
     * An error message for current check.
     */
    private final String warningMessage = getCheckMessage(MSG_KEY);

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/coding";
    }

    @Test
    public void testDefault()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(SingleBreakOrContinueCheck.class);
        final String[] expected = {
            "8:9: " + warningMessage,
            "26:9: " + warningMessage,
            "44:9: " + warningMessage,
            "88:9: " + warningMessage,
            "106:9: " + warningMessage,
            "124:9: " + warningMessage,
            "168:9: " + warningMessage,
            "187:9: " + warningMessage,
            "206:9: " + warningMessage,
            "346:13: " + warningMessage,
            "367:13: " + warningMessage,
            "389:13: " + warningMessage,
        };

        verify(checkConfig, getPath("InputSingleBreakOrContinueCheck.java"),
                expected);
    }

}
