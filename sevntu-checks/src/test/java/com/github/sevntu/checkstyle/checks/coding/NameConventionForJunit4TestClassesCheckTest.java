////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2013 Oliver Burn
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

package com.github.sevntu.checkstyle.checks.coding;

import java.text.MessageFormat;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

/**
 * @author <a href="mailto:denant0vz@gmail.com">Denis Antonenkov</a>
 * @author <a href="mailto:zuy_alexey@mail.ru">Zuy Alexey</a>
 */
public class NameConventionForJunit4TestClassesCheckTest extends BaseCheckTestSupport
{
    private final String msgFormat =
            getCheckMessage(NameConventionForJunit4TestClassesCheck.MSG_KEY);

    private final String defaultTestClassName =
            ".+Test\\d*|.+Tests\\d*|Test.+|Tests.+|.+IT|.+ITs|.+TestCase|.+TestCases";

    private final DefaultConfiguration checkConfig =
            buildConfiguration(defaultTestClassName, "", "Test");

    @Test
    public void testAnnotatedClass()
            throws Exception
    {

        DefaultConfiguration customConfig =
                buildConfiguration(defaultTestClassName, "RunWith", "");

        final String[] expected = {
                buildMesssage("14:18: ", defaultTestClassName),
        };

        verify(customConfig, getPath("InputNameConventionForTest1.java"), expected);
    }

    @Test
    public void testAnnotatedMethod()
            throws Exception
    {
        final String[] expected = {
                buildMesssage("8:18: ", defaultTestClassName),
        };

        verify(checkConfig, getPath("InputNameConventionForTest2.java"), expected);
    }

    @Test
    public void testClassIsNotTest()
            throws Exception
    {
        final String[] expected = {
                };

        verify(checkConfig, getPath("InputNameConventionForTest3.java"), expected);
    }

    @Test
    public void testRegex()
            throws Exception
    {
        final String[] expected = {
                buildMesssage("7:18: ", defaultTestClassName),
        };

        verify(checkConfig, getPath("InputNameConventionForTest4.java"), expected);
    }

    @Test
    public void testQualifiedAnnotationName()
            throws Exception
    {
        DefaultConfiguration customConfig =
                buildConfiguration(defaultTestClassName, "", "org.junit.Test");

        final String[] expected = {
                buildMesssage("8:18: ", defaultTestClassName),
        };

        verify(customConfig, getPath("InputNameConventionForTest5.java"), expected);
    }

    @Test
    public void testJunitClassTestAnnotationNamesRegexOption()
            throws Exception
    {
        DefaultConfiguration customConfig =
                buildConfiguration(defaultTestClassName, "SomeTestAnnotation", "");

        final String[] expected = {
                buildMesssage("7:18: ", defaultTestClassName),
        };

        verify(customConfig, getPath("InputNameConventionForTest6.java"), expected);
    }

    @Test
    public void testJunitMethodTestAnnotationNamesRegexOption()
            throws Exception
    {
        DefaultConfiguration customConfig =
                buildConfiguration(defaultTestClassName, "", "SomeTestAnnotation");

        final String[] expected = {
                buildMesssage("6:18: ", defaultTestClassName),
        };

        verify(customConfig, getPath("InputNameConventionForTest7.java"), expected);
    }

    @Test
    public void testCorrectTestClassNameRegexOption()
            throws Exception
    {
        DefaultConfiguration customConfig =
                buildConfiguration("Hello*", "", "Test");

        final String[] expected = {
                buildMesssage("5:14: ", "Hello*"),
        };

        verify(customConfig, getPath("InputNameConventionForTest8.java"), expected);
    }

    private static DefaultConfiguration buildConfiguration(String expectedNameRegex,
            String classAnnotationNameRegex, String methodAnnotationNameRegex)
    {
        DefaultConfiguration config =
                createCheckConfig(NameConventionForJunit4TestClassesCheck.class);

        config.addAttribute("expectedClassNameRegex", expectedNameRegex);
        config.addAttribute("classAnnotationNameRegex", classAnnotationNameRegex);
        config.addAttribute("methodAnnotationNameRegex", methodAnnotationNameRegex);

        return config;
    }

    private String buildMesssage(String lineNumber, String arguments)
    {
        return lineNumber + MessageFormat.format(msgFormat, arguments);
    }
}
