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

import static com.github.sevntu.checkstyle.checks.annotation.RequiredParameterForAnnotationCheck.MSG_KEY;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class RequiredParameterForAnnotationCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/annotation";
    }

    @Test
    public void testValidateRequiredParameter()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(RequiredParameterForAnnotationCheck.class);

        checkConfig.addProperty("annotationName", "testAnnotation1");
        checkConfig.addProperty("requiredParameters", "firstParameter");

        final String[] expected = {
            "6:4: " + getCheckMessage(MSG_KEY, "testAnnotation1", "firstParameter"),
            "34:4: " + getCheckMessage(MSG_KEY, "testAnnotation1", "firstParameter"),
            "68:4: " + getCheckMessage(MSG_KEY, "testAnnotation1", "firstParameter"),
            "90:4: " + getCheckMessage(MSG_KEY, "testAnnotation1", "firstParameter"),
            "118:4: " + getCheckMessage(MSG_KEY, "testAnnotation1", "firstParameter"),
        };

        verify(checkConfig, getPath("InputRequiredParameterForAnnotationCheck.java"), expected);
    }

    @Test
    public void testMultipleProperties1()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(RequiredParameterForAnnotationCheck.class);

        checkConfig.addProperty("annotationName", "testAnnotation1");
        checkConfig.addProperty("requiredParameters",
                "firstParameter,secondParameter,thirdParameter");

        final String[] expected = {
            "6:4: " + getCheckMessage(MSG_KEY,
                    "testAnnotation1", "firstParameter, secondParameter, thirdParameter"),
            "10:4: " + getCheckMessage(MSG_KEY, "testAnnotation1", "thirdParameter"),
            "19:4: " + getCheckMessage(MSG_KEY,
                    "testAnnotation1", "secondParameter, thirdParameter"),
            "34:4: " + getCheckMessage(MSG_KEY,
                    "testAnnotation1", "firstParameter, secondParameter, thirdParameter"),
            "39:4: " + getCheckMessage(MSG_KEY, "testAnnotation1", "thirdParameter"),
            "50:4: " + getCheckMessage(MSG_KEY,
                    "testAnnotation1", "secondParameter, thirdParameter"),
            "68:4: " + getCheckMessage(MSG_KEY,
                    "testAnnotation1", "firstParameter, secondParameter, thirdParameter"),
            "71:4: " + getCheckMessage(MSG_KEY, "testAnnotation1", "thirdParameter"),
            "78:4: " + getCheckMessage(MSG_KEY,
                    "testAnnotation1", "secondParameter, thirdParameter"),
            "90:4: " + getCheckMessage(MSG_KEY,
                    "testAnnotation1", "firstParameter, secondParameter, thirdParameter"),
            "94:4: " + getCheckMessage(MSG_KEY, "testAnnotation1", "thirdParameter"),
            "103:4: " + getCheckMessage(MSG_KEY,
                    "testAnnotation1", "secondParameter, thirdParameter"),
            "118:4: " + getCheckMessage(MSG_KEY,
                    "testAnnotation1", "firstParameter, secondParameter, thirdParameter"),
            "122:4: " + getCheckMessage(MSG_KEY, "testAnnotation1", "thirdParameter"),
            "131:4: " + getCheckMessage(MSG_KEY,
                    "testAnnotation1", "secondParameter, thirdParameter"),
        };
        verify(checkConfig, getPath("InputRequiredParameterForAnnotationCheck.java"), expected);
    }

    @Test
    public void testMultipleProperties2()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(RequiredParameterForAnnotationCheck.class);

        checkConfig.addProperty("annotationName", "testAnnotation1");
        checkConfig.addProperty("requiredParameters", "firstParameter,secondParameter");

        final String[] expected = {
            "6:4: " + getCheckMessage(MSG_KEY,
                    "testAnnotation1", "firstParameter, secondParameter"),
            "19:4: " + getCheckMessage(MSG_KEY, "testAnnotation1", "secondParameter"),
            "34:4: " + getCheckMessage(MSG_KEY,
                    "testAnnotation1", "firstParameter, secondParameter"),
            "50:4: " + getCheckMessage(MSG_KEY, "testAnnotation1", "secondParameter"),
            "68:4: " + getCheckMessage(MSG_KEY,
                    "testAnnotation1", "firstParameter, secondParameter"),
            "78:4: " + getCheckMessage(MSG_KEY, "testAnnotation1", "secondParameter"),
            "90:4: " + getCheckMessage(MSG_KEY,
                    "testAnnotation1", "firstParameter, secondParameter"),
            "103:4: " + getCheckMessage(MSG_KEY, "testAnnotation1", "secondParameter"),
            "118:4: " + getCheckMessage(MSG_KEY,
                    "testAnnotation1", "firstParameter, secondParameter"),
            "131:4: " + getCheckMessage(MSG_KEY, "testAnnotation1", "secondParameter"),
        };
        verify(checkConfig, getPath("InputRequiredParameterForAnnotationCheck.java"), expected);
    }

    @Test
    public void testForAnnotationWithCanonicalName()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(RequiredParameterForAnnotationCheck.class);

        checkConfig.addProperty("annotationName", "com.github.sevntu.checkstyle.checks"
                + ".annotation.InputRequiredParameterForAnnotationCheck.testAnnotation2");
        checkConfig.addProperty("requiredParameters", "par1");

        final String[] expected = {
            "28:4: " + getCheckMessage(MSG_KEY,
                    "com.github.sevntu.checkstyle.checks.annotation"
                    + ".InputRequiredParameterForAnnotationCheck.testAnnotation2", "par1"),
            "61:4: " + getCheckMessage(MSG_KEY,
                    "com.github.sevntu.checkstyle.checks.annotation"
                    + ".InputRequiredParameterForAnnotationCheck.testAnnotation2", "par1"),
            "85:4: " + getCheckMessage(MSG_KEY,
                    "com.github.sevntu.checkstyle.checks.annotation"
                    + ".InputRequiredParameterForAnnotationCheck.testAnnotation2", "par1"),
            "112:4: " + getCheckMessage(MSG_KEY,
                    "com.github.sevntu.checkstyle.checks.annotation"
                    + ".InputRequiredParameterForAnnotationCheck.testAnnotation2", "par1"),
            "140:4: " + getCheckMessage(MSG_KEY,
                    "com.github.sevntu.checkstyle.checks.annotation"
                    + ".InputRequiredParameterForAnnotationCheck.testAnnotation2", "par1"),
        };
        verify(checkConfig, getPath("InputRequiredParameterForAnnotationCheck.java"), expected);
    }

    @Test
    public void testMultipleParametersForAnnotationWithCanonicalName()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(RequiredParameterForAnnotationCheck.class);

        checkConfig.addProperty("annotationName", "com.github.sevntu.checkstyle.checks"
                + ".annotation.InputRequiredParameterForAnnotationCheck.testAnnotation2");
        checkConfig.addProperty("requiredParameters", "par1,par2");

        final String[] expected = {
            "28:4: " + getCheckMessage(MSG_KEY,
                    "com.github.sevntu.checkstyle.checks.annotation"
                    + ".InputRequiredParameterForAnnotationCheck.testAnnotation2", "par1, par2"),
            "61:4: " + getCheckMessage(MSG_KEY,
                    "com.github.sevntu.checkstyle.checks.annotation"
                    + ".InputRequiredParameterForAnnotationCheck.testAnnotation2", "par1, par2"),
            "85:4: " + getCheckMessage(MSG_KEY,
                    "com.github.sevntu.checkstyle.checks.annotation"
                    + ".InputRequiredParameterForAnnotationCheck.testAnnotation2", "par1, par2"),
            "112:4: " + getCheckMessage(MSG_KEY,
                    "com.github.sevntu.checkstyle.checks.annotation"
                    + ".InputRequiredParameterForAnnotationCheck.testAnnotation2", "par1, par2"),
            "140:4: " + getCheckMessage(MSG_KEY,
                    "com.github.sevntu.checkstyle.checks.annotation"
                    + ".InputRequiredParameterForAnnotationCheck.testAnnotation2", "par1, par2"),
        };
        verify(checkConfig, getPath("InputRequiredParameterForAnnotationCheck.java"), expected);
    }

    @Test
    public void temporaryTestForCallGetRequiredTokens() {
        final RequiredParameterForAnnotationCheck check = new RequiredParameterForAnnotationCheck();
        final int[] tokens = check.getRequiredTokens();
        Assertions.assertNotNull(tokens);
    }

}
