package com.puppycrawl.tools.checkstyle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader;
import com.puppycrawl.tools.checkstyle.PropertiesExpander;
import com.puppycrawl.tools.checkstyle.api.AuditListener;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.Configuration;
import com.puppycrawl.tools.checkstyle.api.SeverityLevel;
import com.puppycrawl.tools.checkstyle.api.SeverityLevelCounter;

public class InnerClassCheckTest2 {

	@Test
	public void testVisitTokenDetailAST() {

		String className = "/home/dev/workspace/Checkrs/src/exmp/Outer.java";
		String configName = "/home/dev/workspace/config.xml";

		final AuditListener listener = new SeverityLevelCounter(SeverityLevel.ERROR);

		final List<File> files = new ArrayList<File>();
		File file = new File(className);
		files.add(file);

		final Properties props = System.getProperties();
		final Configuration config = loadConfig(configName, props);
		final Checker c = createChecker(config, listener);
		final int numErrs = c.process(files);

		Assert.assertEquals(6, numErrs);
	}

	private static Checker createChecker(Configuration aConfig,
			AuditListener aNosy) {
		Checker c = null;
		try {
			c = new Checker();

			final ClassLoader moduleClassLoader =
					Checker.class.getClassLoader();
			c.setModuleClassLoader(moduleClassLoader);
			c.configure(aConfig);
			c.addListener(aNosy);
		} catch (final Exception e) {
			System.out.println("Unable to create Checker: "
					+ e.getMessage());
			e.printStackTrace(System.out);
			System.exit(1);
		}
		return c;
	}

	private static Configuration loadConfig(String aLine,
			Properties aProps) {
		try {
			return ConfigurationLoader.loadConfiguration(
					aLine, new PropertiesExpander(aProps));
		} catch (final CheckstyleException e) {
			System.out.println("Error loading configuration file");
			e.printStackTrace(System.out);
			System.exit(1);
			return null; // can never get here
		}
	}

}
