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

import static com.github.sevntu.checkstyle.checks.coding.RequireFailForTryCatchInJunitCheck.MSG_KEY;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.DetailAstImpl;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.CommonUtil;

public class RequireFailForTryCatchInJunitCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/coding";
    }

    @Test
    public void testIndifferent1() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(RequireFailForTryCatchInJunitCheck.class);
        final String[] expected = CommonUtil.EMPTY_STRING_ARRAY;
        verify(checkConfig, getPath("InputRequireFailForTryCatchInJunitCheck1.java"), expected);
    }

    @Test
    public void testIndifferent2() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(RequireFailForTryCatchInJunitCheck.class);
        final String[] expected = CommonUtil.EMPTY_STRING_ARRAY;
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
        final String[] expected = CommonUtil.EMPTY_STRING_ARRAY;
        verify(checkConfig, getPath("InputRequireFailForTryCatchInJunitCheck5.java"), expected);
    }

    @Test
    public void testJunit5() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(RequireFailForTryCatchInJunitCheck.class);
        final String[] expected = {
            "33:9: " + getCheckMessage(MSG_KEY),
        };
        verify(checkConfig, getPath("InputRequireFailForTryCatchInJunitCheck6.java"), expected);
    }

    @Test
    public void testJunit4And5Mix() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(RequireFailForTryCatchInJunitCheck.class);
        final String[] expected = {
            "9:9: " + getCheckMessage(MSG_KEY),
            "18:9: " + getCheckMessage(MSG_KEY),
        };
        verify(checkConfig, getPath("InputRequireFailForTryCatchInJunitCheck7.java"), expected);
    }

    @Test
    public void testStaticWildCardImport() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(RequireFailForTryCatchInJunitCheck.class);
        final String[] expected = CommonUtil.EMPTY_STRING_ARRAY;
        verify(checkConfig, getPath("InputRequireFailForTryCatchInJunitCheck8.java"), expected);
    }

    @Test
    public void testStaticWildCardImportFailMissing() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(RequireFailForTryCatchInJunitCheck.class);
        final String[] expected = {
            "9:9: " + getCheckMessage(MSG_KEY),
        };
        verify(checkConfig, getPath("InputRequireFailForTryCatchInJunitCheck9.java"), expected);
    }

    @Test
    public void testAssertjSupport() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(RequireFailForTryCatchInJunitCheck.class);
        final String[] expected = {
            "81:9: " + getCheckMessage(MSG_KEY),
        };
        verify(checkConfig, getPath("InputRequireFailForTryCatchInJunitCheck10.java"), expected);
    }

    @Test
    public void testTruthSupportStaticImport() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(RequireFailForTryCatchInJunitCheck.class);
        final String[] expected = {
            "106:9: " + getCheckMessage(MSG_KEY),
            "117:9: " + getCheckMessage(MSG_KEY),
            "49:9: " + getCheckMessage(MSG_KEY),
        };
        verify(checkConfig, getPath(
            "InputRequireFailForTryCatchInJunitCheckTruthStaticImport.java"), expected);
    }

    @Test
    public void testTruthSupportStarImport() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(RequireFailForTryCatchInJunitCheck.class);
        final String[] expected = {
            "104:9: " + getCheckMessage(MSG_KEY),
            "115:9: " + getCheckMessage(MSG_KEY),
            "47:9: " + getCheckMessage(MSG_KEY),
        };
        verify(checkConfig, getPath(
            "InputRequireFailForTryCatchInJunitCheckTruthStarImport.java"), expected);
    }

    @Test
    public void testUnsupportedNode() {
        final DetailAstImpl sync = new DetailAstImpl();
        sync.setType(TokenTypes.LITERAL_SYNCHRONIZED);

        try {
            final RequireFailForTryCatchInJunitCheck check =
                    new RequireFailForTryCatchInJunitCheck();
            check.visitToken(sync);

            fail("exception expected");
        }
        catch (IllegalArgumentException exc) {
            Assertions.assertEquals("Found unsupported token: LITERAL_SYNCHRONIZED",
                exc.getMessage());
        }
    }
}
