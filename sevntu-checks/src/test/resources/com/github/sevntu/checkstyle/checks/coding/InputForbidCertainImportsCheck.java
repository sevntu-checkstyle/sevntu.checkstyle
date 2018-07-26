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
public class InputForbidCertainImportsCheck extends Check
{
    
    public int a()
    {
        
        AutomaticBean smth = new com.puppycrawl.tools.checkstyle.api.AutomaticBean() { // forbidden!
            @Override
            protected void finishLocalSetup() {
            }
        };
        Number test = new Integer(0);
        return 5;
    }

	public int[] getDefaultTokens()
	{
		return null;
	}

}
