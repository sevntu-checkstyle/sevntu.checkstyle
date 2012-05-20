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
				"33: Inner class should not implement default Serializable interface.",
				"45: Inner class should not implement default Serializable interface.",
				"52: Inner class should not implement default Serializable interface.",
				"59: Inner class should not implement default Serializable interface.",
				"67: Inner class should not implement default Serializable interface.",
				"74: Inner class should not implement default Serializable interface.",
				"97: Inner class should not implement default Serializable interface.",
				"104: Inner class should not implement default Serializable interface.",
				"121: Inner class should not implement default Serializable interface.",
				"134: Inner class should not implement default Serializable interface.",
				"145: Inner class should not implement default Serializable interface.",
				"159: Inner class should not implement default Serializable interface.",
				"166: Inner class should not implement default Serializable interface.",
				"173: Inner class should not implement default Serializable interface."};
		
		/*final String[] expected2 = {
				"33: Inner class should not implement default Serializable interface.",
				"59: Inner class should not implement default Serializable interface.",
				"67: Inner class should not implement default Serializable interface.",
				"74: Inner class should not implement default Serializable interface.",
				"97: Inner class should not implement default Serializable interface.",
				"104: Inner class should not implement default Serializable interface.",
				"121: Inner class should not implement default Serializable interface.",
				"134: Inner class should not implement default Serializable interface.",
				"145: Inner class should not implement default Serializable interface."};*/
		verify(checkConfig, getPath("coding" + File.separator
				+ "InputAvoidDefaultSerializableInInnerClasses.java"), expected);
	}
}