package com.github.sevntu.checkstyle.checks.coding;

import static com.github.sevntu.checkstyle.checks.coding.IllegalCatchExtendedCheck.*;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.github.sevntu.checkstyle.checks.coding.IllegalCatchExtendedCheck;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import org.junit.Test;

public class IllegalCatchExtendedCheckTest extends BaseCheckTestSupport
{
    
    private final DefaultConfiguration checkConfig = createCheckConfig(IllegalCatchExtendedCheck.class);
    
    @Test
    public final void testDefault() throws Exception
    {        
        String[] expected = {
        	"9:9: " + getCheckMessage(MSG_KEY, "RuntimeException"),
        	"11:9: " + getCheckMessage(MSG_KEY, "java.lang.Exception"),
        	"13:9: " + getCheckMessage(MSG_KEY, "Throwable"),
        	"24:9: " + getCheckMessage(MSG_KEY, "RuntimeException"),
        	"29:9: " + getCheckMessage(MSG_KEY, "java.lang.Exception"),
        	"34:9: " + getCheckMessage(MSG_KEY, "Throwable"),
        };

        checkConfig.addAttribute("allowThrow", "false");
        checkConfig.addAttribute("allowRethrow", "false");
        
        verify(checkConfig,getPath("InputIllegalCatchCheckNew.java"),expected);
    }

    @Test
    public final void testThrowPermit() throws Exception
    {

        String[] expected = {
        	"9:9: " + getCheckMessage(MSG_KEY, "RuntimeException"),
           	"11:9: " + getCheckMessage(MSG_KEY, "java.lang.Exception"),
           	"13:9: " + getCheckMessage(MSG_KEY, "Throwable"),
//         	"24:9: " + getCheckMessage(MSG_KEY, "RuntimeException"),
           	"29:9: " + getCheckMessage(MSG_KEY, "java.lang.Exception"),
           	"34:9: " + getCheckMessage(MSG_KEY, "Throwable"),
        };

        checkConfig.addAttribute("allowThrow", "true");
        checkConfig.addAttribute("allowRethrow", "false");
        
        verify(checkConfig,getPath("InputIllegalCatchCheckNew.java"),expected);
    }
    
    @Test
    public final void testReThrowPermit() throws Exception
    {
        checkConfig.addAttribute("illegalClassNames",
                                 "java.lang.Error, java.lang.Exception, java.lang.Throwable");

        String[] expected = {
//     		"9:9: " + getCheckMessage(MSG_KEY, "RuntimeException"),
           	"11:9: " + getCheckMessage(MSG_KEY, "java.lang.Exception"),
           	"13:9: " + getCheckMessage(MSG_KEY, "Throwable"),
//         	"24:9: " + getCheckMessage(MSG_KEY, "RuntimeException"),
//          "29:9: " + getCheckMessage(MSG_KEY, "java.lang.Exception"),
//          "34:9: " + getCheckMessage(MSG_KEY, "Throwable"),
        };

        checkConfig.addAttribute("allowThrow", "false");
        checkConfig.addAttribute("allowRethrow", "true");

        verify(checkConfig,getPath("InputIllegalCatchCheckNew.java"),expected);
    }

}