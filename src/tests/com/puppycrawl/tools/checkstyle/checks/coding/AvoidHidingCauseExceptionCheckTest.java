package com.puppycrawl.tools.checkstyle.checks.coding;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import java.io.File;
import org.junit.Test;

public class AvoidHidingCauseExceptionCheckTest extends BaseCheckTestSupport {
    final String message = "Cause exception 'e' was lost.";
    @Test
    public final void testSimple() throws Exception {
        DefaultConfiguration checkConfig = createCheckConfig(AvoidHidingCauseExceptionCheck.class);

        String[] expected = {
                "18:13: "+message,
                "23:13: "+message,
                "38:13: "+message,
                "64:13: "+message,
                "74:13: "+message,
                "94:13: "+message,
                };

        System.setProperty(
                "testinputs.dir",
                "/media/Data/Work/sevntu.checkstyle/sevntu.checkstyle/src/testinputs/com/puppycrawl/tools/checkstyle/coding");
               // "/home/developer/Daniil Yaroslavtsev/sevntu.checkstyle/sevntu.checkstyle/src/testinputs/com/puppycrawl/tools/checkstyle/coding");
        //verify(checkConfig, getPath("coding" + File.separator + "InputAvoidHidingCauseExceptionCheck.java"), expected);
        //verify(checkConfig,
       //         getPath("InputAvoidHidingCauseExceptionCheck.java"), expected);
    }

    @Test
    public final void testNestedAndFixed() throws Exception {
        DefaultConfiguration checkConfig = createCheckConfig(AvoidHidingCauseExceptionCheck.class);

        String[] expected = {
//                  "16:13: Cause exception 'e' was lost.",
//                  "23:17: Cause exception 'n' was lost.",
//                  "34:21: Cause exception 'x' was lost.",               
                  "51:13: Cause exception 'e' was lost.", 
                  "59:17: Cause exception 'e' was lost.", 
                  "61:13: Cause exception 'e' was lost.", 
                };

        System.setProperty(
                "testinputs.dir",
                "/media/Data/Work/sevntu.checkstyle/sevntu.checkstyle/src/testinputs/com/puppycrawl/tools/checkstyle/coding");
               // "/home/developer/Daniil Yaroslavtsev/sevntu.checkstyle/sevntu.checkstyle/src/testinputs/com/puppycrawl/tools/checkstyle/coding");
        //verify(checkConfig, getPath("coding" + File.separator + "InputAvoidHidingCauseExceptionCheck.java"), expected);
        verify(checkConfig,
                getPath("InputAvoidHidingCauseExceptionCheck1.java"), expected);
    }
    
}
