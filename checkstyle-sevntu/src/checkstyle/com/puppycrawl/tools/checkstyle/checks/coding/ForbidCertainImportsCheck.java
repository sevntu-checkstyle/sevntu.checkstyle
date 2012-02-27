package com.puppycrawl.tools.checkstyle.checks.coding;

import java.util.regex.Pattern;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Forbids usage Certain Imports usage in class. <br>
 * Idea was taken from similar check in Sonar project. <br>
 * <br>
 * Parameters:
 * <ol>
 * <li>Package name. (type=regexp)</li>
 * <li>Forbidden package name in imports (type=regexp)</li>
 * </ol>
 * Check retrieves package and imports text for any *.java file in string
 * representation without "package"/"import" words or semicolons. So you can be
 * sure to write regular expressions for package/imports text filtering. <br>
 * <br>
 * Example of usage: Forbid to use "*.ui.*" packages in "*.dao.*" packages: <br>
 * <br>
 * <dl>
 * <li>Package name regexp = ".+.ui..+"</li>
 * <li>Forbidden import regexp = ".+.dao..+"</li>
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
     * Pattern object is used to store the regexp for the package text checking.
     */
    private Pattern mPackageNameRegexp;

    /**
     * Pattern object is used to store the regexp for the forbidden import text checking.
     */
    private Pattern mForbiddenImportRegexp;

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    private String mKey = "forbid.certain.imports";

    /**
     * Boolean flag. True, if the current package text is being processed
     * matches the given regexp.
     */
    private boolean packageMatches;

    /**
     * Gets the regexp is currently used for the package text checking.
     * @return The Pattern object is used to store the regexp for the package text checking.
     */
    public String getPackageNameRegexp()
    {
        return mPackageNameRegexp.toString();
    }

    /**
     * Sets the regexp is currently using for the package text checking.
     * @param aPackageNameRegexp
     *        String contains the regex to set for the package text checking.
     */
    public void setPackageNameRegexp(String aPackageNameRegexp)
    {
        if (aPackageNameRegexp != null) {
            mPackageNameRegexp = Pattern.compile(aPackageNameRegexp);
        }
    }

    /**
     * Gets the regexp is currently used for the imports text checking.
     * @return the regexp String is used for the imports text checking.
     */
    public String getForbiddenImportRegexp()
    {
        return mForbiddenImportRegexp.toString();
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
            mForbiddenImportRegexp = Pattern.compile(aForbiddenPackageNameRegexp);
        }
    }

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] { TokenTypes.PACKAGE_DEF, TokenTypes.IMPORT };
    }

    @Override
    public void visitToken(DetailAST aAst)
    {       
        switch (aAst.getType()) {
        case TokenTypes.PACKAGE_DEF:
            if (mPackageNameRegexp != null) {
                String packageText = getText(aAst);
                packageMatches = mPackageNameRegexp.matcher(packageText).matches();
            }
            break;

        case TokenTypes.IMPORT:
            if (packageMatches && mForbiddenImportRegexp != null) {
                String importText = getText(aAst);
                boolean importMatches = mForbiddenImportRegexp.matcher(importText).matches();
                if (importMatches) {
                    log(aAst.getLineNo(), mKey, getForbiddenImportRegexp());
                }
            }
            break;
        }
    }

    /**
     * Gets package/import text from the given DetailAST PACKAGE_DEF or IMPORT
     * node.
     * @param aAST
     *        - DetailAST node is pointing to package or import definition
     *        (should be a PACKAGE_DEF or IMPORT type).
     */
    private static String getText(DetailAST aAST)
    {
        String result = null;

        DetailAST ident = aAST.findFirstToken(TokenTypes.IDENT);

        if (ident == null) {
            final DetailAST firstDotAST = aAST.findFirstToken(TokenTypes.DOT);
            if (firstDotAST != null) {
                final FullIdent fullPathIdent = FullIdent
                        .createFullIdentBelow(firstDotAST);
                final DetailAST nameAST = firstDotAST.getLastChild();
                result = fullPathIdent.getText() + "." + nameAST.getText();
            }
        }
        else {
            result = ident.getText();
        }
        return result;
    }

}
