package com.github.sevntu.checkstyle.checks.design;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import static com.github.sevntu.checkstyle.checks.design.AvoidConditionInversionCheck.MSG_KEY;
/**
 * 
 * @author <a href="mailto:nesterenko-aleksey@list.ru">Aleksey Nesterenko</a>
 *
 */
public class AvoidConditionInversionTest extends BaseCheckTestSupport {
	final DefaultConfiguration checkConfig =
            createCheckConfig(AvoidConditionInversionCheck.class);
	
	@Test
    public void defaultTest() throws Exception {
		
        final String[] expected = {
        		"7: " + getCheckMessage(MSG_KEY),
        		"11: " + getCheckMessage(MSG_KEY),
        		"15: " + getCheckMessage(MSG_KEY),
        		"19: " + getCheckMessage(MSG_KEY),
        		"23: " + getCheckMessage(MSG_KEY),
        		"29: " + getCheckMessage(MSG_KEY),
        		"31: " + getCheckMessage(MSG_KEY),
        		"39: " + getCheckMessage(MSG_KEY),
        		"102: " + getCheckMessage(MSG_KEY),
        		"107: " + getCheckMessage(MSG_KEY)
        };
        
        verify(checkConfig, getPath("InputAvoidConditionInversion.java"),
                expected);
    }
	
	@Test
	public void avoidOnlyRelationalOperandsInCondition() throws Exception {
		
		boolean applyOnlyToMathematicalOperands = true;
		checkConfig.addAttribute("applyOnlyToRelationalOperands",
				Boolean.toString(applyOnlyToMathematicalOperands));
		
		final String[] expected = {
				"7: " + getCheckMessage(MSG_KEY),
        		"11: " + getCheckMessage(MSG_KEY),
        		"15: " + getCheckMessage(MSG_KEY),
        		"19: " + getCheckMessage(MSG_KEY),
        		"23: " + getCheckMessage(MSG_KEY),
        		"29: " + getCheckMessage(MSG_KEY),
        		"31: " + getCheckMessage(MSG_KEY),
        };
		
		verify(checkConfig, getPath("InputAvoidConditionInversion.java"),
                expected);
	}
	
}
