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

package com.puppycrawl.tools.checkstyle.checks.naming;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * Check name of the type to varidate abbreviations in capital letters, a verify of
 * variables and methods names are optional.  
 * </p>
 * @author <a href="mailto:fishh1991@gmail.com">Troshin Sergey</a>
 */
public class AbbreviationAsWordInTypeNameCheck extends TypeNameCheck {

	/**
	 * Variable indicates on the permitted amount of capital
	 * letters in abbreviations in the classes, interfaces,
	 * variables and methods names
	 */
    private int allowedCapitalCounts = 3;
    
    /**
     * true, if variables and methods names must be checked on 
     * restricted abbreviations.
     */
    private boolean checkVariablesAndMethodsNames = false;

    /**
     * A structure that contains names that must be skiped for 
     * checking.
     */
    private Set<String> permittedWords = new HashSet<String>();

    /**
     * Use this for setting a required amount of permitted capital
     * letters in abbreviations in names.
     * 
     * @param aAllowedCapitalCounts
     * 					required amount of permitted capital letters.
     */
    public void setAllowedCapitalCounts(int aAllowedCapitalCounts) {
	allowedCapitalCounts = aAllowedCapitalCounts;
    }
    
    /**
     * An option to enable check for variables and methods names.
     * 
     * @param aCheckVariablesAndMethodsNames
     * 					required value; true, if abbreviations in variables and
     * 					methods names have to be checked.
     */
    public void setCheckVariablesAndMethodsNames(boolean aCheckVariablesAndMethodsNames) {
    	checkVariablesAndMethodsNames = aCheckVariablesAndMethodsNames;
    }

    /**
     * Set a list of names that must be skipped for checking.
     * 
     * @param aPermitedWords
     * 			an array of names that must be skipped for checking.
     */
    public void setPermitedWords(String[] aPermitedWords) {
    	permittedWords.addAll(Arrays.asList(aPermitedWords));
    }

    @Override
    public int[] getDefaultTokens() {
    	if (checkVariablesAndMethodsNames) {
    		return new int [] {
    				TokenTypes.CLASS_DEF,
    				TokenTypes.INTERFACE_DEF,
    				TokenTypes.METHOD_DEF,
    				TokenTypes.VARIABLE_DEF
    		};
    	} else {  	
    		return new int [] {
    				TokenTypes.CLASS_DEF,
    				TokenTypes.INTERFACE_DEF
    		};
    	}
    }
    
    @Override
    public void beginTree(DetailAST aRootAst) {
    	setFormat(regexpFactory(allowedCapitalCounts));
    }

    @Override
    public void visitToken(DetailAST aAst) {

	final DetailAST nameAst = aAst.findFirstToken(TokenTypes.IDENT);

	final String typeName = nameAst.getText();

	if (getRegexp().matcher(typeName).find() &&
			(!permittedWords.contains(typeName))) {

	    log(nameAst.getLineNo(), "abbreviation.as.word.in.type.name.check",
		    allowedCapitalCounts);
	}
    }

    /**
     * Return a regexp that highlights redundant type names.
     * 
     * @param aAlowedCapitalCounts
     *            amount of permitted capital letters.
     * @return regexp that highlights redundant type names.
     */
    public String regexpFactory(int aAlowedCapitalCounts) {
	return "\\w*[A-Z]{" + (aAlowedCapitalCounts + 1) + "}\\w*$";
    }

}
