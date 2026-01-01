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

import com.github.sevntu.checkstyle.SevntuUtil;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * Highlights usage of redundant returns inside constructors and methods with void
 * result.
 * </p>
 * <p>
 * For example:
 * </p>
 * <p>
 * 1. Non-empty constructor
 * </p>
 * <pre>
 *    public HelloWorld(){
 *        doStuff();
 *        return;
 *    }</pre>
 * <p>
 * 2. Method with void result
 * </p>
 * <pre>
 *    public void testMethod1(){
 *        doStuff();
 *        return;
 *    }</pre>
 * <p>
 * However, if your IDE does not support breakpoints on the method entry, you
 * can allow the use of redundant returns in constructors and methods with void
 * result without code except for 'return;'.
 * </p>
 * <p>
 * For example:
 * </p>
 * <p>
 * 1. Empty constructor
 * </p>
 * <pre>
 *    public HelloWorld(){
 *        return;
 *    }</pre>
 * <p>
 * 2. Method with void result and empty body
 * </p>
 * <pre>
 *    public void testMethod1(){
 *        return;
 *    }</pre>
 *
 * @author <a href="mailto:fishh1991@gmail.com">Troshin Sergey</a>
 * @author <a href="mailto:maxvetrenko2241@gmail.com">Max Vetrenko</a>
 * @author <a href="mailto:nesterenko-aleksey@list.ru">Alexey Nesterenko</a>
 * @since 1.8.0
 */
public class RedundantReturnCheck extends AbstractCheck {

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY = "redundant.return.check";

    /**
     *  If True, allow 'return' in empty constructors and methods that return void.
     */
    private boolean allowReturnInEmptyMethodsAndConstructors;

    /**
     * Setter for allowReturnInEmptyMethodsAndConstructors.
     *
     * @param allowEmptyBlocks allow 'return' in empty constructors and methods that return void.
     */
    public void setAllowReturnInEmptyMethodsAndConstructors(boolean allowEmptyBlocks) {
        allowReturnInEmptyMethodsAndConstructors = allowEmptyBlocks;
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.CTOR_DEF,
            TokenTypes.METHOD_DEF,
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
    public void visitToken(DetailAST ast) {
        final DetailAST blockAst = ast.getLastChild();

        switch (ast.getType()) {
            case TokenTypes.CTOR_DEF:
                if (!ignoreLonelyReturn(blockAst) && hasNonEmptyBody(ast)) {
                    final List<DetailAST> redundantReturns = getRedundantReturns(blockAst);
                    log(redundantReturns);
                }
                break;

            case TokenTypes.METHOD_DEF:
                if (!ignoreLonelyReturn(blockAst) && isVoidMethodWithNonEmptyBody(ast)) {
                    final List<DetailAST> redundantReturns = getRedundantReturns(blockAst);
                    log(redundantReturns);
                }
                break;

            default:
                SevntuUtil.reportInvalidToken(ast.getType());
                break;
        }
    }

    /**
     * Ignores method or constructor if it contains <b>only</b> return statement
     * in its body.
     *
     * @param objectBlockAst The token to examine.
     * @return true if the block can be ignored.
     */
    private boolean ignoreLonelyReturn(DetailAST objectBlockAst) {
        return allowReturnInEmptyMethodsAndConstructors
                    && objectBlockAst.getFirstChild() != null
                    && objectBlockAst.getFirstChild().getType()
                        == TokenTypes.LITERAL_RETURN;
    }

    /**
     * Checks if method's or ctor's body is not empty.
     *
     * @param defAst The token to examine.
     * @return true if body is not empty.
     */
    private static boolean hasNonEmptyBody(DetailAST defAst) {
        return defAst.getLastChild().getChildCount() > 1;
    }

    /**
     * Checks if method is void and has a body.
     *
     * @param methodDefAst The token to examine.
     * @return true if void method has non-empty body.
     */
    private static boolean isVoidMethodWithNonEmptyBody(DetailAST methodDefAst) {
        return methodDefAst.getLastChild().getType() == TokenTypes.SLIST
                    && methodDefAst.findFirstToken(TokenTypes.TYPE)
                        .findFirstToken(TokenTypes.LITERAL_VOID) != null
                    && hasNonEmptyBody(methodDefAst);
    }

    /**
     * Puts violation on each redundant return met in object block of
     * method or ctor.
     *
     * @param redundantReturnsAst The token to examine.
     */
    private void log(List<DetailAST> redundantReturnsAst) {
        for (DetailAST redundantLiteralReturnAst : redundantReturnsAst) {
            log(redundantLiteralReturnAst, MSG_KEY);
        }
    }

    /**
     * Returns the list of redundant returns found in method's or ctor's
     * object block.
     *
     * @param objectBlockAst
     *            - a method or constructor object block
     * @return list of redundant returns or empty list if none were found
     */
    private static List<DetailAST> getRedundantReturns(DetailAST objectBlockAst) {
        final List<DetailAST> redundantReturns = new ArrayList<>();

        final int placeForRedundantReturn = objectBlockAst
            .getLastChild().getPreviousSibling().getType();

        if (placeForRedundantReturn == TokenTypes.LITERAL_RETURN) {
            final DetailAST lastChildAst = objectBlockAst.getLastChild();

            final DetailAST redundantReturnAst = lastChildAst.getPreviousSibling();

            redundantReturns.add(redundantReturnAst);
        }
        else if (placeForRedundantReturn == TokenTypes.LITERAL_TRY
                && !getRedundantReturnsInTryCatchBlock(objectBlockAst
                    .findFirstToken(TokenTypes.LITERAL_TRY)).isEmpty()) {
            final List<DetailAST> redundantsAst = getRedundantReturnsInTryCatchBlock(objectBlockAst
                    .findFirstToken(TokenTypes.LITERAL_TRY));

            redundantReturns.addAll(redundantsAst);
        }

        return redundantReturns;
    }

    /**
     * Returns the list of redundant returns found in try, catch, finally object blocks.
     *
     * @param tryAst
     *            - Ast that contain a try node.
     * @return list of redundant returns or empty list if none were found
     */
    private static List<DetailAST> getRedundantReturnsInTryCatchBlock(DetailAST tryAst) {
        final List<DetailAST> redundantReturns = new ArrayList<>();

        final DetailAST tryBlockAst;

        if (tryAst.getFirstChild().getType() == TokenTypes.RESOURCE_SPECIFICATION) {
            tryBlockAst = tryAst.getFirstChild().getNextSibling();
        }
        else {
            tryBlockAst = tryAst.getFirstChild();
        }

        DetailAST redundantReturnAst =
            getRedundantReturnInBlock(tryBlockAst.getLastChild().getPreviousSibling());

        // if redundant return is in try block
        if (redundantReturnAst != null) {
            redundantReturns.add(redundantReturnAst);
        }

        DetailAST blockAst = tryBlockAst;

        // look for redundant returns in all catches
        for (DetailAST catchBlockAst = getNextCatchBlock(blockAst);
                catchBlockAst != null;
                        catchBlockAst = getNextCatchBlock(blockAst)) {
            final DetailAST lastStatementOfCatchBlockAst = catchBlockAst
                .getLastChild().getLastChild().getPreviousSibling();

            if (lastStatementOfCatchBlockAst != null) {
                redundantReturnAst = getRedundantReturnInBlock(lastStatementOfCatchBlockAst);

                if (redundantReturnAst != null) {
                    redundantReturns.add(redundantReturnAst);
                }
            }
            blockAst = blockAst.getNextSibling();
        }

        // if redundant return is in finally block
        if (blockAst.getNextSibling() != null) {
            final DetailAST afterCatchBlockAst = blockAst.getNextSibling().getLastChild()
                .getLastChild();

            redundantReturnAst = getRedundantReturnInBlock(afterCatchBlockAst.getPreviousSibling());

            if (redundantReturnAst != null) {
                redundantReturns.add(redundantReturnAst);
            }
        }

        return redundantReturns;
    }

    /**
     * Gets next catch block in try block if exists.
     *
     * @param blockAst The token to examine.
     * @return next found catchBlockAst, if no catch was found - returns null
     */
    private static DetailAST getNextCatchBlock(DetailAST blockAst) {
        DetailAST catchBlockAst = null;
        if (blockAst.getNextSibling() != null
                && blockAst.getNextSibling().getType() == TokenTypes.LITERAL_CATCH) {
            catchBlockAst = blockAst.getNextSibling();
        }
        return catchBlockAst;
    }

    /**
     * Returns redundant return from try-catch-finally block.
     *
     * @param statementAst
     *            - a place where the redundantReturn is expected.
     * @return redundant literal return if found, else null.
     */
    private static DetailAST getRedundantReturnInBlock(DetailAST statementAst) {
        DetailAST redundantReturnAst = null;

        if (statementAst != null) {
            if (statementAst.getType() == TokenTypes.LITERAL_RETURN) {
                redundantReturnAst = statementAst;
            }
            else {
                if (statementAst.getFirstChild() != null) {
                    final DetailAST foundRedundantReturnAst =
                            findRedundantReturnInCatch(statementAst);
                    if (foundRedundantReturnAst != null) {
                        redundantReturnAst = foundRedundantReturnAst;
                    }
                }
            }
        }

        return redundantReturnAst;
    }

    /**
     * Looks for literal return in the last statement of a catch block.
     *
     * @param lastStatementInCatchBlockAst The token to examine.
     * @return redundant literal return, if there's no one - returns null
     */
    private static DetailAST findRedundantReturnInCatch(DetailAST lastStatementInCatchBlockAst) {
        DetailAST redundantReturnAst = null;
        DetailAST returnAst;
        DetailAST toVisitAst = SevntuUtil.getNextSubTreeNode(lastStatementInCatchBlockAst,
                lastStatementInCatchBlockAst);

        while (toVisitAst != null) {
            if (toVisitAst.getType() == TokenTypes.OBJBLOCK) {
                while (toVisitAst.getNextSibling() == null) {
                    toVisitAst = toVisitAst.getParent();
                }
                toVisitAst = toVisitAst.getNextSibling();
            }
            else if (isFinalReturn(toVisitAst)) {
                returnAst = toVisitAst;

                while (toVisitAst != null
                        && toVisitAst.getParent() != lastStatementInCatchBlockAst.getLastChild()) {
                    toVisitAst = toVisitAst.getParent();
                }

                if (toVisitAst != null) {
                    redundantReturnAst = returnAst;
                }

                toVisitAst = returnAst;
            }

            toVisitAst = SevntuUtil.getNextSubTreeNode(toVisitAst, lastStatementInCatchBlockAst);
        }

        return redundantReturnAst;
    }

    /**
     * Checks if the {@code ast} is the final return statement.
     *
     * @param ast the AST to examine.
     * @return {@code true} if the {@code ast} is the final return statement.
     */
    private static boolean isFinalReturn(DetailAST ast) {
        return (ast.getParent().getParent().getNextSibling() == null
                    || ast.getParent().getParent().getNextSibling().getType()
                    == TokenTypes.RCURLY)
                && ast.getType() == TokenTypes.LITERAL_RETURN
                && ast.getParent().getNextSibling() == null;
    }

}
