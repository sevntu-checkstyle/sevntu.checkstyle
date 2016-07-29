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

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * Checks if enum constant contains optional trailing comma.
 * </p>
 * <p>
 * Rationale: Putting this comma in make is easier to change the
 * order of the elements or add new elements on the end.
 * </p>
 * <p>
 * An example of how to configure the check is:
 * </p>
 * <pre>
 * &lt;module name="EnumTrailingComma"/&gt;
 * </pre>
 *
 * @author <a href="kariem.hussein@gmail.com">Kariem Hussein</a>
 */
public class EnumTrailingCommaCheck extends AbstractCheck {

    /** Key for warning message text in "messages.properties" file. */
    public static final String MSG_KEY = "enum.trailing.comma";
    /** Key for warning message text in "messages.properties" file. */
    public static final String MSG_KEY_SEMI = "enum.trailing.comma.semi";

    @Override
    public int[] getDefaultTokens() {
        return getAcceptableTokens();
    }

    @Override
    public int[] getAcceptableTokens() {
        return new int[]{TokenTypes.ENUM_DEF};
    }

    @Override
    public int[] getRequiredTokens() {
        return getAcceptableTokens();
    }

    @Override
    public void visitToken(DetailAST enumDef) {
        final DetailAST enumConstBlock = enumDef.findFirstToken(TokenTypes.OBJBLOCK);

        final DetailAST enumConstLeft = enumConstBlock.findFirstToken(TokenTypes.LCURLY);
        final DetailAST enumConstRight = enumConstBlock.findFirstToken(TokenTypes.RCURLY);

        // Only check, if block is multi-line and there are more than one enum constants
        if (enumConstLeft.getLineNo() != enumConstRight.getLineNo()
                && enumConstBlock.getChildCount(TokenTypes.ENUM_CONSTANT_DEF) > 1) {
            final DetailAST constant = enumConstBlock.findFirstToken(TokenTypes.ENUM_CONSTANT_DEF);
            final DetailAST lastComma = getLastComma(constant);

            final DetailAST next = lastComma.getNextSibling();
            final int type = next.getType();
            switch (type) {
                case TokenTypes.SEMI:
                    if (next.getLineNo() == lastComma.getLineNo()) {
                        // semi on the same line as last comma
                        log(next.getLineNo(), MSG_KEY_SEMI);
                    }
                    break;
                case TokenTypes.ENUM_CONSTANT_DEF:
                    log(next.getLineNo(), MSG_KEY);
                    break;
                default:
            }
        }
    }

    /**
     * Get the last comma in a series of siblings.
     *
     * @param start the first sibling
     * @return the AST containing the last comma
     */
    private DetailAST getLastComma(DetailAST start) {
        DetailAST comma = null;
        for (DetailAST ast = start; ast != null; ast = ast.getNextSibling()) {
            if (ast.getType() == TokenTypes.COMMA) {
                comma = ast;
            }
        }
        return comma;
    }
}
