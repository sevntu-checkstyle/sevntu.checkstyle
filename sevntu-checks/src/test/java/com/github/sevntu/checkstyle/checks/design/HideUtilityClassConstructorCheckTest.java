///////////////////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code and other text files for adherence to a set of rules.
// Copyright (C) 2001-2026 the original author or authors.
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
///////////////////////////////////////////////////////////////////////////////////////////////

package com.github.sevntu.checkstyle.checks.design;

import static com.github.sevntu.checkstyle.checks.design.HideUtilityClassConstructorCheck.MSG_KEY;

import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class HideUtilityClassConstructorCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/design";
    }

    /** Only static methods and no constructor - default ctor is visible. */
    @Test
    public void testUtilClass() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(HideUtilityClassConstructorCheck.class);
        final String[] expected = {
            "11:1: " + getCheckMessage(MSG_KEY),
        };
        verify(checkConfig, getPath("InputHideUtilityClassConstructorCheck.java"), expected);
    }

    /** Non static methods - always OK. */
    @Test
    public void testNonUtilClass() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(HideUtilityClassConstructorCheck.class);
        final String[] expected = {
        };
        verify(checkConfig, getPath("InputHideUtilityClassConstructorCheck4.java"), expected);
    }

    @Test
    public void testDerivedNonUtilClass() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(HideUtilityClassConstructorCheck.class);
        final String[] expected = {
        };
        verify(checkConfig, getPath("InputHideUtilityClassConstructorCheck5.java"), expected);
    }

    @Test
    public void testOnlyNonstaticFieldNonUtilClass() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(HideUtilityClassConstructorCheck.class);
        final String[] expected = {
        };
        verify(checkConfig, getPath("InputHideUtilityClassConstructorCheck6.java"), expected);
    }

    @Test
    public void testEmptyAbstractClass() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(HideUtilityClassConstructorCheck.class);
        final String[] expected = {
        };
        verify(checkConfig, getPath("InputHideUtilityClassConstructorCheck1.java"), expected);
    }

    @Test
    public void testEmptyClassWithOnlyPrivateFields() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(HideUtilityClassConstructorCheck.class);
        final String[] expected = {
        };
        verify(checkConfig, getPath("InputHideUtilityClassConstructorCheck2.java"), expected);
    }

    @Test
    public void testClassWithStaticInnerClass() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(HideUtilityClassConstructorCheck.class);
        final String[] expected = {
        };
        verify(checkConfig, getPath("InputHideUtilityClassConstructorCheck3.java"), expected);
    }

}
