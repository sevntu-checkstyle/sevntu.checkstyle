package com.puppycrawl.tools.checkstyle.checks.coding;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class OverridableMethodInConstructorTest extends BaseCheckTestSupport {
	
	DefaultConfiguration checkConfig = createCheckConfig(OverridableMethodInConstructorCheck.class);

	@Before
	public void setTestinputsDir(){
		System.setProperty(
				"testinputs.dir",
				"/media/data/Work/Git repository clone = Eclipse workspace/sevntu.checkstyle/checkstyle-sevntu/src/testinputs/com/puppycrawl/tools/checkstyle");

//		checkConfig.addAttribute("CheckCloneMethod", "true");
//		checkConfig.addAttribute("CheckReadObjectMethod", "true");

	}
	
	
	@Test
	public final void testNoWarnings() throws Exception {
		

		String[] expected = {};

		verify(checkConfig, getPath("coding" + File.separator
				+ "InputOverridableMethodInConstructor1.java"), expected);
	}

	@Test
	public final void testWarning() throws Exception {

		String[] expected = { "10:27: " + createMsg("overrideMe") };


		verify(checkConfig, getPath("coding" + File.separator
				+ "InputOverridableMethodInConstructor2.java"), expected);
	}

	@Test
	public final void test2WarningsIn2Ctors() throws Exception {
		String[] expected = { "10:27: " + createMsg("overrideMe"),
				"15:27: " + createMsg("overrideMe") };

		verify(checkConfig, getPath("coding" + File.separator
				+ "InputOverridableMethodInConstructor3.java"), expected);
	}

	@Test
	public final void testWarningInSecondDepth() throws Exception {

		String[] expected = { "10:32: " + createMsg("overrideMe") };

		verify(checkConfig, getPath("coding" + File.separator
				+ "InputOverridableMethodInConstructor4.java"), expected);
	}

	@Test
	public final void testWarningsInThirdDepth() throws Exception {

		String[] expected = { "10:32: " + createMsg("overrideMe"),
				"11:27: " + createMsg("overrideMe"), };

		verify(checkConfig, getPath("coding" + File.separator
				+ "InputOverridableMethodInConstructor5.java"), expected);
	}

	@Test
	public final void testCloneNoWarningsSimple() throws Exception {

		String[] expected = {};

		verify(checkConfig, getPath("coding" + File.separator
				+ "InputOverridableMethodInConstructor6.java"), expected);
	}

	@Test
	public final void testCloneNoWarnings() throws Exception {

		String[] expected = {};

		verify(checkConfig, getPath("coding" + File.separator
				+ "InputOverridableMethodInConstructor7.java"), expected);
	}

	@Test
	public final void testCloneWarnings() throws Exception {

		String[] expected = {
				"20:37: Overridable method 'doSmth' called in \"clone()\" method.",
				"37:37: Overridable method 'doSmth' called in \"clone()\" method.", };

		verify(checkConfig, getPath("coding" + File.separator
				+ "InputOverridableMethodInConstructor8.java"), expected);
	}

	@Test
	public final void testCloneSecondDepth() throws Exception {

		String[] expected = {
				"25:37: Overridable method 'doSmth' called in \"clone()\" method.",
				"26:20: Overridable method 'doSmth2' called in \"clone()\" method.", };

		verify(checkConfig, getPath("coding" + File.separator
				+ "InputOverridableMethodInConstructor9.java"), expected);
	}

	@Test
	public final void testCloneThirdDepthImplementation() throws Exception {

		String[] expected = {
				"25:37: Overridable method 'doSmth' called in \"clone()\" method.",
				"26:19: Overridable method 'accept' called in \"clone()\" method.",
				"27:24: Overridable method 'accept' called in \"clone()\" method.",
				"62:37: Overridable method 'doSmth' called in \"clone()\" method.", };

		verify(checkConfig, getPath("coding" + File.separator
				+ "InputOverridableMethodInConstructor13.java"), expected);
	}

	@Test
	public final void testSerializableNoWarnings() throws Exception {

		String[] expected = {};

		verify(checkConfig, getPath("coding" + File.separator
				+ "InputOverridableMethodInConstructor10.java"), expected);
	}

	@Test
	public final void testSerializableWarning() throws Exception {

		String[] expected = { "31:20: Overridable method 'doSmth' called in \"readObject()\" method.", };

		verify(checkConfig, getPath("coding" + File.separator
				+ "InputOverridableMethodInConstructor11.java"), expected);
	}

	@Test
	public final void testSerializable3WarningsInThirdDepth() throws Exception {

		String[] expected = {
				"30:20: Overridable method 'doSmth' called in \"readObject()\" method.",
				"31:25: Overridable method 'doSmth' called in \"readObject()\" method.",
				"32:28: Overridable method 'doSmth' called in \"readObject()\" method.", };

		verify(checkConfig, getPath("coding" + File.separator
				+ "InputOverridableMethodInConstructor12.java"), expected);
	}

	@Test
	public final void testSerializableThirdDepthImplementation()
			throws Exception {

		String[] expected = {
				"34:20: Overridable method 'doSmth' called in \"readObject()\" method.",
				"60:19: Overridable method 'doSmth' called in \"readObject()\" method.",
				"61:24: Overridable method 'doSmth' called in \"readObject()\" method.",
				"62:20: Overridable method 'doSmth2' called in \"readObject()\" method.",
				"63:25: Overridable method 'doSmth2' called in \"readObject()\" method.",
				"77:23: Overridable method 'doSmth' called in \"readObject()\" method.",
				"78:28: Overridable method 'doSmth' called in \"readObject()\" method.",
				"80:24: Overridable method 'doSmth2' called in \"readObject()\" method.",
				"81:29: Overridable method 'doSmth2' called in \"readObject()\" method.", };

		verify(checkConfig, getPath("coding" + File.separator
				+ "InputOverridableMethodInConstructor14.java"), expected);
	}

	@Test
	public final void testCtorOverloadedMethods() throws Exception {

		String[] expected = {};

		verify(checkConfig, getPath("coding" + File.separator
				+ "InputOverridableMethodInConstructor15.java"), expected);
	}

	@Test
	public final void test2EqualMethodNamesWithNoWarning() throws Exception {

		String[] expected = {};

		verify(checkConfig, getPath("coding" + File.separator
				+ "InputOverridableMethodInConstructor16.java"), expected);
	}

	@Test
	public final void test2EqualMethodNamesWithoutWarning2() throws Exception {

		String[] expected = {};

		verify(checkConfig, getPath("coding" + File.separator
				+ "InputOverridableMethodInConstructor17.java"), expected);
	}

	@Test
	public final void testCallMethodIsNotInBuildPath2() throws Exception {

		String[] expected = {};

		verify(checkConfig, getPath("coding" + File.separator
				+ "InputOverridableMethodInConstructor18.java"), expected);
	}
	
	@Test
	public final void testCallMethodIsNotInBuildPath() throws Exception {

		String[] expected = {};

		verify(checkConfig, getPath("coding" + File.separator
				+ "InputOverridableMethodInConstructor19.java"), expected);
	}
	
	@Test
	public final void testReadObjectInInterface() throws Exception {

		String[] expected = {};

		verify(checkConfig, getPath("coding" + File.separator
				+ "InputOverridableMethodInConstructor20.java"), expected);
	}
	
	@Test
	public final void testStackOverFlowError() throws Exception {

		String[] expected = {};

		verify(checkConfig, getPath("coding" + File.separator
				+ "InputOverridableMethodInConstructor21.java"), expected);
	}
	
	public String createMsg(String methodName) {
		return "Overridable method '" + methodName + "' called in constructor.";
	}

}