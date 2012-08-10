package com.github.sevntu.checkstyle.checks.coding;

import java.util.HashSet;
import java.util.Set;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public class ForbidMethodOverloadingCheck extends Check
{
	private boolean mAllowInClasses;
	private boolean mAllowPrivate;

	public void setAllowInClasses(boolean aAllow)
	{
		this.mAllowInClasses = aAllow;
	}
	

	public void setAllowPrivate(boolean aAllow)
	{
		this.mAllowPrivate = aAllow;
	}
	
	@Override
	public int[] getDefaultTokens() {
		return new int[] { TokenTypes.OBJBLOCK };
	}

	@Override
	public void visitToken(final DetailAST aTypeDefNode)
	{
		if(mAllowInClasses && (aTypeDefNode.getParent().getType() == TokenTypes.CLASS_DEF 
				|| aTypeDefNode.getParent().getType() == TokenTypes.LITERAL_NEW))
		{
			return;
		}
		
		Set<String> methodNames = new HashSet<String>();
		
		for(DetailAST methodNode:Utils.iterate(aTypeDefNode, TokenTypes.METHOD_DEF))
		{
			if(mAllowPrivate && Utils.isPrivateMethod(methodNode))
			{
				continue;
			}
			if(!methodNames.add(Utils.getMethodName(methodNode)))
			{
				log(methodNode.getLineNo(), "forbid.method.overloading");
			}
		}
	}
}
