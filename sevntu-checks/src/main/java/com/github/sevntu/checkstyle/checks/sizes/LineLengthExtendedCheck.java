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

package com.github.sevntu.checkstyle.checks.sizes;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.CommonUtil;

/**
 * Checks for long lines.
 *
 * <p>
 * Rationale: Long lines are hard to read in printouts or if developers have limited screen
 * space for the source code,
 * e.g. if the IDE displays additional information like project tree, class hierarchy, etc.
 * </p>
 *
 * <p>
 * Note: Support for the special handling of imports in CheckStyle Version 2 has been dropped as
 * it is a special case of regexp: The user can set the ignorePattern to "^import" and achieve
 * the same effect.
 * </p>
 * <p>
 * The default maximum allowable line length is 80 characters. To change the maximum, set
 * property max.
 * </p>
 * <p>
 * To ignore lines in the check, set property ignorePattern to a regular expression for the
 * lines to ignore.
 * </p>
 * <p>
 * An example of how to configure the check is:
 * </p>
 *
 * <pre>
 * &lt;module name="LineLength"/&gt;
 * </pre>
 * <p>
 * An example of how to configure the check to accept lines up to 120 characters long is:
 * </p>
 *
 * <pre>
 * &lt;module name="LineLength"&gt;
 *    &lt;property name="max" value="120"/&gt;
 * &lt;/module&gt;
 * </pre>
 * <p>
 * An example of how to configure the check to ignore lines that begin with &quot; * &quot;,
 * followed by just one word, such as within a Javadoc comment, is:
 * </p>
 *
 * <pre>
 * &lt;module name="LineLength"&gt;
 *    &lt;property name="ignorePattern" value="^ *\* *[^ ]+$"/&gt;
 * &lt;/module&gt;
 * </pre>
 *
 * <pre>
 * There are some exceptions for class, constructor, field and method
 * declarations. To ignore to check such lines there are enable|disable options.
 * By default they have "false" values.
 * </pre>
 *
 * @author Lars Kühne
 * @author <a href="mailto:ryly@mail.ru">Ruslan Dyachenko</a>
 * @since 1.5.1
 */
public class LineLengthExtendedCheck extends AbstractCheck {

    /** Warning message key. */
    public static final String MSG_KEY = "maxLineLen";

    /** Default maximum number of columns in a line. */
    private static final int DEFAULT_MAX_COLUMNS = 80;

    /** The maximum number of columns in a line. */
    private int max = DEFAULT_MAX_COLUMNS;

    /** The regexp when long lines are ignored. */
    private Pattern ignorePattern;

    /** Array of strings in source file. */
    private String[] lines;

    /** Check field declaration length. */
    private boolean ignoreField;

    /** Check method declaration length. */
    private boolean ignoreMethod;

    /** Check constructor declaration length. */
    private boolean ignoreConstructor;

    /** Check class declaration length. */
    private boolean ignoreClass;

    /**
     * Creates a new <code>LineLengthCheck</code> instance.
     */
    public LineLengthExtendedCheck() {
        setIgnorePattern("^$");
    }

    /**
     * Enable|Disable checking field declaration length.
     *
     * @param value
     *            check field declaration length.
     */
    public void setIgnoreField(boolean value) {
        ignoreField = value;
    }

    /**
     * Enable|Disable checking method declaration length.
     *
     * @param value
     *            check method declaration length.
     */
    public void setIgnoreMethod(boolean value) {
        ignoreMethod = value;
    }

    /**
     * Enable|Disable checking constructor declaration length.
     *
     * @param value
     *            check constructor declaration length.
     */
    public void setIgnoreConstructor(boolean value) {
        ignoreConstructor = value;
    }

    /**
     * Enable|Disable checking class declaration length.
     *
     * @param value
     *            check class declaration length.
     */
    public void setIgnoreClass(boolean value) {
        ignoreClass = value;
    }

    @Override
    public int[] getDefaultTokens() {
        // array of tokens
        final List<Integer> tokens = new ArrayList<>();

        // disable checking field, method, constructor
        // or class declaration length
        if (ignoreClass) {
            tokens.add(TokenTypes.CLASS_DEF);
        }
        if (ignoreConstructor) {
            tokens.add(TokenTypes.CTOR_DEF);
        }
        if (ignoreField) {
            tokens.add(TokenTypes.VARIABLE_DEF);
        }
        if (ignoreMethod) {
            tokens.add(TokenTypes.METHOD_DEF);
        }

        // array of return tokens
        final int[] returnTokens = new int[tokens.size()];

        for (int index = 0; index < tokens.size(); index++) {
            returnTokens[index] = tokens.get(index);
        }

        return returnTokens;
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
        if (ast.getParent().getType() == TokenTypes.OBJBLOCK
                || ast.getType() == TokenTypes.CLASS_DEF) {
            final int mNumberOfLine = ast.getLineNo();
            final DetailAST endOfIgnoreLine = ast.findFirstToken(TokenTypes.SLIST);

            if (endOfIgnoreLine == null) {
                lines[mNumberOfLine - 1] = null;
            }
            else {
                int mEndNumberOfLine = endOfIgnoreLine.getLineNo();
                while (mEndNumberOfLine >= mNumberOfLine) {
                    lines[mEndNumberOfLine - 1] = null;
                    mEndNumberOfLine--;
                }
            }
        }
    }

    @Override
    public void beginTree(DetailAST rootAST) {
        lines = getLines();
    }

    @Override
    public void finishTree(DetailAST rootAST) {
        for (int index = 0; index < lines.length; index++) {
            if (lines[index] == null) {
                continue;
            }

            final String line = lines[index];
            final int realLength = CommonUtil.lengthExpandedTabs(line,
                    line.length(), getTabWidth());

            if (realLength > max && !ignorePattern.matcher(line).find()) {
                log(index + 1, MSG_KEY, max, realLength);
            }
        }
    }

    /**
     * Setter for the field max.
     *
     * @param length
     *            the maximum length of a line
     */
    public void setMax(int length) {
        max = length;
    }

    /**
     * Set the ignore pattern.
     *
     * @param format
     *            a <code>String</code> value
     * @throws IllegalArgumentException
     *             unable to parse aFormat
     */
    public final void setIgnorePattern(String format) {
        try {
            ignorePattern = Pattern.compile(format);
        }
        catch (final PatternSyntaxException exc) {
            throw new IllegalArgumentException("unable to parse " + format, exc);
        }
    }

}
