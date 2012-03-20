package com.puppycrawl.tools.checkstyle.checks.design;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Checks that any Exception class which matches the defined className regexp
 * have at least one constructor with Exception cause as a parameter. <br>
 * <p>
 * Rationale: <br>
 * <br>
 * "A special form of exception translation called exception chaining is
 * appropriate in cases where the lower-level exception might be helpful to
 * someone debugging the problem that caused the higher-level exception. The
 * lower-level exception (the cause) is passed to the higher-level.."
 * <p align=right>
 * <i>[Joshua Bloch - Effective Java 2nd Edition, Chapter 4, Item 61]</i>
 * </p>
 * <p>
 * Parameters:
 * </p>
 * <ol>
 * <li>Exception classNames regexp. ("classNamesRegexp" option).</li>
 * <li>regexp to ignore classes by names ("ignoredClassNamesRegexp" option).</li>
 * </dl><br>
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 */
public class CauseParameterInExceptionCheck extends Check
{
    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    private static final String WARNING_MSG_KEY =
            "cause.parameter.in.exception";

    /**
     * Pattern object is used to store the regexp for the names of classes, that
     * should be checked. Default value = ".+Exception".
     */
    private Pattern mClassNamesRegexp = Pattern.compile(".+Exception");

    /**
     * Pattern object is used to store the regexp for the names of classes, that
     * should be ignored by check.
     */
    private Pattern mIgnoredClassNamesRegexp = Pattern.compile("");

    /**
     * List contains the names of classes which would be considered as Exception
     * cause. Default value = "Throwable, Exception".
     */
    private Set<String> mAllowedCauseTypes = new HashSet<String>();
    
    /**
     * List of DetailAST objects which are related to Exception classes that
     * need to be warned.
     */
    private List<DetailAST> mExceptionClassesToWarn = new LinkedList<DetailAST>();

    /**
     * Gets the regexp is currently used for the names of classes, that should
     * be checked.
     * @return String contains regexp is currently used for the names of
     *         classes, that should be checked.
     */
    public String getClassNamesRegexp()
    {
        return mClassNamesRegexp.toString();
    }

    /**
     * Sets the regexp for the names of classes, that should be checked.
     * @param aClassNamesRegexp
     *        String contains the regex to set for the names of classes, that
     *        should be checked.
     */
    public void setClassNamesRegexp(String aClassNamesRegexp)
    {
        final String regexp = aClassNamesRegexp == null ? ""
                : aClassNamesRegexp;
        mClassNamesRegexp = Pattern.compile(regexp);
    }

    /**
     * Gets the regexp is currently used for the names of classes, that should
     * be ignored by check.
     * @return String contains regexp is currently used for the names of
     *         classes, that should be ignored by check.
     */
    public String getIgnoredClassNamesRegexp()
    {
        return mIgnoredClassNamesRegexp.toString();
    }

    /**
     * Sets the regexp for the names of classes, that should be ignored by
     * check.
     * @param aIgnoredClassNamesRegexp
     *        String contains the regex to set for the names of classes, that
     *        should be ignored by check.
     */
    public void setIgnoredClassNamesRegexp(String aIgnoredClassNamesRegexp)
    {
        final String regexp = aIgnoredClassNamesRegexp == null ? ""
                : aIgnoredClassNamesRegexp;
        mIgnoredClassNamesRegexp = Pattern.compile(regexp);
    }

    /**
     * Sets the names of classes which would be considered as Exception cause.
     * @param aClassNames
     *        - the list of classNames separated by a comma. ClassName should be
     *        short, such as "NullpointerException", do not use full name -
     *        java.lang.NullpointerException;
     */
    public void setAllowedCauseTypes(final String[] aAllowedCauseTypes)
    {
        mAllowedCauseTypes.clear();
        if (aAllowedCauseTypes != null) {
            for (String name : aAllowedCauseTypes) {
                mAllowedCauseTypes.add(name);
            }
        }
    }
    
    @Override
    public int[] getDefaultTokens()
    {
        return new int []{TokenTypes.CLASS_DEF, TokenTypes.CTOR_DEF, };
    }

    @Override
    public void visitToken(DetailAST aAst)
    {        
        switch (aAst.getType()) {
        case TokenTypes.CLASS_DEF:
            final String exceptionClassName = getName(aAst);       
            if (mClassNamesRegexp.matcher(exceptionClassName).matches()
                    && !mIgnoredClassNamesRegexp.matcher(exceptionClassName).matches())
            {
                mExceptionClassesToWarn.add(aAst);
            }
            break;
        case TokenTypes.CTOR_DEF:
            final DetailAST exceptionClass = getClassDef(aAst);
            if(mExceptionClassesToWarn.contains(exceptionClass) && hasCauseAsParameter(aAst)) { // if current class is not ignored
                mExceptionClassesToWarn.remove(exceptionClass);
            }
            break;
        default:
            final String className = this.getClass().getSimpleName();
            final String tokenType = TokenTypes.getTokenName(aAst.getType());
            final String tokenDescription = aAst.toString();
            final String message =
                    String.format("%s got the wrong input token: %s (%s)",
                            className, tokenType, tokenDescription);
            throw new IllegalArgumentException(message);
        }
    }

    @Override
    public void finishTree(DetailAST aTreeRootAST)
    {
        for (DetailAST classDefNode : mExceptionClassesToWarn) {
                log(classDefNode, WARNING_MSG_KEY, getName(classDefNode));
        }
        mExceptionClassesToWarn.clear();        
    }

    /**
     * Checks that the given constructor contains exception cause as a
     * parameter.
     * @param aCtorDefNode
     *        The CTOR_DEF DetailAST node is related to the constructor
     *        definition.
     * @return true if the given ctor contains exception cause as a parameter
     *         and false otherwise.
     */
    private boolean hasCauseAsParameter(DetailAST aCtorDefNode)
    {
        boolean result = false;
        final DetailAST parameters =
                aCtorDefNode.findFirstToken(TokenTypes.PARAMETERS);
        for (String parameterType : getParameterTypes(parameters)) {
            if (mAllowedCauseTypes.contains(parameterType))
            {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Gets the list of classNames for given constructor parameters types.
     * @param aParametersAST - A PARAMETERS DetailAST.
     * @return the list of classNames for given constructor parameters types.
     */
    private static List<String> getParameterTypes(DetailAST aParametersAST)
    {
        final List<String> result = new LinkedList<String>();
        for (DetailAST parametersChild : getChildren(aParametersAST)) {
            if (parametersChild.getType() == TokenTypes.PARAMETER_DEF) {
                final DetailAST parameterType = parametersChild
                        .findFirstToken(TokenTypes.TYPE);
                final String parameter = parameterType.getFirstChild()
                        .getText();
                result.add(parameter);
            }
        }
        return result;
    }

    /**
     * Gets the name of given class or constructor.
     * @param aClassOrCtorDefNode
     *        the a CLASS_DEF or CTOR_DEF node
     * @return the name of class or constructor is related to CLASS_DEF or
     *         CTOR_DEF node.
     */
    private static String getName(final DetailAST aClassOrCtorDefNode)
    {
        final DetailAST classNameIdent = aClassOrCtorDefNode
                .findFirstToken(TokenTypes.IDENT);
        return classNameIdent.getText();
    }

    /**
     * Gets the parent CLASS_DEF DetailAST node for given DetailAST node.
     * @param aNode
     *        The DetailAST node.
     * @return The parent CLASS_DEF node for the class that owns a token is
     *         related to the given DetailAST node or null if given token is not
     *         located in any class.
     */
    private static DetailAST getClassDef(final DetailAST aNode)
    {
        DetailAST curNode = aNode;
        while (curNode != null && curNode.getType() != TokenTypes.CLASS_DEF) {
            curNode = curNode.getParent();
        }
        return curNode;
    }

    /**
     * Gets all the children which are one level below on the current DetailAST
     * parent node.
     * @param aNode
     *        Current parent node.
     * @return The list of children one level below on the current parent node.
     */
    private static List<DetailAST> getChildren(final DetailAST aNode)
    {
        final List<DetailAST> result = new LinkedList<DetailAST>();
        DetailAST curNode = aNode.getFirstChild();
        while (curNode != null) {
            result.add(curNode);
            curNode = curNode.getNextSibling();
        }
        return result;
    }

}
