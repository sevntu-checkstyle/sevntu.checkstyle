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

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Checks if enum constant contains an optional trailing comma.
 *
 * <p>Rationale: Putting this comma in makes is easier to change the order of the elements or add
 * new elements at the end of the list. This is similar to
 * <a href="https://checkstyle.org/config_coding.html#ArrayTrailingComma">ArrayTrailingComma</a>.
 *
 * <p>The following is a normal enum type declaration:
 * <pre>
 * enum Type {
 *     ALPHA,
 *     BETA,
 *     GAMMA
 * }
 * </pre>
 *
 * <p>However, if you want to append something to the list, you would need to change the line
 * containing the last enum constant:
 *
 * <pre>
 * enum Type {
 *     ALPHA,
 *     BETA,
 *     GAMMA,   // changed due to the ','
 *     DELTA    // new line
 * }
 * </pre>
 *
 * <p>This check makes sure that also the last enum constant has a trailing comma, which is
 * valid according to the Java Spec (see <a
 * href="http://docs.oracle.com/javase/specs/jls/se8/html/jls-8.html#jls-8.9.1">Enum Constants</a>)
 *
 * <pre>
 * enum Type {
 *     ALPHA,
 *     BETA,
 *     GAMMA,
 *     DELTA, // removing this comma will result in a violation with the check activated
 * }
 * </pre>
 *
 * <p>However, you could also add a semicolon behind that comma on the same line, which would raise
 * a violation
 *
 * <pre>
 * enum Type {
 *     ALPHA,
 *     BETA,
 *     GAMMA,
 *     DELTA,; // violation
 * }
 * </pre>
 * In this case the semicolon should be removed. However, if there is more in the enum body, the
 * semicolon should be placed on a line by itself.
 *
 * <p>An example of how to configure the check is:
 * <pre>
 * &lt;module name="EnumTrailingComma"/&gt;
 * </pre>
 *
 * <p>Please note that using this check together with {@code NoWhitespaceBefore} or
 * {@code SeparatorWrap} may create conflicts with enums that contain a body:
 * this check enforces the semicolon on a separate line while {@code NoWhiteSpaceBefore} does
 * not allow the semicolon to be preceded with whitespace and {@code SeparatorWrap} expects the
 * semicolon to be on the same line as the last enum constant.
 *
 * @author <a href="kariem.hussein@gmail.com">Kariem Hussein</a>
 */
public class EnumTrailingCommaCheck extends AbstractCheck {

    /** Key for warning message text in "messages.properties" file. */
    public static final String MSG_KEY = "enum.trailing.comma";

    @Override
    public int[] getDefaultTokens() {
        return getAcceptableTokens();
    }

    @Override
    public int[] getAcceptableTokens() {
        return new int[] {TokenTypes.ENUM_DEF};
    }

    @Override
    public int[] getRequiredTokens() {
        return getAcceptableTokens();
    }

    @Override
    public void visitToken(DetailAST enumDef) {
        final DetailAST enumConstBlock = enumDef.findFirstToken(TokenTypes.OBJBLOCK);

        final DetailAST enumConstLeft = enumConstBlock.findFirstToken(TokenTypes.LCURLY);

        DetailAST enumConstEnd = enumConstBlock.findFirstToken(TokenTypes.SEMI);
        if (enumConstEnd == null) {
            enumConstEnd = enumConstBlock.findFirstToken(TokenTypes.RCURLY);
        }

        // Only check, if block is multi-line and there are more than one enum constants
        if (enumConstLeft.getLineNo() != enumConstEnd.getLineNo()
                && enumConstBlock.getChildCount(TokenTypes.ENUM_CONSTANT_DEF) > 1) {

            final DetailAST lastSibling = enumConstEnd.getPreviousSibling();
            if (lastSibling.getType() != TokenTypes.COMMA) {
                // constant definition does not end in a comma
                log(lastSibling, MSG_KEY);
            }
            else if (enumConstEnd.getType() == TokenTypes.SEMI
                    && enumConstEnd.getLineNo() == lastSibling.getLineNo()) {
                // constant definition ends in a comma, but is followed by semi on same line
                log(enumConstEnd, MSG_KEY);
            }
        }
    }

}
