package com.puppycrawl.tools.checkstyle.checks.coding;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class AvoidModifiersForTypesCheck extends Check
{

    private static final String WARNING_MSG_KEY = "avoid.modifiers.for.types";    

    /**
     * Pattern object is used to store the regexp for the names of classes, that
     * could not have 'final' modifier.
     */
    private Pattern mForbiddenClassesRegexpFinal;

    /**
     * Pattern object is used to store the regexp for the names of classes, that
     * could not have 'static' modifier.
     */
    private Pattern mForbiddenClassesRegexpStatic;

    /**
     * Pattern object is used to store the regexp for the names of classes, that
     * could not have 'transient' modifier.
     */
    private Pattern mForbiddenClassesRegexpTransient;

    /**
     * Pattern object is used to store the regexp for the names of classes, that
     * could not have 'volatile' modifier.
     */
    private Pattern mForbiddenClassesRegexpVolatile;

    /**
     * Gets the regexp is currently used for the names of classes, that could
     * not have 'static' modifier.
     * @return String contains regexp is currently used for the names of
     *         classes, that could not have 'static' modifier.
     */
    public String getForbiddenClassesRegexpStatic()
    {
        return mForbiddenClassesRegexpStatic.toString();
    }

    /**
     * Sets the regexp for the names of classes, that could not have 'static'
     * modifier.
     * @param aPackageNameRegexp
     *        String contains the regex to set for the names of classes, that
     *        could not have 'static' modifier.
     */
    public void setForbiddenClassesRegexpStatic(String aForbiddenClassesRegexpStatic)
    {        
        String regexp = aForbiddenClassesRegexpStatic == null ? ""
                : aForbiddenClassesRegexpStatic;
        mForbiddenClassesRegexpStatic = Pattern.compile(regexp);
    }

    /**
     * Gets the regexp is currently used for the names of classes, that could
     * not have 'final' modifier.
     * @return String contains regexp is currently used for the names of
     *         classes, that could not have 'final' modifier.
     */
    public String getForbiddenClassesRegexpFinal()
    {
        return mForbiddenClassesRegexpFinal.toString();
    }

    /**
     * Sets the regexp for the names of classes, that could not have 'final'
     * modifier.
     * @param aPackageNameRegexp
     *        String contains the regex to set for the names of classes, that
     *        could not have 'final' modifier.
     */
    public void setForbiddenClassesRegexpFinal(String aForbiddenClassesRegexpFinal)
    {
        String regexp = aForbiddenClassesRegexpFinal == null ? ""
                : aForbiddenClassesRegexpFinal;
        mForbiddenClassesRegexpFinal = Pattern.compile(regexp);
    }

    /**
     * Gets the regexp is currently used for the names of classes, that could
     * not have 'transient' modifier.
     * @return String contains regexp is currently used for the names of
     *         classes, that could not have 'transient' modifier.
     */
    public String getForbiddenClassesRegexpTransient()
    {
        return mForbiddenClassesRegexpTransient.toString();
    }

    /**
     * Sets the regexp for the names of classes, that could not have 'transient'
     * modifier.
     * @param aPackageNameRegexp
     *        String contains the regex to set for the names of classes, that
     *        could not have 'transient' modifier.
     */
    public void setForbiddenClassesRegexpTransient(String aForbiddenClassesRegexpTransient)
    {
        String regexp = aForbiddenClassesRegexpTransient == null ? ""
                : aForbiddenClassesRegexpTransient;
        mForbiddenClassesRegexpTransient = Pattern.compile(regexp);
    }

    /**
     * Gets the regexp is currently used for the names of classes, that could
     * not have 'volatile' modifier.
     * @return String contains regexp is currently used for the names of
     *         classes, that could not have 'volatile' modifier.
     */
    public String getForbiddenClassesRegexpVolatile()
    {
        return mForbiddenClassesRegexpVolatile.toString();
    }

    /**
     * Sets the regexp for the names of classes, that could not have 'volatile'
     * modifier.
     * @param aPackageNameRegexp
     *        String contains the regex to set for the names of classes, that
     *        could not have 'volatile' modifier.
     */
    public void setForbiddenClassesRegexpVolatile(String aForbiddenClassesRegexpVolatile)
    {
        String regexp = aForbiddenClassesRegexpVolatile == null ? ""
                : aForbiddenClassesRegexpVolatile;
        mForbiddenClassesRegexpVolatile = Pattern.compile(regexp);
    }

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] { TokenTypes.VARIABLE_DEF };
    }

    public void visitToken(DetailAST aAst)
    {

        final String className = getClassName(aAst);

        if (className != null) {

            for (int modifierType : getModifiers(aAst)) {
                switch (modifierType) {
                case TokenTypes.LITERAL_STATIC:
                    if (mForbiddenClassesRegexpStatic.matcher(className)
                            .matches()) {
                        log(aAst, WARNING_MSG_KEY, className, "static");
                    }
                    break;
                case TokenTypes.FINAL:
                    if (mForbiddenClassesRegexpFinal.matcher(className)
                            .matches()) {
                        log(aAst, WARNING_MSG_KEY, className, "final");
                    }
                    break;
                case TokenTypes.LITERAL_TRANSIENT:
                    if (mForbiddenClassesRegexpTransient.matcher(className)
                            .matches()) {
                        log(aAst, WARNING_MSG_KEY, className, "transient");
                    }
                    break;
                case TokenTypes.LITERAL_VOLATILE:
                    if (mForbiddenClassesRegexpVolatile.matcher(className)
                            .matches()) {
                        log(aAst, WARNING_MSG_KEY, className, "volatile");
                    }
                    break;
                default:
                    throw new IllegalArgumentException(
                            "AvoidModifiersForTypesCheck: the processing got the "
                                    + "wrong input token: "
                                    + aAst.toString() + ", token type = "
                                    + TokenTypes.getTokenName(aAst.getType())
                                    + ".");
                }
            }
        }
    }


    /**
     * Gets the className of the defined variable.
     * @param variableDefNode
     * @return String contains the className of the defined variable or null if
     *         the current processed object is an array of primitive types
     */
    private static String getClassName(DetailAST variableDefNode)
    {
        String result = null;
        final DetailAST type = variableDefNode.findFirstToken(TokenTypes.TYPE);
        final DetailAST textWithoutDots = type.findFirstToken(TokenTypes.IDENT);

        if (textWithoutDots == null) {
            // if there are TokenTypes.DOT nodes in subTree.
            final DetailAST parentDotAST = type.findFirstToken(TokenTypes.DOT);
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

        if (result != null) {
            result = getClassName(result);
        }

        return result;
    }


    /**
     * Gets the class name from full (dotted) classPath.
     * @param aClassNameAndPath
     *        - the full (dotted) classPath
     * @return the name of the class is specified by the current full name&path.
     */
    private static String getClassName(final String aClassNameAndPath)
    {
        return aClassNameAndPath.replaceAll(".+\\.", "");
    }

    /**
     * Gets the modifiers of the defined variable (final,static,transient or
     * volatile).
     * @param aVariableDefAst
     * @return
     */
    private static List<Integer> getModifiers(DetailAST aVariableDefAst)
    {
        List<Integer> modifiersList = new LinkedList<Integer>();
        DetailAST modifiersAST = aVariableDefAst.findFirstToken(TokenTypes.MODIFIERS);
        for (DetailAST modifier : getChildren(modifiersAST)) {
            modifiersList.add(modifier.getType());
        }
        return modifiersList;
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
