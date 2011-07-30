package com.puppycrawl.tools.checkstyle.checks.design;

import java.io.File;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class VariableDeclarationUsageDistanceCheckTest extends BaseCheckTestSupport {
	@Test
	public void testGeneralLogic() throws Exception {
		final DefaultConfiguration checkConfig = createCheckConfig(VariableDeclarationUsageDistanceCheck.class);
		checkConfig.addAttribute("distance", "0");
		checkConfig.addAttribute("regExp", "");
		final String[] expected = {
				"11: Declaration of variable 'b' should be here.",
				"11: Declaration of variable 'd' should be here.",
				"17: Declaration of variable 'a' should be here.",
				"17: Declaration of variable 'c' should be here.",
				"19: Declaration of variable 'b' should be here.",
				"31: Declaration of variable 'b' should be here.",
				"33: Declaration of variable 'a' should be here.",
				"39: Declaration of variable 'temp' should be here.",
				"45: Declaration of variable 'temp' should be here.",
				"48: Declaration of variable 'result' should be here.",
				"48: Declaration of variable 'str' should be here.",
				"60: Declaration of variable 'a' should be here.",
				"60: Declaration of variable 'b' should be here.",
				"65: Declaration of variable 'count' should be here.",
				"73: Declaration of variable 'a' should be here.",
				"73: Declaration of variable 'b' should be here.",
				"76: Declaration of variable 'count' should be here.",
				"81: Declaration of variable 'd' should be here.",
				"105: Declaration of variable 'block' should be here.",
				"105: Declaration of variable 'blockNumWithSimilarVar' should be here.",
				"105: Declaration of variable 'dist' should be here.",
				"105: Declaration of variable 'index' should be here.",
				"125: Declaration of variable 'res' should be here.",
		};
		verify(checkConfig, getPath("design" + File.separator + "InputVariableDeclarationUsageDistanceCheck.java"), expected);
	}
	
	@Test
	public void testDistance() throws Exception {
		final DefaultConfiguration checkConfig = createCheckConfig(VariableDeclarationUsageDistanceCheck.class);
		checkConfig.addAttribute("distance", "2");
		checkConfig.addAttribute("regExp", "");
		final String[] expected = {
				"33: Declaration of variable 'a' should be here.",
				"65: Declaration of variable 'count' should be here.",
				"76: Declaration of variable 'count' should be here.",
				"105: Declaration of variable 'blockNumWithSimilarVar' should be here.",
				"105: Declaration of variable 'dist' should be here.",
		};
		verify(checkConfig, getPath("design" + File.separator + "InputVariableDeclarationUsageDistanceCheck.java"), expected);
	}
	
	@Test
	public void testVariableRegExp() throws Exception {
		final DefaultConfiguration checkConfig = createCheckConfig(VariableDeclarationUsageDistanceCheck.class);
		checkConfig.addAttribute("distance", "0");
		checkConfig.addAttribute("regExp", "a|b|c|d|block|dist");
		final String[] expected = {
				"39: Declaration of variable 'temp' should be here.",
				"45: Declaration of variable 'temp' should be here.",
				"48: Declaration of variable 'result' should be here.",
				"48: Declaration of variable 'str' should be here.",
				"65: Declaration of variable 'count' should be here.",
				"76: Declaration of variable 'count' should be here.",
				"105: Declaration of variable 'blockNumWithSimilarVar' should be here.",
				"105: Declaration of variable 'index' should be here.",
				"125: Declaration of variable 'res' should be here.",
		};
		verify(checkConfig, getPath("design" + File.separator + "InputVariableDeclarationUsageDistanceCheck.java"), expected);
	}
}
