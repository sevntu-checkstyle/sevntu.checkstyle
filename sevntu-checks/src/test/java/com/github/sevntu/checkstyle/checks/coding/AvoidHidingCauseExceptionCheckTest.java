package com.github.sevntu.checkstyle.checks.coding;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.github.sevntu.checkstyle.checks.coding.AvoidHidingCauseExceptionCheck;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import org.junit.Test;

public class AvoidHidingCauseExceptionCheckTest extends BaseCheckTestSupport {
    final String message = "Cause exception 'e' was lost.";

    @Test
    public final void test() throws Exception {
        DefaultConfiguration checkConfig = createCheckConfig(AvoidHidingCauseExceptionCheck.class);

        String[] expected = {
                "18:13: " + message,
                "23:13: " + message,
                "38:13: " + message,
                "55:13: " + message,
                "65:13: " + message,
                "85:13: " + message,
                "103:13: " + message,
                "110:17: Cause exception 'n' was lost.",
                "123:21: Cause exception 'x' was lost.",
                "129:13: " + message,
                "142:13: " + message,
                "152:13: " + message, };

        verify(checkConfig, getPath("InputAvoidHidingCauseExceptionCheck.java"), expected);
    }

}
