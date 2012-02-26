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
    public void testSimple() throws Exception
    {
        checkConfig.addAttribute("packageNameRegexp", ".+\\.old\\..+");
        checkConfig.addAttribute("forbiddenPackageNameRegexp", ".+\\.api\\..+");

        String[] expected = {
                "4: "+ getMessage(),
                "10: "+ getMessage(),
                "11: "+ getMessage(),
                "12: "+ getMessage(),                
        };

        verify(checkConfig, getPath("coding" + File.separator
                + "InputForbidsCertainImports.java"), expected);
    }

    @Test
    public void testEmptyImportsAndDefaultPackage() throws Exception
    {
        checkConfig.addAttribute("packageNameRegexp", ".+\\.old\\..+");
        checkConfig.addAttribute("forbiddenPackageNameRegexp", ".+\\.api\\..+");

        String[] expected = {            
        };

        verify(checkConfig, getPath("coding" + File.separator
                + "InputForbidCertainImportsDefaultPackageWithoutImports.java"), expected);
    }
    
    @Test
    public void testEmptyImportsAndDefaultPackageWithoutParams() throws Exception
    {
        checkConfig.addAttribute("packageNameRegexp", "");
        checkConfig.addAttribute("forbiddenPackageNameRegexp", "");

        String[] expected = {
        };

        verify(checkConfig, getPath("coding" + File.separator
                + "InputForbidCertainImportsDefaultPackageWithoutImports.java"), expected);
    }

    private String getMessage() {
        return "Use of this import is forbidden in this package.";
    }
    
    
}
