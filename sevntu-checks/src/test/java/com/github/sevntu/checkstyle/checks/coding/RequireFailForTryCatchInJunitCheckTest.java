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

import static com.github.sevntu.checkstyle.checks.coding.RequireFailForTryCatchInJunitCheck.MSG_KEY;
import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.CommonUtils;

public class RequireFailForTryCatchInJunitCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/coding";
    }

    @Test
    public void testIndifferent1() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(RequireFailForTryCatchInJunitCheck.class);
        final String[] expected = CommonUtils.EMPTY_STRING_ARRAY;
        verify(checkConfig, getPath("InputRequireFailForTryCatchInJunitCheck1.java"), expected);
    }

    @Test
    public void testIndifferent2() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(RequireFailForTryCatchInJunitCheck.class);
        final String[] expected = CommonUtils.EMPTY_STRING_ARRAY;
        verify(checkConfig, getPath("InputRequireFailForTryCatchInJunitCheck2.java"), expected);
    }

    @Test
    public void testBad1() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(RequireFailForTryCatchInJunitCheck.class);
        final String[] expected = {
            "8:9: " + getCheckMessage(MSG_KEY),
            "12:9: " + getCheckMessage(MSG_KEY),
            "18:9: " + getCheckMessage(MSG_KEY),
            "23:9: " + getCheckMessage(MSG_KEY),
            "28:9: " + getCheckMessage(MSG_KEY),
            "33:9: " + getCheckMessage(MSG_KEY),
            "42:9: " + getCheckMessage(MSG_KEY),
            "46:9: " + getCheckMessage(MSG_KEY),
            "52:9: " + getCheckMessage(MSG_KEY),
            "57:9: " + getCheckMessage(MSG_KEY),
            "62:9: " + getCheckMessage(MSG_KEY),
            "67:9: " + getCheckMessage(MSG_KEY),
            "76:9: " + getCheckMessage(MSG_KEY),
        };
        verify(checkConfig, getPath("InputRequireFailForTryCatchInJunitCheck3.java"), expected);
    }

    @Test
    public void testBad2() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(RequireFailForTryCatchInJunitCheck.class);
        final String[] expected = {
            "9:9: " + getCheckMessage(MSG_KEY),
            "14:9: " + getCheckMessage(MSG_KEY),
        };
        verify(checkConfig, getPath("InputRequireFailForTryCatchInJunitCheck4.java"), expected);
    }

    @Test
    public void testGood() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(RequireFailForTryCatchInJunitCheck.class);
        final String[] expected = CommonUtils.EMPTY_STRING_ARRAY;
        verify(checkConfig, getPath("InputRequireFailForTryCatchInJunitCheck5.java"), expected);
    }

    @Test
    public void testUnsupportedNode() {
        final DetailAST sync = new DetailAST();
        sync.setType(TokenTypes.LITERAL_SYNCHRONIZED);

        try {
            final RequireFailForTryCatchInJunitCheck check =
                    new RequireFailForTryCatchInJunitCheck();
            check.visitToken(sync);

            fail();
        }
        catch (IllegalArgumentException ex) {
            Assert.assertEquals("Found unsupported token: LITERAL_SYNCHRONIZED", ex.getMessage());
        }
    }

}
