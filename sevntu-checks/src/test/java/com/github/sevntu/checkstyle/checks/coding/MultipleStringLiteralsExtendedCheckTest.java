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

package com.github.sevntu.checkstyle.checks.coding;

import static com.github.sevntu.checkstyle.checks.coding.MultipleStringLiteralsExtendedCheck.MSG_KEY;

import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class MultipleStringLiteralsExtendedCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/coding";
    }

    @Test
    public void testIt() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(MultipleStringLiteralsExtendedCheck.class);
        checkConfig.addProperty("allowedDuplicates", "2");
        checkConfig.addProperty("ignoreStringsRegexp", "");
        checkConfig.addProperty("highlightAllDuplicates", "false");

        final String[] expected = {
            "5:16: " + getCheckMessage(MSG_KEY, "\"StringContents\"", 3),
            "8:17: " + getCheckMessage(MSG_KEY, "\"\"", 4),
            "10:23: " + getCheckMessage(MSG_KEY, "\", \"", 3),
        };

        verify(checkConfig, getPath("InputMultipleStringLiteralsExtendedCheck.java"), expected);
    }

    @Test
    public void testItAndShowAllWarnings()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(MultipleStringLiteralsExtendedCheck.class);
        checkConfig.addProperty("allowedDuplicates", "2");
        checkConfig.addProperty("ignoreStringsRegexp", "");
        checkConfig.addProperty("highlightAllDuplicates", "true");

        final String[] expected = {
            "5:16: " + getCheckMessage(MSG_KEY, "\"StringContents\"", 3),
            "8:17: " + getCheckMessage(MSG_KEY, "\"\"", 4),
            "8:22: " + getCheckMessage(MSG_KEY, "\"\"", 4),
            "9:17: " + getCheckMessage(MSG_KEY, "\"\"", 4),
            "9:22: " + getCheckMessage(MSG_KEY, "\"\"", 4),
            "10:23: " + getCheckMessage(MSG_KEY, "\", \"", 3),
            "10:30: " + getCheckMessage(MSG_KEY, "\", \"", 3),
            "10:37: " + getCheckMessage(MSG_KEY, "\", \"", 3),
            "13:21: " + getCheckMessage(MSG_KEY, "\"StringContents\"", 3),
            "14:28: " + getCheckMessage(MSG_KEY, "\"StringContents\"", 3),
        };

        verify(checkConfig, getPath("InputMultipleStringLiteralsExtendedCheck.java"), expected);
    }

    @Test
    public void testItIgnoreEmpty() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(MultipleStringLiteralsExtendedCheck.class);
        checkConfig.addProperty("allowedDuplicates", "2");
        checkConfig.addProperty("highlightAllDuplicates", "false");

        final String[] expected = {
            "5:16: " + getCheckMessage(MSG_KEY, "\"StringContents\"", 3),
            "10:23: " + getCheckMessage(MSG_KEY, "\", \"", 3),
        };

        verify(checkConfig, getPath("InputMultipleStringLiteralsExtendedCheck.java"), expected);
    }

    @Test
    public void testItIgnoreEmptyAndComaSpace() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(MultipleStringLiteralsExtendedCheck.class);
        checkConfig.addProperty("allowedDuplicates", "2");
        checkConfig.addProperty("ignoreStringsRegexp", "^((\"\")|(\", \"))$");
        checkConfig.addProperty("highlightAllDuplicates", "false");

        final String[] expected = {
            "5:16: " + getCheckMessage(MSG_KEY, "\"StringContents\"", 3),
        };

        verify(checkConfig, getPath("InputMultipleStringLiteralsExtendedCheck.java"), expected);
    }

    @Test
    public void testItWithoutIgnoringAnnotations() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(MultipleStringLiteralsExtendedCheck.class);
        checkConfig.addProperty("allowedDuplicates", "3");
        checkConfig.addProperty("ignoreOccurrenceContext", "");
        checkConfig.addProperty("highlightAllDuplicates", "false");

        final String[] expected = {
            "19:23: " + getCheckMessage(MSG_KEY, "\"unchecked\"", 4),
        };

        verify(checkConfig, getPath("InputMultipleStringLiteralsExtendedCheck.java"), expected);
    }

    @Test
    public void testNullProperty() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(MultipleStringLiteralsExtendedCheck.class);
        checkConfig.addProperty("ignoreStringsRegexp", null);

        final String[] expected = {
            "5:16: " + getCheckMessage(MSG_KEY, "\"StringContents\"", 3),
            "7:17: " + getCheckMessage(MSG_KEY, "\"DoubleString\"", 2),
            "8:17: " + getCheckMessage(MSG_KEY, "\"\"", 4),
            "10:23: " + getCheckMessage(MSG_KEY, "\", \"", 3),
        };

        verify(checkConfig, getPath("InputMultipleStringLiteralsExtendedCheck.java"), expected);
    }

    @Test
    public void testIgnoreOccurrenceContextProperty() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(MultipleStringLiteralsExtendedCheck.class);
        checkConfig.addProperty("ignoreOccurrenceContext", "EXPR, PLUS");

        final String[] expected = {};

        verify(checkConfig, getPath("InputMultipleStringLiteralsExtendedCheck.java"), expected);
    }

}
