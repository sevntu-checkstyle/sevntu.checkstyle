package com.puppycrawl.tools.checkstyle.checks.design;

import java.io.File;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class VariableDeclarationUsageDistanceCheckTest extends BaseCheckTestSupport {
	@Test
	public void testDistance() throws Exception {
		final DefaultConfiguration checkConfig = createCheckConfig(VariableDeclarationUsageDistanceCheck.class);
//		checkConfig.addAttribute("distance", "0");
//		checkConfig.addAttribute("ignoreVariablePattern", "temp");
		final String[] expected = {
				"12: Declaration of variable 'a' should be here.",
				"25: Declaration of variable 'str' should be here.",
				"36: Declaration of variable 'a' should be here.",
				"37: Declaration of variable 'count' should be here.",
				"48: Declaration of variable 'count' should be here.",
		};
//		System.setProperty("testinputs.dir",
//				"/home/ruslan/git/sevntu.checkstyle/checkstyle-sevntu/src/testinputs/com/puppycrawl/tools/checkstyle/design");
		verify(checkConfig, getPath("design" + File.separator + "InputVariableDeclarationUsageDistanceCheck.java"), expected);
	}
}
