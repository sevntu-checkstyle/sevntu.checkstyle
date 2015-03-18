package com.github.sevntu.checkstyle.checks.coding;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import org.junit.Test;

import static com.github.sevntu.checkstyle.checks.coding.WhitespaceBeforeArrayInitializerCheck.MSG_KEY;

public class WhitespaceBeforeArrayInitializerCheckTest extends BaseCheckTestSupport
{
    private final DefaultConfiguration mDefaultConfig = createCheckConfig(WhitespaceBeforeArrayInitializerCheck.class);

    @Test
    public void testWhitespaceBeforeArrayInitializer() throws Exception {
        final String expected[] = {
                "5:28: " + getCheckMessage(MSG_KEY),
                "13:32: " + getCheckMessage(MSG_KEY),
                "16:32: " + getCheckMessage(MSG_KEY),
                "17:21: " + getCheckMessage(MSG_KEY)
        };
        verify(mDefaultConfig, getPath("InputWhitespaceBeforeArrayIntializer.java"), expected);
    }
}