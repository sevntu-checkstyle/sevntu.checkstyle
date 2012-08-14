package com.github.sevntu.checkstyle.checks.coding;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.github.sevntu.checkstyle.checks.coding.IllegalCatchCheck;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import java.io.File;
import org.junit.Test;

public class IllegalCatchCheckTest extends BaseCheckTestSupport
{
    
    private final DefaultConfiguration checkConfig = createCheckConfig(IllegalCatchCheck.class);
    
    @Test
    public final void testDefault() throws Exception
    {        
        String[] expected = {
            "9:9: Catching 'RuntimeException' is not allowed.",
            "11:9: Catching 'java.lang.Exception' is not allowed.",
            "13:9: Catching 'Throwable' is not allowed.",       
            "24:9: Catching 'RuntimeException' is not allowed.",
            "29:9: Catching 'java.lang.Exception' is not allowed.",
            "34:9: Catching 'Throwable' is not allowed.",
        };

        checkConfig.addAttribute("allowThrow", "false");
        checkConfig.addAttribute("allowRethrow", "false");
        
        verify(checkConfig,getPath("coding" + File.separator
                + "InputIllegalCatchCheckNew.java"),expected);
    }

    @Test
    public final void testThrowPermit() throws Exception
    {

        String[] expected = {
            "9:9: Catching 'RuntimeException' is not allowed.",
            "11:9: Catching 'java.lang.Exception' is not allowed.",
            "13:9: Catching 'Throwable' is not allowed.",
//          "24:9: Catching 'RuntimeException' is not allowed.",
            "29:9: Catching 'java.lang.Exception' is not allowed.",
            "34:9: Catching 'Throwable' is not allowed.",
        };

        checkConfig.addAttribute("allowThrow", "true");
        checkConfig.addAttribute("allowRethrow", "false");
        
        verify(checkConfig,getPath("coding" + File.separator
                + "InputIllegalCatchCheckNew.java"),expected);
    }
    
    @Test
    public final void testReThrowPermit() throws Exception
    {
        checkConfig.addAttribute("illegalClassNames",
                                 "java.lang.Error, java.lang.Exception, java.lang.Throwable");

        String[] expected = {
//          "9:9: Catching 'RuntimeException' is not allowed.",
            "11:9: Catching 'java.lang.Exception' is not allowed.",
            "13:9: Catching 'Throwable' is not allowed.",
//          "24:9: Catching 'RuntimeException' is not allowed.",
//          "29:9: Catching 'java.lang.Exception' is not allowed.",
//          "34:9: Catching 'Throwable' is not allowed.",
        };

        checkConfig.addAttribute("allowThrow", "false");
        checkConfig.addAttribute("allowRethrow", "true");

        verify(checkConfig,getPath("coding" + File.separator
                + "InputIllegalCatchCheckNew.java"),expected);
    }

}