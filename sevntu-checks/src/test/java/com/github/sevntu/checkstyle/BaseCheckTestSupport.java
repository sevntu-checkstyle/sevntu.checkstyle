package com.github.sevntu.checkstyle;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
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

		public BriefLogger(OutputStream out)
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

	public static DefaultConfiguration createCheckConfig(Class<?> aClazz)
	{
		return new DefaultConfiguration(aClazz.getName());
	}

	protected void verify(Configuration aConfig, String aFileName, String[] aExpected)
			throws Exception
	{
		verify(createChecker(aConfig), aFileName, aFileName, aExpected);
	}

	protected void verify(Checker aC, String aFileName, String[] aExpected) throws Exception
	{
		verify(aC, aFileName, aFileName, aExpected);
	}

	protected void verify(Checker aC, String aProcessedFilename, String aMessageFileName,
			String[] aExpected) throws Exception
	{
		verify(aC, new File[] { new File(aProcessedFilename) }, aMessageFileName, aExpected);
	}

	protected void verify(Checker checker, File[] aProcessedFiles, String aMessageFileName,
			String[] aExpected) throws Exception
	{
		printStream.flush();
		List<File> testInputFiles = Lists.newArrayList(aProcessedFiles);
		int foundErrorsCount = checker.process(testInputFiles);

		// Process each output line
		ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
		BufferedReader br = new BufferedReader(new InputStreamReader(bais));

		try {
			for (int i = 0; i < aExpected.length; i++) {
				final String expected = aMessageFileName + ":" + aExpected[i];
				final String actual = br.readLine();
				assertEquals("error message " + i, expected, actual);
			}

			assertEquals("Check generated unexpected warning: " + br.readLine(), aExpected.length, foundErrorsCount);
			checker.destroy();
		} finally {
			br.close();
			bais.close();
		}
	}

	protected Checker createChecker(Configuration aCheckConfig) throws Exception
	{
		Checker checker = new Checker();
		// make sure the tests always run with english error messages
		// so the tests don't fail in supported locales like german
		Locale locale = Locale.ENGLISH;
		checker.setLocaleCountry(locale.getCountry());
		checker.setLocaleLanguage(locale.getLanguage());
		checker.setModuleClassLoader(Thread.currentThread().getContextClassLoader());

		DefaultConfiguration defaultConfig = createCheckerConfig(aCheckConfig);
		checker.configure(defaultConfig);

		checker.addListener(new BriefLogger(printStream));
		return checker;
	}

	protected DefaultConfiguration createCheckerConfig(Configuration aConfig)
	{
		DefaultConfiguration result = new DefaultConfiguration("configuration");
		DefaultConfiguration treeWalkerConfig = createCheckConfig(TreeWalker.class);
		// make sure that the tests always run with this charset
		result.addAttribute("charset", "iso-8859-1");
		result.addChild(treeWalkerConfig);
		treeWalkerConfig.addChild(aConfig);
		return result;
	}

	protected String getPath(String aFilename)
	{
		String result = null;
		try {
			URL resource = getClass().getResource(aFilename);
			if (resource == null) {
				throw new RuntimeException(String.format("Resource '%s' can NOT be found "
						+ "(does not exist or just not visible for current classloader)",
						aFilename));
			} else {
				result = new File(resource.getPath()).getCanonicalPath();
			}
		} catch (IOException e) {
			throw new RuntimeException("Error while getting path for resource: " + aFilename, e);
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

}
