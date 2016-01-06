////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2016 the original author or authors.
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

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;

/**
 * Test unit for
 * {@link EnumValueNameCheck}.
 * @author Pavel Baranchikov
 */
public class EnumValueNameCheckTest extends BaseCheckTestSupport
{
    private final String msgObj = getCheckMessage(EnumValueNameCheck.MSG_OBJ);
    private final String msgConst = getCheckMessage(EnumValueNameCheck.MSG_CONST);
    private final String inputFile;

    public EnumValueNameCheckTest() throws IOException
    {
        inputFile = getPath("InputEnumValueNameCheck.java");
    }

    /**
     * Tests for a default naming pattern.
     * 
     * @throws Exception
     *         on some errors during verification.
     */
    @Test
    public void testDefault()
        throws Exception
    {
        final DefaultConfiguration checkConfig =
                createCheckConfig(EnumValueNameCheck.class);
        final MessageContext constContext = new MessageContext(false,
                EnumValueNameCheck.DEFAULT_CONST_PATTERN);
        final MessageContext objContext = new MessageContext(true,
                EnumValueNameCheck.DEFAULT_OBJ_PATTERN);
        final String[] expected =
        {
                buildMessage(35, 9, "FirstSimple", constContext),
                buildMessage(43, 26, "SECOND_COMPLEX", objContext),
                buildMessage(66, 19, "MoSecond", constContext),
                buildMessage(76, 9, "FO_FIRST", objContext),
        };
        verify(checkConfig, inputFile, expected);
    }

    /**
     * Tests for a default naming pattern with exclusion of "some*" member
     * names.
     * 
     * @throws Exception
     *         on some errors during verification.
     */
    @Test
    public void testExcludes()
        throws Exception
    {
        final DefaultConfiguration checkConfig =
                createCheckConfig(EnumValueNameCheck.class);
        final MessageContext constContext = new MessageContext(false,
                EnumValueNameCheck.DEFAULT_CONST_PATTERN);
        final MessageContext objContext = new MessageContext(true,
                EnumValueNameCheck.DEFAULT_OBJ_PATTERN);
        checkConfig.addAttribute("excludes", "some*");
        final String[] expected =
        {
                buildMessage(35, 9, "FirstSimple", constContext),
                buildMessage(43, 26, "SECOND_COMPLEX", objContext),
                buildMessage(66, 9, "MO_FIRST", objContext),
                buildMessage(76, 19, "FoSecond", constContext),
        };
        verify(checkConfig, inputFile, expected);
    }

    /**
     * Tests for wrong formatter string.
     * 
     * @throws Exception
     *         on some errors during verification.
     */
    @Test(expected = CheckstyleException.class)
    public void testInvalidFormat()
        throws Exception
    {
        final DefaultConfiguration checkConfig =
                createCheckConfig(EnumValueNameCheck.class);
        checkConfig.addAttribute("format", "\\");
        final String[] expected = {};
        verify(checkConfig, inputFile, expected);
    }

    /**
     * Tests for upset naming - Values Enumeration in camel notation while Class
     * Enumeration - in upper-case notation.
     * 
     * @throws Exception
     *         on some errors during verification.
     */
    @Test
    public void testUpset()
        throws Exception
    {
        final DefaultConfiguration checkConfig =
                createCheckConfig(EnumValueNameCheck.class);
        final MessageContext constContext = new MessageContext(false,
                EnumValueNameCheck.DEFAULT_OBJ_PATTERN);
        final MessageContext objContext = new MessageContext(true,
                EnumValueNameCheck.DEFAULT_CONST_PATTERN);

        checkConfig.addAttribute("constFormat", constContext.getPattern());
        checkConfig.addAttribute("objFormat", objContext.getPattern());

        final String[] expected =
        {
                buildMessage(35, 22, "SECOND_SIMPLE", constContext),
                buildMessage(43, 9, "FirstComplex", objContext),
                buildMessage(66, 9, "MO_FIRST", constContext),
                buildMessage(76, 19, "FoSecond", objContext),
        };
        verify(checkConfig, inputFile, expected);
    }

    /**
     * Tests equal values for constants and static final object references.
     * 
     * @throws Exception
     *         on some errors during verification.
     */
    @Test
    public void testEqualRegexps()
        throws Exception
    {
        final DefaultConfiguration checkConfig =
                createCheckConfig(EnumValueNameCheck.class);
        final MessageContext constContext = new MessageContext(false,
                EnumValueNameCheck.DEFAULT_CONST_PATTERN);
        final MessageContext objContext = new MessageContext(true,
                EnumValueNameCheck.DEFAULT_CONST_PATTERN);
        checkConfig.addAttribute("constFormat", constContext.getPattern());
        checkConfig.addAttribute("objFormat", objContext.getPattern());

        final String[] expected =
        {
                buildMessage(35, 9, "FirstSimple", constContext),
                buildMessage(43, 9, "FirstComplex", objContext),
                buildMessage(66, 19, "MoSecond", constContext),
                buildMessage(76, 19, "FoSecond", objContext),
        };
        verify(checkConfig, inputFile, expected);
    }

    private String buildMessage(int lineNumber, int colNumber,
            String constName, MessageContext context)
    {
        final String msg = context.isEnumObj() ? msgObj : msgConst;
        return lineNumber + ":" + colNumber + ": "
                + MessageFormat.format(msg, constName, context.getPattern());
    }

    /**
     * Class containing pattern and is-object flag.
     */
    private static class MessageContext
    {
        private final boolean enumObj;
        private final String pattern;

        private MessageContext(boolean enumIsObj, String pattern)
        {
            this.enumObj = enumIsObj;
            this.pattern = pattern;
        }

        public boolean isEnumObj()
        {
            return enumObj;
        }

        public String getPattern()
        {
            return pattern;
        }

    }

}
