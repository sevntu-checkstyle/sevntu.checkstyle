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
package com.puppycrawl.tools.checkstyle.checks.coding;

import java.util.LinkedList;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.checks.CheckUtils;

/**
 * <p>
 * This check prevents ...
 * 
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil
 *         Yaroslavtsev</a>
 */
public class AvoidNotShortCircuitOperatorsForBooleanCheck extends Check {

    /** Creates new instance of the check. */
    public AvoidNotShortCircuitOperatorsForBooleanCheck() {
    }

    @Override
    public int[] getDefaultTokens() {
        return new int[] { TokenTypes.BOR, TokenTypes.BAND,
                TokenTypes.BOR_ASSIGN, TokenTypes.BAND_ASSIGN, };
    }

    @Override
    public void visitToken(final DetailAST aDetailAST) {

//        System.out.println("Current node:\"" + aDetailAST.getText()
//                + "\". Line: " + aDetailAST.getLineNo() + ", column: "
//                + aDetailAST.getColumnNo());

        DetailAST currentNode = aDetailAST;
        while (currentNode != null
                && currentNode.getType() != TokenTypes.LITERAL_IF
                && currentNode.getType() != TokenTypes.FOR_CONDITION
                && currentNode.getType() != TokenTypes.LITERAL_WHILE
                && currentNode.getType() != TokenTypes.LITERAL_RETURN
                && currentNode.getType() != TokenTypes.VARIABLE_DEF
                && currentNode.getType() != TokenTypes.METHOD_DEF
                && currentNode.getType() != TokenTypes.CLASS_DEF) {
            currentNode = currentNode.getParent();
        }

        int type = currentNode.getType();
//        System.out.println("This node:\"" + currentNode.getText()+ "\". Line: " + currentNode.getLineNo() + ", column: "
//                + currentNode.getColumnNo());

        if (type != TokenTypes.METHOD_DEF
                && type != TokenTypes.CLASS_DEF) {

            if (type == TokenTypes.VARIABLE_DEF && isBoolean(currentNode)) {
                log(aDetailAST,"avoid.not.short.circuit.operators.for.boolean",aDetailAST.getText());
            } 
            
            else if (type == TokenTypes.LITERAL_RETURN){ // "return" situation

                while (currentNode != null
                        && currentNode.getType() != TokenTypes.METHOD_DEF
                        && currentNode.getType() != TokenTypes.CLASS_DEF) {
                    currentNode = currentNode.getParent();
                }

                if(currentNode.getType() == TokenTypes.METHOD_DEF
                        && isBoolean(currentNode)){
                    log(aDetailAST,"avoid.not.short.circuit.operators.for.boolean",aDetailAST.getText());
                }
            }

            else if (type == TokenTypes.LITERAL_IF
                 || type == TokenTypes.LITERAL_WHILE
                 || type == TokenTypes.FOR_CONDITION){ // "for/if/while" situation

                if(isNotInteger(aDetailAST)){
                log(aDetailAST,"avoid.not.short.circuit.operators.for.boolean",aDetailAST.getText());
                }

            }
        }
    }

    public boolean isBoolean(DetailAST aNode) {
        return "boolean".equals(CheckUtils.createFullType(
                aNode.findFirstToken(TokenTypes.TYPE)).getText());
    }

    public boolean isIntegerType(DetailAST aNode) {
        return "int".equals(CheckUtils.createFullType(
                aNode.findFirstToken(TokenTypes.TYPE)).getText());
    }
    
    public boolean isNotInteger(DetailAST aNode) {
        
        LinkedList<String> childNames = new LinkedList<String>();
        LinkedList<String> integerVariablesNames = new LinkedList<String>();

        for(DetailAST child:getChilds(aNode)){
            childNames.add(child.getText());
        }

        while (aNode != null
                && aNode.getType() != TokenTypes.CTOR_DEF
                && aNode.getType() != TokenTypes.METHOD_DEF
                && aNode.getType() != TokenTypes.CLASS_DEF) {
            aNode = aNode.getParent();
        }

        if (aNode.getType() != TokenTypes.CTOR_DEF
                || aNode.getType() != TokenTypes.CLASS_DEF
                || aNode.getType() != TokenTypes.METHOD_DEF) {

            for (DetailAST currentNode : getChilds(aNode.getLastChild())) {
                if (currentNode.getType() == TokenTypes.VARIABLE_DEF){

                    if (isIntegerType(currentNode)) {
                        integerVariablesNames.add(currentNode.findFirstToken(TokenTypes.IDENT).getText());
                    }
                }
            }

            for(String name:childNames){
                if(integerVariablesNames.contains(name)){
                    return false;
                }

            }

        }

        return true;
    }

    /** Gets all the children one level below on the current top node.
     * @param aNode - current parent node.
     * @return an array of childs one level below
     * on the current parent node aNode. */
    public DetailAST[] getChilds(DetailAST aNode)
    {
        final DetailAST[] result = new DetailAST[aNode.getChildCount()];

        DetailAST currNode = aNode.getFirstChild();

        for (int i = 0; i < aNode.getNumberOfChildren(); i++) {
            result[i] = currNode;
            currNode = currNode.getNextSibling();
        }

        return result;
    }

}
