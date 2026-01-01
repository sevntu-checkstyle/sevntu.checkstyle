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

import static com.github.sevntu.checkstyle.checks.coding.UnnecessaryParenthesesExtendedCheck.MSG_KEY_ASSIGN;
import static com.github.sevntu.checkstyle.checks.coding.UnnecessaryParenthesesExtendedCheck.MSG_KEY_EXPR;
import static com.github.sevntu.checkstyle.checks.coding.UnnecessaryParenthesesExtendedCheck.MSG_KEY_IDENT;
import static com.github.sevntu.checkstyle.checks.coding.UnnecessaryParenthesesExtendedCheck.MSG_KEY_LITERAL;
import static com.github.sevntu.checkstyle.checks.coding.UnnecessaryParenthesesExtendedCheck.MSG_KEY_RETURN;
import static com.github.sevntu.checkstyle.checks.coding.UnnecessaryParenthesesExtendedCheck.MSG_KEY_STRING;

import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

/**
 * Test fixture for the UnnecessaryParenthesesCheck.
 *
 * @author Eric K. Roe
 * @author Antonenko Dmitriy
 */
public class UnnecessaryParenthesesExtendedCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/coding";
    }

    @Test
    public void testDefault() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(UnnecessaryParenthesesExtendedCheck.class);
        checkConfig.addProperty("ignoreCalculationOfBooleanVariables", "false");
        checkConfig.addProperty("ignoreCalculationOfBooleanVariablesWithReturn", "false");
        checkConfig.addProperty("ignoreCalculationOfBooleanVariablesWithAssert", "false");
        final String[] expected = {
            "5:22: " + getCheckMessage(MSG_KEY_ASSIGN),
            "5:29: " + getCheckMessage(MSG_KEY_EXPR),
            "5:31: " + getCheckMessage(MSG_KEY_IDENT, "i"),
            "5:46: " + getCheckMessage(MSG_KEY_ASSIGN),
            "6:15: " + getCheckMessage(MSG_KEY_ASSIGN),
            "7:14: " + getCheckMessage(MSG_KEY_IDENT, "x"),
            "7:17: " + getCheckMessage(MSG_KEY_ASSIGN),
            "8:15: " + getCheckMessage(MSG_KEY_ASSIGN),
            "9:14: " + getCheckMessage(MSG_KEY_IDENT, "x"),
            "9:17: " + getCheckMessage(MSG_KEY_ASSIGN),
            "12:22: " + getCheckMessage(MSG_KEY_ASSIGN),
            "12:30: " + getCheckMessage(MSG_KEY_IDENT, "i"),
            "12:46: " + getCheckMessage(MSG_KEY_ASSIGN),
            "16:17: " + getCheckMessage(MSG_KEY_LITERAL, "0"),
            "26:11: " + getCheckMessage(MSG_KEY_ASSIGN),
            "30:11: " + getCheckMessage(MSG_KEY_ASSIGN),
            "32:11: " + getCheckMessage(MSG_KEY_ASSIGN),
            "34:11: " + getCheckMessage(MSG_KEY_ASSIGN),
            "35:16: " + getCheckMessage(MSG_KEY_IDENT, "a"),
            "36:14: " + getCheckMessage(MSG_KEY_IDENT, "a"),
            "36:20: " + getCheckMessage(MSG_KEY_IDENT, "b"),
            "36:26: " + getCheckMessage(MSG_KEY_LITERAL, "600"),
            "36:40: " + getCheckMessage(MSG_KEY_LITERAL, "12.5f"),
            "36:56: " + getCheckMessage(MSG_KEY_IDENT, "arg2"),
            "37:14: " + getCheckMessage(MSG_KEY_STRING, "\"this\""),
            "37:25: " + getCheckMessage(MSG_KEY_STRING, "\"that\""),
            "38:11: " + getCheckMessage(MSG_KEY_ASSIGN),
            "38:14: " + getCheckMessage(MSG_KEY_STRING, "\"this is a really, really...\""),
            "40:16: " + getCheckMessage(MSG_KEY_RETURN),
            "44:21: " + getCheckMessage(MSG_KEY_LITERAL, "1"),
            "44:26: " + getCheckMessage(MSG_KEY_LITERAL, "13.5"),
            "45:22: " + getCheckMessage(MSG_KEY_LITERAL, "true"),
            "46:17: " + getCheckMessage(MSG_KEY_IDENT, "b"),
            "50:17: " + getCheckMessage(MSG_KEY_ASSIGN),
            "52:11: " + getCheckMessage(MSG_KEY_ASSIGN),
            "54:16: " + getCheckMessage(MSG_KEY_RETURN),
            "64:13: " + getCheckMessage(MSG_KEY_EXPR),
            "68:16: " + getCheckMessage(MSG_KEY_EXPR),
            "73:19: " + getCheckMessage(MSG_KEY_EXPR),
            "74:23: " + getCheckMessage(MSG_KEY_LITERAL, "4000"),
            "79:19: " + getCheckMessage(MSG_KEY_ASSIGN),
            "81:11: " + getCheckMessage(MSG_KEY_ASSIGN),
            "81:16: " + getCheckMessage(MSG_KEY_LITERAL, "3"),
            "82:27: " + getCheckMessage(MSG_KEY_ASSIGN),
            "86:16: " + getCheckMessage(MSG_KEY_EXPR),
        };

        verify(checkConfig, getPath("InputUnnecessaryParenthesesExtendedCheck.java"), expected);
    }

    @Test
    public void test15Extensions() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(UnnecessaryParenthesesExtendedCheck.class);
        final String[] expected = {};
        checkConfig.addProperty("ignoreCalculationOfBooleanVariables", "false");
        checkConfig.addProperty("ignoreCalculationOfBooleanVariablesWithReturn", "false");
        checkConfig.addProperty("ignoreCalculationOfBooleanVariablesWithAssert", "false");
        verify(checkConfig, getPath("InputUnnecessaryParenthesesExtendedCheck2.java"), expected);
    }

    @Test
    public void testUbv() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(UnnecessaryParenthesesExtendedCheck.class);
        final String[] expected = {
            "5:22: " + getCheckMessage(MSG_KEY_ASSIGN),
            "5:29: " + getCheckMessage(MSG_KEY_EXPR),
            "5:31: " + getCheckMessage(MSG_KEY_IDENT, "i"),
            "5:46: " + getCheckMessage(MSG_KEY_ASSIGN),
            "6:15: " + getCheckMessage(MSG_KEY_ASSIGN),
            "7:14: " + getCheckMessage(MSG_KEY_IDENT, "x"),
            "7:17: " + getCheckMessage(MSG_KEY_ASSIGN),
            "8:15: " + getCheckMessage(MSG_KEY_ASSIGN),
            "9:14: " + getCheckMessage(MSG_KEY_IDENT, "x"),
            "9:17: " + getCheckMessage(MSG_KEY_ASSIGN),
            "12:22: " + getCheckMessage(MSG_KEY_ASSIGN),
            "12:30: " + getCheckMessage(MSG_KEY_IDENT, "i"),
            "12:46: " + getCheckMessage(MSG_KEY_ASSIGN),
            "16:17: " + getCheckMessage(MSG_KEY_LITERAL, "0"),
            "26:11: " + getCheckMessage(MSG_KEY_ASSIGN),
            "30:11: " + getCheckMessage(MSG_KEY_ASSIGN),
            "32:11: " + getCheckMessage(MSG_KEY_ASSIGN),
            "34:11: " + getCheckMessage(MSG_KEY_ASSIGN),
            "35:16: " + getCheckMessage(MSG_KEY_IDENT, "a"),
            "36:14: " + getCheckMessage(MSG_KEY_IDENT, "a"),
            "36:20: " + getCheckMessage(MSG_KEY_IDENT, "b"),
            "36:26: " + getCheckMessage(MSG_KEY_LITERAL, "600"),
            "36:40: " + getCheckMessage(MSG_KEY_LITERAL, "12.5f"),
            "36:56: " + getCheckMessage(MSG_KEY_IDENT, "arg2"),
            "37:14: " + getCheckMessage(MSG_KEY_STRING, "\"this\""),
            "37:25: " + getCheckMessage(MSG_KEY_STRING, "\"that\""),
            "38:11: " + getCheckMessage(MSG_KEY_ASSIGN),
            "38:14: " + getCheckMessage(MSG_KEY_STRING, "\"this is a really, really...\""),
            "40:16: " + getCheckMessage(MSG_KEY_RETURN),
            "44:21: " + getCheckMessage(MSG_KEY_LITERAL, "1"),
            "44:26: " + getCheckMessage(MSG_KEY_LITERAL, "13.5"),
            "45:22: " + getCheckMessage(MSG_KEY_LITERAL, "true"),
            "46:17: " + getCheckMessage(MSG_KEY_IDENT, "b"),
            "50:17: " + getCheckMessage(MSG_KEY_ASSIGN),
            "52:11: " + getCheckMessage(MSG_KEY_ASSIGN),
            "54:16: " + getCheckMessage(MSG_KEY_RETURN),
            "64:13: " + getCheckMessage(MSG_KEY_EXPR),
            "68:16: " + getCheckMessage(MSG_KEY_EXPR),
            "73:19: " + getCheckMessage(MSG_KEY_EXPR),
            "74:23: " + getCheckMessage(MSG_KEY_LITERAL, "4000"),
            "79:19: " + getCheckMessage(MSG_KEY_ASSIGN),
            "81:11: " + getCheckMessage(MSG_KEY_ASSIGN),
            "81:16: " + getCheckMessage(MSG_KEY_LITERAL, "3"),
            "82:27: " + getCheckMessage(MSG_KEY_ASSIGN),
            "96:19: " + getCheckMessage(MSG_KEY_ASSIGN),
            "100:24: " + getCheckMessage(MSG_KEY_EXPR),
            "123:27: " + getCheckMessage(MSG_KEY_IDENT, "i"),
        };
        checkConfig.addProperty("ignoreCalculationOfBooleanVariables", "true");
        checkConfig.addProperty("ignoreCalculationOfBooleanVariablesWithReturn", "true");
        checkConfig.addProperty("ignoreCalculationOfBooleanVariablesWithAssert", "true");
        verify(checkConfig, getPath("InputUnnecessaryParenthesesExtendedCheckUbv.java"), expected);
    }

}
