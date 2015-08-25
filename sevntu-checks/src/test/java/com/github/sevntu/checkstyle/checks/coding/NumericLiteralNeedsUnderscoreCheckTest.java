package com.github.sevntu.checkstyle.checks.coding;

import static com.github.sevntu.checkstyle.checks.coding.NumericLiteralNeedsUnderscoreCheck.MSG_KEY;

import org.junit.Test;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

public class NumericLiteralNeedsUnderscoreCheckTest extends BaseCheckTestSupport
{

    private final String warningMessage = getCheckMessage(MSG_KEY);

    @Test
    public void test()
            throws Exception
    {
        DefaultConfiguration checkConfig = createCheckConfig(NumericLiteralNeedsUnderscoreCheck.class);
        final String[] expected = {
                "27: " + warningMessage,
                "28: " + warningMessage,
                "29: " + warningMessage,
                "30: " + warningMessage,
                "31: " + warningMessage,
                "32: " + warningMessage,
                "33: " + warningMessage,
                "50: " + warningMessage,
                "51: " + warningMessage,
                "52: " + warningMessage,
                "53: " + warningMessage,
                "54: " + warningMessage,
                "55: " + warningMessage,
                "56: " + warningMessage,
                "57: " + warningMessage,
                "75: " + warningMessage,
                "76: " + warningMessage,
                "77: " + warningMessage,
                "78: " + warningMessage,
                "79: " + warningMessage,
                "80: " + warningMessage,
                "81: " + warningMessage,
                "82: " + warningMessage,
                "83: " + warningMessage,
                "84: " + warningMessage,
                "85: " + warningMessage,
                "99: " + warningMessage,
                "100: " + warningMessage,
                "101: " + warningMessage,
                "102: " + warningMessage,
                "103: " + warningMessage,
                "104: " + warningMessage,
        };
        verify(checkConfig, getPath("InputNumericLiteralNeedUnderscoreCheck.java"),
                expected);
    }

    @Test
    public void testWithConfig()
            throws Exception
    {
        DefaultConfiguration checkConfig = createCheckConfig(NumericLiteralNeedsUnderscoreCheck.class);
        checkConfig.addAttribute("minDecimalSymbolLen", "1");
        checkConfig.addAttribute("maxDecimalSymbolsUntilUnderscore", "3");
        checkConfig.addAttribute("minHexSymbolLen", "1");
        checkConfig.addAttribute("maxHexSymbolsUntilUnderscore", "2");
        checkConfig.addAttribute("minBinarySymbolLen", "1");
        checkConfig.addAttribute("maxBinarySymbolsUntilUnderscore", "4");
        final String[] expected = {
                "23: " + warningMessage,
                "24: " + warningMessage,
                "25: " + warningMessage,
                "26: " + warningMessage,
                "27: " + warningMessage,
                "28: " + warningMessage,
                "44: " + warningMessage,
                "45: " + warningMessage,
                "46: " + warningMessage,
                "47: " + warningMessage,
                "48: " + warningMessage,
                "49: " + warningMessage,
                "50: " + warningMessage,
                "67: " + warningMessage,
                "68: " + warningMessage,
                "69: " + warningMessage,
                "70: " + warningMessage,
                "71: " + warningMessage,
                "72: " + warningMessage,
                "73: " + warningMessage,
                "74: " + warningMessage,
                "86: " + warningMessage,
                "87: " + warningMessage,
                "88: " + warningMessage,
                "89: " + warningMessage,
        };
        verify(checkConfig, getPath("InputNumericLiteralNeedUnderscoreCheck2.java"),
                expected);
    }

}
