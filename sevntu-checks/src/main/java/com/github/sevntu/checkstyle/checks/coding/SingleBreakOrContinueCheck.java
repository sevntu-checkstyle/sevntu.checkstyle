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

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * This check restricts the number of break and continue statements inside cycle body (only one is
 * allowed).
 * </p>
 * <p>
 * Restricting the number of break and continue statements in a loop is done in the interest of good
 * structured programming.
 * </p>
 * <p>
 * One break and continue statement is acceptable in a loop, since it facilitates optimal coding. If
 * there is more than one, the code should be refactored to increase readability.
 * </p>
 * <p>
 * For example: (http://nemo.sonarqube.org/coding_rules#languages=java|q=one%20break)
 * </p>
 *
 * <pre>
 * for (int i = 1; i &lt;= 10; i++)
 * { // violation - 2 continue - one might be tempted to add some logic in between
 *     if (i % 2 == 0)
 *     {
 *         continue;
 *     }
 *
 *     if (i % 3 == 0)
 *     {
 *         continue;
 *     }
 *
 *     System.out.println("i = " + i);
 * }
 * </pre>
 * <p>
 * Please note that Switch statements and inner loops are <em>ignored</em> in this check. This Rule
 * only validate loop structure with depth 0.
 * </p>
 * <p>
 * For example:
 * </p>
 *
 * <pre>
 * for (int i = 1; i &lt;= 10; i++)// OK - Outer loop
 * {
 *     while (true) // violation - Inner loop: 1 continue and 1 break
 *     {
 *         if (true)
 *         {
 *             continue;
 *         }
 *
 *         if (true)
 *         {
 *             break;
 *         }
 *
 *         System.out.println("violation - 1 continue and 1 break");
 *     }
 * }
 * </pre>
 *
 * <pre>
 *  while (true) // OK - Switch block
 *  {
 *      final char chr = value.charAt(i);
 *      switch (chr) {
 *      case '&lt;':
 *          sb.append("&lt;");
 *          break;
 *      case '&gt;':
 *          sb.append("&gt;");
 *          break;
 *      case '\"':
 *          sb.append("&quot;");
 *          break;
 *      case '&amp;':
 *          sb.append(chr);
 *          break;
 *      default:
 *          sb.append(chr);
 *          break;
 *      }
 *  }
 * </pre>
 *
 * @author <a href="mailto:yasser.aziza@gmail.com">Yasser Aziza</a>
 * @since 1.18.0
 */
public class SingleBreakOrContinueCheck extends AbstractCheck {

    /**
     * Warning message key.
     */
    public static final String MSG_KEY = "single.break.or.continue.in.loops";

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.LITERAL_FOR,
            TokenTypes.LITERAL_WHILE,
            TokenTypes.LITERAL_DO, };
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
        if (getNumberOfContinueAndBreaks(ast.getFirstChild()) > 1) {
            log(ast, MSG_KEY);
        }
    }

    /**
     * Gets the number of "continue" and "break" statements inside a loop.
     *
     * @param node current parent node.
     * @return number of break and continue statements inside a loop
     */
    private int getNumberOfContinueAndBreaks(DetailAST node) {
        int numberOfBreakOrContinue = 0;

        if (node != null) {
            if (TokenTypes.LITERAL_CONTINUE == node.getType()
                    || TokenTypes.LITERAL_BREAK == node.getType()) {
                numberOfBreakOrContinue++;
            }
            else if (shouldIgnore(node)) {
                numberOfBreakOrContinue += getNumberOfContinueAndBreaks(node.getNextSibling());
            }
            else {
                numberOfBreakOrContinue += getNumberOfContinueAndBreaks(node.getFirstChild());
                numberOfBreakOrContinue += getNumberOfContinueAndBreaks(node.getNextSibling());
            }
        }

        return numberOfBreakOrContinue;
    }

    /**
     * Either a node should be ignored while counting the number of "break" and "continue"
     * statements. This check is needed in order to e.g. ignore Switch statements and inner loops.
     *
     * @param node current node.
     * @return <code>true</code> if the node should be ignored, otherwise <code>false</code>
     */
    private static boolean shouldIgnore(DetailAST node) {
        return TokenTypes.LITERAL_SWITCH == node.getType()
                || TokenTypes.LITERAL_FOR == node.getType()
                || TokenTypes.LITERAL_WHILE == node.getType()
                || TokenTypes.LITERAL_DO == node.getType();
    }

}
