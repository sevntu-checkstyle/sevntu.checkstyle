package com.github.sevntu.checkstyle.checks.coding;

import java.util.regex.Pattern;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * This check verifies that large numeric literals are spaced by underscores.
 * </p>
 * <p>
 * Java 7 allows for underscores to delimit numeric literals to enhance readability because very
 * large numeric literals are hard to read. For example:
 * </p>
 * <code>
 * long creditCardNumber = 1234_5678_1234_5678L;
 * </code>
 * <p>
 * is much easier to read than
 * </p>
 * <code>
 * long creditCardNumber = 1234567812345678L;
 * </code>
 * <p>
 * This check comes with the following parameters:
 * </p>
 * <p>
 * "minDecimalSymbolLength" - The minimum number of symbols in a decimal literal (includes int,
 * long, float, and double) before the check begins to look for underscores. Numeric literals with
 * delimiters like decimal points or exponentials will be split before checking the length. Default
 * is 7.
 * </p>
 * <p>
 * "maxDecimalSymbolsUntilUnderscore" - The maximum number of symbols in a decimal literal allowed
 * before the check demands an underscore. This does not take postfixes and delimiters like decimal
 * points and exponentials into account. Default is 3.
 * </p>
 * <p>
 * "minHexSymbolLength" - The minimum number of symbols in a hex literal before the check begins to
 * look for underscores. Numeric literals with delimiters like decimal points or exponentials will
 * be split before checking the length. Default is 5.
 * </p>
 * <p>
 * "maxHexSymbolsUntilUnderscore" - The maximum number of symbols in a hex literal allowed before
 * the check demands an underscore. This does not take the prefix 0x, delimiters like decimal points
 * and exponentials, and postfixes into account. Default is 4.
 * </p>
 * <p>
 * "minBinarySymbolLength" - The minimum number of symbols in a binary literal before the check
 * begins to look for underscores. Default is 9.
 * </p>
 * <p>
 * "maxBinarySymbolsUntilUnderscore" - The maximum number of symbols in a binary literal allowed
 * before the check demands an underscore. This does not take the prefix 0b and postfixes into
 * account. Default is 8.
 * </p>
 * <p>
 * Examples (assuming default parameters):
 * </p>
 *
 * <pre>
 * // Ignored because length of token is 6, which is less than minDecimalSymbolLength (7)
 * int ignoredDecimal = 123456;
 * // Ignored because the postfix L is not taken into account
 * long ignoredDecimal = 123456L;
 * // Ignored because each segment delimited by the decimal point has a length less than minDecimalSymbolLength (7)
 * float ignoredDecimal = 123456.123456f;
 * // Failed because token does not have underscores every 3 characters (maxDecimalSymbolsUntilUnderscore = 3)
 * int failingDecimal = 1234567;
 * double failingDecimal = 1.1234567e0d;
 * int passingDecimal = 1_234_567;
 * double passingDecimal = 123456.123456e0d;
 * double passingDecimal = 1.123_456_7e0d;
 * int ignoredHex = 0xFFFF;
 * int failingHex = 0xFFFFFFFF;
 * int passingHex = 0xFFFF_FFFF;
 * float passingHex = 0xAAAA.BBBBp1f;
 * int ignoredBinary = 0b01010101;
 * int failingBinary = 0b0000000000000000;
 * int passingBinary = 0b00000000_00000000;
 * </pre>
 * <p>
 * An example of how to configure parameters:
 * </p>
 *
 * <pre>
 * &lt;module name="NumericLiteralNeedsUnderscoreCheck"&gt;
 *    &lt;property name="minDecimalSymbolLength" value="4"/&gt;
 *    &lt;property name="maxDecimalSymbolsUntilUnderscore" value="3"/&gt;
 *    &lt;property name="minHexSymbolLength" value="3"/&gt;
 *    &lt;property name="maxHexSymbolsUntilUnderscore" value="2"/&gt;
 *    &lt;property name="minBinarySymbolLength" value="5"/&gt;
 *    &lt;property name="maxBinarySymbolsUntilUnderscore" value="4"/&gt;
 * &lt;/module&gt;
 * </pre>
 * <p>
 * Examples (assuming above parameters):
 * </p>
 *
 * <pre>
 * // Ignored because length of token is 3, which is less than minDecimalSymbolLength (4)
 * int ignoredDecimal = 123;
 * // Ignored because each segment delimited by the decimal point has a length less than minDecimalSymbolLength (4)
 * float ignoredDecimal = 123.123f;
 * // Failed because token does not have underscores every 3 characters (maxDecimalSymbolsUntilUnderscore = 3)
 * int failingDecimal = 1234;
 * int passingDecimal = 1_234;
 * int ignoredHex = 0xFF;
 * int failingHex = 0xFFFF;
 * int passingHex = 0xFF_FF;
 * int ignoredBinary = 0b0101;
 * int failingBinary = 0b00001111;
 * int passingBinary = 0b0000_1111;
 * </pre>
 * 
 * @author Cheng-Yu Pai
 */

public class NumericLiteralNeedsUnderscoreCheck extends Check
{

    /**
     * Key for error message.
     */
    public static final String MSG_KEY = "numeric.literal.need.underscore";

    private static enum Type
    {
        DECIMAL, HEX, BINARY;
    }

    private static final int DEFAULT_MIN_DECIMAL_SYMBOL_LEN = 7;
    private static final int DEFAULT_MAX_DECIMAL_SYMBOLS_UNTIL_UNDERSCORE = 3;

    private static final int DEFAULT_MIN_HEX_SYMBOL_LEN = 5;
    private static final int DEFAULT_MAX_HEX_SYMBOLS_UNTIL_UNDERSCORE = 4;

    private static final int DEFAULT_MIN_BINARY_SYMBOL_LEN = 9;
    private static final int DEFAULT_MAX_BINARY_SYMBOLS_UNTIL_UNDERSCORE = 8;

    private static final Pattern DECIMAL_SPLITTER = Pattern.compile("[\\.eE]");
    private static final Pattern HEX_SPLITTER = Pattern.compile("[\\.pP]");

    private static final int PREFIX_LENGTH = 2;

    private int minDecimalSymbolLength = DEFAULT_MIN_DECIMAL_SYMBOL_LEN;
    private int maxDecimalSymbolsUntilUnderscore = DEFAULT_MAX_DECIMAL_SYMBOLS_UNTIL_UNDERSCORE;

    private int minHexSymbolLength = DEFAULT_MIN_HEX_SYMBOL_LEN;
    private int maxHexSymbolsUntilUnderscore = DEFAULT_MAX_HEX_SYMBOLS_UNTIL_UNDERSCORE;

    private int minBinarySymbolLength = DEFAULT_MIN_BINARY_SYMBOL_LEN;
    private int maxBinarySymbolsUntilUnderscore = DEFAULT_MAX_BINARY_SYMBOLS_UNTIL_UNDERSCORE;

    /**
     * Sets how many characters in a decimal literal there must be before it checks for an
     * underscore
     * @param len
     *        minimum checking length of the literal
     */
    public void setMinDecimalSymbolLen(int len)
    {
        minDecimalSymbolLength = len;
    }

    /**
     * Sets how many characters there can be until there must be an underscore (for decimal
     * literals)
     * @param amount
     *        maximum number of characters between underscores
     */
    public void setMaxDecimalSymbolsUntilUnderscore(int amount)
    {
        maxDecimalSymbolsUntilUnderscore = amount;
    }

    /**
     * Sets how many characters in a hex literal there must be before it checks for an underscore
     * @param len
     *        minimum checking length of the literal
     */
    public void setMinHexSymbolLen(int len)
    {
        minHexSymbolLength = len;
    }

    /**
     * Sets how many characters there can be until there must be an underscore (for hex literals)
     * @param amount
     *        maximum number of characters between underscores
     */
    public void setMaxHexSymbolsUntilUnderscore(int amount)
    {
        maxHexSymbolsUntilUnderscore = amount;
    }

    /**
     * Sets how many characters in a byte literal there must be before it checks for an underscore
     * @param len
     *        minimum checking length of the literal
     */
    public void setMinBinarySymbolLen(int len)
    {
        minBinarySymbolLength = len;
    }

    /**
     * Sets how many characters there can be until there must be an underscore (for binary literals)
     * @param amount
     *        maximum number of characters between underscores
     */
    public void setMaxBinarySymbolsUntilUnderscore(int amount)
    {
        maxBinarySymbolsUntilUnderscore = amount;
    }

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] {
                TokenTypes.NUM_INT,
                TokenTypes.NUM_LONG,
                TokenTypes.NUM_FLOAT,
                TokenTypes.NUM_DOUBLE
        };
    }

    @Override
    public void visitToken(final DetailAST ast)
    {
        if (!passesCheck(ast.getText())) {
            log(ast.getLineNo(), MSG_KEY);
        }
    }

    /**
     * Returns true if the numeric literal passes the check
     * @param rawLiteral
     *        numeric literal
     * @return if the numeric literal passes the check
     */
    private boolean passesCheck(String rawLiteral)
    {
        Type type = getNumericType(rawLiteral);
        int minCheckingLength = minSymbolsBeforeChecking(type);
        int symbolsUntilUnderscore = maxSymbolsUntilUnderscore(type);
        String[] numericSegments = getNumericSegments(rawLiteral);
        boolean passing = true;
        for (String numericSegment : numericSegments) {
            if (!numericSegmentPassesRequirement(numericSegment,
                    minCheckingLength, symbolsUntilUnderscore)) {
                passing = false;
                break;
            }
        }
        return passing;
    }

    /**
     * Parses the numeric literal to return the minimum checking length for the literal's type
     * @param rawLiteral
     *        numeric literal
     * @return minimum length before checking
     */
    private int minSymbolsBeforeChecking(Type type)
    {
        int minLength;
        switch (type) {
        case DECIMAL:
            minLength = minDecimalSymbolLength;
            break;
        case HEX:
            minLength = minHexSymbolLength;
            break;
        case BINARY:
            minLength = minBinarySymbolLength;
            break;
        default:
            throw new IllegalStateException("Unexpected numeric type " + type.toString());
        }
        return minLength;
    }

    /**
     * Parses the numeric literal to return the maximum number of characters before there must be an
     * underscore for the literal's type
     * @param rawLiteral
     *        numeric literal
     * @return maximum number of characters between underscores
     */
    private int maxSymbolsUntilUnderscore(Type type)
    {
        int maxSymbols;
        switch (type) {
        case DECIMAL:
            maxSymbols = maxDecimalSymbolsUntilUnderscore;
            break;
        case HEX:
            maxSymbols = maxHexSymbolsUntilUnderscore;
            break;
        case BINARY:
            maxSymbols = maxBinarySymbolsUntilUnderscore;
            break;
        default:
            throw new IllegalStateException("Unexpected numeric type " + type.toString());
        }
        return maxSymbols;
    }

    /**
     * <p>
     * Generates easily checkable numeric tokens from the raw literal text.
     * </p>
     * <p>
     * For example: 123.4567 passes because each section itself is not too long and is perfectly
     * readable.
     * </p>
     * Additionally, Java will not compile underscores next to decimal points etc.
     * @param rawLiteral
     *        numeric literal
     * @return numeric tokens (segments) without non-numeric characters
     */
    private String[] getNumericSegments(String rawLiteral)
    {
        Type type = getNumericType(rawLiteral);
        String strippedLiteral = removePrePostfixByType(rawLiteral, type);
        String[] numericSegments;
        switch (type) {
        case DECIMAL:
            numericSegments = DECIMAL_SPLITTER.split(strippedLiteral);
            break;
        case HEX:
            numericSegments = HEX_SPLITTER.split(strippedLiteral);
            break;
        case BINARY:
            numericSegments = new String[1];
            numericSegments[0] = strippedLiteral;
            break;
        default:
            throw new IllegalStateException("Unexpected numeric type " + type.toString());
        }
        return numericSegments;
    }

    /**
     * <p>
     * Returns the type of numeric literal given the raw text.
     * </p>
     * <p>
     * Decimal literals are normal numbers. Example: 1, 1.0, 1.0e0f, 1L
     * </p>
     * <p>
     * Hex literals are preceded by 0x. Example: 0xDEADBEEF, 0xA.Bp1d
     * </p>
     * <p>
     * Binary literals are preceded by 0b. Example: 0b00001111
     * </p>
     * @param rawLiteral
     *        numeric literal
     * @return the type of literal (either decimal, hex, or binary)
     */
    private Type getNumericType(String rawLiteral)
    {
        Type type;
        if (rawLiteral.length() < PREFIX_LENGTH) {
            type = Type.DECIMAL;
        }
        else {
            String prefix = rawLiteral.substring(0, PREFIX_LENGTH);
            if (prefix.equals("0x")) {
                type = Type.HEX;
            }
            else if (prefix.equals("0b")) {
                type = Type.BINARY;
            }
            else {
                type = Type.DECIMAL;
            }
        }
        return type;
    }

    /**
     * Returns whether or not the text passes the underscore requirement given the text and minimum
     * length
     * @param numericSegment
     *        the numeric segment to check
     * @param minLength
     *        minimum length for the numericSegment
     * @param symbolsUntilUnderscore
     *        maximum number of characters until there must be an underscore
     * @return whether or not the numeric segment passes the requirements
     */
    private static boolean
            numericSegmentPassesRequirement(String numericSegment, int minLength, int symbolsUntilUnderscore)
    {
        if (numericSegment.length() < minLength) {
            return true;
        }
        final char underscore = '_';
        int symbolCount = 0;
        boolean passes = true;
        for (int i = 0; i < numericSegment.length(); i++) {
            char current = numericSegment.charAt(i);
            if (symbolCount >= symbolsUntilUnderscore && current != underscore) {
                passes = false;
                break;
            }
            if (current == underscore) {
                symbolCount = 0;
            }
            else {
                symbolCount++;
            }
        }
        return passes;
    }

    /**
     * Removes 0x, 0b prefixes, and l, L, f, F, d, D postfixes from numeric literals
     * @param rawLiteral
     *        the numeric literal that needs to be stripped of prefixes and postfixes
     * @param literalType
     *        the type of the literal being passed in
     * @return a stripped version of the raw literal
     */
    private static String removePrePostfixByType(String rawLiteral, Type literalType)
    {
        String processedLiteral;
        switch (literalType) {
        case DECIMAL:
            processedLiteral = removeLetterPostfix(rawLiteral);
            break;
        case HEX:
            processedLiteral = removePrefix(rawLiteral);
            processedLiteral = removePostfixHex(processedLiteral);
            break;
        case BINARY:
            processedLiteral = removePrefix(rawLiteral);
            processedLiteral = removeLetterPostfix(processedLiteral);
            break;
        default:
            processedLiteral = rawLiteral;
            break;
        }
        return processedLiteral;
    }

    /**
     * Removes the prefixes 0x and 0b
     * @param text
     *        the text to remove the prefixes
     * @return the text without the prefixes
     */
    private static String removePrefix(String text)
    {
        return text.substring(PREFIX_LENGTH);
    }

    /**
     * Removes the postfix from the text if it exists. Does not handle hex literals correctly, for
     * that use removePostfixHex
     * @param text
     *        the text to remove the postfixes
     * @return the text without the postfixes
     */
    private static String removeLetterPostfix(String text)
    {
        char lastchar = text.charAt(text.length() - 1);
        String noPostfixText;
        if (Character.isDigit(lastchar)) {
            noPostfixText = text;
        }
        else {
            noPostfixText = text.substring(0, text.length() - 1);
        }
        return noPostfixText;
    }

    /**
     * Removes the postfix from the hex literal text if it exists. Does not handle other literals
     * correctly, for those use removeLetterPostfix
     * @param text
     *        the text to remove the postfixes
     * @return the text without the postfixes
     */
    private static String removePostfixHex(String text)
    {
        char lastchar = Character.toUpperCase(text.charAt(text.length() - 1));
        boolean hasPostfix = false;
        if (lastchar == 'L') {
            // Example: 0x00FFL
            hasPostfix = true;
        }
        else if (lastchar == 'F' && (text.contains("p") || text.contains("P"))) {
            // Example: 0x1.0p1f (Hex Float)
            hasPostfix = true;
        }
        String noPostfixText;
        if (hasPostfix) {
            noPostfixText = text.substring(0, text.length() - 1);
        }
        else {
            noPostfixText = text;
        }
        return noPostfixText;
    }

}
