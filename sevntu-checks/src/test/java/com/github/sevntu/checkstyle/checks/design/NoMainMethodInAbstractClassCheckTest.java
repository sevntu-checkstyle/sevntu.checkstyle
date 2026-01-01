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

import static com.github.sevntu.checkstyle.checks.design.NoMainMethodInAbstractClassCheck.MSG_KEY;

import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

/**
 * Test class for NoMainMethodInAbstractClass check.
 *
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
     *
     * @throws Exception
     *         exceptions while verify()
     */
    @Test
    public final void testDefault()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(NoMainMethodInAbstractClassCheck.class);
        final String[] expected = {
            "23:5: " + warningMessage,
            "26:5: " + warningMessage,
            "34:9: " + warningMessage,
            "37:9: " + warningMessage,
            "51:5: " + warningMessage,
            "53:9: " + warningMessage,
            "57:5: " + warningMessage,
            "59:9: " + warningMessage,
            "100:5: " + warningMessage,
            "103:5: " + warningMessage,
            "106:5: " + warningMessage,
            "109:5: " + warningMessage,
            "116:5: " + warningMessage,
            "119:5: " + warningMessage,
            "122:5: " + warningMessage,
            "125:5: " + warningMessage,
            "130:5: " + warningMessage,
            "136:5: " + warningMessage,
            "151:8: " + warningMessage,
            "155:4: " + warningMessage,
            "166:9: " + warningMessage,
            "175:9: " + warningMessage,
            "185:9: " + warningMessage,
        };

        verify(checkConfig,
                getPath("InputNoMainMethodInAbstractClassCheck.java"),
                expected);
    }

}
