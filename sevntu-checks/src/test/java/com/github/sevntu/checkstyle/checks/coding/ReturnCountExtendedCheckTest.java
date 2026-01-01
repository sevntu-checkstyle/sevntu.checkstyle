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

import static com.github.sevntu.checkstyle.checks.coding.ReturnCountExtendedCheck.MSG_KEY_CTOR;
import static com.github.sevntu.checkstyle.checks.coding.ReturnCountExtendedCheck.MSG_KEY_LAMBDA;
import static com.github.sevntu.checkstyle.checks.coding.ReturnCountExtendedCheck.MSG_KEY_METHOD;

import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class ReturnCountExtendedCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/coding";
    }

    @Test
    public void testNullOnIgnoreMethodsNames() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ReturnCountExtendedCheck.class);
        checkConfig.addProperty("maxReturnCount", "99");
        checkConfig.addProperty("ignoreMethodLinesCount", "99");
        checkConfig.addProperty("minIgnoreReturnDepth", "99");
        checkConfig.addProperty("ignoreEmptyReturns", "true");
        checkConfig.addProperty("topLinesToIgnoreCount", "99");
        checkConfig.addProperty("ignoreMethodsNames", null);

        final String[] expected = {};

        verify(checkConfig, getPath("InputReturnCountExtendedCheckMethods.java"), expected);
    }

    @Test
    public void testMethodsMaxReturnLiteralsIsOne() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ReturnCountExtendedCheck.class);
        checkConfig.addProperty("maxReturnCount", "1");
        checkConfig.addProperty("ignoreMethodLinesCount", "0");
        checkConfig.addProperty("minIgnoreReturnDepth", "5");
        checkConfig.addProperty("ignoreEmptyReturns", "false");
        checkConfig.addProperty("topLinesToIgnoreCount", "0");

        final String[] expected = {
            "26:16: " + getCheckMessage(MSG_KEY_METHOD, "twoReturnsInMethod", 2, 1),
            "38:16: " + getCheckMessage(MSG_KEY_METHOD, "threeReturnsInMethod", 3, 1),
            "58:16: " + getCheckMessage(MSG_KEY_METHOD, "fourReturnsInMethod", 4, 1),
            "92:16: " + getCheckMessage(MSG_KEY_METHOD, "nm", 2, 1),
            "105:17: " + getCheckMessage(MSG_KEY_METHOD, "returnFromLiteral", 6, 1),
        };

        verify(checkConfig, getPath("InputReturnCountExtendedCheckMethods.java"), expected);
    }

    @Test
    public void testMethodsMaxReturnLiteralsIsTwo() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ReturnCountExtendedCheck.class);
        checkConfig.addProperty("maxReturnCount", "2");
        checkConfig.addProperty("ignoreMethodLinesCount", "0");
        checkConfig.addProperty("minIgnoreReturnDepth", "5");
        checkConfig.addProperty("ignoreEmptyReturns", "false");
        checkConfig.addProperty("topLinesToIgnoreCount", "0");

        final String[] expected = {
            "38:16: " + getCheckMessage(MSG_KEY_METHOD, "threeReturnsInMethod", 3, 2),
            "58:16: " + getCheckMessage(MSG_KEY_METHOD, "fourReturnsInMethod", 4, 2),
            "105:17: " + getCheckMessage(MSG_KEY_METHOD, "returnFromLiteral", 6, 2),
        };

        verify(checkConfig, getPath("InputReturnCountExtendedCheckMethods.java"), expected);
    }

    @Test
    public void testIgnoreEmptyReturns() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ReturnCountExtendedCheck.class);
        checkConfig.addProperty("maxReturnCount", "2");
        checkConfig.addProperty("ignoreMethodLinesCount", "0");
        checkConfig.addProperty("minIgnoreReturnDepth", "5");
        checkConfig.addProperty("ignoreEmptyReturns", "true");
        checkConfig.addProperty("topLinesToIgnoreCount", "0");

        final String[] expected = {
            "38:16: " + getCheckMessage(MSG_KEY_METHOD, "threeReturnsInMethod", 3, 2),
            "58:16: " + getCheckMessage(MSG_KEY_METHOD, "fourReturnsInMethod", 4, 2),
        };

        verify(checkConfig, getPath("InputReturnCountExtendedCheckMethods.java"), expected);
    }

    @Test
    public void testConstructorsMaxReturnLiteralsIsOne() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ReturnCountExtendedCheck.class);
        checkConfig.addProperty("maxReturnCount", "1");
        checkConfig.addProperty("ignoreMethodLinesCount", "0");
        checkConfig.addProperty("minIgnoreReturnDepth", "5");
        checkConfig.addProperty("ignoreEmptyReturns", "false");
        checkConfig.addProperty("topLinesToIgnoreCount", "0");

        final String[] expected = {
            "29:12: " + getCheckMessage(MSG_KEY_CTOR, "InputReturnCountExtendedCheckCtors", 2, 1),
            "42:12: " + getCheckMessage(MSG_KEY_CTOR, "InputReturnCountExtendedCheckCtors", 3, 1),
            "64:12: " + getCheckMessage(MSG_KEY_CTOR, "InputReturnCountExtendedCheckCtors", 4, 1),
        };

        verify(checkConfig, getPath("InputReturnCountExtendedCheckCtors.java"), expected);
    }

    @Test
    public void testignoreMethodLinesCount() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ReturnCountExtendedCheck.class);
        checkConfig.addProperty("maxReturnCount", "1");
        checkConfig.addProperty("ignoreMethodLinesCount", "10");
        checkConfig.addProperty("minIgnoreReturnDepth", "5");
        checkConfig.addProperty("ignoreEmptyReturns", "false");
        checkConfig.addProperty("topLinesToIgnoreCount", "0");

        final String[] expected = {
            "38:16: " + getCheckMessage(MSG_KEY_METHOD, "threeReturnsInMethod", 3, 1),
            "58:16: " + getCheckMessage(MSG_KEY_METHOD, "fourReturnsInMethod", 4, 1),
            "105:17: " + getCheckMessage(MSG_KEY_METHOD, "returnFromLiteral", 6, 1),
        };

        verify(checkConfig, getPath("InputReturnCountExtendedCheckMethods.java"), expected);
    }

    @Test
    public void testignoreMethodLinesCount2() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ReturnCountExtendedCheck.class);
        checkConfig.addProperty("maxReturnCount", "1");
        checkConfig.addProperty("ignoreMethodLinesCount", "20");
        checkConfig.addProperty("minIgnoreReturnDepth", "5");
        checkConfig.addProperty("ignoreEmptyReturns", "false");
        checkConfig.addProperty("topLinesToIgnoreCount", "0");

        final String[] expected = {
            "58:16: " + getCheckMessage(MSG_KEY_METHOD, "fourReturnsInMethod", 4, 1),
            "105:17: " + getCheckMessage(MSG_KEY_METHOD, "returnFromLiteral", 6, 1),
        };

        verify(checkConfig, getPath("InputReturnCountExtendedCheckMethods.java"), expected);
    }

    @Test
    public void testminIgnoreReturnDepth() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ReturnCountExtendedCheck.class);
        checkConfig.addProperty("maxReturnCount", "0");
        checkConfig.addProperty("ignoreMethodLinesCount", "0");
        checkConfig.addProperty("minIgnoreReturnDepth", "1");
        checkConfig.addProperty("ignoreEmptyReturns", "false");
        checkConfig.addProperty("topLinesToIgnoreCount", "0");

        final String[] expected = {
            "7:16: " + getCheckMessage(MSG_KEY_METHOD, "oneReturnInMethod", 1, 0),
            "11:16: " + getCheckMessage(MSG_KEY_METHOD, "oneReturnInMethod2", 1, 0),
            "92:16: " + getCheckMessage(MSG_KEY_METHOD, "nm", 1, 0),
            "138:17: " + getCheckMessage(MSG_KEY_METHOD, "doNothing", 1, 0),
        };

        verify(checkConfig, getPath("InputReturnCountExtendedCheckMethods.java"), expected);
    }

    @Test
    public void testIgnoreNonEmptyReturns() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ReturnCountExtendedCheck.class);
        checkConfig.addProperty("maxReturnCount", "1");
        checkConfig.addProperty("ignoreMethodLinesCount", "0");
        checkConfig.addProperty("minIgnoreReturnDepth", "5");
        checkConfig.addProperty("ignoreEmptyReturns", "false");
        checkConfig.addProperty("topLinesToIgnoreCount", "0");

        final String[] expected = {
            "29:12: " + getCheckMessage(MSG_KEY_CTOR, "InputReturnCountExtendedCheckCtors", 2, 1),
            "42:12: " + getCheckMessage(MSG_KEY_CTOR, "InputReturnCountExtendedCheckCtors", 3, 1),
            "64:12: " + getCheckMessage(MSG_KEY_CTOR, "InputReturnCountExtendedCheckCtors", 4, 1),
        };

        verify(checkConfig, getPath("InputReturnCountExtendedCheckCtors.java"), expected);
    }

    @Test
    public void testMethodsInMethods() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ReturnCountExtendedCheck.class);
        checkConfig.addProperty("maxReturnCount", "1");
        checkConfig.addProperty("ignoreMethodLinesCount", "0");
        checkConfig.addProperty("minIgnoreReturnDepth", "5");
        checkConfig.addProperty("ignoreEmptyReturns", "false");
        checkConfig.addProperty("topLinesToIgnoreCount", "0");

        final String[] expected = {
            "104:24: " + getCheckMessage(MSG_KEY_METHOD, "handleEvent", 3, 1),
        };

        verify(checkConfig, getPath("InputReturnCountExtendedCheckMethodsInMethods.java"),
                expected);
    }

    @Test
    public void testIgnoreMethodsNamesProperty() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ReturnCountExtendedCheck.class);
        checkConfig.addProperty("maxReturnCount", "1");
        checkConfig.addProperty("ignoreMethodLinesCount", "0");
        checkConfig.addProperty("minIgnoreReturnDepth", "5");
        checkConfig.addProperty("ignoreEmptyReturns", "false");
        checkConfig.addProperty("topLinesToIgnoreCount", "0");
        checkConfig.addProperty("ignoreMethodsNames", "threeReturnsInMethod, twoReturnsInMethod");

        final String[] expected = {
            "58:16: " + getCheckMessage(MSG_KEY_METHOD, "fourReturnsInMethod", 4, 1),
            "92:16: " + getCheckMessage(MSG_KEY_METHOD, "nm", 2, 1),
            "105:17: " + getCheckMessage(MSG_KEY_METHOD, "returnFromLiteral", 6, 1),
        };

        verify(checkConfig, getPath("InputReturnCountExtendedCheckMethods.java"), expected);
    }

    @Test
    public void testRegexIgnoreMethodsNamesProperty() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ReturnCountExtendedCheck.class);
        checkConfig.addProperty("maxReturnCount", "1");
        checkConfig.addProperty("ignoreMethodLinesCount", "0");
        checkConfig.addProperty("minIgnoreReturnDepth", "5");
        checkConfig.addProperty("ignoreEmptyReturns", "false");
        checkConfig.addProperty("topLinesToIgnoreCount", "0");
        checkConfig.addProperty("ignoreMethodsNames", "(?iu)(?:TwO|Four)(?-iu)ReturnsInMethod");

        final String[] expected = {
            "38:16: " + getCheckMessage(MSG_KEY_METHOD, "threeReturnsInMethod", 3, 1),
            "92:16: " + getCheckMessage(MSG_KEY_METHOD, "nm", 2, 1),
            "105:17: " + getCheckMessage(MSG_KEY_METHOD, "returnFromLiteral", 6, 1),
        };

        verify(checkConfig, getPath("InputReturnCountExtendedCheckMethods.java"), expected);
    }

    @Test
    public void testAnonymousClass() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ReturnCountExtendedCheck.class);
        checkConfig.addProperty("maxReturnCount", "1");
        checkConfig.addProperty("ignoreMethodLinesCount", "0");
        checkConfig.addProperty("minIgnoreReturnDepth", "99");
        checkConfig.addProperty("ignoreEmptyReturns", "false");
        checkConfig.addProperty("topLinesToIgnoreCount", "0");

        final String[] expected = {
            "14:16: " + getCheckMessage(MSG_KEY_METHOD, "method2", 2, 1),
            "16:24: " + getCheckMessage(MSG_KEY_METHOD, "method2", 2, 1),
        };

        verify(checkConfig, getPath("InputReturnCountExtendedCheckAnonymousClasses.java"),
                expected);
    }

    @Test
    public void testLambda() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ReturnCountExtendedCheck.class);
        checkConfig.addProperty("maxReturnCount", "1");
        checkConfig.addProperty("ignoreMethodLinesCount", "0");
        checkConfig.addProperty("minIgnoreReturnDepth", "99");
        checkConfig.addProperty("ignoreEmptyReturns", "false");
        checkConfig.addProperty("topLinesToIgnoreCount", "0");

        final String[] expected = {
            "12:55: " + getCheckMessage(MSG_KEY_LAMBDA, 2, 1),
            "24:49: " + getCheckMessage(MSG_KEY_LAMBDA, 2, 1),
            "31:42: " + getCheckMessage(MSG_KEY_LAMBDA, 3, 1),
            "38:9: " + getCheckMessage(MSG_KEY_METHOD, "methodWithTwoReturnWithLambdas", 2, 1),
            "46:57: " + getCheckMessage(MSG_KEY_LAMBDA, 2, 1),
        };

        verify(checkConfig, getPath("InputReturnCountExtendedCheckLambdas.java"), expected);
    }

}
