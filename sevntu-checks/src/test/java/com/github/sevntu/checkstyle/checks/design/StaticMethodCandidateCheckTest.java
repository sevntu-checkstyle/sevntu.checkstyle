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

package com.github.sevntu.checkstyle.checks.design;

import static com.github.sevntu.checkstyle.checks.design.StaticMethodCandidateCheck.MSG_KEY;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import org.junit.jupiter.api.Test;

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
            "44:5: " + getCheckMessage(MSG_KEY, "foo11"),
            "63:9: " + getCheckMessage(MSG_KEY, "nestedFoo1"),
            "97:9: " + getCheckMessage(MSG_KEY, "doSomething"),
            "134:5: " + getCheckMessage(MSG_KEY, "main"),
            "191:5: " + getCheckMessage(MSG_KEY, "bar"),
            "202:5: " + getCheckMessage(MSG_KEY, "fooBar"),
            "204:5: " + getCheckMessage(MSG_KEY, "barFoo"),
            "206:5: " + getCheckMessage(MSG_KEY, "fooo"),
            "208:5: " + getCheckMessage(MSG_KEY, "baar"),
            "210:5: " + getCheckMessage(MSG_KEY, "fOo"),
            "212:5: " + getCheckMessage(MSG_KEY, "foO"),
        };
        verify(checkConfig, getPath("InputStaticMethodCandidateCheck.java"), expected);
    }

    @Test
    public void testSkippedMethods() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(StaticMethodCandidateCheck.class);
        checkConfig.addProperty("skippedMethods", "foo, bar,foobar");
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
        assertArrayEquals(expected, check.getAcceptableTokens(), "invalid tokens");
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
        assertArrayEquals(expected, check.getRequiredTokens(), "invalid tokens");
    }

    @Test
    public void testLambda() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(StaticMethodCandidateCheck.class);
        final String[] expected = {
        };
        verify(checkConfig, getNonCompilablePath("InputStaticMethodCandidateCheckLambda.java"),
                expected);
    }

    @Test
    public void testInterface() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(StaticMethodCandidateCheck.class);
        final String[] expected = {
            "16:5: " + getCheckMessage(MSG_KEY, "bar"),
        };
        verify(checkConfig,
                getNonCompilablePath("InputStaticMethodCandidateCheckInterfaceMethod.java"),
                expected);
    }

    @Test
    public void testTypeParameter() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(StaticMethodCandidateCheck.class);
        final String[] expected = {
            "20:5: " + getCheckMessage(MSG_KEY, "f2"),
            "78:5: " + getCheckMessage(MSG_KEY, "f6"),
            "91:5: " + getCheckMessage(MSG_KEY, "f7"),
        };
        verify(checkConfig, getPath("InputStaticMethodCandidateCheckTypeParameter.java"), expected);
    }

}
