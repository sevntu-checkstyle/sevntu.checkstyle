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
		/*
		 * final String[] expected = {
		 * "33: Inner class are implement default Serializable interface.",
		 * "45: Inner class are implement default Serializable interface.",
		 * "52: Inner class are implement default Serializable interface.",
		 * "59: Inner class are implement default Serializable interface.",
		 * "74: Inner class are implement default Serializable interface.",
		 * "97: Inner class are implement default Serializable interface.",
		 * "104: Inner class are implement default Serializable interface.",
		 * "121: Inner class are implement default Serializable interface.",
		 * "134: Inner class are implement default Serializable interface." };
		 */
		final String[] expected = {
				"33: avoid.default.serializable.in.inner.classes",
				"59: avoid.default.serializable.in.inner.classes",
				"67: avoid.default.serializable.in.inner.classes",
				"74: avoid.default.serializable.in.inner.classes",
				"97: avoid.default.serializable.in.inner.classes",
				"104: avoid.default.serializable.in.inner.classes",
				"121: avoid.default.serializable.in.inner.classes",
				"134: avoid.default.serializable.in.inner.classes",
				"145: avoid.default.serializable.in.inner.classes",
				"151: avoid.default.serializable.in.inner.classes" };
		verify(checkConfig, getPath("coding" + File.separator
				+ "InputAvoidDefaultSerializableInInnerClasses.java"), expected);
	}
}