////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2016 the original author or authors.
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

import static com.github.sevntu.checkstyle.checks.coding.AvoidModifiersForTypesCheck.*;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.github.sevntu.checkstyle.checks.coding.AvoidModifiersForTypesCheck;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 * @author <a href="mailto:yasser.aziza@gmail.com">Yasser Aziza</a>
 * </p>
 */
public class AvoidModifiersForTypesCheckTest extends BaseCheckTestSupport
{

    private final DefaultConfiguration checkConfig = createCheckConfig(AvoidModifiersForTypesCheck.class);

    @Test
    public void testFinal() throws Exception
    {
        String annotationRegexp = null;
        String finalRegexp = "InputAvoidModifiersForTypesCheck";
        String staticRegexp = null;
        String transientRegexp = null;
        String volatileRegexp = null;
        String publicRegexp = null;
        String packagePrivateRegexp = null;
        String protectedRegexp = null;
        String privateRegexp = null;

        checkConfig.addAttribute("forbiddenClassesRegexpAnnotation", annotationRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpFinal", finalRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpStatic", staticRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpTransient", transientRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpVolatile", volatileRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpPublic", publicRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpPackagePrivate", packagePrivateRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpProtected", protectedRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpPrivate", privateRegexp);

        String[] expected = {
            "19:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "final"),
            "20:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "final"),
            "24:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "final"),
            "29:9: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "final"),
        };

        verify(checkConfig, getPath("InputAvoidModifiersForTypesCheck.java"), expected);
    }

    @Test
    public void testPackagePrivate() throws Exception
    {
        String annotationRegexp = null;
        String finalRegexp = null;
        String staticRegexp = null;
        String transientRegexp = null;
        String volatileRegexp = null;
        String privateRegexp = null;
        String packagePrivateRegexp = "InputAvoidModifiersForTypesCheck";
        String protectedRegexp = null;
        String publicRegexp = null;

        checkConfig.addAttribute("forbiddenClassesRegexpAnnotation", annotationRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpFinal", finalRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpStatic", staticRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpTransient", transientRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpVolatile", volatileRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpPrivate", privateRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpPackagePrivate", packagePrivateRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpProtected", protectedRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpPublic", publicRegexp);

        String[] expected = {
            "23:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "package-private"),
            "24:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "package-private"),
        };

        verify(checkConfig, getPath("InputAvoidModifiersForTypesCheck.java"), expected);
    }

    @Test
    public void testAnnotationPrivateStatic() throws Exception
    {
        String annotationRegexp = "File";
        String finalRegexp = null;
        String staticRegexp = "File";
        String transientRegexp = null;
        String volatileRegexp = null;
        String privateRegexp = "File";
        String packagePrivateRegexp = null;
        String protectedRegexp = null;
        String publicRegexp = null;

        checkConfig.addAttribute("forbiddenClassesRegexpAnnotation", annotationRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpFinal", finalRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpStatic", staticRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpTransient", transientRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpVolatile", volatileRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpPrivate", privateRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpPackagePrivate", packagePrivateRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpProtected", protectedRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpPublic", publicRegexp);

        String[] expected = {
            "18:5: " + getCheckMessage(MSG_KEY, "File", "annotation"),
            "18:5: " + getCheckMessage(MSG_KEY, "File", "private"),
            "18:5: " + getCheckMessage(MSG_KEY, "File", "static"),
        };

        verify(checkConfig, getPath("InputAvoidModifiersForTypesCheck.java"), expected);
    }

    @Test
    public void testProtectedAndTransient() throws Exception
    {
        String annotationRegexp = null;
        String finalRegexp = null;
        String staticRegexp = null;
        String transientRegexp = "InputAvoidModifiersForTypesCheck";
        String volatileRegexp = null;
        String privateRegexp = null;
        String packagePrivateRegexp = null;
        String protectedRegexp = "InputAvoidModifiersForTypesCheck";
        String publicRegexp = null;

        checkConfig.addAttribute("forbiddenClassesRegexpAnnotation", annotationRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpFinal", finalRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpStatic", staticRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpTransient", transientRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpVolatile", volatileRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpPrivate", privateRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpPackagePrivate", packagePrivateRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpProtected", protectedRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpPublic", publicRegexp);

        String[] expected = {
            "21:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "protected"),
            "21:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "transient"),
        };

        verify(checkConfig, getPath("InputAvoidModifiersForTypesCheck.java"), expected);
    }

    @Test
    public void testPublicAndVolatile() throws Exception
    {
        String annotationRegexp = null;
        String finalRegexp = null;
        String staticRegexp = null;
        String transientRegexp = null;
        String volatileRegexp = "InputAvoidModifiersForTypesCheck";
        String privateRegexp = null;
        String packagePrivateRegexp = null;
        String protectedRegexp = null;
        String publicRegexp = "InputAvoidModifiersForTypesCheck";

        checkConfig.addAttribute("forbiddenClassesRegexpAnnotation", annotationRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpFinal", finalRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpStatic", staticRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpTransient", transientRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpVolatile", volatileRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpPrivate", privateRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpPackagePrivate", packagePrivateRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpProtected", protectedRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpPublic", publicRegexp);

        String[] expected = {
            "22:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "public"),
            "22:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "volatile"),
        };

        verify(checkConfig, getPath("InputAvoidModifiersForTypesCheck.java"), expected);
    }

    @Test
    public void testFinalAndPrivatePackage() throws Exception
    {
        String annotationRegexp = null;
        String finalRegexp = "InputAvoidModifiersForTypesCheck";
        String staticRegexp = null;
        String transientRegexp = null;
        String volatileRegexp = null;
        String privateRegexp = null;
        String packagePrivateRegexp = "InputAvoidModifiersForTypesCheck";
        String protectedRegexp = null;
        String publicRegexp = null;

        checkConfig.addAttribute("forbiddenClassesRegexpAnnotation", annotationRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpFinal", finalRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpStatic", staticRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpTransient", transientRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpVolatile", volatileRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpPrivate", privateRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpPackagePrivate", packagePrivateRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpProtected", protectedRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpPublic", publicRegexp);

        String[] expected = {
            "19:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "final"),
            "20:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "final"),
            "23:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "package-private"),
            "24:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "final"), // both
            "24:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "package-private"), // both
            "29:9: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "final"),
        };

        verify(checkConfig, getPath("InputAvoidModifiersForTypesCheck.java"), expected);
    }

    @Test
    public void testFinalAndStatic() throws Exception
    {
        String annotationRegexp = null;
        String finalRegexp = "InputAvoidModifiersForTypesCheck";
        String staticRegexp = "InputAvoidModifiersForTypesCheck";
        String transientRegexp = null;
        String volatileRegexp = null;
        String privateRegexp = null;
        String packagePrivateRegexp = null;
        String protectedRegexp = null;
        String publicRegexp = null;

        checkConfig.addAttribute("forbiddenClassesRegexpAnnotation", annotationRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpFinal", finalRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpStatic", staticRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpTransient", transientRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpVolatile", volatileRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpPrivate", privateRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpPackagePrivate", packagePrivateRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpProtected", protectedRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpPublic", publicRegexp);

        String[] expected = {
            "19:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "final"),
            "20:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "final"), // both
            "20:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "static"), // both
            "24:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "final"),
            "29:9: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "final"),
        };

        verify(checkConfig, getPath("InputAvoidModifiersForTypesCheck.java"), expected);
    }

    @Test
    public void testProtected() throws Exception
    {
        String annotationRegexp = null;
        String finalRegexp = null;
        String staticRegexp = null;
        String transientRegexp = null;
        String volatileRegexp = null;
        String privateRegexp = null;
        String packagePrivateRegexp = null;
        String protectedRegexp = "InputAvoidModifiersForTypesCheck";
        String publicRegexp = null;

        checkConfig.addAttribute("forbiddenClassesRegexpAnnotation", annotationRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpFinal", finalRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpStatic", staticRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpTransient", transientRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpVolatile", volatileRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpPrivate", privateRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpPackagePrivate", packagePrivateRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpProtected", protectedRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpPublic", publicRegexp);

        String[] expected = {
            "21:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "protected"),
        };

        verify(checkConfig, getPath("InputAvoidModifiersForTypesCheck.java"), expected);
    }

    @Test
    public void testLogger() throws Exception
    {
        String annotationRegexp = null;
        String finalRegexp = null;
        String staticRegexp = null;
        String transientRegexp = null;
        String volatileRegexp = null;
        String privateRegexp = null;
        String packagePrivateRegexp = "Logger";
        String protectedRegexp = "Logger";
        String publicRegexp = "Logger";

        checkConfig.addAttribute("forbiddenClassesRegexpAnnotation", annotationRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpFinal", finalRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpStatic", staticRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpTransient", transientRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpVolatile", volatileRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpPrivate", privateRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpPackagePrivate", packagePrivateRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpProtected", protectedRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpPublic", publicRegexp);

        String[] expected = {
            "36:9: " + getCheckMessage(MSG_KEY, "Logger", "protected"),
            "37:9: " + getCheckMessage(MSG_KEY, "Logger", "public"),
            "38:9: " + getCheckMessage(MSG_KEY, "Logger", "package-private"),
        };

        verify(checkConfig, getPath("InputAvoidModifiersForTypesCheck.java"), expected);
    }

    @Test
    public void testAnyFile() throws Exception
    {
        String annotationRegexp = null;
        String finalRegexp = null;
        String staticRegexp = null;
        String transientRegexp = null;
        String volatileRegexp = null;
        String privateRegexp = null;
        String packagePrivateRegexp = null;
        String protectedRegexp = null;
        String publicRegexp = null;

        checkConfig.addAttribute("forbiddenClassesRegexpAnnotation", annotationRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpFinal", finalRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpStatic", staticRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpTransient", transientRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpVolatile", volatileRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpPrivate", privateRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpPackagePrivate", packagePrivateRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpProtected", protectedRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpPublic", publicRegexp);

        String[] expected = {};

        verify(checkConfig, getPath("InputAvoidModifiersForTypesCheck2.java"), expected);
    }

    /**
     * This test is needed in order to test the Invalid Token case which is not reproducible with
     * compilable sources and correctly worked parser.
     * @throws Exception
     */
    @Test(expected = IllegalArgumentException.class)
    public void testUnsupportedModifier()
    {
        DetailAST sync = new DetailAST();
        sync.setType(TokenTypes.LITERAL_SYNCHRONIZED);

        DetailAST ident = new DetailAST();
        ident.setType(TokenTypes.IDENT);
        ident.setText("dummy");

        DetailAST type = new DetailAST();
        type.setType(TokenTypes.TYPE);
        type.addChild(ident);

        DetailAST child = new DetailAST();
        child.setType(TokenTypes.MODIFIERS);
        child.addChild(sync);

        DetailAST node = new DetailAST();
        node.addChild(type);
        node.addChild(child);

        DetailAST parent = new DetailAST();
        parent.setType(TokenTypes.OBJBLOCK);
        parent.addChild(node);

        AvoidModifiersForTypesCheck check = new AvoidModifiersForTypesCheck();
        check.visitToken(node);

        fail();
    }
}
