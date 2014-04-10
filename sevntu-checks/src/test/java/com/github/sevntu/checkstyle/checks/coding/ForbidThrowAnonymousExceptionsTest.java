package com.github.sevntu.checkstyle.checks.coding;

import static com.github.sevntu.checkstyle.checks.coding.ForbidThrowAnonymousExceptionsCheck.*;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class ForbidThrowAnonymousExceptionsTest extends BaseCheckTestSupport
{

    /**
     * Default check configuration
     */
    private final DefaultConfiguration mCheckConfig =
        createCheckConfig(ForbidThrowAnonymousExceptionsCheck.class);

    @Test
    public final void basicTest() throws Exception
    {

        final String[] expected = {
        		"7: " + getCheckMessage(MSG_KEY),
            };

        verify(mCheckConfig,
                getPath("InputForbidThrowAnonymousExceptions.java"), expected);
    }
}