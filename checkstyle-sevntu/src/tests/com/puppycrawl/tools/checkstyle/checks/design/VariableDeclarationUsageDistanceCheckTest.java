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
		checkConfig.addAttribute("validateBetweenScopes", "true");
		final String[] expected = {
				"30: Distance between variable 'a' declaration and its first usage is 2, but allowed 1.",
				"38: Distance between variable 'temp' declaration and its first usage is 2, but allowed 1.",
				"44: Distance between variable 'temp' declaration and its first usage is 2, but allowed 1.",
				"57: Distance between variable 'count' declaration and its first usage is 2, but allowed 1.",
				"71: Distance between variable 'count' declaration and its first usage is 4, but allowed 1.",
				"96: Distance between variable 'arg' declaration and its first usage is 2, but allowed 1.",
				"144: Distance between variable 'm' declaration and its first usage is 3, but allowed 1.",
				"145: Distance between variable 'n' declaration and its first usage is 2, but allowed 1.",
				"184: Distance between variable 'result' declaration and its first usage is 2, but allowed 1.",
				"219: Distance between variable 't' declaration and its first usage is 5, but allowed 1.",
				"222: Distance between variable 'c' declaration and its first usage is 3, but allowed 1.",
				"223: Distance between variable 'd2' declaration and its first usage is 3, but allowed 1.",
				"260: Distance between variable 'selected' declaration and its first usage is 2, but allowed 1.",
				"261: Distance between variable 'model' declaration and its first usage is 2, but allowed 1.",
				"287: Distance between variable 'sw' declaration and its first usage is 2, but allowed 1.",
				"300: Distance between variable 'wh' declaration and its first usage is 2, but allowed 1.",
				"329: Distance between variable 'logLevel' declaration and its first usage is 2, but allowed 1.",
		};
		verify(checkConfig, getPath("design" + File.separator + "InputVariableDeclarationUsageDistanceCheck.java"),
				expected);
	}

	@Test
	public void testDistance() throws Exception {
		final DefaultConfiguration checkConfig = createCheckConfig(VariableDeclarationUsageDistanceCheck.class);
		checkConfig.addAttribute("allowedDistance", "3");
		checkConfig.addAttribute("ignoreVariablePattern", "");
		checkConfig.addAttribute("validateBetweenScopes", "true");
		final String[] expected = {
				"71: Distance between variable 'count' declaration and its first usage is 4, but allowed 3.",
				"219: Distance between variable 't' declaration and its first usage is 5, but allowed 3.",
		};
		verify(checkConfig, getPath("design" + File.separator + "InputVariableDeclarationUsageDistanceCheck.java"), expected);
	}
	
	@Test
	public void testVariableRegExp() throws Exception {
		final DefaultConfiguration checkConfig = createCheckConfig(VariableDeclarationUsageDistanceCheck.class);
		checkConfig.addAttribute("allowedDistance", "1");
		checkConfig.addAttribute("ignoreVariablePattern", "a|b|c|d|block|dist|t|m");
		checkConfig.addAttribute("validateBetweenScopes", "true");
		final String[] expected = {
				"38: Distance between variable 'temp' declaration and its first usage is 2, but allowed 1.",
				"44: Distance between variable 'temp' declaration and its first usage is 2, but allowed 1.",
				"57: Distance between variable 'count' declaration and its first usage is 2, but allowed 1.",
				"71: Distance between variable 'count' declaration and its first usage is 4, but allowed 1.",
				"96: Distance between variable 'arg' declaration and its first usage is 2, but allowed 1.",
				"145: Distance between variable 'n' declaration and its first usage is 2, but allowed 1.",
				"184: Distance between variable 'result' declaration and its first usage is 2, but allowed 1.",
				"223: Distance between variable 'd2' declaration and its first usage is 3, but allowed 1.",
				"260: Distance between variable 'selected' declaration and its first usage is 2, but allowed 1.",
				"261: Distance between variable 'model' declaration and its first usage is 2, but allowed 1.",
				"287: Distance between variable 'sw' declaration and its first usage is 2, but allowed 1.",
				"300: Distance between variable 'wh' declaration and its first usage is 2, but allowed 1.",
				"329: Distance between variable 'logLevel' declaration and its first usage is 2, but allowed 1.",
		};
		verify(checkConfig, getPath("design" + File.separator + "InputVariableDeclarationUsageDistanceCheck.java"), expected);
	}
	
	@Test
	public void testValidateBetweenScopesOption() throws Exception {
		final DefaultConfiguration checkConfig = createCheckConfig(VariableDeclarationUsageDistanceCheck.class);
		checkConfig.addAttribute("allowedDistance", "1");
		checkConfig.addAttribute("ignoreVariablePattern", "");
		checkConfig.addAttribute("validateBetweenScopes", "false");
		final String[] expected = {
				"30: Distance between variable 'a' declaration and its first usage is 2, but allowed 1.",
				"38: Distance between variable 'temp' declaration and its first usage is 2, but allowed 1.",
				"44: Distance between variable 'temp' declaration and its first usage is 2, but allowed 1.",
				"71: Distance between variable 'count' declaration and its first usage is 4, but allowed 1.",
				"96: Distance between variable 'arg' declaration and its first usage is 2, but allowed 1.",
				"219: Distance between variable 't' declaration and its first usage is 5, but allowed 1.",
				"222: Distance between variable 'c' declaration and its first usage is 3, but allowed 1.",
				"223: Distance between variable 'd2' declaration and its first usage is 3, but allowed 1.",
		};
		verify(checkConfig, getPath("design" + File.separator + "InputVariableDeclarationUsageDistanceCheck.java"),
				expected);
	}
}
