package com.puppycrawl.tools.checkstyle.checks.coding;

import java.util.LinkedList;

import com.puppycrawl.tools.checkstyle.api.Check;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

/**
 * <p>
 * This check prevents the default implementation Serializable interface in
 * inner classes (Serializable interface are default if methods readObject() or
 * writeObject() are not override in class).
 * For more information read "Effective Java (2nd edition)" chapter 11, item 74, page 294.
 * </p>
 * 
 * @author <a href="mailto:IliaDubinin91@gmail.com">Ilia Dubinin</a>
 */
public class AvoidDefaultSerializableInInnerClasses extends Check
{

	@Override
	public int[] getDefaultTokens()
	{
		return new int[] { TokenTypes.OBJBLOCK };
	}

	@Override
	public void visitToken(DetailAST aDetailAST)
	{
		LinkedList<DetailAST> listOfClasses = new LinkedList<DetailAST>();
		listOfClasses = fillSerializableClassList(aDetailAST, listOfClasses);
		if (listOfClasses.size() > 0)
		{
			for (DetailAST current : listOfClasses)
			{
				if (!isContainsOverridedMethod(current))
				{
					DetailAST implementsBlock = current
							.findFirstToken(TokenTypes.IMPLEMENTS_CLAUSE);
					log(implementsBlock.getLineNo(),
							"avoid.default.serializable.in.inner.classes");
				}
			}
		}
	}

	/**
	 * <p>
	 * Method add to list all classes nodes, that implement Serializable
	 * interface;
	 * </p>
	 * 
	 * @param objBlock - OBJBLOCK node of top class
	 */
	private LinkedList<DetailAST> fillSerializableClassList(DetailAST objBlock,
			LinkedList<DetailAST> listOfClasses)
	{
		DetailAST current = objBlock.getFirstChild();
		while (current != null)
		{
			if ("CLASS_DEF".equals(current.getText()))
			{
				if (isSerializable(current))
				{
					listOfClasses.add(current);
				}
				listOfClasses = fillSerializableClassList(
						current.findFirstToken(TokenTypes.OBJBLOCK),
						listOfClasses);
			}
			if ("METHOD_DEF".equals(current.getText())
					|| "CTOR_DEF".equals(current.getText()))
			{
				listOfClasses = fillSerializableClassList(
						current.findFirstToken(TokenTypes.SLIST), listOfClasses);
			}
			current = current.getNextSibling();
		}
		return listOfClasses;
	}

	/**
	 * <p>
	 * Return true, if inner class contain overrided method readObject() or
	 * writeObject();
	 * </p>
	 * 
	 * @param methNode the start node for method definition.
	 * @return The boolean value. True, if method was overrided. problem. null
	 *         pointer if class don't have any method
	 */
	private boolean isContainsOverridedMethod(DetailAST classNode)
	{
		boolean result = false;
		DetailAST methodNode = classNode.findFirstToken(TokenTypes.OBJBLOCK);
		if ((methodNode = methodNode.findFirstToken(TokenTypes.METHOD_DEF)) != null)
		{
			for (DetailAST node : getMethodList(methodNode))
			{
				if ("readObject".equals(node.findFirstToken(TokenTypes.IDENT)
						.getText()))
					result = isReallyOverloaded(node, "ObjectInputStream");
				if ("writeObject".equals(node.findFirstToken(TokenTypes.IDENT)
						.getText()))
					result = isReallyOverloaded(node, "ObjectOutputStream");
			}
		}
		return result;
	}

	/**
	 * <p>
	 * Return true, if methods readObject() and writeObject() have correct
	 * modifiers, type and parameters;
	 * </p>
	 * 
	 * @param methodNode - current method node;
	 * @param argType - type of arguments for readObject or writObject;
	 * @return boolean value;
	 */
	private boolean isReallyOverloaded(DetailAST methodNode, String argType)
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
		return param & isPrivate & isVoid;
	}

	/**
	 * <p>
	 * Return true, if inner class implement Serializable interface;
	 * </p>
	 * 
	 * @param methNode the start node for interface definition.
	 * @return The boolean value.
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

	/**
	 * <p>
	 * Method return the list of DetailAST nodes, that are sibling for "node"
	 * and have node's type;
	 * </p>
	 * 
	 * @param node
	 * @return list of DetailAST nodes;
	 */
	private LinkedList<DetailAST> getMethodList(DetailAST node)
	{
		LinkedList<DetailAST> listOfNodes = new LinkedList<DetailAST>();
		DetailAST current = node;
		while (current != null)
		{
			if (node.getText().equals(current.getText()))
			{
				listOfNodes.add(current);
			}
			current = current.getNextSibling();
		}
		return listOfNodes;

	}
}
