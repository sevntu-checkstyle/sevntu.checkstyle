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

package com.github.sevntu.checkstyle.checks.coding;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import com.github.sevntu.checkstyle.Utils;
import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * This Check warns on throwing anonymous exception.<br>
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
 * @author <a href="mailto:nesterenko-aleksey@list.ru">Aleksey Nesterenko</a>
 * @author <a href="mailto:maxvetrenko2241@gmail.com">Max Vetrenko</a>
 */
public class ForbidThrowAnonymousExceptionsCheck extends Check
{
    /**
     * Warning message key.
     */
    public static final String MSG_KEY = "forbid.throw.anonymous.exception";

    private static final String DEFAULT_EXCEPTION_CLASS_NAME_REGEX = "^.*Exception";

    private Pattern pattern = Pattern.compile(DEFAULT_EXCEPTION_CLASS_NAME_REGEX);

    private List<String> anonymousExceptions = new ArrayList<String>();

    public void setExceptionClassNameRegex(String exceptionClassNameRegex) {
        this.pattern = Pattern.compile(exceptionClassNameRegex);
    }

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] { TokenTypes.LITERAL_THROW, TokenTypes.VARIABLE_DEF };
    }

    @Override
    public void visitToken(DetailAST literalThrowOrVariableDefAst)
    {
        switch (literalThrowOrVariableDefAst.getType()) {
            case TokenTypes.LITERAL_THROW:
                identifyThrowingAnonymousException(literalThrowOrVariableDefAst);
                break;
            case TokenTypes.VARIABLE_DEF:
                lookForAnonymousExceptionDefinition(literalThrowOrVariableDefAst);
                break;
            default:
                Utils.reportInvalidToken(literalThrowOrVariableDefAst.getType());
                break;
        }
    }

    /**
     * Warns on throwing anonymous exception
     */
    private void
            identifyThrowingAnonymousException(DetailAST throwDefAst)
    {
        DetailAST throwingLiteralNewAst = getLiteralNew(throwDefAst);

        if (throwingLiteralNewAst != null
                && hasObjectBlock(throwingLiteralNewAst)) {
            log(throwDefAst.getLineNo(), MSG_KEY);
        }

        else if (throwingLiteralNewAst == null) {
            DetailAST throwingExceptionNameAst = getThrowingExceptionNameAst(throwDefAst
                    .getFirstChild());
            if (throwingExceptionNameAst != null
                    && anonymousExceptions.contains(throwingExceptionNameAst
                            .getText())) {
                log(throwDefAst.getLineNo(), MSG_KEY);
            }
        }
    }

    /**
     * Analyzes variable definition for anonymous exception definition, if found
     * - adds it to list of anonymous exceptions
     */
    private void
            lookForAnonymousExceptionDefinition(DetailAST variableDefAst)
    {
        DetailAST variableLiteralNewAst = null;
        DetailAST variableAssignment = variableDefAst.findFirstToken(TokenTypes.ASSIGN);
        if (variableAssignment != null && variableAssignment.getFirstChild() != null) {
            variableLiteralNewAst = getLiteralNew(variableAssignment);
        }

        DetailAST variableNameAst = variableDefAst
                .findFirstToken(TokenTypes.TYPE).getNextSibling();
        if (isExceptionName(variableNameAst)) {
            String exceptionName = variableNameAst.getText();

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
     * Gets the literal new node from variable definition node or throw node
     */
    private static DetailAST
            getLiteralNew(DetailAST literalThrowOrVariableDefAst)
    {
        return literalThrowOrVariableDefAst.getFirstChild().findFirstToken(
                TokenTypes.LITERAL_NEW);
    }

    /**
     * Retrieves the AST node which contains the name of throwing exception
     */
    private static DetailAST
            getThrowingExceptionNameAst(DetailAST expressionAst)
    {
        return expressionAst.findFirstToken(TokenTypes.IDENT);
    }

    /**
     * Checks if definition with a literal new has an ObjBlock
     */
    private static boolean hasObjectBlock(DetailAST literalNewAst)
    {
        return literalNewAst.getLastChild().getType() == TokenTypes.OBJBLOCK;
    }

    /**
     * Checks if variable name is definitely an exception name. It is so if
     * variable type ends with "Exception" suffix
     */
    private boolean isExceptionName(DetailAST variableNameAst)
    {
        DetailAST typeAst = variableNameAst.getPreviousSibling();
        String typeName = typeAst.getFirstChild().getText();
        return pattern.matcher(typeName).matches();
    }

}
