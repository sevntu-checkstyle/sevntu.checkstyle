import java.io.IOException;

public class InputOverridableMethodInConstructor7 {

	public static void main(String args[]) throws IOException,
			ClassNotFoundException, CloneNotSupportedException {
		(new Object2()).clone();
	}

	private static class Object1 {

	}

	private static class Object2 extends Object1 implements Cloneable {
		private String value;

		@Override
		protected Object clone() throws CloneNotSupportedException {
			Object clone = super.clone();
			((Object2) clone).value = "Foo!";
			return clone;
		}
	}

	private static class Object3 extends Object2 {
		private String value;

		@Override
		protected Object clone() throws CloneNotSupportedException {
			Object clone = super.clone();
			((Object2) clone).value = "Foo!";
			return clone;
		}
	}
}

	

