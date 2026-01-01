///////////////////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code and other text files for adherence to a set of rules.
// Copyright (C) 2001-2026 the original author or authors.
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
///////////////////////////////////////////////////////////////////////////////////////////////

package com.github.sevntu.checkstyle.checks.design;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.github.sevntu.checkstyle.SevntuUtil;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Checks that any Exception class which matches the defined className regexp
 * have at least one constructor with Exception cause as a parameter. <br><p>
 * Rationale: <br><br>
 * "A special form of exception translation called exception chaining is
 * appropriate in cases where the lower-level exception might be helpful to
 * someone debugging the problem that caused the higher-level exception. The
 * lower-level exception (the cause) is passed to the higher-level.."
 * <i>[Joshua Bloch - Effective Java 2nd Edition, Chapter 4, Item 61]</i>
 * </p><p>Parameters: </p><ol>
 * <li>Exception classNames regexp. ("classNamesRegexp" option).</li>
 * <li>regexp to ignore classes by names ("ignoredClassNamesRegexp" option).
 * </li><li>The names of classes which would be considered as Exception cause
 * ("allowedCauseTypes" option).</li></ol><br>
 *
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil
 *         Yaroslavtsev</a>
 * @since 1.8.0
 */
public class CauseParameterInExceptionCheck extends AbstractCheck {

    /**
     * A key is pointing to the warning message text in "messages.properties"
     * file.
     */
    public static final String MSG_KEY = "cause.parameter.in.exception";

    /**
     * Pattern object is used to store the regexp for the names of classes, that
     * should be checked. Default value = ".+Exception".
     */
    private Pattern classNamesRegexp = Pattern.compile(".+Exception");

    /**
     * Pattern object is used to store the regexp for the names of classes, that
     * should be ignored by check.
     */
    private Pattern ignoredClassNamesRegexp = Pattern.compile("");

    /**
     * List contains the names of classes which would be considered as Exception
     * cause. Default value = "Throwable, Exception".
     */
    private final Set<String> allowedCauseTypes = new HashSet<>();

    /**
     * List of DetailAST objects which are related to Exception classes that
     * need to be warned.
     */
    private final List<DetailAST> exceptionClassesToWarn =
            new LinkedList<>();

    /**
     * Creates the new check instance.
     */
    public CauseParameterInExceptionCheck() {
        allowedCauseTypes.add("Exception");
        allowedCauseTypes.add("Throwable");
    }

    /**
     * Sets the regexp for the names of classes, that should be checked.
     *
     * @param classNamesRegexp
     *        String contains the regex to set for the names of classes, that
     *        should be checked.
     */
    public void setClassNamesRegexp(String classNamesRegexp) {
        final String regexp;
        if (classNamesRegexp == null) {
            regexp = "";
        }
        else {
            regexp = classNamesRegexp;
        }
        this.classNamesRegexp = Pattern.compile(regexp);
    }

    /**
     * Sets the regexp for the names of classes, that should be ignored by
     * check.
     *
     * @param ignoredClassNamesRegexp
     *        String contains the regex to set for the names of classes, that
     *        should be ignored by check.
     */
    public void setIgnoredClassNamesRegexp(String ignoredClassNamesRegexp) {
        final String regexp;
        if (ignoredClassNamesRegexp == null) {
            regexp = "";
        }
        else {
            regexp = ignoredClassNamesRegexp;
        }
        this.ignoredClassNamesRegexp = Pattern.compile(regexp);
    }

    /**
     * Sets the names of classes which would be considered as Exception cause.
     *
     * @param allowedCauseTypes
     *        - the list of classNames separated by a comma. ClassName should be
     *        short, such as "NullpointerException", do not use full name -
     *        java.lang.NullpointerException;
     */
    public void setAllowedCauseTypes(final String... allowedCauseTypes) {
        this.allowedCauseTypes.clear();
        for (String name : allowedCauseTypes) {
            this.allowedCauseTypes.add(name);
        }
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {
            TokenTypes.CLASS_DEF,
            TokenTypes.CTOR_DEF,
        };
    }

    @Override
    public int[] getAcceptableTokens() {
        return getDefaultTokens();
    }

    @Override
    public int[] getRequiredTokens() {
        return getDefaultTokens();
    }

    @Override
    public void beginTree(DetailAST rootAST) {
        exceptionClassesToWarn.clear();
    }

    @Override
    public void visitToken(DetailAST ast) {
        switch (ast.getType()) {
            case TokenTypes.CLASS_DEF:
                final String exceptionClassName = getName(ast);
                if (classNamesRegexp.matcher(exceptionClassName).matches()
                    && !ignoredClassNamesRegexp.matcher(exceptionClassName)
                        .matches()) {
                    exceptionClassesToWarn.add(ast);
                }
                break;
            case TokenTypes.CTOR_DEF:
                final DetailAST exceptionClass = getClassDef(ast);
                if (exceptionClassesToWarn.contains(exceptionClass)
                    && hasCauseAsParameter(ast)) {
                    exceptionClassesToWarn.remove(exceptionClass);
                }
                break;
            default:
                SevntuUtil.reportInvalidToken(ast.getType());
                break;
        }
    }

    @Override
    public void finishTree(DetailAST treeRootAST) {
        for (DetailAST classDefNode : exceptionClassesToWarn) {
            log(classDefNode, MSG_KEY, getName(classDefNode));
        }
    }

    /**
     * Checks that the given constructor contains exception cause as a
     * parameter.
     *
     * @param ctorDefNode
     *        The CTOR_DEF DetailAST node is related to the constructor
     *        definition.
     * @return true if the given ctor contains exception cause as a parameter
     *         and false otherwise.
     */
    private boolean hasCauseAsParameter(DetailAST ctorDefNode) {
        boolean result = false;
        final DetailAST parameters =
                ctorDefNode.findFirstToken(TokenTypes.PARAMETERS);
        for (String parameterType : getParameterTypes(parameters)) {
            if (allowedCauseTypes.contains(parameterType)) {
                result = true;
                break;
            }
        }
        return result;
    }

    /**
     * Gets the list of classNames for given constructor parameters types.
     *
     * @param parametersAST - A PARAMETERS DetailAST.
     * @return the list of classNames for given constructor parameters types.
     */
    private static List<String> getParameterTypes(DetailAST parametersAST) {
        final List<String> result = new LinkedList<>();
        for (DetailAST parametersChild : getChildren(parametersAST)) {
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
     *
     * @param classOrCtorDefNode
     *        the a CLASS_DEF or CTOR_DEF node
     * @return the name of class or constructor is related to CLASS_DEF or
     *         CTOR_DEF node.
     */
    private static String getName(final DetailAST classOrCtorDefNode) {
        final DetailAST classNameIdent = classOrCtorDefNode
                .findFirstToken(TokenTypes.IDENT);
        return classNameIdent.getText();
    }

    /**
     * Gets the parent CLASS_DEF DetailAST node for given DetailAST node.
     *
     * @param node
     *        The DetailAST node.
     * @return The parent CLASS_DEF node for the class that owns a token is
     *         related to the given DetailAST node or null if given token is not
     *         located in any class.
     */
    private static DetailAST getClassDef(final DetailAST node) {
        DetailAST curNode = node;
        while (curNode != null && curNode.getType() != TokenTypes.CLASS_DEF) {
            curNode = curNode.getParent();
        }
        return curNode;
    }

    /**
     * Gets all the children which are one level below on the current DetailAST
     * parent node.
     *
     * @param node
     *        Current parent node.
     * @return The list of children one level below on the current parent node.
     */
    private static List<DetailAST> getChildren(final DetailAST node) {
        final List<DetailAST> result = new LinkedList<>();
        DetailAST curNode = node.getFirstChild();
        while (curNode != null) {
            result.add(curNode);
            curNode = curNode.getNextSibling();
        }
        return result;
    }

}
