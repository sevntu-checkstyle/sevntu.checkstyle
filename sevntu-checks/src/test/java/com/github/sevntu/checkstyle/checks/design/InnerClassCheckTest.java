package com.github.sevntu.checkstyle.checks.design;

import static com.github.sevntu.checkstyle.checks.design.InnerClassCheck.*;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.github.sevntu.checkstyle.checks.design.InnerClassCheck;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class InnerClassCheckTest extends BaseCheckTestSupport {

	private final String warningMessage = getCheckMessage(MSG_KEY);
	
	@Test
	public void testMembersBeforeInner() throws Exception {
		final DefaultConfiguration checkConfig =
				createCheckConfig(InnerClassCheck.class);
		final String[] expected = {
				"15:17: " + warningMessage,
				"25:17: " + warningMessage,
				"26:17: " + warningMessage,
				"39:25: " + warningMessage,
				"40:25: " + warningMessage,
				"44:9: " + warningMessage,
				"60:25: " + warningMessage,
				"61:25: " + warningMessage,
				"65:9: " + warningMessage,
				"69:9: " + warningMessage,
				"78:5: " + warningMessage,
		};
		verify(checkConfig, getPath("InputInnerClassCheck.java"), expected);
	}
}
