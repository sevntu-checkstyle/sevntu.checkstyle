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
import java.util.List;
import java.util.regex.Pattern;

import com.github.sevntu.checkstyle.SevntuUtil;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * This Check warns on throwing anonymous exception.
 * </p>
 * Examples:
 * <pre>
 * catch (Exception e) {
 *        throw new RuntimeException()  { //WARNING
 *          //some code
 *     };
 * }
 * <br>
 * catch (Exception e) {
 *     RuntimeException run = new RuntimeException()  {
 *          //some code
 *     };
 *     throw run;  //WARNING
 * }
 * </pre> The distinguishing of <b>exception</b> types occurs by
 * analyzing variable's class's name.<br>
 * Check has an option which contains the regular expression for exception class name matching<br>
 * Default value is "^.*Exception" because usually exception type ends with suffix "Exception".<br>
 * Then, if we have an ObjBlock (distinguished by curly braces), it's anonymous<br>
 * exception definition. It could be defined in <b>throw</b> statement
 * immediately.<br>
 * In that case, after literal new, there would be an expression type finishing
 * with and ObjBlock.<br>
 * <br>
 *
 * @author <a href="mailto:nesterenko-aleksey@list.ru">Aleksey Nesterenko</a>
 * @author <a href="mailto:maxvetrenko2241@gmail.com">Max Vetrenko</a>
 * @since 1.11.0
 */
public class ForbidThrowAnonymousExceptionsCheck extends AbstractCheck {

    /**
     * Warning message key.
     */
    public static final String MSG_KEY = "forbid.throw.anonymous.exception";

    /** Regular expression of exception naming. */
    private static final String DEFAULT_EXCEPTION_CLASS_NAME_REGEX = "^.*Exception";

    /** List of anonymous exceptions to ignore. */
    private final List<String> anonymousExceptions = new ArrayList<>();

    /** User set expression for exception names. */
    private Pattern pattern = Pattern.compile(DEFAULT_EXCEPTION_CLASS_NAME_REGEX);

    /**
     * Setter for pattern.
     *
     * @param exceptionClassNameRegex The regular expression to set.
     */
    public void setExceptionClassNameRegex(String exceptionClassNameRegex) {
        pattern = Pattern.compile(exceptionClassNameRegex);
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.LITERAL_THROW,
            TokenTypes.VARIABLE_DEF,
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
    public void visitToken(DetailAST literalThrowOrVariableDefAst) {
        switch (literalThrowOrVariableDefAst.getType()) {
            case TokenTypes.LITERAL_THROW:
                identifyThrowingAnonymousException(literalThrowOrVariableDefAst);
                break;
            case TokenTypes.VARIABLE_DEF:
                lookForAnonymousExceptionDefinition(literalThrowOrVariableDefAst);
                break;
            default:
                SevntuUtil.reportInvalidToken(literalThrowOrVariableDefAst.getType());
                break;
        }
    }

    /**
     * Warns on throwing anonymous exception.
     *
     * @param throwDefAst The token to examine.
     */
    private void identifyThrowingAnonymousException(DetailAST throwDefAst) {
        final DetailAST throwingLiteralNewAst = getLiteralNew(throwDefAst);

        if (throwingLiteralNewAst != null
                && hasObjectBlock(throwingLiteralNewAst)) {
            log(throwDefAst, MSG_KEY);
        }
        else if (throwingLiteralNewAst == null) {
            final DetailAST throwingExceptionNameAst = getThrowingExceptionNameAst(throwDefAst
                    .getFirstChild());
            if (throwingExceptionNameAst != null
                    && anonymousExceptions.contains(throwingExceptionNameAst
                            .getText())) {
                log(throwDefAst, MSG_KEY);
            }
        }
    }

    /**
     * Analyzes variable definition for anonymous exception definition. if found
     * - adds it to list of anonymous exceptions
     *
     * @param variableDefAst The token to examine.
     */
    private void
            lookForAnonymousExceptionDefinition(DetailAST variableDefAst) {
        DetailAST variableLiteralNewAst = null;
        final DetailAST variableAssignment = variableDefAst.findFirstToken(TokenTypes.ASSIGN);
        if (variableAssignment != null && variableAssignment.getFirstChild() != null) {
            variableLiteralNewAst = getLiteralNew(variableAssignment);
        }

        final DetailAST variableNameAst = variableDefAst
                .findFirstToken(TokenTypes.TYPE).getNextSibling();
        if (isExceptionName(variableNameAst)) {
            final String exceptionName = variableNameAst.getText();

            if (anonymousExceptions.contains(exceptionName)) {
                anonymousExceptions.remove(exceptionName);
            }

            if (variableLiteralNewAst != null
                    && hasObjectBlock(variableLiteralNewAst)) {
                anonymousExceptions.add(exceptionName);
            }
        }
    }

    /**
     * Gets the literal new node from variable definition node or throw node.
     *
     * @param literalThrowOrVariableDefAst The token to examine.
     * @return the specified node.
     */
    private static DetailAST
            getLiteralNew(DetailAST literalThrowOrVariableDefAst) {
        return literalThrowOrVariableDefAst.getFirstChild().findFirstToken(
                TokenTypes.LITERAL_NEW);
    }

    /**
     * Retrieves the AST node which contains the name of throwing exception.
     *
     * @param expressionAst The token to examine.
     * @return the specified node.
     */
    private static DetailAST
            getThrowingExceptionNameAst(DetailAST expressionAst) {
        return expressionAst.findFirstToken(TokenTypes.IDENT);
    }

    /**
     * Checks if definition with a literal new has an ObjBlock.
     *
     * @param literalNewAst The token to examine.
     * @return true if the new has an object block.
     */
    private static boolean hasObjectBlock(DetailAST literalNewAst) {
        return literalNewAst.getLastChild().getType() == TokenTypes.OBJBLOCK;
    }

    /**
     * Checks if variable name is definitely an exception name. It is so if
     * variable type ends with "Exception" suffix
     *
     * @param variableNameAst The token to examine.
     * @return true if the name is an exception.
     */
    private boolean isExceptionName(DetailAST variableNameAst) {
        final DetailAST typeAst = variableNameAst.getPreviousSibling();
        final String typeName = typeAst.getFirstChild().getText();
        return pattern.matcher(typeName).matches();
    }

}
