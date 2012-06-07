package com.puppycrawl.tools.checkstyle.checks.coding;

import java.io.File;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class IllegalCatchCheckTest extends BaseCheckTestSupport
{

	private final DefaultConfiguration checkConfig = createCheckConfig(IllegalCatchCheck.class);

	@Test
	public final void testDefault() throws Exception
	{
		String[] expected = {
				"9:9: Catching 'RuntimeException' is not allowed.",
				"11:9: Catching 'java.lang.Exception' is not allowed.",
				"13:9: Catching 'Throwable' is not allowed.",
				"24:9: Catching 'RuntimeException' is not allowed.",
				"29:9: Catching 'java.lang.Exception' is not allowed.",
				"34:9: Catching 'Throwable' is not allowed.",
		};

		checkConfig.addAttribute("allowThrow", "false");
		checkConfig.addAttribute("allowRethrow", "false");

		verify(checkConfig, getPath("coding" + File.separator
				+ "InputIllegalCatchCheckNew.java"), expected);
	}

	@Test
	public final void testRethrowPermit() throws Exception
	{

		String[] expected = {
				"9:9: Catching 'RuntimeException' is not allowed.",
				"11:9: Catching 'java.lang.Exception' is not allowed.",
				"13:9: Catching 'Throwable' is not allowed.",
				//          "24:9: Catching 'RuntimeException' is not allowed.",
				"29:9: Catching 'java.lang.Exception' is not allowed.",
				"34:9: Catching 'Throwable' is not allowed.",
		};

		checkConfig.addAttribute("allowThrow", "false");
		checkConfig.addAttribute("allowRethrow", "true");

		verify(checkConfig, getPath("coding" + File.separator
				+ "InputIllegalCatchCheckNew.java"), expected);
	}

	@Test
	public final void testThrowPermit() throws Exception
	{
		checkConfig.addAttribute("illegalClassNames",
				"java.lang.Error, java.lang.Exception, java.lang.Throwable, java.lang.RuntimeException");

		String[] expected = {
				"9:9: Catching 'RuntimeException' is not allowed.",
				"11:9: Catching 'java.lang.Exception' is not allowed.",
				"13:9: Catching 'Throwable' is not allowed.",
				"24:9: Catching 'RuntimeException' is not allowed.",
				// "29:9: Catching 'java.lang.Exception' is not allowed.",
				// "34:9: Catching 'Throwable' is not allowed.",
		};

		checkConfig.addAttribute("allowThrow", "true");
		checkConfig.addAttribute("allowRethrow", "false");

		verify(checkConfig, getPath("coding" + File.separator
				+ "InputIllegalCatchCheckNew.java"), expected);
	}

	@Test
	public final void testAllowRethrowBugs() throws Exception
	{
		checkConfig.addAttribute("illegalClassNames",
				"java.lang.Error, java.lang.Exception, java.lang.Throwable");

		String[] expected = {};

		checkConfig.addAttribute("allowThrow", "false");
		checkConfig.addAttribute("allowRethrow", "true");

		verify(checkConfig, getPath("coding" + File.separator
				+ "InputIllegalCatchCheckBugs.java"), expected);
	}

	@Test
	public final void testDisallowRethrowBugs() throws Exception
	{
		checkConfig.addAttribute("illegalClassNames",
				"java.lang.Error, java.lang.Exception, java.lang.Throwable");

		String[] expected = {
				"53:19: Catching 'Exception' is not allowed.",
		};

		checkConfig.addAttribute("allowThrow", "true");
		checkConfig.addAttribute("allowRethrow", "false");

		verify(checkConfig, getPath("coding" + File.separator
				+ "InputIllegalCatchCheckBugs.java"), expected);
	}

	@Test
	public final void testAllowThrowAllowRethrowBugs() throws Exception
	{
		checkConfig.addAttribute("illegalClassNames",
				"java.lang.Error, java.lang.Exception, java.lang.Throwable");

		String[] expected = {
				};

		checkConfig.addAttribute("allowThrow", "true");
		checkConfig.addAttribute("allowRethrow", "true");

		verify(checkConfig, getPath("coding" + File.separator
				+ "InputIllegalCatchCheckBugs.java"), expected);
	}

}