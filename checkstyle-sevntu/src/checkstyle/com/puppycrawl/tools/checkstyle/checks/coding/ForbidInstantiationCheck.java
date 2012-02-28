package com.puppycrawl.tools.checkstyle.checks.coding;

import java.util.HashSet;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Forbids throwing certain types of objects by their classname. <br>
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil
 *         Yaroslavtsev</a>
 */
public class ForbidInstantiationCheck extends Check
{

    /**
     * Set contains classNames for objects that are forbidden to instantiate.
     */
    private Set<String> mForbiddenClassNames = new HashSet<String>();

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    private String mKey = "forbid.instantiation";

    /**
     * Sets a classNames for objects that are forbidden to instantiate.
     * @param aClassNames - the list of classNames separated by a comma.
     */
    public void setForbiddenClassNames(final String[] aClassNames)
    {
        if (aClassNames != null) {
            for (String name : aClassNames) {
                mForbiddenClassNames.add(name);
            }
        }
    }

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] { TokenTypes.LITERAL_NEW };
    }

    @Override
    public void visitToken(DetailAST aAst)
    {
        DetailAST objNameAST = aAst.findFirstToken(TokenTypes.IDENT);
        if (objNameAST != null) {
            final String objName = objNameAST.getText();
            for (String className : mForbiddenClassNames) {
                if (objName.equals(className)) {
                    log(aAst, mKey, className);
                }
            }
        }
    }
}
