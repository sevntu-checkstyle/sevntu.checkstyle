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

import static com.github.sevntu.checkstyle.checks.design.AvoidConditionInversionCheck.MSG_KEY;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.DetailAstImpl;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class AvoidConditionInversionCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/design";
    }

    @Test
    public void defaultTest() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(AvoidConditionInversionCheck.class);
        final String[] expected = {
            "7:24: " + getCheckMessage(MSG_KEY),
            "11:24: " + getCheckMessage(MSG_KEY),
            "15:24: " + getCheckMessage(MSG_KEY),
            "19:21: " + getCheckMessage(MSG_KEY),
            "23:24: " + getCheckMessage(MSG_KEY),
            "29:27: " + getCheckMessage(MSG_KEY),
            "31:34: " + getCheckMessage(MSG_KEY),
            "39:24: " + getCheckMessage(MSG_KEY),
            "102:21: " + getCheckMessage(MSG_KEY),
            "107:21: " + getCheckMessage(MSG_KEY),
        };

        verify(checkConfig, getPath("InputAvoidConditionInversionCheck.java"),
                expected);
    }

    @Test
    public void avoidOnlyRelationalOperandsInCondition() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(AvoidConditionInversionCheck.class);
        checkConfig.addProperty("applyOnlyToRelationalOperands",
                "true");

        final String[] expected = {
            "7:24: " + getCheckMessage(MSG_KEY),
            "11:24: " + getCheckMessage(MSG_KEY),
            "15:24: " + getCheckMessage(MSG_KEY),
            "19:21: " + getCheckMessage(MSG_KEY),
            "23:24: " + getCheckMessage(MSG_KEY),
            "29:27: " + getCheckMessage(MSG_KEY),
            "31:34: " + getCheckMessage(MSG_KEY),
        };

        verify(checkConfig, getPath("InputAvoidConditionInversionCheck.java"),
                expected);
    }

    @Test
    public void testUnsupportedNode() {
        final DetailAstImpl sync = new DetailAstImpl();
        sync.setType(TokenTypes.LITERAL_SYNCHRONIZED);

        try {
            final AvoidConditionInversionCheck check = new AvoidConditionInversionCheck();
            check.visitToken(sync);

            fail("exception expected");
        }
        catch (IllegalArgumentException exc) {
            Assertions.assertEquals("Found unsupported token: LITERAL_SYNCHRONIZED",
                exc.getMessage());
        }
    }

}
