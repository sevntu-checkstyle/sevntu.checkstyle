package com.puppycrawl.tools.checkstyle.checks.coding;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * This check prevents the default implementation Serializable interface in
 * inner classes (Serializable interface are default if methods readObject() or
 * writeObject() are not override in class). For more information read
 * "Effective Java (2nd edition)" chapter 11, item 74, page 294.
 * </p>
 * 
 * @author <a href="mailto:IliaDubinin91@gmail.com">Ilia Dubinin</a>
 */
public class AvoidDefaultSerializableInInnerClasses extends Check
{
	private boolean allowPartlyImplement;
	@Override
	public int[] getDefaultTokens()
	{
		return new int[] { TokenTypes.CLASS_DEF };
	}
	
	@Override
	public void visitToken(DetailAST aDetailAST)
	{
		boolean topLevelClass = (aDetailAST.getParent() == null);
		if (isSerializable(aDetailAST)
				&& !isStatic(aDetailAST)
				&& !hasSerialazableMethods(aDetailAST)
				&& !topLevelClass)
		{
			
			DetailAST implementsBlock = aDetailAST
					.findFirstToken(TokenTypes.IMPLEMENTS_CLAUSE);
			log(implementsBlock.getLineNo(),
					"avoid.default.serializable.in.inner.classes");
			
		}
	}
	/**
	 * <p>
	 * Set allow partly implement serializable interface.
	 * </p>
	 * @param allow
	 */
	public void setAllowPartlyImplement(boolean allow)
	{
		this.allowPartlyImplement = allow;
	}
	/**
	 * <p>
	 * Return true if it is nested class.
	 * </p>
	 * @param aDetailAST
	 * @return
	 */
	private boolean isStatic(DetailAST classNode)
	{
		boolean result = false;
		DetailAST modifiers = classNode.findFirstToken(TokenTypes.MODIFIERS);
		if(modifiers != null)
		{
			modifiers = modifiers.getFirstChild();
			while(modifiers != null)
			{
				if("static".equals(modifiers.getText()))
				{
					result = true;
					break;
				}
				modifiers = modifiers.getNextSibling();
			}
		}
		return result;
	}
	/**
	 * <p>
	 * Return true, if inner class contain overrided method readObject() and
	 * writeObject();
	 * </p>
	 * 
	 * @param methNode
	 *            the start node for method definition.
	 * @return The boolean value. True, if method was overrided.
	 */
	private boolean hasSerialazableMethods(DetailAST classNode)
	{
		boolean hasRead = false, hasWrite = false, result = false;
		DetailAST methodNode = classNode.findFirstToken(TokenTypes.OBJBLOCK)
				.findFirstToken(TokenTypes.METHOD_DEF);
		
		while (methodNode != null)
		{
			if (TokenTypes.METHOD_DEF == methodNode.getType())
			{
				if ("readObject".equals(methodNode.findFirstToken(
						TokenTypes.IDENT).getText()))
				{
					hasRead = isSerializableMethod(methodNode);
				}
				if ("writeObject".equals(methodNode.findFirstToken(
						TokenTypes.IDENT).getText()))
				{
					hasWrite = isSerializableMethod(methodNode);
				}
			}
			if(allowPartlyImplement)
			{
				if(hasRead || hasWrite)
				{
					result = hasRead || hasWrite;
					break;
				}
			}
			else
			{
				if(hasRead && hasWrite)
				{
					result = hasRead && hasWrite;
					break;
				}
			}
			methodNode = methodNode.getNextSibling();
		}
		
		return result;
	}
	
	/**
	 * <p>
	 * Return true, if methods readObject() and writeObject() have correct
	 * modifiers;
	 * </p>
	 * 
	 * @param methodNode
	 *            - current method node;
	 * @param argType
	 *            - type of arguments for readObject or writObject;
	 * @return boolean value;
	 */
	private boolean isSerializableMethod(DetailAST methodNode)
	{
		DetailAST modifiers = methodNode.findFirstToken(TokenTypes.MODIFIERS);
		boolean isPrivate = false;
		if (modifiers.getChildCount() == 1)
		{
			isPrivate = "private".equals(modifiers.getFirstChild().getText());
			
		}
		return isPrivate;
	}
	
	/**
	 * <p>
	 * Return true, if class implement Serializable interface;
	 * </p>
	 * 
	 * @param classDefNode
	 * 				- the start node for class definition.
	 * @return boolean value. True, if class implements Serializable
	 *         interface.
	 */
	private boolean isSerializable(DetailAST classDefNode)
	{
		DetailAST implementationsDef = classDefNode
				.findFirstToken(TokenTypes.IMPLEMENTS_CLAUSE);
		boolean result = false;
		if (implementationsDef != null)
		{
			implementationsDef = implementationsDef.getFirstChild();
			
			while (implementationsDef != null)
			{
				if ("Serializable".equals(implementationsDef.getText()))
				{
					result = true;
					break;
				}
				implementationsDef = implementationsDef.getNextSibling();
			}
		}
		return result;
	}
	
}
