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
                "18:5: Field definition in wrong order.",
                "22:5: Field definition in wrong order.",
                "24:5: Field definition in wrong order.",
                "44:5: Constructor definition in wrong order.",
                "56:9: Field definition in wrong order.",
                "81:9: Class definition in wrong order.",
                "93:5: Method definition in wrong order.",
                "105:5: Field definition in wrong order." };
        System.setProperty("testinputs.dir",
                "/home/danil/workspace/my/sevntu.checkstyle/src/testinputs/com/puppycrawl/tools/checkstyle");
        checkConfig.addAttribute("customDeclarationOrder",
                                  "Field(.*final.*public|.*public.*final) ###"
                                + "Field(@SuppressWarnings(.*serial.*)) ###"
                                + "Field(@Rule) ###"
                                + "Field(protected) ### "
                                + "Field(private) ###"
                                + "CTOR()###"
                                + "Method() ###"
                                + "Method(.*abstract.*public|.*public.*abstract|protected) ###"
                                + "Method(@AfterClass) ###"
                                + "Method(@Ignore) ###"
                                + "InnerClass (public.*abstract) ###"
                                + "InnerClass (private)");
        checkConfig.addAttribute("ignoreRegExCase", "true");
        verify(checkConfig, getPath("coding" + File.separator
                + "InputCustomDeclarationOrder.java"), expected);
        checkConfig.addAttribute("customDeclarationOrder", "Field .*final.*public|.*public.*final)");
    }

}
