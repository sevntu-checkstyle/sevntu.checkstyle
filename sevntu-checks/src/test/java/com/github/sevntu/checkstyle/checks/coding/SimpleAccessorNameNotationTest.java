package com.github.sevntu.checkstyle.checks.coding;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.github.sevntu.checkstyle.checks.coding.SimpleAccessorNameNotationCheck;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class SimpleAccessorNameNotationTest extends BaseCheckTestSupport {

	@Test
	public void test() throws Exception {
		DefaultConfiguration checkConfig = createCheckConfig(SimpleAccessorNameNotationCheck.class);

		checkConfig.addAttribute("prefix", "m");

		final String[] expected1 = { "7: Unexpected setter name.",
				"10: Unexpected getter name.", "13: Unexpected setter name.",
				"16: Unexpected getter name.", "25: Unexpected setter name.",
				"28: Unexpected getter name.", };

		verify(checkConfig, getPath("InputSimpleAccessorNameNotation.java"), expected1);
	}
	
	@Test
    public void correctWithoutPrefix() throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(SimpleAccessorNameNotationCheck.class);
        final String[] expected = {
                "16: Unexpected setter name.",
                "21: Unexpected getter name.",
        };
        verify(checkConfig, getPath("InputSimpleAccessorNameNotation1.java"), expected);
    }
    
    @Test
    public void correctWithtPrefix() throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(SimpleAccessorNameNotationCheck.class);
        checkConfig.addAttribute("prefix", "m");
        final String[] expected = {
                "6: Unexpected setter name.",
                "11: Unexpected getter name.",
        };
        verify(checkConfig, getPath("InputSimpleAccessorNameNotation1.java"), expected);
    }
    
    @Test
    public void partlyWithoutPrefix() throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(SimpleAccessorNameNotationCheck.class);
        final String[] expected = {
                "14: Unexpected getter name.",
                "19: Unexpected setter name.",
                "29: Unexpected setter name.",
                "34: Unexpected getter name.",
                "39: Unexpected setter name.",
                "44: Unexpected getter name.",
        };
        verify(checkConfig, getPath("InputSimpleAccessorNameNotation2.java"), expected);
    }
    
    @Test
    public void partlyWithPrefix() throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(SimpleAccessorNameNotationCheck.class);
        checkConfig.addAttribute("prefix", "m");
        final String[] expected = {
                "9: Unexpected setter name.",
                "14: Unexpected getter name.",
                "19: Unexpected setter name.",
                "24: Unexpected getter name.",
                "34: Unexpected getter name.",
                "39: Unexpected setter name.",
        };
        verify(checkConfig, getPath("InputSimpleAccessorNameNotation2.java"), expected);
    }
    
    @Test
    public void errorsWithoutPrefix() throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(SimpleAccessorNameNotationCheck.class);
        final String[] expected = {
                "6: Unexpected setter name.",
                "11: Unexpected getter name.",
                "16: Unexpected setter name.",
                "21: Unexpected getter name.",
        };
        verify(checkConfig, getPath("InputSimpleAccessorNameNotation3.java"), expected);
    }
    
    @Test
    public void errorsWithPrefix() throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(SimpleAccessorNameNotationCheck.class);
        checkConfig.addAttribute("prefix", "m");
        final String[] expected = {
                "6: Unexpected setter name.",
                "11: Unexpected getter name.",
                "16: Unexpected setter name.",
                "21: Unexpected getter name.",
        };
        verify(checkConfig, getPath("InputSimpleAccessorNameNotation3.java"), expected);
    }
    
    @Test
    public void cleanWithoutPrefix() throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(SimpleAccessorNameNotationCheck.class);
        final String[] expected = { };
        verify(checkConfig, getPath("InputSimpleAccessorNameNotation4.java"), expected);
    }
    
    @Test
    public void cleanWithPrefix() throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(SimpleAccessorNameNotationCheck.class);
        checkConfig.addAttribute("prefix", "m");
        final String[] expected = { };
        verify(checkConfig, getPath("InputSimpleAccessorNameNotation4.java"), expected);
    }
    
    @Test
    public void testBoolean() throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(SimpleAccessorNameNotationCheck.class);
        final String[] expected = {
                "27: Unexpected setter name.",
                "32: Unexpected getter name.",
        };
        verify(checkConfig, getPath("InputSimpleAccessorNameNotation5.java"), expected);
    }
    
    @Test
    public void testAnonymousCases() throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(SimpleAccessorNameNotationCheck.class);
        final String[] expected = {
        };
        verify(checkConfig, getPath("InputSimpleAccessorNameNotation6.java"), expected);
    }
    
    @Test
    public void testInterface() throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(SimpleAccessorNameNotationCheck.class);
        final String[] expected = {
        };
        verify(checkConfig, getPath("InputSimpleAccessorNameNotation7.java"), expected);
    }
}
