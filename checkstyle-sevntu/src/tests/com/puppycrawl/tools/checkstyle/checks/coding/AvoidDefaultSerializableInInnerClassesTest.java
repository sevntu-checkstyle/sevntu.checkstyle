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
				"33: Inner class are implement default Serializable interface.",
				"59: Inner class are implement default Serializable interface.",
				"67: Inner class are implement default Serializable interface.",
				"74: Inner class are implement default Serializable interface.",
				"97: Inner class are implement default Serializable interface.",
				"104: Inner class are implement default Serializable interface.",
				"121: Inner class are implement default Serializable interface.",
				"134: Inner class are implement default Serializable interface.",
				"145: Inner class are implement default Serializable interface.",
				"151: Inner class are implement default Serializable interface." };
		verify(checkConfig, getPath("coding" + File.separator
				+ "InputAvoidDefaultSerializableInInnerClasses.java"), expected);
	}
}