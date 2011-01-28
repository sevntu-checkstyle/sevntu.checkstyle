package com.puppycrawl.tools.checkstyle.checks.coding;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import java.io.File;
import org.junit.Test;

public class AvoidNotShortCircuitOperatorsForBooleanCheckTest extends BaseCheckTestSupport {
    final String message1 = "Not short-circuit Operator '";
    final String message2 = "' used.";
    
    @Test
    public final void testAll() throws Exception {
        DefaultConfiguration checkConfig = createCheckConfig(AvoidNotShortCircuitOperatorsForBooleanCheck.class);

        String[] expected = {
            "5:17: "+message1+"|"+message2,
            "9:25: "+message1+"|"+message2,
        //  "10:23: "+message1+"|"+message2,
            "11:15: "+message1+"|"+message2,
            "15:12: "+message1+"|="+message2,
            "16:14: "+message1+"&"+message2
                };

        System.setProperty(
                "testinputs.dir",
                //"/media/Data/Work/sevntu.checkstyle/sevntu.checkstyle/src/testinputs/com/puppycrawl/tools/checkstyle/coding");
                "/home/developer/Daniil Yaroslavtsev/sevntu.checkstyle/sevntu.checkstyle/src/testinputs/com/puppycrawl/tools/checkstyle/coding");
        //verify(checkConfig, getPath("coding" + File.separator + "InputAvoidNotShortCircuitOperatorsForBooleanCheck1.java"), expected);
        verify(checkConfig,
                getPath("InputAvoidNotShortCircuitOperatorsForBooleanCheck1.java"), expected);
    }

}
