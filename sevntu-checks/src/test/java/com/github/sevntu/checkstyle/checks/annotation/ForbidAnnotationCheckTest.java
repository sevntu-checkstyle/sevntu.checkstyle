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

import static com.github.sevntu.checkstyle.checks.annotation.ForbidAnnotationCheck.MSG_KEY;

import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

/**
 * Test that annotation's target is correct.
 *
 * @author <a href="mailto:hidoyatov.v.i@gmail.com">Hidoyatov Victor</a>
 *
 */
public class ForbidAnnotationCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/annotation";
    }

    @Test
    public void testDefaultCheck() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(ForbidAnnotationCheck.class);

        final String[] expected1 = {};

        verify(checkConfig, getPath("InputForbidAnnotationCheck2.java"), expected1);
    }

    @Test
    public void testNullProperties() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(ForbidAnnotationCheck.class);

        checkConfig.addProperty("annotationNames", null);
        checkConfig.addProperty("annotationTargets", null);

        final String[] expected1 = {};

        verify(checkConfig, getPath("InputForbidAnnotationCheck2.java"), expected1);
    }

    @Test
    public void testFullAnnotationName() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(ForbidAnnotationCheck.class);

        checkConfig.addProperty("annotationNames", "Test");

        final String[] expected1 = {};

        verify(checkConfig, getPath("InputForbidAnnotationCheck2.java"), expected1);
    }

    @Test
    public void testVariableIsForbidden() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(ForbidAnnotationCheck.class);

        checkConfig.addProperty("annotationNames",
                "Edible,Author,Author2,SuppressWarnings");
        checkConfig.addProperty("annotationTargets", "VARIABLE_DEF");

        final String[] expected2 = {
            "13:5: " + getCheckMessage(MSG_KEY, "VARIABLE_DEF", "Edible"),
            "20:7: " + getCheckMessage(MSG_KEY, "VARIABLE_DEF", "Author"),
            "21:7: " + getCheckMessage(MSG_KEY, "VARIABLE_DEF", "Author2"),
            "59:7: " + getCheckMessage(MSG_KEY, "VARIABLE_DEF", "SuppressWarnings"),
        };

        verify(checkConfig, getPath("InputForbidAnnotationCheck.java"), expected2);
    }

    @Test
    public void testMethodIsForbidden() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(ForbidAnnotationCheck.class);

        checkConfig.addProperty("annotationNames", "Twizzle,One,Two,Three,B");
        checkConfig.addProperty("annotationTargets", "METHOD_DEF");

        final String[] expected3 = {
            "28:7: " + getCheckMessage(MSG_KEY, "METHOD_DEF", "Twizzle"),
            "39:7: " + getCheckMessage(MSG_KEY, "METHOD_DEF", "One"),
            "40:7: " + getCheckMessage(MSG_KEY, "METHOD_DEF", "Two"),
            "41:7: " + getCheckMessage(MSG_KEY, "METHOD_DEF", "Three"),
            "47:9: " + getCheckMessage(MSG_KEY, "METHOD_DEF", "B"),
        };

        verify(checkConfig, getPath("InputForbidAnnotationCheck.java"), expected3);
    }

    @Test
    public void testClassAndConstuctorIsForbidden() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(ForbidAnnotationCheck.class);

        checkConfig.addProperty("annotationNames", "Test,ctor,ctor2");
        checkConfig.addProperty("annotationTargets", "CLASS_DEF,CTOR_DEF");

        final String[] expected4 = {
            "6:1: " + getCheckMessage(MSG_KEY, "CLASS_DEF", "Test"),
            "8:5: " + getCheckMessage(MSG_KEY, "CTOR_DEF", "ctor"),
            "9:5: " + getCheckMessage(MSG_KEY, "CTOR_DEF", "ctor2"),
        };

        verify(checkConfig, getPath("InputForbidAnnotationCheck.java"), expected4);
    }

    @Test
    public void testAnnotationIsForbidden() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(ForbidAnnotationCheck.class);

        checkConfig.addProperty("annotationNames", "Retention,Target");
        checkConfig.addProperty("annotationTargets", "ANNOTATION_DEF");

        final String[] expected5 = {
            "34:7: " + getCheckMessage(MSG_KEY, "ANNOTATION_DEF", "Retention"),
            "35:7: " + getCheckMessage(MSG_KEY, "ANNOTATION_DEF", "Target"),
        };

        verify(checkConfig, getPath("InputForbidAnnotationCheck.java"), expected5);
    }

    @Test
    public void testParameterAndInterfaceIsForbidden() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(ForbidAnnotationCheck.class);

        checkConfig.addProperty("annotationNames", "MyAnnotation,A");
        checkConfig.addProperty("annotationTargets",
                "PARAMETER_DEF,INTERFACE_DEF");

        final String[] expected6 = {
            "43:17: " + getCheckMessage(MSG_KEY, "PARAMETER_DEF", "MyAnnotation"),
            "45:7: " + getCheckMessage(MSG_KEY, "INTERFACE_DEF", "A"),
        };

        verify(checkConfig, getPath("InputForbidAnnotationCheck.java"), expected6);
    }

    @Test
    public void testEnumIsForbidden() throws Exception {
        final DefaultConfiguration checkConfig = createModuleConfig(ForbidAnnotationCheck.class);

        checkConfig.addProperty("annotationNames", "C,int1,int2,int3");
        checkConfig.addProperty("annotationTargets",
                "ENUM_DEF,ENUM_CONSTANT_DEF");

        final String[] expected7 = {
            "50:7: " + getCheckMessage(MSG_KEY, "ENUM_DEF", "C"),
            "52:12: " + getCheckMessage(MSG_KEY, "ENUM_CONSTANT_DEF", "int1"),
            "54:12: " + getCheckMessage(MSG_KEY, "ENUM_CONSTANT_DEF", "int2"),
            "56:12: " + getCheckMessage(MSG_KEY, "ENUM_CONSTANT_DEF", "int3"),
        };

        verify(checkConfig, getPath("InputForbidAnnotationCheck.java"), expected7);
    }

}
