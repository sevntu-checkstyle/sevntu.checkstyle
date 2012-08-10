package com.github.sevntu.checkstyle.checks.coding;

import java.util.Iterator;

import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;

public final class Utils {

	private Utils() {

	}

	
	
	/**
	 * <p>
	 * Return true, if method is private.
	 * </p>
	 * 
	 * @param aMethodNode
	 *            - current method node;
	 * @return boolean value;
	 */
	public static String getMethodName(DetailAST aMethodNode)
	{
		assert TokenTypes.METHOD_DEF == aMethodNode.getType();
		return aMethodNode.findFirstToken(TokenTypes.IDENT).getText();
	}
	
	/**
	 * <p>
	 * Return true, if method is private.
	 * </p>
	 * 
	 * @param aMethodNode
	 *            - current method node;
	 * @return boolean value;
	 */
	public static boolean isPrivateMethod(DetailAST aMethodNode)
	{
		assert TokenTypes.METHOD_DEF == aMethodNode.getType();
		DetailAST modifiers = aMethodNode.findFirstToken(TokenTypes.MODIFIERS);
		modifiers = modifiers.getFirstChild();
		boolean isPrivate = false;
		while (!isPrivate && modifiers != null) {
			isPrivate = "private".equals(modifiers.getText());
			modifiers = modifiers.getNextSibling();
		}
		return isPrivate;
	}
	
	public static ChildrenIterable iterate(DetailAST aParent, int aChildType) {
		return new ChildrenIterable(aParent, aChildType);
	}

	private static class ChildrenIterable implements Iterable<DetailAST> {
		private final ChildrenIterator iterator;

		public ChildrenIterable(DetailAST aParent, int aChildType) {
			iterator = new ChildrenIterator(aParent, aChildType);

		}

		public Iterator<DetailAST> iterator() {
			return iterator;
		}

	}

	/**
	 * <b> Nested class, that implements custom iterator for DetailAST method nodes. </b>
	 */
	private static class ChildrenIterator implements Iterator<DetailAST>
	{
		/**
		 * <b> Type of child. </b>
		 */

		private final int mChildType;
		/**
		 * <b> Next </b>
		 */
		private DetailAST mNext;

		/**
		 * <b> Children Iterator constructor. </b>
		 * 
		 * @param aParent
		 *            - child parent.
		 * @param aChildType
		 *            - type of child.
		 */
		public ChildrenIterator(DetailAST aParent, int aChildType)
		{
			this.mChildType = aChildType;
			mNext = aParent.findFirstToken(aChildType);
		}

		/**
		 * <b> Method iterator. </b>
		 * 
		 * @param aParent
		 *            - parent.
		 * @return method iterator.
		 */
		public static ChildrenIterator methodsIterator(DetailAST aParent)
		{
			return new ChildrenIterator(aParent, TokenTypes.METHOD_DEF);
		}

		/**
		 * <b> Return boolean value, if has next element. </b>
		 * 
		 * @return boolean value
		 */
		public boolean hasNext()
		{
			return mNext != null;
		}

		/**
		 * <b> Return next DetailAST element. </b>
		 * 
		 * @return next DetailAST.
		 */
		public DetailAST next()
		{
			final DetailAST result = mNext;
			while (mNext != null) {
				mNext = mNext.getNextSibling();
				if (mNext != null && mNext.getType() == mChildType) {
					break;
				}
			}
			return result;
		}

		/**
		 * <b> Not implemented method. </b>
		 */
		public void remove()
		{
			throw new IllegalStateException("Not implemented");
		}
	}

}
