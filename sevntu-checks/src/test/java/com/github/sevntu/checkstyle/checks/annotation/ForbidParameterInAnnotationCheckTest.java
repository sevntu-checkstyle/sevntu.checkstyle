package com.github.sevntu.checkstyle.checks.annotation;

import static com.github.sevntu.checkstyle.checks.annotation.ForbidParameterInAnnotationCheck.*;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class ForbidParameterInAnnotationCheckTest extends BaseCheckTestSupport
{
    @Test
    public void testAnnotationWithStringParameter()
            throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(ForbidParameterInAnnotationCheck.class);
        checkConfig.addAttribute("annotationName", "Anno1");
        checkConfig.addAttribute("parameterName", "str");
        checkConfig.addAttribute("parameterValueRegexp", "someString\\d+");

        String[] expected = { "45:18: " + getCheckMessage(MSG_KEY, "str", "Anno1") };

        verify(checkConfig, getPath("ForbidParameterInAnnotationCheckInput.java"), expected);
    }

    @Test
    public void testAnnotationWithIntegerParameter()
            throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(ForbidParameterInAnnotationCheck.class);

        checkConfig.addAttribute("annotationName", "Anno2");
        checkConfig.addAttribute("parameterName", "intVal");
        checkConfig.addAttribute("parameterValueRegexp", "[1-5]");

        String[] expected = { "50:21: " + getCheckMessage(MSG_KEY, "intVal", "Anno2") };

        verify(checkConfig, getPath("ForbidParameterInAnnotationCheckInput.java"), expected);
    }

    @Test
    public void testAnnotationWithFloatParameter()
            throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(ForbidParameterInAnnotationCheck.class);

        checkConfig.addAttribute("annotationName", "Anno3");
        checkConfig.addAttribute("parameterName", "floatVal");
        checkConfig.addAttribute("parameterValueRegexp", "2\\.\\d+f");

        String[] expected = { "55:23: " + getCheckMessage(MSG_KEY, "floatVal", "Anno3") };

        verify(checkConfig, getPath("ForbidParameterInAnnotationCheckInput.java"), expected);
    }

    @Test
    public void testAnnotationWtithBooleanParameter()
            throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(ForbidParameterInAnnotationCheck.class);

        checkConfig.addAttribute("annotationName", "Anno4");
        checkConfig.addAttribute("parameterName", "boolVal");
        checkConfig.addAttribute("parameterValueRegexp", "true");

        String[] expected = { "61:22: " + getCheckMessage(MSG_KEY, "boolVal", "Anno4") };

        verify(checkConfig, getPath("ForbidParameterInAnnotationCheckInput.java"), expected);
    }

    @Test
    public void testAnnotationWithDotSplittedParameter()
            throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(ForbidParameterInAnnotationCheck.class);

        checkConfig.addAttribute("annotationName", "Bean");
        checkConfig.addAttribute("parameterName", "name");
        checkConfig.addAttribute("parameterValueRegexp",
                "AnnotationConfigUtils\\.[A-Z_]+");

        String[] expected = { "60:11: " + getCheckMessage(MSG_KEY, "name", "Bean") };

        verify(checkConfig, getPath("ForbidParameterInAnnotationCheckInput.java"), expected);
    }

    @Test
    public void testAnnotationWithSeveralParameters()
            throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(ForbidParameterInAnnotationCheck.class);

        checkConfig.addAttribute("annotationName", "Anno5");
        checkConfig.addAttribute("parameterName", "stringValue");
        checkConfig.addAttribute("parameterValueRegexp", "[a-z]+111String");

        String[] expected = { "68:61: " + getCheckMessage(MSG_KEY, "stringValue", "Anno5") };

        verify(checkConfig, getPath("ForbidParameterInAnnotationCheckInput.java"), expected);
    }

    @Test
    public void testAnnotationWithUnnamedParameter()
            throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(ForbidParameterInAnnotationCheck.class);

        checkConfig.addAttribute("annotationName", "SuppressWarnings");
        checkConfig.addAttribute("parameterName", "");
        checkConfig.addAttribute("parameterValueRegexp", "unchecked");

        String[] expected = { "67:23: " + getCheckMessage(MSG_KEY, "value", "SuppressWarnings") };

        verify(checkConfig, getPath("ForbidParameterInAnnotationCheckInput.java"), expected);
    }
}
