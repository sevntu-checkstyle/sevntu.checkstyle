package com.puppycrawl.tools.checkstyle.checks.coding;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import java.io.File;
import org.junit.Test;

public class AvoidHidingCauseExceptionCheckTest extends BaseCheckTestSupport {
    final String message = "Cause exception 'e' was lost.";
    @Test
    public final void testAll() throws Exception {
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
                //"/media/Data/Work/sevntu.checkstyle/sevntu.checkstyle/src/testinputs/com/puppycrawl/tools/checkstyle/coding");
                "/home/developer/Daniil Yaroslavtsev/sevntu.checkstyle/sevntu.checkstyle/src/testinputs/com/puppycrawl/tools/checkstyle/coding");
        //verify(checkConfig, getPath("coding" + File.separator + "InputAvoidHidingCauseExceptionCheck.java"), expected);
        verify(checkConfig,
                getPath("InputAvoidHidingCauseExceptionCheck.java"), expected);
    }

}
