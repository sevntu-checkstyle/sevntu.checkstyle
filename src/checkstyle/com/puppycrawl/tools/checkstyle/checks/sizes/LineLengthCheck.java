////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2010  Oliver Burn
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

package com.puppycrawl.tools.checkstyle.checks.sizes;

import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.beanutils.ConversionException;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.api.Utils;

/**
 * Checks for long lines.
 *
 * <p>
 * Rationale: Long lines are hard to read in printouts or if developers have
 * limited screen space for the source code, e.g. if the IDE displays additional
 * information like project tree, class hierarchy, etc.
 * </p>
 *
 * <p>
 * Note: Support for the special handling of imports in CheckStyle Version 2 has
 * been dropped as it is a special case of regexp: The user can set the
 * ignorePattern to "^import" and achieve the same effect.
 * </p>
 * <p>
 * The default maximum allowable line length is 80 characters. To change the
 * maximum, set property max.
 * </p>
 * <p>
 * To ignore lines in the check, set property ignorePattern to a regular
 * expression for the lines to ignore.
 * </p>
 * <p>
 * An example of how to configure the check is:
 * </p>
 *
 * <pre>
 * &lt;module name="LineLength"/&gt;
 * </pre>
 * <p>
 * An example of how to configure the check to accept lines up to 120 characters
 * long is:
 * </p>
 *
 * <pre>
 * &lt;module name="LineLength"&gt;
 *    &lt;property name="max" value="120"/&gt;
 * &lt;/module&gt;
 * </pre>
 * <p>
 * An example of how to configure the check to ignore lines that begin with
 * &quot; * &quot;, followed by just one word, such as within a Javadoc comment,
 * is:
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
 */
public class LineLengthCheck extends Check
{
    /** default maximum number of columns in a line */
    private static final int DEFAULT_MAX_COLUMNS = 80;

    /** the maximum number of columns in a line */
    private int mMax = DEFAULT_MAX_COLUMNS;

    /** the regexp when long lines are ignored */
    private Pattern mIgnorePattern;

    /** array of strings in source file */
    private String[] mLines;

    /** check field declaration length */
    private boolean mIgnoreField;

    /** check method declaration length */
    private boolean mIgnoreMethod;

    /** check constructor declaration length */
    private boolean mIgnoreConstructor;

    /** check class declaration length */
    private boolean mIgnoreClass;

    /**
     * Enable|Disable checking field declaration length.
     *
     * @param aValue check field declaration length.
     */
    public void setIgnoreField(boolean aValue)
    {
        mIgnoreField = aValue;
    }

    /**
     * Enable|Disable checking method declaration length.
     *
     * @param aValue check method declaration length.
     */
    public void setIgnoreMethod(boolean aValue)
    {
        mIgnoreMethod = aValue;
    }

    /**
     * Enable|Disable checking constructor declaration length.
     *
     * @param aValue check constructor declaration length.
     */
    public void setIgnoreConstructor(boolean aValue)
    {
        mIgnoreConstructor = aValue;
    }

    /**
     * Enable|Disable checking class declaration length.
     *
     * @param aValue check class declaration length.
     */
    public void setIgnoreClass(boolean aValue)
    {
        mIgnoreClass = aValue;
    }

    /**
     * Creates a new <code>LineLengthCheck</code> instance.
     */
    public LineLengthCheck()
    {
        setIgnorePattern("^$");
    }

    @Override
    public int[] getDefaultTokens()
    {
        /* array of tokens */
        final ArrayList<Integer> tokens = new ArrayList<Integer>();

        /*disable checking field, method, constructor
         * or class declaration length
         */
        if (mIgnoreClass) {
            tokens.add(TokenTypes.CLASS_DEF);
        }
        if (mIgnoreConstructor) {
            tokens.add(TokenTypes.CTOR_DEF);
        }
        if (mIgnoreField) {
            tokens.add(TokenTypes.VARIABLE_DEF);
        }
        if (mIgnoreMethod) {
            tokens.add(TokenTypes.METHOD_DEF);
        }

        /* array of return tokens */
        final int[] returnTokens = new int[tokens.size()];

        for (int index = 0; index < tokens.size(); index++) {
            returnTokens[index] = tokens.get(index);
        }

        return returnTokens;
    }

    @Override
    public void visitToken(DetailAST aAST)
    {
        final DetailAST endOfIgnoreLine = aAST.findFirstToken(TokenTypes.SLIST);
        if (null != aAST.getParent()
                && aAST.getParent().getType() == TokenTypes.OBJBLOCK
                || aAST.getType() == TokenTypes.CLASS_DEF)
        {
            final int mNumberOfLine = aAST.getLineNo();
            if (null == endOfIgnoreLine) {
                mLines[mNumberOfLine - 1] = null;
            }
            else {
                int mEndNumberOfLine = endOfIgnoreLine.getLineNo();
                while (mEndNumberOfLine >= mNumberOfLine) {
                    mLines[mEndNumberOfLine - 1] = null;
                    mEndNumberOfLine--;
                }
            }
        }
    }

    @Override
    public void beginTree(DetailAST aRootAST)
    {
        mLines = getLines();
    }

    @Override
    public void finishTree(DetailAST aRootAST)
    {
        for (int i = 0; i < mLines.length; i++) {

            if (null == mLines[i]) {
                continue;
            }

            final String line = mLines[i];
            final int realLength = Utils.lengthExpandedTabs(line,
                    line.length(), getTabWidth());

            if ((realLength > mMax) && !mIgnorePattern.matcher(line).find()) {
                log(i + 1, "maxLineLen", mMax);
            }
        }
    }

    /**
     * @param aLength the maximum length of a line
     */
    public void setMax(int aLength)
    {
        mMax = aLength;
    }

    /**
     * Set the ignore pattern.
     *
     * @param aFormat a <code>String</code> value
     * @throws ConversionException unable to parse aFormat
     */
    public void setIgnorePattern(String aFormat) throws ConversionException
    {
        try {
            mIgnorePattern = Utils.getPattern(aFormat);
        }
        catch (final PatternSyntaxException e) {
            throw new ConversionException("unable to parse " + aFormat, e);
        }
    }
}
