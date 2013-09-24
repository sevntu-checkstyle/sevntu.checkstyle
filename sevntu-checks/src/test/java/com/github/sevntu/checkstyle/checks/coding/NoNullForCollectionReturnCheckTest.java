package com.github.sevntu.checkstyle.checks.coding;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class NoNullForCollectionReturnCheckTest extends BaseCheckTestSupport
{

    @Test
    public void testArraysNotDeep() throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(NoNullForCollectionReturnCheck.class);
        final String[] expected = {
                "10: Method returns null instead of empty collection.",
                "45: Method returns null instead of empty collection.",
                "53: Method returns null instead of empty collection.",
                "58: Method returns null instead of empty collection.",
        };

        verify(checkConfig, getPath("InputNoNullForCollectionReturnCheck1.java"), expected);
    }
    
    @Test
    public void testArraysDeep() throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(NoNullForCollectionReturnCheck.class);
        checkConfig.addAttribute("searchThroughMethodBody", "true");
        final String[] expected = {
                "10: Method returns null instead of empty collection.",
                "17: Method returns null instead of empty collection.",
                "45: Method returns null instead of empty collection.",
                "53: Method returns null instead of empty collection.",
                "58: Method returns null instead of empty collection.",
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

                "5: Method returns null instead of empty collection.",
                "15: Method returns null instead of empty collection.",
                "25: Method returns null instead of empty collection.",
                "35: Method returns null instead of empty collection.",
                "45: Method returns null instead of empty collection.",
                "55: Method returns null instead of empty collection.",
                "65: Method returns null instead of empty collection.",
        };

        verify(checkConfig, getPath("InputNoNullForCollectionReturnCheck2.java"), expected);
    }
    
    @Test
    public void testRevereCode() throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(NoNullForCollectionReturnCheck.class);
        final String[] expected = {
                "12: Method returns null instead of empty collection.",
                "15: Method returns null instead of empty collection.",
        };

        verify(checkConfig, getPath("InputNoNullForCollectionReturnCheck3.java"), expected);
    }
    
    @Test
    public void testRevereCodeDeep() throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(NoNullForCollectionReturnCheck.class);
        checkConfig.addAttribute("searchThroughMethodBody", "true");
        final String[] expected = {
                "12: Method returns null instead of empty collection.",
                "15: Method returns null instead of empty collection.",
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
                "16: Method returns null instead of empty collection.",
                "24: Method returns null instead of empty collection.",
                "26: Method returns null instead of empty collection.",
                "38: Method returns null instead of empty collection.",
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
}
