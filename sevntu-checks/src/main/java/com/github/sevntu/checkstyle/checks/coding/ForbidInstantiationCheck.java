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
package com.github.sevntu.checkstyle.checks.coding;

import java.util.Set;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import com.github.sevntu.checkstyle.Utils;
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
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY = "forbid.instantiation";

    /**
     * Set which contains classNames for objects that are forbidden to
     * instantiate.
     */
    private Set<String> forbiddenClasses = new HashSet<String>();

    /**
     * List which contains String representation of imports for class is
     * currently being processed.
     */
    private List<String> importsList = new LinkedList<String>();

    /**
     * Creates the check instance.
     */
    public ForbidInstantiationCheck()
    {
        forbiddenClasses.add("java.lang.NullPointerException");
    }

    /**
     * Sets a classNames&Paths for objects that are forbidden to instantiate.
     * @param classNames
     *        - the list of classNames separated by a comma. ClassName should be
     *        full, such as "java.lang.NullpointerException", do not use short
     *        name - NullpointerException;
     */
    public void setForbiddenClasses(final String[] classNames)
    {
        forbiddenClasses.clear();
        if (classNames != null) {
            for (String name : classNames) {
                forbiddenClasses.add(name);
            }
        }
    }

    @Override
    public void beginTree(final DetailAST rootAST)
    {
        importsList.clear();
    }

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] {TokenTypes.IMPORT, TokenTypes.LITERAL_NEW };
    }

    @Override
    public void visitToken(DetailAST ast)
    {
        switch (ast.getType()) {

            case TokenTypes.IMPORT:
                importsList.add(getText(ast));
                break;

            case TokenTypes.LITERAL_NEW:

                final String instanceClass = getText(ast);

                if (instanceClass != null) { // non-primitive instance

                    final String instanceClassName = getClassName(instanceClass);

                    for (String forbiddenClass : forbiddenClasses) {

                        if (forbiddenClass.startsWith("java.lang.")
                            && forbiddenClass.endsWith(instanceClassName))
                        { // java.lang.*
                            log(ast, MSG_KEY, instanceClassName);
                        }
                        else if (instanceClass.contains(".")) { // className is full

                            if (instanceClass.equals(forbiddenClass)) {
                                // the full path is forbidden
                                log(ast, MSG_KEY, instanceClassName);
                            }
                        }
                        else if (addedUsingForbiddenImport(instanceClass,
                            forbiddenClass))
                        {
                            // className is short and exists in imports
                            log(ast, MSG_KEY, instanceClass);
                        }
                    }
                }
                break;

            default:
                Utils.reportInvalidToken(ast.getType());
                break;
        }

    }

    /**
     * Checks that the class with given className is visible because of the
     * forbidden import.
     * @param className
     *        - the name of the class to check.
     * @param forbiddenClassNameAndPath
     *        - full name&path of the given forbidden class.
     * @return true if the class with given className is imported with the
     *         forbidden import and false otherwise.
     */
    private boolean addedUsingForbiddenImport(final String className,
            String forbiddenClassNameAndPath)
    {
        boolean result = false;

        for (String importText : importsList) {
            if (importText.endsWith("*")) {
                final String importTextWithoutAsterisk =
                        importText.substring(0, importText.length() - 1);
                if (forbiddenClassNameAndPath.equals(
                        importTextWithoutAsterisk + className))
                {
                    result = true;
                    break;
                }
            }
            else if (importText.equals(forbiddenClassNameAndPath)
                    && importText.endsWith(className))
            {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Gets the class name from full (dotted) classPath.
     * @param classNameAndPath
     *        - the full (dotted) classPath
     * @return the name of the class is specified by the current full name&path.
     */
    private static String getClassName(final String classNameAndPath)
    {
        return classNameAndPath.replaceAll(".+\\.", "");
    }

    /**
     * Gets the text representation from the given DetailAST node.
     * @param ast
     *        - DetailAST node is pointing to import definition or to the "new"
     *        literal node ("IMPORT" or "LITERAL_NEW" node types).
     * @return Import text without "import" word and semicolons for given
     *         "IMPORT" node or instanstiated class Name&Path for given
     *         "LITERAL_NEW" node.
     */
    private static String getText(final DetailAST ast)
    {
        String result = null;

        final DetailAST textWithoutDots = ast.findFirstToken(TokenTypes.IDENT);

        if (textWithoutDots == null) {
            // if there are TokenTypes.DOT nodes in subTree.
            final DetailAST parentDotAST = ast.findFirstToken(TokenTypes.DOT);
            if (parentDotAST != null) {
                final FullIdent dottedPathIdent = FullIdent
                        .createFullIdentBelow(parentDotAST);
                final DetailAST nameAST = parentDotAST.getLastChild();
                result = dottedPathIdent.getText() + "." + nameAST.getText();
            }
        }
        else { // if subtree doesn`t contain dots.
            result = textWithoutDots.getText();
        }
        return result;
    }

}
