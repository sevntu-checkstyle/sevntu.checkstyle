package TestClasses;

import java.io.IOException;
import java.io.Serializable;

public class TestSerialization {

	public static void main(String args[]) throws IOException,
			ClassNotFoundException, CloneNotSupportedException {
		(new Object2()).clone();
	}

	private static class Object1 implements Cloneable {

		private void doSmth() {
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

		private void doSmth() {
			System.out.println(value.toUpperCase());
		}

		@Override
		protected Object clone() throws CloneNotSupportedException {
			Object clone = super.clone();
			((Object2) clone).value = "Foo";
			((Object2) clone).doSmth();
			return clone;
		}
	}

}

	

