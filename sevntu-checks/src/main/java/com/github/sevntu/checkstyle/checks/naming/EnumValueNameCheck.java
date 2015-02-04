////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2013  Oliver Burn
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
package com.github.sevntu.checkstyle.checks.naming;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;
import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.api.Utils;

/**
 * Check forces enum values to match the specific pattern. According to
 * "Java Coding Style" by Achut Reddy p 3.3 constants include
 * "all static final object reference types that are never followed by "
 * ." (dot).", i.e. enums, which are followed by dot while used in the code are
 * to be treated as static object references, while enums, that are not used
 * with following dot, should be treated as constants.
 * <p>
 * Enums are defined to be used as class have some own methods. This condition
 * is used to distinguish between Values Enumeration and Class Enumeration.
 * Values Enumeration looks like the following: <code>
 * enum SimpleErrorEnum
 *   {
 *       FIRST_SIMPLE, SECOND_SIMPLE, THIRD_SIMPLE;
 *   }
 * </code>
 * <p>
 * While Class Enumeration has some methods, for example: <code>
 * enum SimpleErrorEnum
 *   {
 *       FIRST_SIMPLE, SECOND_SIMPLE, THIRD_SIMPLE;
 * 
 *       public String toString() {
 *           return Integer.toString(ordinal() + 10);
 *       }
 *   }
 * </code>
 * <p>
 * Name format for Class Enumeration values is specified with
 * {@link #setObjFormat(String)} , while format for enum constants - with
 * {@link #setConstFormat(String)}
 * <p>
 * To avoid assuming enum as static object reference, while using some specific
 * methods, {@link #setExcludes(String[])} can be used. For example to make enum in
 * the previous example a constant set Excludes property to a value
 * <code>toString</code>
 * <p>
 * By default <code>toString</code> is used as an exclusion.
 *
 * @see <a
 *      href=" http://www.scribd.com/doc/15884743/Java-Coding-Style-by-Achut-Reddy">"Java Coding Style"
 *      by Achut Reddy</a>
 *
 * @author Pavel Baranchikov
 */
public class EnumValueNameCheck extends Check
{
    public static final String MSG_CONST = "enum.name.const.invalidPattern";
    public static final String MSG_OBJ = "enum.name.obj.invalidPattern";

    /**
     * Default pattern for Values Enumeration names.
     */
    public static final String DEFAULT_CONST_PATTERN =
            "^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$";

    /**
     * Default pattern for Class Enumeration names.
     */
    public static final String DEFAULT_OBJ_PATTERN = "^[A-Z][a-zA-Z0-9]*$";

    /**
     * Default exclusions value.
     */
    public static final String[] DEFAULT_EXCLUSION = { "toString" };

    /**
     * Regular expression to test Class Enumeration names against.
     */
    private Pattern objRegexp;

    /**
     * Format for Class Enumeration names to check for. Compiled to
     * {@link #objRegexp}
     */
    private String objFormat;

    /**
     * Regular expression to test Values Enumeration names against.
     */
    private Pattern constRegexp;

    /**
     * Format for Values Enumeration names to check for. Compiled to
     * {@link #constRegexp}
     */
    private String constFormat;

    /**
     * Method and field names to exclude from check.
     */
    private final List<Pattern> excludes;

    /**
     * Constructs check with the default pattern.
     */
    public EnumValueNameCheck()
    {
        setConstFormat(DEFAULT_CONST_PATTERN);
        setObjFormat(DEFAULT_OBJ_PATTERN);
        excludes = Lists.newArrayList();
        setExcludes(DEFAULT_EXCLUSION);
    }

    /**
     * Method sets format to match Class Enumeration names.
     * @param constRegexp format to check against
     */
    public final void setConstFormat(String constRegexp)
    {
        this.constRegexp = Utils.getPattern(constRegexp, 0);
        constFormat = constRegexp;
    }

    /**
     * Method sets format to match Values Enumeration names.
     * @param objectRegexp format to check against
     */
    public final void setObjFormat(String objectRegexp)
    {
        objRegexp = Utils.getPattern(objectRegexp, 0);
        objFormat = objectRegexp;
    }

    /**
     * Method sets method and field name exclusions.
     * @param excludes
     *        comma separated list or regular expressions
     */
    public void setExcludes(String[] excludes)
    {
        this.excludes.clear();
        for (String exclude: excludes) {
            this.excludes.add(Utils.getPattern(exclude));
        }
    }

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] { TokenTypes.ENUM_CONSTANT_DEF };
    }

    @Override
    public void visitToken(DetailAST ast)
    {
        final DetailAST nameAST = ast.findFirstToken(TokenTypes.IDENT);
        final boolean enumIsClass = isClassEnumeration(ast);
        final Pattern pattern = enumIsClass ? objRegexp
                : constRegexp;
        if (!pattern.matcher(nameAST.getText()).find()) {
            final String format = enumIsClass ? objFormat
                    : constFormat;
            final String msg = enumIsClass ? MSG_OBJ : MSG_CONST;
            log(nameAST.getLineNo(),
                    nameAST.getColumnNo(),
                    msg,
                    nameAST.getText(),
                    format);
        }
    }

    /**
     * Method determines, whether the Enum, specified as parameter has any
     * members. Method uses {@link #excludes} while looking though the tree
     * nodes
     *
     * @param ast
     *        ast to check
     * @return <code>true</code> if enum is a class enumeration
     */
    private boolean isClassEnumeration(DetailAST ast)
    {
        return hasMembers(ast, excludes);
    }

    /**
     * Method determines whether the specified enum is a constant or is an
     * object.
     * 
     * @param ast
     *        token of a enum value definition
     * @param excludes
     *        list of ignored member names
     * @return <code>true</code> if enum is a class enumeration
     */
    private static boolean
            hasMembers(DetailAST ast, List<Pattern> excludes)
    {
        final DetailAST objBlock = ast.getParent();
        assert (objBlock.getType() == TokenTypes.OBJBLOCK);
        boolean memberFound = false;
        for (DetailAST member = objBlock.getFirstChild(); member != null; member = member
                .getNextSibling()) {
            if (member.getType() == TokenTypes.METHOD_DEF
                    || member.getType() == TokenTypes.VARIABLE_DEF) {
                final DetailAST memberIdent = member
                        .findFirstToken(TokenTypes.IDENT);
                assert (memberIdent != null);
                final String identifierStr = memberIdent.getText();
                if (!isAnyMatched(excludes, identifierStr)) {
                    memberFound = true;
                    break;
                }
            }
        }
        return memberFound;
    }

    /**
     * Returns whether at least one of patterns are successfully matched arainst
     * the specified string value
     * @param patterns
     *        pattern list to match against
     * @param value
     *        value to match
     * @return <code>true</code> if at least one pattern have been successfully
     *         matched.
     */
    private static boolean
            isAnyMatched(Collection<Pattern> patterns, String value)
    {
        for (Pattern pattern : patterns) {
            if (pattern.matcher(value).find()) {
                return true;
            }
        }
        return false;
    }

}
