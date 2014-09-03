package com.github.sevntu.checkstyle.checks.coding;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import org.junit.Test;

public class InputAvoidModifiersForTypesCheck2 extends BaseCheckTestSupport
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
            "18: Line is longer than 80 characters.",
            "145: Line is longer than 80 characters.",
        };
        //System.setProperty("testinputs.dir", "/home/romani/Practice/New_workspace/sevntu.checkstyle/src/testinputs/com/puppycrawl/tools/checkstyle/sizes");
        verify(checkConfig, getPath("InputSimple.java"), expected);
    }

    @Test
    public void testSimpleIgnore()
        throws Exception
    {
        final DefaultConfiguration checkConfig =
            createCheckConfig(LineLengthCheck.class);
        checkConfig.addAttribute("max", "40");
        checkConfig.addAttribute("ignorePattern",  "^.*is OK.*regexp.*$");
        final String[] expected = {
            "1: Line is longer than 40 characters.",
            "5: Line is longer than 40 characters.",
            "6: Line is longer than 40 characters.",
            "18: Line is longer than 40 characters.",
            "101: Line is longer than 40 characters.",
            "125: Line is longer than 40 characters.",
            "128: Line is longer than 40 characters.",
            "132: Line is longer than 40 characters.",
            "145: Line is longer than 40 characters.",
            "146: Line is longer than 40 characters.",
            "148: Line is longer than 40 characters.",
            "151: Line is longer than 40 characters.",
            "152: Line is longer than 40 characters.",
            "192: Line is longer than 40 characters.",
            "200: Line is longer than 40 characters.",
            "207: Line is longer than 40 characters.",
        };
        checkConfig.addAttribute("ignoreClass", "true");
        checkConfig.addAttribute("ignoreConstructor", "true");
        checkConfig.addAttribute("ignoreField", "true");
        checkConfig.addAttribute("ignoreMethod", "true");
        //System.setProperty("testinputs.dir", "/home/romani/Practice/New_workspace/sevntu.checkstyle/src/testinputs/com/puppycrawl/tools/checkstyle/sizes");
        verify(checkConfig, getPath("InputSimple.java"), expected);
    }
    
    class LineLengthCheck {
        
    }
    
}