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
				"15:17: Fields and methods should be before inner classes.",
				"25:17: Fields and methods should be before inner classes.",
				"26:17: Fields and methods should be before inner classes.",
				"39:25: Fields and methods should be before inner classes.",
				"40:25: Fields and methods should be before inner classes.",
				"44:9: Fields and methods should be before inner classes."
		};
		verify(checkConfig, getPath("design" + File.separator + "InputInnerClassCheck.java"), expected);
	}

}
