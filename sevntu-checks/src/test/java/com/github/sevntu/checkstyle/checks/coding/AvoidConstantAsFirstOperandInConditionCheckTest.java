package com.github.sevntu.checkstyle.checks.coding;

import static com.github.sevntu.checkstyle.checks.coding.AvoidConstantAsFirstOperandInConditionCheck.*;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import org.junit.Test;

public class AvoidConstantAsFirstOperandInConditionCheckTest extends BaseCheckTestSupport {

    @Test
    public void testAll() throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(AvoidConstantAsFirstOperandInConditionCheck.class);
        final String[] expected = {
        		"24: " + getCheckMessage(MSG_KEY, "=="),
        		"25: " + getCheckMessage(MSG_KEY, "=="),
        		"27: " + getCheckMessage(MSG_KEY, "=="),
        		"28: " + getCheckMessage(MSG_KEY, "=="),
        		"29: " + getCheckMessage(MSG_KEY, "=="),
        		"30: " + getCheckMessage(MSG_KEY, "=="),
        		"31: " + getCheckMessage(MSG_KEY, "=="),

        		
        		"46: " + getCheckMessage(MSG_KEY, "=="),
        		"47: " + getCheckMessage(MSG_KEY, "!="),
        		"52: " + getCheckMessage(MSG_KEY, "=="),
        		"53: " + getCheckMessage(MSG_KEY, "!="),
        		"58: " + getCheckMessage(MSG_KEY, "=="),
        		"59: " + getCheckMessage(MSG_KEY, "!="),
        		"67: " + getCheckMessage(MSG_KEY, "=="),
        		"71: " + getCheckMessage(MSG_KEY, "=="),
        		"72: " + getCheckMessage(MSG_KEY, "=="),
        		"73: " + getCheckMessage(MSG_KEY, "=="),
        		"74: " + getCheckMessage(MSG_KEY, "=="),
        		"76: " + getCheckMessage(MSG_KEY, "=="),
        		"77: " + getCheckMessage(MSG_KEY, "=="),
        		"78: " + getCheckMessage(MSG_KEY, "=="),
        		"84: " + getCheckMessage(MSG_KEY, "=="),
        		"85: " + getCheckMessage(MSG_KEY, "=="),
        		"86: " + getCheckMessage(MSG_KEY, "=="),

        		"97: " + getCheckMessage(MSG_KEY, "=="),
        		"101: " + getCheckMessage(MSG_KEY, "=="),
        		"111: " + getCheckMessage(MSG_KEY, "=="),
        		"112: " + getCheckMessage(MSG_KEY, "=="),
        };
        verify(checkConfig, getPath("InputAvoidConstantAsFirstOperandInConditionCheck.java"), expected);
    }

    @Test
    public void testAttributes() throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(AvoidConstantAsFirstOperandInConditionCheck.class);
        checkConfig.addAttribute("targetConstantTypes", "LITERAL_FALSE,NUM_INT,NUM_FLOAT");
        final String[] expected = {
        		"25: " + getCheckMessage(MSG_KEY, "=="),
        		"28: " + getCheckMessage(MSG_KEY, "=="),
        		"31: " + getCheckMessage(MSG_KEY, "=="),
        };
        verify(checkConfig, getPath("InputAvoidConstantAsFirstOperandInConditionCheck.java"), expected);

    }
}