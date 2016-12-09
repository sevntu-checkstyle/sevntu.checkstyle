package com.github.sevntu.checkstyle.checks.coding;
public class InputOverridableMethodInConstructorCheck22 {

	InputOverridableMethodInConstructorCheck22() {
        doSMTH(); // a warning here!
	}

	private void doSMTH() {
		doSMTH2();
	}

	private void doSMTH2() {
		doSMTH3();
	}

	private void doSMTH3() {
		doSMTH4();
		doPublic();
	}
	
	private void doSMTH4() {
		doSMTH();
	}
	
	public void doPublic() {
	}
	
}
