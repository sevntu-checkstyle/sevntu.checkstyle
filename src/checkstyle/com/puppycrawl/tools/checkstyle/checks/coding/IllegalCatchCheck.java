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

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FullIdent;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.checks.CheckUtils;

/**
 * Catching java.lang.Exception, java.lang.Error or java.lang.RuntimeException
 * is almost never acceptable.
 * 
 * @author <a href="mailto:simon@redhillconsulting.com.au">Simon Harris</a>
 */
public final class IllegalCatchCheck extends AbstractIllegalCheck
{
    /** Creates new instance of the check. */
    public IllegalCatchCheck()
    {
        super(new String[] { "Exception", "Error", "RuntimeException",
                "Throwable", "java.lang.Error", "java.lang.Exception",
                "java.lang.RuntimeException", "java.lang.Throwable", });
    }

    @Override
    public int[] getDefaultTokens()
    {
        return new int[] { TokenTypes.LITERAL_CATCH };
    }

    @Override
    public int[] getRequiredTokens()
    {
        return getDefaultTokens();
    }

    @Override
    public void visitToken(DetailAST aDetailAST)
    {
        final DetailAST paramDef = aDetailAST.findFirstToken(TokenTypes.PARAMETER_DEF);
       // System.out.println("nextNode - line:" + paramDef.getLineNo() + " col:" +paramDef.getColumnNo()+ ", TYPE: " + paramDef.getText());
        
        //DetailAST currentNode = paramDef;
        String str=paramDef.toStringList();
        boolean noWarning = str.indexOf(" throw[")>=0;           
            
        final DetailAST excType = paramDef.findFirstToken(TokenTypes.TYPE);
        final FullIdent ident = CheckUtils.createFullType(excType);

        if (!noWarning && isIllegalClassName(ident.getText())) {
            log(aDetailAST, "illegal.catch", ident.getText());
        }
    }
    
//    public DetailAST throwSearch(DetailAST currentNode){
//                
//        System.out.println("Current node: line:" + currentNode.getLineNo() + " col:" +currentNode.getColumnNo()+ ", TYPE: " + currentNode.getText());
//        
//        if(currentNode.getNumberOfChildren()<=0){ // has not childs 
//          //   go to next node
//            DetailAST NextSibling = currentNode.getNextSibling();
//            if(NextSibling!=null)currentNode=NextSibling;
//            else return null;
//        }
//        
//        else { // has childs
//            
//            if(currentNode.getType()==TokenTypes.LITERAL_THROW)return currentNode;             
//            
//            return(throwSearch(currentNode.getFirstChild()));
//                       
//        }
//        
//        return null;
//    }
    
    
    
}