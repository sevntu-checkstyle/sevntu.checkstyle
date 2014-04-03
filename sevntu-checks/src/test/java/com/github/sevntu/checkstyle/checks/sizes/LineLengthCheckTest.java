package com.github.sevntu.checkstyle.checks.sizes;

import static com.github.sevntu.checkstyle.checks.sizes.LineLengthExtendedCheck.*;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class LineLengthCheckTest extends BaseCheckTestSupport
{
	@Test
	public void testSimple()
			throws Exception
	{
		final DefaultConfiguration checkConfig =
				createCheckConfig(LineLengthExtendedCheck.class);
		checkConfig.addAttribute("max", "80");
		checkConfig.addAttribute("ignorePattern", "^.*is OK.*regexp.*$");
		final String[] expected = {
				"18: " + getCheckMessage(MSG_KEY, 80),
				"145: " + getCheckMessage(MSG_KEY, 80),
		};
		verify(checkConfig, getPath("InputSimple.java"), expected);
	}

	@Test
	public void testSimpleIgnore()
			throws Exception
	{
		final DefaultConfiguration checkConfig =
				createCheckConfig(LineLengthExtendedCheck.class);
		checkConfig.addAttribute("max", "40");
		checkConfig.addAttribute("ignorePattern", "^.*is OK.*regexp.*$");
		final String[] expected = {
				"1: " + getCheckMessage(MSG_KEY, 40),
				"5: " + getCheckMessage(MSG_KEY, 40),
				"6: " + getCheckMessage(MSG_KEY, 40),
				"18: " + getCheckMessage(MSG_KEY, 40),
				"101: " + getCheckMessage(MSG_KEY, 40),
				"125: " + getCheckMessage(MSG_KEY, 40),
				"128: " + getCheckMessage(MSG_KEY, 40),
				"132: " + getCheckMessage(MSG_KEY, 40),
				"145: " + getCheckMessage(MSG_KEY, 40),
				"146: " + getCheckMessage(MSG_KEY, 40),
				"148: " + getCheckMessage(MSG_KEY, 40),
				"151: " + getCheckMessage(MSG_KEY, 40),
				"152: " + getCheckMessage(MSG_KEY, 40),
				"192: " + getCheckMessage(MSG_KEY, 40),
				"200: " + getCheckMessage(MSG_KEY, 40),
				"207: " + getCheckMessage(MSG_KEY, 40),
		};
		checkConfig.addAttribute("ignoreClass", "true");
		checkConfig.addAttribute("ignoreConstructor", "true");
		checkConfig.addAttribute("ignoreField", "true");
		checkConfig.addAttribute("ignoreMethod", "true");
		verify(checkConfig, getPath("InputSimple.java"), expected);
	}
}
