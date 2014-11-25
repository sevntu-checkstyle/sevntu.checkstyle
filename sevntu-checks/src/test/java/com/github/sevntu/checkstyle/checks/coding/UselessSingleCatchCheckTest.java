package com.github.sevntu.checkstyle.checks.coding;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

import static com.github.sevntu.checkstyle.checks.coding.UselessSingleCatchCheck.*;

public class UselessSingleCatchCheckTest extends BaseCheckTestSupport
{
    private final String warningMessage =getCheckMessage(MSG_KEY);
    
    private final DefaultConfiguration config = createCheckConfig(UselessSingleCatchCheck.class);
    
    @Test
    public void testMultiCatch() throws Exception
    {        
        final String expected[] = { };
        
        verify(config, getPath("InputUselessSingleCatchCheck1.java"), expected);
    }
    
    @Test
    public void testWrappingCatch() throws Exception
    {
        final String expected[] = { };
        
        verify(config, getPath("InputUselessSingleCatchCheck2.java"), expected);
    }
    
    @Test
    public void testLoggingCatch() throws Exception
    {
        final String expected[] = { };
        
        verify(config, getPath("InputUselessSingleCatchCheck3.java"), expected);
    }
    
    @Test
    public void testThrowsAnotherException() throws Exception
    {
        final String expected[] = { };
        
        verify(config, getPath("InputUselessSingleCatchCheck4.java"), expected);
    }
    
    @Test
    public void testUselessCatch() throws Exception
    {
        final String expected[] = {
                "15:9: " + warningMessage
        };
        
        verify(config, getPath("InputUselessSingleCatchCheck5.java"), expected);
    }
}
