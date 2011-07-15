package TestClasses;

import java.io.IOException;

public class InputOverridableMethodInConstructor6 {

	public static void main(String args[]) throws IOException,
			ClassNotFoundException, CloneNotSupportedException {
		(new Object2()).clone();
	}

	private static class Object1 implements Cloneable {

		public void doSmth() {
			System.out.println("Bar!");
		}

		@Override
		protected Object clone() throws CloneNotSupportedException {
			Object clone = super.clone();
			((Object1) clone).doSmth();
			return clone;
		}

	}

	private static class Object2 extends Object1 {
		private String value;

		public void doSmth() {
			System.out.println(value);
		}

		@Override
		protected Object clone() throws CloneNotSupportedException {
			Object clone = super.clone();
			((Object2) clone).value = "Foo!";
			((Object2) clone).doSmth();
			return clone;
		}
	}

}