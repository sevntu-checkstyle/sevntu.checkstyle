package com.puppycrawl.tools.checkstyle.checks.design;

import java.io.File;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class VariableDeclarationUsageDistanceCheckTest extends BaseCheckTestSupport {
	
	@Test
	public void testGeneralLogic() throws Exception {
		final DefaultConfiguration checkConfig = createCheckConfig(VariableDeclarationUsageDistanceCheck.class);
		checkConfig.addAttribute("allowedDistance", "1");
		checkConfig.addAttribute("ignoreVariablePattern", "");
		final String[] expected = {
				"11: Declaration of variable 'b' should be here.",
				"19: Declaration of variable 'a' should be here.",
				"19: Declaration of variable 'c' should be here.",
				"34: Declaration of variable 'a' should be here.",
				"40: Declaration of variable 'temp' should be here.",
				"46: Declaration of variable 'temp' should be here.",
				"49: Declaration of variable 'result' should be here.",
				"50: Declaration of variable 'str' should be here.",
				"61: Declaration of variable 'a' should be here.",
				"66: Declaration of variable 'count' should be here.",
				"74: Declaration of variable 'a' should be here.",
				"77: Declaration of variable 'count' should be here.",
				"83: Declaration of variable 'd' should be here.",
				"99: Declaration of variable 'b' should be here.",
				"100: Declaration of variable 'bb' should be here.",
				"102: Declaration of variable 'arg' should be here.",
				"111: Declaration of variable 'block' should be here.",
				"111: Declaration of variable 'blockNumWithSimilarVar' should be here.",
				"111: Declaration of variable 'dist' should be here.",
				"111: Declaration of variable 'index' should be here.",
				"146: Declaration of variable 'b' should be here.",
				"147: Declaration of variable 'c' should be here.",
				"151: Declaration of variable 'n' should be here.",
				"152: Declaration of variable 'm' should be here.",
				"161: Declaration of variable 'b1' should be here.",
				"162: Declaration of variable 'b2' should be here.",
				"162: Declaration of variable 'result' should be here.",
				"186: Declaration of variable 'b' should be here.",
				"188: Declaration of variable 'result' should be here.",
				"190: Declaration of variable 'a' should be here.",
				"199: Declaration of variable 'b1' should be here.",
				"200: Declaration of variable 'b3' should be here.",
				"201: Declaration of variable 'b2' should be here.",
				"201: Declaration of variable 'result' should be here.",
				"212: Declaration of variable 'i' should be here.",
				"213: Declaration of variable 'j' should be here.",
				"223: Declaration of variable 'a' should be here.",
				"223: Declaration of variable 'd1' should be here.",
				"225: Declaration of variable 'c' should be here.",
				"225: Declaration of variable 'd2' should be here.",
				"229: Declaration of variable 't' should be here.",
		};
		verify(checkConfig, getPath("design" + File.separator + "InputVariableDeclarationUsageDistanceCheck.java"), expected);
	}
	
	@Test
	public void testDistance() throws Exception {
		final DefaultConfiguration checkConfig = createCheckConfig(VariableDeclarationUsageDistanceCheck.class);
		checkConfig.addAttribute("allowedDistance", "3");
		checkConfig.addAttribute("ignoreVariablePattern", "");
		final String[] expected = {
				"34: Declaration of variable 'a' should be here.",
				"66: Declaration of variable 'count' should be here.",
				"77: Declaration of variable 'count' should be here.",
				"102: Declaration of variable 'arg' should be here.",
				"111: Declaration of variable 'blockNumWithSimilarVar' should be here.",
				"111: Declaration of variable 'dist' should be here.",
				"146: Declaration of variable 'b' should be here.",
				"152: Declaration of variable 'm' should be here.",
				"162: Declaration of variable 'result' should be here.",
				"190: Declaration of variable 'a' should be here.",
				"200: Declaration of variable 'b3' should be here.",
				"201: Declaration of variable 'result' should be here.",
				"223: Declaration of variable 'a' should be here.",
				"225: Declaration of variable 'c' should be here.",
				"229: Declaration of variable 't' should be here.",
		};
		verify(checkConfig, getPath("design" + File.separator + "InputVariableDeclarationUsageDistanceCheck.java"), expected);
	}
	
	@Test
	public void testVariableRegExp() throws Exception {
		final DefaultConfiguration checkConfig = createCheckConfig(VariableDeclarationUsageDistanceCheck.class);
		checkConfig.addAttribute("allowedDistance", "1");
		checkConfig.addAttribute("ignoreVariablePattern", "a|b|c|d|block|dist");
		final String[] expected = {
				"40: Declaration of variable 'temp' should be here.",
				"46: Declaration of variable 'temp' should be here.",
				"49: Declaration of variable 'result' should be here.",
				"50: Declaration of variable 'str' should be here.",
				"66: Declaration of variable 'count' should be here.",
				"77: Declaration of variable 'count' should be here.",
				"100: Declaration of variable 'bb' should be here.",
				"102: Declaration of variable 'arg' should be here.",
				"111: Declaration of variable 'blockNumWithSimilarVar' should be here.",
				"111: Declaration of variable 'index' should be here.",
				"151: Declaration of variable 'n' should be here.",
				"152: Declaration of variable 'm' should be here.",
				"161: Declaration of variable 'b1' should be here.",
				"162: Declaration of variable 'b2' should be here.",
				"162: Declaration of variable 'result' should be here.",
				"188: Declaration of variable 'result' should be here.",
				"199: Declaration of variable 'b1' should be here.",
				"200: Declaration of variable 'b3' should be here.",
				"201: Declaration of variable 'b2' should be here.",
				"201: Declaration of variable 'result' should be here.",
				"212: Declaration of variable 'i' should be here.",
				"213: Declaration of variable 'j' should be here.",
				"223: Declaration of variable 'd1' should be here.",
				"225: Declaration of variable 'd2' should be here.",
				"229: Declaration of variable 't' should be here.",
		};
		verify(checkConfig, getPath("design" + File.separator + "InputVariableDeclarationUsageDistanceCheck.java"), expected);
	}
}
