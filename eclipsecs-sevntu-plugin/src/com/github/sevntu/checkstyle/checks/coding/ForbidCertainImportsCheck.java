package com.github.sevntu.checkstyle.checks.coding;

import java.util.regex.Pattern;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Forbids usage Certain Imports usage in class. <br>
 * Idea was taken from similar check in Sonar project. <br>
 * Parameters:
 * <ol>
 * <li>Package name. (type=regexp)</li>
 * <li>Forbidden package name in imports (type=regexp) </li></ol>
 * Example of usage: Forbid
 * to use "*.ui.*" packages in "*.dao.*" packages By means of few instances of
 * this check will be possible to check any number of rules.<br><br>
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil
 *         Yaroslavtsev</a>
 */
public class ForbidCertainImportsCheck extends Check
{

    /**
     * option
     */
    private Pattern mPackageNameRegexp;

    /**
     * option
     */
    private Pattern mForbiddenImportRegexp;

    /**
     * 
     */
    private String mKey = "forbid.certain.imports";

    /**
     * 
     */    
    private boolean packageMatches;

    /**
     * @return the mPackageName
     */
    public String getPackageNameRegexp()
    {
        return mPackageNameRegexp.toString();
    }

    /**
     * @param aPackageNameRegexp
     *        the mPackageName to set
     */
    public void setPackageNameRegexp(String aPackageNameRegexp)
    {
        if (aPackageNameRegexp != null && !("".equals(aPackageNameRegexp))) {
            mPackageNameRegexp = Pattern.compile(aPackageNameRegexp);
        }
    }

    /**
     * @return the forbiddenPackageName
     */
    public String getForbiddenImportRegexp()
    {
        return mForbiddenImportRegexp.toString();
    }

    /**
     * @param aForbiddenPackageNameRegexp
     *        the forbidden Package name to set
     */
    public void setForbiddenImportRegexp(String
            aForbiddenPackageNameRegexp)
    {
        if (aForbiddenPackageNameRegexp != null && !("".equals(aForbiddenPackageNameRegexp))) {
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
                    log(aAst.getLineNo(), mKey);
                }
            }
            break;
        }
    }

    /**
     * Gets package/import text from the given DetailAST PaCKAGE_DEF or IMPORT
     * node.
     * @param packageOrImportAST
     */
    private String getText(DetailAST packageOrImportAST)
    {
        String result = null;
        DetailAST packageIdent = packageOrImportAST
                .findFirstToken(TokenTypes.IDENT);
        if (packageIdent == null) {
            DetailAST packagePathAST = packageOrImportAST
                    .findFirstToken(TokenTypes.DOT);
            if (packagePathAST != null) {
                FullIdent packagePathIdent = FullIdent
                        .createFullIdentBelow(packagePathAST);
                DetailAST packageName = packagePathAST.getLastChild();
                result = packagePathIdent.getText() + "."
                        + packageName.getText();
            }
        }
        else {
            result = packageIdent.getText();
        }
        return result;
    }

}
