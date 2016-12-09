package com.github.sevntu.checkstyle.checks.coding;
public class InputOverridableMethodInConstructorCheck25 {

	final class A {
		A() {
			doSmth(); // no warnings here!
		}
		void doSmth() {}
	}
}
