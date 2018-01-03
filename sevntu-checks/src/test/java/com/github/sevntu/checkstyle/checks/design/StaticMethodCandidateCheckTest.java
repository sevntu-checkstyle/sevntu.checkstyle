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

package com.github.sevntu.checkstyle.checks.design;

import static com.github.sevntu.checkstyle.checks.design.StaticMethodCandidateCheck.MSG_KEY;
import static org.junit.Assert.assertArrayEquals;

import java.io.File;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class StaticMethodCandidateCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/design";
    }

    @Test
    public void testDefault() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(StaticMethodCandidateCheck.class);
        final String[] expected = {
            "37: " + getCheckMessage(MSG_KEY, "foo11"),
            "56: " + getCheckMessage(MSG_KEY, "nestedFoo1"),
            "86: " + getCheckMessage(MSG_KEY, "doSomething"),
            "123: " + getCheckMessage(MSG_KEY, "main"),
            "176: " + getCheckMessage(MSG_KEY, "bar"),
            "184: " + getCheckMessage(MSG_KEY, "fooBar"),
            "186: " + getCheckMessage(MSG_KEY, "barFoo"),
            "188: " + getCheckMessage(MSG_KEY, "fooo"),
            "190: " + getCheckMessage(MSG_KEY, "baar"),
            "192: " + getCheckMessage(MSG_KEY, "fOo"),
            "194: " + getCheckMessage(MSG_KEY, "foO"),
        };
        verify(checkConfig, getPath("InputStaticMethodCandidateCheck.java"), expected);
    }

    @Test
    public void testSkippedMethods() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(StaticMethodCandidateCheck.class);
        checkConfig.addAttribute("skippedMethods", "foo, bar,foobar");
        final String[] expected = {};
        verify(checkConfig, getPath("InputStaticMethodCandidateCheckSkippedMethods.java"),
                expected);
    }

    @Test
    public void testGetAcceptableTokens() {
        final int[] expected = {
            TokenTypes.CLASS_DEF,
            TokenTypes.METHOD_DEF,
            TokenTypes.LITERAL_IF,
            TokenTypes.LITERAL_FOR,
            TokenTypes.LITERAL_WHILE,
            TokenTypes.LITERAL_DO,
            TokenTypes.LITERAL_CATCH,
            TokenTypes.LITERAL_TRY,
            TokenTypes.VARIABLE_DEF,
            TokenTypes.PARAMETER_DEF,
            TokenTypes.ENUM_DEF,
            TokenTypes.ENUM_CONSTANT_DEF,
            TokenTypes.EXPR,
            TokenTypes.STATIC_INIT,
            TokenTypes.INSTANCE_INIT,
            TokenTypes.LITERAL_NEW,
            TokenTypes.LITERAL_THIS,
            TokenTypes.CTOR_DEF,
            TokenTypes.TYPE,
            TokenTypes.TYPE_ARGUMENT,
            TokenTypes.TYPE_PARAMETER,
            TokenTypes.INTERFACE_DEF,
            TokenTypes.LITERAL_SUPER,
        };
        final StaticMethodCandidateCheck check = new StaticMethodCandidateCheck();
        assertArrayEquals(expected, check.getAcceptableTokens());
    }

    @Test
    public void testGetRequiredTokens() {
        final int[] expected = {
            TokenTypes.CLASS_DEF,
            TokenTypes.METHOD_DEF,
            TokenTypes.LITERAL_IF,
            TokenTypes.LITERAL_FOR,
            TokenTypes.LITERAL_WHILE,
            TokenTypes.LITERAL_DO,
            TokenTypes.LITERAL_CATCH,
            TokenTypes.LITERAL_TRY,
            TokenTypes.VARIABLE_DEF,
            TokenTypes.PARAMETER_DEF,
            TokenTypes.ENUM_DEF,
            TokenTypes.ENUM_CONSTANT_DEF,
            TokenTypes.EXPR,
            TokenTypes.STATIC_INIT,
            TokenTypes.INSTANCE_INIT,
            TokenTypes.LITERAL_NEW,
            TokenTypes.LITERAL_THIS,
            TokenTypes.CTOR_DEF,
            TokenTypes.TYPE,
            TokenTypes.TYPE_ARGUMENT,
            TokenTypes.TYPE_PARAMETER,
            TokenTypes.INTERFACE_DEF,
            TokenTypes.LITERAL_SUPER,
        };
        final StaticMethodCandidateCheck check = new StaticMethodCandidateCheck();
        assertArrayEquals(expected, check.getRequiredTokens());
    }

    @Test
    public void testLambda() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(StaticMethodCandidateCheck.class);
        final String[] expected = {
        };
        verify(checkConfig, "src/test/resources-noncompilable/com/github/sevntu/checkstyle/checks/"
                + "design/InputStaticMethodCandidateCheckLambda.java", expected);
    }

    @Test
    public void testInterface() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(StaticMethodCandidateCheck.class);
        final String[] expected = {
            "16: " + getCheckMessage(MSG_KEY, "bar"),
        };
        verify(checkConfig, new File("src/test/resources-noncompilable/com/github/"
            + "sevntu/checkstyle/checks/design/InputStaticMethodCandidateCheckInterfaceMethod.java")
                .getCanonicalPath(), expected);
    }

}
