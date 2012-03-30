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
package com.puppycrawl.tools.checkstyle.checks.design;

import java.io.File;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

/**
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil
 *         Yaroslavtsev</a>
 */
public class CauseParameterInExceptionCheckTest extends BaseCheckTestSupport
{
    private final DefaultConfiguration checkConfig = createCheckConfig(CauseParameterInExceptionCheck.class);

    @Test
    public void testNormalWork() throws Exception
    {
        checkConfig.addAttribute("classNamesRegexp", ".+Exception");
        checkConfig.addAttribute("ignoredClassNamesRegexp", null);
        checkConfig.addAttribute("allowedCauseTypes", "Throwable, Exception");

        String[] expected = {
            "3:1: " + getMessage("InputCauseParameterInException"),
        };

        verify(checkConfig, getPath("design" + File.separator
                + "InputCauseParameterInException.java"), expected);
    }

    @Test
    public void testNormalWork2() throws Exception
    {
        checkConfig.addAttribute("classNamesRegexp", ".+Exception2");
        checkConfig.addAttribute("ignoredClassNamesRegexp", null);
        checkConfig.addAttribute("allowedCauseTypes", "Throwable, Exception");

        String[] expected = {
            "3:1: " + getMessage("InputCauseParameterInException2"),
            "22:5: " + getMessage("MyException2"),
        };

        verify(checkConfig, getPath("design" + File.separator
                + "InputCauseParameterInException2.java"), expected);
    }

    @Test
    public void testIgnorePattern() throws Exception
    {
        checkConfig.addAttribute("classNamesRegexp", ".+Exception2");
        checkConfig.addAttribute("ignoredClassNamesRegexp", "Input.+");
        checkConfig.addAttribute("allowedCauseTypes", "Throwable, Exception");

        String[] expected = {
            "22:5: " + getMessage("MyException2"),
        };

        verify(checkConfig, getPath("design" + File.separator
                + "InputCauseParameterInException2.java"), expected);
    }

    @Test
    public void testIgnorePattern2() throws Exception
    {
        checkConfig.addAttribute("classNamesRegexp", ".+Exception2");
        checkConfig.addAttribute("ignoredClassNamesRegexp", "My.+");
        checkConfig.addAttribute("allowedCauseTypes", "Throwable, Exception");

        String[] expected = {
            "3:1: " + getMessage("InputCauseParameterInException2"),
        };

        verify(checkConfig, getPath("design" + File.separator
                + "InputCauseParameterInException2.java"), expected);
    }

    @Test
    public void testStrangeSituation() throws Exception
    {
        checkConfig.addAttribute("classNamesRegexp", ".+Exception");
        checkConfig.addAttribute("ignoredClassNamesRegexp", "");
        checkConfig.addAttribute("allowedCauseTypes", "Throwable, Exception");

        String[] expected = {
        };

        verify(checkConfig, getPath("design" + File.separator
                + "InputCauseParameterInException3.java"), expected);
    }

    @Test
    public void testStrangeSituation2() throws Exception
    {
        checkConfig.addAttribute("classNamesRegexp", null);
        checkConfig.addAttribute("ignoredClassNamesRegexp", null);
        checkConfig.addAttribute("allowedCauseTypes", "Throwable, Exception");

        String[] expected = {
        };

        verify(checkConfig, getPath("design" + File.separator
                + "InputCauseParameterInException3.java"), expected);
    }

    @Test
    public void testStrangeSituation3() throws Exception
    {
        checkConfig.addAttribute("classNamesRegexp", null);
        checkConfig.addAttribute("ignoredClassNamesRegexp", null);
        checkConfig.addAttribute("allowedCauseTypes", "Throwable, Exception");

        String[] expected = {
        };

        verify(checkConfig, getPath("design" + File.separator
                + "InputCauseParameterInException4.java"), expected);
    }

    private String getMessage(String className)
    {
        return "'"
                + className
                + "' class should have a constructor with exception cause as parameter.";
    }

}
