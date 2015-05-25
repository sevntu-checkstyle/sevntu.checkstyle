////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2016 the original author or authors.
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

package com.github.sevntu.checkstyle.checks.misc;

import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.beanutils.ConversionException;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Check, performing checks for literal numeric type specifiers case. Numeric
 * float constants cat be represented in different ways:
 * <ul>
 * <li><code>50.0f</code>
 * <li><code>50.0F</code>
 * </ul>
 * The check prevents from mixing these two styles, allowing user to restrict
 * check usage for only certain types of literal numeric constants, specified in
 * "tokens" property:
 * <ul>
 * <li>NUM_LONG
 * <li>NUM_FLOAT
 * <li>NUM_DOUBLE
 * </ul>
 * Check can force euther upper or lower case of the constant, using "option"
 * property:
 * <ul>
 * <li>upper
 * <li>lower
 * </ul>
 * <br>
 * Check is full equivalent to already existing check UpperEll, when specifying
 * option to "upper" and tokens to "NUM_LONG" <br>
 * Default properties are upper case and all the available tokens: NUM_LONG,
 * NUM_FLOAT, NUM_DOUBLE
 *
 * @author Pavel Baranchikov
 * @see {@link com.puppycrawl.tools.checkstyle.checks.UpperEllCheck}
 *
 */
public class PrimitiveLiteralsCaseCheck extends AbstractCheck {

    /**
     * Message key to show when specifier should be in upper case, but is in
     * lower.
     */
    protected static final String MSG_UPPER_CASE = "primitige.literal.case.upper";
    /**
     * Message key to show when specifier should be in lower case, but is in
     * upper.
     */
    protected static final String MSG_LOWER_CASE = "primitige.literal.case.lower";
    /**
     * Processors for different required character cases.
     */
    private final Map<CharacterCase, CaseValidationProcessor> processors;
    /**
     * Required character case.
     */
    private CharacterCase requiredCase = CharacterCase.UPPER;

    /**
     * Constructs the check.
     */
    public PrimitiveLiteralsCaseCheck() {
        processors = new EnumMap<CharacterCase, CaseValidationProcessor>(
                CharacterCase.class);
        processors.put(CharacterCase.UPPER, new UpperCaseValidationProcessor());
        processors.put(CharacterCase.LOWER, new LowerCaseValidationProcessor());
    }

    /**
     * Sets requiredCase value from the configuration.
     *
     * @param requiredCaseStr string representation for required case.
     */
    public void setRequiredCase(String requiredCaseStr) {
        try {
            this.requiredCase = CharacterCase.valueOf(requiredCaseStr.trim()
                    .toUpperCase(Locale.ENGLISH));
        }
        catch (IllegalArgumentException iae) {
            throw new ConversionException("unknown character case: "
                    + requiredCaseStr, iae);
        }
    }

    @Override
    public int[] getDefaultTokens() {
        return getAcceptableTokens();
    }

    @Override
    public int[] getAcceptableTokens() {
        return new int[] {
            TokenTypes.NUM_DOUBLE,
            TokenTypes.NUM_FLOAT,
            TokenTypes.NUM_LONG,
        };
    }

    @Override
    public int[] getRequiredTokens() {
        return new int[0];
    }

    @Override
    public void visitToken(DetailAST ast) {
        final String astString = ast.getText();
        final char specifier = astString.charAt(astString.length() - 1);
        if (Character.isDigit(specifier)) {
            // For primitives, ending with digit - no validation should be applied.
            return;
        }
        final CaseValidationProcessor processor = processors.get(requiredCase);
        final boolean correctSpecifier = processor.isSpecifierCorrect(specifier);
        if (!correctSpecifier) {
            log(ast.getLineNo(),
                    ast.getColumnNo() + astString.length() - 1,
                    processor.getMsgKey(),
                    processor.getAdjustedSpecifier(specifier));
        }
    }

    /**
     * Enum describes character casess.
     */
    protected enum CharacterCase {
        /**
         * Upper case character, i.e. {@code F}, {@code L}, {@code D}.
         */
        UPPER,
        /**
         * Lower case character, i.e. {@code f}, {@code l}, {@code d}.
         */
        LOWER;
    }

    /**
     * Interface to hold functionality specific for the characther case.
     */
    private interface CaseValidationProcessor {
        /**
         * Returns error message key for of this processor.
         *
         * @return erorr message key
         */

        String getMsgKey();

        /**
         * Method determines, whether the specifier is wrong. This is not the
         * same as just inverting the case, when specifier is correct, because
         * there may be no specifier at all, which is not wrong case.
         *
         * @param specifier
         *        primitive specifier
         * @return <code>true</code> if the specifier is wrong
         */
        boolean isSpecifierCorrect(char specifier);

        /**
         * Method corrects the specifier to the one it should be when the option
         * is selected active.
         *
         * @param specifier
         *        specifier to convert to correct case
         * @return correct specifier
         */
        char getAdjustedSpecifier(char specifier);
    }

    /**
     * Processor for upper case required.
     */
    private static class UpperCaseValidationProcessor implements CaseValidationProcessor {
        @Override
        public char getAdjustedSpecifier(char specifier) {
            return Character.toUpperCase(specifier);
        }

        @Override
        public String getMsgKey() {
            return MSG_UPPER_CASE;
        }

        @Override
        public boolean isSpecifierCorrect(char specifier) {
            return Character.isUpperCase(specifier);
        }

    }

    /**
     * Processor for lower case required.
     */
    private static class LowerCaseValidationProcessor implements CaseValidationProcessor {

        @Override
        public char getAdjustedSpecifier(char specifier) {
            return Character.toLowerCase(specifier);
        }

        @Override
        public String getMsgKey() {
            return MSG_LOWER_CASE;
        }

        @Override
        public boolean isSpecifierCorrect(char specifier) {
            return Character.isLowerCase(specifier);
        }

    }
}
