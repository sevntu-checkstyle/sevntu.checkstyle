package com.puppycrawl.tools.checkstyle.checks.coding;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import java.io.File;
import org.junit.Test;

public class OverridableMethodInConstructorTest extends BaseCheckTestSupport {

    @Test
    public final void testWarning() throws Exception {
        DefaultConfiguration checkConfig = createCheckConfig(OverridableMethodInConstructorCheck.class);

        String[] expected = { "9:27: " + createMsg("overrideMe") };

        System.setProperty("testinputs.dir",
        "/media/data/Work/sevntu.checkstyle/sevntu.checkstyle/sevntu.checkstyle/src/testinputs/com/puppycrawl/tools/checkstyle");

                verify(checkConfig, getPath("coding" + File.separator + "InputOverridableMethodInConstructor.java"), expected);
        //  verify(checkConfig,
        //  getPath("InputAvoidNotShortCircuitOperatorsForBooleanCheck.java"), expected);
    }

    @Test
    public final void testNoWarnings() throws Exception {
        DefaultConfiguration checkConfig = createCheckConfig(OverridableMethodInConstructorCheck.class);

        String[] expected = {
        };

        System.setProperty("testinputs.dir",
        "/media/data/Work/sevntu.checkstyle/sevntu.checkstyle/sevntu.checkstyle/src/testinputs/com/puppycrawl/tools/checkstyle");

                verify(checkConfig, getPath("coding" + File.separator + "InputOverridableMethodInConstructor1.java"), expected);
        //  verify(checkConfig,
        //  getPath("InputAvoidNotShortCircuitOperatorsForBooleanCheck.java"), expected);
    }
    
    @Test
    public final void testWarning2() throws Exception {
        DefaultConfiguration checkConfig = createCheckConfig(OverridableMethodInConstructorCheck.class);

        String[] expected = { "10:27: " + createMsg("overrideMe") };

        System.setProperty("testinputs.dir",
        "/media/data/Work/sevntu.checkstyle/sevntu.checkstyle/sevntu.checkstyle/src/testinputs/com/puppycrawl/tools/checkstyle");

                verify(checkConfig, getPath("coding" + File.separator + "InputOverridableMethodInConstructor2.java"), expected);
        //  verify(checkConfig,
        //  getPath("InputAvoidNotShortCircuitOperatorsForBooleanCheck.java"), expected);
    }
    
    @Test
    public final void testWarnings() throws Exception {
        DefaultConfiguration checkConfig = createCheckConfig(OverridableMethodInConstructorCheck.class);

        String[] expected = { 
                "10:27: " + createMsg("overrideMe"),
                "15:27: " + createMsg("overrideMe")
                };

        System.setProperty("testinputs.dir",
        "/media/data/Work/sevntu.checkstyle/sevntu.checkstyle/sevntu.checkstyle/src/testinputs/com/puppycrawl/tools/checkstyle");

                verify(checkConfig, getPath("coding" + File.separator + "InputOverridableMethodInConstructor3.java"), expected);
        //  verify(checkConfig,
        //  getPath("InputAvoidNotShortCircuitOperatorsForBooleanCheck.java"), expected);
    }
    
    
    public String createMsg(String methodName){
        return "Overridable method '" + methodName + "' called in constructor.";
    }

}