package com.github.sevntu.checkstyle.checks.coding;

import static com.github.sevntu.checkstyle.checks.coding.SimpleAccessorNameNotationCheck.*;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.github.sevntu.checkstyle.checks.coding.SimpleAccessorNameNotationCheck;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class SimpleAccessorNameNotationTest extends BaseCheckTestSupport {

	private final String warningGetterMessage = getCheckMessage(MSG_KEY_GETTER);
	private final String warningSetterMessage = getCheckMessage(MSG_KEY_SETTER);
	
	@Test
	public void test() throws Exception {
		DefaultConfiguration checkConfig = createCheckConfig(SimpleAccessorNameNotationCheck.class);

		checkConfig.addAttribute("prefix", "m");

		final String[] expected = { 
				"7: " + warningSetterMessage,
				"10: " + warningGetterMessage, 
				"13: " + warningSetterMessage,
				"16: " + warningGetterMessage, 
				"25: " + warningSetterMessage,
				"28: " + warningGetterMessage, };

		verify(checkConfig, getPath("InputSimpleAccessorNameNotation.java"), expected);
	}
	
	@Test
    public void correctWithoutPrefix() throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(SimpleAccessorNameNotationCheck.class);
        final String[] expected = {
                "16: " + warningSetterMessage,
                "21: " + warningGetterMessage,
        };
        verify(checkConfig, getPath("InputSimpleAccessorNameNotation1.java"), expected);
    }
    
    @Test
    public void correctWithtPrefix() throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(SimpleAccessorNameNotationCheck.class);
        checkConfig.addAttribute("prefix", "m");
        final String[] expected = {
                "6: " + warningSetterMessage,
                "11: " + warningGetterMessage,
        };
        verify(checkConfig, getPath("InputSimpleAccessorNameNotation1.java"), expected);
    }
    
    @Test
    public void partlyWithoutPrefix() throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(SimpleAccessorNameNotationCheck.class);
        final String[] expected = {
                "14: " + warningGetterMessage,
                "19: " + warningSetterMessage,
                "29: " + warningSetterMessage,
                "34: " + warningGetterMessage,
                "39: " + warningSetterMessage,
                "44: " + warningGetterMessage,
        };
        verify(checkConfig, getPath("InputSimpleAccessorNameNotation2.java"), expected);
    }
    
    @Test
    public void partlyWithPrefix() throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(SimpleAccessorNameNotationCheck.class);
        checkConfig.addAttribute("prefix", "m");
        final String[] expected = {
                "9: " + warningSetterMessage,
                "14: " + warningGetterMessage,
                "19: " + warningSetterMessage,
                "24: " + warningGetterMessage,
                "34: " + warningGetterMessage,
                "39: " + warningSetterMessage,
        };
        verify(checkConfig, getPath("InputSimpleAccessorNameNotation2.java"), expected);
    }
    
    @Test
    public void errorsWithoutPrefix() throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(SimpleAccessorNameNotationCheck.class);
        final String[] expected = {
                "6: " + warningSetterMessage,
                "11: " + warningGetterMessage,
                "16: " + warningSetterMessage,
                "21: " + warningGetterMessage,
        };
        verify(checkConfig, getPath("InputSimpleAccessorNameNotation3.java"), expected);
    }
    
    @Test
    public void errorsWithPrefix() throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(SimpleAccessorNameNotationCheck.class);
        checkConfig.addAttribute("prefix", "m");
        final String[] expected = {
                "6: " + warningSetterMessage,
                "11: " + warningGetterMessage,
                "16: " + warningSetterMessage,
                "21: " + warningGetterMessage,
        };
        verify(checkConfig, getPath("InputSimpleAccessorNameNotation3.java"), expected);
    }
    
    @Test
    public void cleanWithoutPrefix() throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(SimpleAccessorNameNotationCheck.class);
        final String[] expected = {};
        verify(checkConfig, getPath("InputSimpleAccessorNameNotation4.java"), expected);
    }
    
    @Test
    public void cleanWithPrefix() throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(SimpleAccessorNameNotationCheck.class);
        checkConfig.addAttribute("prefix", "m");
        final String[] expected = {};
        verify(checkConfig, getPath("InputSimpleAccessorNameNotation4.java"), expected);
    }
    
    @Test
    public void testBoolean() throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(SimpleAccessorNameNotationCheck.class);
        final String[] expected = {
                "27: " + warningSetterMessage,
                "32: " + warningGetterMessage,
        };
        verify(checkConfig, getPath("InputSimpleAccessorNameNotation5.java"), expected);
    }
    
    @Test
    public void testAnonymousCases() throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(SimpleAccessorNameNotationCheck.class);
        final String[] expected = {};
        verify(checkConfig, getPath("InputSimpleAccessorNameNotation6.java"), expected);
    }
    
    @Test
    public void testInterface() throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(SimpleAccessorNameNotationCheck.class);
        final String[] expected = {};
        verify(checkConfig, getPath("InputSimpleAccessorNameNotation7.java"), expected);
    }
}
