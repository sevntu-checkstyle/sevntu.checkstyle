package com.puppycrawl.tools.checkstyle.checks.design;

import java.io.File;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

/**
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil
 *         Yaroslavtsev</a>
 */
public class CauseParameterInExceptionCheckTest extends BaseCheckTestSupport
{
    private final DefaultConfiguration checkConfig = createCheckConfig(CauseParameterInExceptionCheck.class);

    @Test
    public void testNormalWork() throws Exception
    {
        checkConfig.addAttribute("classNamesRegexp", ".+Exception");
        checkConfig.addAttribute("ignoredClassNamesRegexp", null);        

        String[] expected = {
                "3:1: " + getMessage("InputCauseParameterInException"),
                //"22:5: " + getMessage("MyException"),                
        };

        verify(checkConfig, getPath("design" + File.separator
                + "InputCauseParameterInException.java"), expected);
    }
    
    private String getMessage(String className)
    {
        return "'"
                + className
                + "' class should have a constructor with exception cause as parameter.";
    }
    
}
