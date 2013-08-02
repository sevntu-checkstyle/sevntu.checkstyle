package com.github.sevntu.checkstyle.checks.naming;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
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
				);
		checkConfig.addAttribute("ignoreOverriddenMethods", "true");

		final String[] expected = {
				// message <Linenumber> <expected capital count>
				String.format(message, 9, expectedCapitalCount),
				String.format(message, 12, expectedCapitalCount),
				String.format(message, 32, expectedCapitalCount),
				String.format(message, 37, expectedCapitalCount) };

		verify(checkConfig, getPath("InputAbbreviationAsWordInTypeNameCheck.java"), expected);
	}

	@Test
	public void testTypeNamesForFourPermitedCapitalLetters() throws Exception {

		final int expectedCapitalCount = 4;
		final DefaultConfiguration checkConfig = createCheckConfig(AbbreviationAsWordInNameCheck.class);
		checkConfig.addAttribute("allowedAbbreviationLength", String.valueOf(expectedCapitalCount));
		checkConfig.addAttribute("allowedAbbreviations", "CLASS,FACTORY");
		checkConfig.addAttribute("targets", "CLASS_DEF"
				);
		checkConfig.addAttribute("ignoreOverriddenMethods", "true");
		
		final String[] expected = {
				// message <Linenumber> <expected capital count>
				String.format(message, 32, expectedCapitalCount) };

		verify(checkConfig, getPath("InputAbbreviationAsWordInTypeNameCheck.java"), expected);
	}

	@Test
	public void testTypeNamesForFivePermitedCapitalLetters() throws Exception {

		final int expectedCapitalCount = 5;
		final DefaultConfiguration checkConfig = createCheckConfig(AbbreviationAsWordInNameCheck.class);
		checkConfig.addAttribute("allowedAbbreviationLength", String.valueOf(expectedCapitalCount));
		checkConfig.addAttribute("allowedAbbreviations", "CLASS");
		checkConfig.addAttribute("targets", "CLASS_DEF"
				);
		checkConfig.addAttribute("ignoreOverriddenMethods", "true");
		
		final String[] expected = {
				// message <Linenumber> <expected capital count>
				String.format(message, 32, expectedCapitalCount),
				String.format(message, 37, expectedCapitalCount),
			};

		verify(checkConfig, getPath("InputAbbreviationAsWordInTypeNameCheck.java"), expected);
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

		verify(checkConfig, getPath("InputAbbreviationAsWordInTypeNameCheck.java"), expected);
	}
	
	@Test
	public void testTypeAndVariablesAndMethodNamesWithNoIgnores() throws Exception {

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
				String.format(message, 66, expectedCapitalCount),
				String.format(message, 72, expectedCapitalCount),
				String.format(message, 78, expectedCapitalCount),
				String.format(message, 84, expectedCapitalCount),
			};

		verify(checkConfig, getPath("InputAbbreviationAsWordInTypeNameCheck.java"), expected);
	}

	@Test
	public void testTypeAndVariablesAndMethodNamesWithIgnores() throws Exception {

		final int expectedCapitalCount = 5;
		final DefaultConfiguration checkConfig = createCheckConfig(AbbreviationAsWordInNameCheck.class);
		checkConfig.addAttribute("allowedAbbreviationLength", String.valueOf(expectedCapitalCount));
		checkConfig.addAttribute("allowedAbbreviations", "NUMBER,MARAZMATIC,VARIABLE");
		checkConfig.addAttribute("ignoreStatic", "true");
		checkConfig.addAttribute("ignoreFinal", "true");
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
			};

		verify(checkConfig, getPath("InputAbbreviationAsWordInTypeNameCheck.java"), expected);
	}

	@Test
	public void testTypeAndVariablesAndMethodNamesWithIgnoresFinal() throws Exception {

		final int expectedCapitalCount = 4;
		final DefaultConfiguration checkConfig = createCheckConfig(AbbreviationAsWordInNameCheck.class);
		checkConfig.addAttribute("allowedAbbreviationLength", String.valueOf(expectedCapitalCount));
		checkConfig.addAttribute("allowedAbbreviations", "MARAZMATIC,VARIABLE");
		checkConfig.addAttribute("ignoreStatic", "false");
		checkConfig.addAttribute("ignoreFinal", "true");
		checkConfig.addAttribute("targets", "CLASS_DEF" + 
				",VARIABLE_DEF" +
				",METHOD_DEF,ENUM_DEF,ENUM_CONSTANT_DEF" +
				",PARAMETER_DEF,INTERFACE_DEF,ANNOTATION_DEF"
				);
		checkConfig.addAttribute("ignoreOverriddenMethods", "true");
		
		final String[] expected = {
				// message <Linenumber> <expected capital count>
				String.format(message, 12, expectedCapitalCount),
				String.format(message, 32, expectedCapitalCount),
				String.format(message, 37, expectedCapitalCount),
				String.format(message, 38, expectedCapitalCount),
				String.format(message, 58, expectedCapitalCount), // not in ignore list
				String.format(message, 60, expectedCapitalCount), // no ignore for static
			};

		verify(checkConfig, getPath("InputAbbreviationAsWordInTypeNameCheck.java"), expected);
	}
	
	@Test
	public void testTypeAndVariablesAndMethodNamesWithIgnoresStatic() throws Exception {

		final int expectedCapitalCount = 5;
		final DefaultConfiguration checkConfig = createCheckConfig(AbbreviationAsWordInNameCheck.class);
		checkConfig.addAttribute("allowedAbbreviationLength", String.valueOf(expectedCapitalCount));
		checkConfig.addAttribute("allowedAbbreviations", "MARAZMATIC,VARIABLE");
		checkConfig.addAttribute("ignoreStatic", "true");
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
				String.format(message, 58, expectedCapitalCount), // not in ignore list
				String.format(message, 59, expectedCapitalCount), // no ignore for final
			};

		verify(checkConfig, getPath("InputAbbreviationAsWordInTypeNameCheck.java"), expected);
	}

	@Test
    public void testTypeNamesForThreePermitedCapitalLettersWithOverridenMethod() throws Exception {

        final DefaultConfiguration checkConfig = createCheckConfig(AbbreviationAsWordInNameCheck.class);
        final int expectedCapitalCount = 3;
        checkConfig.addAttribute("allowedAbbreviationLength", String.valueOf(expectedCapitalCount));
        checkConfig.addAttribute("allowedAbbreviations", "");
        checkConfig.addAttribute("targets", "CLASS_DEF, METHOD_DEF"
                );
        checkConfig.addAttribute("ignoreOverriddenMethods", "true");

        final String[] expected = {
            String.format(message, 22, expectedCapitalCount),
            };

        verify(checkConfig, 
        		getPath("InputAbbreviationAsWordInTypeNameCheckOverridableMethod.java"), expected);
    }

	@Test
	public void testTypeNamesForZeroPermitedCapitalLetter() throws Exception {
	    final DefaultConfiguration checkConfig =
	            createCheckConfig(AbbreviationAsWordInNameCheck.class);
	    final int expectedCapitalCount = 0;
	    checkConfig.addAttribute("allowedAbbreviationLength",
	            String.valueOf(expectedCapitalCount));
	    checkConfig.addAttribute("allowedAbbreviations", "");
	    checkConfig.addAttribute("ignoreStatic", "false");
	    checkConfig.addAttribute("ignoreFinal", "false");
	    checkConfig.addAttribute("ignoreOverriddenMethods", "false");
	    checkConfig.addAttribute("targets", "CLASS_DEF,INTERFACE_DEF,ENUM_DEF,"
	            + "ANNOTATION_DEF,ANNOTATION_FIELD_DEF,ENUM_CONSTANT_DEF,"
	            + "PARAMETER_DEF,VARIABLE_DEF,METHOD_DEF"
	    );
	    final String[] expected = {
	            String.format(message, 3, expectedCapitalCount),
	            String.format(message, 6, expectedCapitalCount),
	            String.format(message, 9, expectedCapitalCount),
	            String.format(message, 12, expectedCapitalCount),
	            String.format(message, 32, expectedCapitalCount),
	            String.format(message, 37, expectedCapitalCount),
	            String.format(message, 38, expectedCapitalCount),
	            String.format(message, 39, expectedCapitalCount),
	            String.format(message, 40, expectedCapitalCount),
	            String.format(message, 46, expectedCapitalCount),
	            String.format(message, 47, expectedCapitalCount),
	            String.format(message, 48, expectedCapitalCount),
	            String.format(message, 49, expectedCapitalCount),
	            String.format(message, 57, expectedCapitalCount),
	            String.format(message, 58, expectedCapitalCount),
	            String.format(message, 59, expectedCapitalCount),
	            String.format(message, 60, expectedCapitalCount),
	            String.format(message, 61, expectedCapitalCount),
	            String.format(message, 66, expectedCapitalCount),
	            String.format(message, 72, expectedCapitalCount),
	            String.format(message, 78, expectedCapitalCount),
	            String.format(message, 84, expectedCapitalCount),
	            String.format(message, 88, expectedCapitalCount),
	            String.format(message, 90, expectedCapitalCount),
                String.format(message, 98, expectedCapitalCount),
	    };
	    verify(checkConfig, 
	            getPath("InputAbbreviationAsWordInTypeNameCheck.java"), expected);
	}
}
