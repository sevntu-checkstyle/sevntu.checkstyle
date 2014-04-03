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
package com.github.sevntu.checkstyle.checks.coding;

import static com.github.sevntu.checkstyle.checks.coding.AvoidModifiersForTypesCheck.*;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.github.sevntu.checkstyle.checks.coding.AvoidModifiersForTypesCheck;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

/**
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public class AvoidModifiersForTypesCheckTest extends BaseCheckTestSupport
{

    private final DefaultConfiguration checkConfig = createCheckConfig(AvoidModifiersForTypesCheck.class);

    @Test
    public void testFinal() throws Exception
    {
        String finalRegexp = "InputAvoidModifiersForTypesCheck";
        String staticRegexp = null;
        String transientRegexp = null;
        String volatileRegexp = null;

        checkConfig.addAttribute("forbiddenClassesRegexpFinal", finalRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpStatic", staticRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpTransient", transientRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpVolatile", volatileRegexp);

        String[] expected = {
        	"11:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "final"),
        	"12:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "final"),
        	"19:9: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "final"),
        };

        verify(checkConfig, getPath("InputAvoidModifiersForTypesCheck.java"), expected);
    }

    @Test
    public void testStatic() throws Exception
    {
        String finalRegexp = null;
        String staticRegexp = "File";
        String transientRegexp = null;
        String volatileRegexp = null;

        checkConfig.addAttribute("forbiddenClassesRegexpFinal", finalRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpStatic", staticRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpTransient", transientRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpVolatile", volatileRegexp);

        String[] expected = {
        	"10:5: " + getCheckMessage(MSG_KEY, "File", "static"),
        };

        verify(checkConfig, getPath("InputAvoidModifiersForTypesCheck.java"), expected);
    }

    @Test
    public void testTransient() throws Exception
    {
        String finalRegexp = null;
        String staticRegexp = null;
        String transientRegexp = "InputAvoidModifiersForTypesCheck";
        String volatileRegexp = null;

        checkConfig.addAttribute("forbiddenClassesRegexpFinal", finalRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpStatic", staticRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpTransient", transientRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpVolatile", volatileRegexp);

        String[] expected = {
        	"13:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "transient"),
        };

        verify(checkConfig, getPath("InputAvoidModifiersForTypesCheck.java"), expected);
    }

    @Test
    public void testVolatile() throws Exception
    {
        String finalRegexp = null;
        String staticRegexp = null;
        String transientRegexp = null;
        String volatileRegexp = "InputAvoidModifiersForTypesCheck";

        checkConfig.addAttribute("forbiddenClassesRegexpFinal", finalRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpStatic", staticRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpTransient", transientRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpVolatile", volatileRegexp);

        String[] expected = {
        	"14:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "volatile"),
        };

        verify(checkConfig, getPath("InputAvoidModifiersForTypesCheck.java"), expected);
    }

    @Test
    public void testFinalAndStatic() throws Exception
    {
        String finalRegexp = "InputAvoidModifiersForTypesCheck";
        String staticRegexp = "InputAvoidModifiersForTypesCheck";
        String transientRegexp = null;
        String volatileRegexp = null;

        checkConfig.addAttribute("forbiddenClassesRegexpFinal", finalRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpStatic", staticRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpTransient", transientRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpVolatile", volatileRegexp);

        String[] expected = {
        	"11:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "final"),
        	"12:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "final"), // both
        	"12:5: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "static"), // both
        	"19:9: " + getCheckMessage(MSG_KEY, "InputAvoidModifiersForTypesCheck", "final"),
        };

        verify(checkConfig, getPath("InputAvoidModifiersForTypesCheck.java"), expected);
    }

    @Test
    public void testAnyFile() throws Exception
    {
        String finalRegexp = null;
        String staticRegexp = null;
        String transientRegexp = null;
        String volatileRegexp = null;

        checkConfig.addAttribute("forbiddenClassesRegexpFinal", finalRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpStatic", staticRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpTransient", transientRegexp);
        checkConfig.addAttribute("forbiddenClassesRegexpVolatile", volatileRegexp);

        String[] expected = {};

        verify(checkConfig, getPath("InputAvoidModifiersForTypesCheck2.java"), expected);
    }
}
