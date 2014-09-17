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
                "10: " + getCheckMessage(MSG_KEY),
                "17: " + getCheckMessage(MSG_KEY), 
                "22: " + getCheckMessage(MSG_KEY),
                "32: " + getCheckMessage(MSG_KEY), 
                "39: " + getCheckMessage(MSG_KEY),
                "52: " + getCheckMessage(MSG_KEY), 
                "56: " + getCheckMessage(MSG_KEY),
                "60: " + getCheckMessage(MSG_KEY), 
                "87: " + getCheckMessage(MSG_KEY),
                "100: " + getCheckMessage(MSG_KEY), 
                "104: " + getCheckMessage(MSG_KEY),
                "117: " + getCheckMessage(MSG_KEY),
                "128: " + getCheckMessage(MSG_KEY),
                "140: " + getCheckMessage(MSG_KEY),
                "151: " + getCheckMessage(MSG_KEY),
                "181: " + getCheckMessage(MSG_KEY)
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
                "17: " + getCheckMessage(MSG_KEY),
                "32: " + getCheckMessage(MSG_KEY), 
                "39: " + getCheckMessage(MSG_KEY),
                "52: " + getCheckMessage(MSG_KEY), 
                "56: " + getCheckMessage(MSG_KEY),
                "60: " + getCheckMessage(MSG_KEY), 
                "100: " + getCheckMessage(MSG_KEY),
                "104: " + getCheckMessage(MSG_KEY), 
                "117: " + getCheckMessage(MSG_KEY),
                "128: " + getCheckMessage(MSG_KEY),
                "140: " + getCheckMessage(MSG_KEY),
                "151: " + getCheckMessage(MSG_KEY),
                "181: " + getCheckMessage(MSG_KEY)
        };

        verify(checkConfig, getPath("InputRedundantReturn.java"), expected);

    }
    
    @Test
    public void testForNullPointerExceptionsPresence()
            throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(RedundantReturnCheck.class);

        final String[] expected = { 
                
        };

        verify(checkConfig, "src/test/resources-noncompilable/com/github/sevntu/checkstyle/checks/"
        		+ "coding/InputRedundantReturnTestNPE.java", expected);

    }
    
    @Test
    public void testForNullPointerExceptionsInInterface()
            throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(RedundantReturnCheck.class);

        final String[] expected = { 
                
        };

        verify(checkConfig, getPath("InputRedundantReturnMethodInInterface.java"), expected);

    }
    
    @Test
    public void testSomeFalsePositiveCase()
            throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(RedundantReturnCheck.class);

        final String[] expected = { 
                
        };

        verify(checkConfig, getPath("InputRedundantReturnFalsePositive.java"), expected);

    }

}