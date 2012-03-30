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
package com.puppycrawl.tools.checkstyle.checks.coding;

import java.io.File;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
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
            "8:35: " + getMessage("NullPointerException"),
            "11:36: " + getMessage("NullPointerException"),
        };

        verify(checkConfig, getPath("coding" + File.separator
                + "InputForbidInstantiationCheck.java"), expected);
    }

    @Test
    public void testNormalWork() throws Exception
    {

        checkConfig.addAttribute("forbiddenClasses", "java.io.File , java.lang.String , ");

        String[] expected = {
            "13:21: " + getMessage("File"),
            "14:20: " + getMessage("String"),
        };

        verify(checkConfig, getPath("coding" + File.separator
                + "InputForbidInstantiationCheck.java"), expected);
    }

    private String getMessage(String className)
    {
        return "Instantiation of '" + className + "' is not allowed.";
    }


}
