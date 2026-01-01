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

import java.util.regex.Pattern;

import com.github.sevntu.checkstyle.SevntuUtil;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>Forbids certain imports usage in certain packages.
 * </p>
 * You can configure this check using the following parameters:
 * <ol>
 * <li>Package qualified name regexp;</li>
 * <li>Forbidden imports regexp;</li>
 * <li>Forbidden imports excludes regexp.</li>
 * </ol>
 * <p>
 * This check loads packages qualified names without
 * words "package","import" and semicolons, so, please, do NOT include "package" or
 * "import" words (or semicolons) into config regexps.</p>
 * <p>
 * Real-life example of usage: forbid to use all "*.ui.*" packages in "*.dao.*" packages,
 * but ignore all Exception imports (such as
 * <b>org.springframework.dao.InvalidDataAccessResourceUsageException</b>).
 * For doing that, you should to use the following check parameters:
 * </p>
 * <ul>
 * <li>Package name regexp = ".*ui.*"</li>
 * <li>Forbidden imports regexp = ".*dao.*"</li>
 * <li>Forbidden imports excludes regexp = "^.+Exception$"</li>
 * </ul>
 * <p>
 * You can cover more sophisticated rules by means of few check instances.
 * </p>
 *
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil
 *         Yaroslavtsev</a>
 * @since 1.8.0
 */
public class ForbidCertainImportsCheck extends AbstractCheck {

    /**
     * The key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY = "forbid.certain.imports";

    /**
     * Pattern for matching package fully qualified name
     * (sets the scope of affected packages).
     */
    private Pattern packageNamesRegexp;

    /**
     * Pattern for matching forbidden imports.
     */
    private Pattern forbiddenImportsRegexp;

    /**
     * Pattern for excluding imports from checking.
     */
    private Pattern forbiddenImportsExcludesRegexp;

    /**
     * True, if currently processed package fully qualified name
     * matches regexp is provided by user.
     */
    private boolean packageMatches;

    /**
     * Sets the regexp for matching package fully qualified name.
     *
     * @param packageNameRegexp
     *        regexp for package fully qualified name matching.
     */
    public void setPackageNameRegexp(String packageNameRegexp) {
        if (packageNameRegexp != null) {
            packageNamesRegexp = Pattern.compile(packageNameRegexp);
        }
    }

    /**
     * Gets the regexp is used for matching forbidden imports.
     *
     * @return regexp for forbidden imports matching.
     */
    public String getForbiddenImportRegexp() {
        return forbiddenImportsRegexp.toString();
    }

    /**
     * Sets the regexp for matching forbidden imports.
     *
     * @param forbiddenImportsRegexp
     *        regexp for matching forbidden imports.
     */
    public void setForbiddenImportsRegexp(String forbiddenImportsRegexp) {
        if (forbiddenImportsRegexp != null) {
            this.forbiddenImportsRegexp = Pattern.compile(forbiddenImportsRegexp);
        }
    }

    /**
     * Sets the regexp for excluding imports from checking.
     *
     * @param forbiddenImportsExcludesRegexp
     *        String contains a regexp for excluding imports from checking.
     */
    public void setForbiddenImportsExcludesRegexp(String
            forbiddenImportsExcludesRegexp) {
        if (forbiddenImportsExcludesRegexp != null) {
            this.forbiddenImportsExcludesRegexp = Pattern
                    .compile(forbiddenImportsExcludesRegexp);
        }
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.PACKAGE_DEF,
            TokenTypes.IMPORT,
            TokenTypes.LITERAL_NEW,
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
    public void beginTree(DetailAST rootAST) {
        packageMatches = false;
    }

    @Override
    public void visitToken(DetailAST ast) {
        switch (ast.getType()) {
            case TokenTypes.PACKAGE_DEF:
                if (packageNamesRegexp != null) {
                    final String packageQualifiedName = getText(ast);
                    packageMatches = packageNamesRegexp.matcher(packageQualifiedName)
                        .matches();
                }
                break;
            case TokenTypes.IMPORT:
                final String importQualifiedText = getText(ast);
                if (isImportForbidden(importQualifiedText)) {
                    log(ast, importQualifiedText);
                }
                break;
            case TokenTypes.LITERAL_NEW:
                if (ast.findFirstToken(TokenTypes.DOT) != null) {
                    final String classQualifiedText = getText(ast);
                    if (isImportForbidden(classQualifiedText)) {
                        log(ast, classQualifiedText);
                    }
                }
                break;
            default:
                SevntuUtil.reportInvalidToken(ast.getType());
                break;
        }
    }

    /**
     * Checks if given import both matches 'include' and not matches 'exclude' patterns.
     *
     * @param importText package fully qualified name
     * @return true is given import is forbidden in current
     *     classes package, false otherwise
     */
    private boolean isImportForbidden(String importText) {
        return packageMatches
                && forbiddenImportsRegexp != null
                && forbiddenImportsRegexp.matcher(importText).matches()
                && (forbiddenImportsExcludesRegexp == null
                    || !forbiddenImportsExcludesRegexp.matcher(importText).matches());
    }

    /**
     * Logs message on the part of code.
     *
     * @param nodeToWarn
     *        A DetailAST node is pointing to the part of code to warn on.
     * @param importText
     *        import to be warned.
     */
    private void log(DetailAST nodeToWarn, String importText) {
        log(nodeToWarn, MSG_KEY,
                getForbiddenImportRegexp(), importText);
    }

    /**
     * Gets package/import text representation from node of PACKAGE_DEF or IMPORT type.
     *
     * @param packageDefOrImportNode
     *        - DetailAST node is pointing to package or import definition
     *        (should be a PACKAGE_DEF or IMPORT type).
     * @return The fully qualified name of package or import without
     *         "package"/"import" words or semicolons.
     */
    private static String getText(DetailAST packageDefOrImportNode) {
        final String result;

        final DetailAST identNode = packageDefOrImportNode.findFirstToken(TokenTypes.IDENT);

        if (identNode == null) {
            final DetailAST parentDotAST = packageDefOrImportNode.findFirstToken(TokenTypes.DOT);
            final FullIdent dottedPathIdent = FullIdent
                    .createFullIdentBelow(parentDotAST);
            final DetailAST nameAST = parentDotAST.getLastChild();
            result = dottedPathIdent.getText() + "." + nameAST.getText();
        }
        else {
            result = identNode.getText();
        }
        return result;
    }

}
