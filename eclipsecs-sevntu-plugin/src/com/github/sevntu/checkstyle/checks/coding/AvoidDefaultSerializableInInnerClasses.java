package com.github.sevntu.checkstyle.checks.coding;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * This check prevents the default implementation Serializable interface in
 * inner classes (Serializable interface are default if methods readObject() or
 * writeObject() are not override in class). Check has option, that allow
 * implementation only one method, if it true, but if it false - class must
 * implement both methods. For more information read
 * "Effective Java (2nd edition)" chapter 11, item 74, page 294.
 * </p>
 * 
 * @author <a href="mailto:IliaDubinin91@gmail.com">Ilia Dubinin</a>
 */
public class AvoidDefaultSerializableInInnerClasses extends Check 
{
	
	private boolean allowPartialImplementation;

	/**
	 * <p>
	 * Set allow partly implementation serializable interface.
	 * </p>
	 * 
	 * @param allow
	 */
	public void setAllowPartialImplementation(boolean allow) 
	{
		this.allowPartialImplementation = allow;
	}

	@Override
	public int[] getDefaultTokens() {
		return new int[] { TokenTypes.CLASS_DEF };
	}

	@Override
	public void visitToken(DetailAST aDetailAST) 
	{
		boolean topLevelClass = (aDetailAST.getParent() == null);
		if (!topLevelClass && isSerializable(aDetailAST) && !isStatic(aDetailAST)
				&& !hasSerialazableMethods(aDetailAST)) 
		{
			DetailAST implementsBlock = aDetailAST
					.findFirstToken(TokenTypes.IMPLEMENTS_CLAUSE);
			log(implementsBlock.getLineNo(),
					"avoid.default.serializable.in.inner.classes");
		}
	}

	/**
	 * <p>
	 * Return true if it is nested class.
	 * Terminology is here : http://download.oracle.com/javase/tutorial/java/javaOO/nested.html
	 * </p>
	 * 
	 * @param aDetailAST
	 * @return
	 */
	private static boolean isStatic(DetailAST classNode) 
	{
		boolean result = false;
		DetailAST modifiers = classNode.findFirstToken(TokenTypes.MODIFIERS);
		if (modifiers != null) 
		{
			modifiers = modifiers.getFirstChild();
			while (!result && modifiers != null) 
			{
				result = "static".equals(modifiers.getText());
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
		DetailAST methodNode = classNode.findFirstToken(TokenTypes.OBJBLOCK)
				.findFirstToken(TokenTypes.METHOD_DEF);
		boolean hasRead = false, hasWrite = false, result = false;
		while (!result && methodNode != null) 
		{
			if (TokenTypes.METHOD_DEF == methodNode.getType()) 
			{
				String methodName = methodNode.findFirstToken(TokenTypes.IDENT)
						.getText();
				if ("readObject".equals(methodName)) 
				{
					hasRead = isPrivateMethod(methodNode);
				}
				if ("writeObject".equals(methodName)) 
				{
					hasWrite = isPrivateMethod(methodNode);
				}
			}
			if (allowPartialImplementation)
			{
				result = hasRead || hasWrite;
			} else 
			{
				result = hasRead && hasWrite;
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
	private static boolean isPrivateMethod(DetailAST methodNode) 
	{
		DetailAST modifiers = methodNode.findFirstToken(TokenTypes.MODIFIERS);
		modifiers = modifiers.getFirstChild();
		boolean isPrivate = false;
		while(!isPrivate && modifiers != null) 
		{
			isPrivate = "private".equals(modifiers.getText());
			modifiers = modifiers.getNextSibling();
		}
		return isPrivate;
	}

	/**
	 * <p>
	 * Return true, if class implement Serializable interface;
	 * </p>
	 * 
	 * @param classDefNode
	 *            - the start node for class definition.
	 * @return boolean value. True, if class implements Serializable interface.
	 */
	private static boolean isSerializable(DetailAST classDefNode) 
	{
		DetailAST implementationsDef = classDefNode
				.findFirstToken(TokenTypes.IMPLEMENTS_CLAUSE);
		boolean result = false;
		if (implementationsDef != null)
		{
			implementationsDef = implementationsDef.getFirstChild();

			while (!result && implementationsDef != null) 
			{
				result = "Serializable".equals(implementationsDef.getText());
				implementationsDef = implementationsDef.getNextSibling();
			}
		}
		return result;
	}

}
