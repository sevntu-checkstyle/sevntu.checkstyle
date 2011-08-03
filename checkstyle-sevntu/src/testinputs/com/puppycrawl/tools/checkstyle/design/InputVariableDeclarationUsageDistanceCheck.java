package com.puppycrawl.tools.checkstyle.design;

public class InputVariableDeclarationUsageDistanceCheck {

	private int test1;

	static {
		int b;
		int d;
		{
			d = ++b; // DECLARATION SHOULD BE HERE
		}
	}
	
	static {
		int c;
		int a = 3;
		int b = 2;
		{ // DECLARATION SHOULD BE HERE
			a = a + b; // DECLARATION SHOULD BE HERE
			c = b;
		}
		{
			c--;
		}
		a = 7;
	}

	static {
		int a = -1;
		int b = 2;
		b++; // DECLARATION SHOULD BE HERE
		int c = --b;
		a = b; // DECLARATION SHOULD BE HERE
	}

	public InputVariableDeclarationUsageDistanceCheck(int test1) {
		int temp = -1;
		this.test1 = test1;
		temp = test1; // DECLARATION SHOULD BE HERE
	}

	public boolean testMethod() {
		int temp = 7;
		new InputVariableDeclarationUsageDistanceCheck(2);
		InputVariableDeclarationUsageDistanceCheck(temp); // DECLARATION SHOULD BE HERE
		boolean result = false;
		String str = "";
		if (test1 > 1) { // DECLARATION SHOULD BE HERE
			str = "123"; // DECLARATION SHOULD BE HERE
			result = true;
		}
		return result;
	}

	public void testMethod2() {
		int count;
		int a = 3;
		int b = 2;
		{
			a = a  // DECLARATION SHOULD BE HERE
					+ b 
					- 5 
					+ 2 
					* a;
			count = b; // DECLARATION SHOULD BE HERE
		}
	}

	public void testMethod3() {
		int count;
		int a = 3;
		int b = 3;
		a = a + b; // DECLARATION SHOULD BE HERE
		b = a + a;
		testMethod2();
		count = b; // DECLARATION SHOULD BE HERE
	}
	
	public void testMethod4(int arg) {
		int d;
		for (int i = 0; i < 10; i++) {
			d++; // DECLARATION SHOULD BE HERE
			if (i > 5) {
				d += arg;
			}
		}
		
		String ar[] = {"1", "2"};
		for (String st : ar) { // DECLARATION SHOULD BE HERE
			System.out.println(st);
		}
	}
	
	public void testMethod5() {
		int arg = 7;
		boolean b = true;
		boolean bb = false;
		if (b) // DECLARATION SHOULD BE HERE
			if (!bb) // DECLARATION SHOULD BE HERE
				b = false;
		testMethod4(arg); // DECLARATION SHOULD BE HERE
	}
	
	public void testMethod6() {
		int blockNumWithSimilarVar = 3;
		int dist = 0;
		int index = 0;
		int block = 0;
		
		if (blockNumWithSimilarVar <= 1) { // DECLARATION SHOULD BE HERE
			do {
				dist++;
				if (block > 4) {
					break;
				}
				index++;
				block++;
			} while (index < 7);
		} else {
			while (index < 8) {
				dist += block;
				index++;
				block++;
			}
		}
	}
	
	public boolean testMethod7(int a) {
		boolean res;
		switch(a) { // DECLARATION SHOULD BE HERE
		case 1:
			res = true;
			break;
		default:
			res = false;
		}
		return res;	
	}

	public void testMethod8() {
		int b;
		int c;
		int m;
		int n;
		{ // DECLARATION SHOULD BE HERE
			c++; // DECLARATION SHOULD BE HERE
			b++;
		}
		{
			n++; // DECLARATION SHOULD BE HERE
			m++; // DECLARATION SHOULD BE HERE
			b++;
		}
	}
	
	public void testMethod9() {
		boolean result = false;
		boolean b1 = true;
		boolean b2 = false;
		if (b1) { // DECLARATION SHOULD BE HERE
			if (!b2) { // DECLARATION SHOULD BE HERE
				result = true;
		    }
		    result = true;
		}
	}
	
	public boolean testMethod10() {
		boolean result;
		try { // DECLARATION SHOULD BE HERE
			result = true;
		} catch (IOException e) {
			result = false;
		} finally {
			result = false;
		}
		return result;
	}
	
	public void testMethod11() {
		int a = 0;
		int b = 10;
		boolean result;
		try {
			b--; // DECLARATION SHOULD BE HERE
		} catch (IOException e) {
			result = false; // DECLARATION SHOULD BE HERE
		} finally {
			a++; // DECLARATION SHOULD BE HERE
		}
	}

	public void testMethod12() {
		boolean result = false;
		boolean b3 = true;
		boolean b1 = true;
		boolean b2 = false;
		if (b1) { // DECLARATION SHOULD BE HERE
			if (b3) { // DECLARATION SHOULD BE HERE
				if (!b2) { // DECLARATION SHOULD BE HERE
					result = true;
				}
				result = true;
			}
		}
	}
}
