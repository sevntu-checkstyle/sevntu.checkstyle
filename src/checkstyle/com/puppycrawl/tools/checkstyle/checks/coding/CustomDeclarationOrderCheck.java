package com.puppycrawl.tools.checkstyle.checks.coding;

import java.util.ArrayList;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class CustomDeclarationOrderCheck extends Check
{
	 /** State for the VARIABLE_DEF */
    private static final int STATE_VARIABLE_DEF = 1;

    /** State for the VARIABLE_DEF */
    private static final int STATE_ANNOTATION_VARIABLE_DEF = 2;

    /** State for the CTOR_DEF */
    private static final int STATE_CTOR_DEF = 3;

    /** State for the METHOD_DEF */
    private static final int STATE_METHOD_DEF = 4;
    
    /** List of order declaration customizing by user */
    private final ArrayList<String> mCustomOrderDeclaration = new ArrayList<String>();
    
	/**
	 * @param aInputOrderDeclaration String line with the user custom declaration 
	 * @return the mCustomOrderDeclaration massive of declaration
	 */
	public ArrayList<String> setCustomOrderDeclaration(String aInputOrderDeclaration) {
		
		return mCustomOrderDeclaration;
	}
    
	@Override
	public int[] getDefaultTokens() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void visitToken(DetailAST aAST) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void leaveToken(DetailAST aAST)
	{
        if(aAST.getType() == TokenTypes.OBJBLOCK) {
           // mScopeStates.pop();
        }
	}

}
