import java.io.IOException;

<<<<<<< HEAD
public class InputOverridableMethodInConstructor9 {
=======
public class InputOverridableMethodInConstructor8 {
>>>>>>> 0f2bb80789b144152f5dd13fa0eb07137c73de0e

	public static void main(String args[]) throws IOException,
			ClassNotFoundException, CloneNotSupportedException {
		(new Object2()).clone();
	}

	private static class Object1 implements Cloneable {

<<<<<<< HEAD
		private void doSmth() {
			System.out.println("Bar!");
			doSmth();
			doSmth2(); // call to overridable method
=======
		public void doSmth() {
			System.out.println("Bar!");
>>>>>>> 0f2bb80789b144152f5dd13fa0eb07137c73de0e
		}
		
		public void doSmth2() {
			System.out.println("Bar2!");
		}

		@Override
		protected Object clone() throws CloneNotSupportedException {
			Object clone = super.clone();
<<<<<<< HEAD
            ((Object1) clone).doSmth(); // a warning here
            doSmth2(); // a warning here
=======
			((Object1) clone).doSmth(); // a warning here
			//doSmth2(); // a warning here
>>>>>>> 0f2bb80789b144152f5dd13fa0eb07137c73de0e
			return clone;
		}

	}

<<<<<<< HEAD
	static class Object2 extends Object1 {
		private String value;

=======
	private static class Object2 extends Object1 {
		private String value;

		public void doSmth() {
			System.out.println(value);
		}

>>>>>>> 0f2bb80789b144152f5dd13fa0eb07137c73de0e
		@Override
		public void doSmth2() {
			System.out.println(value);
		}
		
		@Override
		protected Object clone() throws CloneNotSupportedException {
			Object clone = super.clone();
			((Object2) clone).value = "Foo!";
<<<<<<< HEAD
			//((Object2) clone).doSmth2();
=======
			((Object2) clone).doSmth();
>>>>>>> 0f2bb80789b144152f5dd13fa0eb07137c73de0e
			return clone;
		}
	}

}

	

