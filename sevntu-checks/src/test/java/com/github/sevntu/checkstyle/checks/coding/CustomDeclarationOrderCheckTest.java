package com.github.sevntu.checkstyle.checks.coding;

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
                "10:5: Field definition in wrong order. Expected 'Field(private static final long serialVersionUID)' then 'Field(.*final public.*|.*public final.*)'.",
                "20:5: Field definition in wrong order. Expected 'Field(protected.*)' then 'Field(private.*)'.",
                "22:5: Field definition in wrong order. Expected 'Field(@SuppressWarnings(.*serial.*).*)' then 'Field(private.*)'.",
                "45:5: Constructor definition in wrong order. Expected 'Ctor()' then 'Method(.*abstract.*public.*|.*public.*abstract.*|protected.*)'.",
                "52:5: Method definition in wrong order. Expected 'Method(public static.*(new|edit|create).*)' then 'Method(.*abstract.*public.*|.*public.*abstract.*|protected.*)'.",
                "60:9: Field definition in wrong order. Expected 'Field(.*final public.*|.*public final.*)' then 'Field(private.*)'.",
                "80:9: Class definition in wrong order. Expected 'InnerClass (public.*abstract.*)' then 'InnerClass (private.*)'.",
                "84:5: Method definition in wrong order. Expected 'Method(@Deprecated.*)' then 'InnerClass (private.*)'.",
                "95:5: Method definition in wrong order. Expected 'Method(.*abstract.*public.*|.*public.*abstract.*|protected.*)' then 'InnerClass (private.*)'.",
                "105:5: Field definition in wrong order. Expected 'Field(private.*)' then 'Ctor()'." };
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
                "38:5: Field definition in wrong order. Expected 'DeclareAnnonClassField(private.*)' then 'Field(public.*)'.",
                "53:5: Field definition in wrong order. Expected 'DeclareAnnonClassField(private.*)' then 'Field(private.*)'.",
                "65:5: Field definition in wrong order. Expected 'DeclareAnnonClassField()' then 'Ctor(public.*)'.",
                "94:9: Field definition in wrong order. Expected 'DeclareAnnonClassField(private.*)' then 'Field(private.*)'.",
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
                "49:5: Method definition in wrong order. Expected 'GetterSetter(.*)' then 'Method(.*)'.",
                "54:5: Method definition in wrong order. Expected 'GetterSetter(.*)' then 'Method(.*)'.",
                "59:5: Method definition in wrong order. Expected 'GetterSetter(.*)' then 'Method(.*)'.",
                "64:5: Method definition in wrong order. Expected 'GetterSetter(.*)' then 'Method(.*)'.",
                "69:5: Method definition in wrong order. Expected 'GetterSetter(.*)' then 'Method(.*)'.",
                "74:5: Method definition in wrong order. Expected 'GetterSetter(.*)' then 'Method(.*)'.",
                "89: Setter 'setField' is in wrong order. Should be right after 'getField'.",
                "126: Setter 'setField' is in wrong order. Should be right after 'getField'.",
                "131: Setter 'setX' is in wrong order. Should be right after 'getX'.",
                "136: Setter 'setVisible' is in wrong order. Should be right after 'isVisible'.",
                "165: Setter 'setField' is in wrong order. Should be right after 'getField'.",
                "173:3: Method definition in wrong order. Expected 'GetterSetter(.*)' then 'Method(.*)'.",
                "177:3: Method definition in wrong order. Expected 'GetterSetter(.*)' then 'Method(.*)'.",
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
                "40:5: Field definition in wrong order. Expected 'Field(private)' then 'InnerInterface(.*)'.",
                "48:5: Field definition in wrong order. Expected 'Field(private)' then 'InnerEnum(.*)'.",
                "56:5: Interface definition in wrong order. Expected 'InnerInterface(.*)' then 'InnerEnum(.*)'.",
                "69:5: Method definition in wrong order. Expected 'Method(.*)' then 'InnerEnum(.*)'.",

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
                "9:5: Field definition in wrong order. Expected 'Field(private)' then 'Method(.*)'.",
                "16:5: Field definition in wrong order. Expected 'Field(private)' then 'Method(.*)'.",
        };
        checkConfig.addAttribute(
                        "customDeclarationOrder",
                        "Field(private) ### Field(public) ### MainMethod() ### Method(.*)"
                        );
        checkConfig.addAttribute("caseSensitive", "false");
        verify(checkConfig, getPath("InputCustomDeclarationOrderCheckMainMethod.java"), expected);
    }
}
