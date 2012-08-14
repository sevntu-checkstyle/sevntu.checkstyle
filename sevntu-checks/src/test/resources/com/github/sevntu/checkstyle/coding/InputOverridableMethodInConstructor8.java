import java.io.IOException;

public class InputOverridableMethodInConstructor8 {

	public static void main(String args[]) throws IOException,
			ClassNotFoundException, CloneNotSupportedException {
		(new Object2()).clone();
	}

	private class Object1 implements Cloneable { 

		
		public void doSmth() {
			System.out.println("Bar!");
		}

		@Override
		protected Object clone() throws CloneNotSupportedException {
			Object clone = super.clone();
            ((Object1) clone).doSmth(); // a warning here
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
        protected Object clone() throws CloneNotSupportedException {
			Object clone = super.clone();
			((Object2) clone).value = "Foo!";
            ((Object2) clone).doSmth(); // a warning here
			return clone;
		}
	}
}