package com.github.sevntu.checkstyle.checks.coding;

import static com.github.sevntu.checkstyle.checks.coding.ReturnNullInsteadOfBoolean.*;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.github.sevntu.checkstyle.checks.coding.ReturnNullInsteadOfBoolean;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class ReturnNullInsteadOfBooleanTest extends BaseCheckTestSupport {
	
	private final String warningMessage = getCheckMessage(MSG_KEY);
	
	@Test
	public void testDefault() throws Exception {
		final DefaultConfiguration checkConfig =
				createCheckConfig(ReturnNullInsteadOfBoolean.class);
		final String[] expected = {
				"11:9: " + warningMessage,
				"22:25: " + warningMessage,
		};
		verify(checkConfig, getPath("InputReturnNullInsteadOfBoolean.java"), expected);
	}
}
