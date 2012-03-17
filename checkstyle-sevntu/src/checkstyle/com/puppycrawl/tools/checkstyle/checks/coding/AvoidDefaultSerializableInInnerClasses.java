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
 * </p>
 * 
 * @author <a href="mailto:IliaDubinin91@gmail.com">Ilia Dubinin</a>
 */
public class AvoidDefaultSerializableInInnerClasses extends Check
{
	private LinkedList<DetailAST> listOfClasses = new LinkedList<DetailAST>();

	@Override
	public int[] getDefaultTokens()
	{
		return new int[] { TokenTypes.OBJBLOCK };
	}

	@Override
	public void visitToken(DetailAST aDetailAST)
	{
		fillSerializableClassList(aDetailAST);
		if (listOfClasses.size() > 0)
			for (DetailAST current : listOfClasses)
				if (!scanMethod(current))
				{
					DetailAST implementsBlock = current
							.findFirstToken(TokenTypes.IMPLEMENTS_CLAUSE);
					log(implementsBlock.getLineNo(),
							"avoid.default.serializable.in.inner.classes");
				}
	}

	/**
	 * all okay
	 * 
	 * @param root
	 */
	private void fillSerializableClassList(DetailAST root)
	{
		DetailAST current = root.getFirstChild();
		while (current != null)
		{
			if ("CLASS_DEF".equals(current.getText()))
			{
				if (isSerializable(current))
					listOfClasses.add(current);
				fillSerializableClassList(current
						.findFirstToken(TokenTypes.OBJBLOCK));
			}
			if ("METHOD_DEF".equals(current.getText())
					|| "CTOR_DEF".equals(current.getText()))
				fillSerializableClassList(current
						.findFirstToken(TokenTypes.SLIST));
			current = current.getNextSibling();
		}
	}

	/**
	 * <p>
	 * Return true, if inner class contain overrided method readObject() or
	 * writeObject();
	 * 
	 * @param methNode
	 *            the start node for method definition.
	 * @return The boolean value. True, if method was overrided. problem. null
	 *         pointer if class don't have any method
	 */
	private boolean scanMethod(DetailAST classNode)
	{
		boolean result = false;
		DetailAST methodNode = classNode.findFirstToken(TokenTypes.OBJBLOCK);
		if ((methodNode = methodNode.findFirstToken(TokenTypes.METHOD_DEF)) != null)
			for (DetailAST node : getList(methodNode))
			{
				if ("readObject".equals(node.findFirstToken(TokenTypes.IDENT)
						.getText()))
					result = isNotOverloaded(node, "ObjectInputStream");
				if ("writeObject".equals(node.findFirstToken(TokenTypes.IDENT)
						.getText()))
					result = isNotOverloaded(node, "ObjectOutputStream");
			}
		return result;
	}

	// не перегружены ли методы сериализации
	private boolean isNotOverloaded(DetailAST methodNode, String argType)
	{
		DetailAST parameters = methodNode.findFirstToken(TokenTypes.PARAMETERS);
		boolean result = false;
		if (parameters.getChildCount(TokenTypes.PARAMETER_DEF) == 1)
		{
			parameters = parameters.findFirstToken(TokenTypes.PARAMETER_DEF)
					.findFirstToken(TokenTypes.TYPE).getFirstChild();
			result = argType.equals(parameters.getText());
		}
		return result;
	}

	/**
	 * <p>
	 * Return true, if inner class implement Serializable interface;
	 * 
	 * @param methNode
	 *            the start node for interface definition.
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

	private LinkedList<DetailAST> getList(DetailAST node)
	{
		LinkedList<DetailAST> listOfNodes = new LinkedList<DetailAST>();
		DetailAST current = node;
		while (current != null)
		{
			if (node.getText().equals(current.getText()))
				listOfNodes.add(current);
			current = current.getNextSibling();
		}
		return listOfNodes;

	}
}