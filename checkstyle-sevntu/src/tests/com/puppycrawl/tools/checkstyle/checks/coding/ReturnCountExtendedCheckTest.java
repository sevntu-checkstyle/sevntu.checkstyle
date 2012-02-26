package com.puppycrawl.tools.checkstyle.checks.coding;

import java.io.File;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class ReturnCountExtendedCheckTest extends BaseCheckTestSupport
{

    private final DefaultConfiguration checkConfig = createCheckConfig(ReturnCountExtendedCheck.class);

    @Test
    public void testMethodsMaxReturnLiteralsIsOne() throws Exception
    {

        checkConfig.addAttribute("maxReturnCount", "1");
        checkConfig.addAttribute("linesLimit", "0"); // swithed off
        checkConfig.addAttribute("returnDepthLimit", "5");
        checkConfig.addAttribute("ignoreEmptyReturns", "false");
        checkConfig.addAttribute("rowsToIgnoreCount", "0");

    String[] expected = {
            "25:16: " + createMsg("twoReturnsInMethod","method", 2, 1),
            "37:16: " + createMsg("threeReturnsInMethod", "method", 3, 1),
            "57:16: " + createMsg("fourReturnsInMethod", "method", 4, 1),
            };

            verify(checkConfig, getPath("coding" + File.separator
                    + "InputReturnCountExtendedCheckMethods.java"), expected);
    }

    @Test
    public void testConstructorsMaxReturnLiteralsIsOne() throws Exception
    {

        checkConfig.addAttribute("maxReturnCount", "1");
        checkConfig.addAttribute("linesLimit", "0");
        checkConfig.addAttribute("returnDepthLimit", "5");
        checkConfig.addAttribute("ignoreEmptyReturns", "false");
        checkConfig.addAttribute("rowsToIgnoreCount", "0");

    String[] expected = {
            "28:12: " + createMsg("InputReturnCountExtendedCheckCtors", "constructor", 2, 1),
            "41:12: " + createMsg("InputReturnCountExtendedCheckCtors", "constructor", 3, 1),
            "63:12: " + createMsg("InputReturnCountExtendedCheckCtors", "constructor", 4, 1),
            };

            verify(checkConfig, getPath("coding" + File.separator
                    + "InputReturnCountExtendedCheckCtors.java"), expected);
    }

    @Test
    public void testLinesLimit() throws Exception
    {

        checkConfig.addAttribute("maxReturnCount", "1");
        checkConfig.addAttribute("linesLimit", "10");
        checkConfig.addAttribute("returnDepthLimit", "5");
        checkConfig.addAttribute("ignoreEmptyReturns", "false");
        checkConfig.addAttribute("rowsToIgnoreCount", "0");

    String[] expected = {
           // "25:16: " + createMsg("twoReturnsInMethod","method", 2, 1),
            "37:16: " + createMsg("threeReturnsInMethod", "method", 3, 1),
            "57:16: " + createMsg("fourReturnsInMethod", "method", 4, 1),
            };

            verify(checkConfig, getPath("coding" + File.separator
                    + "InputReturnCountExtendedCheckMethods.java"), expected);
    }

    @Test
    public void testLinesLimit2() throws Exception
    {              
        
        checkConfig.addAttribute("maxReturnCount", "1");
        checkConfig.addAttribute("linesLimit", "20");
        checkConfig.addAttribute("returnDepthLimit", "5");
        checkConfig.addAttribute("ignoreEmptyReturns", "false");
        checkConfig.addAttribute("rowsToIgnoreCount", "0");

    String[] expected = {
           // "25:16: " + createMsg("twoReturnsInMethod","method", 2, 1),
           // "37:16: " + createMsg("threeReturnsInMethod", "method", 3, 1),
            "57:16: " + createMsg("fourReturnsInMethod", "method", 4, 1), 
            };

            verify(checkConfig, getPath("coding" + File.separator
                    + "InputReturnCountExtendedCheckMethods.java"), expected);
    }

    @Test
    public void testReturnDepthLimit() throws Exception
    {              
        
        checkConfig.addAttribute("maxReturnCount", "0");  
        checkConfig.addAttribute("linesLimit", "0");
        checkConfig.addAttribute("returnDepthLimit", "1");
        checkConfig.addAttribute("ignoreEmptyReturns", "false");
        checkConfig.addAttribute("rowsToIgnoreCount", "0");

    String[] expected = {
            "6:16: " + createMsg("oneReturnInMethod","method", 1, 0),
            "10:16: " + createMsg("oneReturnInMethod2","method", 1, 0),
            //"25:5: " + createMsg("twoReturnsInMethod","method", 2, 0),
            //"42: " + createMsg("threeReturnsInMethod", "method", 1, 0),
            //"67: " + createMsg("fourReturnsInMethod", "method", 1, 0), 
            };

            verify(checkConfig, getPath("coding" + File.separator
                    + "InputReturnCountExtendedCheckMethods.java"), expected);
    }
    
    @Test
    public void testIgnoreEmptyReturns() throws Exception
    {              
        checkConfig.addAttribute("maxReturnCount", "1");
        checkConfig.addAttribute("linesLimit", "0");
        checkConfig.addAttribute("returnDepthLimit", "5");
        checkConfig.addAttribute("ignoreEmptyReturns", "true");
        checkConfig.addAttribute("rowsToIgnoreCount", "0");

    String[] expected = {
            "28:12: " + createMsg("InputReturnCountExtendedCheckCtors", "constructor", 2, 1),
            "41:12: " + createMsg("InputReturnCountExtendedCheckCtors", "constructor", 3, 1),
            "63:12: " + createMsg("InputReturnCountExtendedCheckCtors", "constructor", 3, 1),
            };

            verify(checkConfig, getPath("coding" + File.separator
                    + "InputReturnCountExtendedCheckCtors.java"), expected);
    }

    @Test
    public void testMethodsInMethods() throws Exception
    {              
        checkConfig.addAttribute("maxReturnCount", "1");
        checkConfig.addAttribute("linesLimit", "0");
        checkConfig.addAttribute("returnDepthLimit", "5");
        checkConfig.addAttribute("ignoreEmptyReturns", "false");
        checkConfig.addAttribute("rowsToIgnoreCount", "0");

    String[] expected = {
            "100:24: " + createMsg("handleEvent", "method", 3, 1), 
            };

            verify(checkConfig, getPath("coding" + File.separator
                    + "InputReturnCountExtendedCheckMethodsInMethods.java"), expected);
    }
    
    private String createMsg(String methodName, String methodType, int is, int max)
    {
        return "Return count for '"+methodName+"' "+methodType+" is "+is+" (max allowed is "+max+").";
    }
    
}
