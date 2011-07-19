package com.puppycrawl.tools.checkstyle.checks.coding;
import com.puppycrawl.tools.checkstyle.api.*;

public class MethodLimitCheck extends Check
{
    private static final int DEFAULT_MAX = 30;
    private int max = DEFAULT_MAX;


    public int[] getDefaultTokens()
    {
    	return new int[]{TokenTypes.OBJBLOCK};
//        return new int[]{TokenTypes.CLASS_DEF, TokenTypes.INTERFACE_DEF};
    }
    	
	public void visitToken(DetailAST ast)
    {
		DetailAST objBlock = ast;
//        DetailAST objBlock = ast.findFirstToken(TokenTypes.OBJBLOCK);
        int methodDefs = objBlock.getChildCount(TokenTypes.METHOD_DEF);
        
        if (methodDefs > this.max) {
            log(ast.getLineNo(),
                "too many methods, only " + this.max + " are allowed");
        }
   }
	
	public void setMax(int max) {
		this.max = max;
	}
}
