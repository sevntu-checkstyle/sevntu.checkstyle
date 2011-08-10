public class InputOverridableMethodInConstructor25 {

	final class A {
		A() {
			doSmth(); // no warnings here!
		}
		void doSmth() {}
	}
}