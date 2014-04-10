////////////////////////////////////////////////////////////////////////////////
// checkstyle: Checks Java source code for adherence to a set of rules.
// Copyright (C) 2001-2012 Oliver Burn
//
// This library is free software; you can redistribute it and/or
// modify it under the terms of the GNU Lesser General Public
// License as published by the Free Software Foundation; either
// version 2.1 of the License, or (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
////////////////////////////////////////////////////////////////////////////////

package com.github.sevntu.checkstyle.checks.coding;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;


/**
 * <p>
 * Forbid throwing anonymous exception.
 * limitation: This Check does not validate cases then Exception object is created before it is thrown.
 * For example:
 * <p><code><pre>
 * catch (Exception e) {
 *	   throw new RuntimeException() { //anonymous exception declaration 
 *		    //some code
 *	   };</code></pre>
 *
 * @author <a href="mailto:maxvetrenko2241@gmail.com">Max Vetrenko</a>
 */
public class ForbidThrowAnonymousExceptionsCheck extends Check
{
	/**
	 * Warning message key.
	 */
	public static final String MSG_KEY = "forbid.throw.anonymous.exception";
	
    @Override
    public int[] getDefaultTokens()
    {
        return new int[] { TokenTypes.LITERAL_THROW };
    }

    @Override
    public void visitToken(DetailAST aLiteralThrow)
    {
    	final DetailAST literalNew = getLiteralNew(aLiteralThrow.getFirstChild());
    	if (literalNew != null && hasObjectBlock(literalNew)) {
    		log(aLiteralThrow.getLineNo(), MSG_KEY);
    	}
    }
    
    /**
     * 
     * @param aExprNode
     * @return literal 'new' or null.
     */
    private DetailAST getLiteralNew (DetailAST aExprNode) {
    	return aExprNode.findFirstToken(TokenTypes.LITERAL_NEW);
    }
    
    /**
     * 
     * @param aLiteralNew
     * @return true, if literal 'new' has OBJBLOCK as a last child.
     */
    private boolean hasObjectBlock (DetailAST aLiteralNew) {
    	return aLiteralNew.getLastChild().getType() == TokenTypes.OBJBLOCK;
    }
}
