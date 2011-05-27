package com.github.sevntu.checkstyle.checks.coding;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import java.io.File;
import org.junit.Test;

public class AvoidNotShortCircuitOperatorsForBooleanCheckTest extends BaseCheckTestSupport {


    @Test
    public final void testAll() throws Exception {
        DefaultConfiguration checkConfig = createCheckConfig(AvoidNotShortCircuitOperatorsForBooleanCheck.class);

        String[] expected = {
            "6:17: "+createMsg("|"),
            "25:20: "+createMsg("|"),
            "35:25: "+createMsg("|"),
            "48:25: "+createMsg("|"),
            "53:16: "+createMsg("&"),
            
            "64:17: "+createMsg("|"),
            "71:9: "+createMsg("|"),
            "79:9: "+createMsg("|"),
            "88:17: "+createMsg("|"),
            
            "94:22: "+createMsg("|"),
            "95:14: "+createMsg("|"),
            "96:14: "+createMsg("|"),
            "97:11: "+createMsg("|="),
            
                };

                verify(checkConfig, getPath("coding" + File.separator + "InputAvoidNotShortCircuitOperatorsForBooleanCheck.java"), expected);
      //  verify(checkConfig,
        //        getPath("InputAvoidNotShortCircuitOperatorsForBooleanCheck.java"), expected);
    }

    public String createMsg(String literal){
        return "Not short-circuit Operator '" + literal + "' used.";
    }

}
