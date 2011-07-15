package TestClasses;

import java.io.IOException;

public class TestCloneableSeconDepth {

	public static void main(String args[]) throws IOException,
			ClassNotFoundException, CloneNotSupportedException {
		(new Object2()).clone();
	}

	private static class Object1 implements Cloneable {

		private void doSmth() {
			System.out.println("Bar!");
			doSmth2();
		}
		
		public void doSmth2() {
			System.out.println("Bar2!");
		}

		@Override
		protected Object clone() throws CloneNotSupportedException {
			Object clone = super.clone();
			((Object1) clone).doSmth(); // !
			//doSmth2(); // !
			return clone;
		}

	}

	private static class Object2 extends Object1 {
		private String value;

		public void doSmth() {
			System.out.println(value);
		}

		@Override
		public void doSmth2() {
			System.out.println(value);
		}
		
		@Override
		protected Object clone() throws CloneNotSupportedException {
			Object clone = super.clone();
			((Object2) clone).value = "Foo!";
			((Object2) clone).doSmth();
			return clone;
		}
		
		public void a(int x, int y){
			a(5,6);
		}
	}

}

	

