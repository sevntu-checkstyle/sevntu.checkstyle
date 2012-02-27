/**
 * 
 */
package com.puppycrawl.tools.checkstyle.checks.coding;

import java.io.File;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;


/**
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil
 *         Yaroslavtsev</a>
 */
public class ForbidCertainImportsCheckTest extends BaseCheckTestSupport
{

    private final DefaultConfiguration checkConfig = createCheckConfig(ForbidCertainImportsCheck.class);

    @Test
    public void testNormalWork() throws Exception
    {
        String importPattern = ".+\\.api\\..+";
        
        checkConfig.addAttribute("packageNameRegexp", ".+\\.old\\..+");
        checkConfig.addAttribute("forbiddenImportRegexp", importPattern);

        String[] expected = {
                "4: "+ getMessage(importPattern),
                "10: "+ getMessage(importPattern),
                "11: "+ getMessage(importPattern),
                "12: "+ getMessage(importPattern),                
        };

        verify(checkConfig, getPath("coding" + File.separator
                + "InputForbidsCertainImports.java"), expected);
    }

    @Test
    public void testEmptyImportsAndDefaultPackage() throws Exception
    {
        checkConfig.addAttribute("packageNameRegexp", ".+\\.old\\..+");
        checkConfig.addAttribute("forbiddenImportRegexp", ".+\\.api\\..+");

        String[] expected = {            
        };

        verify(checkConfig, getPath("coding" + File.separator
                + "InputForbidCertainImportsDefaultPackageWithoutImports.java"), expected);
    }
    
    @Test
    public void testEmptyImportsAndDefaultPackageWithoutParams() throws Exception
    {
        checkConfig.addAttribute("packageNameRegexp", "");
        checkConfig.addAttribute("forbiddenImportRegexp", "");

        String[] expected = {
        };

        verify(checkConfig, getPath("coding" + File.separator
                + "InputForbidCertainImportsDefaultPackageWithoutImports.java"), expected);
    }

    @Test
    public void testEmptyImportsAndDefaultPackageWithoutParams2() throws Exception
    {
        
//        checkConfig.addAttribute("packageNameRegexp", "");
//        checkConfig.addAttribute("forbiddenImportRegexp", "");

        String[] expected = {
        };

        verify(checkConfig, getPath("coding" + File.separator
                + "InputForbidCertainImportsDefaultPackageWithoutImports.java"), expected);
    }
    
    private String getMessage(String pattern) {
        return "This import should not match '"+pattern+"' pattern.";
    }

    
}
