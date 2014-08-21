package com.github.sevntu.checkstyle.checks.coding;
public class InputOverridableMethodInConstructor21 {

	public InputOverridableMethodInConstructor21() {
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