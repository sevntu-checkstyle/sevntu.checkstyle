package com.github.sevntu.checkstyle.checks.coding;

import static com.github.sevntu.checkstyle.checks.coding.EitherLogOrThrowCheck.*;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

/**
 * Test for EitherLogOrThrowCheck.
 * @author <a href="mailto:barataliba@gmail.com">Baratali Izmailov</a>
 */
public class EitherLogOrThrowCheckTest extends BaseCheckTestSupport
{
	private final String warningMessage = getCheckMessage(MSG_KEY);
	
    @Test
    public void test() throws Exception
    {
        final DefaultConfiguration checkConfig =
                createCheckConfig(EitherLogOrThrowCheck.class);
        
        final String[] expected = {
        		"19: " + warningMessage,
        		"31: " + warningMessage,
        		"43: " + warningMessage,
        		"82: " + warningMessage,
        		"93: " + warningMessage,
        		"102: " + warningMessage,
        		"112: " + warningMessage,
        		"124: " + warningMessage,
        		"154: " + warningMessage,
        		"164: " + warningMessage,
        		"207: " + warningMessage,
        		"231: " + warningMessage,
        		"241: " + warningMessage,
        		"252: " + warningMessage,
        		"262: " + warningMessage,
        };
        verify(checkConfig, getPath("InputEitherLogOrThrowCheck.java"),
                expected);
    }
}
