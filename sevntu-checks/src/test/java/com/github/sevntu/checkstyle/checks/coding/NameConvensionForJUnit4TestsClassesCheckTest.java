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
 */
public class NameConvensionForJUnit4TestsClassesCheckTest extends BaseCheckTestSupport
{

    private final String msgClass = getCheckMessage(
            NameConvensionForJUnit4TestsClassesCheck.MSG_KEY);

    private final DefaultConfiguration mCheckConfig = createCheckConfig(
            NameConvensionForJUnit4TestsClassesCheck.class);

    @Test
    public void testExtendsTestCase() throws Exception
    {
        final String[] expected = {};
        verify(mCheckConfig,
                getPath("InputNameConvensionForTests1.java"),
                expected);
    }

    @Test
    public void testClassIsNotTest() throws Exception
    {
        final String[] expected = {};
        verify(mCheckConfig,
                getPath("InputNameConvensionForTests2.java"),
                expected);

    }

    @Test
    public void testClassJUnit4Test() throws Exception
    {
        final String[] expected = {};
        verify(mCheckConfig,
                getPath("InputNameConvensionForTests3.java"),
                expected);
    }

    @Test
    public void testEnum() throws Exception
    {
        final String[] expected = {};
        verify(mCheckConfig,
                getPath("InputNameConvensionForTests4.java"), expected);
    }

    @Test
    public void testInterface() throws Exception
    {
        final String[] expected = {};
        verify(mCheckConfig,
                getPath("InputNameConvensionForTests5.java"), expected);
    }

    @Test
    public void testNestedEnums() throws Exception
    {
        final String[] expected = {buildMesssage("1",
                ".+Test|Test.+|.+IT|.+TestCase"), };
        verify(mCheckConfig,
                getPath("InputNameConvensionForTests6.java"), expected);
    }

    @Test
    public void testAttribute() throws Exception
    {
        mCheckConfig.addAttribute("validTestClassNameRegex", "Hello*");
        final String[] expected = {buildMesssage("15", "Hello*"), };
        verify(mCheckConfig,
                getPath("InputNameConvensionForTests7.java"),
                expected);
    }

    @Test
    public void testQualifiedNameExtends() throws Exception
    {
        final String[] expected = {};
        verify(mCheckConfig,
                getPath("InputNameConvensionForTests8.java"),
                expected);
    }
    
    @Test 
    public void testAnnotationRunWith() throws Exception {
        final String[] expected = {buildMesssage("5", ".+Test|Test.+|.+IT|.+TestCase"), };
        verify(mCheckConfig,
                getPath("InputNameConvensionForTests10.java"),
                expected);
    }

    private String buildMesssage(String aLineNumber, String aArguments)
    {
        return aLineNumber + ": " + MessageFormat.format(msgClass, aArguments);
    }
}
