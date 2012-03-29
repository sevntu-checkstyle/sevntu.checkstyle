package com.puppycrawl.tools.checkstyle.checks.design;

import java.io.File;

import org.junit.Test;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

/**
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil
 *         Yaroslavtsev</a>
 */
public class ChildBlockLengthTest extends BaseCheckTestSupport
{
    private final DefaultConfiguration checkConfig = createCheckConfig(ChildBlockLengthCheck.class);

    @Test
    public void testNPEonAllBlockTypes() throws Exception
    {
        checkConfig.addAttribute("maxChildBlockPercentage","100");
        checkConfig.addAttribute("blockTypes", "LITERAL_IF, LITERAL_SWITCH, LITERAL_FOR, " +
        		"LITERAL_DO, LITERAL_WHILE, LITERAL_TRY, LITERAL_ELSE, LITERAL_CATCH");
        
        String[] expected = {
        };

        verify(checkConfig, getPath("design" + File.separator
                + "InputChildBlockLengthCheck.java"), expected);
    }
     
    
    @Test
    public void testNestedConditions() throws Exception
    {
        checkConfig.addAttribute("maxChildBlockPercentage","100");
        checkConfig.addAttribute("blockTypes", "LITERAL_IF, LITERAL_SWITCH, LITERAL_FOR, " +
                "LITERAL_DO, LITERAL_WHILE, LITERAL_TRY, LITERAL_ELSE, LITERAL_CATCH");        

        String[] expected = {       
        };

        verify(checkConfig, getPath("design" + File.separator
                + "InputChildBlockLengthCheckNested.java"), expected);
    }
    
    @Test
    public void testManyBadChildBlocks() throws Exception
    {
        checkConfig.addAttribute("maxChildBlockPercentage","20");
        checkConfig.addAttribute("blockTypes", "LITERAL_IF, LITERAL_SWITCH, LITERAL_FOR, " +
                "LITERAL_DO, LITERAL_WHILE, LITERAL_TRY, LITERAL_ELSE, LITERAL_CATCH");        
        checkConfig.addAttribute("ignoreBlockLinesCount","0");
        
        String[] expected = {
                "15:15: " + getMessage("5"),  // 5.2%      
        };

        verify(checkConfig, getPath("design" + File.separator
                + "InputChildBlockLengthCheckManyBlocksOnOneScope.java"), expected);
    }
    
    @Test
    public void testManyBadChildBlocks2() throws Exception
    {
        checkConfig.addAttribute("maxChildBlockPercentage","19");
        checkConfig.addAttribute("blockTypes", "LITERAL_IF, LITERAL_SWITCH, LITERAL_FOR, " +
                "LITERAL_DO, LITERAL_WHILE, LITERAL_TRY, LITERAL_ELSE, LITERAL_CATCH");        
        checkConfig.addAttribute("ignoreBlockLinesCount","0");
        
        String[] expected = {
                "15:15: " + getMessage("4"),
                "31:15: " + getMessage("4"),         
        };

        verify(checkConfig, getPath("design" + File.separator
                + "InputChildBlockLengthCheckManyBlocksOnOneScope.java"), expected);
    }
    
    @Test
    public void testBadChildBlocksThatAreDoubleNested() throws Exception
    {
        checkConfig.addAttribute("maxChildBlockPercentage","70");
        checkConfig.addAttribute("blockTypes", "LITERAL_IF, LITERAL_SWITCH, LITERAL_FOR, " +
                "LITERAL_DO, LITERAL_WHILE, LITERAL_TRY, LITERAL_ELSE, LITERAL_CATCH");        
        checkConfig.addAttribute("ignoreBlockLinesCount","0");
        
        String[] expected = {  
                "41:7: " + getMessage("6"),  
                "42:9: " + getMessage("4"), 
        };

        verify(checkConfig, getPath("design" + File.separator
                + "InputChildBlockLengthCheckDoubleNested.java"), expected);
    }

    @Test
    public void testIgnoreBlockLinesCountOption() throws Exception
    {
        checkConfig.addAttribute("maxChildBlockPercentage","19");
        checkConfig.addAttribute("blockTypes", "LITERAL_IF, LITERAL_SWITCH, LITERAL_FOR, " +
                "LITERAL_DO, LITERAL_WHILE, LITERAL_TRY, LITERAL_ELSE, LITERAL_CATCH");             
        checkConfig.addAttribute("ignoreBlockLinesCount","26");

        String[] expected = {
//                "15:15: " + getMessage("4"),
//                "31:15: " + getMessage("4"),
        };

        verify(checkConfig, getPath("design" + File.separator
                + "InputChildBlockLengthCheckManyBlocksOnOneScope.java"), expected);
    }
    
    
    @Test
    public void testBadChildBlocksThatAreDoubleNested2() throws Exception
    {
        checkConfig.addAttribute("maxChildBlockPercentage","70");
        checkConfig.addAttribute("blockTypes", "LITERAL_IF, LITERAL_SWITCH, LITERAL_FOR, " +
                "LITERAL_DO, LITERAL_WHILE, LITERAL_TRY, LITERAL_ELSE, LITERAL_CATCH");        

        String[] expected = {  
        };

        verify(checkConfig, getPath("design" + File.separator
                + "InputChildBlockLengthCheckCheckNPE.java"), expected);
    }

    private String getMessage(String linesCount)
    {
        return "Block length should be lesser or equal to " + linesCount + " lines.";
    }

}
