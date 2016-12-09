package com.github.sevntu.checkstyle.checks.coding;
//
//import java.io.File;
//import org.junit.Test;
//import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
//import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
//
//import com.puppycrawl.tools.checkstyle.api.Check; // forbidden
//import com.puppycrawl.tools.checkstyle.api.DetailAST; // forbidden
//import com.puppycrawl.tools.checkstyle.api.TokenTypes; // forbidden

/**
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil
 *         Yaroslavtsev</a>
 */
public class InputForbidCertainImportsCheckDefaultPackageWithoutImports extends Check
{

    
    public int a()
    {
        return 5;
    }

}
