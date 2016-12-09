////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2016 the original author or authors.
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

import static com.github.sevntu.checkstyle.checks.coding.ReturnBooleanFromTernaryCheck.MSG_KEY;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class ReturnBooleanFromTernaryCheckTest extends BaseCheckTestSupport {

    private final String warninigMessage = getCheckMessage(MSG_KEY);

    @Test
    public void testDefault() throws Exception {
        final DefaultConfiguration checkConfig =
                createCheckConfig(ReturnBooleanFromTernaryCheck.class);
        final String[] expected = {
            "6:35: " + warninigMessage,
            "7:38: " + warninigMessage,
            "8:38: " + warninigMessage,
            "9:35: " + warninigMessage,
            "9:43: " + warninigMessage,
            "10:28: " + warninigMessage,
            "11:27: " + warninigMessage,
        };
        verify(checkConfig, getPath("InputReturnBooleanFromTernaryCheck.java"), expected);
    }
}
