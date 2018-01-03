////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2018 the original author or authors.
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
////////////////////////////////////////////////////////////////////////////////

package com.github.sevntu.checkstyle.checks.coding;

import java.util.regex.Pattern;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * This check verifies that large numeric literals are spaced by underscores.
 * </p>
 * <p>
 * Java 7 allows for underscores to delimit numeric literals to enhance readability because
 * very large numeric literals are hard to read. For example:
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
 * long, float, and double) before the check begins to look for underscores. Numeric literals
 * with delimiters like decimal points or exponentials will be split before checking the
 * length. Default is 7.
 * </p>
 * <p>
 * "maxDecimalSymbolsUntilUnderscore" - The maximum number of symbols in a decimal literal
 * allowed before the check demands an underscore. This does not take postfixes and delimiters
 * like decimal points and exponentials into account. Default is 3.
 * </p>
 * <p>
 * "minHexSymbolLength" - The minimum number of symbols in a hex literal before the check
 * begins to look for underscores. Numeric literals with delimiters like decimal points or
 * exponentials will be split before checking the length. Default is 5.
 * </p>
 * <p>
 * "maxHexSymbolsUntilUnderscore" - The maximum number of symbols in a hex literal allowed
 * before the check demands an underscore. This does not take the prefix 0x, delimiters like
 * decimal points and exponentials, and postfixes into account. Default is 4.
 * </p>
 * <p>
 * "minBinarySymbolLength" - The minimum number of symbols in a binary literal before the check
 * begins to look for underscores. Default is 9.
 * </p>
 * <p>
 * "maxBinarySymbolsUntilUnderscore" - The maximum number of symbols in a binary literal
 * allowed before the check demands an underscore. This does not take the prefix 0b and
 * postfixes into account. Default is 8.
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
 * // Ignored because each segment delimited by the decimal point has a length
 * // less than minDecimalSymbolLength (7)
 * float ignoredDecimal = 123456.123456f;
 * // Failed because token does not have underscores every 3 characters
 * // (maxDecimalSymbolsUntilUnderscore = 3)
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
 * // Ignored because each segment delimited by the decimal point has a length
 * // less than minDecimalSymbolLength (4)
 * float ignoredDecimal = 123.123f;
 * // Failed because token does not have underscores every 3 characters
 * // (maxDecimalSymbolsUntilUnderscore = 3)
 * int failingDecimal = 1234;
 * int passingDecimal = 1_234;
 * int ignoredHex = 0xFF;
 * int failingHex = 0xFFFF;
 * int passingHex = 0xFF_FF;
 * int ignoredBinary = 0b0101;
 * int failingBinary = 0b00001111;
 * int passingBinary = 0b0000_1111;
 * </pre>
 * @author Cheng-Yu Pai
 */

public class NumericLiteralNeedsUnderscoreCheck extends AbstractCheck {

    /**
     * Key for error message.
     */
    public static final String MSG_KEY = "numeric.literal.need.underscore";

    /**
     * Type of numeric literal.
     */
    protected enum NumericType {

        /**
         * Denotes a decimal literal. For example, 1.2f
         */
        DECIMAL,

        /**
         * Denotes a hex literal. For example, 0x00FF
         */
        HEX,

        /**
         * Denotes a binary literal. For example, 0b0011
         */
        BINARY;

    }

    /**
     * Default minimum symbols for a decimal literal before checking.
     */
    private static final int DEFAULT_MIN_DECIMAL_SYMBOL_LEN = 7;

    /**
     * Default maximum symbols for a decimal literal before it demands an underscore.
     */
    private static final int DEFAULT_MAX_DECIMAL_SYMBOLS_UNTIL_UNDERSCORE = 3;

    /**
     * Default minimum symbols for a hex literal before checking.
     */
    private static final int DEFAULT_MIN_HEX_SYMBOL_LEN = 5;

    /**
     * Default maximum symbols for a hex literal before it demands an underscore.
     */
    private static final int DEFAULT_MAX_HEX_SYMBOLS_UNTIL_UNDERSCORE = 4;

    /**
     * Default minimum symbols for a binary literal before checking.
     */
    private static final int DEFAULT_MIN_BINARY_SYMBOL_LEN = 9;

    /**
     * Default maximum symbols for a binary literal before it demands an underscore.
     */
    private static final int DEFAULT_MAX_BINARY_SYMBOLS_UNTIL_UNDERSCORE = 8;

    /**
     * Default regexp for fields to ignore for this check.
     */
    private static final Pattern DEFAULT_IGNORE_FIELD_NAME_PATTERN =
            Pattern.compile("serialVersionUID");

    /**
     * Regex for splitting a decimal literal into checkable substrings.
     */
    private static final Pattern DECIMAL_SPLITTER = Pattern.compile("[\\.eE]");

    /**
     * Regex for splitting a hex literal into checkable substrings.
     */
    private static final Pattern HEX_SPLITTER = Pattern.compile("[\\.pP]");

    /**
     * Length of prefixes. Prefixes are 0x and 0b.
     */
    private static final int PREFIX_LENGTH = 2;

    /**
     * Unexpected numeric type error string.
     */
    private static final String UNEXPECTED_NUMERIC_TYPE_ERROR = "Unexpected numeric type ";

    /**
     * Minimum symbols for a decimal literal before checking.
     */
    private int minDecimalSymbolLength = DEFAULT_MIN_DECIMAL_SYMBOL_LEN;

    /**
     * Maximum symbols for a decimal literal before it demands an underscore.
     */
    private int maxDecimalSymbolsUntilUnderscore = DEFAULT_MAX_DECIMAL_SYMBOLS_UNTIL_UNDERSCORE;

    /**
     * Minimum symbols for a hex literal before checking.
     */
    private int minHexSymbolLength = DEFAULT_MIN_HEX_SYMBOL_LEN;

    /**
     * Maximum symbols for a hex literal before it demands an underscore.
     */
    private int maxHexSymbolsUntilUnderscore = DEFAULT_MAX_HEX_SYMBOLS_UNTIL_UNDERSCORE;

    /**
     * Minimum symbols for a binary literal before checking.
     */
    private int minBinarySymbolLength = DEFAULT_MIN_BINARY_SYMBOL_LEN;

    /**
     * Maximum symbols for a binary literal before it demands an underscore.
     */
    private int maxBinarySymbolsUntilUnderscore = DEFAULT_MAX_BINARY_SYMBOLS_UNTIL_UNDERSCORE;

    /**
     * Regexp for fields to ignore.
     */
    private Pattern ignoreFieldNamePattern = DEFAULT_IGNORE_FIELD_NAME_PATTERN;

    /**
     * Sets how many characters in a decimal literal there must be before it checks for an
     * underscore.
     * @param length
     *        minimum checking length of the literal
     */
    public void setMinDecimalSymbolLength(int length) {
        minDecimalSymbolLength = length;
    }

    /**
     * Sets how many characters there can be until there must be an underscore (for decimal
     * literals).
     * @param amount
     *        maximum number of characters between underscores
     */
    public void setMaxDecimalSymbolsUntilUnderscore(int amount) {
        maxDecimalSymbolsUntilUnderscore = amount;
    }

    /**
     * Sets how many characters in a hex literal there must be before it checks for an
     * underscore.
     * @param length
     *        minimum checking length of the literal
     */
    public void setMinHexSymbolLength(int length) {
        minHexSymbolLength = length;
    }

    /**
     * Sets how many characters there can be until there must be an underscore (for hex
     * literals).
     * @param amount
     *        maximum number of characters between underscores
     */
    public void setMaxHexSymbolsUntilUnderscore(int amount) {
        maxHexSymbolsUntilUnderscore = amount;
    }

    /**
     * Sets how many characters in a byte literal there must be before it checks for an
     * underscore.
     * @param length
     *        minimum checking length of the literal
     */
    public void setMinBinarySymbolLength(int length) {
        minBinarySymbolLength = length;
    }

    /**
     * Sets how many characters there can be until there must be an underscore (binary
     * literals).
     * @param amount
     *        maximum number of characters between underscores
     */
    public void setMaxBinarySymbolsUntilUnderscore(int amount) {
        maxBinarySymbolsUntilUnderscore = amount;
    }

    /**
     * Sets the regexp pattern for field names to ignore.
     * @param pattern
     *        the regexp pattern of fields to ignore
     */
    public void setIgnoreFieldNamePattern(String pattern) {
        ignoreFieldNamePattern = Pattern.compile(pattern);
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.NUM_INT,
            TokenTypes.NUM_LONG,
            TokenTypes.NUM_FLOAT,
            TokenTypes.NUM_DOUBLE,
        };
    }

    @Override
    public int[] getAcceptableTokens() {
        return getDefaultTokens();
    }

    @Override
    public int[] getRequiredTokens() {
        return getDefaultTokens();
    }

    @Override
    public void visitToken(final DetailAST ast) {
        if (!passesCheck(ast)) {
            log(ast.getLineNo(), MSG_KEY);
        }
    }

    /**
     * Checks if the provided token is a field.
     * @param ast
     *        the token to check
     * @return whether or not the token is a field
     */
    private static boolean isField(final DetailAST ast) {
        DetailAST current = ast;
        while (current.getParent() != null && current.getType() != TokenTypes.VARIABLE_DEF) {
            current = current.getParent();
        }
        return current.getType() == TokenTypes.VARIABLE_DEF
                && current.branchContains(TokenTypes.LITERAL_STATIC)
                && current.branchContains(TokenTypes.FINAL);
    }

    /**
     * Returns the provided field's name.
     * @param ast
     *        the field for which the function looks for a name
     * @return the field's name
     */
    private static String getFieldName(final DetailAST ast) {
        DetailAST current = ast;
        while (current.getType() != TokenTypes.VARIABLE_DEF) {
            current = current.getParent();
        }
        current = current.getFirstChild();
        while (current.getType() != TokenTypes.IDENT) {
            current = current.getNextSibling();
        }
        return current.getText();
    }

    /**
     * Returns true if the ast passes the check.
     * @param ast
     *        the numeric literal to check
     * @return if the numeric literal passes the check
     */
    private boolean passesCheck(final DetailAST ast) {
        boolean passing;
        if (isField(ast) && ignoreFieldNamePattern.matcher(getFieldName(ast)).find()) {
            passing = true;
        }
        else {
            final String rawLiteral = ast.getText();
            final NumericType type = getNumericType(rawLiteral);
            final int minCheckingLength = minSymbolsBeforeChecking(type);
            final int symbolsUntilUnderscore = maxSymbolsUntilUnderscore(type);
            final String strippedLiteral = removePrePostfixByType(rawLiteral, type);
            final String[] numericSegments = getNumericSegments(strippedLiteral, type);
            passing = true;
            for (String numericSegment : numericSegments) {
                if (!numericSegmentPassesRequirement(numericSegment,
                        minCheckingLength, symbolsUntilUnderscore)) {
                    passing = false;
                    break;
                }
            }
        }
        return passing;
    }

    /**
     * Parses the numeric literal to return the minimum checking length for the literal's type.
     * @param type
     *        the type of numerical literal
     * @return minimum length before checking
     */
    private int minSymbolsBeforeChecking(NumericType type) {
        final int minLength;
        if (type.equals(NumericType.DECIMAL)) {
            minLength = minDecimalSymbolLength;
        }
        else if (type.equals(NumericType.HEX)) {
            minLength = minHexSymbolLength;
        }
        else if (type.equals(NumericType.BINARY)) {
            minLength = minBinarySymbolLength;
        }
        else {
            throw new IllegalStateException(UNEXPECTED_NUMERIC_TYPE_ERROR
                    + type.toString());
        }
        return minLength;
    }

    /**
     * Parses the numeric literal to return the maximum number of characters before there must
     * be an underscore for the literal's type.
     * @param type
     *        the type of numerical literal
     * @return maximum number of characters between underscores
     */
    private int maxSymbolsUntilUnderscore(NumericType type) {
        final int maxSymbols;
        if (type.equals(NumericType.DECIMAL)) {
            maxSymbols = maxDecimalSymbolsUntilUnderscore;
        }
        else if (type.equals(NumericType.HEX)) {
            maxSymbols = maxHexSymbolsUntilUnderscore;
        }
        else if (type.equals(NumericType.BINARY)) {
            maxSymbols = maxBinarySymbolsUntilUnderscore;
        }
        else {
            throw new IllegalStateException(UNEXPECTED_NUMERIC_TYPE_ERROR
                    + type.toString());
        }
        return maxSymbols;
    }

    /**
     * <p>
     * Generates easily checkable numeric tokens from the raw literal text, assuming
     * the numeric type provided.
     * </p>
     * <p>
     * For example: 123.4567 passes because each section itself is not too long and is
     * perfectly readable.
     * </p>
     * Additionally, Java will not compile underscores next to decimal points etc.
     * @param strippedLiteral
     *        numeric literal stripped of any prefixes and postfixes
     * @param type
     *        the numeric type of the literal
     * @return numeric tokens (segments) without non-numeric characters
     */
    private static String[] getNumericSegments(String strippedLiteral, NumericType type) {
        final String[] numericSegments;
        if (type.equals(NumericType.DECIMAL)) {
            numericSegments = DECIMAL_SPLITTER.split(strippedLiteral);
        }
        else if (type.equals(NumericType.HEX)) {
            numericSegments = HEX_SPLITTER.split(strippedLiteral);
        }
        else if (type.equals(NumericType.BINARY)) {
            numericSegments = new String[1];
            numericSegments[0] = strippedLiteral;
        }
        else {
            throw new IllegalStateException(UNEXPECTED_NUMERIC_TYPE_ERROR
                    + type.toString());
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
    private static NumericType getNumericType(String rawLiteral) {
        final NumericType type;
        if (rawLiteral.length() < PREFIX_LENGTH) {
            type = NumericType.DECIMAL;
        }
        else {
            final String prefix = rawLiteral.substring(0, PREFIX_LENGTH);
            if ("0x".equals(prefix)) {
                type = NumericType.HEX;
            }
            else if ("0b".equals(prefix)) {
                type = NumericType.BINARY;
            }
            else {
                type = NumericType.DECIMAL;
            }
        }
        return type;
    }

    /**
     * Returns whether or not the text passes the underscore requirement given the text and
     * minimum length.
     * @param numericSegment
     *        the numeric segment to check
     * @param minLength
     *        minimum length for the numericSegment
     * @param symbolsUntilUnderscore
     *        maximum number of characters until there must be an underscore
     * @return whether or not the numeric segment passes the requirements
     */
    private static boolean numericSegmentPassesRequirement(String numericSegment,
            int minLength, int symbolsUntilUnderscore) {
        boolean passes = true;
        if (numericSegment.length() >= minLength) {
            final char underscore = '_';
            int symbolCount = 0;

            for (int i = 0; i < numericSegment.length(); i++) {
                final char current = numericSegment.charAt(i);
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
        }
        return passes;
    }

    /**
     * Removes 0x, 0b prefixes, and l, L, f, F, d, D postfixes from numeric literals.
     * @param rawLiteral
     *        the numeric literal that needs to be stripped of prefixes and postfixes
     * @param literalType
     *        the type of the literal being passed in
     * @return a stripped version of the raw literal
     */
    private static String removePrePostfixByType(String rawLiteral, NumericType literalType) {
        String processedLiteral;
        if (literalType.equals(NumericType.DECIMAL)) {
            processedLiteral = removeLetterPostfix(rawLiteral);
        }
        else if (literalType.equals(NumericType.HEX)) {
            processedLiteral = removePrefix(rawLiteral);
            processedLiteral = removePostfixHex(processedLiteral);
        }
        else if (literalType.equals(NumericType.BINARY)) {
            processedLiteral = removePrefix(rawLiteral);
            processedLiteral = removeLetterPostfix(processedLiteral);
        }
        else {
            throw new IllegalStateException(UNEXPECTED_NUMERIC_TYPE_ERROR
                    + literalType.toString());
        }
        return processedLiteral;
    }

    /**
     * Removes the prefixes 0x and 0b.
     * @param text
     *        the text to remove the prefixes
     * @return the text without the prefixes
     */
    private static String removePrefix(String text) {
        return text.substring(PREFIX_LENGTH);
    }

    /**
     * Removes the postfix from the text if it exists. Does not handle hex literals correctly,
     * for that use removePostfixHex.
     * @param text
     *        the text to remove the postfixes
     * @return the text without the postfixes
     */
    private static String removeLetterPostfix(String text) {
        final char lastchar = text.charAt(text.length() - 1);
        final String noPostfixText;
        if (Character.isDigit(lastchar)) {
            noPostfixText = text;
        }
        else {
            noPostfixText = text.substring(0, text.length() - 1);
        }
        return noPostfixText;
    }

    /**
     * Removes the postfix from the hex literal text if it exists. Does not handle other
     * literals correctly, for those use removeLetterPostfix.
     * @param text
     *        the text to remove the postfixes
     * @return the text without the postfixes
     */
    private static String removePostfixHex(String text) {
        final char lastchar = Character.toUpperCase(text.charAt(text.length() - 1));
        boolean hasPostfix = false;
        if (lastchar == 'L') {
            // Example: 0x00FFL
            hasPostfix = true;
        }
        else if (lastchar == 'F' && (text.contains("p") || text.contains("P"))) {
            // Example: 0x1.0p1f (Hex Float)
            hasPostfix = true;
        }
        final String noPostfixText;
        if (hasPostfix) {
            noPostfixText = text.substring(0, text.length() - 1);
        }
        else {
            noPostfixText = text;
        }
        return noPostfixText;
    }

}
