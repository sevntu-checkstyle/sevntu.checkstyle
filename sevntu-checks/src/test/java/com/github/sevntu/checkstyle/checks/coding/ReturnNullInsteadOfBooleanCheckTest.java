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

import static com.github.sevntu.checkstyle.checks.coding.ReturnNullInsteadOfBooleanCheck.MSG_KEY;
import static org.junit.Assert.fail;

import org.junit.Assert;
import org.junit.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class ReturnNullInsteadOfBooleanCheckTest extends AbstractModuleTestSupport {

    private final String warningMessage = getCheckMessage(MSG_KEY);

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/coding";
    }

    @Test
    public void testDefault() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(ReturnNullInsteadOfBooleanCheck.class);
        final String[] expected = {
            "11:9: " + warningMessage,
            "22:25: " + warningMessage,
        };
        verify(checkConfig, getPath("InputReturnNullInsteadOfBooleanCheck.java"), expected);
    }

    @Test
    public void testUnsupportedNodeVisit() {
        final DetailAST sync = new DetailAST();
        sync.setType(TokenTypes.LITERAL_SYNCHRONIZED);

        try {
            final ReturnNullInsteadOfBooleanCheck check = new ReturnNullInsteadOfBooleanCheck();
            check.visitToken(sync);

            fail();
        }
        catch (IllegalArgumentException ex) {
            Assert.assertEquals("Found unsupported token: LITERAL_SYNCHRONIZED", ex.getMessage());
        }
    }

    @Test
    public void testUnsupportedNodeLeave() {
        final DetailAST sync = new DetailAST();
        sync.setType(TokenTypes.LITERAL_SYNCHRONIZED);

        try {
            final ReturnNullInsteadOfBooleanCheck check = new ReturnNullInsteadOfBooleanCheck();
            check.leaveToken(sync);

            fail();
        }
        catch (IllegalArgumentException ex) {
            Assert.assertEquals("Found unsupported token: LITERAL_SYNCHRONIZED", ex.getMessage());
        }
    }

}
