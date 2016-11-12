package com.github.sevntu.checkstyle.checks.annotation;

import static com.github.sevntu.checkstyle.checks.annotation.ForbidAnnotationElementValueCheck.MSG_KEY;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class ForbidAnnotationElementValueCheckTest extends BaseCheckTestSupport {
    @Test
    public void testAnnotationWithStringParameter() throws Exception {
        final DefaultConfiguration checkConfig =
                createCheckConfig(ForbidAnnotationElementValueCheck.class);
        checkConfig.addAttribute("annotationName", "Anno1");
        checkConfig.addAttribute("elementName", "str");
        checkConfig.addAttribute("forbiddenElementValueRegexp", "someString\\d+");

        String[] expected = {
            "47:12: " + getCheckMessage(MSG_KEY, "str", "Anno1")
        };

        verify(checkConfig, getPath("ForbidAnnotationElementValueCheckInput.java"), expected);
    }

    @Test
    public void testAnnotationWithIntegerParameter() throws Exception {
        final DefaultConfiguration checkConfig =
                createCheckConfig(ForbidAnnotationElementValueCheck.class);

        checkConfig.addAttribute("annotationName", "Anno2");
        checkConfig.addAttribute("elementName", "intVal");
        checkConfig.addAttribute("forbiddenElementValueRegexp", "[1-5]");

        String[] expected = {
            "52:12: " + getCheckMessage(MSG_KEY, "intVal", "Anno2")
        };

        verify(checkConfig, getPath("ForbidAnnotationElementValueCheckInput.java"), expected);
    }

    @Test
    public void testAnnotationWithFloatParameter() throws Exception {
        final DefaultConfiguration checkConfig =
                createCheckConfig(ForbidAnnotationElementValueCheck.class);

        checkConfig.addAttribute("annotationName", "Anno3");
        checkConfig.addAttribute("elementName", "floatVal");
        checkConfig.addAttribute("forbiddenElementValueRegexp", "2\\.\\d+f");

        String[] expected = {
            "57:12: " + getCheckMessage(MSG_KEY, "floatVal", "Anno3")
        };

        verify(checkConfig, getPath("ForbidAnnotationElementValueCheckInput.java"), expected);
    }

    @Test
    public void testAnnotationWithBooleanParameter() throws Exception {
        final DefaultConfiguration checkConfig =
                createCheckConfig(ForbidAnnotationElementValueCheck.class);

        checkConfig.addAttribute("annotationName", "Anno4");
        checkConfig.addAttribute("elementName", "boolVal");
        checkConfig.addAttribute("forbiddenElementValueRegexp", "true");

        String[] expected = {
            "63:12: " + getCheckMessage(MSG_KEY, "boolVal", "Anno4")
        };

        verify(checkConfig, getPath("ForbidAnnotationElementValueCheckInput.java"), expected);
    }

    @Test
    public void testAnnotationWithDotSplittedParameter() throws Exception {
        final DefaultConfiguration checkConfig =
                createCheckConfig(ForbidAnnotationElementValueCheck.class);

        checkConfig.addAttribute("annotationName", "Bean");
        checkConfig.addAttribute("elementName", "name");
        checkConfig.addAttribute("forbiddenElementValueRegexp", "AnnotationConfigUtils\\.[A-Z_]+");

        String[] expected = {
            "62:11: " + getCheckMessage(MSG_KEY, "name", "Bean")
        };

        verify(checkConfig, getPath("ForbidAnnotationElementValueCheckInput.java"), expected);
    }

    @Test
    public void testAnnotationWithSeveralParameters() throws Exception {
        final DefaultConfiguration checkConfig =
                createCheckConfig(ForbidAnnotationElementValueCheck.class);

        checkConfig.addAttribute("annotationName", "Anno5");
        checkConfig.addAttribute("elementName", "stringValue");
        checkConfig.addAttribute("forbiddenElementValueRegexp", "[a-z]+111String");

        String[] expected = {
            "69:47: " + getCheckMessage(MSG_KEY, "stringValue", "Anno5")
        };

        verify(checkConfig, getPath("ForbidAnnotationElementValueCheckInput.java"), expected);
    }

    @Test
    public void testAnnotationWithSingleParameter() throws Exception {
        final DefaultConfiguration checkConfig =
                createCheckConfig(ForbidAnnotationElementValueCheck.class);

        checkConfig.addAttribute("annotationName", "SuppressWarnings");
        checkConfig.addAttribute("elementName", "value");
        checkConfig.addAttribute("forbiddenElementValueRegexp", "unchecked");

        String[] expected = {
            "68:23: " + getCheckMessage(MSG_KEY, "value", "SuppressWarnings")
        };

        verify(checkConfig, getPath("ForbidAnnotationElementValueCheckInput.java"), expected);
    }

    @Test
    public void testAnnotationWtithBooleanParameterValueDoesntMatch() throws Exception {
        final DefaultConfiguration checkConfig =
                createCheckConfig(ForbidAnnotationElementValueCheck.class);

        checkConfig.addAttribute("annotationName", "Anno4");
        checkConfig.addAttribute("elementName", "boolVal");
        checkConfig.addAttribute("forbiddenElementValueRegexp", "false");

        String[] expected = {};

        verify(checkConfig, getPath("ForbidAnnotationElementValueCheckInput.java"), expected);
    }

    @Test
    public void testAnnotationWithListAsParameterValue() throws Exception {
        final DefaultConfiguration checkConfig =
                createCheckConfig(ForbidAnnotationElementValueCheck.class);

        checkConfig.addAttribute("annotationName", "SuppressWarnings");
        checkConfig.addAttribute("elementName", "value");
        checkConfig.addAttribute("forbiddenElementValueRegexp", "\\{.*\\}");

        String[] expected = {
            "74:23: " + getCheckMessage(MSG_KEY, "value", "SuppressWarnings"),
            "79:23: " + getCheckMessage(MSG_KEY, "value", "SuppressWarnings")
        };

        verify(checkConfig, getPath("ForbidAnnotationElementValueCheckInput.java"), expected);
    }

    @Test
    public void testAnnotationWithAnnotationAsParameter() throws Exception {
        final DefaultConfiguration checkConfig =
                createCheckConfig(ForbidAnnotationElementValueCheck.class);

        checkConfig.addAttribute("annotationName", "Name");
        checkConfig.addAttribute("elementName", "last");
        checkConfig.addAttribute("forbiddenElementValueRegexp", "Hacker");

        String[] expected = {
            "96:34: " + getCheckMessage(MSG_KEY, "last", "Name")
        };

        verify(checkConfig, getPath("ForbidAnnotationElementValueCheckInput.java"), expected);
    }

    @Test
    public void testAnnotationWithDefaultValues() throws Exception {
        final DefaultConfiguration checkConfig =
                createCheckConfig(ForbidAnnotationElementValueCheck.class);

        String[] expected = {
            "114:11: " + getCheckMessage(MSG_KEY, "expected", "Test")
        };

        verify(checkConfig, getPath("ForbidAnnotationElementValueCheckInput.java"), expected);
    }

    @Test
    public void testFullAnnotationClasspath() throws Exception {
        final DefaultConfiguration checkConfig =
                createCheckConfig(ForbidAnnotationElementValueCheck.class);

        checkConfig.addAttribute("annotationName", "SuppressWarnings");
        checkConfig.addAttribute("elementName", "value");
        checkConfig.addAttribute("forbiddenElementValueRegexp", "rawtypes");

        String[] expected = {
            "8:33: " + getCheckMessage(MSG_KEY, "value", "SuppressWarnings")
        };

        verify(checkConfig, getPath("ForbidAnnotationElementValueCheck2Input.java"), expected);
    }
}
