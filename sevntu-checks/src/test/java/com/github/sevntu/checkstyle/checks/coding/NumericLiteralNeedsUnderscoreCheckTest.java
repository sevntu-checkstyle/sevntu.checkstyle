package com.github.sevntu.checkstyle.checks.coding;

import static com.github.sevntu.checkstyle.checks.coding.NumericLiteralNeedsUnderscoreCheck.MSG_KEY;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.internal.WhiteboxImpl;

import com.github.sevntu.checkstyle.BaseCheckTestSupport;
import com.github.sevntu.checkstyle.checks.coding.NumericLiteralNeedsUnderscoreCheck.NumericType;
import com.puppycrawl.tools.checkstyle.DefaultConfiguration;

@RunWith(PowerMockRunner.class)
@PrepareForTest(NumericType.class)
public class NumericLiteralNeedsUnderscoreCheckTest extends BaseCheckTestSupport {

    private final String warningMessage = getCheckMessage(MSG_KEY);
    
    private static final String EXCEPTION_MESSAGE = "Unexpected numeric type "; 

    @Test
    public void test() throws Exception {
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
    public void testWithConfig() throws Exception {
        DefaultConfiguration checkConfig = createCheckConfig(NumericLiteralNeedsUnderscoreCheck.class);
        checkConfig.addAttribute("minDecimalSymbolLength", "1");
        checkConfig.addAttribute("maxDecimalSymbolsUntilUnderscore", "3");
        checkConfig.addAttribute("minHexSymbolLength", "1");
        checkConfig.addAttribute("maxHexSymbolsUntilUnderscore", "2");
        checkConfig.addAttribute("minBinarySymbolLength", "1");
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

    @Test
    public void testIgnore() throws Exception {
        final DefaultConfiguration checkConfig =
                createCheckConfig(NumericLiteralNeedsUnderscoreCheck.class);
        final String[] expected = {
                "7: " + warningMessage,
                "8: " + warningMessage,
                "10: " + warningMessage,
                "15: " + warningMessage,
                "16: " + warningMessage,
                "17: " + warningMessage,
                "22: " + warningMessage,
                "24: " + warningMessage,
                "30: " + warningMessage,
                "31: " + warningMessage,
                "32: " + warningMessage,
                "33: " + warningMessage,
                "40: " + warningMessage,
        };
        verify(checkConfig, new File("src/test/resources-noncompilable/com/github/sevntu/checkstyle/checks/"
                + "coding/InputNumericLiteralNeedUnderscoreCheck3.java").getCanonicalPath(),
                expected);
    }

    @Test
    public void testConfiguredIgnore() throws Exception {
        final DefaultConfiguration checkConfig =
                createCheckConfig(NumericLiteralNeedsUnderscoreCheck.class);
        checkConfig.addAttribute("ignoreFieldNamePattern", "RED");
        checkConfig.addAttribute("minDecimalSymbolLength", "1");
        final String[] expected = {
                "7: " + warningMessage,
                "8: " + warningMessage,
                "10: " + warningMessage,
                "14: " + warningMessage,
                "15: " + warningMessage,
                "17: " + warningMessage,
                "22: " + warningMessage,
                "24: " + warningMessage,
                "30: " + warningMessage,
                "31: " + warningMessage,
                "32: " + warningMessage,
                "33: " + warningMessage,
                "38: " + warningMessage,
        };
        verify(checkConfig, new File("src/test/resources-noncompilable/com/github/sevntu/checkstyle/checks/"
                + "coding/InputNumericLiteralNeedUnderscoreCheck3.java").getCanonicalPath(),
                expected);
    }

    @Test
    public void testMinSymbolsBeforeCheckingSwitchReflection() throws Exception {
        try {
            final NumericLiteralNeedsUnderscoreCheck check = new NumericLiteralNeedsUnderscoreCheck();
            final NumericType mockType = PowerMockito.mock(NumericType.class);
            WhiteboxImpl.invokeMethod(check, "minSymbolsBeforeChecking", mockType);
        } catch (IllegalStateException e) {
            Assert.assertTrue(e.getMessage().startsWith(EXCEPTION_MESSAGE));
        }
    }

    @Test
    public void testMaxSymbolsUntilUnderscoreSwitchReflection() throws Exception {
        try {
            final NumericLiteralNeedsUnderscoreCheck check = new NumericLiteralNeedsUnderscoreCheck();
            final NumericType mockType = PowerMockito.mock(NumericType.class);
            WhiteboxImpl.invokeMethod(check, "maxSymbolsUntilUnderscore", mockType);
        } catch (IllegalStateException e) {
            Assert.assertTrue(e.getMessage().startsWith(EXCEPTION_MESSAGE));
        }
    }

    @Test
    public void testGetNumericSegmentsSwitchReflection() throws Exception {
        try {
            final NumericLiteralNeedsUnderscoreCheck check = new NumericLiteralNeedsUnderscoreCheck();
            final NumericType mockType = PowerMockito.mock(NumericType.class);
            WhiteboxImpl.invokeMethod(check, "getNumericSegments", "", mockType);
        } catch (IllegalStateException e) {
            Assert.assertTrue(e.getMessage().startsWith(EXCEPTION_MESSAGE));
        }
    }

    @Test
    public void testRemovePrePostfixByTypeSwitchReflection() throws Exception {
        try {
            final NumericLiteralNeedsUnderscoreCheck check = new NumericLiteralNeedsUnderscoreCheck();
            final NumericType mockType = PowerMockito.mock(NumericType.class);
            WhiteboxImpl.invokeMethod(check, "removePrePostfixByType", "", mockType);
        } catch (IllegalStateException e) {
            Assert.assertTrue(e.getMessage().startsWith(EXCEPTION_MESSAGE));
        }
    }

}
