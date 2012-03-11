////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2011  Oliver Burn
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

package com.puppycrawl.tools.checkstyle.checks.coding;

import java.util.regex.Pattern;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Forbids certain imports usage in class. <br>
 * Idea was taken from the similar check in Sonar project. <br>
 * <br>
 * Parameters:
 * <ol>
 * <li>Package regexp;</li>
 * <li>Forbidden imports regexp.</li>
 * </ol>
 * Check operates with packages/imports text in string representation without
 * "package"/"import" words and semicolons. You can be sure to write any regular
 * expressions for package/imports text filtering. <br>
 * <br>
 * Example of usage: Forbid to use "*.ui.*" packages in "*.dao.*" packages: <br>
 * <br>
 * <dl>
 * <li>Package name regexp = ".+.ui..+"</li>
 * <li>Forbidden imports regexp = ".+.dao..+"</li>
 * </dl>
 * <br>
 * By means of few instances of this check will be possible to check any number
 * of rules.<br>
 * <br>
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil
 *         Yaroslavtsev</a>
 */
public class ForbidCertainImportsCheck extends Check
{

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    private static final String WARNING_MSG_KEY = "forbid.certain.imports";

    /**
     * Pattern object is used to store the regexp for the package text checking.
     */
    private Pattern mPackageNamesRegexp;

    /**
     * Pattern object is used to store the regexp for the forbidden import text
     * checking.
     */
    private Pattern mForbiddenImportsRegexp;


    /**
     * Boolean flag. True, if the current package text is being processed
     * matches the given regexp.
     */
    private boolean mPackageMatches;


    /**
     * Gets the regexp is currently used for the package text checking.
     * @return The Pattern object is used to store the regexp for the package
     *         text checking.
     */
    public String getPackageNameRegexp()
    {
        return mPackageNamesRegexp.toString();
    }

    /**
     * Sets the regexp is currently using for the package text checking.
     * @param aPackageNameRegexp
     *        String contains the regex to set for the package text checking.
     */
    public void setPackageNameRegexp(String aPackageNameRegexp)
    {
        if (aPackageNameRegexp != null) {
            mPackageNamesRegexp = Pattern.compile(aPackageNameRegexp);
        }
    }

    /**
     * Gets the regexp is currently used for the imports text checking.
     * @return the regexp String is used for the imports text checking.
     */
    public String getForbiddenImportRegexp()
    {
        return mForbiddenImportsRegexp.toString();
    }

    /**
     * Sets the regexp is currently used for the imports text checking.
     * @param aForbiddenPackageNameRegexp
     *        String contains a regex to set for the imports text checking.
     */
    public void setForbiddenImportRegexp(String
            aForbiddenPackageNameRegexp)
    {
        if (aForbiddenPackageNameRegexp != null) {
            mForbiddenImportsRegexp = Pattern
                    .compile(aForbiddenPackageNameRegexp);
        }
    }

    @Override
    public int[] getDefaultTokens()
    {
        final int[] defaultTokens;
        if (mPackageNamesRegexp != null && mForbiddenImportsRegexp != null) {
            defaultTokens = new int[] {TokenTypes.PACKAGE_DEF,
                TokenTypes.IMPORT, TokenTypes.LITERAL_NEW, };
        }
        else {
            defaultTokens = new int[] {};
        }
        return defaultTokens;
    }

    @Override
    public void visitToken(DetailAST aAst)
    {
        switch (aAst.getType()) {
        case TokenTypes.PACKAGE_DEF:
            if (mPackageNamesRegexp != null) {
                final String packageText = getText(aAst);
                mPackageMatches = mPackageNamesRegexp.matcher(packageText)
                        .matches();
            }
            break;

        case TokenTypes.IMPORT:
            if (mPackageMatches && mForbiddenImportsRegexp != null) {
                final String importText = getText(aAst);
                final boolean importMatches = mForbiddenImportsRegexp.matcher(
                        importText).matches();
                if (importMatches) {
                    log(aAst);
                }
            }
            break;
        case TokenTypes.LITERAL_NEW:
            if (mForbiddenImportsRegexp != null
                    && aAst.findFirstToken(TokenTypes.DOT) != null
                    && mPackageMatches)
            {
                final String importText = getText(aAst);
                final boolean importMatches = mForbiddenImportsRegexp.matcher(
                        importText).matches();
                if (importMatches) {
                    log(aAst);
                }
            }
            break;
        default:
            throw new IllegalArgumentException(
                    "ForbidCertainImportsCheck: the processing got the "
                            + "wrong input token: "
                            + aAst.toString() + ", token type = "
                            + aAst.getType()
                            + ".");
        }
    }

    /**
     * Logs a warning message for given warn location.
     * @param aNodeToWarn
     *        A DetailAST node is pointing to the current warn location.
     */
    private void log(DetailAST aNodeToWarn)
    {
        log(aNodeToWarn.getLineNo(), WARNING_MSG_KEY,
                getForbiddenImportRegexp());
    }

    /**
     * Gets package/import text representation from the given DetailAST
     * PACKAGE_DEF or IMPORT node.
     * @param aAST
     *        - DetailAST node is pointing to package or import definition
     *        (should be a PACKAGE_DEF or IMPORT type).
     * @return The String representationpackage or import text without
     *         "package"/"import" words or semicolons.
     */
    private static String getText(DetailAST aAST)
    {
        String result = null;

        final DetailAST textWithoutDots = aAST.findFirstToken(TokenTypes.IDENT);

        if (textWithoutDots == null) {
         // if there are TokenTypes.DOT nodes in package/import text subTree.
            final DetailAST parentDotAST = aAST.findFirstToken(TokenTypes.DOT);
            if (parentDotAST != null) {
                final FullIdent dottedPathIdent = FullIdent
                        .createFullIdentBelow(parentDotAST);
                final DetailAST nameAST = parentDotAST.getLastChild();
                result = dottedPathIdent.getText() + "." + nameAST.getText();
            }
            else {
                // no code
            }
        }
        else { // if package/import text doesn`t contain dots.
            result = textWithoutDots.getText();
        }
        return result;
    }

}
