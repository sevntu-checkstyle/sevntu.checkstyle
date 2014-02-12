package com.github.sevntu.checkstyle.checks.coding;

import static com.github.sevntu.checkstyle.checks.coding.FinalizeImplementationCheck.*;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class FinalizeImplementationCheckTest extends BaseCheckTestSupport
{

    /**
     * Default check configuration
     */
    private final DefaultConfiguration mCheckConfig =
        createCheckConfig(FinalizeImplementationCheck.class);

    @Test
    public final void basicTest() throws Exception
    {

        final String[] expected = {
                "21: " + getCheckMessage(MSG_KEY_MISSED_TRY_FINALLY),
                "34: " + getCheckMessage(MSG_KEY_MISSED_TRY_FINALLY),
                "47: " + getCheckMessage(MSG_KEY_PUBLIC_FINALIZE),
                "61: " + getCheckMessage(MSG_KEY_USELESS_FINALIZE),
                "74: " + getCheckMessage(MSG_KEY_MISSED_SUPER_FINALIZE_CALL),
            };

        verify(mCheckConfig,
                getPath("InputFinalizeImplementationCheck.java"), expected);
    }
}