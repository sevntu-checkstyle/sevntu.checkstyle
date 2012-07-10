package com.github.sevntu.checkstyle.checks.design;

import java.io.File;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.github.sevntu.checkstyle.checks.design.InnerClassCheck;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class InnerClassCheckTest extends BaseCheckTestSupport {

	@Test
	public void testMembersBeforeInner() throws Exception {
		final DefaultConfiguration checkConfig =
				createCheckConfig(InnerClassCheck.class);
		final String[] expected = {
				"15:17: Fields and methods should be before inner classes.",
				"25:17: Fields and methods should be before inner classes.",
				"26:17: Fields and methods should be before inner classes.",
				"39:25: Fields and methods should be before inner classes.",
				"40:25: Fields and methods should be before inner classes.",
				"44:9: Fields and methods should be before inner classes.",
				"60:25: Fields and methods should be before inner classes.",
				"61:25: Fields and methods should be before inner classes.",
				"65:9: Fields and methods should be before inner classes.",
				"69:9: Fields and methods should be before inner classes.",
				"78:5: Fields and methods should be before inner classes.",
		};
		//System.setProperty("testinputs.dir",
       // "/home/romani/Practice/New_workspace/sevntu.checkstyle/src/testinputs/com/puppycrawl/tools/checkstyle");
		verify(checkConfig, getPath("design" + File.separator + "InputInnerClassCheck.java"), expected);
	}
}
