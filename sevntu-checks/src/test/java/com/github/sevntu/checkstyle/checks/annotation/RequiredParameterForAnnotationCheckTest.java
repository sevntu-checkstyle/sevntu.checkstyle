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

package com.github.sevntu.checkstyle.checks.annotation;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

/**
 * @author Clebert Suconic
 */

public class RequiredParameterForAnnotationCheckTest extends BaseCheckTestSupport
{

    @org.junit.Test
    public void testValidateRequiredParameter()
            throws Exception
    {
        final DefaultConfiguration checkConfig =
                createCheckConfig(RequiredParameterForAnnotationCheck.class);

        checkConfig.addAttribute("annotationName", "SomeNote");
        checkConfig.addAttribute("requiredParameters", "thename");

        final String[] expected = { "14:4: "
                + getCheckMessage(RequiredParameterForAnnotationCheck.MSG_KEY,
                        "SomeNote", "thename") };
        verify(checkConfig, getPath("RequiredAnnotation.java"), expected);
    }

    @org.junit.Test
    public void testMultipleProperties()
            throws Exception
    {
        final DefaultConfiguration checkConfig =
                createCheckConfig(RequiredParameterForAnnotationCheck.class);

        checkConfig.addAttribute("annotationName", "MultipleRequired");
        checkConfig.addAttribute("requiredParameters", "prop1,prop2");

        final String[] expected = {};
        verify(checkConfig, getPath("RequiredAnnotation.java"), expected);
    }

    @org.junit.Test
    public void testMultipleParametersValidateFailure()
            throws Exception
    {
        final DefaultConfiguration checkConfig =
                createCheckConfig(RequiredParameterForAnnotationCheck.class);

        checkConfig.addAttribute("annotationName", "MultipleRequired");
        checkConfig.addAttribute("requiredParameters", "prop1,propDontExist");

        final String[] expected = { "18:4: "
                + getCheckMessage(RequiredParameterForAnnotationCheck.MSG_KEY,
                        "MultipleRequired", "propDontExist") };
        verify(checkConfig, getPath("RequiredAnnotation.java"), expected);
    }

    @org.junit.Test
    public void testMissingTwoParameters()
            throws Exception
    {
        final DefaultConfiguration checkConfig =
                createCheckConfig(RequiredParameterForAnnotationCheck.class);

        checkConfig.addAttribute("annotationName", "SomeNote");
        checkConfig.addAttribute("requiredParameters", "a,b");

        final String[] expected = {
                "5:4: "
                        + getCheckMessage(
                                RequiredParameterForAnnotationCheck.MSG_KEY,
                                "SomeNote", "a, b"),
                "10:4: "
                        + getCheckMessage(
                                RequiredParameterForAnnotationCheck.MSG_KEY,
                                "SomeNote", "a, b"),
                "14:4: "
                        + getCheckMessage(
                                RequiredParameterForAnnotationCheck.MSG_KEY,
                                "SomeNote", "a, b") };
        verify(checkConfig, getPath("RequiredAnnotation.java"), expected);
    }

}
