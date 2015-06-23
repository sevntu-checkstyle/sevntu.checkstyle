package com.github.sevntu.checkstyle;

import static java.text.MessageFormat.format;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.junit.Assert;

import com.google.common.collect.Lists;
import com.puppycrawl.tools.checkstyle.Checker;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.DefaultLogger;
import com.puppycrawl.tools.checkstyle.TreeWalker;
import com.puppycrawl.tools.checkstyle.api.AuditEvent;
import com.puppycrawl.tools.checkstyle.api.Configuration;

public abstract class BaseCheckTestSupport extends Assert
{
	/** A brief logger that only display errors */
	protected static class BriefLogger extends DefaultLogger
	{

		public BriefLogger(OutputStream out) throws UnsupportedEncodingException
		{
			super(out, true);
		}

		@Override
		public void auditStarted(AuditEvent evt) {
		}

		@Override
		public void fileFinished(AuditEvent evt) {
		}

		@Override
		public void fileStarted(AuditEvent evt) {
		}
	}

	private final ByteArrayOutputStream baos = new ByteArrayOutputStream();
	private final PrintStream printStream = new PrintStream(baos);

	public static DefaultConfiguration createCheckConfig(Class<?> clazz)
	{
		return new DefaultConfiguration(clazz.getName());
	}

	protected void verify(Configuration config, String fileName, String[] expected)
			throws Exception
	{
		verify(createChecker(config), fileName, fileName, expected);
	}

	protected void verify(Checker c, String fileName, String[] expected) throws Exception
	{
		verify(c, fileName, fileName, expected);
	}

	protected void verify(Checker c, String processedFilename, String messageFileName,
			String[] aExpected) throws Exception
	{
		verify(c, new File[] { new File(processedFilename) }, messageFileName, aExpected);
	}

	protected void verify(Checker checker, File[] processedFiles, String messageFileName,
			String[] expected) throws Exception
	{
		printStream.flush();
		List<File> testInputFiles = Lists.newArrayList(processedFiles);
		int foundErrorsCount = checker.process(testInputFiles);

		// Process each output line
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		BufferedReader br = new BufferedReader(new InputStreamReader(bais));

		try {
			for (int i = 0; i < expected.length; i++) {
				final String expectedResult = messageFileName + ":" + expected[i];
				final String actual = br.readLine();
				assertEquals("error message " + i, expectedResult, actual);
			}

			assertEquals("Check generated unexpected warning: " + br.readLine(), expected.length, foundErrorsCount);
			checker.destroy();
		} finally {
			br.close();
			bais.close();
		}
	}

	protected Checker createChecker(Configuration checkConfig) throws Exception
	{
		Checker checker = new Checker();
		// make sure the tests always run with english error messages
		// so the tests don't fail in supported locales like german
		Locale locale = Locale.ENGLISH;
		checker.setLocaleCountry(locale.getCountry());
		checker.setLocaleLanguage(locale.getLanguage());
		checker.setModuleClassLoader(Thread.currentThread().getContextClassLoader());

		DefaultConfiguration defaultConfig = createCheckerConfig(checkConfig);
		checker.configure(defaultConfig);

		checker.addListener(new BriefLogger(printStream));
		return checker;
	}

	protected DefaultConfiguration createCheckerConfig(Configuration config)
	{
		DefaultConfiguration result = new DefaultConfiguration("configuration");
		DefaultConfiguration treeWalkerConfig = createCheckConfig(TreeWalker.class);
		// make sure that the tests always run with this charset
		result.addAttribute("charset", "iso-8859-1");
		result.addChild(treeWalkerConfig);
		treeWalkerConfig.addChild(config);
		return result;
	}

	protected String getPath(String filename)
	{
		String result = null;
		try {
			URL resource = getClass().getResource(filename);
			if (resource == null) {
				throw new RuntimeException(String.format("Resource '%s' can NOT be found "
						+ "(does not exist or just not visible for current classloader)",
						filename));
			} else {
				result = new File(resource.getPath()).getCanonicalPath();
			}
		} catch (IOException e) {
			throw new RuntimeException("Error while getting path for resource: " + filename, e);
		}
		return result;
	}

	/**
	 * Gets the check message 'as is' from appropriate 'messages.properties' file.
	 * @param messageKey the key of message in 'messages.properties' file.
	 */
	public String getCheckMessage(String messageKey) {
		Properties pr = new Properties();
		try {
			pr.load(getClass().getResourceAsStream("messages.properties"));
		} catch (IOException e) {
			return null;
		}
		return pr.getProperty(messageKey);
	}
	
	/**
	 * Gets the check message 'as is' from appropriate 'messages.properties' file.
	 * @param messageKey the key of message in 'messages.properties' file.
	 * @param arguments the arguments of message in 'messages.properties' file.
	 */
	public String getCheckMessage(String messageKey, Object ... arguments) {
		return format(getCheckMessage(messageKey), arguments);
	}
}
