package com.puppycrawl.tools.checkstyle.checks.coding;

import java.io.File;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class RedundantReturnTest extends BaseCheckTestSupport {
    @Test
    public void testInputWithIgnoreEmptyConstructorsTrue() throws Exception {
	final DefaultConfiguration checkConfig = createCheckConfig(RedundantReturnCheck.class);
	checkConfig.addAttribute("avoidEmptyBlocks", "true");

	final String[] expected = { "12: Redundant return.",
		"19: Redundant return.", "24: Redundant return.",
		"34: Redundant return.", "41: Redundant return.",
		"54: Redundant return.", "58: Redundant return.",
		"62: Redundant return.", "89: Redundant return.",
		"102: Redundant return.", "106: Redundant return."
	};

	verify(checkConfig, getPath("coding" + File.separator
		+ "InputRedundantReturn.java"), expected);
    }

    @Test
    public void testInputWithIgnoreEmptyConstructorsFalse() throws Exception {
	final DefaultConfiguration checkConfig = createCheckConfig(RedundantReturnCheck.class);
	checkConfig.addAttribute("avoidEmptyBlocks", "false");

	final String[] expected = { "19: Redundant return.",
		"34: Redundant return.", "41: Redundant return.",
		"54: Redundant return.", "58: Redundant return.",
		"62: Redundant return.", "102: Redundant return.",
		"106: Redundant return."
	};

	verify(checkConfig, getPath("coding" + File.separator
		+ "InputRedundantReturn.java"), expected);

    }
}
