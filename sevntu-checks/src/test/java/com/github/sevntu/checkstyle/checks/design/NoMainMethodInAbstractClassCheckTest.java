////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2012  Oliver Burn
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

import java.util.Properties;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

/**
 * Test class for NoMainMethodInAbstractClass check.
 * @author Baratali Izmailov
 */
public class NoMainMethodInAbstractClassCheckTest extends BaseCheckTestSupport
{
    /**
     * Name of file with messages.
     */
    public static final String PROP_FILE_NAME = "messages.properties";
    /**
     * Message for this check.
     */
    private static final String MESSAGE =
            getMessage(NoMainMethodInAbstractClassCheck.MSG_KEY);

    /**
     * Main test.
     * @throws Exception
     *         exceptions while verify()
     */
    @Test
    public final void testDefault()
        throws Exception
    {
        final DefaultConfiguration checkConfig =
                createCheckConfig(NoMainMethodInAbstractClassCheck.class);
        final int[] lines = {23, 26, 34, 37, 51, 53, 57, 59, 100, 103, 106,
            109, 116, 119, 122, 125, 130, 136, 151, 155, 164, 173, 183, };
        final String[] expected = new String[lines.length];
        for (int i = 0; i < lines.length; i++) {
            expected[i] = lines[i] + ": " + MESSAGE;
        }

        verify(checkConfig,
                getPath("InputNoMainMethodInAbstractClassCheck.java"),
                expected);
    }

    /**
     * Get error message from property file.
     * @param aKey
     *        key for error message
     * @return error message
     */
    private static String getMessage(final String aKey)
    {
        final Properties prop = new Properties();
        try {
            prop.load(NoMainMethodInAbstractClassCheck.class
                    .getResourceAsStream(PROP_FILE_NAME));
        }
        catch (Exception e) {
            return null;
        }
        return prop.getProperty(aKey);
    }
}
