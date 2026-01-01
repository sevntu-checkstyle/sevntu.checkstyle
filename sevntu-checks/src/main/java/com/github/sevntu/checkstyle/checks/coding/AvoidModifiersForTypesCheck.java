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

package com.github.sevntu.checkstyle.checks.coding;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import com.github.sevntu.checkstyle.SevntuUtil;
import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.TokenUtil;

/**
 * <p>
 * Disallow some set of modifiers for Java types specified by regexp.
 * </p>
 * <p>
 * Field modifiers types according to Java Spec:
 * (https://docs.oracle.com/javase/specs/jls/se7/html/jls-8.html#jls-8.3.1)
 * </p>
 * <ul>
 * <li><b>Annotation</b>: using the 'forbiddenClassesRegexpAnnotation' option.
 * <li><b>final</b>: using the 'forbiddenClassesRegexpFinal' option.
 * <li><b>static</b>: using the 'forbiddenClassesRegexpStatic'option.
 * <li><b>transient</b>: using the 'forbiddenClassesRegexpTransient' option.
 * <li><b>volatile</b>: using the 'forbiddenClassesRegexpVolatile' option.
 * <li><b>private</b>: using the 'forbiddenClassesRegexpPrivate' option.
 * <li><b>package-private</b>: using the 'forbiddenClassesRegexpPackagePrivate' option.
 * <li><b>protected</b>: using the 'forbiddenClassesRegexpProtected' option.
 * <li><b>public</b>: using the 'forbiddenClassesRegexpPublic' option.
 * </ul>
 * <p>
 * <b>Example 1:</b> Forbid use of 'static' modifiers for 'ULCComponents'
 * (http://ulc.canoo.com/ulccommunity/Contributions/Extensions/GoodPractices.html)
 * </p>
 *
 * <p>
 * Never keep instances of ULC classes in static variables (ULCIcons neither!). They cannot be
 * shared between different sessions.
 * </p>
 * <p>
 * So we can disallow "static" modifier for all ULC* components by setting up an
 * "forbiddenClassesRegexpStatic" option to "ULC.+" regexp String.
 * </p>
 *
 * <p>
 * <b>Configuration:</b>
 * </p>
 * <pre>
 * &lt;module name="TreeWalker"&gt;
 *      &lt;module name="AvoidModifiersForTypesCheck"&gt;
 *          &lt;property name="forbiddenClassesRegexpStatic" value="ULC.+"/&gt;
 *      &lt;/module&gt;
 * &lt;/module&gt;
 * </pre>
 *
 * <p>
 * <b>Example 2:</b> Forbid using annotation for fields: (e.g. <code>&#64;Autowired</code> ). This
 * can be done by setting up the "forbiddenClassesRegexpAnnotation" option to "Person" regexp
 * String.
 * </p>
 *
 * <p>
 * <b>Configuration:</b>
 * </p>
 * <pre>
 * &lt;module name="TreeWalker"&gt;
 *      &lt;module name="AvoidModifiersForTypesCheck"&gt;
 *          &lt;property name="forbiddenClassesRegexpAnnotation" value="Person"/&gt;
 *      &lt;/module&gt;
 * &lt;/module&gt;
 * </pre>
 *
 * <pre>
 * public class Customer {
 *
 *     &#64;Autowired
 *     private Person person; // Violation
 *
 *     private int type; // OK
 *
 *     private String action; // OK
 *
 * }
 * </pre>
 * <p>
 * <b>Example 3:</b> Forbid returning Logger out of the class, since it is a very bad practice as it
 * produce logs that are hard to investigate as logging class does not contains that code and search
 * should be done in other classes or in hierarchy (if filed is public or accessible by other
 * protected or package).
 * </p>
 * <p>
 * This check can be activated by setting up the "forbiddenClassesRegexpPublic",
 * "forbiddenClassesRegexpPackagePrivate" and "forbiddenClassesRegexpProtected" options to "Logger"
 * regexp String.
 * </p>
 *
 * <p>
 * <b>Configuration:</b>
 * </p>
 * <pre>
 * &lt;module name="TreeWalker"&gt;
 *      &lt;module name="AvoidModifiersForTypesCheck"&gt;
 *          &lt;property name="forbiddenClassesRegexpProtected" value="Logger"/&gt;
 *          &lt;property name="forbiddenClassesRegexpPublic" value="Logger"/&gt;
 *          &lt;property name="forbiddenClassesRegexpPackagePrivate" value="Logger"/&gt;
 *      &lt;module&gt;
 * &lt;/module&gt;
 * </pre>
 *
 * <pre>
 * public class Check {
 *
 *     private Logger log1 = Logger.getLogger(getClass().getName()); // OK
 *
 *     protected Logger log2 = Logger.getLogger(getClass().getName()); // Violation
 *
 *     public Logger log3 = Logger.getLogger(getClass().getName()); // Violation
 *
 *     Logger log4 = Logger.getLogger(getClass().getName()); // Violation
 *
 * }
 * </pre>
 *
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil Yaroslavtsev</a>
 * @author <a href="mailto:yasser.aziza@gmail.com">Yasser Aziza</a>
 * @since 1.8.0
 */
public class AvoidModifiersForTypesCheck extends AbstractCheck {

    /**
     * The key is pointing to the message text String in
     * "messages.properties file".
     */
    public static final String MSG_KEY = "avoid.modifiers.for.types";

    /**
     * Pattern object is used to store the regexp for the names of classes, that
     * could not have 'annotation' modifier.
     */
    private Pattern forbiddenClassesRegexpAnnotation = Pattern.compile("");

    /**
     * Pattern object is used to store the regexp for the names of classes, that
     * could not have 'final' modifier.
     */
    private Pattern forbiddenClassesRegexpFinal = Pattern.compile("");

    /**
     * Pattern object is used to store the regexp for the names of classes, that
     * could not have 'static' modifier.
     */
    private Pattern forbiddenClassesRegexpStatic = Pattern.compile("ULC.+");

    /**
     * Pattern object is used to store the regexp for the names of classes, that
     * could not have 'transient' modifier.
     */
    private Pattern forbiddenClassesRegexpTransient = Pattern.compile("");

    /**
     * Pattern object is used to store the regexp for the names of classes, that
     * could not have 'volatile' modifier.
     */
    private Pattern forbiddenClassesRegexpVolatile = Pattern.compile("");

    /**
     * Pattern object is used to store the regexp for the names of classes, that
     * could not have 'private' modifier.
     */
    private Pattern forbiddenClassesRegexpPrivate = Pattern.compile("");

    /**
     * Pattern object is used to store the regexp for the names of classes, that
     * could not have no modifier 'package-private'.
     */
    private Pattern forbiddenClassesRegexpPackagePrivate = Pattern.compile("");

    /**
     * Pattern object is used to store the regexp for the names of classes, that
     * could not have 'protected' modifier.
     */
    private Pattern forbiddenClassesRegexpProtected = Pattern.compile("");

    /**
     * Pattern object is used to store the regexp for the names of classes, that
     * could not have 'public' modifier.
     */
    private Pattern forbiddenClassesRegexpPublic = Pattern.compile("");

    /**
     * Sets the regexp for the names of classes, that could not have 'annotation'
     * modifier.
     *
     * @param forbiddenClassesRegexpAnnotation
     *        String contains the regex to set for the names of classes, that
     *        could not have 'annotation' modifier.
     */
    public void setForbiddenClassesRegexpAnnotation(String forbiddenClassesRegexpAnnotation) {
        final String regexp;

        if (forbiddenClassesRegexpAnnotation == null) {
            regexp = "";
        }
        else {
            regexp = forbiddenClassesRegexpAnnotation;
        }

        this.forbiddenClassesRegexpAnnotation = Pattern.compile(regexp);
    }

    /**
     * Sets the regexp for the names of classes, that could not have 'final'
     * modifier.
     *
     * @param forbiddenClassesRegexpFinal
     *        String contains the regex to set for the names of classes, that
     *        could not have 'final' modifier.
     */
    public void setForbiddenClassesRegexpFinal(String forbiddenClassesRegexpFinal) {
        final String regexp;

        if (forbiddenClassesRegexpFinal == null) {
            regexp = "";
        }
        else {
            regexp = forbiddenClassesRegexpFinal;
        }

        this.forbiddenClassesRegexpFinal = Pattern.compile(regexp);
    }

    /**
     * Sets the regexp for the names of classes, that could not have 'static'
     * modifier.
     *
     * @param forbiddenClassesRegexpStatic
     *        String contains the regex to set for the names of classes, that
     *        could not have 'static' modifier.
     */
    public void setForbiddenClassesRegexpStatic(String forbiddenClassesRegexpStatic) {
        final String regexp;

        if (forbiddenClassesRegexpStatic == null) {
            regexp = "";
        }
        else {
            regexp = forbiddenClassesRegexpStatic;
        }

        this.forbiddenClassesRegexpStatic = Pattern.compile(regexp);
    }

    /**
     * Sets the regexp for the names of classes, that could not have 'transient'
     * modifier.
     *
     * @param forbiddenClassesRegexpTransient
     *        String contains the regex to set for the names of classes, that
     *        could not have 'transient' modifier.
     */
    public void setForbiddenClassesRegexpTransient(String forbiddenClassesRegexpTransient) {
        final String regexp;

        if (forbiddenClassesRegexpTransient == null) {
            regexp = "";
        }
        else {
            regexp = forbiddenClassesRegexpTransient;
        }

        this.forbiddenClassesRegexpTransient = Pattern.compile(regexp);
    }

    /**
     * Sets the regexp for the names of classes, that could not have 'volatile'
     * modifier.
     *
     * @param forbiddenClassesRegexpVolatile
     *        String contains the regex to set for the names of classes, that
     *        could not have 'volatile' modifier.
     */
    public void setForbiddenClassesRegexpVolatile(String forbiddenClassesRegexpVolatile) {
        final String regexp;

        if (forbiddenClassesRegexpVolatile == null) {
            regexp = "";
        }
        else {
            regexp = forbiddenClassesRegexpVolatile;
        }

        this.forbiddenClassesRegexpVolatile = Pattern.compile(regexp);
    }

    /**
     * Sets the regexp for the names of classes, that could not have 'private'
     * modifier.
     *
     * @param forbiddenClassesRegexpPrivate
     *        String contains the regex to set for the names of classes, that
     *        could not have 'private' modifier.
     */
    public void setForbiddenClassesRegexpPrivate(String forbiddenClassesRegexpPrivate) {
        final String regexp;

        if (forbiddenClassesRegexpPrivate == null) {
            regexp = "";
        }
        else {
            regexp = forbiddenClassesRegexpPrivate;
        }

        this.forbiddenClassesRegexpPrivate = Pattern.compile(regexp);
    }

    /**
     * Sets the regexp for the names of classes, that could not have no modifier
     * ('package-private').
     *
     * @param forbiddenClassesRegexpPackagePrivate
     *        String contains the regex to set for the names of classes, that
     *        could not have no modifier ('package-private').
     */
    public void setForbiddenClassesRegexpPackagePrivate(
            String forbiddenClassesRegexpPackagePrivate) {
        final String regexp;

        if (forbiddenClassesRegexpPackagePrivate == null) {
            regexp = "";
        }
        else {
            regexp = forbiddenClassesRegexpPackagePrivate;
        }

        this.forbiddenClassesRegexpPackagePrivate = Pattern.compile(regexp);
    }

    /**
     * Sets the regexp for the names of classes, that could not have 'protected'
     * modifier.
     *
     * @param forbiddenClassesRegexpProtected
     *        String contains the regex to set for the names of classes, that
     *        could not have 'protected' modifier.
     */
    public void setForbiddenClassesRegexpProtected(String forbiddenClassesRegexpProtected) {
        final String regexp;

        if (forbiddenClassesRegexpProtected == null) {
            regexp = "";
        }
        else {
            regexp = forbiddenClassesRegexpProtected;
        }

        this.forbiddenClassesRegexpProtected = Pattern.compile(regexp);
    }

    /**
     * Sets the regexp for the names of classes, that could not have 'public'
     * modifier.
     *
     * @param forbiddenClassesRegexpPublic
     *        String contains the regex to set for the names of classes, that
     *        could not have 'public' modifier.
     */
    public void setForbiddenClassesRegexpPublic(String forbiddenClassesRegexpPublic) {
        final String regexp;

        if (forbiddenClassesRegexpPublic == null) {
            regexp = "";
        }
        else {
            regexp = forbiddenClassesRegexpPublic;
        }

        this.forbiddenClassesRegexpPublic = Pattern.compile(regexp);
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] {TokenTypes.VARIABLE_DEF };
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
    public void visitToken(DetailAST ast) {
        final String classNameAndPath = getClassNameAndPath(ast);

        if (classNameAndPath != null) {
            final String className = getClassName(classNameAndPath);

            final Set<Integer> modifiersSet = getModifiers(ast);

            if (ast.getParent().getType() == TokenTypes.OBJBLOCK
                    && !modifiersSet.contains(TokenTypes.LITERAL_PUBLIC)
                    && !modifiersSet.contains(TokenTypes.LITERAL_PROTECTED)
                    && !modifiersSet.contains(TokenTypes.LITERAL_PRIVATE)
                    && forbiddenClassesRegexpPackagePrivate.matcher(className).matches()) {
                log(ast, MSG_KEY, className, "package-private");
            }

            for (int modifierType : modifiersSet) {
                if (match(modifierType, className)) {
                    String tokenName = TokenUtil.getTokenName(modifierType);

                    // Remove literal prefix and switch to lower case for better readability
                    tokenName = tokenName.toLowerCase(Locale.ENGLISH).replaceAll("literal_", "");

                    log(ast, MSG_KEY, className, tokenName);
                }
            }
        }
    }

    /**
     * Checks whether a specific Java modifier is used in a given class with
     * the specified regular expression.
     *
     * @param modifierType the modifier type
     * @param className the class name
     * @return either <code>true</code> if the regexp match the className,
     *     else <code>false</code>
     */
    private boolean match(int modifierType, String className) {
        final Pattern pattern = mapToRegExp(modifierType);
        return pattern.matcher(className).matches();
    }

    /**
     * Maps the modifierType to a regular expression.
     *
     * @param modifierType the modifier type
     * @return the Pattern object storing the regexp for the names of classes,
     *     that must not have the modifierType.
     */
    private Pattern mapToRegExp(int modifierType) {
        Pattern result = null;

        switch (modifierType) {
            case TokenTypes.ANNOTATION:
                result = forbiddenClassesRegexpAnnotation;
                break;
            case TokenTypes.FINAL:
                result = forbiddenClassesRegexpFinal;
                break;
            case TokenTypes.LITERAL_STATIC:
                result = forbiddenClassesRegexpStatic;
                break;
            case TokenTypes.LITERAL_TRANSIENT:
                result = forbiddenClassesRegexpTransient;
                break;
            case TokenTypes.LITERAL_VOLATILE:
                result = forbiddenClassesRegexpVolatile;
                break;
            case TokenTypes.LITERAL_PRIVATE:
                result = forbiddenClassesRegexpPrivate;
                break;
            case TokenTypes.LITERAL_PROTECTED:
                result = forbiddenClassesRegexpProtected;
                break;
            case TokenTypes.LITERAL_PUBLIC:
                result = forbiddenClassesRegexpPublic;
                break;
            default:
                SevntuUtil.reportInvalidToken(modifierType);
                break;
        }

        return result;
    }

    /**
     * Gets the full className of the defined variable.
     *
     * @param variableDefNode
     *        A DetailAST node is related to variable definition (VARIABLE_DEF
     *        node type).
     * @return String contains the className of the defined variable or null if
     *         the current processed object is an array of primitive types
     */
    private static String getClassNameAndPath(DetailAST variableDefNode) {
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
        }
        // if subtree doesn`t contain dots.
        else {
            result = textWithoutDots.getText();
        }

        return result;
    }

    /**
     * Gets the class name from full (dotted) classPath.
     *
     * @param classNameAndPath
     *        - the full (dotted) classPath. Must not be null.
     * @return the name of the class is specified by the current full name&path.
     *         Guaranteed to not be null if aClassNameAndPath is not null.
     */
    private static String getClassName(final String classNameAndPath) {
        return classNameAndPath.replaceAll(".+\\.", "");
    }

    /**
     * Gets the modifiers of the defined variable (annotation, public, private, final, static,
     * transient or volatile).
     *
     * @param variableDefAst
     *        A DetailAST node is related to the variable definition
     *        (VARIABLE_DEF type)
     * @return List of token types is related to the given variable modifiers.
     */
    private static Set<Integer> getModifiers(DetailAST variableDefAst) {
        final Set<Integer> modifiersSet = new HashSet<>();
        final DetailAST modifiersAST = variableDefAst
                .findFirstToken(TokenTypes.MODIFIERS);
        for (DetailAST modifier : getChildren(modifiersAST)) {
            modifiersSet.add(modifier.getType());
        }

        return modifiersSet;
    }

    /**
     * Gets all the children which are one level below on the current DetailAST
     * parent node.
     *
     * @param node
     *        The parent node.
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
