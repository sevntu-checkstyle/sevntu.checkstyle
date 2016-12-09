package com.github.sevntu.checkstyle.checks.design;
import java.util.*;
public class InputAvoidConditionInversionCheck
{
	private boolean flag = false;
	private boolean isDifferent(int a, int b) {
		return ! (a == b);	//WARNING
	}
	
	private boolean isLower(int a, int b) {
		return ! (a > b);	//WARNING
	}
	
	private boolean isLowerLimits(int a, int b) {
		return ! ((a>=8) && (b>=5));	//WARNING
	}
	
	private void foo(int a, int b) {
		if (! ((a>=8) && (b>=5))) {	//WARNING
			// do some stuff
		}
		
		while (! ((a>=8) && (b>=5))) {	//WARNING
			// do some stuff
		}
		
		do {
			// do some stuff
		} while ((! ((a>=8) && (b>=5))));	//WARNING
		
		for (int i = 0; (! ((a>=8) && (b>=5))); i++) {	//WARNING
			//do some stuff
		}
		
	}
	
	private boolean fun(int a, int b) {
		String str = "";
		return !(a < b 	
				&& str.toLowerCase().contains("a") 
				|| b < a 
				&& !str.toLowerCase().contains("b"));
		/*
		 * return (a >= b 
		 * && str.toLowerCase().contains("a") 
		 * || a >= b 
		 * && !str.toLowerCase().contains("b"));
		 */
	}
	
	private void o() {
		if (true) {
			for (;;);
		}
		return;
	}
	
	private int getRandomNumber() {
		for (int i = 0; i < 4; i++);
		return 4;
	}
	
	private boolean isNotEmpty() {
		List<String> list = new ArrayList<String>();
		return !list.isEmpty();	//No WARNING
		
	}
	
	private void testLoops() {
		String str = "";
		
		while (!"".concat("s").equals("ss")) {  //No WARNING	
			
			
			
		}
		
		for (String line = "s"; !(line.endsWith("s"));) {	//No WARNING 
			
			line += "s"; 
		}
		
	}
	
	private void foo1() {
		String s = "";
		if (!(s instanceof String)) {	//No WARNING
			
			//do some stuff
		}
		
		boolean b = false;
		
		if (!b) {	//No WARNING
			//do some stuff
		}
		
		if (!(this.flag)) {	//No WARNING
			
			//do some stuff
		}
		if (! ("s" instanceof String || this instanceof Object)) { //WARNING if property 
			//'applyOnlyToRelationalOperands' is false
			//do some stuff
		}
		boolean a = false;
		if (! (a && b)) { //WARNING if property 
			//'applyOnlyToRelationalOperands' is false
			//do some stuff
		}
	}
	
}
