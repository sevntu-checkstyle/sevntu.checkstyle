package com.github.sevntu.checkstyle.checks.coding;

import com.puppycrawl.tools.checkstyle.api.AutomaticBean; // forbidden
import java.io.File;
import org.junit.Test;

import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

import com.puppycrawl.tools.checkstyle.api.AutomaticBean; // forbidden!

/**
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil
 *         Yaroslavtsev</a>
 */
public class InputForbidsCertainImports extends Check
{
    
    public int a()
    {
        
        AutomaticBean smth = new com.puppycrawl.tools.checkstyle.api.AutomaticBean(); // forbidden!
        return 5;
    }

	public int[] getDefaultTokens()
	{
		return null;
	}

}
