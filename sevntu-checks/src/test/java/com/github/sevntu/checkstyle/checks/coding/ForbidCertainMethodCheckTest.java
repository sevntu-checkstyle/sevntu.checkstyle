////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2018 the original author or authors.
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

import static com.github.sevntu.checkstyle.checks.coding.ForbidCertainMethodCheck.MSG_KEY;

import org.junit.Assert;
import org.junit.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class ForbidCertainMethodCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/coding";
    }

    @Test
    public void testEmptyRules() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(ForbidCertainMethodCheck.class);
        verify(checkConfig, getPath("InputForbidCertainMethodCheck.java"));
    }

    @Test
    public void testNoArgumentCount() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(ForbidCertainMethodCheck.class);
        checkConfig.addAttribute("methodName", "exit");
        final String[] expected = {
            "22:20: " + getCheckMessage(MSG_KEY, "exit", "exit", "1", "0-"),
        };
        verify(checkConfig, getPath("InputForbidCertainMethodCheck.java"), expected);
    }

    @Test
    public void testWithEmptyArgumentCount() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(ForbidCertainMethodCheck.class);
        checkConfig.addAttribute("methodName", "exit2");
        checkConfig.addAttribute("argumentCount", "");
        try {
            verify(checkConfig, getPath("InputForbidCertainMethodCheck.java"));
            Assert.fail("Expecting CheckstyleException");
        }
        catch (CheckstyleException ex) {
            checkExceptionMessage(ex,
                "cannot initialize module com.puppycrawl.tools.checkstyle.TreeWalker - "
                    + "Cannot set property 'argumentCount' to '' in module "
                    + "com.github.sevntu.checkstyle.checks.coding.ForbidCertainMethodCheck");
        }
    }

    @Test
    public void testWithSpacesForArgumentCount() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(ForbidCertainMethodCheck.class);
        checkConfig.addAttribute("methodName", "exit2");
        checkConfig.addAttribute("argumentCount", "  ");
        try {
            verify(checkConfig, getPath("InputForbidCertainMethodCheck.java"));
            Assert.fail("Expecting CheckstyleException");
        }
        catch (CheckstyleException ex) {
            checkExceptionMessage(ex,
                "cannot initialize module com.puppycrawl.tools.checkstyle.TreeWalker - "
                    + "Cannot set property 'argumentCount' to '  ' in module "
                    + "com.github.sevntu.checkstyle.checks.coding.ForbidCertainMethodCheck");
        }
    }

    @Test
    public void testWithSpacesAndCommaForArgumentCount() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(ForbidCertainMethodCheck.class);
        checkConfig.addAttribute("methodName", "exit2");
        checkConfig.addAttribute("argumentCount", " , ");
        try {
            verify(checkConfig, getPath("InputForbidCertainMethodCheck.java"));
            Assert.fail("Expecting CheckstyleException");
        }
        catch (CheckstyleException ex) {
            checkExceptionMessage(ex,
                "cannot initialize module com.puppycrawl.tools.checkstyle.TreeWalker - "
                    + "Cannot set property 'argumentCount' to ' , ' in module "
                    + "com.github.sevntu.checkstyle.checks.coding.ForbidCertainMethodCheck");
        }
    }

    @Test
    public void testWithNullArgumentCount() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(ForbidCertainMethodCheck.class);
        checkConfig.addAttribute("methodName", "exit2");
        checkConfig.addAttribute("argumentCount", null);
        try {
            verify(checkConfig, getPath("InputForbidCertainMethodCheck.java"));
            Assert.fail("Expecting CheckstyleException");
        }
        catch (CheckstyleException ex) {
            checkExceptionMessage(ex,
                "cannot initialize module com.puppycrawl.tools.checkstyle.TreeWalker - "
                    + "Cannot set property 'argumentCount' to 'null' in module "
                    + "com.github.sevntu.checkstyle.checks.coding.ForbidCertainMethodCheck");
        }
    }

    @Test
    public void testWithRegexForMethodName() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(ForbidCertainMethodCheck.class);
        checkConfig.addAttribute("methodName", "assert(True|False)");
        checkConfig.addAttribute("argumentCount", "1");

        final String[] expected = {
            "29:26: " + getCheckMessage(MSG_KEY, "assertTrue", "assert(True|False)", 1,
                "1"),
        };
        verify(checkConfig, getPath("InputForbidCertainMethodCheck.java"), expected);
    }

    @Test
    public void testUnsupportedToken() {
        final DetailAST sync = new DetailAST();
        sync.setType(TokenTypes.LITERAL_SYNCHRONIZED);
        try {
            final ForbidCertainMethodCheck check = new ForbidCertainMethodCheck();
            check.visitToken(sync);
            Assert.fail("Expecting IllegalArgumentException");
        }
        catch (IllegalArgumentException ex) {
            Assert.assertEquals("Found unsupported token: LITERAL_SYNCHRONIZED", ex.getMessage());
        }
    }

    @Test
    public void testNullMethodNameRegex() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(ForbidCertainMethodCheck.class);
        checkConfig.addAttribute("methodName", null);
        try {
            verify(checkConfig, getPath("InputForbidCertainMethodCheck.java"));
            Assert.fail("Expecting CheckstyleException");
        }
        catch (CheckstyleException ex) {
            checkExceptionMessage(ex,
                "cannot initialize module com.puppycrawl.tools.checkstyle.TreeWalker - "
                    + "Cannot set property 'methodName' to 'null' in module "
                    + "com.github.sevntu.checkstyle.checks.coding.ForbidCertainMethodCheck");
        }
    }

    @Test
    public void testEmptyMethodNameRegex() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(ForbidCertainMethodCheck.class);
        checkConfig.addAttribute("methodName", "");
        verify(checkConfig, getPath("InputForbidCertainMethodCheck.java"));
    }

    @Test
    public void testBadMethodNameRegex() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(ForbidCertainMethodCheck.class);
        checkConfig.addAttribute("methodName", "[exit");
        try {
            verify(checkConfig, getPath("InputForbidCertainMethodCheck.java"));
            Assert.fail("Expecting CheckstyleException");
        }
        catch (CheckstyleException ex) {
            checkExceptionMessage(ex,
                "cannot initialize module com.puppycrawl.tools.checkstyle.TreeWalker - "
                    + "Cannot set property 'methodName' to '[exit' in module "
                    + "com.github.sevntu.checkstyle.checks.coding.ForbidCertainMethodCheck");
        }
    }

    @Test
    public void testBadRangeForArgumentCount() throws Exception {
        testBadRangeInner("badArgCount",
            "cannot initialize module com.puppycrawl.tools.checkstyle.TreeWalker - "
            + "Cannot set property 'argumentCount' to 'badArgCount' in module "
            + "com.github.sevntu.checkstyle.checks.coding.ForbidCertainMethodCheck");
    }

    @Test
    public void testBadRange2ForArgumentCount() throws Exception {
        testBadRangeInner("badStart-badEnd",
            "cannot initialize module com.puppycrawl.tools.checkstyle.TreeWalker - "
            + "Cannot set property 'argumentCount' to 'badStart-badEnd' in module "
            + "com.github.sevntu.checkstyle.checks.coding.ForbidCertainMethodCheck");
    }

    @Test
    public void testBadRange3ForArgumentCount() throws Exception {
        testBadRangeInner("2, badStart-badEnd",
            "cannot initialize module com.puppycrawl.tools.checkstyle.TreeWalker - "
            + "Cannot set property 'argumentCount' to '2, badStart-badEnd' in module "
            + "com.github.sevntu.checkstyle.checks.coding.ForbidCertainMethodCheck");
    }

    @Test
    public void testBadRange4ForArgumentCount() throws Exception {
        testBadRangeInner(" , 1-4 ",
            "cannot initialize module com.puppycrawl.tools.checkstyle.TreeWalker - "
            + "Cannot set property 'argumentCount' to ' , 1-4 ' in module "
            + "com.github.sevntu.checkstyle.checks.coding.ForbidCertainMethodCheck");
    }

    @Test
    public void testUnboundedRangeForArgumentCount() throws Exception {
        testBadRangeInner(" - ",
            "cannot initialize module com.puppycrawl.tools.checkstyle.TreeWalker - "
            + "Cannot set property 'argumentCount' to ' - ' in module "
            + "com.github.sevntu.checkstyle.checks.coding.ForbidCertainMethodCheck");
    }

    @Test
    public void testRangeWithBadBoundsForArgumentCount() throws Exception {
        testBadRangeInner("10-1",
            "cannot initialize module com.puppycrawl.tools.checkstyle.TreeWalker - "
            + "Cannot set property 'argumentCount' to '10-1' in module "
            + "com.github.sevntu.checkstyle.checks.coding.ForbidCertainMethodCheck");
    }

    private void testBadRangeInner(String value, String expectedMessage) throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(ForbidCertainMethodCheck.class);
        checkConfig.addAttribute("methodName", "exit");
        checkConfig.addAttribute("argumentCount", value);
        final String[] expected = {
        };
        try {
            verify(checkConfig, getPath("InputForbidCertainMethodCheck.java"), expected);
            Assert.fail("Expecting CheckstyleException");
        }
        catch (CheckstyleException ex) {
            checkExceptionMessage(ex,
                expectedMessage);
        }
    }

    @Test
    public void testRangeForArgumentCount() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(ForbidCertainMethodCheck.class);
        checkConfig.addAttribute("methodName", "assert(True|False)");
        checkConfig.addAttribute("argumentCount", "0-1");

        final String[] expected = {
            "29:26: " + getCheckMessage(MSG_KEY, "assertTrue", "assert(True|False)", 1,
                "0-1"),
        };
        verify(checkConfig, getPath("InputForbidCertainMethodCheck.java"), expected);
    }

    @Test
    public void testRangeWithSpaceForArgumentCount() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(ForbidCertainMethodCheck.class);
        checkConfig.addAttribute("methodName", "assert(True|False)");
        checkConfig.addAttribute("argumentCount", "  0   - 1 ");

        final String[] expected = {
            "29:26: " + getCheckMessage(MSG_KEY, "assertTrue", "assert(True|False)", 1,
                "  0   - 1 "),
        };
        verify(checkConfig, getPath("InputForbidCertainMethodCheck.java"), expected);
    }

    @Test
    public void testLeftOpenRangeForArgumentCount() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(ForbidCertainMethodCheck.class);
        checkConfig.addAttribute("methodName", "assert(True|False)");
        checkConfig.addAttribute("argumentCount", "-1");

        final String[] expected = {
            "29:26: " + getCheckMessage(MSG_KEY, "assertTrue", "assert(True|False)", 1,
                "-1"),
        };
        verify(checkConfig, getPath("InputForbidCertainMethodCheck.java"), expected);
    }

    @Test
    public void testRightOpenRangeForArgumentCount() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(ForbidCertainMethodCheck.class);
        checkConfig.addAttribute("methodName", "assert(True|False)");
        checkConfig.addAttribute("argumentCount", "0-");

        final String[] expected = {
            "29:26: " + getCheckMessage(MSG_KEY,
                "assertTrue", "assert(True|False)", 1, "0-"),
            "30:26: " + getCheckMessage(MSG_KEY,
                "assertTrue", "assert(True|False)", 2, "0-"),
        };
        verify(checkConfig, getPath("InputForbidCertainMethodCheck.java"), expected);
    }

    @Test
    public void testMultipleRangesForArgumentCount() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(ForbidCertainMethodCheck.class);
        checkConfig.addAttribute("methodName", "asList");
        // allow arg count 4, 8
        checkConfig.addAttribute("argumentCount", "-3, 5-7, 9-");

        final String[] expected = {
            "43:26: " + getCheckMessage(MSG_KEY, "asList", "asList", 1, "-3, 5-7, 9-"),
            "44:26: " + getCheckMessage(MSG_KEY, "asList", "asList", 2, "-3, 5-7, 9-"),
            "45:26: " + getCheckMessage(MSG_KEY, "asList", "asList", 3, "-3, 5-7, 9-"),
            "47:26: " + getCheckMessage(MSG_KEY, "asList", "asList", 5, "-3, 5-7, 9-"),
            "48:26: " + getCheckMessage(MSG_KEY, "asList", "asList", 6, "-3, 5-7, 9-"),
            "49:26: " + getCheckMessage(MSG_KEY, "asList", "asList", 7, "-3, 5-7, 9-"),
            "51:26: " + getCheckMessage(MSG_KEY, "asList", "asList", 9, "-3, 5-7, 9-"),
            "52:26: " + getCheckMessage(MSG_KEY, "asList", "asList", 10, "-3, 5-7, 9-"),
            "53:26: " + getCheckMessage(MSG_KEY, "asList", "asList", 11, "-3, 5-7, 9-"),
            "54:26: " + getCheckMessage(MSG_KEY, "asList", "asList", 12, "-3, 5-7, 9-"),
        };
        verify(checkConfig, getPath("InputForbidCertainMethodCheck.java"), expected);
    }

    private static void checkExceptionMessage(Exception exception, String expectedMessage) {
        Assert.assertEquals("Exception did not have correct error message.",
                expectedMessage,
                exception.getMessage());
    }

}
