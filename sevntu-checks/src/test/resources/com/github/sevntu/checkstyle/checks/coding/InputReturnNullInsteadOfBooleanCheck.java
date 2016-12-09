package com.github.sevntu.checkstyle.checks.coding;

public class InputReturnNullInsteadOfBooleanCheck{
	
    public Boolean test1() {
        int i = 1;
        switch (i) {
        case 1: return true;
        case 2: return false;
        }
        return null; //this should be logged
    }

    //this method is not the violation and should not be logged
    public Boolean test2(){
    	return null == null ? Boolean.FALSE:Boolean.TRUE;
    }
    
    public void test3(){
    	TestInterface testI = new TestInterface(){
    		public Boolean testMethod(){
    			return null; //this should be logged
    		}
    		
    	};
    }
    
    public InputReturnNullInsteadOfBooleanCheck(){
        return; //here was NPE once apon a time
    }
    
    public Object test4(){
    	TestInterface testI = new TestInterface(){
    		public Boolean testMethod(){
    			return true;
    		}
    		
    	};
    	return null; //this should not be logged
    }
    
    public interface TestInterface{
    	public Boolean testMethod();
    }
}
