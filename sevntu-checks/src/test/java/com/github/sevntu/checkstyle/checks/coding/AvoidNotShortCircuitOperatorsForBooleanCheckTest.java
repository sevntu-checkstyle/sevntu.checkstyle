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

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.github.sevntu.checkstyle.checks.coding.AvoidNotShortCircuitOperatorsForBooleanCheck;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import java.io.File;
import org.junit.Test;

public class AvoidNotShortCircuitOperatorsForBooleanCheckTest extends BaseCheckTestSupport
{

    private final DefaultConfiguration checkConfig = createCheckConfig(AvoidNotShortCircuitOperatorsForBooleanCheck.class);

    @Test
    public final void testAll() throws Exception
    {

        String[] expected = {
            "6:17: " + createMsg("|"),
            "25:20: " + createMsg("|"),
            "35:25: " + createMsg("|"),
            "48:25: " + createMsg("|"),
            "53:16: " + createMsg("&"),
            "64:17: " + createMsg("|"),
            "71:9: " + createMsg("|"),
            "79:9: " + createMsg("|"),
            "88:17: " + createMsg("|"),
            "94:22: " + createMsg("|"),
            "95:14: " + createMsg("|"),
            "96:14: " + createMsg("|"),
            "97:11: " + createMsg("|="),
        };

        verify(checkConfig, getPath("coding" + File.separator
                + "InputAvoidNotShortCircuitOperatorsForBooleanCheck.java"),
                expected);
    }

    public String createMsg(String literal)
    {
        return "Not short-circuit Operator '" + literal + "' used.";
    }

}
