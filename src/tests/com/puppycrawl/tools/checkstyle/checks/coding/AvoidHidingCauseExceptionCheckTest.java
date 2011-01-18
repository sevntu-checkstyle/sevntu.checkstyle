package com.puppycrawl.tools.checkstyle.checks.coding;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import java.io.File;
import org.junit.Test;


public class AvoidHidingCauseExceptionCheckTest extends BaseCheckTestSupport
{
    @Test
    public final void test1() throws Exception
    {
        DefaultConfiguration checkConfig = createCheckConfig(AvoidHidingCauseExceptionCheck.class);

        String[] expected = { 
                "21:13: Avoid hiding cause exception 'e'."
        };

        System.setProperty("testinputs.dir", "/home/developer/Daniil Yaroslavtsev/sevntu.checkstyle/sevntu.checkstyle/src/testinputs/com/puppycrawl/tools/checkstyle/coding");
        
        //verify(checkConfig, getPath("coding" + File.separator + "InputIllegalCatchCheck.java"), expected);
        verify(checkConfig,getPath("InputAvoidHidingCauseExceptionCheck1.java"),expected);
    }

    @Test
    public final void test2() throws Exception
    {
        DefaultConfiguration checkConfig = createCheckConfig(AvoidHidingCauseExceptionCheck.class);

        String[] expected = { 
                "17:13: Avoid hiding cause exception 'e'."
        };

        System.setProperty("testinputs.dir", "/home/developer/Daniil Yaroslavtsev/sevntu.checkstyle/sevntu.checkstyle/src/testinputs/com/puppycrawl/tools/checkstyle/coding");
        
        //verify(checkConfig, getPath("coding" + File.separator + "InputIllegalCatchCheck.java"), expected);
        verify(checkConfig,getPath("InputAvoidHidingCauseExceptionCheck2.java"),expected);
    }
    
    @Test
    public final void test3() throws Exception
    {
        DefaultConfiguration checkConfig = createCheckConfig(AvoidHidingCauseExceptionCheck.class);

        String[] expected = { 
                "17:13: Avoid hiding cause exception 'e'."
        };

        System.setProperty("testinputs.dir", "/home/developer/Daniil Yaroslavtsev/sevntu.checkstyle/sevntu.checkstyle/src/testinputs/com/puppycrawl/tools/checkstyle/coding");
        
        //verify(checkConfig, getPath("coding" + File.separator + "InputIllegalCatchCheck.java"), expected);
        verify(checkConfig,getPath("InputAvoidHidingCauseExceptionCheck3.java"),expected);
    }
    
    @Test
    public final void test4() throws Exception
    {
        DefaultConfiguration checkConfig = createCheckConfig(AvoidHidingCauseExceptionCheck.class);

        String[] expected = { 
                "21:13: Avoid hiding cause exception 'e'."
        };

        System.setProperty("testinputs.dir", "/home/developer/Daniil Yaroslavtsev/sevntu.checkstyle/sevntu.checkstyle/src/testinputs/com/puppycrawl/tools/checkstyle/coding");
        
        //verify(checkConfig, getPath("coding" + File.separator + "InputIllegalCatchCheck.java"), expected);
        verify(checkConfig,getPath("InputAvoidHidingCauseExceptionCheck4.java"),expected);
    }
    
   

}
