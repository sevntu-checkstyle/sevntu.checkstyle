package com.github.sevntu.checkstyle.checks.coding;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;
import org.junit.Test;

public class AvoidConstantAsFirstOperandInConditionCheckTest extends BaseCheckTestSupport {

    @Test
    public void testAll() throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(AvoidConstantAsFirstOperandInConditionCheck.class);
        final String[] expected = {
                createErrorMessage(24, "=="),
                createErrorMessage(25, "=="),
                createErrorMessage(27, "=="),
                createErrorMessage(28, "=="),
                createErrorMessage(29, "=="),
                createErrorMessage(30, "=="),
                createErrorMessage(31, "=="),

                createErrorMessage(46, "=="),
                createErrorMessage(47, "!="),
                createErrorMessage(52, "=="),
                createErrorMessage(53, "!="),
                createErrorMessage(58, "=="),
                createErrorMessage(59, "!="),
                createErrorMessage(67, "=="),
                createErrorMessage(71, "=="),
                createErrorMessage(72, "=="),
                createErrorMessage(73, "=="),
                createErrorMessage(74, "=="),
                createErrorMessage(76, "=="),
                createErrorMessage(77, "=="),
                createErrorMessage(78, "=="),
                createErrorMessage(84, "=="),
                createErrorMessage(85, "=="),
                createErrorMessage(86, "=="),

                createErrorMessage(97, "=="),
                createErrorMessage(101, "=="),
                createErrorMessage(111, "=="),
                createErrorMessage(112, "=="),
        };
        verify(checkConfig, getPath("InputAvoidConstantAsFirstOperandInConditionCheck.java"), expected);
    }

    @Test
    public void testAttributes() throws Exception {
        final DefaultConfiguration checkConfig = createCheckConfig(AvoidConstantAsFirstOperandInConditionCheck.class);
        checkConfig.addAttribute("targetConstantTypes", "LITERAL_FALSE,NUM_INT,NUM_FLOAT");
        final String[] expected = {
                createErrorMessage(25, "=="),
                createErrorMessage(28, "=="),
                createErrorMessage(31, "=="),
        };
        verify(checkConfig, getPath("InputAvoidConstantAsFirstOperandInConditionCheck.java"), expected);

    }

    /**
     * Create error message.
     *
     * @param lineNumber Line number.
     * @param operand    Operand.
     * @return String message
     */
    private String createErrorMessage(int lineNumber, String operand) {
        return lineNumber + ": Constant have to be second operand of '" + operand + "'.";
    }

}