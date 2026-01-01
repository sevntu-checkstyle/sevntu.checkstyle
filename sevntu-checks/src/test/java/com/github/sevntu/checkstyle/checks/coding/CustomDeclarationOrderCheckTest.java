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

import static com.github.sevntu.checkstyle.checks.coding.CustomDeclarationOrderCheck.MSG_KEY_CLASS;
import static com.github.sevntu.checkstyle.checks.coding.CustomDeclarationOrderCheck.MSG_KEY_CONSTRUCTOR;
import static com.github.sevntu.checkstyle.checks.coding.CustomDeclarationOrderCheck.MSG_KEY_FIELD;
import static com.github.sevntu.checkstyle.checks.coding.CustomDeclarationOrderCheck.MSG_KEY_INTERFACE;
import static com.github.sevntu.checkstyle.checks.coding.CustomDeclarationOrderCheck.MSG_KEY_INVALID_SETTER;
import static com.github.sevntu.checkstyle.checks.coding.CustomDeclarationOrderCheck.MSG_KEY_METHOD;

import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class CustomDeclarationOrderCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/coding";
    }

    @Test
    public void testCustomDeclarationOrder() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(CustomDeclarationOrderCheck.class);
        final String[] expected = {
            "10:5: " + getCheckMessage(MSG_KEY_FIELD,
                    "Field(private static final long serialVersionUID)",
                    "Field(.*final public .*|.*public final .*)"),
            "20:5: " + getCheckMessage(MSG_KEY_FIELD, "Field(protected .*)", "Field(private .*)"),
            "22:5: " + getCheckMessage(MSG_KEY_FIELD, "Field(@SuppressWarnings(.*serial.*).*)",
                    "Field(private .*)"),
            "45:5: " + getCheckMessage(MSG_KEY_CONSTRUCTOR, "Ctor()",
                    "Method(.*abstract .*public .*|.*public .*abstract .*|protected .*)"),
            "52:5: " + getCheckMessage(MSG_KEY_METHOD,
                    "Method(public static .*(new|edit|create).*)",
                    "Method(.*abstract .*public .*|.*public .*abstract .*|protected .*)"),
            "60:9: " + getCheckMessage(MSG_KEY_FIELD, "Field(.*final public .*|.*public final .*)",
                    "Field(private .*)"),
            "80:9: " + getCheckMessage(MSG_KEY_CLASS, "InnerClass (public .*abstract .*)",
                    "InnerClass (private .*)"),
            "84:5: " + getCheckMessage(MSG_KEY_METHOD, "Method(@Deprecated .*)",
                    "InnerClass (private .*)"),
            "95:5: " + getCheckMessage(MSG_KEY_METHOD,
                    "Method(.*abstract .*public .*|.*public .*abstract .*|protected .*)",
                    "InnerClass (private .*)"),
            "105:5: " + getCheckMessage(MSG_KEY_FIELD, "Field(private .*)", "Ctor()"),
        };
        checkConfig.addProperty("customDeclarationOrder",
                                  "Field(private static final long serialVersionUID) ###"
                                + "Field(.*final public .*|.*public final .*) ###"
                                + "Field(@SuppressWarnings(.*serial.*).*) ###"
                                + "Field(protected .*) ###"
                                + "Field(private .*) ###"
                                + "Ctor()###"
                                + "Method(@Deprecated .*) ###"
                                + "Method(public static .*(new|edit|create).*) ###"
                                + "Method() ###"
                                + "Method(.*abstract .*public .*|.*public .*abstract .*|"
                                    + "protected .*) ###"
                                + "InnerClass (public .*abstract .*) ###"
                                + "InnerClass (private .*)");
        checkConfig.addProperty("caseSensitive", "true");
        verify(checkConfig, getPath("InputCustomDeclarationOrderCheck.java"), expected);
        checkConfig
                .addProperty("customDeclarationOrder", "Field .*final.*public|.*public.*final)");
    }

    @Test
    public void anonymousClasses()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(CustomDeclarationOrderCheck.class);
        final String[] expected = {
            "39:5: " + getCheckMessage(MSG_KEY_FIELD, "DeclareAnonClassField(private .*)",
                    "Field(public .*)"),
            "54:5: " + getCheckMessage(MSG_KEY_FIELD, "DeclareAnonClassField(private .*)",
                    "Field(private .*)"),
            "66:5: " + getCheckMessage(MSG_KEY_FIELD, "DeclareAnonClassField()",
                    "Ctor(public .*)"),
            "95:9: " + getCheckMessage(MSG_KEY_FIELD, "DeclareAnonClassField(private .*)",
                    "Field(private .*)"),
        };
        checkConfig
            .addProperty(
                    "customDeclarationOrder",
                    "DeclareAnonClassField(private .*) ###"
                            + "DeclareAnonClassField(protected .*) ###"
                            + "DeclareAnonClassField() ###"
                            + "DeclareAnonClassField(public .*) ###"
                            + "Field(private .*) ###"
                            + "Field(public .*) ###"
                            + "Ctor(public .*) ###"
                            + "Method(public .*)"
        );
        checkConfig.addProperty("caseSensitive", "false");
        verify(checkConfig, getPath("InputCustomDeclarationOrderCheckAnonymousClasses.java"),
                expected);
    }

    @Test
    public void gettersSetters()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(CustomDeclarationOrderCheck.class);
        final String[] expected = {
            "50:5: " + getCheckMessage(MSG_KEY_METHOD, "GetterSetter(.*)", "Method(.*)"),
            "56:5: " + getCheckMessage(MSG_KEY_METHOD, "GetterSetter(.*)", "Method(.*)"),
            "61:5: " + getCheckMessage(MSG_KEY_METHOD, "GetterSetter(.*)", "Method(.*)"),
            "66:5: " + getCheckMessage(MSG_KEY_METHOD, "GetterSetter(.*)", "Method(.*)"),
            "71:5: " + getCheckMessage(MSG_KEY_METHOD, "GetterSetter(.*)", "Method(.*)"),
            "76:5: " + getCheckMessage(MSG_KEY_METHOD, "GetterSetter(.*)", "Method(.*)"),
            "91:5: " + getCheckMessage(MSG_KEY_INVALID_SETTER, "setField", "getField"),
            "128:5: " + getCheckMessage(MSG_KEY_INVALID_SETTER, "setField", "getField"),
            "133:5: " + getCheckMessage(MSG_KEY_INVALID_SETTER, "setX", "getX"),
            "138:5: " + getCheckMessage(MSG_KEY_INVALID_SETTER, "setVisible", "isVisible"),
            "168:5: " + getCheckMessage(MSG_KEY_INVALID_SETTER, "setField", "getField"),
            "176:5: " + getCheckMessage(MSG_KEY_METHOD, "GetterSetter(.*)", "Method(.*)"),
            "180:5: " + getCheckMessage(MSG_KEY_METHOD, "GetterSetter(.*)", "Method(.*)"),
            "282:5: " + getCheckMessage(MSG_KEY_METHOD, "GetterSetter(.*)", "Method(.*)"),
            "286:5: " + getCheckMessage(MSG_KEY_METHOD, "GetterSetter(.*)", "Method(.*)"),
            "291:5: " + getCheckMessage(MSG_KEY_METHOD, "GetterSetter(.*)", "Method(.*)"),
        };
        checkConfig.addProperty(
            "customDeclarationOrder",
            "GetterSetter(.*) ### Method(.*)"
        );
        checkConfig.addProperty("caseSensitive", "false");
        verify(checkConfig, getPath("InputCustomDeclarationOrderCheckGettersSetters.java"),
                expected);
    }

    @Test
    public void innerInterfacesAndEnums()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(CustomDeclarationOrderCheck.class);
        final String[] expected = {
            "41:5: " + getCheckMessage(MSG_KEY_FIELD, "Field(private )", "InnerInterface(.*)"),
            "49:5: " + getCheckMessage(MSG_KEY_FIELD, "Field(private )", "InnerEnum(.*)"),
            "57:5: " + getCheckMessage(MSG_KEY_INTERFACE, "InnerInterface(.*)", "InnerEnum(.*)"),
            "70:5: " + getCheckMessage(MSG_KEY_METHOD, "Method(.*)", "InnerEnum(.*)"),
        };

        checkConfig.addProperty(
            "customDeclarationOrder",
            "Field(private ) ### Field(public ) ### Method(.*) ### InnerInterface(.*) ### "
                    + "InnerEnum(.*)"
        );
        checkConfig.addProperty("caseSensitive", "true");
        verify(checkConfig, getPath("InputCustomDeclarationOrderCheckInnerInterfaceEnum.java"),
                expected);
    }

    @Test
    public void mainMethod()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(CustomDeclarationOrderCheck.class);
        final String[] expected = {
            "10:5: " + getCheckMessage(MSG_KEY_FIELD, "Field(private )", "MainMethod(.*)"),
            "17:5: " + getCheckMessage(MSG_KEY_FIELD, "Field(private )", "MainMethod(.*)"),
            "23:5: " + getCheckMessage(MSG_KEY_METHOD, "MainMethod(.*)", "Method(.*)"),
        };
        checkConfig.addProperty(
                        "customDeclarationOrder",
                        "Field(private ) ### Field(public ) ### MainMethod(.*) ### Method(.*)"
        );
        checkConfig.addProperty("caseSensitive", "false");
        verify(checkConfig, getPath("InputCustomDeclarationOrderCheckMainMethod.java"), expected);
    }

}
