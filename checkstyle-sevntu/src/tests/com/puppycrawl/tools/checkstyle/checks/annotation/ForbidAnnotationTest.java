////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2011  Oliver Burn
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
////////////////////////////////////////////////////////////////////////////////
package com.puppycrawl.tools.checkstyle.checks.annotation;

import java.io.File;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
/**
 * Test that annotation's target is correct.
 * @author <a href="mailto:hidoyatov.v.i@gmail.com">Hidoyatov Victor</a>
 *
 */
public class ForbidAnnotationTest extends BaseCheckTestSupport
{

    @Test
    public void testPackageIsForbidden() throws Exception
    {
        DefaultConfiguration checkConfig = createCheckConfig(ForbidAnnotationCheck.class);

        checkConfig.addAttribute("annotationNames", "pack1,pack2,pack3");
        checkConfig.addAttribute("annotationTargets", "PACKAGE_DEF");

        final String[] expected1 = {
            "1: Incorrect target: 'package' for annotation: 'pack1'.",
            "2: Incorrect target: 'package' for annotation: 'pack2'.",
            "3: Incorrect target: 'package' for annotation: 'pack3'.", };

        verify(checkConfig, getPath("annotation" + File.separator
                + "ForbiAnnotationInput.java"), expected1);
    }

    @Test
    public void testVariableIsForbidden() throws Exception
    {
        DefaultConfiguration checkConfig = createCheckConfig(ForbidAnnotationCheck.class);

        checkConfig.addAttribute("annotationNames",
                "Edible,Author,Author2,SuppressWarnings");
        checkConfig.addAttribute("annotationTargets", "VARIABLE_DEF");

        final String[] expected2 = {
            "12: Incorrect target: 'VARIABLE_DEF' for annotation: 'Edible'.",
            "19: Incorrect target: 'VARIABLE_DEF' for annotation: 'Author'.",
            "20: Incorrect target: 'VARIABLE_DEF' for annotation: 'Author2'.",
            "58: Incorrect target: 'VARIABLE_DEF' for annotation: 'SuppressWarnings'.", };

        verify(checkConfig, getPath("annotation" + File.separator
                + "ForbiAnnotationInput.java"), expected2);
    }

    @Test
    public void testMethodIsForbidden() throws Exception
    {
        DefaultConfiguration checkConfig = createCheckConfig(ForbidAnnotationCheck.class);

        checkConfig.addAttribute("annotationNames", "Twizzle,One,Two,Three,B");
        checkConfig.addAttribute("annotationTargets", "METHOD_DEF");

        final String[] expected3 = {
            "27: Incorrect target: 'METHOD_DEF' for annotation: 'Twizzle'.",
            "38: Incorrect target: 'METHOD_DEF' for annotation: 'One'.",
            "39: Incorrect target: 'METHOD_DEF' for annotation: 'Two'.",
            "40: Incorrect target: 'METHOD_DEF' for annotation: 'Three'.",
            "46: Incorrect target: 'METHOD_DEF' for annotation: 'B'.", };

        verify(checkConfig, getPath("annotation" + File.separator
                + "ForbiAnnotationInput.java"), expected3);
    }

    @Test
    public void testClassAndConstuctorIsForbidden() throws Exception
    {
        DefaultConfiguration checkConfig = createCheckConfig(ForbidAnnotationCheck.class);

        checkConfig.addAttribute("annotationNames", "Test,ctor,ctor2");
        checkConfig.addAttribute("annotationTargets", "CLASS_DEF,CTOR_DEF");

        final String[] expected4 = {
            "5: Incorrect target: 'CLASS_DEF' for annotation: 'Test'.",
            "7: Incorrect target: 'CTOR_DEF' for annotation: 'ctor'.",
            "8: Incorrect target: 'CTOR_DEF' for annotation: 'ctor2'.", };

        verify(checkConfig, getPath("annotation" + File.separator
                + "ForbiAnnotationInput.java"), expected4);
    }

    @Test
    public void testAnnotationIsForbidden() throws Exception
    {
        DefaultConfiguration checkConfig = createCheckConfig(ForbidAnnotationCheck.class);

        checkConfig.addAttribute("annotationNames", "Retention,Target");
        checkConfig.addAttribute("annotationTargets", "ANNOTATION_DEF");

        final String[] expected5 = {
            "33: Incorrect target: 'ANNOTATION_DEF' for annotation: 'Retention'.",
            "34: Incorrect target: 'ANNOTATION_DEF' for annotation: 'Target'.", };

        verify(checkConfig, getPath("annotation" + File.separator
                + "ForbiAnnotationInput.java"), expected5);
    }

    @Test
    public void testParamerterAndInterfaceIsForbidden() throws Exception
    {
        DefaultConfiguration checkConfig = createCheckConfig(ForbidAnnotationCheck.class);

        checkConfig.addAttribute("annotationNames", "MyAnnotation,A");
        checkConfig.addAttribute("annotationTargets",
                "PARAMETER_DEF,INTERFACE_DEF");

        final String[] expected6 = {
            "42: Incorrect target: 'PARAMETER_DEF' for annotation: 'MyAnnotation'.",
            "44: Incorrect target: 'INTERFACE_DEF' for annotation: 'A'.", };

        verify(checkConfig, getPath("annotation" + File.separator
                + "ForbiAnnotationInput.java"), expected6);
    }

    @Test
    public void testEnumIsForbidden() throws Exception
    {
        DefaultConfiguration checkConfig = createCheckConfig(ForbidAnnotationCheck.class);

        checkConfig.addAttribute("annotationNames", "C,int1,int2,int3");
        checkConfig.addAttribute("annotationTargets",
                "ENUM_DEF,ENUM_CONSTANT_DEF");

        final String[] expected7 = {
            "49: Incorrect target: 'ENUM_DEF' for annotation: 'C'.",
            "51: Incorrect target: 'ENUM_CONSTANT_DEF' for annotation: 'int1'.",
            "53: Incorrect target: 'ENUM_CONSTANT_DEF' for annotation: 'int2'.",
            "55: Incorrect target: 'ENUM_CONSTANT_DEF' for annotation: 'int3'.",
        };

        verify(checkConfig, getPath("annotation" + File.separator
                + "ForbiAnnotationInput.java"), expected7);
    }
}
