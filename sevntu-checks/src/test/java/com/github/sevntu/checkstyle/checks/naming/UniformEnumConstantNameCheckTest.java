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

package com.github.sevntu.checkstyle.checks.naming;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;

/**
 * Test unit for {@link UniformEnumConstantNameCheck}.
 *
 * @author Pavel Baranchikov
 */
public class UniformEnumConstantNameCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/naming";
    }

    /**
     * Tests for a default naming pattern.
     *
     * @throws Exception
     *         on some errors during verification.
     */
    @Test
    public void testDefault()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(UniformEnumConstantNameCheck.class);
        final String[] expected = {
                buildMessage(37, 9, "SECOND_SIMPLE",
                        UniformEnumConstantNameCheck.CAMEL_PATTERN),
                buildMessage(48, 9, "SecondComplex",
                        UniformEnumConstantNameCheck.UPPERCASE_PATTERN),
                buildMessage(90, 9, "WF_First",
                        UniformEnumConstantNameCheck.DEFAULT_PATTERN),
        };
        verify(checkConfig, getPath("InputUniformEnumConstantNameCheck.java"), expected);
    }

    /**
     * Tests for a format set to a single upper-case notation.
     *
     * @throws Exception
     *         on some errors during verification.
     */
    @Test
    public void testUpperCase()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(UniformEnumConstantNameCheck.class);
        checkConfig.addProperty("formats",
                "^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$");
        final String[] expected = {
                buildMessage(35, 9, "FirstSimple",
                        "^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$"),
                buildMessage(48, 9, "SecondComplex",
                        "^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$"),
                buildMessage(82, 9, "CcFirst",
                        "^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$"),
                buildMessage(90, 9, "WF_First",
                        "^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$"),
        };
        verify(checkConfig, getPath("InputUniformEnumConstantNameCheck.java"), expected);
    }

    /**
     * Tests for a accepting all format is used as one of the formats.
     *
     * @throws Exception
     *         on some errors during verification.
     */
    @Test
    public void testAllAfterUpper()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(UniformEnumConstantNameCheck.class);
        checkConfig.addProperty("formats",
                "^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$" + ",.*");
        final String[] expected = {};
        verify(checkConfig, getPath("InputUniformEnumConstantNameCheck.java"), expected);
    }

    /**
     * Tests for wrong formatter string.
     *
     * @throws Exception
     *         on some errors during verification.
     */
    @Test
    public void testInvalidFormat()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(UniformEnumConstantNameCheck.class);
        checkConfig.addProperty("formats", "\\");
        final String[] expected = {};
        try {
            verify(checkConfig, getPath("InputUniformEnumConstantNameCheck.java"), expected);
            fail("exception expected");
        }
        catch (CheckstyleException exc) {
            final String messagePrefix = "cannot initialize module"
                    + " com.puppycrawl.tools.checkstyle.TreeWalker -"
                    + " cannot initialize module"
                    + " com.github.sevntu.checkstyle.checks.naming.UniformEnumConstantNameCheck -"
                    + " Cannot set property 'formats' to '\\'";
            Assertions.assertTrue(exc.getMessage().startsWith(messagePrefix),
                "Exception's message does not start with: " + messagePrefix);
        }
    }

    /**
     * Tests for wrong tokens specified.
     *
     * @throws Exception
     *         on some errors during verification.
     */
    @Test
    public void testWrongToken()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(UniformEnumConstantNameCheck.class);
        checkConfig.addProperty("tokens", "INTERFACE_DEF");
        final String[] expected = {};
        try {
            verify(checkConfig, getPath("InputUniformEnumConstantNameCheck.java"), expected);
            fail("exception expected");
        }
        catch (CheckstyleException exc) {
            final String messagePrefix = "cannot initialize module "
                + "com.puppycrawl.tools.checkstyle.TreeWalker - "
                + "Token \"INTERFACE_DEF\" was not found in "
                + "Acceptable tokens list in check ";
            Assertions.assertTrue(exc.getMessage().startsWith(messagePrefix),
                "Exception's message does not start with: " + messagePrefix);
        }
    }

    private String buildMessage(int lineNumber, int colNumber,
            String constName, String... pattern) {
        final String msgKey;
        final String patternsString;
        if (pattern.length == 1) {
            msgKey = UniformEnumConstantNameCheck.MSG_NOT_VALID_SINGLE;
            patternsString = pattern[0];
        }
        else {
            msgKey = UniformEnumConstantNameCheck.MSG_NOT_VALID_MULTI;
            patternsString = Arrays.asList(pattern).toString();
        }
        return lineNumber
                + ":"
                + colNumber
                + ": "
                + getCheckMessage(msgKey,
                        constName, patternsString);
    }

}
