package com.github.sevntu.checkstyle.checks.coding;

import static com.github.sevntu.checkstyle.checks.coding.UselessSuperCtorCallCheck.*;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class UselessSuperCtorCallCheckTest extends BaseCheckTestSupport
{
    private final String msgSuperWithoutArgs = getCheckMessage(MSG_KEY_SUPER_CALL_WITHOUT_ARGS);

    private final DefaultConfiguration mDefaultConfig = createCheckConfig(UselessSuperCtorCallCheck.class);

    @Test
    public void testSingleCtorWithSuperWithinNotDerivedClass()
            throws Exception
    {
        final String expected[] = {
                "7: " + getCheckMessage(MSG_KEY_SUPER_CALL_IN_NOT_DERIVED_CLASS,
                            "InputUselessSuperCtorCall1"),
        };

        verify(mDefaultConfig, getPath("InputUselessSuperCtorCall1.java"), expected);
    }

    @Test
    public void testSingleCtorWithSuperWithinDerivedClass()
            throws Exception
    {
        final String expected[] = {
                "7: " + msgSuperWithoutArgs,
        };

        verify(mDefaultConfig, getPath("InputUselessSuperCtorCall2.java"), expected);
    }

    @Test
    public void testMultipleCtorsWithSuperWithinNonsubclass()
            throws Exception
    {
        final String expected[] = {
                "7: " + getCheckMessage(MSG_KEY_SUPER_CALL_IN_NOT_DERIVED_CLASS,
                            "InputUselessSuperCtorCall3"),
                "12: " + getCheckMessage(MSG_KEY_SUPER_CALL_IN_NOT_DERIVED_CLASS,
                            "InputUselessSuperCtorCall3"),
        };

        verify(mDefaultConfig, getPath("InputUselessSuperCtorCall3.java"), expected);
    }

    @Test
    public void testInnerClassWithCtorWithSuper()
            throws Exception
    {
        final String expected[] = {
                "9: " + getCheckMessage(MSG_KEY_SUPER_CALL_IN_NOT_DERIVED_CLASS,
                            "Inner"),
        };

        verify(mDefaultConfig, getPath("InputUselessSuperCtorCall4.java"), expected);
    }

    @Test
    public void testClassWithSuperCtorWithArgs()
            throws Exception
    {
        final String expected[] = {};

        verify(mDefaultConfig, getPath("InputUselessSuperCtorCall5.java"), expected);
    }

    @Test
    public void testClassWithSuperCtorWithoutArgsAndIgnoreSuperCtorCallOptionSetToTrue()
            throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(UselessSuperCtorCallCheck.class);
        checkConfig.addAttribute("ignoreSuperCtorCallWithoutArgs", "true");

        final String expected[] = {};

        verify(checkConfig, getPath("InputUselessSuperCtorCall6.java"), expected);
    }
}
