package com.github.sevntu.checkstyle.checks.coding;
public class InputForbidThrowAnonymousExceptions {
	public void anonymousEx() {
		try {
			//some code
		} catch (Exception e) {
		      throw new RuntimeException() { //anonymous exception declaration
		           //some code
		      };
		 } 
	}
	
	public void notAnonEx() {
		try {
			//some code
		} catch (Exception e) {
			throw new RuntimeException();
		}
	}
	
	//limitation: This Check does not validate cases then Exception object is created before it is thrown
	public void notAnonEx2() {
		RuntimeException re = new RuntimeException();
		try {
			//some code
		} catch (Exception e) {
			throw re;
		}
	}
}