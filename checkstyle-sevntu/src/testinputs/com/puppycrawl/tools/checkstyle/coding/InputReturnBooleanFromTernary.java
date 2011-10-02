package com.github.sevntu.checkstyle.coding;

public class InputReturnBooleanFromTernary{
	
	public void logged() {
		boolean i1 = true ? false : true;
		Object i2 = (2 == 2) ? 1 : true;
		Object i3 = (2 == 2) ? false : 1;
		boolean i4 = true ? false ? true : true : true;
	}

	public void notLogged() {
		int i2 = true ? 1 : 2;
		int i4 = true ? false ? 1 : 2 : 3;
	}

}
