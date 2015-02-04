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

import static com.github.sevntu.checkstyle.checks.coding.TryWithResourcesCheck.MSG_KEY;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

/**
 * Tests for {@link TryWithResourcesCheck}.
 * @author Damian Szczepanik / damianszczepanik@github
 */
public class TryWithResourcesCheckTest extends BaseCheckTestSupport
{

    @Test
    public void testInvalidDeclarations()
            throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(TryWithResourcesCheck.class);

        final String[] expected = {
                "18: " + getCheckMessage(MSG_KEY),
                "24: " + getCheckMessage(MSG_KEY),
                "30: " + getCheckMessage(MSG_KEY),
                "35: " + getCheckMessage(MSG_KEY),
                "40: " + getCheckMessage(MSG_KEY),
                "46: " + getCheckMessage(MSG_KEY)
        };

        verify(checkConfig, getPath("InputTryWithResourcesInvalidStatements.java"), expected);
    }

    @Test
    public void testFalsePositive()
            throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(TryWithResourcesCheck.class);

        final String[] expected = {
                "14: " + getCheckMessage(MSG_KEY),
                "21: " + getCheckMessage(MSG_KEY),
                "28: " + getCheckMessage(MSG_KEY)
        };

        verify(checkConfig, getPath("InputTryWithResourcesFalsePositive.java"), expected);
    }

    @Test
    public void testValidStatements()
            throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(TryWithResourcesCheck.class);

        final String[] expected = {
                };

        verify(checkConfig, getPath("InputTryWithResourcesValidStatements.java"), expected);
    }

    @Test
    public void testPureStatements()
            throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(TryWithResourcesCheck.class);

        final String[] expected = {};

        verify(checkConfig, "src/test/resources-noncompilable/com/github/sevntu/checkstyle/checks/"
                + "coding/InputTryWithResourcesPureStatementsJDK7.java", expected);
    }

    @Test
    public void testAnnotations()
            throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(TryWithResourcesCheck.class);

        final String[] expected = {
                "21: " + getCheckMessage(MSG_KEY)
        };

        verify(checkConfig, getPath("InputTryWithResourcesAdnotations.java"), expected);

    }

    @Test
    public void testUnresolved()
            throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(TryWithResourcesCheck.class);

        final String[] expected = {};

        verify(checkConfig, getPath("InputTryWithResourcesUnresolved.java"), expected);
    }

    @Test
    public void testInvalidStatementsJDK7()
            throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(TryWithResourcesCheck.class);

        final String[] expected = {};

        verify(checkConfig, "src/test/resources-noncompilable/com/github/sevntu/checkstyle/checks/"
                + "coding/InputTryWithResourcesValidStatementsJDK7.java", expected);
    }
}
