////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2018 the original author or authors.
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

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

/**
 * @author <a href="mailto:denant0vz@gmail.com">Denis Antonenkov</a>
 * @author <a href="mailto:zuy_alexey@mail.ru">Zuy Alexey</a>
 */
public class NameConventionForJunit4TestClassesCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/coding";
    }

    @Test
    public void testAnnotatedClass()
            throws Exception {
        final DefaultConfiguration customConfig =
                createModuleConfig(NameConventionForJunit4TestClassesCheck.class);
        customConfig.addAttribute("expectedClassNameRegex",
                ".+Test\\d*|.+Tests\\d*|Test.+|Tests.+|.+IT|.+ITs|.+TestCase|.+TestCases");
        customConfig.addAttribute("classAnnotationNameRegex", "RunWith");
        customConfig.addAttribute("methodAnnotationNameRegex", "");

        final String[] expected = {
            "14:18: " + getCheckMessage(NameConventionForJunit4TestClassesCheck.MSG_KEY,
                    ".+Test\\d*|.+Tests\\d*|Test.+|Tests.+|.+IT|.+ITs|.+TestCase|.+TestCases"),
        };

        verify(customConfig, getPath("InputNameConventionForJunit4TestClassesCheck1.java"),
                expected);
    }

    @Test
    public void testAnnotatedMethod()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(NameConventionForJunit4TestClassesCheck.class);
        checkConfig.addAttribute("expectedClassNameRegex",
                ".+Test\\d*|.+Tests\\d*|Test.+|Tests.+|.+IT|.+ITs|.+TestCase|.+TestCases");
        checkConfig.addAttribute("classAnnotationNameRegex", "");
        checkConfig.addAttribute("methodAnnotationNameRegex", "Test");

        final String[] expected = {
            "8:18: " + getCheckMessage(NameConventionForJunit4TestClassesCheck.MSG_KEY,
                    ".+Test\\d*|.+Tests\\d*|Test.+|Tests.+|.+IT|.+ITs|.+TestCase|.+TestCases"),
        };

        verify(checkConfig, getPath("InputNameConventionForJunit4TestClassesCheck2.java"),
                expected);
    }

    @Test
    public void testClassIsNotTest()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(NameConventionForJunit4TestClassesCheck.class);
        checkConfig.addAttribute("expectedClassNameRegex",
                ".+Test\\d*|.+Tests\\d*|Test.+|Tests.+|.+IT|.+ITs|.+TestCase|.+TestCases");
        checkConfig.addAttribute("classAnnotationNameRegex", "");
        checkConfig.addAttribute("methodAnnotationNameRegex", "Test");

        final String[] expected = {
        };

        verify(checkConfig, getPath("InputNameConventionForJunit4TestClassesCheck3.java"),
                expected);
    }

    @Test
    public void testRegex()
            throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(NameConventionForJunit4TestClassesCheck.class);
        checkConfig.addAttribute("expectedClassNameRegex",
                ".+Test\\d*|.+Tests\\d*|Test.+|Tests.+|.+IT|.+ITs|.+TestCase|.+TestCases");
        checkConfig.addAttribute("classAnnotationNameRegex", "");
        checkConfig.addAttribute("methodAnnotationNameRegex", "Test");

        final String[] expected = {
            "7:18: " + getCheckMessage(NameConventionForJunit4TestClassesCheck.MSG_KEY,
                    ".+Test\\d*|.+Tests\\d*|Test.+|Tests.+|.+IT|.+ITs|.+TestCase|.+TestCases"),
        };

        verify(checkConfig, getPath("InputNameConventionForJunit4TestClassesCheck4.java"),
                expected);
    }

    @Test
    public void testQualifiedAnnotationName()
            throws Exception {
        final DefaultConfiguration customConfig =
                createModuleConfig(NameConventionForJunit4TestClassesCheck.class);
        customConfig.addAttribute("expectedClassNameRegex",
                ".+Test\\d*|.+Tests\\d*|Test.+|Tests.+|.+IT|.+ITs|.+TestCase|.+TestCases");
        customConfig.addAttribute("classAnnotationNameRegex", "");
        customConfig.addAttribute("methodAnnotationNameRegex", "org.junit.Test");

        final String[] expected = {
            "8:18: " + getCheckMessage(NameConventionForJunit4TestClassesCheck.MSG_KEY,
                    ".+Test\\d*|.+Tests\\d*|Test.+|Tests.+|.+IT|.+ITs|.+TestCase|.+TestCases"),
        };

        verify(customConfig, getPath("InputNameConventionForJunit4TestClassesCheck5.java"),
                expected);
    }

    @Test
    public void testJunitClassTestAnnotationNamesRegexOption()
            throws Exception {
        final DefaultConfiguration customConfig =
                createModuleConfig(NameConventionForJunit4TestClassesCheck.class);
        customConfig.addAttribute("expectedClassNameRegex",
                ".+Test\\d*|.+Tests\\d*|Test.+|Tests.+|.+IT|.+ITs|.+TestCase|.+TestCases");
        customConfig.addAttribute("classAnnotationNameRegex", "SomeTestAnnotation");
        customConfig.addAttribute("methodAnnotationNameRegex", "");

        final String[] expected = {
            "7:18: " + getCheckMessage(NameConventionForJunit4TestClassesCheck.MSG_KEY,
                    ".+Test\\d*|.+Tests\\d*|Test.+|Tests.+|.+IT|.+ITs|.+TestCase|.+TestCases"),
        };

        verify(customConfig, getPath("InputNameConventionForJunit4TestClassesCheck6.java"),
                expected);
    }

    @Test
    public void testJunitMethodTestAnnotationNamesRegexOption()
            throws Exception {
        final DefaultConfiguration customConfig =
                createModuleConfig(NameConventionForJunit4TestClassesCheck.class);
        customConfig.addAttribute("expectedClassNameRegex",
                ".+Test\\d*|.+Tests\\d*|Test.+|Tests.+|.+IT|.+ITs|.+TestCase|.+TestCases");
        customConfig.addAttribute("classAnnotationNameRegex", "");
        customConfig.addAttribute("methodAnnotationNameRegex", "SomeTestAnnotation");

        final String[] expected = {
            "6:18: " + getCheckMessage(NameConventionForJunit4TestClassesCheck.MSG_KEY,
                    ".+Test\\d*|.+Tests\\d*|Test.+|Tests.+|.+IT|.+ITs|.+TestCase|.+TestCases"),
        };

        verify(customConfig, getPath("InputNameConventionForJunit4TestClassesCheck7.java"),
                expected);
    }

    @Test
    public void testCorrectTestClassNameRegexOption()
            throws Exception {
        final DefaultConfiguration customConfig =
                createModuleConfig(NameConventionForJunit4TestClassesCheck.class);
        customConfig.addAttribute("expectedClassNameRegex", "Hello*");
        customConfig.addAttribute("classAnnotationNameRegex", "");
        customConfig.addAttribute("methodAnnotationNameRegex", "Test");

        final String[] expected = {
            "5:14: "
                    + getCheckMessage(NameConventionForJunit4TestClassesCheck.MSG_KEY, "Hello*"),
        };

        verify(customConfig, getPath("InputNameConventionForJunit4TestClassesCheck8.java"),
                expected);
    }

    @Test
    public void testNullProperties()
            throws Exception {
        final DefaultConfiguration customConfig =
                createModuleConfig(NameConventionForJunit4TestClassesCheck.class);
        customConfig.addAttribute("expectedClassNameRegex", null);
        customConfig.addAttribute("classAnnotationNameRegex", null);
        customConfig.addAttribute("methodAnnotationNameRegex", null);

        final String[] expected = {};

        verify(customConfig, getPath("InputNameConventionForJunit4TestClassesCheck8.java"),
                expected);
    }

    @Test
    public void testEmptyProperties()
            throws Exception {
        final DefaultConfiguration customConfig =
                createModuleConfig(NameConventionForJunit4TestClassesCheck.class);
        customConfig.addAttribute("expectedClassNameRegex", "");
        customConfig.addAttribute("classAnnotationNameRegex", "");
        customConfig.addAttribute("methodAnnotationNameRegex", "");

        final String[] expected = {};

        verify(customConfig, getPath("InputNameConventionForJunit4TestClassesCheck8.java"),
                expected);
    }

}
