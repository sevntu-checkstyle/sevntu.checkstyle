package com.puppycrawl.tools.checkstyle.checks.coding;

import java.io.File;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class ReturnCountExtendedCheckTest extends BaseCheckTestSupport
{

    private final DefaultConfiguration checkConfig = createCheckConfig(ReturnCountExtendedCheck.class);

//    @Test
//    public void testMethodsMaxReturnLiteralsIsOne() throws Exception
//    {
//
//        checkConfig.addAttribute("max", "1");
//        checkConfig.addAttribute("linesLimit", "0"); // swithed off
//        checkConfig.addAttribute("returnDepthLimit", "5");
//        checkConfig.addAttribute("ignoreEmptyReturns", "false");
//
//    String[] expected = {
//            "33: " + createMsg("twoReturnsInMethod","method", 2, 1),
//            "52: " + createMsg("threeReturnsInMethod", "method", 3, 1),
//            "76: " + createMsg("fourReturnsInMethod", "method", 4, 1),
//            };
//
//            verify(checkConfig, getPath("coding" + File.separator
//                    + "InputReturnCountExtendedCheckMethods.java"), expected);
//    }
//
//    @Test
//    public void testConstructorsMaxReturnLiteralsIsOne() throws Exception
//    {
//
//        checkConfig.addAttribute("max", "1");
//        checkConfig.addAttribute("linesLimit", "0");
//        checkConfig.addAttribute("returnDepthLimit", "5");
//        checkConfig.addAttribute("ignoreEmptyReturns", "false");
//
//    String[] expected = {
//            "36: " + createMsg("InputReturnCountExtendedCheckCtors", "constructor", 2, 1),
//            "57: " + createMsg("InputReturnCountExtendedCheckCtors", "constructor", 3, 1),
//            "82: " + createMsg("InputReturnCountExtendedCheckCtors", "constructor", 4, 1),
//            };
//
//            verify(checkConfig, getPath("coding" + File.separator
//                    + "InputReturnCountExtendedCheckCtors.java"), expected);
//    }
//
//    @Test
//    public void testLinesLimit() throws Exception
//    {
//
//        checkConfig.addAttribute("max", "1");
//        checkConfig.addAttribute("linesLimit", "10");
//        checkConfig.addAttribute("returnDepthLimit", "5");
//        checkConfig.addAttribute("ignoreEmptyReturns", "false");
//
//    String[] expected = {
//           // "33: " + createMsg("twoReturnsInMethod","method", 2, 1),
//            "52: " + createMsg("threeReturnsInMethod", "method", 3, 1),
//            "76: " + createMsg("fourReturnsInMethod", "method", 4, 1),
//            };
//
//            verify(checkConfig, getPath("coding" + File.separator
//                    + "InputReturnCountExtendedCheckMethods.java"), expected);
//    }
//
//    @Test
//    public void testLinesLimit2() throws Exception
//    {              
//        
//        checkConfig.addAttribute("max", "1");
//        checkConfig.addAttribute("linesLimit", "20");
//        checkConfig.addAttribute("returnDepthLimit", "5");
//        checkConfig.addAttribute("ignoreEmptyReturns", "false");
//
//    String[] expected = {
//           // "33: " + createMsg("twoReturnsInMethod","method", 2, 1),
//           // "52: " + createMsg("threeReturnsInMethod", "method", 3, 1),
//            "76: " + createMsg("fourReturnsInMethod", "method", 4, 1), 
//            };
//
//            verify(checkConfig, getPath("coding" + File.separator
//                    + "InputReturnCountExtendedCheckMethods.java"), expected);
//    }
//
//    @Test
//    public void testReturnDepthLimit() throws Exception
//    {              
//        
//        checkConfig.addAttribute("max", "0");  
//        checkConfig.addAttribute("linesLimit", "0");
//        checkConfig.addAttribute("returnDepthLimit", "1");
//        checkConfig.addAttribute("ignoreEmptyReturns", "false");
//
//    String[] expected = {
//            "7: " + createMsg("oneReturnInMethod","method", 1, 0),
//            "22: " + createMsg("oneReturnInMethod2","method", 1, 0),
//            //"33: " + createMsg("twoReturnsInMethod","method", 2, 0),
//            //"42: " + createMsg("threeReturnsInMethod", "method", 1, 0),
//            //"67: " + createMsg("fourReturnsInMethod", "method", 1, 0), 
//            };
//
//            verify(checkConfig, getPath("coding" + File.separator
//                    + "InputReturnCountExtendedCheckMethods.java"), expected);
//    }
    
    @Test
    public void testIgnoreEmptyReturns() throws Exception
    {              
        
        checkConfig.addAttribute("max", "1");  
        checkConfig.addAttribute("linesLimit", "0");
        checkConfig.addAttribute("returnDepthLimit", "5");
        checkConfig.addAttribute("ignoreEmptyReturns", "true");

    String[] expected = {
            "28: " + createMsg("InputReturnDepthCheck","constructor", 6, 1),
            "53: " + createMsg("nm","method", 3, 1),
            };

            verify(checkConfig, getPath("coding" + File.separator
                    + "InputReturnDepthCheck.java"), expected);
    }
    
    private String createMsg(String methodName, String methodType, int is, int max)
    {
        return "Return count for '"+methodName+"' "+methodType+" is "+is+" (max allowed is "+max+").";
    }
    
}
