package com.puppycrawl.tools.checkstyle.checks.coding;

import java.io.File;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class RedundantReturnTest extends BaseCheckTestSupport {
	@Test
	public void testDefault() throws Exception {
		final DefaultConfiguration checkConfig = createCheckConfig(RedundantReturnCheck.class);
		checkConfig.addAttribute("flag","false");
		
		final String[] expected = {
				"19: Redundant return.",
				"34: Redundant return.",
				"41: Redundant return."
				};
		
		final String[] expected2 ={
				"12: Redundant return.",
				"19: Redundant return.",
				"24: Redundant return.",
				"34: Redundant return.",
				"41: Redundant return.",
				"60: Redundant return."
		};
		
		
		if (checkConfig.getAttribute("flag").equalsIgnoreCase("true")){
			verify(checkConfig, getPath("coding" + File.separator
				+ "InputRedundantReturn.java"), expected);
		}
		else {
			verify(checkConfig, getPath("coding" + File.separator
					+ "InputRedundantReturn.java"), expected2);
		}
	}
}
