package com.puppycrawl.tools.checkstyle.checks.coding;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import java.io.File;
import org.junit.Test;

public class AvoidNotShortCircuitOperatorsForBooleanCheckTest extends BaseCheckTestSupport {


    @Test
    public final void testAll() throws Exception {
        DefaultConfiguration checkConfig = createCheckConfig(AvoidNotShortCircuitOperatorsForBooleanCheck.class);

        String[] expected = {
            "5:17: "+createMessage("|"),
            "9:25: "+createMessage("|"),
        //  "11:15: "+createMessage("|"),
            "24:20: "+createMessage("|"),
            "34:25: "+createMessage("|"),
            "47:25: "+createMessage("|"),
            "52:16: "+createMessage("&"),
                };

        System.setProperty(
                "testinputs.dir",
              //  "/media/B32C-8EF7/sevntu.checkstyle/sevntu.checkstyle/src/testinputs/com/puppycrawl/tools/checkstyle/coding");
             //   "/media/data/Work/sevntu.checkstyle/sevntu.checkstyle/src/testinputs/com/puppycrawl/tools/checkstyle/coding");
                "/home/developer/sevntu.checkstyle/src/testinputs/com/puppycrawl/tools/checkstyle/coding");
                //verify(checkConfig, getPath("coding" + File.separator + "InputAvoidNotShortCircuitOperatorsForBooleanCheck1.java"), expected);
        verify(checkConfig,
                getPath("InputAvoidNotShortCircuitOperatorsForBooleanCheck.java"), expected);
    }

    public String createMessage(String literal){
        return "Not short-circuit Operator '" + literal + "' used.";      
    }
    
}
