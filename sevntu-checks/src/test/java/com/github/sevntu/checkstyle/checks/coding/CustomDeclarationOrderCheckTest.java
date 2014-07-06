package com.github.sevntu.checkstyle.checks.coding;

import static com.github.sevntu.checkstyle.checks.coding.CustomDeclarationOrderCheck.*;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class CustomDeclarationOrderCheckTest extends BaseCheckTestSupport {

    @Test
    public void testCustomDecrationOrder() throws Exception
    {
        final DefaultConfiguration checkConfig =
            createCheckConfig(CustomDeclarationOrderCheck.class);
        final String[] expected = {
        		"10:5: " + getCheckMessage(MSG_KEY_FIELD, "Field(private static final long serialVersionUID)", "Field(.*final public.*|.*public final.*)"),
        		"20:5: " + getCheckMessage(MSG_KEY_FIELD, "Field(protected.*)", "Field(private.*)"),
        		"22:5: " + getCheckMessage(MSG_KEY_FIELD, "Field(@SuppressWarnings(.*serial.*).*)", "Field(private.*)"),
        		"45:5: " + getCheckMessage(MSG_KEY_CONSTRUCTOR, "Ctor()", "Method(.*abstract.*public.*|.*public.*abstract.*|protected.*)"),
        		"52:5: " + getCheckMessage(MSG_KEY_METHOD, "Method(public static.*(new|edit|create).*)", "Method(.*abstract.*public.*|.*public.*abstract.*|protected.*)"),
                "60:9: " + getCheckMessage(MSG_KEY_FIELD, "Field(.*final public.*|.*public final.*)", "Field(private.*)"),
                "80:9: " + getCheckMessage(MSG_KEY_CLASS, "InnerClass (public.*abstract.*)", "InnerClass (private.*)"),
                "84:5: " + getCheckMessage(MSG_KEY_METHOD, "Method(@Deprecated.*)", "InnerClass (private.*)"),
                "95:5: " + getCheckMessage(MSG_KEY_METHOD, "Method(.*abstract.*public.*|.*public.*abstract.*|protected.*)", "InnerClass (private.*)"),
                "105:5: " + getCheckMessage(MSG_KEY_FIELD, "Field(private.*)", "Ctor()"), 
                };
        checkConfig.addAttribute("customDeclarationOrder",
                                  "Field(private static final long serialVersionUID) ###"
                                + "Field(.*final public.*|.*public final.*) ###"
                                + "Field(@SuppressWarnings(.*serial.*).*) ###"
                                + "Field(protected.*) ###"
                                + "Field(private.*) ###"
                                + "Ctor()###"
                                + "Method(@Deprecated.*) ###"
                                + "Method(public static.*(new|edit|create).*) ###"
                                + "Method() ###"
                                + "Method(.*abstract.*public.*|.*public.*abstract.*|protected.*) ###"
                                + "InnerClass (public.*abstract.*) ###"
                                + "InnerClass (private.*)");
        checkConfig.addAttribute("caseSensitive", "true");
        verify(checkConfig, getPath("InputCustomDeclarationOrder.java"), expected);
        checkConfig.addAttribute("customDeclarationOrder", "Field .*final.*public|.*public.*final)");
    }

    @Test
    public void anonymousClasses()
            throws Exception
    {
        final DefaultConfiguration checkConfig =
                createCheckConfig(CustomDeclarationOrderCheck.class);
        final String[] expected = {
        		"38:5: " + getCheckMessage(MSG_KEY_FIELD, "DeclareAnnonClassField(private.*)", "Field(public.*)"),
        		"53:5: " + getCheckMessage(MSG_KEY_FIELD, "DeclareAnnonClassField(private.*)", "Field(private.*)"),
        		"65:5: " + getCheckMessage(MSG_KEY_FIELD, "DeclareAnnonClassField()", "Ctor(public.*)"),
        		"94:9: " + getCheckMessage(MSG_KEY_FIELD, "DeclareAnnonClassField(private.*)", "Field(private.*)"),
        };
        checkConfig
                .addAttribute(
                        "customDeclarationOrder",
                        "DeclareAnnonClassField(private.*) ###"
                                + "DeclareAnnonClassField(protected.*) ###"
                                + "DeclareAnnonClassField() ###"
                                + "DeclareAnnonClassField(public.*) ###"
                                + "Field(private.*) ###"
                                + "Field(public.*) ###"
                                + "Ctor(public.*) ###"
                                + "Method(public.*)"
                        );
        checkConfig.addAttribute("caseSensitive", "false");
        verify(checkConfig, getPath("InputCustomDeclarationOrderCheckAnonymousClasses.java"),
                expected);
    }

    @Test
    public void gettersSetters()
            throws Exception
    {
        final DefaultConfiguration checkConfig =
                createCheckConfig(CustomDeclarationOrderCheck.class);
        final String[] expected = {
        		"51:5: " + getCheckMessage(MSG_KEY_METHOD, "GetterSetter(.*)", "Method(.*)"),
        		"57:5: " + getCheckMessage(MSG_KEY_METHOD, "GetterSetter(.*)", "Method(.*)"),
        		"62:5: " + getCheckMessage(MSG_KEY_METHOD, "GetterSetter(.*)", "Method(.*)"),
        		"67:5: " + getCheckMessage(MSG_KEY_METHOD, "GetterSetter(.*)", "Method(.*)"),
        		"72:5: " + getCheckMessage(MSG_KEY_METHOD, "GetterSetter(.*)", "Method(.*)"),
        		"77:5: " + getCheckMessage(MSG_KEY_METHOD, "GetterSetter(.*)", "Method(.*)"), 
        		"92: " + getCheckMessage(MSG_KEY_INVALID_SETTER, "setField", "getField"),
        		"129: " + getCheckMessage(MSG_KEY_INVALID_SETTER, "setField", "getField"),
        		"134: " + getCheckMessage(MSG_KEY_INVALID_SETTER, "setX", "getX"), 
        		"139: " + getCheckMessage(MSG_KEY_INVALID_SETTER, "setVisible", "isVisible"),
        		"169: " + getCheckMessage(MSG_KEY_INVALID_SETTER, "setField", "getField"),
        		"177:5: " + getCheckMessage(MSG_KEY_METHOD, "GetterSetter(.*)", "Method(.*)"),
        		"181:5: " + getCheckMessage(MSG_KEY_METHOD, "GetterSetter(.*)", "Method(.*)"),
        		"293:5: " + getCheckMessage(MSG_KEY_METHOD, "GetterSetter(.*)", "Method(.*)"),
        		"297:5: " + getCheckMessage(MSG_KEY_METHOD, "GetterSetter(.*)", "Method(.*)"),
        		"302:5: " + getCheckMessage(MSG_KEY_METHOD, "GetterSetter(.*)", "Method(.*)"),
        };
        checkConfig.addAttribute(
                        "customDeclarationOrder",
                        "GetterSetter(.*) ### Method(.*)"
                        );
        checkConfig.addAttribute("caseSensitive", "false");
        verify(checkConfig, getPath("InputCustomDeclarationOrderCheckGettersSetters.java"), expected);
    }

    @Test
    public void innerInterfacesAndEnums()
            throws Exception
    {
        final DefaultConfiguration checkConfig =
                createCheckConfig(CustomDeclarationOrderCheck.class);
        final String[] expected = {
        		"40:5: " + getCheckMessage(MSG_KEY_FIELD, "Field(private)", "InnerInterface(.*)"),
        		"48:5: " + getCheckMessage(MSG_KEY_FIELD, "Field(private)", "InnerEnum(.*)"),
        		"56:5: " + getCheckMessage(MSG_KEY_INTERFACE, "InnerInterface(.*)", "InnerEnum(.*)"),
        		"69:5: " + getCheckMessage(MSG_KEY_METHOD, "Method(.*)", "InnerEnum(.*)"),
        };
        
        checkConfig.addAttribute(
                        "customDeclarationOrder",
                        "Field(private) ### Field(public) ### Method(.*) ### InnerInterface(.*) ### InnerEnum(.*)"
                        );
        checkConfig.addAttribute("caseSensitive", "true");
        verify(checkConfig, getPath("InputCustomDeclarationOrderCheckInnerInterfaceEnum.java"), expected);
    }

    @Test
    public void mainMethod()
            throws Exception
    {
        final DefaultConfiguration checkConfig =
                createCheckConfig(CustomDeclarationOrderCheck.class);
        final String[] expected = {
        		"9:5: " + getCheckMessage(MSG_KEY_FIELD, "Field(private)", "Method(.*)"),
        		"16:5: " + getCheckMessage(MSG_KEY_FIELD, "Field(private)", "Method(.*)"),
        };
        checkConfig.addAttribute(
                        "customDeclarationOrder",
                        "Field(private) ### Field(public) ### MainMethod() ### Method(.*)"
                        );
        checkConfig.addAttribute("caseSensitive", "false");
        verify(checkConfig, getPath("InputCustomDeclarationOrderCheckMainMethod.java"), expected);
    }
}
