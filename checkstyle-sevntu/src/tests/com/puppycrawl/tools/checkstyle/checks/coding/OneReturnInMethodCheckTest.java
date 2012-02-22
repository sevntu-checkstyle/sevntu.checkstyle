package com.puppycrawl.tools.checkstyle.checks.coding;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class OneReturnInMethodCheckTest extends BaseCheckTestSupport
{

    private final DefaultConfiguration checkConfig = createCheckConfig(OneReturnInMethodCheck.class);

    @Before
    public void setTestinputsDir()
    {

    }

    @Test
    public void test() throws Exception
    {        
    String[] expected = {"11:5: " + createMsg("oneReturnInMethod"),
            
            };

            verify(checkConfig, getPath("coding" + File.separator
                    + "InputOneReturnInMethodCheck.java"), expected);
    }

    
    private String createMsg(String methodName)
    {        
        return "Method '"+methodName+"' should have only one 'return' operator.";
    }
    
}
