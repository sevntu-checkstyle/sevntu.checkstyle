package com.puppycrawl.tools.checkstyle.checks.design;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Checks that any Exception class which matches the defined className regexp
 * have at least one constructor with Exception cause as a parameter. <br>
 * <br>
 * Parameters:
 * <dl>
 * <li>Exception classNames regexp. ("classNamesRegexp" option)</li>
 * <li>regexp to ignore classes by names ("ignoredClassNamesRegexp" option).
 * </li> <br> </dl>
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil
 *         Yaroslavtsev</a>
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
     * List of ExceptionClass objects are related to Exception classes is
     * currently found in processed file.
     */
    private LinkedList<ExceptionClass> mExceptionClasses;

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
     * Creates the new CauseParameterInExceptionCheck instance.
     */
    public CauseParameterInExceptionCheck()
    {
        mExceptionClasses = new LinkedList<ExceptionClass>();
    }

    @Override
    public int[] getDefaultTokens()
    {
        return new int []{TokenTypes.CLASS_DEF, TokenTypes.CTOR_DEF };
    }

    @Override
    public void visitToken(DetailAST aAst)
    {
        switch (aAst.getType()) {
        case TokenTypes.CLASS_DEF:
            final ExceptionClass exceptionClass = new ExceptionClass(aAst);
            final String exceptionClassName = exceptionClass.getClassName();
            if (mClassNamesRegexp.matcher(exceptionClassName).matches()
                    && !mIgnoredClassNamesRegexp.matcher(exceptionClassName).matches()) {
                mExceptionClasses.add(exceptionClass);
            }
            break;
        case TokenTypes.CTOR_DEF:
            final ExceptionClass currentExceptionClass = getExceptionClass(aAst);
            if (currentExceptionClass != null) {
                currentExceptionClass.addConstructorDefNode(aAst);
            }
            break;
        default:
            throw new IllegalArgumentException(
                    "CauseParameterInExceptionCheck: the processing got the "
                            + "wrong input token: "
                            + aAst.toString() + ", token type = "
                            + TokenTypes.getTokenName(aAst.getType())
                            + ".");
        }
    }

    @Override
    public void finishTree(DetailAST aTreeRootAST)
    {
        for (ExceptionClass exceptionClass : mExceptionClasses) {
            final DetailAST classDefNode = exceptionClass.getClassDefNode();
            if (!hasCtorWithCauseAsParameter(exceptionClass)) {
                log(classDefNode, WARNING_MSG_KEY, getName(classDefNode));
            }
        }
    }

    /**
     * Gets the ExceptionClass object is related to the parent class for given
     * CTOR_DEF node.
     * @param aCtorDefNode
     *        - The CTOR_DEF DetailAST node.
     * @return The Exception class which contains the given constructor or null
     *         if current Exception class was ignored by check.
     */
    private ExceptionClass getExceptionClass(DetailAST aCtorDefNode)
    {
        ExceptionClass result = null;
        final DetailAST classDef = getClassDef(aCtorDefNode);

        for (ExceptionClass exceptionClass : mExceptionClasses) {
            if (classDef == exceptionClass.getClassDefNode()) {
                result = exceptionClass;
                break;
            }
        }
        return result;
    }

    /**
     * Checks that given Exception class contains at least one constructor with
     * exception cause as a parameter.
     * @param aExceptionClass
     *        ExceptionClass Object is related to the current processed
     *        exception.
     * @return true if given Exception class contains the constructor with
     *         exception cause as a parameter and false otherwise.
     */
    private static boolean hasCtorWithCauseAsParameter(ExceptionClass aExceptionClass)
    {
        boolean result = false;
        for (DetailAST ctorNode : aExceptionClass.getContructorDefList()) {
            if (hasCauseAsParameter(ctorNode)) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Checks that given constructor contains exception cause as a parameter.
     * @param aCtorDefNode
     *        The CTOR_DEF DetailAST node is related to the constructor
     *        definition.
     * @return true if the given ctor contains exception cause as a parameter
     *         and false otherwise.
     */
    private static boolean hasCauseAsParameter(DetailAST aCtorDefNode)
    {
        boolean result = false;
        final DetailAST parameters =
                aCtorDefNode.findFirstToken(TokenTypes.PARAMETERS);
        for (String parameterType : getParameterTypes(parameters)) {
            if ("Throwable".equals(parameterType)
                    || "Exception".equals(parameterType))
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
     * Gets a parent CLASS_DEF DetailAST node for given DetailAST
     * node.     *
     * @param aNode
     *            A DetailAST node.
     * @return The parent CLASS_DEF node for the class that owns a Token
     *         is related to the given DetailAST node.
     * */
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

    /**
     * Class that represents an Exception class. Contains class and related
     * ctors definitions as DetailASTs objects.
     */
    private class ExceptionClass
    {
        /**
         * ClassName for current Exception class instance.
         */
        private String className;
 
        /**
         * A DetailAST node that represents the Exception class definition.
         */
        private DetailAST mClassDefNode;

        /**
         * List of DetailAST nodes that represents constructors definitions for
         * an Exception class.
         */
        private List<DetailAST> mContructorDefList;

        /**
         * ExceptionClass ctor.
         * @param aClassDefAST
         *        CLASS_DEF DetailAST node for Exception class to be
         *        constructed.
         */
        public ExceptionClass(DetailAST aClassDefAST)
        {
            this.className = getName(aClassDefAST);
            this.mClassDefNode = aClassDefAST;
            mContructorDefList = new LinkedList<DetailAST>();
        }


        /**
         * Adds the constructor to the ctors list.
         * @param aCtorDefNode
         *        the CTOR_DEF DetailAST node which points to a constructor
         *        definition.
         */
        public void addConstructorDefNode(DetailAST aCtorDefNode)
        {
            mContructorDefList.add(aCtorDefNode);
        }

        /**
         * Gets the CLASS_DEF DetailAST node for current Exception class
         * instance.
         * @return the CLASS_DEF DetailAST node.
         */
        public DetailAST getClassDefNode()
        {
            return mClassDefNode;
        }

        /**
         * Gets the list of contructor definitions DetailASTs for current
         * Exception class instance.
         * @return the contructorDefList
         */
        public List<DetailAST> getContructorDefList()
        {
            return mContructorDefList;
        }

        /**
         * Gets the Exception className.
         * @return the Exception className.
         */
        public String getClassName()
        {
            return className;
        }
    }
}
