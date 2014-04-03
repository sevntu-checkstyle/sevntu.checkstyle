package com.github.sevntu.checkstyle.checks.coding;

import static com.github.sevntu.checkstyle.checks.coding.NoNullForCollectionReturnCheck.*;	

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class NoNullForCollectionReturnCheckTest extends BaseCheckTestSupport
{

	private final String warningMessage = getCheckMessage(MSG_KEY);
	
    @Test
    public void testArraysNotDeep() throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(NoNullForCollectionReturnCheck.class);
        final String[] expected = {
                "10: " + warningMessage,
                "45: " + warningMessage,
                "53: " + warningMessage,
                "58: " + warningMessage,
        };

        verify(checkConfig, getPath("InputNoNullForCollectionReturnCheck1.java"), expected);
    }
    
    @Test
    public void testArraysDeep() throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(NoNullForCollectionReturnCheck.class);
        checkConfig.addAttribute("searchThroughMethodBody", "true");
        final String[] expected = {
                "10: " + warningMessage,
                "17: " + warningMessage,
                "45: " + warningMessage,
                "53: " + warningMessage,
                "58: " + warningMessage,
        };

        verify(checkConfig, getPath("InputNoNullForCollectionReturnCheck1.java"), expected);
    }
    
    @Test
    public void testCollections() throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(NoNullForCollectionReturnCheck.class);
        String listOfCollecton = "Collection ArrayList LinkedList Stack Vector HashSet TreeSet";
        checkConfig.addAttribute("collectionList", listOfCollecton);
        final String[] expected = {

                "5: " + warningMessage,
                "15: " + warningMessage,
                "25: " + warningMessage,
                "35: " + warningMessage,
                "45: " + warningMessage,
                "55: " + warningMessage,
                "65: " + warningMessage,
        };

        verify(checkConfig, getPath("InputNoNullForCollectionReturnCheck2.java"), expected);
    }
    
    @Test
    public void testRevereCode() throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(NoNullForCollectionReturnCheck.class);
        final String[] expected = {
                "12: " + warningMessage,
                "15: " + warningMessage,
        };

        verify(checkConfig, getPath("InputNoNullForCollectionReturnCheck3.java"), expected);
    }
    
    @Test
    public void testRevereCodeDeep() throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(NoNullForCollectionReturnCheck.class);
        checkConfig.addAttribute("searchThroughMethodBody", "true");
        final String[] expected = {
                "12: " + warningMessage,
                "15: " + warningMessage,
        };

        verify(checkConfig, getPath("InputNoNullForCollectionReturnCheck3.java"), expected);
    }
    
    @Test
    public void testInterface() throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(NoNullForCollectionReturnCheck.class);
        final String[] expected = {
        };

        verify(checkConfig, getPath("InputNoNullForCollectionReturnCheck4.java"), expected);
    }
    
    @Test
    public void testInnerClasses() throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(NoNullForCollectionReturnCheck.class);
        final String[] expected = {
                "16: " + warningMessage,
                "24: " + warningMessage,
                "26: " + warningMessage,
                "38: " + warningMessage,
        };

        verify(checkConfig, getPath("InputNoNullForCollectionReturnCheck5.java"), expected);
    }
    
    @Test
    public void testRealCode() throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(NoNullForCollectionReturnCheck.class);
        checkConfig.addAttribute("searchThroughMethodBody", "true");
        final String[] expected = {
        };

        verify(checkConfig, getPath("InputNoNullForCollectionReturnCheck6.java"), expected);
    }

    @Test
    public void testIss148()
            throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(NoNullForCollectionReturnCheck.class);
        final String[] expected = {
                "24: " + warningMessage,
                };

        verify(checkConfig, getPath("InputNoNullForCollectionReturnCheck7.java"), expected);
    }

    @Test
    public void testIss148Deep()
            throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(NoNullForCollectionReturnCheck.class);
        checkConfig.addAttribute("searchThroughMethodBody", "true");
        final String[] expected = {
                "24: " + warningMessage,
                };

        verify(checkConfig, getPath("InputNoNullForCollectionReturnCheck7.java"), expected);
    }
    
    
}
