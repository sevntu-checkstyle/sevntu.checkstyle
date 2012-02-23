package com.puppycrawl.tools.checkstyle.checks.coding;

import java.io.File;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class ReturnDepthCheckTest extends BaseCheckTestSupport
{

    private final DefaultConfiguration checkConfig = createCheckConfig(ReturnDepthCheck.class);

    @Test
    public void testReturnDepthLimit()
            throws Exception
    {

        int maxAllowed = 1;

        checkConfig.addAttribute("returnDepthLimit", maxAllowed + "");

        String[] expected = {
                "29:13: "
                        + createMsg("twoReturnsInMethod", "method", 2,
                                maxAllowed),
                "48:17: "
                        + createMsg("threeReturnsInMethod", "method", 3,
                                maxAllowed),
                "62:17: "
                        + createMsg("fourReturnsInMethod", "method", 3,
                                maxAllowed),
        };

        verify(checkConfig, getPath("coding" + File.separator
                + "InputReturnDepthCheckMethods.java"), expected);
    }

    @Test
    public void testReturnDepthLimit2()
            throws Exception
    {

        int maxAllowed = 2;

        checkConfig.addAttribute("returnDepthLimit", maxAllowed + "");

        String[] expected = {
                //"29:13: " + createMsg("twoReturnsInMethod", "method", 2, maxAllowed),
                "48:17: "
                        + createMsg("threeReturnsInMethod", "method", 3,
                                maxAllowed),
                "62:17: "
                        + createMsg("fourReturnsInMethod", "method", 3,
                                maxAllowed),
        };

        verify(checkConfig, getPath("coding" + File.separator
                + "InputReturnDepthCheckMethods.java"), expected);
    }

    @Test
    public void testReturnDepthLimit3()
            throws Exception
    {

        int maxAllowed = 3;

        checkConfig.addAttribute("returnDepthLimit", maxAllowed + "");

        String[] expected = {
                };

        verify(checkConfig, getPath("coding" + File.separator
                + "InputReturnDepthCheckMethods.java"), expected);
    }

    private String
            createMsg(String methodName, String methodType, int is, int max)
    {
        return "Return depth is " + is + " (max allowed is " + max + ").";
    }

}
