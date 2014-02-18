////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2013  Oliver Burn
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
package com.github.sevntu.checkstyle.checks.naming;

import java.io.IOException;
import java.text.MessageFormat;

import org.junit.Assert;
import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;

/**
 * Test unit for
 * {@link com.puppycrawl.tools.checkstyle.checks.naming.EnumValueName}.
 * @author Pavel Baranchikov
 */
public class EnumValueNameTest extends BaseCheckTestSupport
{
    private final String msg = getCheckMessage(EnumValueName.MSG);
    private final String inputFile;

    public EnumValueNameTest() throws IOException
    {
        inputFile = getPath("InputEnumValueNameCheck.java");
    }

    /**
     * Tests for a default naming patter - uppercase letters with underscores.
     * @throws Exception on some errors during verification.
     */
    @Test
    public void testDefault()
        throws Exception
    {
        final DefaultConfiguration checkConfig =
                createCheckConfig(EnumValueName.class);
        final String constPattern = EnumValueName.DEFAULT_CONST_PATTERN;
        final String objPattern = EnumValueName.DEFAULT_OBJ_PATTERN;
        final String[] expected =
        {
                buildMessage(34, 9, "FirstSimple", constPattern),
                buildMessage(34, 22, "SecondSimple", constPattern),
                buildMessage(34, 36, "ThirdSimple", constPattern),
                buildMessage(70, 9, "FIRST_COMPLEX", objPattern),
                buildMessage(70, 27, "SECOND_COMPLEX", objPattern),
                buildMessage(70, 46, "THIRD_COMPLEX", objPattern),
        };
        verify(checkConfig, inputFile, expected);
    }

    @Test
    public void testInvalidFormat()
        throws Exception
    {
        final DefaultConfiguration checkConfig =
                createCheckConfig(EnumValueName.class);
        checkConfig.addAttribute("format", "\\");
        final String[] expected =
        {
        };
        try {
            verify(checkConfig, inputFile, expected);
            Assert.fail();
        }
        catch (CheckstyleException ex) {
            return;
        }
    }

    /**
     * Tests for a camel naming.
     * @throws Exception on some errors during verification.
     */
    @Test
    public void testUpset()
        throws Exception
    {
        final DefaultConfiguration checkConfig =
                createCheckConfig(EnumValueName.class);
        final String constPattern = EnumValueName.DEFAULT_OBJ_PATTERN;
        final String objPattern = EnumValueName.DEFAULT_CONST_PATTERN;
        checkConfig.addAttribute("format", constPattern);
        checkConfig.addAttribute("objFormat", objPattern);

        final String[] expected =
        {
                buildMessage(42, 9, "FirstComplex", objPattern),
                buildMessage(42, 26, "SecondComplex", objPattern),
                buildMessage(42, 44, "ThirdComplex", objPattern),
                buildMessage(62, 9, "FIRST_SIMPLE", constPattern),
                buildMessage(62, 23, "SECOND_SIMPLE", constPattern),
                buildMessage(62, 38, "THIRD_SIMPLE", constPattern),
        };
        verify(checkConfig, inputFile, expected);
    }

    private String buildMessage(int lineNumber, int colNumber,
            String constName, String pattern)
    {
        return lineNumber + ":" + colNumber + ": "
                + MessageFormat.format(msg, constName, pattern);
    }

}
