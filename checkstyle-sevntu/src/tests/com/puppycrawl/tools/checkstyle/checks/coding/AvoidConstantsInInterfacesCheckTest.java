package com.puppycrawl.tools.checkstyle.checks.coding;

import java.io.File;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import org.junit.Test;

public class AvoidConstantsInInterfacesCheckTest extends BaseCheckTestSupport {
	@Test
	public void testDefault() throws Exception {
		final DefaultConfiguration checkConfig = createCheckConfig(AvoidConstantsInInterfacesCheck.class);
		final String[] expected = {
				"7: Please avoid to declare constant(s) in the interface",
				"13: Please avoid to declare constant(s) in the interface",
				"14: Please avoid to declare constant(s) in the interface",
				"22: Please avoid to declare constant(s) in the interface",
				"29: Please avoid to declare constant(s) in the interface",};
		verify(checkConfig, getPath("coding" + File.separator
				+ "InputAvoidConstantsInInterfacesCheck.java"), expected);

	}
}
