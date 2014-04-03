package com.github.sevntu.checkstyle.checks.coding;

import static com.github.sevntu.checkstyle.checks.coding.ReturnBooleanFromTernary.*;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.github.sevntu.checkstyle.checks.coding.ReturnBooleanFromTernary;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class ReturnBooleanFromTernaryTest extends BaseCheckTestSupport {
	
	private final String warninigMessage = getCheckMessage(MSG_KEY);
	
	@Test
	public void testDefault() throws Exception {
		final DefaultConfiguration checkConfig =
				createCheckConfig(ReturnBooleanFromTernary.class);
		final String[] expected = {
				"6:35: " + warninigMessage,
				"7:38: " + warninigMessage,
				"8:38: " + warninigMessage,
				"9:35: " + warninigMessage,
				"9:43: " + warninigMessage,
		};
		verify(checkConfig, getPath("InputReturnBooleanFromTernary.java"), expected);
	}
}
