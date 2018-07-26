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

import static com.github.sevntu.checkstyle.checks.coding.EnumTrailingCommaAndSemicolonCheck.MSG_KEY;
import static com.github.sevntu.checkstyle.checks.coding.EnumTrailingCommaAndSemicolonCheck.MSG_KEY_SEMI;

import org.junit.Assert;
import org.junit.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class EnumTrailingCommaAndSemicolonCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/coding";
    }

    @Test
    public void testDefault() throws Exception {
        final DefaultConfiguration checkConfig =
            createModuleConfig(EnumTrailingCommaAndSemicolonCheck.class);
        final String[] expected = {
            "14: " + getCheckMessage(MSG_KEY),
            "20: " + getCheckMessage(MSG_KEY),
            "26: " + getCheckMessage(MSG_KEY_SEMI),
        };
        verify(checkConfig, getPath("InputEnumTrailingCommaAndSemicolonCheck.java"), expected);
    }

    @Test
    public void testTokensNotNull() {
        final EnumTrailingCommaAndSemicolonCheck check = new EnumTrailingCommaAndSemicolonCheck();
        Assert.assertNotNull(check.getAcceptableTokens());
        Assert.assertNotNull(check.getDefaultTokens());
        Assert.assertNotNull(check.getRequiredTokens());
    }
}
