package com.puppycrawl.tools.checkstyle.checks.naming;

import java.io.File;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class AbbreviationAsWordInTypeNameCheckTest extends BaseCheckTestSupport {

    @Test
    public void testTypeNamesForThreePermitedCapitalLetters() throws Exception {
	
	final DefaultConfiguration checkConfig = createCheckConfig(AbbreviationAsWordInTypeNameCheck.class);
	checkConfig.addAttribute("allowedCapitalCounts", "3");
	checkConfig.addAttribute("permitedWords", "IIIInputAbstractClassName");
	
	final String[] expected = {
		"6: Type name must contain no more than '3' capital letters.",
		"9: Type name must contain no more than '3' capital letters.",
		"12: Type name must contain no more than '3' capital letters.",
		"32: Type name must contain no more than '3' capital letters.",
		"37: Type name must contain no more than '3' capital letters."
	};
	
	verify(checkConfig, getPath("naming" + File.separator
		+ "InputAbbreviationAsWordInTypeNameCheck.java"), expected);
    }
    
    @Test
    public void testTypeNamesForFourPermitedCapitalLetters() throws Exception {
	
	final DefaultConfiguration checkConfig = createCheckConfig(AbbreviationAsWordInTypeNameCheck.class);
	checkConfig.addAttribute("allowedCapitalCounts", "4");
	checkConfig.addAttribute("permitedWords", "AbstractCLASSName,WellNamedFACTORY");
	
	final String[] expected = {
		"9: Type name must contain no more than '4' capital letters.",
		"32: Type name must contain no more than '4' capital letters."
	};
	
	verify(checkConfig, getPath("naming" + File.separator
		+ "InputAbbreviationAsWordInTypeNameCheck.java"), expected);
    }
    
    @Test
    public void testTypeNamesForFivePermitedCapitalLetters() throws Exception {
	
	final DefaultConfiguration checkConfig = createCheckConfig(AbbreviationAsWordInTypeNameCheck.class);
	checkConfig.addAttribute("allowedCapitalCounts", "5");
	checkConfig.addAttribute("permitedWords", "AbstractCLASSName");
	
	final String[] expected = {
		"32: Type name must contain no more than '5' capital letters.",
		"37: Type name must contain no more than '5' capital letters."
	};
	
	verify(checkConfig, getPath("naming" + File.separator
		+ "InputAbbreviationAsWordInTypeNameCheck.java"), expected);
    }
    
    @Test
    public void testTypeAndVariablesAndMethodNames() throws Exception {
	
	final DefaultConfiguration checkConfig = createCheckConfig(AbbreviationAsWordInTypeNameCheck.class);
	checkConfig.addAttribute("allowedCapitalCounts", "5");
	checkConfig.addAttribute("permitedWords", "AbstractCLASSName");
	checkConfig.addAttribute("checkVariablesAndMethodsNames", "true");
	
	final String[] expected = {
		"32: Type name must contain no more than '5' capital letters.",
		"37: Type name must contain no more than '5' capital letters.",
		"38: Type name must contain no more than '5' capital letters.",
		"39: Type name must contain no more than '5' capital letters.",
		"40: Type name must contain no more than '5' capital letters."
	};
	
	verify(checkConfig, getPath("naming" + File.separator
		+ "InputAbbreviationAsWordInTypeNameCheck.java"), expected);
    }
}
