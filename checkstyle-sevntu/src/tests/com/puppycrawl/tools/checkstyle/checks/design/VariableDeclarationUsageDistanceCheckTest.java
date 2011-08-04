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
				"8: Distance between variable first usage is '2', but allowed '1'.",
				"16: Distance between variable first usage is '3', but allowed '1'.",
				"17: Distance between variable first usage is '2', but allowed '1'.",
				"30: Distance between variable first usage is '4', but allowed '1'.",
				"38: Distance between variable first usage is '2', but allowed '1'.",
				"44: Distance between variable first usage is '2', but allowed '1'.",
				"47: Distance between variable first usage is '2', but allowed '1'.",
				"48: Distance between variable first usage is '2', but allowed '1'.",
				"57: Distance between variable first usage is '4', but allowed '1'.",
				"58: Distance between variable first usage is '2', but allowed '1'.",
				"71: Distance between variable first usage is '6', but allowed '1'.",
				"72: Distance between variable first usage is '2', but allowed '1'.",
				"81: Distance between variable first usage is '2', but allowed '1'.",
				"96: Distance between variable first usage is '4', but allowed '1'.",
				"97: Distance between variable first usage is '2', but allowed '1'.",
				"98: Distance between variable first usage is '2', but allowed '1'.",
				"106: Distance between variable first usage is '4', but allowed '1'.",
				"107: Distance between variable first usage is '4', but allowed '1'.",
				"108: Distance between variable first usage is '3', but allowed '1'.",
				"109: Distance between variable first usage is '2', but allowed '1'.",
				"142: Distance between variable first usage is '4', but allowed '1'.",
				"143: Distance between variable first usage is '3', but allowed '1'.",
				"144: Distance between variable first usage is '4', but allowed '1'.",
				"145: Distance between variable first usage is '2', but allowed '1'.",
				"158: Distance between variable first usage is '4', but allowed '1'.",
				"159: Distance between variable first usage is '2', but allowed '1'.",
				"160: Distance between variable first usage is '2', but allowed '1'.",
				"182: Distance between variable first usage is '5', but allowed '1'.",
				"183: Distance between variable first usage is '2', but allowed '1'.",
				"184: Distance between variable first usage is '3', but allowed '1'.",
				"195: Distance between variable first usage is '6', but allowed '1'.",
				"196: Distance between variable first usage is '4', but allowed '1'.",
				"197: Distance between variable first usage is '2', but allowed '1'.",
				"198: Distance between variable first usage is '3', but allowed '1'.",
				"210: Distance between variable first usage is '2', but allowed '1'.",
				"211: Distance between variable first usage is '2', but allowed '1'.",
				"218: Distance between variable first usage is '11', but allowed '1'.",
				"219: Distance between variable first usage is '4', but allowed '1'.",
				"220: Distance between variable first usage is '3', but allowed '1'.",
				"221: Distance between variable first usage is '4', but allowed '1'.",
				"222: Distance between variable first usage is '3', but allowed '1'.",
				"234: Distance between variable first usage is '3', but allowed '1'.",
		};
		verify(checkConfig, getPath("design" + File.separator + "InputVariableDeclarationUsageDistanceCheck.java"), expected);
	}
	
	@Test
	public void testDistance() throws Exception {
		final DefaultConfiguration checkConfig = createCheckConfig(VariableDeclarationUsageDistanceCheck.class);
		checkConfig.addAttribute("allowedDistance", "3");
		checkConfig.addAttribute("ignoreVariablePattern", "");
		final String[] expected = {
				"30: Distance between variable first usage is '4', but allowed '3'.",
				"57: Distance between variable first usage is '4', but allowed '3'.",
				"71: Distance between variable first usage is '6', but allowed '3'.",
				"96: Distance between variable first usage is '4', but allowed '3'.",
				"106: Distance between variable first usage is '4', but allowed '3'.",
				"107: Distance between variable first usage is '4', but allowed '3'.",
				"142: Distance between variable first usage is '4', but allowed '3'.",
				"144: Distance between variable first usage is '4', but allowed '3'.",
				"158: Distance between variable first usage is '4', but allowed '3'.",
				"182: Distance between variable first usage is '5', but allowed '3'.",
				"195: Distance between variable first usage is '6', but allowed '3'.",
				"196: Distance between variable first usage is '4', but allowed '3'.",
				"218: Distance between variable first usage is '11', but allowed '3'.",
				"219: Distance between variable first usage is '4', but allowed '3'.",
				"221: Distance between variable first usage is '4', but allowed '3'.",
		};
		verify(checkConfig, getPath("design" + File.separator + "InputVariableDeclarationUsageDistanceCheck.java"), expected);
	}
	
	@Test
	public void testVariableRegExp() throws Exception {
		final DefaultConfiguration checkConfig = createCheckConfig(VariableDeclarationUsageDistanceCheck.class);
		checkConfig.addAttribute("allowedDistance", "1");
		checkConfig.addAttribute("ignoreVariablePattern", "a|b|c|d|block|dist");
		final String[] expected = {
				"38: Distance between variable first usage is '2', but allowed '1'.",
				"44: Distance between variable first usage is '2', but allowed '1'.",
				"47: Distance between variable first usage is '2', but allowed '1'.",
				"48: Distance between variable first usage is '2', but allowed '1'.",
				"57: Distance between variable first usage is '4', but allowed '1'.",
				"71: Distance between variable first usage is '6', but allowed '1'.",
				"96: Distance between variable first usage is '4', but allowed '1'.",
				"98: Distance between variable first usage is '2', but allowed '1'.",
				"106: Distance between variable first usage is '4', but allowed '1'.",
				"108: Distance between variable first usage is '3', but allowed '1'.",
				"144: Distance between variable first usage is '4', but allowed '1'.",
				"145: Distance between variable first usage is '2', but allowed '1'.",
				"158: Distance between variable first usage is '4', but allowed '1'.",
				"159: Distance between variable first usage is '2', but allowed '1'.",
				"160: Distance between variable first usage is '2', but allowed '1'.",
				"184: Distance between variable first usage is '3', but allowed '1'.",
				"195: Distance between variable first usage is '6', but allowed '1'.",
				"196: Distance between variable first usage is '4', but allowed '1'.",
				"197: Distance between variable first usage is '2', but allowed '1'.",
				"198: Distance between variable first usage is '3', but allowed '1'.",
				"210: Distance between variable first usage is '2', but allowed '1'.",
				"211: Distance between variable first usage is '2', but allowed '1'.",
				"218: Distance between variable first usage is '11', but allowed '1'.",
				"220: Distance between variable first usage is '3', but allowed '1'.",
				"222: Distance between variable first usage is '3', but allowed '1'.",
				"234: Distance between variable first usage is '3', but allowed '1'.",
		};
		verify(checkConfig, getPath("design" + File.separator + "InputVariableDeclarationUsageDistanceCheck.java"), expected);
	}
}
