package com.puppycrawl.tools.checkstyle.checks.coding;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class MethodLimitCheckTest extends BaseCheckTestSupport{
	 @Test
	    public void testIt() throws Exception
	    {
	        final DefaultConfiguration checkConfig =
	            createCheckConfig(MethodLimitCheck.class);
	        checkConfig.addAttribute("max", "2");
	        final String[] expected = {
	        	"12: too many methods, only 2 are allowed",
	            "25: too many methods, only 2 are allowed",
	            "37: too many methods, only 2 are allowed",
	            "62: too many methods, only 2 are allowed",
	            "70: too many methods, only 2 are allowed"
	        };
	        verify(checkConfig, getPath("InputMethodLimitCheck.java"), expected);

	    }

}
