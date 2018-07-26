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

import static com.github.sevntu.checkstyle.checks.coding.AvoidModifiersForTypesCheck.MSG_KEY;
import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 * @author <a href="mailto:yasser.aziza@gmail.com">Yasser Aziza</a>
 * </p>
 */
public class AvoidModifiersForTypesCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/coding";
    }

    @Test
    public void testFinal() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(AvoidModifiersForTypesCheck.class);
        checkConfig.addAttribute("forbiddenClassesRegexpAnnotation", null);
        checkConfig.addAttribute("forbiddenClassesRegexpFinal",
                "InputAvoidModifiersForTypesCheck");
        checkConfig.addAttribute("forbiddenClassesRegexpStatic", null);
        checkConfig.addAttribute("forbiddenClassesRegexpTransient", null);
        checkConfig.addAttribute("forbiddenClassesRegexpVolatile", null);
        checkConfig.addAttribute("forbiddenClassesRegexpPublic", null);
        checkConfig.addAttribute("forbiddenClassesRegexpPackagePrivate", null);
        checkConfig.addAttribute("forbiddenClassesRegexpProtected", null);
        checkConfig.addAttribute("forbiddenClassesRegexpPrivate", null);

        final String[] expected = {
            "19:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "final"),
            "20:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "final"),
            "24:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "final"),
            "29:9: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "final"),
        };

        verify(checkConfig, getPath("InputAvoidModifiersForTypesCheck.java"), expected);
    }

    @Test
    public void testPackagePrivate() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(AvoidModifiersForTypesCheck.class);
        checkConfig.addAttribute("forbiddenClassesRegexpAnnotation", null);
        checkConfig.addAttribute("forbiddenClassesRegexpFinal", null);
        checkConfig.addAttribute("forbiddenClassesRegexpStatic", null);
        checkConfig.addAttribute("forbiddenClassesRegexpTransient", null);
        checkConfig.addAttribute("forbiddenClassesRegexpVolatile", null);
        checkConfig.addAttribute("forbiddenClassesRegexpPrivate", null);
        checkConfig.addAttribute("forbiddenClassesRegexpPackagePrivate",
                "InputAvoidModifiersForTypesCheck");
        checkConfig.addAttribute("forbiddenClassesRegexpProtected", null);
        checkConfig.addAttribute("forbiddenClassesRegexpPublic", null);

        final String[] expected = {
            "23:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck",
                    "package-private"),
            "24:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck",
                    "package-private"),
        };

        verify(checkConfig, getPath("InputAvoidModifiersForTypesCheck.java"), expected);
    }

    @Test
    public void testAnnotationPrivateStatic() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(AvoidModifiersForTypesCheck.class);
        checkConfig.addAttribute("forbiddenClassesRegexpAnnotation", "File");
        checkConfig.addAttribute("forbiddenClassesRegexpFinal", null);
        checkConfig.addAttribute("forbiddenClassesRegexpStatic", "File");
        checkConfig.addAttribute("forbiddenClassesRegexpTransient", null);
        checkConfig.addAttribute("forbiddenClassesRegexpVolatile", null);
        checkConfig.addAttribute("forbiddenClassesRegexpPrivate", "File");
        checkConfig.addAttribute("forbiddenClassesRegexpPackagePrivate", null);
        checkConfig.addAttribute("forbiddenClassesRegexpProtected", null);
        checkConfig.addAttribute("forbiddenClassesRegexpPublic", null);

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
        checkConfig.addAttribute("forbiddenClassesRegexpAnnotation", null);
        checkConfig.addAttribute("forbiddenClassesRegexpFinal", null);
        checkConfig.addAttribute("forbiddenClassesRegexpStatic", null);
        checkConfig.addAttribute("forbiddenClassesRegexpTransient",
                "InputAvoidModifiersForTypesCheck");
        checkConfig.addAttribute("forbiddenClassesRegexpVolatile", null);
        checkConfig.addAttribute("forbiddenClassesRegexpPrivate", null);
        checkConfig.addAttribute("forbiddenClassesRegexpPackagePrivate", null);
        checkConfig.addAttribute("forbiddenClassesRegexpProtected",
                "InputAvoidModifiersForTypesCheck");
        checkConfig.addAttribute("forbiddenClassesRegexpPublic", null);

        final String[] expected = {
            "21:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "protected"),
            "21:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "transient"),
        };

        verify(checkConfig, getPath("InputAvoidModifiersForTypesCheck.java"), expected);
    }

    @Test
    public void testPublicAndVolatile() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(AvoidModifiersForTypesCheck.class);
        checkConfig.addAttribute("forbiddenClassesRegexpAnnotation", null);
        checkConfig.addAttribute("forbiddenClassesRegexpFinal", null);
        checkConfig.addAttribute("forbiddenClassesRegexpStatic", null);
        checkConfig.addAttribute("forbiddenClassesRegexpTransient", null);
        checkConfig.addAttribute("forbiddenClassesRegexpVolatile",
                "InputAvoidModifiersForTypesCheck");
        checkConfig.addAttribute("forbiddenClassesRegexpPrivate", null);
        checkConfig.addAttribute("forbiddenClassesRegexpPackagePrivate", null);
        checkConfig.addAttribute("forbiddenClassesRegexpProtected", null);
        checkConfig.addAttribute("forbiddenClassesRegexpPublic",
                "InputAvoidModifiersForTypesCheck");

        final String[] expected = {
            "22:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "public"),
            "22:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "volatile"),
        };

        verify(checkConfig, getPath("InputAvoidModifiersForTypesCheck.java"), expected);
    }

    @Test
    public void testFinalAndPrivatePackage() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(AvoidModifiersForTypesCheck.class);
        checkConfig.addAttribute("forbiddenClassesRegexpAnnotation", null);
        checkConfig.addAttribute("forbiddenClassesRegexpFinal",
                "InputAvoidModifiersForTypesCheck");
        checkConfig.addAttribute("forbiddenClassesRegexpStatic", null);
        checkConfig.addAttribute("forbiddenClassesRegexpTransient", null);
        checkConfig.addAttribute("forbiddenClassesRegexpVolatile", null);
        checkConfig.addAttribute("forbiddenClassesRegexpPrivate", null);
        checkConfig.addAttribute("forbiddenClassesRegexpPackagePrivate",
                "InputAvoidModifiersForTypesCheck");
        checkConfig.addAttribute("forbiddenClassesRegexpProtected", null);
        checkConfig.addAttribute("forbiddenClassesRegexpPublic", null);

        final String[] expected = {
            "19:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "final"),
            "20:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "final"),
            "23:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck",
                    "package-private"),
            "24:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "final"),
            "24:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck",
                    "package-private"),
            "29:9: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "final"),
        };

        verify(checkConfig, getPath("InputAvoidModifiersForTypesCheck.java"), expected);
    }

    @Test
    public void testFinalAndStatic() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(AvoidModifiersForTypesCheck.class);
        checkConfig.addAttribute("forbiddenClassesRegexpAnnotation", null);
        checkConfig.addAttribute("forbiddenClassesRegexpFinal",
                "InputAvoidModifiersForTypesCheck");
        checkConfig.addAttribute("forbiddenClassesRegexpStatic",
                "InputAvoidModifiersForTypesCheck");
        checkConfig.addAttribute("forbiddenClassesRegexpTransient", null);
        checkConfig.addAttribute("forbiddenClassesRegexpVolatile", null);
        checkConfig.addAttribute("forbiddenClassesRegexpPrivate", null);
        checkConfig.addAttribute("forbiddenClassesRegexpPackagePrivate", null);
        checkConfig.addAttribute("forbiddenClassesRegexpProtected", null);
        checkConfig.addAttribute("forbiddenClassesRegexpPublic", null);

        final String[] expected = {
            "19:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "final"),
            "20:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "final"),
            "20:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "static"),
            "24:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "final"),
            "29:9: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "final"),
        };

        verify(checkConfig, getPath("InputAvoidModifiersForTypesCheck.java"), expected);
    }

    @Test
    public void testProtected() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(AvoidModifiersForTypesCheck.class);
        checkConfig.addAttribute("forbiddenClassesRegexpAnnotation", null);
        checkConfig.addAttribute("forbiddenClassesRegexpFinal", null);
        checkConfig.addAttribute("forbiddenClassesRegexpStatic", null);
        checkConfig.addAttribute("forbiddenClassesRegexpTransient", null);
        checkConfig.addAttribute("forbiddenClassesRegexpVolatile", null);
        checkConfig.addAttribute("forbiddenClassesRegexpPrivate", null);
        checkConfig.addAttribute("forbiddenClassesRegexpPackagePrivate", null);
        checkConfig.addAttribute("forbiddenClassesRegexpProtected",
                "InputAvoidModifiersForTypesCheck");
        checkConfig.addAttribute("forbiddenClassesRegexpPublic", null);

        final String[] expected = {
            "21:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "protected"),
        };

        verify(checkConfig, getPath("InputAvoidModifiersForTypesCheck.java"), expected);
    }

    @Test
    public void testLogger() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(AvoidModifiersForTypesCheck.class);
        checkConfig.addAttribute("forbiddenClassesRegexpAnnotation", null);
        checkConfig.addAttribute("forbiddenClassesRegexpFinal", null);
        checkConfig.addAttribute("forbiddenClassesRegexpStatic", null);
        checkConfig.addAttribute("forbiddenClassesRegexpTransient", null);
        checkConfig.addAttribute("forbiddenClassesRegexpVolatile", null);
        checkConfig.addAttribute("forbiddenClassesRegexpPrivate", null);
        checkConfig.addAttribute("forbiddenClassesRegexpPackagePrivate", "Logger");
        checkConfig.addAttribute("forbiddenClassesRegexpProtected", "Logger");
        checkConfig.addAttribute("forbiddenClassesRegexpPublic", "Logger");

        final String[] expected = {
            "36:9: " + getCheckMessage(MSG_KEY, "Logger", "protected"),
            "37:9: " + getCheckMessage(MSG_KEY, "Logger", "public"),
            "38:9: " + getCheckMessage(MSG_KEY, "Logger", "package-private"),
        };

        verify(checkConfig, getPath("InputAvoidModifiersForTypesCheck.java"), expected);
    }

    @Test
    public void testAnyFile() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(AvoidModifiersForTypesCheck.class);
        checkConfig.addAttribute("forbiddenClassesRegexpAnnotation", null);
        checkConfig.addAttribute("forbiddenClassesRegexpFinal", null);
        checkConfig.addAttribute("forbiddenClassesRegexpStatic", null);
        checkConfig.addAttribute("forbiddenClassesRegexpTransient", null);
        checkConfig.addAttribute("forbiddenClassesRegexpVolatile", null);
        checkConfig.addAttribute("forbiddenClassesRegexpPrivate", null);
        checkConfig.addAttribute("forbiddenClassesRegexpPackagePrivate", null);
        checkConfig.addAttribute("forbiddenClassesRegexpProtected", null);
        checkConfig.addAttribute("forbiddenClassesRegexpPublic", null);

        final String[] expected = {};

        verify(checkConfig, getPath("InputAvoidModifiersForTypesCheck2.java"), expected);
    }

    /**
     * This test is needed in order to test the Invalid Token case which is not reproducible with
     * compilable sources and correctly worked parser.
     */
    @Test
    public void testUnsupportedModifier() {
        final DetailAST sync = new DetailAST();
        sync.setType(TokenTypes.LITERAL_SYNCHRONIZED);

        final DetailAST ident = new DetailAST();
        ident.setType(TokenTypes.IDENT);
        ident.setText("dummy");

        final DetailAST type = new DetailAST();
        type.setType(TokenTypes.TYPE);
        type.addChild(ident);

        final DetailAST child = new DetailAST();
        child.setType(TokenTypes.MODIFIERS);
        child.addChild(sync);

        final DetailAST node = new DetailAST();
        node.addChild(type);
        node.addChild(child);

        final DetailAST parent = new DetailAST();
        parent.setType(TokenTypes.OBJBLOCK);
        parent.addChild(node);

        final AvoidModifiersForTypesCheck check = new AvoidModifiersForTypesCheck();
        try {
            check.visitToken(node);
            fail();
        }
        catch (IllegalArgumentException ex) {
            Assert.assertEquals("Found unsupported token: LITERAL_SYNCHRONIZED", ex.getMessage());
        }
    }

}
