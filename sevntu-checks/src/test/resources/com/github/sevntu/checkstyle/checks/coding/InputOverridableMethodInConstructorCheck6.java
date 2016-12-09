package com.github.sevntu.checkstyle.checks.coding;
import java.io.IOException;
public class InputOverridableMethodInConstructorCheck6 {

	public static void main(String args[]) throws IOException,
	                ClassNotFoundException, CloneNotSupportedException {
		(new Object2()).clone();
	}

	static private class Object2 implements Cloneable {

		// final
		private void doSmth() {
			System.out.println("Bar!");
		}
		
		@Override
		protected Object clone() throws CloneNotSupportedException {
			Object clone = super.clone();
			((Object2) clone).doSmth(); // no warnings here
			return clone;
		}

	}

}

	

