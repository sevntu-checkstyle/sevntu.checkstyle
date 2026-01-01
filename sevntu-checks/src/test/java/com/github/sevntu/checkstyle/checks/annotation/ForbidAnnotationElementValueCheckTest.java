///////////////////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code and other text files for adherence to a set of rules.
// Copyright (C) 2001-2026 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
///////////////////////////////////////////////////////////////////////////////////////////////

package com.github.sevntu.checkstyle.checks.annotation;

import static com.github.sevntu.checkstyle.checks.annotation.ForbidAnnotationElementValueCheck.MSG_KEY;

import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class ForbidAnnotationElementValueCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/annotation";
    }

    @Test
    public void testAnnotationWithStringParameter() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidAnnotationElementValueCheck.class);
        checkConfig.addProperty("annotationName", "Anno1");
        checkConfig.addProperty("elementName", "str");
        checkConfig.addProperty("forbiddenElementValueRegexp", "someString\\d+");

        final String[] expected = {
            "47:12: " + getCheckMessage(MSG_KEY, "str", "Anno1"),
        };

        verify(checkConfig, getPath("InputForbidAnnotationElementValueCheck.java"), expected);
    }

    @Test
    public void testAnnotationWithIntegerParameter() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidAnnotationElementValueCheck.class);

        checkConfig.addProperty("annotationName", "Anno2");
        checkConfig.addProperty("elementName", "intVal");
        checkConfig.addProperty("forbiddenElementValueRegexp", "[1-5]");

        final String[] expected = {
            "52:12: " + getCheckMessage(MSG_KEY, "intVal", "Anno2"),
        };

        verify(checkConfig, getPath("InputForbidAnnotationElementValueCheck.java"), expected);
    }

    @Test
    public void testAnnotationWithFloatParameter() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidAnnotationElementValueCheck.class);

        checkConfig.addProperty("annotationName", "Anno3");
        checkConfig.addProperty("elementName", "floatVal");
        checkConfig.addProperty("forbiddenElementValueRegexp", "2\\.\\d+f");

        final String[] expected = {
            "57:12: " + getCheckMessage(MSG_KEY, "floatVal", "Anno3"),
        };

        verify(checkConfig, getPath("InputForbidAnnotationElementValueCheck.java"), expected);
    }

    @Test
    public void testAnnotationWithBooleanParameter() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidAnnotationElementValueCheck.class);

        checkConfig.addProperty("annotationName", "Anno4");
        checkConfig.addProperty("elementName", "boolVal");
        checkConfig.addProperty("forbiddenElementValueRegexp", "true");

        final String[] expected = {
            "63:12: " + getCheckMessage(MSG_KEY, "boolVal", "Anno4"),
        };

        verify(checkConfig, getPath("InputForbidAnnotationElementValueCheck.java"), expected);
    }

    @Test
    public void testAnnotationWithDotSplittedParameter() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidAnnotationElementValueCheck.class);

        checkConfig.addProperty("annotationName", "Bean");
        checkConfig.addProperty("elementName", "name");
        checkConfig.addProperty("forbiddenElementValueRegexp", "AnnotationConfigUtils\\.[A-Z_]+");

        final String[] expected = {
            "62:11: " + getCheckMessage(MSG_KEY, "name", "Bean"),
        };

        verify(checkConfig, getPath("InputForbidAnnotationElementValueCheck.java"), expected);
    }

    @Test
    public void testAnnotationWithSeveralParameters() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidAnnotationElementValueCheck.class);

        checkConfig.addProperty("annotationName", "Anno5");
        checkConfig.addProperty("elementName", "stringValue");
        checkConfig.addProperty("forbiddenElementValueRegexp", "[a-z]+111String");

        final String[] expected = {
            "69:47: " + getCheckMessage(MSG_KEY, "stringValue", "Anno5"),
        };

        verify(checkConfig, getPath("InputForbidAnnotationElementValueCheck.java"), expected);
    }

    @Test
    public void testAnnotationWithSingleParameter() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidAnnotationElementValueCheck.class);

        checkConfig.addProperty("annotationName", "SuppressWarnings");
        checkConfig.addProperty("elementName", "value");
        checkConfig.addProperty("forbiddenElementValueRegexp", "unchecked");

        final String[] expected = {
            "68:23: " + getCheckMessage(MSG_KEY, "value", "SuppressWarnings"),
        };

        verify(checkConfig, getPath("InputForbidAnnotationElementValueCheck.java"), expected);
    }

    @Test
    public void testAnnotationWithBooleanParameterValueDoesntMatch() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidAnnotationElementValueCheck.class);

        checkConfig.addProperty("annotationName", "Anno4");
        checkConfig.addProperty("elementName", "boolVal");
        checkConfig.addProperty("forbiddenElementValueRegexp", "false");

        final String[] expected = {};

        verify(checkConfig, getPath("InputForbidAnnotationElementValueCheck.java"), expected);
    }

    @Test
    public void testAnnotationWithListAsParameterValue() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidAnnotationElementValueCheck.class);

        checkConfig.addProperty("annotationName", "SuppressWarnings");
        checkConfig.addProperty("elementName", "value");
        checkConfig.addProperty("forbiddenElementValueRegexp", "\\{.*\\}");

        final String[] expected = {
            "74:23: " + getCheckMessage(MSG_KEY, "value", "SuppressWarnings"),
            "79:23: " + getCheckMessage(MSG_KEY, "value", "SuppressWarnings"),
        };

        verify(checkConfig, getPath("InputForbidAnnotationElementValueCheck.java"), expected);
    }

    @Test
    public void testAnnotationWithAnnotationAsParameter() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidAnnotationElementValueCheck.class);

        checkConfig.addProperty("annotationName", "Name");
        checkConfig.addProperty("elementName", "last");
        checkConfig.addProperty("forbiddenElementValueRegexp", "Hacker");

        final String[] expected = {
            "96:34: " + getCheckMessage(MSG_KEY, "last", "Name"),
        };

        verify(checkConfig, getPath("InputForbidAnnotationElementValueCheck.java"), expected);
    }

    @Test
    public void testAnnotationWithDefaultValues() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidAnnotationElementValueCheck.class);

        final String[] expected = {
            "114:11: " + getCheckMessage(MSG_KEY, "expected", "Test"),
        };

        verify(checkConfig, getPath("InputForbidAnnotationElementValueCheck.java"), expected);
    }

    @Test
    public void testFullAnnotationClasspath() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ForbidAnnotationElementValueCheck.class);

        checkConfig.addProperty("annotationName", "SuppressWarnings");
        checkConfig.addProperty("elementName", "value");
        checkConfig.addProperty("forbiddenElementValueRegexp", "rawtypes");

        final String[] expected = {
            "8:33: " + getCheckMessage(MSG_KEY, "value", "SuppressWarnings"),
        };

        verify(checkConfig, getPath("InputForbidAnnotationElementValueCheck2.java"), expected);
    }

}
