import java.io.IOException;

public class InputOverridableMethodInConstructor8 {

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
<<<<<<< HEAD
            ((Object1) clone).doSmth(); // a warning here
=======
			((Object1) clone).doSmth(); // a warning here
>>>>>>> 0f2bb80789b144152f5dd13fa0eb07137c73de0e
			return clone;
		}
	}

	private static class Object2 extends Object1 {
		private String value;

		@Override
		public void doSmth() {
			System.out.println(value);
		}

		@Override
<<<<<<< HEAD
        protected Object clone() throws CloneNotSupportedException {
			Object clone = super.clone();
			((Object2) clone).value = "Foo!";
            ((Object2) clone).doSmth(); // a warning here
=======
		protected Object clone() throws CloneNotSupportedException {
			Object clone = super.clone();
			((Object2) clone).value = "Foo!";
			((Object2) clone).doSmth(); // a warning here
>>>>>>> 0f2bb80789b144152f5dd13fa0eb07137c73de0e
			return clone;
		}
	}
}