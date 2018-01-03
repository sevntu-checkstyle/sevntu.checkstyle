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

package com.github.sevntu.checkstyle.checks.design;

import static com.github.sevntu.checkstyle.checks.design.NoMainMethodInAbstractClassCheck.MSG_KEY;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

/**
 * Test class for NoMainMethodInAbstractClass check.
 * @author Baratali Izmailov
 */
public class NoMainMethodInAbstractClassCheckTest extends AbstractModuleTestSupport {

    /**
     * Name of file with messages.
     */
    public static final String PROP_FILE_NAME = "messages.properties";
    /**
     * Message for this check.
     */
    private final String warningMessage = getCheckMessage(MSG_KEY);

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/design";
    }

    /**
     * Main test.
     * @throws Exception
     *         exceptions while verify()
     */
    @Test
    public final void testDefault()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(NoMainMethodInAbstractClassCheck.class);
        final String[] expected = {
            "23: " + warningMessage,
            "26: " + warningMessage,
            "34: " + warningMessage,
            "37: " + warningMessage,
            "51: " + warningMessage,
            "53: " + warningMessage,
            "57: " + warningMessage,
            "59: " + warningMessage,
            "100: " + warningMessage,
            "103: " + warningMessage,
            "106: " + warningMessage,
            "109: " + warningMessage,
            "116: " + warningMessage,
            "119: " + warningMessage,
            "122: " + warningMessage,
            "125: " + warningMessage,
            "130: " + warningMessage,
            "136: " + warningMessage,
            "151: " + warningMessage,
            "155: " + warningMessage,
            "166: " + warningMessage,
            "175: " + warningMessage,
            "185: " + warningMessage,
        };

        verify(checkConfig,
                getPath("InputNoMainMethodInAbstractClassCheck.java"),
                expected);
    }

}
