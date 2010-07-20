package com.puppycrawl.tools.checkstyle.checks.sizes;

import java.io.File;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import org.junit.Test;

public class LineLengthCheckTest extends BaseCheckTestSupport
{
    @Test
    public void testSimple()
        throws Exception
    {
        final DefaultConfiguration checkConfig =
            createCheckConfig(LineLengthCheck.class);
        checkConfig.addAttribute("max", "80");
        checkConfig.addAttribute("ignorePattern",  "^.*is OK.*regexp.*$");
        final String[] expected = {
            "11: Line is longer than 80 characters.",
            "21: Line is longer than 80 characters.",
        };
        System.setProperty("testinputs.dir", "/home/romani/Practice/workspace/sevntu.checkstyle/src/testinputs/com/puppycrawl/tools/checkstyle");
        verify(checkConfig, getPath("sizes" + File.separator + "InputLineLengthCheck.java"), expected);
    }
}