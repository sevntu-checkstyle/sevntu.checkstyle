package com.puppycrawl.tools.checkstyle.checks.coding;

import java.io.File;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class CustomDeclarationOrderCheckTest extends BaseCheckTestSupport {

    @Test
    public void testCustomDecrationOrder() throws Exception
    {
        final DefaultConfiguration checkConfig =
            createCheckConfig(CustomDeclarationOrderCheck.class);
        final String[] expected = {
                "17:5: Field definition in wrong order. Expected 'Field(protected)' then 'Field(private)'.",
                "19:5: Field definition in wrong order. Expected 'Field(@SuppressWarnings(.*serial.*))' then 'Field(private)'.",
                "38:5: Constructor definition in wrong order. Expected 'Ctor()' then 'Method(.*abstract.*public|.*public.*abstract|protected)'.",
                "49:9: Field definition in wrong order. Expected 'Field(.*final.*public|.*public.*final)' then 'Field(private)'.",
                "69:9: Class definition in wrong order. Expected 'InnerClass (public.*abstract)' then 'InnerClass (private)'.",
                "73:5: Method definition in wrong order. Expected 'Method(@Deprecated)' then 'InnerClass (private)'.",
                "84:5: Method definition in wrong order. Expected 'Method(.*abstract.*public|.*public.*abstract|protected)' then 'InnerClass (private)'.",
                "94:5: Field definition in wrong order. Expected 'Field(private)' then 'Ctor()'." };
        System.setProperty("testinputs.dir",
                "/home/danil/workspace/my/sevntu.checkstyle/src/testinputs/com/puppycrawl/tools/checkstyle");
        checkConfig.addAttribute("customDeclarationOrder",
                                  "Field(.*final.*public|.*public.*final) ###"
                                + "Field(@SuppressWarnings(.*serial.*)) ###"
                                + "Field(protected) ###"
                                + "Field(private) ###"
                                + "Ctor()###"
                                + "Method(@Deprecated) ###"
                                + "Method() ###"
                                + "Method(.*abstract.*public|.*public.*abstract|protected) ###"
                                + "InnerClass (public.*abstract) ###"
                                + "InnerClass (private)");
        checkConfig.addAttribute("ignoreRegExCase", "true");
        verify(checkConfig, getPath("coding" + File.separator
                + "InputCustomDeclarationOrder.java"), expected);
        checkConfig.addAttribute("customDeclarationOrder", "Field .*final.*public|.*public.*final)");
    }

}
