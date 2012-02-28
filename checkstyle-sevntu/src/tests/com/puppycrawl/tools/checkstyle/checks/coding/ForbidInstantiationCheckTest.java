package com.puppycrawl.tools.checkstyle.checks.coding;

import java.io.File;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

/**
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil
 *         Yaroslavtsev</a>
 */
public class ForbidInstantiationCheckTest extends BaseCheckTestSupport
{
    private final DefaultConfiguration checkConfig = createCheckConfig(ForbidInstantiationCheck.class);

    @Test
    public void testNormalWork() throws Exception
    {
        
        checkConfig.addAttribute("forbidExceptionClasses", "NullPointerException , InputForbidInstantiationCheck");

        String[] expected = {
                "6:35: " + getMessage("NullPointerException"),
                "8:9: " + getMessage("InputForbidInstantiationCheck")
        };

        verify(checkConfig, getPath("coding" + File.separator
                + "InputForbidInstantiationCheck.java"), expected);
    }

    private String getMessage(String className)
    {
        return "Instantiation of '" + className + "' is not allowed.";
    }
    
}
