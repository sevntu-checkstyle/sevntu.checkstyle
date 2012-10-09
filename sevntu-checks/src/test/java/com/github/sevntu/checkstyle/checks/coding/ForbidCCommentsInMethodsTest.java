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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.junit.BeforeClass;
import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.github.sevntu.checkstyle.checks.coding.ForbidCCommentsInMethods;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class ForbidCCommentsInMethodsTest extends
        BaseCheckTestSupport
{
    /**
     * An error message for current check.
     */
    private static String errorMessage;

    @Test
    public void defaultTest()
            throws Exception
    {

        final DefaultConfiguration checkConfig = 
                createCheckConfig(ForbidCCommentsInMethods.class);
        final String[] expected = {
                "10: " + errorMessage,
                "17: " + errorMessage,
                "26: " + errorMessage,
                "33: " + errorMessage,
                "45: " + errorMessage,
                "52: " + errorMessage,
                "61: " + errorMessage,
                "68: " + errorMessage,
        };
        verify(checkConfig, getPath("InputForbidCCommentsInMethods.java"),
                expected);
    }

    @Test
    public void testFileWithoutComments()
            throws Exception
    {
        final DefaultConfiguration checkConfig = 
                createCheckConfig(ForbidCCommentsInMethods.class);
        final String[] expected = {};
        verify(checkConfig, getPath("InputForbidCCommentsInMethods2.java"),
                expected);
    }

    @Test
    public void testInterface()
            throws Exception
    {
        final DefaultConfiguration checkConfig = 
                createCheckConfig(ForbidCCommentsInMethods.class);
        final String[] expected = {};
        verify(checkConfig, getPath("InputForbidCCommentsInMethods3.java"),
                expected);
    }

    /**
     * Get an error message for current check from messages.properties file;
     * @return an error message.
     * @throws IOException
     */
    @BeforeClass
    public static void getErrorMessage()
            throws IOException
    {
        InputStream stream = ForbidCCommentsInMethodsTest.class
                .getResourceAsStream("messages.properties");
        BufferedReader input = new BufferedReader(
                new InputStreamReader(stream));
        String message = "forbid.c.comments.in.the.method.body";
        String currentLine = new String();
        while ((currentLine = input.readLine()) != null)
        {
            if (currentLine.startsWith(message))
            {
                errorMessage = currentLine.substring(currentLine
                        .indexOf('=') + 1);
                break;
            }
        }
        if (input != null)
        {
            input.close();
        }
    }
}
