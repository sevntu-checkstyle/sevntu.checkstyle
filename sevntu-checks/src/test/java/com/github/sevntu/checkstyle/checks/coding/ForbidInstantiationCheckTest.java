////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2015 the original author or authors.
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

import static com.github.sevntu.checkstyle.checks.coding.ForbidInstantiationCheck.*;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.github.sevntu.checkstyle.checks.coding.ForbidInstantiationCheck;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

/**
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil
 *         Yaroslavtsev</a>
 */
public class ForbidInstantiationCheckTest extends BaseCheckTestSupport
{
    private final DefaultConfiguration checkConfig = createCheckConfig(ForbidInstantiationCheck.class);

    @Test
    public void testNullPointerException() throws Exception
    {

        checkConfig.addAttribute("forbiddenClasses", "java.lang.NullPointerException");

        String[] expected = {
        	"9:35: " + getCheckMessage(MSG_KEY, "NullPointerException"),
        	"12:36: " + getCheckMessage(MSG_KEY, "NullPointerException"),
        };

        verify(checkConfig, getPath("InputForbidInstantiationCheck.java"), expected);
    }

    @Test
    public void testNormalWork() throws Exception
    {

        checkConfig.addAttribute("forbiddenClasses", "java.io.File , java.lang.String , ");

        String[] expected = {
        	"13:21: " + getCheckMessage(MSG_KEY, "File"),
        	"14:22: " + getCheckMessage(MSG_KEY, "File"),
        	"15:20: " + getCheckMessage(MSG_KEY, "String"),
        };

        verify(checkConfig, getPath("InputForbidInstantiationCheck.java"), expected);
    }

    @Test
    public void testNormalWork2() throws Exception
    {

        checkConfig.addAttribute("forbiddenClasses", "com.github.sevntu.checkstyle.checks.test.utils.File");

        String[] expected = {
        	"12:68: " + getCheckMessage(MSG_KEY, "File"),
        };

        verify(checkConfig, getPath("InputForbidInstantiationCheckWithoutDots.java"), expected);
    }

    @Test
    public void testNormalWork3() throws Exception
    {

        checkConfig.addAttribute("forbiddenClasses", "java.io.File , java.lang.String , ");

        String[] expected = {
        	"13:22: " + getCheckMessage(MSG_KEY, "File"),
        	"14:20: " + getCheckMessage(MSG_KEY, "String"),
        };

        verify(checkConfig, getPath("InputForbidInstantiationCheckWithoutDots.java"), expected);
    }


    @Test
    public void testAsteriskInInput() throws Exception
    {

        checkConfig.addAttribute("forbiddenClasses", "java.io.File , java.lang.String , ");

        String[] expected = {
        	"13:21: " + getCheckMessage(MSG_KEY, "File"),
        	"14:22: " + getCheckMessage(MSG_KEY, "File"),
        	"15:20: " + getCheckMessage(MSG_KEY, "String"),
        };

        verify(checkConfig, getPath("InputForbidInstantiationCheckWithAsterisk.java"), expected);
    }
}
