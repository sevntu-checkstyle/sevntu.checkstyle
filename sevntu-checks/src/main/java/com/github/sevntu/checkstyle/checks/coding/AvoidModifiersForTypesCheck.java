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

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * Disallow some set of modifiers for Java types specified by regexp. <br>
 * <br>
 * Only 4 types according to Java Spec: static, final, transient, volatile. <br>
 * <br>
 * Example: <br>
 * "static" modifier for <a
 * href="http://ulc.canoo.com/ulccommunity/Contributions/Extensions/GoodPractices.html"
 * >ULCComponents</a> <br/> 
 * Static variables <br/>
 * Never keep instances of ULC classes in static variables (ULCIcons neither!). 
 * They cannot be shared between different sessions.  <br>
 * So we can disallow "static" modifier for all ULC* components by setting up an
 * "forbiddenClassesRegexpStatic" option to "ULC.+" regexp String.
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil
 *         Yaroslavtsev</a>
 */
public class AvoidModifiersForTypesCheck extends Check
{

    /**
     * The key is pointing to the message text String in
     * "messages.properties file".
     */
    public static final String MSG_KEY = "avoid.modifiers.for.types";

    /**
     * Pattern object is used to store the regexp for the names of classes, that
     * could not have 'final' modifier.
     */
    private Pattern mForbiddenClassesRegexpFinal = Pattern.compile("");

    /**
     * Pattern object is used to store the regexp for the names of classes, that
     * could not have 'static' modifier.
     */
    private Pattern mForbiddenClassesRegexpStatic = Pattern.compile("ULC.+");

    /**
     * Pattern object is used to store the regexp for the names of classes, that
     * could not have 'transient' modifier.
     */
    private Pattern mForbiddenClassesRegexpTransient = Pattern.compile("");

    /**
     * Pattern object is used to store the regexp for the names of classes, that
     * could not have 'volatile' modifier.
     */
    private Pattern mForbiddenClassesRegexpVolatile = Pattern.compile("");

    /**
     * Sets the regexp for the names of classes, that could not have 'static'
     * modifier.
     * @param aForbiddenClassesRegexpStatic
     *        String contains the regex to set for the names of classes, that
     *        could not have 'static' modifier.
     */
    public void setForbiddenClassesRegexpStatic(
            String aForbiddenClassesRegexpStatic)
    {
        final String regexp = aForbiddenClassesRegexpStatic == null ? ""
                : aForbiddenClassesRegexpStatic;
        mForbiddenClassesRegexpStatic = Pattern.compile(regexp);
    }

    /**
     * Sets the regexp for the names of classes, that could not have 'final'
     * modifier.
     * @param aForbiddenClassesRegexpFinal
     *        String contains the regex to set for the names of classes, that
     *        could not have 'final' modifier.
     */
    public void setForbiddenClassesRegexpFinal(
            String aForbiddenClassesRegexpFinal)
    {
        final String regexp = aForbiddenClassesRegexpFinal == null ? ""
                : aForbiddenClassesRegexpFinal;
        mForbiddenClassesRegexpFinal = Pattern.compile(regexp);
    }

    /**
     * Sets the regexp for the names of classes, that could not have 'transient'
     * modifier.
     * @param aForbiddenClassesRegexpTransient
     *        String contains the regex to set for the names of classes, that
     *        could not have 'transient' modifier.
     */
    public void setForbiddenClassesRegexpTransient(
            String aForbiddenClassesRegexpTransient)
    {
        final String regexp = aForbiddenClassesRegexpTransient == null ? ""
                : aForbiddenClassesRegexpTransient;
        mForbiddenClassesRegexpTransient = Pattern.compile(regexp);
    }

    /**
     * Sets the regexp for the names of classes, that could not have 'volatile'
     * modifier.
     * @param aForbiddenClassesRegexpVolatile
     *        String contains the regex to set for the names of classes, that
     *        could not have 'volatile' modifier.
     */
    public void setForbiddenClassesRegexpVolatile(
            String aForbiddenClassesRegexpVolatile)
    {
        final String regexp = aForbiddenClassesRegexpVolatile == null ? ""
                : aForbiddenClassesRegexpVolatile;
        mForbiddenClassesRegexpVolatile = Pattern.compile(regexp);
    }

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] {TokenTypes.VARIABLE_DEF };
    }

    @Override
    public void visitToken(DetailAST aAst)
    {
        final String classNameAndPath = getClassNameAndPath(aAst);

        if (classNameAndPath != null) {

            final String className = getClassName(classNameAndPath);

            for (int modifierType : getModifiers(aAst)) {
                switch (modifierType) {
                case TokenTypes.LITERAL_STATIC:
                    if (mForbiddenClassesRegexpStatic.matcher(className)
                            .matches())
                    {
                        log(aAst, MSG_KEY, className, "static");
                    }
                    break;
                case TokenTypes.FINAL:
                    if (mForbiddenClassesRegexpFinal.matcher(className)
                            .matches())
                    {
                        log(aAst, MSG_KEY, className, "final");
                    }
                    break;
                case TokenTypes.LITERAL_TRANSIENT:
                    if (mForbiddenClassesRegexpTransient.matcher(className)
                            .matches())
                    {
                        log(aAst, MSG_KEY, className, "transient");
                    }
                    break;
                case TokenTypes.LITERAL_VOLATILE:
                    if (mForbiddenClassesRegexpVolatile.matcher(className)
                            .matches())
                    {
                        log(aAst, MSG_KEY, className, "volatile");
                    }
                    break;
                default:
                    // no code (unreachable block for current Java spec)
                    break;
                }
            }
        }
    }

    /**
     * Gets the full className of the defined variable.
     * @param aVariableDefNode
     *        A DetailAST node is related to variable definition (VARIABLE_DEF
     *        node type).
     * @return String contains the className of the defined variable or null if
     *         the current processed object is an array of primitive types
     */
    private static String getClassNameAndPath(DetailAST aVariableDefNode)
    {
        String result = null;
        final DetailAST type = aVariableDefNode.findFirstToken(TokenTypes.TYPE);
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
        else { // if subtree doesn`t contain dots.
            result = textWithoutDots.getText();
        }

        return result;
    }


    /**
     * Gets the class name from full (dotted) classPath.
     * @param aClassNameAndPath
     *        - the full (dotted) classPath. Must not be null.
     * @return the name of the class is specified by the current full name&path.
     *         Guaranteed to not be null if aClassNameAndPath is not null.
     */
    private static String getClassName(final String aClassNameAndPath)
    {
        return aClassNameAndPath.replaceAll(".+\\.", "");
    }

    /**
     * Gets the modifiers of the defined variable (final, static, transient or
     * volatile).
     * @param aVariableDefAst
     *        A DeatilAST node is related to the variable definition
     *        (VARIABLE_DEF type)
     * @return List of token types is related to the given variable modifiers.
     */
    private static List<Integer> getModifiers(DetailAST aVariableDefAst)
    {
        final List<Integer> modifiersList = new LinkedList<Integer>();
        final DetailAST modifiersAST = aVariableDefAst
                .findFirstToken(TokenTypes.MODIFIERS);
        for (DetailAST modifier : getChildren(modifiersAST)) {
            modifiersList.add(modifier.getType());
        }
        return modifiersList;
    }

    /**
     * Gets all the children which are one level below on the current DetailAST
     * parent node.
     * @param aNode
     *        The parent node.
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
