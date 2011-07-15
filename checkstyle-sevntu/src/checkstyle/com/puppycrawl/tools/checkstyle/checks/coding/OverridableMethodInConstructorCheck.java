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

/**<p> This check prevents calls of overridable methods from constructor body
 * and ...
 * .
 * @author <a href="mailto:Daniil.Yaroslavtsev@gmail.com"> Daniil
 *         Yaroslavtsev</a>
 */
public class OverridableMethodInConstructorCheck extends Check {

    /**
     * A key to search the warning message text in "messages.properties" file.
     * */
    private final String mKeyCtor = "overridable.method.in.constructor";
    private final String mKeyClone = "overridable.method.in.clone";
    private final String mKeyReadObject = "overridable.method.in.readobject";
    
    
    @Override
    public final int[] getDefaultTokens()
    {
        return new int[] {TokenTypes.CTOR_DEF, TokenTypes.METHOD_DEF};
    }

    @Override
    public final void visitToken(final DetailAST aDetailAST)
    {
		// for all CTOR_DEF nodes
		if (aDetailAST.getType() == TokenTypes.CTOR_DEF) {

			DetailAST methodCallAST = work(aDetailAST);

			if (methodCallAST != null) {
				String curMethodName = getCalledMethodName(methodCallAST);
				log(methodCallAST, mKeyCtor, curMethodName);
			}

		}

        else { // for all METHOD_DEF nodes
       	
    String methodName = aDetailAST.findFirstToken(TokenTypes.IDENT).getText();
        	
         if("clone".equals(methodName)){  	 
      DetailAST implementsClause = getClass(aDetailAST).findFirstToken(TokenTypes.IMPLEMENTS_CLAUSE);
      for(DetailAST ident: getChildren(implementsClause)){
    	 if(ident.getText().equals("Cloneable")){ // called in clone() from Cloneable i-face
    		 
    		   	 
    		 
    		 
    	  }    	 
      }             	 
  }
        	
         else if("readObject".equals(methodName)){
            
        	 DetailAST implementsClause = getClass(aDetailAST).findFirstToken(TokenTypes.IMPLEMENTS_CLAUSE);
             for(DetailAST ident: getChildren(implementsClause)){    	 
           	 if(ident.getText().equals("Serializable")){ // called in readObject() from Serializable i-face
           		 

           		 
					}
				}
			}
		}
	}

    
    // try to find a first instance of overridable method call
    // all levels below on the currrent parentNode.
	public DetailAST work(final DetailAST ParentAST) {
		
		DetailAST result = null;

		LinkedList<DetailAST> methodCallsList = getMethodCalls(ParentAST,new LinkedList<DetailAST>());
		
		for (DetailAST curNode : methodCallsList) {
			DetailAST overridable = findOverridable(curNode);
			if (overridable != null) {
				result = curNode;
				break;
			}
		}
		return result;
	}

	
	
	public LinkedList<DetailAST> getMethodCalls(final DetailAST ParentAST, LinkedList<DetailAST> currentResultList) {		
		
		for (DetailAST currentNode : getChildren(ParentAST)) {

			if (currentNode.getNumberOfChildren() > 0) {

				if (currentNode.getType() == TokenTypes.METHOD_CALL) {
					currentResultList.add(currentNode);
				} else {
					getMethodCalls(currentNode,currentResultList);
				}
			}
		}
		return currentResultList;
	}
    
    
	// выдает первый найденный method_call, который
	// вызывает переопределяемый метод.
	public DetailAST findOverridable(final DetailAST methodCallAST) {

		
		String methodName = getCalledMethodName(methodCallAST);

		if (methodName != null) {
			// find all methods named 'methodname' in the same class
			DetailAST methodDef = getMethodDefinition(getClass(methodCallAST),methodName);

			if (methodDef != null) {

				if (hasPrivateOrFinalModifier(methodDef)) {

					LinkedList<DetailAST> methodCallsList = getMethodCalls(methodDef,new LinkedList<DetailAST>());
					
					for (DetailAST curNode : methodCallsList) {
						return findOverridable(curNode);
					}

				} else {
					return methodCallAST;
				}
			}
		}
		return null;
	}


	public String getCalledMethodName(final DetailAST methodCallAST){
		String result = null;
		
		DetailAST ident = methodCallAST.findFirstToken(TokenTypes.IDENT);
				
		if(ident !=null){ // explicit call to a method ?
			result = ident.getText();
		} 
		
		else { // this + .methodCall() ?
			
			DetailAST childAST = methodCallAST.getFirstChild();
			
			if(childAST != null && childAST.getType() == TokenTypes.DOT){
				
				DetailAST literalThis = childAST.findFirstToken(TokenTypes.LITERAL_THIS);
				DetailAST ident2 = childAST.findFirstToken(TokenTypes.IDENT);
				
				if(literalThis!= null && ident2 !=null) {
					result = ident2.getText();					
				}			
			}
		}
		
		return result;
	}


    public DetailAST getMethodDefinition(final DetailAST ParentAST, String methodName)
    {

        for (DetailAST currentNode : getChildren(ParentAST)) {

            if (currentNode.getNumberOfChildren() > 0)
            {
            	if (currentNode.getType() == TokenTypes.METHOD_DEF) {
                	String curMethodName = currentNode.findFirstToken(TokenTypes.IDENT).getText();
                	if(methodName.equals(curMethodName)) {
                		return currentNode;
                	}                	
                }

            	int type = currentNode.getType();
            	if(type!=TokenTypes.CTOR_DEF && type!=TokenTypes.MODIFIERS
            			&& type!=TokenTypes.METHOD_DEF) {
            		return getMethodDefinition(currentNode, methodName);
            	}
            }
        }
     return null;
    }


	public boolean hasPrivateOrFinalModifier(final DetailAST methodDefAST) {

		boolean hasPrivateOrFinalModifier = false;

		final DetailAST modifiers = methodDefAST.findFirstToken(TokenTypes.MODIFIERS);

		if (modifiers != null && modifiers.getChildCount() != 0) {
			for (DetailAST curNode : getChildren(modifiers)) {
				if (curNode.getType() == TokenTypes.LITERAL_PRIVATE
						|| curNode.getType() == TokenTypes.FINAL) {
					hasPrivateOrFinalModifier = true;
				}
			}
		}

		return hasPrivateOrFinalModifier;
	}
    
//	public LinkedList<DetailAST> getParametersList(final DetailAST methodDefAST) {
//		final LinkedList<DetailAST> result = new LinkedList<DetailAST>();
//		DetailAST params = methodDefAST.findFirstToken(TokenTypes.PARAMETERS);
//		
//		if(params.getChildCount() > 0) {			
//			for(DetailAST tmp: getChildren(params)) {
//				if(tmp.getType() == TokenTypes.PARAMETER_DEF) {
//					// пишем в список тип каждого параметра метода
//					result.add(tmp.findFirstToken(TokenTypes.TYPE).getFirstChild()); 
//				}
//			}
//		}	
//		return result;
//	}
       

    /**
     * Method that returns a root CLASS_DEF DetailAST for the class that owns
     * a method for aMethodNode METHOD_CALL node.
     * @return a DetailAST node for the class that owns
     * aMethodNode node.
     * @param aMethodNode - a current method DetailAST.
     * */
    public final DetailAST getClass(final DetailAST aMethodNode)
    {
        DetailAST result = null;
        DetailAST currentNode = aMethodNode;

        while (currentNode != null
                && currentNode.getType() != TokenTypes.CLASS_DEF)
        {
            currentNode = currentNode.getParent();
        }

        if (currentNode != null
                && currentNode.getType() == TokenTypes.CLASS_DEF)
        {
            result = currentNode;
        }

        return result;
    }


    /**
     * Gets all the children one level below on the current top node.
     * @param aNode - current parent node.
     * @return an array of children one level below on the current parent node
     *         aNode.
     */
    public final LinkedList<DetailAST> getChildren(final DetailAST aNode)
    {
        final LinkedList<DetailAST> result = new LinkedList<DetailAST>();

        DetailAST currNode = aNode.getFirstChild();

        while (currNode != null) {
            result.add(currNode);
            currNode = currNode.getNextSibling();
        }
        
        return result;
    }

}