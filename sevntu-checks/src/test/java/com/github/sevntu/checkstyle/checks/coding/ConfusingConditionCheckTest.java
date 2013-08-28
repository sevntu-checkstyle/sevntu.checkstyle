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
package com.github.sevntu.checkstyle.checks.coding;

import java.io.IOException;
import java.util.Properties;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

/**
 * @author <a href="mailto:vadim.panasiuk@gmail.com">Vadim Panasiuk</a>
 */
public class ConfusingConditionCheckTest extends BaseCheckTestSupport
{
    /**
     * An error message for current check.
     */
    private static String errorMessage = getErrorMessage();

    @Test
    public void testDefault()
            throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(ConfusingConditionCheck.class);
        final String[] expected = {
                "10: " + errorMessage,
                "13: " + errorMessage,
                "16: " + errorMessage,
                "19: " + errorMessage,
                "22: " + errorMessage,
                "105: " + errorMessage,
                "108: " + errorMessage,
                "111: " + errorMessage,
                "149: " + errorMessage ,
                "166: " + errorMessage ,
                "177: " + errorMessage , //!!!
                "181: " + errorMessage ,
                "200: " + errorMessage ,
                "215: " + errorMessage ,
                "231: " + errorMessage ,};
        
        verify(checkConfig, getPath("InputConfusingConditionCheck.java"),
                expected);
    }

    public static String getErrorMessage()
    {
        final Properties pr = new Properties();
        try
        {
            pr.load(ConfusingConditionCheck.class
                    .getResourceAsStream("messages.properties"));
        }
        catch (IOException e)
        {
            return null;
        }
        return pr.getProperty(ConfusingConditionCheck.MSG_KEY);
    }
}
