package com.github.sevntu.checkstyle.checks.coding;

import java.io.File;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.github.sevntu.checkstyle.checks.coding.RedundantReturnCheck;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class RedundantReturnCheckTest extends BaseCheckTestSupport {
    @Test
    public void testInputWithIgnoreEmptyConstructorsTrue() throws Exception {
	final DefaultConfiguration checkConfig = createCheckConfig(RedundantReturnCheck.class);
	checkConfig.addAttribute("allowReturnInEmptyMethodsAndConstructors", "false");

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
	checkConfig.addAttribute("allowReturnInEmptyMethodsAndConstructors", "true");

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
