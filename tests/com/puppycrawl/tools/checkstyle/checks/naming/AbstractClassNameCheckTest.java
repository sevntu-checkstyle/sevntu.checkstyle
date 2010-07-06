package com.puppycrawl.tools.checkstyle.checks.naming;

import java.io.File;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class AbstractClassNameCheckTest extends BaseCheckTestSupport
{
    @Test
    public void testIllegalAbstractClassName() throws Exception
    {
        final DefaultConfiguration checkConfig =
            createCheckConfig(AbstractClassNameCheck.class);
        final String[] expected = {
            "3:1: Name 'InputAbstractClassName' must match pattern '^Abstract.*$|^.*Factory$'.",
            "6:1: Name 'NonAbstractClassName' must match pattern '^Abstract.*$|^.*Factory$'.",
            "9:1: Name 'FactoryWithBadName' must match pattern '^Abstract.*$|^.*Factory$'.",
            "13:5: Name 'NonAbstractInnerClass' must match pattern '^Abstract.*$|^.*Factory$'.",
        };
        System.setProperty("testinputs.dir","/home/danil/workspace/my/sevntu.checkstyle/test/com/puppycrawl/tools/checkstyle/checks");
        verify(checkConfig, getPath("naming" + File.separator + "InputAbstractClassName.java"), expected);
    }
    
    @Test
    //^Abstract.*$|^.*Factory$ --> abstract
	public void testIllegalClassType() throws Exception {
		final DefaultConfiguration checkConfig =
				createCheckConfig(AbstractClassNameCheck.class);
		final String[] expected = {
				"3:1: Name 'InputAbstractClassName' must match pattern '^Abstract.*$|^.*Factory$'.",
				"6:1: Name 'NonAbstractClassName' must match pattern '^Abstract.*$|^.*Factory$'.",
				"9:1: Name 'FactoryWithBadName' must match pattern '^Abstract.*$|^.*Factory$'.",
				"13:5: Name 'NonAbstractInnerClass' must match pattern '^Abstract.*$|^.*Factory$'.",
				"26:1: Class 'AbstractClass' must be abstract.",
				"29:1: Class 'Class1Factory' must be abstract.",
				"33:5: Class 'AbstractInnerClass' must be abstract.",
				"38:5: Class 'WellNamedFactory' must be abstract.",
		};
		checkConfig.addAttribute("file", System.getProperty("testinputs.dir")
                + "/import-control_two.xml");
		System.setProperty("testinputs.dir",
				"/home/danil/workspace/my/sevntu.checkstyle/test/com/puppycrawl/tools/checkstyle/checks");
		verify(checkConfig, getPath("naming" + File.separator + "InputAbstractClassName.java"), expected);
	}

}
