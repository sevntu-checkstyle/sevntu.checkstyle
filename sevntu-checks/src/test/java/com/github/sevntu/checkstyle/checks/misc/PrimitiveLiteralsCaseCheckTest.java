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
package com.github.sevntu.checkstyle.checks.misc;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.github.sevntu.checkstyle.checks.misc.PrimitiveLiteralsCaseCheck.CharacterCase;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;

/**
 * Unit tests for {@link PrimitiveLiteralsCaseCheck}.
 * 
 * @author Pavel Baranchikov
 *
 */
public class PrimitiveLiteralsCaseCheckTest extends BaseCheckTestSupport
{

    private final String inputFilename;

    public PrimitiveLiteralsCaseCheckTest()
    {
        inputFilename = getPath(
                "InputPrimitiveLiteralsCaseCheckTest.java");
    }

    @Test
    public void testUpperCase()
            throws Exception
    {
        final DefaultConfiguration checkConfig =
                createCheckConfig(PrimitiveLiteralsCaseCheck.class);
        checkConfig.addAttribute("requiredCase", "upper");
        final String[] expected = {
                "26:29: "
                        + getCheckMessage(
                                PrimitiveLiteralsCaseCheck.MSG_UPPER_CASE,
                                "L"),
                "26:34: "
                        + getCheckMessage(
                                PrimitiveLiteralsCaseCheck.MSG_UPPER_CASE,
                                "F"),
                "26:39: "
                        + getCheckMessage(
                                PrimitiveLiteralsCaseCheck.MSG_UPPER_CASE,
                                "D"),
                "26:58: "
                        + getCheckMessage(
                                PrimitiveLiteralsCaseCheck.MSG_UPPER_CASE,
                                "L"),
                "26:66: "
                        + getCheckMessage(
                                PrimitiveLiteralsCaseCheck.MSG_UPPER_CASE,
                                "L"),
        };
        verify(checkConfig, inputFilename, expected);
    }

    @Test
    public void testLowerCase()
            throws Exception
    {
        final DefaultConfiguration checkConfig =
                createCheckConfig(PrimitiveLiteralsCaseCheck.class);
        checkConfig.addAttribute("requiredCase", "lower");
        final String[] expected = {
                "27:29: "
                        + getCheckMessage(
                                PrimitiveLiteralsCaseCheck.MSG_LOWER_CASE,
                                "l"),
                "27:34: "
                        + getCheckMessage(
                                PrimitiveLiteralsCaseCheck.MSG_LOWER_CASE,
                                "f"),
                "27:39: "
                        + getCheckMessage(
                                PrimitiveLiteralsCaseCheck.MSG_LOWER_CASE,
                                "d"),
                "27:58: "
                        + getCheckMessage(
                                PrimitiveLiteralsCaseCheck.MSG_LOWER_CASE,
                                "l"),
                "27:66: "
                        + getCheckMessage(
                                PrimitiveLiteralsCaseCheck.MSG_LOWER_CASE,
                                "l"),
        };
        verify(checkConfig, inputFilename, expected);
    }

    @Test
    public void testSeveralLower()
            throws Exception
    {
        final DefaultConfiguration checkConfig =
                createCheckConfig(PrimitiveLiteralsCaseCheck.class);
        checkConfig.addAttribute("requiredCase", "lower");
        checkConfig.addAttribute("tokens", "NUM_LONG, NUM_FLOAT");
        final String[] expected = {
                "27:29: "
                        + getCheckMessage(
                                PrimitiveLiteralsCaseCheck.MSG_LOWER_CASE,
                                "l"),
                "27:34: "
                        + getCheckMessage(
                                PrimitiveLiteralsCaseCheck.MSG_LOWER_CASE,
                                "f"),
                "27:58: "
                        + getCheckMessage(
                                PrimitiveLiteralsCaseCheck.MSG_LOWER_CASE,
                                "l"),
                "27:66: "
                        + getCheckMessage(
                                PrimitiveLiteralsCaseCheck.MSG_LOWER_CASE,
                                "l"),
        };
        verify(checkConfig, inputFilename, expected);
    }

    @Test
    public void testSeveralUpper()
            throws Exception
    {
        final DefaultConfiguration checkConfig =
                createCheckConfig(PrimitiveLiteralsCaseCheck.class);
        checkConfig.addAttribute("requiredCase", "upper");
        checkConfig.addAttribute("tokens", "NUM_FLOAT, NUM_DOUBLE");
        final String[] expected = {
                "26:34: "
                        + getCheckMessage(
                                PrimitiveLiteralsCaseCheck.MSG_UPPER_CASE,
                                "F"),
                "26:39: "
                        + getCheckMessage(
                                PrimitiveLiteralsCaseCheck.MSG_UPPER_CASE,
                                "D"),
        };
        verify(checkConfig, inputFilename, expected);
    }

    @Test(expected = CheckstyleException.class)
    public void testWrongTokens()
            throws Exception
    {
        final DefaultConfiguration checkConfig =
                createCheckConfig(PrimitiveLiteralsCaseCheck.class);
        checkConfig.addAttribute("requiredCase", "upper");
        checkConfig.addAttribute("tokens",
                "NUM_FLOAT, NUM_DOUBLE, LITERAL_INT");
        createChecker(checkConfig);
    }

    @Test(expected = CheckstyleException.class)
    public void testWrongOption()
            throws Exception
    {
        final DefaultConfiguration checkConfig =
                createCheckConfig(PrimitiveLiteralsCaseCheck.class);
        checkConfig.addAttribute("requiredCase", "non-existing-character-case");
        createChecker(checkConfig);
    }

    /**
     * Tests, that all of the {@link CharacterCase} values are supported by the
     * check.
     * 
     * @throws Exception
     *         on test exceptions
     */
    @Test
    public void testAllCharacterCases()
            throws Exception
    {
        for (CharacterCase cCase : CharacterCase.values()) {
            final DefaultConfiguration checkConfig =
                    createCheckConfig(PrimitiveLiteralsCaseCheck.class);
            checkConfig.addAttribute("requiredCase", cCase.toString());
            createChecker(checkConfig);
        }
    }

}
