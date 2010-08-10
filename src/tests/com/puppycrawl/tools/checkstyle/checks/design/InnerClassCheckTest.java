package com.puppycrawl.tools.checkstyle.checks.design;

import java.io.File;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class InnerClassCheckTest extends BaseCheckTestSupport {

	@Test
	public void testMembersBeforeInner() throws Exception {
		final DefaultConfiguration checkConfig =
				createCheckConfig(InnerClassCheck.class);
		final String[] expected = {
				"18:17: Fields and methods should be before inner classes.",
				"28:17: Fields and methods should be before inner classes.",
				"29:17: Fields and methods should be before inner classes.",
				"42:25: Fields and methods should be before inner classes.",
				"43:25: Fields and methods should be before inner classes.",
				"47:9: Fields and methods should be before inner classes.",
				"63:25: Fields and methods should be before inner classes.",
				"64:25: Fields and methods should be before inner classes.",
				"68:9: Fields and methods should be before inner classes.",
				"72:9: Fields and methods should be before inner classes.",
				"81:5: Fields and methods should be before inner classes.",
		};
		System.setProperty("testinputs.dir",
        "/home/romani/Practice/New_workspace/sevntu.checkstyle/src/testinputs/com/puppycrawl/tools/checkstyle");
		verify(checkConfig, getPath("design" + File.separator + "InputInnerClassCheck.java"), expected);
	}
}
