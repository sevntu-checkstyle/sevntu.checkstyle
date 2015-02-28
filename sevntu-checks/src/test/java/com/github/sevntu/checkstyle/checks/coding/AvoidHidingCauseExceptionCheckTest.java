package com.github.sevntu.checkstyle.checks.coding;

import static com.github.sevntu.checkstyle.checks.coding.AvoidHidingCauseExceptionCheck.MSG_KEY;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import org.junit.Test;

public class AvoidHidingCauseExceptionCheckTest extends BaseCheckTestSupport {

    @Test
    public final void issue52_test() throws Exception {
        DefaultConfiguration checkConfig = createCheckConfig(AvoidHidingCauseExceptionCheck.class);

        String[] expected = {};

        verify(checkConfig, getPath("InputAvoidHidingCauseExceptionCheck2.java"), expected);
    }
    
    @Test
    public final void test() throws Exception {
        DefaultConfiguration checkConfig = createCheckConfig(AvoidHidingCauseExceptionCheck.class);

        String[] expected = {
                "18:13: " + getCheckMessage(MSG_KEY, "e"),
                "23:13: " + getCheckMessage(MSG_KEY, "e"),
                "38:13: " + getCheckMessage(MSG_KEY, "e"),
                "55:21: " + getCheckMessage(MSG_KEY, "e"),
                "65:17: " + getCheckMessage(MSG_KEY, "e"),
                "85:17: " + getCheckMessage(MSG_KEY, "e"),
                "103:13: " + getCheckMessage(MSG_KEY, "e"),
                "110:17: " + getCheckMessage(MSG_KEY, "n"),
                "123:21: " + getCheckMessage(MSG_KEY, "x"),
                "129:13: " + getCheckMessage(MSG_KEY, "e"),
                "142:13: " + getCheckMessage(MSG_KEY, "e"),
                "152:13: " + getCheckMessage(MSG_KEY, "e"), 
                "197:13: " + getCheckMessage(MSG_KEY, "fakeException"), 
                "216:13: " + getCheckMessage(MSG_KEY, "e"), 
                "221:13: " + getCheckMessage(MSG_KEY, "e"), 
                "226:13: " + getCheckMessage(MSG_KEY, "e"), 
                };

        verify(checkConfig, getPath("InputAvoidHidingCauseExceptionCheck.java"), expected);
    }

}

