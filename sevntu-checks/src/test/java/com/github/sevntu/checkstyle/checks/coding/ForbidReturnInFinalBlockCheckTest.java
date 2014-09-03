////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2012  Oliver Burn
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

import static com.github.sevntu.checkstyle.checks.coding.ForbidReturnInFinalBlockCheck.*;

import org.junit.Test;
import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class ForbidReturnInFinalBlockCheckTest 
        extends BaseCheckTestSupport
{
    /**
     * An error message for current check.
     */
    private final String warningMessage = getCheckMessage(MSG_KEY);
    @Test
    public void testDefault()
            throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(ForbidReturnInFinalBlockCheck.class);
        final String[] expected = {
                "13: " + warningMessage,
                "26: " + warningMessage,
                "46: " + warningMessage,
                "53: " + warningMessage };
        verify(checkConfig, getPath("InputForbidReturnInFinalBlockCheck.java"),
                expected);
    }
}
