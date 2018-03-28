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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.github.sevntu.checkstyle.Utils;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.CheckstyleException;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.CommonUtils;

/**
 * Check that forbidden method is not used. We can forbid a method by method name and number of
 * arguments.
 * This can be used to enforce things like:
 * <ul>
 * <li> exit() method of System class should not be called.</li>
 * <li> assertTrue() and assertFalse() methods of Assert class have a 1 arg variant that does not
 * provide a helpful message on failure. These methods should not be used.
 * </ul>
 * Parameters are:
 * <ul>
 * <li><b>methodName</b> - Regex to match name of the method to be forbidden.
 * When blank or unspecified, all the methods are allowed.</li>
 * <li><b>argumentCount</b> - Number or range to match number of arguments the method takes.
 * Multiple numbers/ranges must be comma separated. When unspecified, defaults to "0-".
 * </ul>
 *
 * <p>An example configuration:
 * <pre>
 * &lt;module name="ForbidCertainMethodCheck"&gt;
 *    &lt;property name="methodName" value="exit"/&gt;
 * &lt;/module&gt;
 * &lt;module name="ForbidCertainMethodCheck"&gt;
 *    &lt;property name="methodName" value="assert(True|False)"/&gt;
 *    &lt;property name="argumentCount" value="1"/&gt;
 * &lt;/module&gt;
 * &lt;module name="ForbidCertainMethodCheck"&gt;
 *    &lt;property name="methodName" value="assertEquals"/&gt;
 *    &lt;property name="argumentCount" value="2"/&gt;
 * &lt;/module&gt;
 * </pre>
 * Argument count can be bounded range (e.g.: {@code 2-4}) or unbounded range
 * (e.g.: {@code -5, 6-}). Unbounded range can be unbounded only on one side.
 * Multiple ranges must be comma separated.
 * For example, the following will allow only 4 and 8 arguments.
 *
 * <pre>
 * &lt;module name="ForbidCertainMethodCheck"&gt;
 *    &lt;property name="methodName" value="asList"/&gt;
 *    &lt;property name="argumentCount" value="-3, 5-7, 9-"/&gt;
 * &lt;/module&gt;
 * </pre>
 *
 * <p>
 * Note: The check only matches method name. Matching on class/object of the
 * method is not done. For e.g. there is no way to forbid only "System.exit()". You can match
 * by methodName="exit", but beware that it will violate "System.exit()" and "MySystem.exit()",
 * so use it with caution.
 * </p>
 *
 * @author <a href="mailto:raghavgautam@gmail.com">Raghav Kumar Gautam</a>
 */
public class ForbidCertainMethodCheck extends AbstractCheck {

    /** Key is pointing to the warning message text in "messages.properties" file. */
    public static final String MSG_KEY = "forbid.certain.method";
    /** Regex for splitting string on comma. */
    private static final Pattern COMMA_REGEX = Pattern.compile(",");

    /** Name of the method. */
    private Pattern methodName = CommonUtils.createPattern("^$");

    /** Range for number of arguments. */
    private String argumentCount = "0-";
    /** Range objects for matching number of arguments. */
    private final List<IntRange> argumentCountRanges = new ArrayList<>(
        Collections.singletonList(new IntRange(0, Integer.MAX_VALUE)));

    /**
     * Set method name regex for the forbidden method.
     * @param methodName regex for the method name
     */
    public void setMethodName(String methodName) {
        this.methodName = CommonUtils.createPattern(methodName);
    }

    /**
     * Set number or range to match number of arguments of the forbidden method.
     * Multiple values must be comma separated.
     * @param argumentCount range for matching number of arguments
     * @throws CheckstyleException when argumentCount is not a valid range
     */
    public void setArgumentCount(String argumentCount) throws CheckstyleException {
        this.argumentCount = argumentCount;
        if (argumentCount.trim().length() > 0) {
            final String[] rangeTokens = COMMA_REGEX.split(argumentCount);
            argumentCountRanges.clear();
            for (String oneToken : rangeTokens) {
                argumentCountRanges.add(IntRange.from(oneToken));
            }
        }
        else {
            throw new CheckstyleException(
                "argumentCount must be non-empty, found: " + argumentCount);
        }
    }

    @Override
    public int[] getDefaultTokens() {
        return getRequiredTokens();
    }

    @Override
    public int[] getAcceptableTokens() {
        return getRequiredTokens();
    }

    @Override
    public int[] getRequiredTokens() {
        return new int[] {
            TokenTypes.METHOD_CALL,
        };
    }

    @Override
    public void visitToken(DetailAST ast) {
        switch (ast.getType()) {
            case TokenTypes.METHOD_CALL:
                final DetailAST dot = ast.getFirstChild();
                // method that looks like: method()
                final String methodNameInCode;
                if (dot.getType() == TokenTypes.IDENT) {
                    methodNameInCode = dot.getText();
                }
                // method that looks like: obj.method()
                else {
                    methodNameInCode = dot.getLastChild().getText();
                }
                final int numArgsInCode = dot.getNextSibling().getChildCount(TokenTypes.EXPR);
                if (isForbiddenMethod(ast, methodNameInCode, numArgsInCode)) {
                    log(ast.getLineNo(), ast.getColumnNo(), MSG_KEY, methodNameInCode, methodName,
                        numArgsInCode, argumentCount);
                }
                break;
            default:
                Utils.reportInvalidToken(ast.getType());
                break;
        }
    }

    /**
     * Check if the method/constructor call against defined rules.
     * @param ast ast for the call
     * @param name ruleName of the the method
     * @param argCount number of arguments of the method
     * @return true if method name and argument matches, false otherwise.
     */
    private boolean isForbiddenMethod(DetailAST ast, String name, int argCount) {
        boolean matched = false;
        if (methodName.matcher(name).matches()) {
            for (IntRange intRange : argumentCountRanges) {
                if (intRange.contains(argCount)) {
                    matched = true;
                    break;
                }
            }
        }
        return matched;
    }

    /**
     * Represents a range of non-negative integers.
     * Range must be bounded on one side or both sides.
     * It can't be unbounded on both side.
     * <br>
     * Some examples of valid ranges:
     * <ul>
     * <li>1-10: 1 and 10 and all numbers between 1 and 10</li>
     * <li>-10: same as 0-10</li>
     * <li>5-: same as 5-infinity</li>
     * <li>1: same as 1-1</li>
     * </ul>
     */
    static class IntRange {
        /** Regex for matching range. */
        private static final Pattern RANGE_PATTERN =
            Pattern.compile("^\\s*+(\\d*+)\\s*+-\\s*+(\\d*+)\\s*+$");
        /** Lower limit of the range. No lower limit when null. */
        private final int lowerLimit;
        /** Upper limit of the range. No upper limit when null. */
        private final int upperLimit;

        /**
         * Initialize IntRange object with a lower limit and an upper limit.
         * @param lowerLimit lower limit of the range, must be >= 0, null is equivalent to 0
         * @param upperLimit upper limit of the range, null is equivalent to infinity
         */
        IntRange(int lowerLimit, int upperLimit) {
            this.lowerLimit = lowerLimit;
            this.upperLimit = upperLimit;
        }

        /**
         * Create a range object corresponding to it string representation.
         * @param range string representation of the range
         * @return IntRange object for the string
         *
         * @throws CheckstyleException if the specified range is not valid
         */
        static IntRange from(String range) throws CheckstyleException {
            int lowerLimit = 0;
            int upperLimit = Integer.MAX_VALUE;
            if (!range.contains("-")) {
                lowerLimit = Integer.parseInt(range.trim());
                upperLimit = lowerLimit;
            }
            else {
                final Matcher matcher = RANGE_PATTERN.matcher(range);
                if (!matcher.find()) {
                    throw new CheckstyleException("Specified range is not valid: " + range);
                }
                final String lowerLimitString = matcher.group(1);
                final String upperLimitString = matcher.group(2);
                if (lowerLimitString.length() == 0 && upperLimitString.length() == 0) {
                    throw new CheckstyleException("Specified range is unbounded on both side: "
                        + range);
                }
                if (lowerLimitString.length() > 0) {
                    lowerLimit = Integer.parseInt(lowerLimitString);
                }
                if (upperLimitString.length() > 0) {
                    upperLimit = Integer.parseInt(upperLimitString);
                }
                if (lowerLimit > upperLimit) {
                    throw new CheckstyleException(
                        "Lower limit of the range is larger than the upper limit: " + range);
                }
            }
            return new IntRange(lowerLimit, upperLimit);
        }

        /**
         * Check if range contain given number. Range is closed.
         * If lower/upper bound is absent, it is considered unbounded on lower/upper side.
         * @param num the number to be checked
         * @return true if number is contained in the range, false otherwise
         */
        public boolean contains(int num) {
            return num >= lowerLimit && num <= upperLimit;
        }
    }

}
