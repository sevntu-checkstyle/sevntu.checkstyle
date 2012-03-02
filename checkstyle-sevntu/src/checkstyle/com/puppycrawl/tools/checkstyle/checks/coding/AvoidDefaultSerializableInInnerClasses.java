package com.puppycrawl.tools.checkstyle.checks.coding;

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
	@Override
	public int[] getDefaultTokens()
	{
		return new int[] { TokenTypes.IMPLEMENTS_CLAUSE };
	}

	@Override
	public void visitToken(DetailAST aDetailAST)
	{
		DetailAST current = aDetailAST.findFirstToken(TokenTypes.OBJBLOCK);
		while (current != null)
		{
			if (!isSerializable(current
					.findFirstToken(TokenTypes.IMPLEMENTS_CLAUSE)))
				continue;
			if (scanMeth(current.findFirstToken(TokenTypes.METHOD_DEF)))
				continue;
			else
				log(current.getLineNo(),
						" Avoid.default.serializable.in.inner.classes",
						current.getText());
		}
	}

	/**
	 * <p>
	 * Return true, if inner class contain override method readObject() or
	 * writeObject();
	 * 
	 * @param methNode
	 *            the start node for method definition.
	 * @return The boolean value.
	 */
	private boolean scanMeth(DetailAST methNode)
	{
		while (methNode != null)
			if (methNode.findFirstToken(TokenTypes.IDENT).getText()
					.equals("readObject")
					|| methNode.findFirstToken(TokenTypes.IDENT).getText()
							.equals("writeObject"))
				return true;
			else
				methNode = methNode.findFirstToken(TokenTypes.METHOD_DEF);
		return false;
	}

	/**
	 * <p>
	 * Return true, if inner class implement Serializable interface;
	 * 
	 * @param methNode
	 *            the start node for interface definition.
	 * @return The boolean value.
	 */
	private boolean isSerializable(DetailAST impNode)
	{
		impNode = impNode.getFirstChild();
		while (impNode != null)
		{
			if (impNode.getText().equals("Serializable"))
				return true;
			impNode = impNode.getNextSibling();
		}
		return false;
	}
}