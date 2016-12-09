package com.github.sevntu.checkstyle.checks.coding;
public class InputOverridableMethodInConstructorCheck21 {

	public InputOverridableMethodInConstructorCheck21() {
		doSmth();
	}

	private void doSmth() {
		doSmth2();
	}

	private void doSmth2() {
		doSmth3();
	}

	private void doSmth3() {
		doSmth();
	}

}
