package com.puppycrawl.tools.checkstyle.checks.coding;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import java.io.File;
import org.junit.Test;

public class AvoidHidingCauseExceptionCheckTest extends BaseCheckTestSupport {
    @Test
    public final void testAll() throws Exception {
        DefaultConfiguration checkConfig = createCheckConfig(AvoidHidingCauseExceptionCheck.class);

        String[] expected = {
                "18:13: Lost cause of exception 'e'.",
                "23:13: Lost cause of exception 'e'.",
             // "38:13: Lost cause of exception 'e'.",
                "64:13: Lost cause of exception 'e'.",
                "74:13: Lost cause of exception 'e'.",
             // "23:13: Lost cause of exception 'e'."
                };

        System.setProperty(
                "testinputs.dir",
                "/media/Data/Work/sevntu.checkstyle/sevntu.checkstyle/src/testinputs/com/puppycrawl/tools/checkstyle/coding");

        //verify(checkConfig, getPath("coding" + File.separator + "InputIllegalCatchCheck.java"), expected);
        verify(checkConfig,
                getPath("InputAvoidHidingCauseExceptionCheck.java"), expected);
    }

}
