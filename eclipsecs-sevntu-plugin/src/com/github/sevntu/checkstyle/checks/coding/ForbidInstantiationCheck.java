package com.github.sevntu.checkstyle.checks.coding;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Forbids instantiation of certain object types by their full classname. <br>
 * <p>
 * For example:<br>
 * "java.lang.NullPointerException" will forbid the NPE instantiation.
 * </p>
 * <p>
 * Note: className should to be full: use "java.lang.NullPointerException"
 * instead of "NullpointerException".
 * </p>
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil
 *         Yaroslavtsev</a>
 */
public class ForbidInstantiationCheck extends Check
{

    /**
     * Set which contains classNames for objects that are forbidden to
     * instantiate.
     */
    private Set<String> mForbiddenClasses = new HashSet<String>();

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    private String mKey = "forbid.instantiation";

    /**
     * List which contains String representation of imports for class is
     * currently being processed.
     */
    private List<String> mImportsList = new LinkedList<String>();

    /**
     * Sets a classNames&Paths for objects that are forbidden to instantiate.
     * @param aClassNames
     *        - the list of classNames separated by a comma. ClassName should be
     *        full, such as "java.lang.NullpointerException", do not use short
     *        name - NullpointerException;
     */
    public void setForbiddenClasses(final String[] aClassNames)
    {
        if (aClassNames != null) {
            for (String name : aClassNames) {
                mForbiddenClasses.add(name);
            }
        }
    }

    @Override
    public void beginTree(final DetailAST aRootAST)
    {
        mImportsList.clear();
    }

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] { TokenTypes.IMPORT, TokenTypes.LITERAL_NEW };
    }

    @Override
    public void visitToken(DetailAST aAst)
    {
        switch (aAst.getType()) {

        case TokenTypes.IMPORT:
            mImportsList.add(getText(aAst));
            break;

        case TokenTypes.LITERAL_NEW:

            final String instanceClass = getText(aAst);

            if (instanceClass != null) { // non-primitive instance

                final String instanceClassName = getClassName(instanceClass);

                for (String forbiddenClass : mForbiddenClasses) {

                    if (forbiddenClass.startsWith("java.lang.")
                            && forbiddenClass.endsWith(instanceClassName))
                    { // java.lang.*
                        log(aAst, mKey, instanceClassName);
                    }
                    else if (instanceClass.contains(".")) { // className is full

                        if (instanceClass.equals(forbiddenClass)) {
                            // the full path is forbidden
                            log(aAst, mKey, instanceClassName);
                        }
                    }
                    else if (addedUsingForbiddenImport(instanceClass,
                            forbiddenClass))
                    {
                        // className is short and exists in imports
                        log(aAst, mKey, instanceClass);
                    }
                }
            }
            break;

        default:
            throw new IllegalArgumentException(
                    "ForbidInstantiationCheck: the processing got the " +
                            "wrong input token: "
                            + aAst.toString() + ", token type = "
                            + aAst.getType()
                            + ".");
        }

    }

    /**
     * Checks that the class with given className is visible because of the
     * forbidden import.
     * @param aClassName
     *        - the name of the class to check.
     * @param aForbiddenClassNameAndPath
     *        - full name&path of the given forbidden class.
     * @return true if the class with given className is imported with the
     *         forbidden import and false otherwise.
     */
    private boolean addedUsingForbiddenImport(final String aClassName,
            String aForbiddenClassNameAndPath)
    {
        boolean result = false;

        for (String importText : mImportsList) {
            if (importText.endsWith("*")) {
                final String importTextWithoutAsterisk =
                        importText.substring(0, importText.length() - 1);
                if (aForbiddenClassNameAndPath.equals(
                        importTextWithoutAsterisk + aClassName))
                {
                    result = true;
                    break;
                }
            }
            else if (importText.equals(aForbiddenClassNameAndPath)
                    && importText.endsWith(aClassName))
            {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Gets the class name from full (dotted) classPath.
     * @param aClassNameAndPath
     *        - the full (dotted) classPath
     * @return the name of the class is specified by the current full name&path.
     */
    private String getClassName(final String aClassNameAndPath)
    {
        return aClassNameAndPath.replaceAll(".+\\.", "");
    }

    /**
     * Gets the text representation from the given DetailAST node.
     * @param aAST
     *        - DetailAST node is pointing to import definition or to the "new"
     *        literal node ("IMPORT" or "LITERAL_NEW" node types).
     * @return Import text without "import" word and semicolons for given
     *         "IMPORT" node or instanstiated class Name&Path for given
     *         "LITERAL_NEW" node.
     */
    private static String getText(final DetailAST aAST)
    {
        String result = null;

        final DetailAST textWithoutDots = aAST.findFirstToken(TokenTypes.IDENT);

        if (textWithoutDots == null) {
            // if there are TokenTypes.DOT nodes in subTree.
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
        else { // if subtree doesn`t contain dots.
            result = textWithoutDots.getText();
        }
        return result;
    }

}
