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
package com.puppycrawl.tools.checkstyle.checks;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.ConversionException;

import com.google.common.collect.ImmutableMap;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TextBlock;
import com.puppycrawl.tools.checkstyle.checks.AbstractFormatCheck;

/**
 * Check that allows use only '//' or '/*' types of comments
 * 
 * <pre>
 * &lt;module name="CommentTypeCheck"&gt; &lt;property name="allowSingleLineComment"
 * value="true"/&gt;
 * </pre>
 * @author Dmitry Gridyushko <sentinel1992@mail.ru>
 */
public class CommentTypeCheck extends AbstractFormatCheck
{
    /** default format for allowed blank line. */
    private static final String DEFAULT_FORMAT = "^[\\s\\}\\);]*$";

    /**
     * mAllowSingleLineComment is a parameter that tells what type of comments
     * are allowed. If <code>true</code> only single line comments are allowed.
     * <code>false</code> mean only block comments are allowed.
     */
    private boolean mAllowSingleLineComment = true;

    public void setAllowSingleLineComment(boolean allow)
    {
        mAllowSingleLineComment = allow;
    }

    /**
     * Creates new instance of the check.
     * @throws ConversionException
     *         unable to parse DEFAULT_FORMAT.
     */
    public CommentTypeCheck() throws ConversionException
    {
        super(DEFAULT_FORMAT);
    }

    @Override
    public int[] getDefaultTokens()
    {
        return new int[0];
    }

    @Override
    public void visitToken(DetailAST aAST)
    {
        throw new IllegalStateException("visitToken() shouldn't be called.");
    }

    @Override
    public void beginTree(DetailAST aRootAST)
    {
        if (mAllowSingleLineComment) {
            Set <Integer> cComments = getFileContents().getCComments().keySet();
            if (!cComments.isEmpty()) {
                log(Collections.min(cComments), "comment.not.allowed", "//");
            }

        }
        else {
            Set <Integer> cppComments = getFileContents().getCppComments().keySet();
            if (!cppComments.isEmpty()) {
                log(Collections.min(cppComments), "comment.not.allowed", "/*");
            }
        }
    }
                

}
