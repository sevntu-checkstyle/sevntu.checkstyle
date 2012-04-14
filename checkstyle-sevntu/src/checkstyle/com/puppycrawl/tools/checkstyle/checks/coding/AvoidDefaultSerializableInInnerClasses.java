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
		boolean hasRead = false, hasWrite = false;
		DetailAST methodNode = classNode.findFirstToken(TokenTypes.OBJBLOCK)
				.findFirstToken(TokenTypes.METHOD_DEF);
		
		while (methodNode != null)
		{
			if (TokenTypes.METHOD_DEF == methodNode.getType())
			{
				if ("readObject".equals(methodNode.findFirstToken(
						TokenTypes.IDENT).getText()))
				{
					hasRead = isISerializableMethod(methodNode, "ObjectInputStream");
				}
				if ("writeObject".equals(methodNode.findFirstToken(
						TokenTypes.IDENT).getText()))
				{
					hasWrite = isISerializableMethod(methodNode,
							"ObjectOutputStream");
				}
			}
			if(hasRead && hasWrite)
			{
				break;
			}
			methodNode = methodNode.getNextSibling();
		}
		
		return hasRead && hasWrite;
	}
	
	/**
	 * <p>
	 * Return true, if methods readObject() and writeObject() have correct
	 * modifiers, type and parameters;
	 * </p>
	 * 
	 * @param methodNode
	 *            - current method node;
	 * @param argType
	 *            - type of arguments for readObject or writObject;
	 * @return boolean value;
	 */
	private boolean isISerializableMethod(DetailAST methodNode, String argType)
	{
		DetailAST parameters = methodNode.findFirstToken(TokenTypes.PARAMETERS);
		DetailAST modifiers = methodNode.findFirstToken(TokenTypes.MODIFIERS);
		DetailAST type = methodNode.findFirstToken(TokenTypes.TYPE);
		boolean param = false;
		boolean isPrivate = false;
		boolean isVoid = false;
		if (parameters.getChildCount(TokenTypes.PARAMETER_DEF) == 1
				&& modifiers.getChildCount() == 1)
		{
			isPrivate = "private".equals(modifiers.getFirstChild().getText());
			isVoid = "void".equals(type.getFirstChild().getText());
			parameters = parameters.findFirstToken(TokenTypes.PARAMETER_DEF)
					.findFirstToken(TokenTypes.TYPE).getFirstChild();
			param = argType.equals(parameters.getText());
		}
		return param && isPrivate && isVoid;
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
