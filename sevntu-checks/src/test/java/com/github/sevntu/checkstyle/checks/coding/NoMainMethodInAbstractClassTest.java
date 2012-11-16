package com.github.sevntu.checkstyle.checks.coding;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.github.sevntu.checkstyle.checks.coding.NoMainMethodInAbstractClassTest;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import org.junit.Test;

public class NoMainMethodInAbstractClassTest extends BaseCheckTestSupport 
{
	@Test
	public void testDefault() throws Exception 
	{
		final DefaultConfiguration checkConfig = 
				createCheckConfig(NoMainMethodInAbstractClass.class);

		final String[] expected = 
			{
				"3: Avoid to have main() method in an abstract class",
				"5: Avoid to have main() method in an abstract class",
				"7: Avoid to have main() method in an abstract class",
			};
		verify(checkConfig, getPath("InputAvoidMainMethodInAbstractClass.java"), expected);
	}
}

