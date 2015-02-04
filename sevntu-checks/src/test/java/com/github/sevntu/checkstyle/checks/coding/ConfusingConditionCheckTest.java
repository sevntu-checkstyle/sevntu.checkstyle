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

import static com.github.sevntu.checkstyle.checks.coding.ConfusingConditionCheck.MSG_KEY;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

/**
 * @author <a href="mailto:vadim.panasiuk@gmail.com">Vadim Panasiuk</a>
 */
public class ConfusingConditionCheckTest extends BaseCheckTestSupport
{

	private final String warningMessage = getCheckMessage(MSG_KEY);
	
	private final DefaultConfiguration checkConfig = createCheckConfig(ConfusingConditionCheck.class);

    @Test
	public void testDefault() throws Exception
    {
		checkConfig.addAttribute("ignoreInnerIf", "true");
		checkConfig.addAttribute("ignoreNullCaseInIf", "true");
		checkConfig.addAttribute("ignoreSequentialIf", "true");
		checkConfig.addAttribute("ignoreThrowInElse", "true");
		checkConfig.addAttribute("multiplyFactorForElseBlocks", "4");
        
        final String[] expected = {
                "10: " + warningMessage,
                "13: " + warningMessage,
                "16: " + warningMessage,
                "19: " + warningMessage,
                "22: " + warningMessage,
                "105: " + warningMessage,
                "108: " + warningMessage,
                "111: " + warningMessage,
                "149: " + warningMessage,
                "166: " + warningMessage,
                "177: " + warningMessage, //!!!
                "181: " + warningMessage,
                "200: " + warningMessage,
                "215: " + warningMessage,
                "231: " + warningMessage,};
        
        verify(checkConfig, getPath("InputConfusingConditionCheck.java"),
                expected);
    }
}
