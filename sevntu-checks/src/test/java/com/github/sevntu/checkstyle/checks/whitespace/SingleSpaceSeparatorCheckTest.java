package com.github.sevntu.checkstyle.checks.whitespace;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import org.junit.Test;

public class SingleSpaceSeparatorCheckTest extends BaseCheckTestSupport {

    private final String message = getCheckMessage(SingleSpaceSeparatorCheck.MSG_KEY);

    @Test
    public void testNoSpaceErrors() throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(SingleSpaceSeparatorCheck.class);
        verify(checkConfig, getPath("InputSingleSpaceNoErrors.java"), new String[0]);
    }

    @Test
    public void testSpaceErrors() throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(SingleSpaceSeparatorCheck.class);
        checkConfig.addAttribute("validateCommentNodes", String.valueOf(true));
        String[] expected = {
            "1:9: " + message,
            "1:22: " + message,
            "4:8: " + message,
            "6:18: " + message,
            "6:42: " + message,
            "7:20: " + message,
            "8:11: " + message,
            "8:15: " + message,
            "11:3: " + message,
            "12:5: " + message,
            "13:7: " + message,
            "14:8: " + message,
            "17:13: " + message,
            "17:23: " + message,
            "17:32: " + message,
            "18:15: " + message,
            "18:22: " + message,
            "19:16: " + message,
            "19:23: " + message,
            "20:19: " + message,
            "21:21: " + message,
            "26:21: " + message,
            "26:27: " + message,
            "27:14: " + message,
            "27:23: " + message,
            "27:31: " + message,
            "27:46: " + message,
            "28:14: " + message,
            "28:22: " + message,
            "30:16: " + message,
            "30:33: " + message,
            "31:7: " + message,};

        verify(checkConfig, getPath("InputSingleSpaceErrors.java"), expected);
    }

    @Test
    public void testSpaceErrorsAroundComments() throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(SingleSpaceSeparatorCheck.class);
        checkConfig.addAttribute("validateCommentNodes", String.valueOf(true));
        String[] expected = {
            "5:10: " + message,
            "5:42: " + message,
            "6:13: " + message,
            "13:13: " + message,
            "13:20: " + message,
            "14:7: " + message,};

        verify(checkConfig, getPath("InputSingleSpaceComments.java"), expected);
    }

    @Test
    public void testSpaceErrorsIfCommentsIgnored() throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(SingleSpaceSeparatorCheck.class);
        String[] expected = {"13:13: " + message};

        verify(checkConfig, getPath("InputSingleSpaceComments.java"), expected);
    }
}
