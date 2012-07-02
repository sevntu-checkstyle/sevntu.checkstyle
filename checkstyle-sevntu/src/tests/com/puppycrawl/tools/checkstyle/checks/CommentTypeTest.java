package com.puppycrawl.tools.checkstyle.checks;

import junit.framework.Assert;
import org.junit.Test;

import com.puppycrawl.tools.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import com.puppycrawl.tools.checkstyle.checks.CommentTypeCheck;

public class CommentTypeTest extends BaseCheckTestSupport
{
    /**
     * Test that Comment Type Check works correctly.
     * @author Dmitry Gridyushko <sentinel1992@mail.ru>
     *
     */
    @Test
    public void testSingleLine()
            throws Exception
    {
        final DefaultConfiguration checkConfig =
                createCheckConfig(CommentTypeCheck.class);
        checkConfig.addAttribute("allowSingleLineComment", "true");

        final String[] expected = {
                "7: This comment type not allowed. Use '//' (Only first appearance is reported)",

        };
        verify(checkConfig, getPath("InputCommentTypeCheck.java"), expected);

    }
    
    @Test
    public void testBlock()
            throws Exception
    {
        final DefaultConfiguration checkConfig =
                createCheckConfig(CommentTypeCheck.class);
        checkConfig.addAttribute("allowSingleLineComment", "false");
        
        final String[] expected = {
                "5: This comment type not allowed. Use '/*' (Only first appearance is reported)",
                
        };
        verify(checkConfig, getPath("InputCommentTypeCheck.java"), expected);
    }
}
