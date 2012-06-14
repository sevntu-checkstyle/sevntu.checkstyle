package com.puppycrawl.tools.checkstyle.checks.coding;

import java.io.File;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class AvoidDefaultSerializableInInnerClassesTest extends
		BaseCheckTestSupport
{

	@Test
	public void testWithAllowPartiaFalse() throws Exception
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
				"170: Inner class should not implement default Serializable interface.",
				"177: Inner class should not implement default Serializable interface."};
		verify(checkConfig, getPath("coding" + File.separator
				+ "InputAvoidDefaultSerializableInInnerClasses.java"), expected);
	}

	@Test
    public void testPrivateNotRealReadObject() throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(AvoidDefaultSerializableInInnerClasses.class);
        checkConfig.addAttribute("allowPartialImplementation", "true");

        final String[] expected = {
                "10: Inner class should not implement default Serializable interface."
                };

        verify(checkConfig, getPath("coding" + File.separator
                + "InputAvoidDefaultSerializableInInnerClasses2.java"), expected);
    }

    @Test
    public void testRealReadObjectNotRealReadObjectRealPrivate() throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(AvoidDefaultSerializableInInnerClasses.class);

        final String[] expected = {};

        verify(checkConfig, getPath("coding" + File.separator
                + "InputAvoidDefaultSerializableInInnerClasses3.java"), expected);
    }
    
    @Test
    public void testWithAllowPartiaTrue() throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(AvoidDefaultSerializableInInnerClasses.class);
        checkConfig.addAttribute("allowPartialImplementation", "true");
        final String[] expected = {
                "33: Inner class should not implement default Serializable interface.",
                "59: Inner class should not implement default Serializable interface.",
                "67: Inner class should not implement default Serializable interface.",
                "74: Inner class should not implement default Serializable interface.",
                "97: Inner class should not implement default Serializable interface.",
                "104: Inner class should not implement default Serializable interface.",
                "121: Inner class should not implement default Serializable interface.",
                "134: Inner class should not implement default Serializable interface.",
                "145: Inner class should not implement default Serializable interface.",
                "170: Inner class should not implement default Serializable interface."};
        verify(checkConfig, getPath("coding" + File.separator
                + "InputAvoidDefaultSerializableInInnerClasses.java"), expected);
    }
}