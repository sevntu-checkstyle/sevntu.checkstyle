/**
 * 
 */
package com.puppycrawl.tools.checkstyle.checks.design;

import java.util.ArrayList;
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
 * <li>regexp to ignore classes by names ("ignoredClassNamesRegexp" option).</li> <br>
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil
 *         Yaroslavtsev</a>
 */
public class CauseParameterInExceptionCheck extends Check
{

    private static final String WARNING_MSG_KEY = "cause.parameter.in.exception";

    /**
     * Pattern object is used to store the regexp for the names of classes, that
     * should be checked.
     */
    private Pattern mClassNamesRegexp = Pattern.compile("");

    /**
     * Pattern object is used to store the regexp for the names of classes, that
     * should be ignored by check.
     */
    private Pattern mIgnoredClassNamesRegexp = Pattern.compile("");

    private ArrayList<ExceptionClass> exceptionClasses;
    
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
        String regexp = aClassNamesRegexp == null ? ""
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
        String regexp = aIgnoredClassNamesRegexp == null ? ""
                : aIgnoredClassNamesRegexp;
        mIgnoredClassNamesRegexp = Pattern.compile(regexp);
    }
    
    /**
     * Creates the new CauseParameterInExceptionCheck instance.
     */
    public CauseParameterInExceptionCheck()
    {
        exceptionClasses = new ArrayList<ExceptionClass>();
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
            exceptionClasses.add(exceptionClass);
            break;

        case TokenTypes.CTOR_DEF:
            final ExceptionClass currentExceptionClass = getExceptionClass(aAst);
                currentExceptionClass.addConstructorDefNode(aAst);
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

    /**
     * 
     * Guaranteed to not be null on current Java SE 7 (or lesser) spec.
     * @param aCtorDefAST
     * @return
     */
    private ExceptionClass getExceptionClass(DetailAST aCtorDefAST)
    {
        ExceptionClass result = null;
        DetailAST classDef = getClassDef(aCtorDefAST);
            for (ExceptionClass exceptionClass : exceptionClasses) {
                if (classDef == exceptionClass.getClassDefNode()) {
                    result = exceptionClass;
                    break;
                }
            }
        return result;
    }

    @Override
    public void finishTree(DetailAST treeRootAst)
    {
        for (ExceptionClass exceptionClass : exceptionClasses) {
            DetailAST classDefNode = exceptionClass.getClassDefNode();
           
            if(!hasCtorWithCauseAsParameter(exceptionClass)) {
                log(classDefNode, WARNING_MSG_KEY, getName(classDefNode));        
            }
        }
    }
        
    /**
     * @param exceptionClass
     * @return
     */
    private boolean hasCtorWithCauseAsParameter(ExceptionClass exceptionClass)
    {
        boolean result = false;
        for (DetailAST ctorNode : exceptionClass.getContructorDefList()) {
            if (hasCauseAsParameter(ctorNode)) {
                result = true;
                break;
            }
        }
        return result;
    }

    private boolean hasCauseAsParameter(DetailAST ctorNode)
    {
        boolean result = false;
        DetailAST parameters = ctorNode.findFirstToken(TokenTypes.PARAMETERS);
        for (String parameterType : getParameterTypes(parameters)) {
            if (parameterType.equals("Throwable")
                    || parameterType.equals("Exception")) {
                result = true;
                break;
            }
        }
        return result;
    }
    
    /**
     * @param parameterDefAST
     * @return
     */
    private List<String> getParameterTypes(DetailAST parametersAST)
    {
        List<String> result = new LinkedList<String>();
        for (DetailAST parametersChild : getChildren(parametersAST)) {
            if(parametersChild.getType() == TokenTypes.PARAMETER_DEF){
                final DetailAST parameterType = parametersChild.findFirstToken(TokenTypes.TYPE);                
                final String parameter = parameterType.getFirstChild().getText();                
                result.add(parameter);
            }
        }
        return result;
    }

    private String getName(final DetailAST aClassOrCtorDefNode)
    {
        final DetailAST classNameIdent = aClassOrCtorDefNode
                .findFirstToken(TokenTypes.IDENT);
        return classNameIdent.getText();
    }
    
    /**
     * Gets a parent CLASS_DEF DetailAST node for given DetailAST
     * node.
     *
     * @param aNode
     *            A DetailAST node.
     * @return The parent CLASS_DEF node for the class that owns a Token
     *         is related to the given DetailAST node.
     * */
    private DetailAST getClassDef(final DetailAST aNode)
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
    
    
    private class ExceptionClass {

        private DetailAST classDefNode;
        private List<DetailAST> contructorDefList;

        public ExceptionClass(DetailAST classDefAST)
        {
            this.classDefNode = classDefAST;
            contructorDefList = new LinkedList<DetailAST>();
        }
        
        public void addConstructorDefNode(DetailAST ctorDefNode) {           
            contructorDefList.add(ctorDefNode);
        }

        /**
         * @return the classDef
         */
        public DetailAST getClassDefNode()
        {
            return classDefNode;
        }

        /**
         * @return the contructorDefList
         */
        public List<DetailAST> getContructorDefList()
        {
            return contructorDefList;
        }                
    }
    
    
}
