package com.github.sevntu.checkstyle.checks.coding;
public class InputForbidThrowAnonymousExceptions {
	public void anonymousEx() {
		try {
			//some code
		    int k = 4;
		} catch (Exception e) {
		      throw new RuntimeException() { //anonymous exception declaration
		           //some code
		      };
		 } 
	}
	
	public void notAnonEx() {
		try {
			//some code
		    Object o = null;
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}
	
	public void notAnonEx3() {
        RuntimeException re = new RuntimeException();
        try {
            //some code
            String rse = "lol";
        } catch (Exception e) {
            throw re;
        }
    }
	
}