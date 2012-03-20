package com.puppycrawl.tools.checkstyle.checks.naming;

import java.io.File;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class AbbreviationAsWordInNameCheckTest extends BaseCheckTestSupport {

	private final String message = "%d: Abbreviation in name must contain no more than '%d' capital letters.";
	
	@Test
	public void testTypeNamesForThreePermitedCapitalLetters() throws Exception {

		final DefaultConfiguration checkConfig = createCheckConfig(AbbreviationAsWordInNameCheck.class);
		final int expectedCapitalCount = 3;
		checkConfig.addAttribute("allowedAbbreviationLength", String.valueOf(expectedCapitalCount));
		checkConfig.addAttribute("allowedAbbreviations", "III");
		checkConfig.addAttribute("targets", "CLASS_DEF"
				//",VARIABLE_DEF" +
				//",METHOD_DEF,ENUM_DEF,ENUM_CONSTANT_DEF" +
				//",PARAMETER_DEF,INTERFACE_DEF,ANNOTATION_DEF"
				);
		checkConfig.addAttribute("ignoreOverriddenMethods", "true");

		final String[] expected = {
				// message <Linenumber> <expected capital count>
				String.format(message, 9, expectedCapitalCount),
				String.format(message, 12, expectedCapitalCount),
				String.format(message, 32, expectedCapitalCount),
				String.format(message, 37, expectedCapitalCount) };

		verify(checkConfig, getPath("naming" + File.separator
				+ "InputAbbreviationAsWordInTypeNameCheck.java"), expected);
	}

	@Test
	public void testTypeNamesForFourPermitedCapitalLetters() throws Exception {

		final int expectedCapitalCount = 4;
		final DefaultConfiguration checkConfig = createCheckConfig(AbbreviationAsWordInNameCheck.class);
		checkConfig.addAttribute("allowedAbbreviationLength", String.valueOf(expectedCapitalCount));
		checkConfig.addAttribute("allowedAbbreviations", "CLASS,FACTORY");
		checkConfig.addAttribute("targets", "CLASS_DEF"
				//",VARIABLE_DEF" +
				//",METHOD_DEF,ENUM_DEF,ENUM_CONSTANT_DEF" +
				//",PARAMETER_DEF,INTERFACE_DEF,ANNOTATION_DEF"
				);
		checkConfig.addAttribute("ignoreOverriddenMethods", "true");
		
		final String[] expected = {
				// message <Linenumber> <expected capital count>
				String.format(message, 32, expectedCapitalCount) };

		verify(checkConfig, getPath("naming" + File.separator
				+ "InputAbbreviationAsWordInTypeNameCheck.java"), expected);
	}

	@Test
	public void testTypeNamesForFivePermitedCapitalLetters() throws Exception {

		final int expectedCapitalCount = 5;
		final DefaultConfiguration checkConfig = createCheckConfig(AbbreviationAsWordInNameCheck.class);
		checkConfig.addAttribute("allowedAbbreviationLength", String.valueOf(expectedCapitalCount));
		checkConfig.addAttribute("allowedAbbreviations", "CLASS");
		checkConfig.addAttribute("targets", "CLASS_DEF"
				//",VARIABLE_DEF" +
				//",METHOD_DEF,ENUM_DEF,ENUM_CONSTANT_DEF" +
				//",PARAMETER_DEF,INTERFACE_DEF,ANNOTATION_DEF"
				);
		checkConfig.addAttribute("ignoreOverriddenMethods", "true");
		
		final String[] expected = {
				// message <Linenumber> <expected capital count>
				String.format(message, 32, expectedCapitalCount),
				String.format(message, 37, expectedCapitalCount),
			};

		verify(checkConfig, getPath("naming" + File.separator
				+ "InputAbbreviationAsWordInTypeNameCheck.java"), expected);
	}

	@Test
	public void testTypeAndVariablesAndMethodNames() throws Exception {

		final int expectedCapitalCount = 5;
		final DefaultConfiguration checkConfig = createCheckConfig(AbbreviationAsWordInNameCheck.class);
		checkConfig.addAttribute("allowedAbbreviationLength", String.valueOf(expectedCapitalCount));
		checkConfig.addAttribute("allowedAbbreviations", "CLASS");
		checkConfig.addAttribute("targets", "CLASS_DEF" + 
				",VARIABLE_DEF" +
				",METHOD_DEF,ENUM_DEF,ENUM_CONSTANT_DEF" +
				",PARAMETER_DEF,INTERFACE_DEF,ANNOTATION_DEF"
				);
		checkConfig.addAttribute("ignoreOverriddenMethods", "true");
		
		final String[] expected = {
				// message <Linenumber> <expected capital count>
				String.format(message, 32, expectedCapitalCount),
				String.format(message, 37, expectedCapitalCount),
				String.format(message, 38, expectedCapitalCount),
				String.format(message, 39, expectedCapitalCount),
				String.format(message, 40, expectedCapitalCount),
				String.format(message, 58, expectedCapitalCount),
			};

		verify(checkConfig, getPath("naming" + File.separator
				+ "InputAbbreviationAsWordInTypeNameCheck.java"), expected);
	}
	
	@Test
	public void testTypeAndVariablesAndMethodNamesWithIgnores() throws Exception {

		final int expectedCapitalCount = 5;
		final DefaultConfiguration checkConfig = createCheckConfig(AbbreviationAsWordInNameCheck.class);
		checkConfig.addAttribute("allowedAbbreviationLength", String.valueOf(expectedCapitalCount));
		checkConfig.addAttribute("allowedAbbreviations", "NUMBER,MARAZMATIC,VARIABLE");
		checkConfig.addAttribute("ignoreStatic", "false");
		checkConfig.addAttribute("ignoreFinal", "false");
		checkConfig.addAttribute("targets", "CLASS_DEF" + 
				",VARIABLE_DEF" +
				",METHOD_DEF,ENUM_DEF,ENUM_CONSTANT_DEF" +
				",PARAMETER_DEF,INTERFACE_DEF,ANNOTATION_DEF"
				);
		checkConfig.addAttribute("ignoreOverriddenMethods", "true");
		
		final String[] expected = {
				// message <Linenumber> <expected capital count>
				String.format(message, 32, expectedCapitalCount),
				String.format(message, 37, expectedCapitalCount),
				String.format(message, 38, expectedCapitalCount),
//				String.format(message, 59, expectedCapitalCount),
//				String.format(message, 60, expectedCapitalCount),
//				String.format(message, 61, expectedCapitalCount),
			};

		verify(checkConfig, getPath("naming" + File.separator
				+ "InputAbbreviationAsWordInTypeNameCheck.java"), expected);
	}
	
	   @Test
	    public void testTypeNamesForThreePermitedCapitalLettersWithOverridenMethod() throws Exception {

	        final DefaultConfiguration checkConfig = createCheckConfig(AbbreviationAsWordInNameCheck.class);
	        final int expectedCapitalCount = 3;
	        checkConfig.addAttribute("allowedAbbreviationLength", String.valueOf(expectedCapitalCount));
	        checkConfig.addAttribute("allowedAbbreviations", "");
	        checkConfig.addAttribute("targets", "CLASS_DEF, METHOD_DEF"
	                //",VARIABLE_DEF" +
	                //",,ENUM_DEF,ENUM_CONSTANT_DEF" +
	                //",PARAMETER_DEF,INTERFACE_DEF,ANNOTATION_DEF"
	                );
	        checkConfig.addAttribute("ignoreOverriddenMethods", "true");

        final String[] expected = {
                };

	        verify(checkConfig, getPath("naming" + File.separator
	                + "InputAbbreviationAsWordInTypeNameCheckOverridableMethod.java"), expected);
	    }
	
}
