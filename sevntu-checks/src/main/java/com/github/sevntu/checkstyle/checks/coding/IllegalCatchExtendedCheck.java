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

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Catching java.lang.Exception, java.lang.Error or java.lang.RuntimeException
 * is almost never acceptable.
 *
 * @author <a href="mailto:simon@redhillconsulting.com.au">Simon Harris</a>
 * @since 1.8.0
 */
public final class IllegalCatchExtendedCheck extends AbstractCheck {

    /**
     * Warning message key.
     */
    public static final String MSG_KEY = "illegal.catch";

    /** Illegal class names. */
    private final Set<String> illegalClassNames = Arrays.stream(
            new String[] {"Exception", "Error", "RuntimeException", "Throwable",
                          "java.lang.Error", "java.lang.Exception", "java.lang.RuntimeException",
                          "java.lang.Throwable", }).collect(Collectors.toSet());

    /**
     * Disable warnings for "catch" blocks containing
     * throwing an exception.
     */
    private boolean allowThrow = true;

    /**
     * Disable warnings for "catch" blocks containing
     * rethrowing an exception.
     */
    private boolean allowRethrow = true;

    /**
     * Set the list of illegal classes.
     *
     * @param classNames
     *            array of illegal exception classes
     */
    public void setIllegalClassNames(final String... classNames) {
        illegalClassNames.clear();
        for (final String name : classNames) {
            illegalClassNames.add(name);
            final int lastDot = name.lastIndexOf('.');
            if (lastDot > 0
                    && lastDot < name.length() - 1) {
                final String shortName = name
                        .substring(name.lastIndexOf('.') + 1);
                illegalClassNames.add(shortName);
            }
        }
    }

    /**
     * Enable(false) | Disable(true) warnings for "catch" blocks containing
     * throwing an exception.
     *
     * @param value Disable warning for throwing
     */
    public void setAllowThrow(final boolean value) {
        allowThrow = value;
    }

    /**
     * Enable(false) | Disable(true) warnings for "catch" blocks containing
     * rethrowing an exception.
     *
     * @param value Disable warnings for rethrowing
     */
    public void setAllowRethrow(final boolean value) {
        allowRethrow = value;
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.LITERAL_CATCH,
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
    public void visitToken(DetailAST detailAST) {
        final DetailAST paramDef = detailAST
                .findFirstToken(TokenTypes.PARAMETER_DEF);
        final DetailAST throwAST = getThrowAST(detailAST);

        DetailAST firstLvlChild = null;
        if (throwAST != null) {
            firstLvlChild = throwAST.getFirstChild();
        }

        DetailAST secondLvlChild = null;
        if (firstLvlChild != null) {
            secondLvlChild = firstLvlChild.getFirstChild();
        }

        // For warnings disable first lvl child must be an EXPR and
        // second lvl child must be IDENT or LITERAL_NEW with
        // appropriate boolean flags.
        final boolean noWarning = secondLvlChild != null
             && (allowThrow && secondLvlChild.getType() == TokenTypes.IDENT
             || allowRethrow && secondLvlChild.getType() == TokenTypes.LITERAL_NEW);

        final DetailAST excType = paramDef.findFirstToken(TokenTypes.TYPE);
        final FullIdent ident = FullIdent.createFullIdent(excType.getFirstChild());

        if (!noWarning && isIllegalClassName(ident.getText())) {
            log(detailAST, MSG_KEY, ident.getText());
        }
    }

    /**
     * Looking for the keyword "throw" among current (aParentAST) node childs.
     *
     * @param parentAST - the current parent node.
     * @return null if the "throw" keyword was not found
     *     or the LITERAL_THROW DetailAST otherwise
     */
    public DetailAST getThrowAST(DetailAST parentAST) {
        DetailAST result = null;
        final DetailAST[] asts = getChilds(parentAST);

        for (DetailAST currentNode : asts) {
            if (currentNode.getType() != TokenTypes.PARAMETER_DEF
                    && currentNode.getFirstChild() != null) {
                result = getThrowAST(currentNode);
            }
            if (currentNode.getType() == TokenTypes.LITERAL_THROW) {
                result = currentNode;
            }
            if (result != null) {
                break;
            }
        }
        return result;
    }

    /**
     * Gets all the children one level below on the current top node.
     *
     * @param node - current parent node.
     * @return an array of childs one level below
     *     on the current parent node aNode. */
    private static DetailAST[] getChilds(DetailAST node) {
        final DetailAST[] result = new DetailAST[node.getChildCount()];

        DetailAST currNode = node.getFirstChild();

        for (int index = 0; index < result.length; index++) {
            result[index] = currNode;
            currNode = currNode.getNextSibling();
        }

        return result;
    }

    /**
     * Checks if given class is illegal.
     *
     * @param ident
     *            ident to check.
     * @return true if given ident is illegal.
     */
    protected boolean isIllegalClassName(final String ident) {
        return illegalClassNames.contains(ident);
    }

}
