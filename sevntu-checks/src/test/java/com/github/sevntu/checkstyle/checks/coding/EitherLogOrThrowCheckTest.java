package com.github.sevntu.checkstyle.checks.coding;

import java.util.Properties;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

/**
 * Test for EitherLogOrThrowCheck.
 * @author <a href="mailto:barataliba@gmail.com">Baratali Izmailov</a>
 */
public class EitherLogOrThrowCheckTest extends BaseCheckTestSupport
{
    /**
     * Check message.
     */
    private static final String MESSAGE =
            getMessage(EitherLogOrThrowCheck.MSG_KEY);
    /**
     * File with messages.
     */
    private static final String PROP_FILE_NAME = "messages.properties";

    /**
     * Test.
     * @throws Exception
     *         check's exceptions
     */
    @Test
    public void test()
        throws Exception
    {
        final DefaultConfiguration checkConfig =
                createCheckConfig(EitherLogOrThrowCheck.class);
        checkConfig.addAttribute("loggerFullyQualifiedClassName",
                "org.slf4j.Logger");
        checkConfig.addAttribute("loggingMethodNames", "error, warn");
        final int[] lines = { 19, 31, 43, 82, 93, 102, 112, 124, 154, 164,
                207, 226, 236, 247, 257, };
        final String[] expected = new String[lines.length];
        for (int i = 0; i < lines.length; i++) {
            expected[i] = lines[i] + ": " + MESSAGE;
        }
        verify(checkConfig, getPath("InputEitherLogOrThrowCheck.java"),
                expected);
    }

    /**
     * Get error message from property file.
     * @param aKey
     *        key for error message
     * @return error message
     */
    private static String getMessage(final String aKey)
    {
        final Properties prop = new Properties();
        try {
            prop.load(EitherLogOrThrowCheck.class
                    .getResourceAsStream(PROP_FILE_NAME));
        }
        catch (Exception e) {
            return null;
        }
        return prop.getProperty(aKey);
    }
}
