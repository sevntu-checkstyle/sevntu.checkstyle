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

package com.github.sevntu.checkstyle.checks.coding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.sevntu.checkstyle.SevntuUtil;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.AnnotationUtil;

/**
 * <p>
 * Checks if a try/catch block has a fail assertion at the end of a try block in a JUnit test
 * method.
 * </p>
 * <p>
 * Rationale: Tests should not complete the try block naturally if they are expecting a failure.
 * If the try completes normally the test will pass successfully and skip over any assertions in
 * the catch block.
 * If tests are not expecting exceptions, then they should remove the catch block and propagate
 * the exception to the JUnit caller which will display the full exception to the user.
 * </p>
 * <p>
 * A JUnit test method is identified by the annotations placed on it. It is only considered a JUnit
 * method if it contains the annotation 'org.junit.Test'. This check doesn't examine methods called
 * by a test method. It must contain the annotation.
 * Failures are identified by a method call to either:
 * </p>
 * <ul>
 * <li>
 * <a href="https://truth.dev/api/latest/com/google/common/truth/StandardSubjectBuilder.html#fail()">
 * com.google.common.truth.Truth#assert_.fail,
 * com.google.common.truth.Truth#assertWithMessage.fail</a>
 * <li>
 * <a href="http://junit.sourceforge.net/junit3.8.1/javadoc/junit/framework/Assert.html#fail()">
 * junit.framework.Assert#fail</a>
 * <li>
 * <a href="https://www.javadoc.io/static/org.assertj/assertj-core/3.19.0/org/assertj/core/api/Assertions.html#fail(java.lang.String)">
 * org.assertj.core.api.Assertions#fail</a>
 * <li>
 * <a href="https://www.javadoc.io/static/org.assertj/assertj-core/3.19.0/org/assertj/core/api/Assertions.html#failBecauseExceptionWasNotThrown(java.lang.Class)">
 * org.assertj.core.api.Assertions#failBecauseExceptionWasNotThrown</a>
 * <li>
 * <a href="https://junit.org/junit4/javadoc/latest/org/junit/Assert.html#fail()">
 * org.junit.Assert#fail</a>
 * <li>
 * <a href="https://junit.org/junit5/docs/current/api/org.junit.jupiter.api/org/junit/jupiter/api/Assertions.html#fail()">
 * org.junit.jupiter.api.Assertions#fail</a>
 * </ul>
 * <p>
 * An example of how to configure the check is:
 * </p>
 * <pre>
 * &lt;module name="RequireFailForTryCatchInJunitCheck"/&gt;
 * </pre>
 * <p>
 * which will cause a violation in the example below:
 * </p>
 * <pre>
 *   &#064;Test
 *   public void testMyCase() {
 *     try { // &lt;-- violation here because try block misses fail assertion.
 *       verifySomeResult(); // &lt;-- add e.g. 'Assert.fail()' after this last statement
 *                           //     of the try block to resolve the violation
 *     }
 *     catch (IllegalArgumentException ex) {
 *       assertEquals("expected exception message",
 *         "Some message that is expected", ex.getMessage());
 *     }
 *   }
 * </pre>
 *
 * @author Richard Veach
 * @author Sebastian Thomschke
 * @since 1.25.0
 */
public class RequireFailForTryCatchInJunitCheck extends AbstractCheck {

    /**
     * Violation message key.
     */
    public static final String MSG_KEY = "require.fail";

    /**
     * Fully qualified junit test annotation.
     */
    private static final List<String> FQ_JUNIT_TESTS = Arrays.asList(
        "org.junit.Test",
        "org.junit.jupiter.api.Test");

    /**
     * Fully qualified identifier of methods whose call would satisfies this check.
     */
    private static final Set<String> FAIL_METHODS = new HashSet<>(Arrays.asList(
        "com.google.common.truth.Truth#assert_.fail",
        "com.google.common.truth.Truth#assertWithMessage.fail",
        "junit.framework.Assert#fail",
        "org.assertj.core.api.Assertions#fail",
        "org.assertj.core.api.Assertions#failBecauseExceptionWasNotThrown",
        "org.junit.Assert#fail",
        "org.junit.jupiter.api.Assertions#fail"));

    /**
     * Lookup map to determine which methods were imported based on a import.<br>
     * <br>
     * <b>Key:</b> FQ name of assertion class<br>
     * <b>Value:</b> List of fail method names prefixed by simple class name<br>
     * <br>
     * Example:
     * <pre>
     * {
     *    "org.assertj.core.api.Assertions": [
     *      "Assertions.fail",
     *      "Assertions.failBecauseExceptionWasNotThrown"
     *    ],
     *    "org.junit.Assert": [
     *      "Assert.fail"
     *    ]
     * }
     *  </pre>
     */
    private static final Map<String, List<String>> FAIL_METHOD_CALLS_BY_IMPORT =
        new HashMap<>();

    /**
     * Lookup map to determine which methods were imported based on a static import.<br>
     * <br>
     * <b>Key:</b> static import value<br>
     * <b>Value:</b> List of method names<br>
     * <br>
     * Example:
     * <pre>
     * {
     *    "org.assertj.core.api.Assertions.*": [
     *      "fail",
     *      "failBecauseExceptionWasNotThrown"
     *    ],
     *    "org.assertj.core.api.Assertions.fail": [
     *      "Assertions.fail"
     *    ],
     *    "org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown": [
     *      "failBecauseExceptionWasNotThrown"
     *    ],
     * }
     * </pre>
     */
    private static final Map<String, List<String>> FAIL_METHOD_CALLS_BY_STATIC_IMPORT =
        new HashMap<>();

    /**
     * Reusable char to satisfy MultipleStringLiterals check.
     */
    private static final char CHAR_DOT = '.';

    /**
     * Reusable char to satisfy MultipleStringLiterals check.
     */
    private static final char CHAR_HASH = '#';

    /**
     * Same as FAIL_METHODS but with '#' replaced by '.'.
     */
    private static final Set<String> FAIL_METHODS_WITH_DOTS = FAIL_METHODS.stream()
        // org.junit.Assert#fail to org.junit.Assert.fail
        .map(entry -> entry.replace(CHAR_HASH, CHAR_DOT))
        .collect(Collectors.toSet());

    static {
        for (final String failMethod : FAIL_METHODS) {
            final int hashPos = failMethod.lastIndexOf(CHAR_HASH);
            final String failMethodName =
                failMethod.substring(hashPos + 1, failMethod.length());
            final String failClassName = failMethod.substring(0, hashPos);
            final int lastDotPos = failClassName.lastIndexOf(CHAR_DOT);
            final String failClassSimpleName =
                failClassName.substring(lastDotPos + 1, failClassName.length());

            // when "import org.junit.jupiter.api.Assertions" -> accept "Assertions.fail()"
            FAIL_METHOD_CALLS_BY_IMPORT
                .computeIfAbsent(failClassName, key -> new ArrayList<>())
                .add(failClassSimpleName + CHAR_DOT + failMethodName);

            // when "import static org.junit.Assert.*" -> accept "fail()"
            FAIL_METHOD_CALLS_BY_STATIC_IMPORT
                .computeIfAbsent(failClassName + ".*", key -> new ArrayList<>())
                .add(failMethodName);

            // when "import static org.junit.Assert.fail" -> accept "fail()"
            // when "import static com.google.common.truth.Truth#assertWithMessage"
            //      -> accept "assertWithMessage.fail()"
            FAIL_METHOD_CALLS_BY_STATIC_IMPORT
                .computeIfAbsent(failClassName + CHAR_DOT
                            + getMethodNameForStaticImport(failMethodName),
                    key -> new ArrayList<>())
                .add(failMethodName);
        }
    }

    /**
     * List of method invocations computed based on the actual class imports that satisfy
     * this check.
     */
    private final Set<String> acceptedFailMethodCalls = new HashSet<>(FAIL_METHODS_WITH_DOTS);

    /**
     * {@code true} if the junit test is imported.
     */
    private boolean importTest;

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
            TokenTypes.IMPORT,
            TokenTypes.STATIC_IMPORT,
            TokenTypes.LITERAL_TRY,
        };
    }

    @Override
    public void beginTree(DetailAST rootAST) {
        importTest = false;
    }

    @Override
    public void visitToken(DetailAST ast) {
        switch (ast.getType()) {
            case TokenTypes.IMPORT:
                final String imprt = getImportText(ast);

                if (FQ_JUNIT_TESTS.contains(imprt)) {
                    importTest = true;
                }
                else {
                    final List<String> failMethodCalls = FAIL_METHOD_CALLS_BY_IMPORT.get(imprt);
                    if (failMethodCalls != null) {
                        acceptedFailMethodCalls.addAll(failMethodCalls);
                    }
                }

                break;
            case TokenTypes.STATIC_IMPORT:
                final String staticImprt = getImportText(ast);

                final List<String> failMethodCall =
                    FAIL_METHOD_CALLS_BY_STATIC_IMPORT.get(staticImprt);
                if (failMethodCall != null) {
                    acceptedFailMethodCalls.addAll(failMethodCall);
                }

                break;
            case TokenTypes.LITERAL_TRY:
                examineTry(ast);
                break;
            default:
                SevntuUtil.reportInvalidToken(ast.getType());
                break;
        }
    }

    /**
     * Examines the try block for violations.
     *
     * @param ast The try block to examine.
     */
    private void examineTry(DetailAST ast) {
        final DetailAST method = getMethod(ast);

        if (isTestMethod(method) && ast.findFirstToken(TokenTypes.LITERAL_CATCH) != null) {

            final DetailAST last = ast.findFirstToken(TokenTypes.SLIST)
                .getLastChild()
                .getPreviousSibling();

            final boolean isValid;
            if (last == null) {
                isValid = false;
            }
            else {
                switch (last.getType()) {
                    case TokenTypes.LITERAL_THROW:
                        // if the last statement within a try-catch block is a throw statement,
                        // we do not need a fail() assertion
                        isValid = true;
                        break;
                    case TokenTypes.SEMI:
                        isValid = isValidFail(last.getPreviousSibling());
                        break;
                    default:
                        isValid = false;
                        break;
                }
            }

            if (!isValid) {
                // report check violation
                log(ast, MSG_KEY);
            }
        }
    }

    /**
     * Checks if the given method is a test method, defined by the junit annotation Test.
     *
     * @param method the method AST to examine.
     * @return {@code true} if the method is a test method.
     */
    private boolean isTestMethod(DetailAST method) {
        boolean result = false;
        if (method != null) {
            result = importTest && AnnotationUtil.containsAnnotation(method, "Test");
            for (int index = 0; !result && index < FQ_JUNIT_TESTS.size(); index++) {
                result = AnnotationUtil.containsAnnotation(method, FQ_JUNIT_TESTS.get(index));
            }
        }
        return result;
    }

    /**
     * Checks if the expression is an junit fail assertion.
     *
     * @param expression The expression to examine.
     * @return {@code true} if the expression is a valid junit fail assertion.
     */
    private boolean isValidFail(DetailAST expression) {
        boolean result = false;

        if (expression.getFirstChild().getType() == TokenTypes.METHOD_CALL) {
            final DetailAST ident = expression.getFirstChild().getFirstChild();

            final String methodCall;
            if (ident.getType() == TokenTypes.IDENT) {
                // e.g. fail("");
                methodCall = ident.getText();
            }
            else {
                DetailAST identChild = ident.getFirstChild();
                if (identChild.getType() == TokenTypes.METHOD_CALL) {
                    // e.g. Truth.assert_().withMessage("...").fail()
                    identChild = getLastMethodInChain(identChild);
                    methodCall = FullIdent.createFullIdent(identChild.getFirstChild()).getText()
                        + "."
                        + FullIdent.createFullIdent(ident.getLastChild()).getText();
                }
                else {
                    // e.g. Assert.fail("");
                    methodCall = FullIdent.createFullIdent(ident).getText();
                }
            }
            result = acceptedFailMethodCalls.contains(methodCall);
        }

        return result;
    }

    /**
     * Returns the last method in the method chain.
     * For {@code Truth.assert_().withMessage().withMessage().fail()} this will be {@code fail}.
     *
     * @param methodCall the method call AST
     * @return the AST of the last method call in the chain
     */
    private static DetailAST getLastMethodInChain(DetailAST methodCall) {
        DetailAST result = methodCall;
        DetailAST child = result.getFirstChild();
        while (child.getType() == TokenTypes.DOT
                && child.getFirstChild().getType() == TokenTypes.METHOD_CALL) {
            result = child.getFirstChild();
            child = result.getFirstChild();
        }
        return result;
    }

    /**
     * Retrieves the method definition AST parent from the specified node, as long as it doesn't
     * contain a lambda.
     *
     * @param node The node to examine.
     * @return The parent method definition.
     */
    private static DetailAST getMethod(DetailAST node) {
        DetailAST result = null;

        for (DetailAST token = node.getParent(); token != null; token = token.getParent()) {
            final int type = token.getType();
            if (type == TokenTypes.METHOD_DEF) {
                result = token;
            }
            if (type == TokenTypes.METHOD_DEF || type == TokenTypes.LAMBDA) {
                break;
            }
        }

        return result;
    }

    /**
     * Returns import text.
     *
     * @param ast ast node that represents import
     * @return String that represents importing class
     */
    private static String getImportText(DetailAST ast) {
        final FullIdent imp;
        if (ast.getType() == TokenTypes.IMPORT) {
            imp = FullIdent.createFullIdentBelow(ast);
        }
        else {
            imp = FullIdent.createFullIdent(ast.getFirstChild().getNextSibling());
        }
        return imp.getText();
    }

    /**
     * Returns the method name to bind with static imports.
     * For the regular methods like {@code junit.framework.Assert#fail}
     * the result will be {@code fail}. For the chained methods like
     * {@code com.google.common.truth.Truth#assert_.fail} the result will be {@code assert_}.
     *
     * @param methodName the accepted method(s) name
     * @return the method name for static imports
     */
    private static String getMethodNameForStaticImport(String methodName) {
        final String result;
        final int dotPos = methodName.indexOf(CHAR_DOT);
        if (dotPos < 0) {
            result = methodName;
        }
        else {
            result = methodName.substring(0, dotPos);
        }
        return result;
    }

}
