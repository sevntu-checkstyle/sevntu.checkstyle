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

package com.github.sevntu.checkstyle.checks.coding;

import static com.github.sevntu.checkstyle.checks.coding.UselessSingleCatchCheck.MSG_KEY;

import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class UselessSingleCatchCheckTest extends AbstractModuleTestSupport {

    private final String warningMessage = getCheckMessage(MSG_KEY);

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/coding";
    }

    @Test
    public void testMultiCatch() throws Exception {
        final DefaultConfiguration config = createModuleConfig(UselessSingleCatchCheck.class);
        final String[] expected = {};

        verify(config, getPath("InputUselessSingleCatchCheck1.java"), expected);
    }

    @Test
    public void testWrappingCatch() throws Exception {
        final DefaultConfiguration config = createModuleConfig(UselessSingleCatchCheck.class);
        final String[] expected = {};

        verify(config, getPath("InputUselessSingleCatchCheck2.java"), expected);
    }

    @Test
    public void testLoggingCatch() throws Exception {
        final DefaultConfiguration config = createModuleConfig(UselessSingleCatchCheck.class);
        final String[] expected = {};

        verify(config, getPath("InputUselessSingleCatchCheck3.java"), expected);
    }

    @Test
    public void testThrowsAnotherException() throws Exception {
        final DefaultConfiguration config = createModuleConfig(UselessSingleCatchCheck.class);
        final String[] expected = {};

        verify(config, getPath("InputUselessSingleCatchCheck4.java"), expected);
    }

    @Test
    public void testUselessCatch() throws Exception {
        final DefaultConfiguration config = createModuleConfig(UselessSingleCatchCheck.class);
        final String[] expected = {
            "15:9: " + warningMessage,
        };

        verify(config, getPath("InputUselessSingleCatchCheck5.java"), expected);
    }

}
