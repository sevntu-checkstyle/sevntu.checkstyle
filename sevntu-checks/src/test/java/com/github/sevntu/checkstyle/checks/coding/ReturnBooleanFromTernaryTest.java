package com.github.sevntu.checkstyle.checks.coding;

import java.io.File;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.github.sevntu.checkstyle.checks.coding.ReturnBooleanFromTernary;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class ReturnBooleanFromTernaryTest extends BaseCheckTestSupport {
	@Test
	public void testDefault() throws Exception {
		final DefaultConfiguration checkConfig =
				createCheckConfig(ReturnBooleanFromTernary.class);
		final String[] expected = {
				"6:35: Returning explicit boolean from ternary operator.",
				"7:38: Returning explicit boolean from ternary operator.",
				"8:38: Returning explicit boolean from ternary operator.",
				"9:35: Returning explicit boolean from ternary operator.",
				"9:43: Returning explicit boolean from ternary operator."
		};
		verify(checkConfig, getPath("coding" + File.separator + "InputReturnBooleanFromTernary.java"), expected);
	}
}
