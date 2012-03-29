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
    public void testNullPointerException() throws Exception
    {
        
        checkConfig.addAttribute("forbiddenClasses", "java.lang.NullPointerException");

        String[] expected = {
                 "8:35: " + getMessage("NullPointerException"),
                 "11:36: " + getMessage("NullPointerException"),
        };

        verify(checkConfig, getPath("coding" + File.separator
                + "InputForbidInstantiationCheck.java"), expected);
    }

    @Test
    public void testNormalWork() throws Exception
    {
        
        checkConfig.addAttribute("forbiddenClasses", "java.io.File , java.lang.String , ");

        String[] expected = {
                 "13:21: " + getMessage("File"),
                 "14:20: " + getMessage("String"),
        };

        verify(checkConfig, getPath("coding" + File.separator
                + "InputForbidInstantiationCheck.java"), expected);
    }
    
    private String getMessage(String className)
    {
        return "Instantiation of '" + className + "' is not allowed.";
    }


}
