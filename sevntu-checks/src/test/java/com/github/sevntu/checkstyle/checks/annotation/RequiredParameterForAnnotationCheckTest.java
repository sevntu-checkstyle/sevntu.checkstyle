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
import org.junit.Assert;

public class RequiredParameterForAnnotationCheckTest extends BaseCheckTestSupport
{

    @org.junit.Test
    public void testValidateRequiredParameter()
            throws Exception
    {
        final DefaultConfiguration checkConfig =
                createCheckConfig(RequiredParameterForAnnotationCheck.class);

        checkConfig.addAttribute("annotationName", "testAnnotation1");
        checkConfig.addAttribute("requiredParameters", "firstParameter");

        final String[] expected = { 
                "6:4: " + getCheckMessage(RequiredParameterForAnnotationCheck.MSG_KEY,
                        "testAnnotation1", "firstParameter"),
                "33:4: " + getCheckMessage(RequiredParameterForAnnotationCheck.MSG_KEY,
                        "testAnnotation1", "firstParameter"),
                "66:4: " + getCheckMessage(RequiredParameterForAnnotationCheck.MSG_KEY,
                        "testAnnotation1", "firstParameter"),
                "87:4: " + getCheckMessage(RequiredParameterForAnnotationCheck.MSG_KEY,
                        "testAnnotation1", "firstParameter"),
                "114:4: " + getCheckMessage(RequiredParameterForAnnotationCheck.MSG_KEY,
                        "testAnnotation1", "firstParameter"),
        };
        
        verify(checkConfig, getPath("InputRequiredParameterForAnnotationCheck.java"), expected);
    }

    @org.junit.Test
    public void testMultipleProperties1()
            throws Exception
    {
        final DefaultConfiguration checkConfig =
                createCheckConfig(RequiredParameterForAnnotationCheck.class);

        checkConfig.addAttribute("annotationName", "testAnnotation1");
        checkConfig.addAttribute("requiredParameters", "firstParameter,secondParameter,thirdParameter");

        final String[] expected = { 
                "6:4: " + getCheckMessage(RequiredParameterForAnnotationCheck.MSG_KEY,
                        "testAnnotation1", "firstParameter, secondParameter, thirdParameter"),
                "10:4: " + getCheckMessage(RequiredParameterForAnnotationCheck.MSG_KEY,
                        "testAnnotation1", "thirdParameter"),
                "18:4: " + getCheckMessage(RequiredParameterForAnnotationCheck.MSG_KEY,
                        "testAnnotation1", "secondParameter, thirdParameter"),
                "33:4: " + getCheckMessage(RequiredParameterForAnnotationCheck.MSG_KEY,
                        "testAnnotation1", "firstParameter, secondParameter, thirdParameter"),
                "38:4: " + getCheckMessage(RequiredParameterForAnnotationCheck.MSG_KEY,
                        "testAnnotation1", "thirdParameter"),
                "48:4: " + getCheckMessage(RequiredParameterForAnnotationCheck.MSG_KEY,
                        "testAnnotation1", "secondParameter, thirdParameter"),
                "66:4: " + getCheckMessage(RequiredParameterForAnnotationCheck.MSG_KEY,
                        "testAnnotation1", "firstParameter, secondParameter, thirdParameter"),
                "69:4: " + getCheckMessage(RequiredParameterForAnnotationCheck.MSG_KEY,
                        "testAnnotation1", "thirdParameter"),
                "75:4: " + getCheckMessage(RequiredParameterForAnnotationCheck.MSG_KEY,
                        "testAnnotation1", "secondParameter, thirdParameter"),
                "87:4: " + getCheckMessage(RequiredParameterForAnnotationCheck.MSG_KEY,
                        "testAnnotation1", "firstParameter, secondParameter, thirdParameter"),
                "91:4: " + getCheckMessage(RequiredParameterForAnnotationCheck.MSG_KEY,
                        "testAnnotation1", "thirdParameter"),
                "99:4: " + getCheckMessage(RequiredParameterForAnnotationCheck.MSG_KEY,
                        "testAnnotation1", "secondParameter, thirdParameter"),
                "114:4: " + getCheckMessage(RequiredParameterForAnnotationCheck.MSG_KEY,
                        "testAnnotation1", "firstParameter, secondParameter, thirdParameter"),
                "118:4: " + getCheckMessage(RequiredParameterForAnnotationCheck.MSG_KEY,
                        "testAnnotation1", "thirdParameter"),
                "126:4: " + getCheckMessage(RequiredParameterForAnnotationCheck.MSG_KEY,
                        "testAnnotation1", "secondParameter, thirdParameter"),
        };
        verify(checkConfig, getPath("InputRequiredParameterForAnnotationCheck.java"), expected);
    }

    @org.junit.Test
    public void testMultipleProperties2()
            throws Exception
    {
        final DefaultConfiguration checkConfig =
                createCheckConfig(RequiredParameterForAnnotationCheck.class);

        checkConfig.addAttribute("annotationName", "testAnnotation1");
        checkConfig.addAttribute("requiredParameters", "firstParameter,secondParameter");

        final String[] expected = { 
                "6:4: " + getCheckMessage(RequiredParameterForAnnotationCheck.MSG_KEY,
                        "testAnnotation1", "firstParameter, secondParameter"),
                "18:4: " + getCheckMessage(RequiredParameterForAnnotationCheck.MSG_KEY,
                        "testAnnotation1", "secondParameter"),
                "33:4: " + getCheckMessage(RequiredParameterForAnnotationCheck.MSG_KEY,
                        "testAnnotation1", "firstParameter, secondParameter"),
                "48:4: " + getCheckMessage(RequiredParameterForAnnotationCheck.MSG_KEY,
                        "testAnnotation1", "secondParameter"),
                "66:4: " + getCheckMessage(RequiredParameterForAnnotationCheck.MSG_KEY,
                        "testAnnotation1", "firstParameter, secondParameter"),
                "75:4: " + getCheckMessage(RequiredParameterForAnnotationCheck.MSG_KEY,
                        "testAnnotation1", "secondParameter"),
                "87:4: " + getCheckMessage(RequiredParameterForAnnotationCheck.MSG_KEY,
                        "testAnnotation1", "firstParameter, secondParameter"),
                "99:4: " + getCheckMessage(RequiredParameterForAnnotationCheck.MSG_KEY,
                        "testAnnotation1", "secondParameter"),
                "114:4: " + getCheckMessage(RequiredParameterForAnnotationCheck.MSG_KEY,
                        "testAnnotation1", "firstParameter, secondParameter"),
                "126:4: " + getCheckMessage(RequiredParameterForAnnotationCheck.MSG_KEY,
                        "testAnnotation1", "secondParameter"),
        };
        verify(checkConfig, getPath("InputRequiredParameterForAnnotationCheck.java"), expected);
    }

    @org.junit.Test
    public void testForAnnotationWithCanonicalName()
            throws Exception
    {
        final DefaultConfiguration checkConfig =
                createCheckConfig(RequiredParameterForAnnotationCheck.class);

        checkConfig.addAttribute("annotationName", "com.github.sevntu.checkstyle.checks"
                + ".annotation.InputRequiredParameterForAnnotationCheck.testAnnotation2");
        checkConfig.addAttribute("requiredParameters", "par1");

        final String[] expected = { 
                "27:4: " + getCheckMessage(RequiredParameterForAnnotationCheck.MSG_KEY,
                        "com.github.sevntu.checkstyle.checks.annotation"
                        + ".InputRequiredParameterForAnnotationCheck.testAnnotation2", "par1"),
                "59:4: " + getCheckMessage(RequiredParameterForAnnotationCheck.MSG_KEY,
                        "com.github.sevntu.checkstyle.checks.annotation"
                        + ".InputRequiredParameterForAnnotationCheck.testAnnotation2", "par1"),
                "82:4: " + getCheckMessage(RequiredParameterForAnnotationCheck.MSG_KEY,
                        "com.github.sevntu.checkstyle.checks.annotation"
                        + ".InputRequiredParameterForAnnotationCheck.testAnnotation2", "par1"),
                "108:4: " + getCheckMessage(RequiredParameterForAnnotationCheck.MSG_KEY,
                        "com.github.sevntu.checkstyle.checks.annotation"
                        + ".InputRequiredParameterForAnnotationCheck.testAnnotation2", "par1"),
                "135:4: " + getCheckMessage(RequiredParameterForAnnotationCheck.MSG_KEY,
                        "com.github.sevntu.checkstyle.checks.annotation"
                        + ".InputRequiredParameterForAnnotationCheck.testAnnotation2", "par1"),
        };
        verify(checkConfig, getPath("InputRequiredParameterForAnnotationCheck.java"), expected);
    }

    @org.junit.Test
    public void testMultipleParametersForAnnotationWithCanonicalName()
            throws Exception
    {
        final DefaultConfiguration checkConfig =
                createCheckConfig(RequiredParameterForAnnotationCheck.class);

        checkConfig.addAttribute("annotationName", "com.github.sevntu.checkstyle.checks"
                + ".annotation.InputRequiredParameterForAnnotationCheck.testAnnotation2");
        checkConfig.addAttribute("requiredParameters", "par1,par2");

        final String[] expected = { 
                "27:4: " + getCheckMessage(RequiredParameterForAnnotationCheck.MSG_KEY,
                        "com.github.sevntu.checkstyle.checks.annotation"
                        + ".InputRequiredParameterForAnnotationCheck.testAnnotation2", "par1, par2"),
                "59:4: " + getCheckMessage(RequiredParameterForAnnotationCheck.MSG_KEY,
                        "com.github.sevntu.checkstyle.checks.annotation"
                        + ".InputRequiredParameterForAnnotationCheck.testAnnotation2", "par1, par2"),
                "82:4: " + getCheckMessage(RequiredParameterForAnnotationCheck.MSG_KEY,
                        "com.github.sevntu.checkstyle.checks.annotation"
                        + ".InputRequiredParameterForAnnotationCheck.testAnnotation2", "par1, par2"),
                "108:4: " + getCheckMessage(RequiredParameterForAnnotationCheck.MSG_KEY,
                        "com.github.sevntu.checkstyle.checks.annotation"
                        + ".InputRequiredParameterForAnnotationCheck.testAnnotation2", "par1, par2"),
                "135:4: " + getCheckMessage(RequiredParameterForAnnotationCheck.MSG_KEY,
                        "com.github.sevntu.checkstyle.checks.annotation"
                        + ".InputRequiredParameterForAnnotationCheck.testAnnotation2", "par1, par2"),
        };
        verify(checkConfig, getPath("InputRequiredParameterForAnnotationCheck.java"), expected);
    }
    
    @org.junit.Test
    public void temporaryTestForCallGetRequiredTokens()
            throws Exception
    {
        RequiredParameterForAnnotationCheck check = new RequiredParameterForAnnotationCheck();
        int[] tokens = check.getRequiredTokens();
        Assert.assertNotNull(tokens);
    }
   
}
