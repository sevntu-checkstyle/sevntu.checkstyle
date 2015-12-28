package com.github.sevntu.checkstyle.checks.coding;

import static com.github.sevntu.checkstyle.checks.coding.ReturnNullInsteadOfBooleanCheck.*;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class ReturnNullInsteadOfBooleanCheckTest extends BaseCheckTestSupport {
	
	private final String warningMessage = getCheckMessage(MSG_KEY);
	
	@Test
	public void testDefault() throws Exception {
		final DefaultConfiguration checkConfig =
				createCheckConfig(ReturnNullInsteadOfBooleanCheck.class);
		final String[] expected = {
				"11:9: " + warningMessage,
				"22:25: " + warningMessage,
		};
		verify(checkConfig, getPath("InputReturnNullInsteadOfBoolean.java"), expected);
	}
}
