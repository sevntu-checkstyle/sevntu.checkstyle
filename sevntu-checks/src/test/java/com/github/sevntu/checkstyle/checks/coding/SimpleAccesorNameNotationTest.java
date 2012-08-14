package com.github.sevntu.checkstyle.checks.coding;

import java.io.File;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.github.sevntu.checkstyle.checks.coding.SimpleAccesorNameNotationCheck;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class SimpleAccesorNameNotationTest extends BaseCheckTestSupport {

	@Test
	public void test() throws Exception {
		DefaultConfiguration checkConfig = createCheckConfig(SimpleAccesorNameNotationCheck.class);

		checkConfig.addAttribute("prefix", "m");

		final String[] expected1 = { "7: Unexpected setter name.",
				"10: Unexpected getter name.", "13: Unexpected setter name.",
				"16: Unexpected getter name.", "25: Unexpected setter name.",
				"28: Unexpected getter name.", };

		verify(checkConfig, getPath("coding" + File.separator
				+ "InputSimpleAccesorNameNotation.java"), expected1);
	}
}
