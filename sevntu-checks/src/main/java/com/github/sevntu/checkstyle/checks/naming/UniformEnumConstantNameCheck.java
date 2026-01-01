///////////////////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code and other text files for adherence to a set of rules.
// Copyright (C) 2001-2026 the original author or authors.
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
///////////////////////////////////////////////////////////////////////////////////////////////

package com.github.sevntu.checkstyle.checks.naming;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Check forces enum constants to match one of the specified patterns and forces
 * all the values to follow only one of the specified patterns.
 *
 * <p>By default both CamelCase and UPPER_CASE are allowed, so check validates,
 * whether all the values conform the either of them.
 *
 * <p>For example, both enums are allowed by the check:<pre>
 * public enum EnumOne {
 *    FirstElement, SecondElement, ThirdElement;
 * }
 * public enum EnumTwo {
 *    FIRST_ELEMENT, SECOND_ELEMENT, THIRD_ELEMENT;
 * }</pre> But the following enum, is violated, because values conform
 * different notations: <pre>
 * public enum EnumThree {
 *    FirstElement, SECOND_ELEMENT, ThirdElement;
 * }
 * </pre>
 *
 * <p>To use only CamelCase, use the following configuration:
 *
 * <pre>
 * &lt;module name="UniformEnumConstantNameCheck"&gt;
 *    &lt;property name="format" value="^[A-Z][a-zA-Z0-9]*$"/&gt;
 * &lt;/module&gt;
 * </pre>
 *
 * <p>If both CamelCase and UPPER_CASE are allowed, use the following configuration
 * (this is the default):
 *
 * <pre>
 * &lt;module name="UniformEnumConstantNameCheck"&gt;
 *    &lt;property name="format" value="^[A-Z][a-zA-Z0-9]*$,^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$"/&gt;
 * &lt;/module&gt;
 * </pre>
 *
 * <p>Only first violation is reported for each enumeration because of the nature
 * of the check: it's impossible to determine which specific pattern user should
 * follow for this certain enumeration, as multiple patterns have been
 * specified. The only thing that this check reports is whether there is at
 * least one pattern (among specified in the configuration), which all the enum
 * constant conform or there is no.
 *
 * @author Pavel Baranchikov
 * @since 1.21.0
 */
public class UniformEnumConstantNameCheck extends AbstractCheck {

    /**
     * Message code for format violations. Used, when more than one format
     * violated.
     */
    public static final String MSG_NOT_VALID_MULTI = "enum.name.formats.violated";
    /**
     * Message code for format violations. Used, when exactly one format
     * violated.
     */
    public static final String MSG_NOT_VALID_SINGLE = "enum.name.format.violated";
    /**
     * Camel notation regular expression.
     */
    public static final String CAMEL_PATTERN = "^[A-Z][a-zA-Z0-9]*$";
    /**
     * Upper case notation regular expression.
     */
    public static final String UPPERCASE_PATTERN = "^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$";
    /**
     * Default pattern for enumeration values.
     */
    public static final String[] DEFAULT_PATTERN = {
        CAMEL_PATTERN,
        UPPERCASE_PATTERN,
    };

    /**
     * Regular expression list to test Enumeration names against.
     */
    private List<Pattern> patterns;
    /**
     * Number of patterns specified for {@code patterns} field. This field is
     * always the size of {@link #patterns}.
     */
    private int patternCount;

    /**
     * Constructs check with the default pattern.
     */
    public UniformEnumConstantNameCheck() {
        setFormats(DEFAULT_PATTERN);
    }

    /**
     * Method sets format to match Class Enumeration names.
     *
     * @param regexps format to check against
     */
    public final void setFormats(String... regexps) {
        patterns = new ArrayList<>(regexps.length);
        for (final String regexp: regexps) {
            final Pattern pattern = Pattern.compile(regexp, 0);
            patterns.add(pattern);
        }
        patternCount = regexps.length;
    }

    @Override
    public int[] getDefaultTokens() {
        return getAcceptableTokens();
    }

    @Override
    public int[] getAcceptableTokens() {
        return new int[] {
            TokenTypes.ENUM_DEF,
            };
    }

    @Override
    public int[] getRequiredTokens() {
        return getAcceptableTokens();
    }

    @Override
    public void visitToken(DetailAST ast) {
        final DetailAST objBlock = ast.findFirstToken(TokenTypes.OBJBLOCK);
        final BitSet conformedPatterns = new BitSet(patternCount);
        conformedPatterns.set(0, patternCount);
        for (DetailAST member = objBlock.getFirstChild(); member != null
                && !conformedPatterns.isEmpty();
                member = member.getNextSibling()) {
            if (member.getType() != TokenTypes.ENUM_CONSTANT_DEF) {
                continue;
            }
            final String enumName = member.findFirstToken(TokenTypes.IDENT).getText();
            final BitSet matched = match(enumName, conformedPatterns);
            if (matched.isEmpty()) {
                logViolation(member, enumName, conformedPatterns);
            }
            conformedPatterns.and(matched);
        }
    }

    /**
     * Logs violation for the specified token, representing the specified enum
     * value wich violates the specified patterns.
     *
     * @param member
     *        token, which violates the check
     * @param enumName
     *        enum value name for this token
     * @param violated
     *        bit set of violated patterns
     */
    private void logViolation(DetailAST member, String enumName, BitSet violated) {
        final String patternsString;
        final String msgKey;
        if (violated.cardinality() == 1) {
            msgKey = MSG_NOT_VALID_SINGLE;
            patternsString = patterns.get(violated.nextSetBit(0))
                    .toString();
        }
        else {
            msgKey = MSG_NOT_VALID_MULTI;
            final Collection<Pattern> violatedPatterns = new ArrayList<>(
                    violated.cardinality());
            int index = violated.nextSetBit(0);
            while (index >= 0) {
                violatedPatterns.add(patterns.get(index));
                index = violated.nextSetBit(index + 1);
            }
            patternsString = violatedPatterns.toString();
        }
        log(member, msgKey, enumName,
                patternsString);
    }

    /**
     * Matches the specified enum name against the patterns, specified by
     * {@code conformedPatterns}.
     *
     * @param name
     *        name to validate
     * @param conformedPatterns
     *        bit set of patterns, which the method should match against.
     * @return bit set of matched patterns. Returned value is always a subset of
     *         {@code conformedPatterns}
     */
    private BitSet match(String name, BitSet conformedPatterns) {
        final BitSet result = new BitSet(patternCount);
        for (int index = 0; index < patterns.size(); index++) {
            if (conformedPatterns.get(index)) {
                final Pattern pattern = patterns.get(index);
                if (pattern.matcher(name).find()) {
                    result.set(index);
                }
            }
        }
        return result;
    }

}
