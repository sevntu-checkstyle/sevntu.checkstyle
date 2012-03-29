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
        checkConfig.addAttribute("allowedCauseTypes", "Throwable, Exception");

        String[] expected = {
                "3:1: " + getMessage("InputCauseParameterInException"),
                //"22:5: " + getMessage("MyException"),                
        };

        verify(checkConfig, getPath("design" + File.separator
                + "InputCauseParameterInException.java"), expected);
    }
    
    @Test
    public void testNormalWork2() throws Exception
    {
        checkConfig.addAttribute("classNamesRegexp", ".+Exception2");
        checkConfig.addAttribute("ignoredClassNamesRegexp", null);        
        checkConfig.addAttribute("allowedCauseTypes", "Throwable, Exception");
        
        String[] expected = {
                "3:1: " + getMessage("InputCauseParameterInException2"),
                "22:5: " + getMessage("MyException2"),                
        };

        verify(checkConfig, getPath("design" + File.separator
                + "InputCauseParameterInException2.java"), expected);
    }
    
    @Test
    public void testIgnorePattern() throws Exception
    {
        checkConfig.addAttribute("classNamesRegexp", ".+Exception2");
        checkConfig.addAttribute("ignoredClassNamesRegexp", "Input.+");  
        checkConfig.addAttribute("allowedCauseTypes", "Throwable, Exception");     

        String[] expected = {
                //"3:1: " + getMessage("InputCauseParameterInException2"),
                "22:5: " + getMessage("MyException2"),                
        };

        verify(checkConfig, getPath("design" + File.separator
                + "InputCauseParameterInException2.java"), expected);
    }
    
    @Test
    public void testIgnorePattern2() throws Exception
    {
        checkConfig.addAttribute("classNamesRegexp", ".+Exception2");
        checkConfig.addAttribute("ignoredClassNamesRegexp", "My.+");
        checkConfig.addAttribute("allowedCauseTypes", "Throwable, Exception");

        String[] expected = {
                "3:1: " + getMessage("InputCauseParameterInException2"),
                //"22:5: " + getMessage("MyException"),                
        };

        verify(checkConfig, getPath("design" + File.separator
                + "InputCauseParameterInException2.java"), expected);
    }
    
    @Test
    public void testStrangeSituation() throws Exception
    {
        checkConfig.addAttribute("classNamesRegexp", ".+Exception");
        checkConfig.addAttribute("ignoredClassNamesRegexp", "");
        checkConfig.addAttribute("allowedCauseTypes", "Throwable, Exception");

        String[] expected = {
                //"3:1: " + getMessage("InputCauseParameterInException2"),
                //"22:5: " + getMessage("MyException"),                
        };

        verify(checkConfig, getPath("design" + File.separator
                + "InputCauseParameterInException3.java"), expected);
    }
    
    @Test
    public void testStrangeSituation2() throws Exception
    {
        checkConfig.addAttribute("classNamesRegexp", null);
        checkConfig.addAttribute("ignoredClassNamesRegexp", null);
        checkConfig.addAttribute("allowedCauseTypes", "Throwable, Exception");

        String[] expected = {
                //"3:1: " + getMessage("InputCauseParameterInException2"),
                //"22:5: " + getMessage("MyException"),                
        };

        verify(checkConfig, getPath("design" + File.separator
                + "InputCauseParameterInException3.java"), expected);
    }
    
    @Test
    public void testStrangeSituation3() throws Exception
    {
        checkConfig.addAttribute("classNamesRegexp", null);
        checkConfig.addAttribute("ignoredClassNamesRegexp", null);
        checkConfig.addAttribute("allowedCauseTypes", "Throwable, Exception");

        String[] expected = {
                //"3:1: " + getMessage("InputCauseParameterInException2"),
                //"22:5: " + getMessage("MyException"),                
        };

        verify(checkConfig, getPath("design" + File.separator
                + "InputCauseParameterInException4.java"), expected);
    }
    
    private String getMessage(String className)
    {
        return "'"
                + className
                + "' class should have a constructor with exception cause as parameter.";
    }
    
}
