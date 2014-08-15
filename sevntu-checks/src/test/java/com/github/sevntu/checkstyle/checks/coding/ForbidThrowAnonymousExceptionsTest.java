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
    private final DefaultConfiguration checkConfig =
        createCheckConfig(ForbidThrowAnonymousExceptionsCheck.class);

    @Test
    public final void anonymousExceptionTest() throws Exception
    {

        final String[] expected = {
        		"8: " + getCheckMessage(MSG_KEY)
            };

        verify(checkConfig,
                getPath("InputForbidThrowAnonymousExceptions.java"), expected);
    }
    
    @Test
    public final void preDefinedAnonymousExceptionTest() throws Exception
    {

        final String[] expected = {
                "8: " + getCheckMessage(MSG_KEY),
                "30: " + getCheckMessage(MSG_KEY)
            };

        verify(checkConfig,
                getPath("InputForbidThrowAnonymousExceptions2.java"), expected);
    }
    
    @Test
    public final void sameNameExceptionsTest() throws Exception
    {

        final String[] expected = {
                "12: " + getCheckMessage(MSG_KEY),
                "23: " + getCheckMessage(MSG_KEY),
                "66: " + getCheckMessage(MSG_KEY),
                "88: " + getCheckMessage(MSG_KEY),
                "107: " + getCheckMessage(MSG_KEY),
                "127: " + getCheckMessage(MSG_KEY)
            };

        verify(checkConfig,
                getPath("InputForbidThrowAnonymousExceptions3.java"), expected);
    }
    
    @Test
    public final void nonStandardExceptionClassNameTest() throws Exception
    {
        String exceptionNameRegex = "^.*bla";
        checkConfig.addAttribute("exceptionClassNameRegex", exceptionNameRegex);
        final String[] expected = {
                "10: " + getCheckMessage(MSG_KEY),
                "12: " + getCheckMessage(MSG_KEY)
            };

        verify(checkConfig,
                getPath("InputForbidThrowAnonymousExceptionsAnotherClassName.java"), expected);
    }
    
}