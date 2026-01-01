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

import static com.github.sevntu.checkstyle.checks.coding.AvoidModifiersForTypesCheck.MSG_KEY;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.DetailAstImpl;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class AvoidModifiersForTypesCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/coding";
    }

    @Test
    public void testFinal() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(AvoidModifiersForTypesCheck.class);
        checkConfig.addProperty("forbiddenClassesRegexpAnnotation", null);
        checkConfig.addProperty("forbiddenClassesRegexpFinal",
                "InputAvoidModifiersForTypesCheck");
        checkConfig.addProperty("forbiddenClassesRegexpStatic", null);
        checkConfig.addProperty("forbiddenClassesRegexpTransient", null);
        checkConfig.addProperty("forbiddenClassesRegexpVolatile", null);
        checkConfig.addProperty("forbiddenClassesRegexpPublic", null);
        checkConfig.addProperty("forbiddenClassesRegexpPackagePrivate", null);
        checkConfig.addProperty("forbiddenClassesRegexpProtected", null);
        checkConfig.addProperty("forbiddenClassesRegexpPrivate", null);

        final String[] expected = {
            "19:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "final"),
            "20:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "final"),
            "25:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "final"),
            "30:9: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "final"),
        };

        verify(checkConfig, getPath("InputAvoidModifiersForTypesCheck.java"), expected);
    }

    @Test
    public void testPackagePrivate() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(AvoidModifiersForTypesCheck.class);
        checkConfig.addProperty("forbiddenClassesRegexpAnnotation", null);
        checkConfig.addProperty("forbiddenClassesRegexpFinal", null);
        checkConfig.addProperty("forbiddenClassesRegexpStatic", null);
        checkConfig.addProperty("forbiddenClassesRegexpTransient", null);
        checkConfig.addProperty("forbiddenClassesRegexpVolatile", null);
        checkConfig.addProperty("forbiddenClassesRegexpPrivate", null);
        checkConfig.addProperty("forbiddenClassesRegexpPackagePrivate",
                "InputAvoidModifiersForTypesCheck");
        checkConfig.addProperty("forbiddenClassesRegexpProtected", null);
        checkConfig.addProperty("forbiddenClassesRegexpPublic", null);

        final String[] expected = {
            "24:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck",
                    "package-private"),
            "25:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck",
                    "package-private"),
        };

        verify(checkConfig, getPath("InputAvoidModifiersForTypesCheck.java"), expected);
    }

    @Test
    public void testAnnotationPrivateStatic() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(AvoidModifiersForTypesCheck.class);
        checkConfig.addProperty("forbiddenClassesRegexpAnnotation", "File");
        checkConfig.addProperty("forbiddenClassesRegexpFinal", null);
        checkConfig.addProperty("forbiddenClassesRegexpStatic", "File");
        checkConfig.addProperty("forbiddenClassesRegexpTransient", null);
        checkConfig.addProperty("forbiddenClassesRegexpVolatile", null);
        checkConfig.addProperty("forbiddenClassesRegexpPrivate", "File");
        checkConfig.addProperty("forbiddenClassesRegexpPackagePrivate", null);
        checkConfig.addProperty("forbiddenClassesRegexpProtected", null);
        checkConfig.addProperty("forbiddenClassesRegexpPublic", null);

        final String[] expected = {
            "18:5: " + getCheckMessage(MSG_KEY, "File", "annotation"),
            "18:5: " + getCheckMessage(MSG_KEY, "File", "private"),
            "18:5: " + getCheckMessage(MSG_KEY, "File", "static"),
        };

        verify(checkConfig, getPath("InputAvoidModifiersForTypesCheck.java"), expected);
    }

    @Test
    public void testProtectedAndTransient() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(AvoidModifiersForTypesCheck.class);
        checkConfig.addProperty("forbiddenClassesRegexpAnnotation", null);
        checkConfig.addProperty("forbiddenClassesRegexpFinal", null);
        checkConfig.addProperty("forbiddenClassesRegexpStatic", null);
        checkConfig.addProperty("forbiddenClassesRegexpTransient",
                "InputAvoidModifiersForTypesCheck");
        checkConfig.addProperty("forbiddenClassesRegexpVolatile", null);
        checkConfig.addProperty("forbiddenClassesRegexpPrivate", null);
        checkConfig.addProperty("forbiddenClassesRegexpPackagePrivate", null);
        checkConfig.addProperty("forbiddenClassesRegexpProtected",
                "InputAvoidModifiersForTypesCheck");
        checkConfig.addProperty("forbiddenClassesRegexpPublic", null);

        final String[] expected = {
            "22:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "protected"),
            "22:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "transient"),
        };

        verify(checkConfig, getPath("InputAvoidModifiersForTypesCheck.java"), expected);
    }

    @Test
    public void testPublicAndVolatile() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(AvoidModifiersForTypesCheck.class);
        checkConfig.addProperty("forbiddenClassesRegexpAnnotation", null);
        checkConfig.addProperty("forbiddenClassesRegexpFinal", null);
        checkConfig.addProperty("forbiddenClassesRegexpStatic", null);
        checkConfig.addProperty("forbiddenClassesRegexpTransient", null);
        checkConfig.addProperty("forbiddenClassesRegexpVolatile",
                "InputAvoidModifiersForTypesCheck");
        checkConfig.addProperty("forbiddenClassesRegexpPrivate", null);
        checkConfig.addProperty("forbiddenClassesRegexpPackagePrivate", null);
        checkConfig.addProperty("forbiddenClassesRegexpProtected", null);
        checkConfig.addProperty("forbiddenClassesRegexpPublic",
                "InputAvoidModifiersForTypesCheck");

        final String[] expected = {
            "23:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "public"),
            "23:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "volatile"),
        };

        verify(checkConfig, getPath("InputAvoidModifiersForTypesCheck.java"), expected);
    }

    @Test
    public void testFinalAndPrivatePackage() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(AvoidModifiersForTypesCheck.class);
        checkConfig.addProperty("forbiddenClassesRegexpAnnotation", null);
        checkConfig.addProperty("forbiddenClassesRegexpFinal",
                "InputAvoidModifiersForTypesCheck");
        checkConfig.addProperty("forbiddenClassesRegexpStatic", null);
        checkConfig.addProperty("forbiddenClassesRegexpTransient", null);
        checkConfig.addProperty("forbiddenClassesRegexpVolatile", null);
        checkConfig.addProperty("forbiddenClassesRegexpPrivate", null);
        checkConfig.addProperty("forbiddenClassesRegexpPackagePrivate",
                "InputAvoidModifiersForTypesCheck");
        checkConfig.addProperty("forbiddenClassesRegexpProtected", null);
        checkConfig.addProperty("forbiddenClassesRegexpPublic", null);

        final String[] expected = {
            "19:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "final"),
            "20:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "final"),
            "24:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck",
                    "package-private"),
            "25:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck",
                    "package-private"),
            "25:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "final"),
            "30:9: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "final"),
        };

        verify(checkConfig, getPath("InputAvoidModifiersForTypesCheck.java"), expected);
    }

    @Test
    public void testFinalAndStatic() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(AvoidModifiersForTypesCheck.class);
        checkConfig.addProperty("forbiddenClassesRegexpAnnotation", null);
        checkConfig.addProperty("forbiddenClassesRegexpFinal",
                "InputAvoidModifiersForTypesCheck");
        checkConfig.addProperty("forbiddenClassesRegexpStatic",
                "InputAvoidModifiersForTypesCheck");
        checkConfig.addProperty("forbiddenClassesRegexpTransient", null);
        checkConfig.addProperty("forbiddenClassesRegexpVolatile", null);
        checkConfig.addProperty("forbiddenClassesRegexpPrivate", null);
        checkConfig.addProperty("forbiddenClassesRegexpPackagePrivate", null);
        checkConfig.addProperty("forbiddenClassesRegexpProtected", null);
        checkConfig.addProperty("forbiddenClassesRegexpPublic", null);

        final String[] expected = {
            "19:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "final"),
            "20:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "final"),
            "20:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "static"),
            "25:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "final"),
            "30:9: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "final"),
        };

        verify(checkConfig, getPath("InputAvoidModifiersForTypesCheck.java"), expected);
    }

    @Test
    public void testProtected() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(AvoidModifiersForTypesCheck.class);
        checkConfig.addProperty("forbiddenClassesRegexpAnnotation", null);
        checkConfig.addProperty("forbiddenClassesRegexpFinal", null);
        checkConfig.addProperty("forbiddenClassesRegexpStatic", null);
        checkConfig.addProperty("forbiddenClassesRegexpTransient", null);
        checkConfig.addProperty("forbiddenClassesRegexpVolatile", null);
        checkConfig.addProperty("forbiddenClassesRegexpPrivate", null);
        checkConfig.addProperty("forbiddenClassesRegexpPackagePrivate", null);
        checkConfig.addProperty("forbiddenClassesRegexpProtected",
                "InputAvoidModifiersForTypesCheck");
        checkConfig.addProperty("forbiddenClassesRegexpPublic", null);

        final String[] expected = {
            "22:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "protected"),
        };

        verify(checkConfig, getPath("InputAvoidModifiersForTypesCheck.java"), expected);
    }

    @Test
    public void testLogger() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(AvoidModifiersForTypesCheck.class);
        checkConfig.addProperty("forbiddenClassesRegexpAnnotation", null);
        checkConfig.addProperty("forbiddenClassesRegexpFinal", null);
        checkConfig.addProperty("forbiddenClassesRegexpStatic", null);
        checkConfig.addProperty("forbiddenClassesRegexpTransient", null);
        checkConfig.addProperty("forbiddenClassesRegexpVolatile", null);
        checkConfig.addProperty("forbiddenClassesRegexpPrivate", null);
        checkConfig.addProperty("forbiddenClassesRegexpPackagePrivate", "Logger");
        checkConfig.addProperty("forbiddenClassesRegexpProtected", "Logger");
        checkConfig.addProperty("forbiddenClassesRegexpPublic", "Logger");

        final String[] expected = {
            "37:9: " + getCheckMessage(MSG_KEY, "Logger", "protected"),
            "38:9: " + getCheckMessage(MSG_KEY, "Logger", "public"),
            "39:9: " + getCheckMessage(MSG_KEY, "Logger", "package-private"),
        };

        verify(checkConfig, getPath("InputAvoidModifiersForTypesCheck.java"), expected);
    }

    @Test
    public void testAnyFile() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(AvoidModifiersForTypesCheck.class);
        checkConfig.addProperty("forbiddenClassesRegexpAnnotation", null);
        checkConfig.addProperty("forbiddenClassesRegexpFinal", null);
        checkConfig.addProperty("forbiddenClassesRegexpStatic", null);
        checkConfig.addProperty("forbiddenClassesRegexpTransient", null);
        checkConfig.addProperty("forbiddenClassesRegexpVolatile", null);
        checkConfig.addProperty("forbiddenClassesRegexpPrivate", null);
        checkConfig.addProperty("forbiddenClassesRegexpPackagePrivate", null);
        checkConfig.addProperty("forbiddenClassesRegexpProtected", null);
        checkConfig.addProperty("forbiddenClassesRegexpPublic", null);

        final String[] expected = {};

        verify(checkConfig, getPath("InputAvoidModifiersForTypesCheck2.java"), expected);
    }

    /**
     * This test is needed in order to test the Invalid Token case which is not reproducible with
     * compilable sources and correctly worked parser.
     */
    @Test
    public void testUnsupportedModifier() {
        final DetailAstImpl sync = new DetailAstImpl();
        sync.setType(TokenTypes.LITERAL_SYNCHRONIZED);

        final DetailAstImpl ident = new DetailAstImpl();
        ident.setType(TokenTypes.IDENT);
        ident.setText("dummy");

        final DetailAstImpl type = new DetailAstImpl();
        type.setType(TokenTypes.TYPE);
        type.addChild(ident);

        final DetailAstImpl child = new DetailAstImpl();
        child.setType(TokenTypes.MODIFIERS);
        child.addChild(sync);

        final DetailAstImpl node = new DetailAstImpl();
        node.addChild(type);
        node.addChild(child);

        final DetailAstImpl parent = new DetailAstImpl();
        parent.setType(TokenTypes.OBJBLOCK);
        parent.addChild(node);

        final AvoidModifiersForTypesCheck check = new AvoidModifiersForTypesCheck();
        try {
            check.visitToken(node);
            fail("exception expected");
        }
        catch (IllegalArgumentException exc) {
            Assertions.assertEquals("Found unsupported token: LITERAL_SYNCHRONIZED",
                exc.getMessage());
        }
    }

}
