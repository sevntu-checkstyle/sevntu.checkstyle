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

import static com.github.sevntu.checkstyle.checks.coding.FinalizeImplementationCheck.MSG_KEY_MISSED_SUPER_FINALIZE_CALL;
import static com.github.sevntu.checkstyle.checks.coding.FinalizeImplementationCheck.MSG_KEY_MISSED_TRY_FINALLY;
import static com.github.sevntu.checkstyle.checks.coding.FinalizeImplementationCheck.MSG_KEY_PUBLIC_FINALIZE;
import static com.github.sevntu.checkstyle.checks.coding.FinalizeImplementationCheck.MSG_KEY_USELESS_FINALIZE;

import org.junit.jupiter.api.Test;

import com.puppycrawl.tools.checkstyle.AbstractModuleTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class FinalizeImplementationCheckTest extends AbstractModuleTestSupport {

    @Override
    protected String getPackageLocation() {
        return "com/github/sevntu/checkstyle/checks/coding";
    }

    @Test
    public final void basicTest() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(FinalizeImplementationCheck.class);
        final String[] expected = {
            "22:5: " + getCheckMessage(MSG_KEY_MISSED_TRY_FINALLY),
            "35:5: " + getCheckMessage(MSG_KEY_MISSED_TRY_FINALLY),
            "48:5: " + getCheckMessage(MSG_KEY_PUBLIC_FINALIZE),
            "62:5: " + getCheckMessage(MSG_KEY_USELESS_FINALIZE),
            "75:3: " + getCheckMessage(MSG_KEY_MISSED_SUPER_FINALIZE_CALL),
        };

        verify(checkConfig,
                getPath("InputFinalizeImplementationCheck.java"), expected);
    }

    @Test
    public final void testSpecialFinalize() throws Exception {
        final DefaultConfiguration checkConfig =
                createModuleConfig(FinalizeImplementationCheck.class);
        final String[] expected = {
            "5:5: " + getCheckMessage(MSG_KEY_MISSED_TRY_FINALLY),
            "18:5: " + getCheckMessage(MSG_KEY_MISSED_TRY_FINALLY),
        };

        verify(checkConfig, getPath("InputFinalizeImplementationCheckSpecial.java"),
                expected);
    }

}
