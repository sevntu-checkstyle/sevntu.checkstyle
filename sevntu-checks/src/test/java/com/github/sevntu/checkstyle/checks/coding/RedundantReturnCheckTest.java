package com.github.sevntu.checkstyle.checks.coding;

import org.junit.Test;
import static com.github.sevntu.checkstyle.checks.coding.RedundantReturnCheck.MSG_KEY;
import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.github.sevntu.checkstyle.checks.coding.RedundantReturnCheck;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class RedundantReturnCheckTest extends BaseCheckTestSupport
{
    @Test
    public void testInputWithIgnoreEmptyConstructorsTrue()
            throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(RedundantReturnCheck.class);
        checkConfig.addAttribute("allowReturnInEmptyMethodsAndConstructors",
                "false");

        final String[] expected = { 
                "12: " + getCheckMessage(MSG_KEY),
                "19: " + getCheckMessage(MSG_KEY), 
                "24: " + getCheckMessage(MSG_KEY),
                "34: " + getCheckMessage(MSG_KEY), 
                "41: " + getCheckMessage(MSG_KEY),
                "54: " + getCheckMessage(MSG_KEY), 
                "58: " + getCheckMessage(MSG_KEY),
                "62: " + getCheckMessage(MSG_KEY), 
                "89: " + getCheckMessage(MSG_KEY),
                "102: " + getCheckMessage(MSG_KEY), 
                "106: " + getCheckMessage(MSG_KEY),
                "119: " + getCheckMessage(MSG_KEY),
                "130: " + getCheckMessage(MSG_KEY),
                "142: " + getCheckMessage(MSG_KEY),
                "153: " + getCheckMessage(MSG_KEY)
        };

        verify(checkConfig, getPath("InputRedundantReturn.java"), expected);
    }

    @Test
    public void testInputWithIgnoreEmptyConstructorsFalse()
            throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(RedundantReturnCheck.class);
        checkConfig.addAttribute("allowReturnInEmptyMethodsAndConstructors",
                "true");

        final String[] expected = { 
                "19: " + getCheckMessage(MSG_KEY),
                "34: " + getCheckMessage(MSG_KEY), 
                "41: " + getCheckMessage(MSG_KEY),
                "54: " + getCheckMessage(MSG_KEY), 
                "58: " + getCheckMessage(MSG_KEY),
                "62: " + getCheckMessage(MSG_KEY), 
                "102: " + getCheckMessage(MSG_KEY),
                "106: " + getCheckMessage(MSG_KEY), 
                "119: " + getCheckMessage(MSG_KEY),
                "130: " + getCheckMessage(MSG_KEY),
                "142: " + getCheckMessage(MSG_KEY),
                "153: " + getCheckMessage(MSG_KEY)
        };

        verify(checkConfig, getPath("InputRedundantReturn.java"), expected);

    }

}