package com.puppycrawl.tools.checkstyle.checks.coding;

import java.io.File;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class AvoidDefaultSerializableInInnerClassesTest extends
		BaseCheckTestSupport
{

	@Test
	public void testDefault() throws Exception
	{
		final DefaultConfiguration checkConfig = createCheckConfig(AvoidDefaultSerializableInInnerClasses.class);
		final String[] expected = {
				"5: Inner class are implement default Serializable interface.",
				"30: Inner class are implement default Serializable interface.",
				"47: Inner class are implement default Serializable interface.", };

		verify(checkConfig, getPath("coding" + File.separator
				+ "AvoidDefaultSerializableInInnerClasses.java"), expected);

		/*
		 * verify(checkConfig, getPath("coding" + File.separator +
		 * "InputAvoidDefaultSerializableInInnerClasses.java"), expected);
		 */
	}
}