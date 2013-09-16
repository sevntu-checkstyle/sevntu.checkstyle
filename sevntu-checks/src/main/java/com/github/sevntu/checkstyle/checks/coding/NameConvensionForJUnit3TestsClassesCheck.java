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
 * regexp to match 'valid' names of UTs classes. You can also set the derived
 * class as the implementation of the test class. For example:
 * <p>
 * 'public class InputNameConvensionForTest extends TestCase'
 * </p>
 * <p>
 * Processing rule:
 * </p>
 * <p>
 * Check whether the current class extends the test class. If the current class
 * is expanding, analyze the methods of the class. If the method name starts
 * with "test" that the current class is a junit3 test class
 * </p>
 */
public class NameConvensionForJUnit3TestsClassesCheck extends Check
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

    private String mInheritingClass = "TestCase";

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

    public void setInheritingClass(String aInheritingClass)
    {
        if (aInheritingClass != null)
        {
            mInheritingClass = aInheritingClass;
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
                if (mCurrentClassNode == null && isExtendsClass(
                        aNode, mInheritingClass))
                {
                    mCurrentClassNode = aNode;
                }
                break;
            case TokenTypes.METHOD_DEF:
                if (hasCurrentClassNodeAsParent(aNode)
                        && isJUnit3Test(aNode))
                {
                    if (hasWrongName(mCurrentClassNode))
                    {
                        log(mCurrentClassNode.findFirstToken(TokenTypes.LITERAL_CLASS)
                              .getLineNo(), MSG_KEY, mValidTestClassNameRegex);
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
     * Returns true, if the current class extends class with specified
     * aClassName.
     * @param aClassDefNode
     *        the node of class definition.
     * @param aClassName
     *        the name of a extended class
     * @return True, if class extends class with specified aClassName.
     */
    private static boolean isExtendsClass(
            final DetailAST aClassDefNode, String aClassName)
    {
        final DetailAST extendsNode = aClassDefNode
                .findFirstToken(TokenTypes.EXTENDS_CLAUSE);
        if (extendsNode != null)
        {
            final DetailAST dotNode = extendsNode
                    .findFirstToken(TokenTypes.DOT);
            return dotNode != null
                    && aClassName.equals(getIdentText(dotNode))
                    || dotNode == null
                    && aClassName.equals(getIdentText(extendsNode));
        }
        return false;
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
     * Returns true, if the method of JUnit3 test.
     * @param aMethodDefNode
     *        the node of method definition.
     * @return True, if the method of JUnit3 test.
     */
    private boolean isJUnit3Test(DetailAST aMethodDefNode)
    {
        return getIdentText(aMethodDefNode).startsWith("test");
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
