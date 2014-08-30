package com.github.sevntu.checkstyle.checks.coding;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.github.sevntu.checkstyle.checks.coding.RedundantReturnCheck;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class RedundantReturnCheckTest extends BaseCheckTestSupport {
    @Test
    public void testInputWithIgnoreEmptyConstructorsTrue() throws Exception {
	final DefaultConfiguration checkConfig = createCheckConfig(RedundantReturnCheck.class);
	checkConfig.addAttribute("allowReturnInEmptyMethodsAndConstructors", "false");

	final String[] expected = {
	        "10: Redundant return.",
	        "17: Redundant return.",
	        "22: Redundant return.",
	        "32: Redundant return.",
	        "39: Redundant return.",
	        "52: Redundant return.",
	        "56: Redundant return.",
	        "60: Redundant return.",
	        "87: Redundant return.",
	        "100: Redundant return.",
	        "104: Redundant return."
	    };

	verify(checkConfig, getPath("InputRedundantReturn.java"), expected);
    }

    @Test
    public void testInputWithIgnoreEmptyConstructorsFalse() throws Exception {
	final DefaultConfiguration checkConfig = createCheckConfig(RedundantReturnCheck.class);
	checkConfig.addAttribute("allowReturnInEmptyMethodsAndConstructors", "true");

	final String[] expected = { 
	        "17: Redundant return.",
	        "32: Redundant return.", 
	        "39: Redundant return.",
	        "52: Redundant return.", 
	        "56: Redundant return.",
	        "60: Redundant return.", 
	        "100: Redundant return.",
	        "104: Redundant return."
	    };

	verify(checkConfig, getPath("InputRedundantReturn.java"), expected);

    }
}
