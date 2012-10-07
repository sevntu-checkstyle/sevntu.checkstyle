import java.io.IOException;

public class InputOverridableMethodInConstructor9 {

	public static void main(String args[]) throws IOException,
			ClassNotFoundException, CloneNotSupportedException {
		(new Object2()).clone();
	}

	private class Object1 implements Cloneable {

		private void doSmth() {
			System.out.println("Bar!");
			doSmth();
			doSmth2(); // leads to call of overridable method
		}
		
		public void doSmth2() {
			System.out.println("Bar2!");
		}

		@Override
		protected Object clone() throws CloneNotSupportedException {
			Object clone = super.clone();
            ((Object1) clone).doSmth(); // a warning here
            doSmth2(); // a warning here
			return clone;
		}

	}

	class Object2 extends Object1 {
		private String value;

		@Override
		public void doSmth2() {
			System.out.println(value);
		}
		
		@Override
		protected Object clone() throws CloneNotSupportedException {
			Object clone = super.clone();
			((Object2) clone).value = "Foo!";
			//((Object2) clone).doSmth2();
			return clone;
		}
	}

}

	
