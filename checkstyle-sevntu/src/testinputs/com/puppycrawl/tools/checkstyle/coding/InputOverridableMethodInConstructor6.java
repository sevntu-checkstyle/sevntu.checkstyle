import java.io.IOException;

public class InputOverridableMethodInConstructor6 {

	public static void main(String args[]) throws IOException,
			ClassNotFoundException, CloneNotSupportedException {
		(new Object2()).clone();
	}

	private class Object1 implements Cloneable {

		// final
		private void doSmth() {
			System.out.println("Bar!");
		}
		
		@Override
		protected Object clone() throws CloneNotSupportedException {
			Object clone = super.clone();
			((Object1) clone).doSmth(); // no warnings here
			return clone;
		}

	}

}

	

