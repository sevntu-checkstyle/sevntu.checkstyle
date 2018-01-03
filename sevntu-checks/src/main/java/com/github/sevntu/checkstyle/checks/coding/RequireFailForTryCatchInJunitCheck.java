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

import com.github.sevntu.checkstyle.Utils;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.AnnotationUtility;

/**
 * <p>
 * Checks if a try/catch block has a junit fail assertion inside the try for a junit method.
 * </p>
 * <p>
 * Rationale: Tests should not complete the try block naturally if they are expecting a failure.
 * If the try completes normally the test will pass successfully and skip over any assertions in
 * the catch block.
 * If tests are not expecting exceptions, then they should remove the catch block and propagate
 * the exception to the junit caller which will display the full exception to the user.
 * </p>
 * <p>
 * A junit test method is identified by the annotations placed on it. It is only considered a junit
 * method if it contains the annotation 'org.junit.Test'. This check doesn't examine methods called
 * by a test method. It must contain the annotation. Failures are identified by the
 * method call to the method 'org.junit.Assert.fail'.
 * </p>
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
 *     try { // violation here as try block has no 'Assert.fail()'.
 *       verifySomeResult();
 *     }
 *     catch (IllegalArgumentException ex) {
 *       assertEquals("expected exception message",
 *           "Some message that is expected", ex.getMessage());
 *     }
 *   }
 * </pre>
 * @author Richard Veach
 */
public class RequireFailForTryCatchInJunitCheck extends AbstractCheck {

    /**
     * Violation message key.
     */
    public static final String MSG_KEY = "require.fail";

    /**
     * Fully qualified junit test annotation.
     */
    private static final String FQ_JUNIT_TEST = "org.junit.Test";
    /**
     * JUnit's fail assertion method name.
     */
    private static final String FAIL = "fail";

    /**
     * {@code true} if the junit test is imported.
     */
    private boolean importTest;
    /**
     * {@code true} if the junit assert is imported.
     */
    private boolean importAssert;
    /**
     * {@code true} if the junit fail assertion method is statically imported.
     */
    private boolean importStaticFail;

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
        importAssert = false;
        importStaticFail = false;
    }

    @Override
    public void visitToken(DetailAST ast) {
        switch (ast.getType()) {
            case TokenTypes.IMPORT:
                final String imprt = getImportText(ast);

                if (FQ_JUNIT_TEST.equals(imprt)) {
                    importTest = true;
                }
                if ("org.junit.Assert".equals(imprt)) {
                    importAssert = true;
                }
                break;
            case TokenTypes.STATIC_IMPORT:
                final String staticImprt = getImportText(ast);

                if ("org.junit.Assert.fail".equals(staticImprt)) {
                    importStaticFail = true;
                }
                break;
            case TokenTypes.LITERAL_TRY:
                examineTry(ast);
                break;
            default:
                Utils.reportInvalidToken(ast.getType());
                break;
        }
    }

    /**
     * Examines the try block for violations.
     * @param ast The try block to examine.
     */
    private void examineTry(DetailAST ast) {
        final DetailAST method = getMethod(ast);

        if (isTestMethod(method)
                && ast.findFirstToken(TokenTypes.LITERAL_CATCH) != null) {
            final DetailAST last = ast.findFirstToken(TokenTypes.SLIST).getLastChild()
                    .getPreviousSibling();

            if (last == null
                    || last.getType() != TokenTypes.SEMI
                    || !isValidFail(last.getPreviousSibling())) {
                log(ast, MSG_KEY);
            }
        }
    }

    /**
     * Checks if the given method is a test method, defined by the junit annotation Test.
     * @param method the method AST to examine.
     * @return {@code true} if the method is a test method.
     */
    private boolean isTestMethod(DetailAST method) {
        return method != null
            && ((importTest && AnnotationUtility.containsAnnotation(method, "Test"))
                    || AnnotationUtility.containsAnnotation(method, FQ_JUNIT_TEST));
    }

    /**
     * Checks if the expression is an junit fail assertion.
     * @param expression The expression to examine.
     * @return {@code true} if the expression is a valid junit fail assertion.
     */
    private boolean isValidFail(DetailAST expression) {
        boolean result = false;

        if (expression.getFirstChild().getType() == TokenTypes.METHOD_CALL) {
            final DetailAST ident = expression.getFirstChild().getFirstChild();

            if (importAssert && ident.getType() == TokenTypes.DOT) {
                final DetailAST firstChild = ident.getFirstChild();

                result = "Assert".equals(firstChild.getText())
                        && FAIL.equals(firstChild.getNextSibling().getText());
            }
            else if (importStaticFail) {
                result = FAIL.equals(ident.getText());
            }
        }

        return result;
    }

    /**
     * Retrieves the method definition AST parent from the specified node, as long as it doesn't
     * contain a lambda.
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

}
