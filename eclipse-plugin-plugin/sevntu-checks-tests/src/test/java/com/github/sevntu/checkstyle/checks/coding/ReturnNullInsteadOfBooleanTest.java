package com.github.sevntu.checkstyle.checks.coding;

import java.io.File;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class ReturnNullInsteadOfBooleanTest extends BaseCheckTestSupport {
	@Test
	public void testDefault() throws Exception {
		final DefaultConfiguration checkConfig =
				createCheckConfig(ReturnNullInsteadOfBoolean.class);
		final String[] expected = {
				"11:9: Method declares to return Boolean and returns null.",
				"22:25: Method declares to return Boolean and returns null."
		};
		verify(checkConfig, getPath("coding" + File.separator + "InputReturnNullInsteadOfBoolean.java"), expected);
	}
}
