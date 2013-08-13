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
                "10: Method return null instead empty collection.",
                "45: Method return null instead empty collection.",
                "53: Method return null instead empty collection.",
                "58: Method return null instead empty collection.",
        };

        verify(checkConfig, getPath("InputNoNullForCollectionReturnCheck1.java"), expected);
    }
    
    @Test
    public void testArraysDeep() throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(NoNullForCollectionReturnCheck.class);
        checkConfig.addAttribute("searchThroughMethodBody", "true");
        final String[] expected = {
                "10: Method return null instead empty collection.",
                "17: Method return null instead empty collection.",
                "45: Method return null instead empty collection.",
                "53: Method return null instead empty collection.",
                "58: Method return null instead empty collection.",
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

                "5: Method return null instead empty collection.",
                "15: Method return null instead empty collection.",
                "25: Method return null instead empty collection.",
                "35: Method return null instead empty collection.",
                "45: Method return null instead empty collection.",
                "55: Method return null instead empty collection.",
                "65: Method return null instead empty collection.",
        };

        verify(checkConfig, getPath("InputNoNullForCollectionReturnCheck2.java"), expected);
    }
    
    @Test
    public void testRevereCode() throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(NoNullForCollectionReturnCheck.class);
        final String[] expected = {
                "12: Method return null instead empty collection.",
                "15: Method return null instead empty collection.",
        };

        verify(checkConfig, getPath("InputNoNullForCollectionReturnCheck3.java"), expected);
    }
    
    @Test
    public void testRevereCodeDeep() throws Exception
    {
        final DefaultConfiguration checkConfig = createCheckConfig(NoNullForCollectionReturnCheck.class);
        checkConfig.addAttribute("searchThroughMethodBody", "true");
        final String[] expected = {
                "12: Method return null instead empty collection.",
                "15: Method return null instead empty collection.",
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
                "16: Method return null instead empty collection.",
                "24: Method return null instead empty collection.",
                "26: Method return null instead empty collection.",
                "38: Method return null instead empty collection.",
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
