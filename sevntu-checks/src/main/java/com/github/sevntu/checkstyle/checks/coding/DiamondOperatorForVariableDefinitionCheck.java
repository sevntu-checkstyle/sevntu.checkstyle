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

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * This Check highlights variable definition statements where <a href=
 * "http://docs.oracle.com/javase/7/docs/technotes/guides/language/type-inference-generic-instance-creation.html">
 * diamond operator</a> could be used.<br>
 * <b>Rationale</b>: using diamond operator (introduced in Java 1.7) leads to shorter code<br>
 * and better code readability. It is suggested by Oracle that the diamond primarily using<br>
 * for variable declarations.<br><br>
 * E.g. of statements:
 * <p>
 * <b>Without diamond operator:</b><br><code>
 * Map&ltString, Map&ltString, Integer&gt&gt someMap = new HashMap&ltString, Map&ltString, Integer&gt&gt();</code><br>
 * <b>With diamond operator:</b><br>
 * <code>
 * Map&ltString, Map&ltString, Integer&gt&gt someMap = new HashMap&lt&gt();
 * </code>
 * </p>
 * @author <a href="mailto:nesterenko-aleksey@list.ru">Aleksey Nesterenko</a>
 */
public class DiamondOperatorForVariableDefinitionCheck extends Check {

    public static final String MSG_KEY = "diamond.operator.for.variable.definition";

    @Override
    public int[] getDefaultTokens() {
        return new int[] { TokenTypes.VARIABLE_DEF };
    }

    @Override
    public void visitToken(DetailAST variableDefNode) {

        DetailAST assignNode = variableDefNode.findFirstToken(TokenTypes.ASSIGN);
        
        if (assignNode != null) {
            DetailAST newNode = assignNode.getFirstChild().getFirstChild();
            if (newNode.getType() == TokenTypes.LITERAL_NEW) {
                DetailAST typeArgs = newNode.findFirstToken(TokenTypes.TYPE_ARGUMENTS);
                if (typeArgs != null && isSameTypeArgsInVariableDef(variableDefNode, typeArgs)) {
                        log(typeArgs, MSG_KEY);
                }
            }
        }
    }

    /**
     * Checks if type arguments of left and right side of assignment are equals
     * @param variableDefNode
     *          current variable definition
     * @param typeArgs
     *          right type arguments of assignment
     * @return true or false
     */
    private static boolean isSameTypeArgsInVariableDef(DetailAST variableDefNode,
            DetailAST typeArgs) {

        DetailAST typeNode = variableDefNode.findFirstToken(TokenTypes.TYPE);
        DetailAST variableDefTypeArgs = typeNode.findFirstToken(TokenTypes.TYPE_ARGUMENTS);
        
        return variableDefTypeArgs != null && variableDefTypeArgs.equalsTree(typeArgs);            
    }
}
