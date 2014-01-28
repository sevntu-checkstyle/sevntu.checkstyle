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

import java.util.regex.Pattern;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.api.Utils;
import com.puppycrawl.tools.checkstyle.checks.naming.AbstractNameCheck;

/**
 * Check forces enum values to match the specific pattern.
 * @author Pavel Baranchikov
 */
public class EnumValueName extends AbstractNameCheck
{
    protected static final String MSG = "name.invalidPattern";
    /**
     * Default pattern for enum constant values.
     */
    protected static final String DEFAULT_CONST_PATTERN =
            "^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$";
    /**
     * Default pattern for enum object values.
     */
    protected static final String DEFAULT_OBJ_PATTERN = "^[A-Z][a-zA-Z0-9]*$";

    /**
     * Regular expression to test enum object values against.
     */
    private Pattern mObjRegexp;
    /**
     * Format for enum object values to check for. Compiled to
     * {@link #mObjRegexp}
     */
    private String mObjFormat;

    /**
     * Constructs check with the default pattern.
     */
    public EnumValueName()
    {
        super(DEFAULT_CONST_PATTERN);
        setObjFormat(DEFAULT_OBJ_PATTERN);
    }

    @Override
    public int[] getDefaultTokens()
    {
        return new int[]
        {TokenTypes.ENUM_CONSTANT_DEF};
    }

    @Override
    public void visitToken(DetailAST aAST)
    {
        if (mustCheckName(aAST)) {
            final DetailAST nameAST = aAST.findFirstToken(TokenTypes.IDENT);
            final boolean enumIsObject = isObject(aAST);
            final Pattern pattern = enumIsObject ? getObjectRegexp()
                    : getRegexp();
            if (!pattern.matcher(nameAST.getText()).find()) {
                final String format = enumIsObject ? getObjFormat()
                        : getFormat();
                log(nameAST.getLineNo(),
                        nameAST.getColumnNo(),
                        MSG,
                        nameAST.getText(),
                        format);
            }
        }
    }

    /**
     * Method determines whether the specified enum is a constant or is an
     * object.
     * @param aAST token of a enum value definition
     * @return <code>true</code> if enum is an object
     */
    protected boolean isObject(DetailAST aAST)
    {
        final DetailAST objBlock = aAST.getParent();
        assert (objBlock.getType() == TokenTypes.OBJBLOCK);
        final DetailAST methodDeclaration = objBlock
                .findFirstToken(TokenTypes.METHOD_DEF);
        return (methodDeclaration != null);
    }

    /**
     * Method sets format to match enum object values.
     * @param aObjectRegexp format to check against
     */
    public final void setObjFormat(String aObjectRegexp)
    {
        mObjRegexp = Utils.getPattern(aObjectRegexp, 0);
        mObjFormat = aObjectRegexp;
    }

    public final Pattern getObjectRegexp()
    {
        return mObjRegexp;
    }

    public String getObjFormat()
    {
        return mObjFormat;
    }
}
