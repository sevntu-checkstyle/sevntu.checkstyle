////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2020 the original author or authors.
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

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

/**
 * LoggerDeclarationsCountCheck test.
 *
 * @author <a href="mailto:mike@vorburger.ch">Michael Vorburger</a> - completed sevntu integration
 */
public class FieldSingleDeclarationCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/coding";
    }

    @Test
    public void testFieldSingleDeclarationOk() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(FieldSingleDeclarationCheck.class);

        checkConfig.addAttribute("fullyQualifiedClassName", "org.slf4j.Logger");

        verify(checkConfig, getPath("InputFieldSingleDeclarationCheckOk.java"), new String[] {});
    }

    @Test
    public void testFieldSingleDeclarationNok() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(FieldSingleDeclarationCheck.class);

        checkConfig.addAttribute("fullyQualifiedClassName", "org.slf4j.Logger");

        final String[] expected = {
            "10: " + getCheckMessage(FieldSingleDeclarationCheck.MSG_KEY, "org.slf4j.Logger"),
        };

        verify(checkConfig, getPath("InputFieldSingleDeclarationCheckNok.java"), expected);
        verify(checkConfig, getPath("InputFieldSingleDeclarationCheckOk.java"), new String[] {});
    }

    @Test
    public void testFieldSingleDeclarationFqnNok() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(FieldSingleDeclarationCheck.class);

        checkConfig.addAttribute("fullyQualifiedClassName", "org.slf4j.Logger");

        final String[] expected = {
            "7: " + getCheckMessage(FieldSingleDeclarationCheck.MSG_KEY, "org.slf4j.Logger"),
        };

        verify(checkConfig, getPath("InputFieldSingleDeclarationCheckFqnNok.java"), expected);
        verify(checkConfig, getPath("InputFieldSingleDeclarationCheckOk.java"), new String[] {});
    }

    @Test
    public void testNoClassNameConfigured() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(FieldSingleDeclarationCheck.class);

        verify(checkConfig, getPath("InputFieldSingleDeclarationCheckOk.java"), new String[] {});
        verify(checkConfig, getPath("InputFieldSingleDeclarationCheckNok.java"), new String[] {});
        verify(checkConfig, getPath("InputFieldSingleDeclarationCheckFqnNok.java"),
                new String[] {});
    }

    @Test
    public void testCustomTokens() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(FieldSingleDeclarationCheck.class);

        checkConfig.addAttribute("fullyQualifiedClassName", "org.slf4j.Logger");
        // This is required just so that getAcceptableTokens() gets coverage
        checkConfig.addAttribute("tokens", "VARIABLE_DEF");

        verify(checkConfig, getPath("InputFieldSingleDeclarationCheckOk.java"), new String[] {});
    }

    @Test
    public void testUnknownClassName() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(FieldSingleDeclarationCheck.class);

        checkConfig.addAttribute("fullyQualifiedClassName", "SomeClass");
        // This is required just so that getAcceptableTokens() gets coverage
        checkConfig.addAttribute("tokens", "VARIABLE_DEF");

        verify(checkConfig, getPath("InputFieldSingleDeclarationCheckOk.java"), new String[] {});
        verify(checkConfig, getPath("InputFieldSingleDeclarationCheckNok.java"), new String[] {});
        verify(checkConfig, getPath("InputFieldSingleDeclarationCheckFqnNok.java"),
            new String[] {});
    }

}
