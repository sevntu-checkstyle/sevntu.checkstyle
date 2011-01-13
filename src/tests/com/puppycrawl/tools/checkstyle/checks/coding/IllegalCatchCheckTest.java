package com.puppycrawl.tools.checkstyle.checks.coding;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import java.io.File;
import org.junit.Test;

public class IllegalCatchCheckTest extends BaseCheckTestSupport
{
    @Test
    public void testDefault() throws Exception
    {
        DefaultConfiguration checkConfig = createCheckConfig(IllegalCatchCheck.class);

        String[] expected = {
            "9:9: Catching 'RuntimeException' is not allowed.",
            "11:9: Catching 'Exception' is not allowed.",
            "13:9: Catching 'Throwable' is not allowed.",
                
//          "24:9: Catching 'java.lang.RuntimeException' is not allowed.",
//          "29:9: Catching 'java.lang.Exception' is not allowed.",
//          "34:9: Catching 'java.lang.Throwable' is not allowed.",
        };
       
      //  System.setProperty("testinputs.dir", "/home/developer/Daniil Yaroslavtsev/sevntu.checkstyle/sevntu.checkstyle/src/testinputs/com/puppycrawl/tools/checkstyle/coding");
        System.setProperty("testinputs.dir", "/media/Data/Work/sevntu.checkstyle/sevntu.checkstyle/src/testinputs/com/puppycrawl/tools/checkstyle/coding");
        //verify(checkConfig, getPath("coding" + File.separator + "InputIllegalCatchCheck.java"), expected);
        verify(checkConfig,getPath("InputIllegalCatchCheck.java"),expected);
    }

    @Test
    public void testIllegalClassNames() throws Exception
    {
        DefaultConfiguration checkConfig = createCheckConfig(IllegalCatchCheck.class);
        checkConfig.addAttribute("illegalClassNames",
                                 "java.lang.Error, java.lang.Exception, java.lang.Throwable");

        String[] expected = {
            "11:9: Catching 'Exception' is not allowed.",
            "13:9: Catching 'Throwable' is not allowed.",
                
         // "29:9: Catching 'java.lang.Exception' is not allowed.",
         // "34:9: Catching 'java.lang.Throwable' is not allowed.",
        };

       // System.setProperty("testinputs.dir", "/home/developer/Daniil Yaroslavtsev/sevntu.checkstyle/sevntu.checkstyle/src/testinputs/com/puppycrawl/tools/checkstyle/coding");
        System.setProperty("testinputs.dir", "/media/Data/Work/sevntu.checkstyle/sevntu.checkstyle/src/testinputs/com/puppycrawl/tools/checkstyle/coding");
        
        //verify(checkConfig, getPath("coding" + File.separator + "InputIllegalCatchCheck.java"), expected);
        verify(checkConfig,getPath("InputIllegalCatchCheck.java"),expected);
    }
}