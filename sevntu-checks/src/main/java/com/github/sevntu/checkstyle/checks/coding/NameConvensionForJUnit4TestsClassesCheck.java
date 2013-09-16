////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright(C) 2001-2012  Oliver Burn
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

import java.util.regex.Pattern;
import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * This check verifies the name of JUnit4 test class for compliance with given
 * naming convention. Usually class can be named "Test*", "*Test", "*TestCase"
 * or "*IT" (the latter is for integration tests), but you can provide you own
 * regexp to match 'valid' names of UTs classes.
 * </p>
 * <p>
 * Processing rule:
 * </p>
 * <p>
 * To determine whether the current class junit4 test it is necessary to analyze
 * the methods that contains the current class. If this method belongs to the
 * main class and method contains annotation "@Test". The main class is a test
 * class.
 * </p>
 * @author <a href="mailto:denant0vz@gmail.com">Denis Antonenkov</a>
 */

public class NameConvensionForJUnit4TestsClassesCheck extends Check
{
    /**
     * The key is pointing to the message text String in
     * "messages.properties file".
     */
    public static final String MSG_KEY = "name.convension.for.tests.classes";
    /**
     * Pattern object is used to store the regexp for the names of classes, that
     * could be named "Test*", "*Test", "*TestCase" or "*IT", but you can
     * provide you own regexp to match 'valid' names of UTs classes.
     */
    private Pattern mValidTestClassNameRegex = Pattern
            .compile(".+Test|Test.+|.+IT|.+TestCase");

    /**
     * True, if the current node is not related to the nodes of the test class.
     * Skip processing for the node.
     */
    private boolean mSkipCurrentNodeProcessing;

    /**
     * A current ClassDef AST is being processed by check.
     */
    private DetailAST mCurrentClassNode;

    /**
     * Sets 'valid' class name regexp for Uts.
     * @param aValidTestClassNameRegex
     *        regexp to match 'valid' unit test class names.
     */
    public void setValidTestClassNameRegex(
            String aValidTestClassNameRegex)
    {
        if (aValidTestClassNameRegex != null)
        {
            mValidTestClassNameRegex = Pattern
                    .compile(aValidTestClassNameRegex);
        }
    }

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] { TokenTypes.CLASS_DEF, TokenTypes.METHOD_DEF, };
    }

    @Override
    public void visitToken(DetailAST aNode)
    {
        if (!mSkipCurrentNodeProcessing)
        {
            switch (aNode.getType())
            {
            case TokenTypes.CLASS_DEF:
                if (mCurrentClassNode == null)
                {
                    if (isAnnotation(aNode, "RunWith")
                            && hasWrongName(aNode))
                    {
                        log(aNode.findFirstToken(TokenTypes.LITERAL_CLASS)
                              .getLineNo(), MSG_KEY, mValidTestClassNameRegex);
                        mSkipCurrentNodeProcessing = true;
                    }
                    else
                    {
                        mCurrentClassNode = aNode;
                    }
                }
                break;
            case TokenTypes.METHOD_DEF:
                if (hasCurrentClassNodeAsParent(aNode)
                        && isAnnotation(aNode, "Test"))
                {
                    if (hasWrongName(mCurrentClassNode))
                    {
                        log(mCurrentClassNode.findFirstToken(
                                TokenTypes.LITERAL_CLASS).getLineNo(),
                                MSG_KEY,
                                mValidTestClassNameRegex);
                        mSkipCurrentNodeProcessing = true;
                    }
                    else
                    {
                        mSkipCurrentNodeProcessing = true;
                    }
                }
                break;
            default:
                throw new IllegalArgumentException("Node of type "
                        + aNode.getType() + " is not implemented");
            }
        }
    }

    @Override
    public void finishTree(DetailAST aRootAST)
    {
        mCurrentClassNode = null;
        mSkipCurrentNodeProcessing = false;
    }

    /**
     * Returns true, if current class is a parent of aMethodDefNode.
     * @param aMethodDefNode
     *        the node of method definition.
     * @return True, if current class is a parent of aMethodDefNode.
     */
    private boolean hasCurrentClassNodeAsParent(DetailAST aMethodDefNode)
    {
        return aMethodDefNode.getParent().getParent().equals(mCurrentClassNode);
    }

    /**
     * Returns true, if the class is not the correct name.
     * @param aJUnitTestClassDefNode
     *        the node of class JUnit test.
     * @return True, if the class is not the correct name.
     */
    private boolean hasWrongName(final DetailAST aJUnitTestClassDefNode)
    {
        final String className = getIdentText(aJUnitTestClassDefNode);
        return !mValidTestClassNameRegex.matcher(className).matches();
    }

    /**
     * Returns true, if the method refers to methods of JUnit4 test.
     * @param aMethodOrClassDefNode
     *        the node of method definition.
     * @return True, if the method refers to methods of JUnit4 test.
     */
    private static boolean isAnnotation(final DetailAST aMethodOrClassDefNode,
            String aAnnotation)
    {
        final DetailAST modifiersNode = aMethodOrClassDefNode
                .findFirstToken(TokenTypes.MODIFIERS);
        final DetailAST annotationNode = modifiersNode
                .findFirstToken(TokenTypes.ANNOTATION);
        return annotationNode != null
                && aAnnotation.equals(getIdentText(annotationNode));
    }

    /**
     * Returns the text identifier for the node containing the identifier.
     * @param aNodeWithIdent
     *        the node containing identifier.
     * @return Returns the text identifier for the node containing the
     *         identifier.
     */
    private static String getIdentText(DetailAST aNodeWithIdent)
    {
        return aNodeWithIdent.findFirstToken(TokenTypes.IDENT).getText();
    }
}
