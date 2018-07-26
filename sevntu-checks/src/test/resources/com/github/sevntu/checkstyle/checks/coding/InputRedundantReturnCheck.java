package com.github.sevntu.checkstyle.checks.coding;

public class InputRedundantReturnCheck {

	/**
	 * @param args
	 */
	public InputRedundantReturnCheck(){
		//allowed/not empty c-tor
		return; //WARNING if option 'allowReturnInEmptyMethodsAndConstructors' is off
	}//c-tor
	
	public	InputRedundantReturnCheck(String s){
		//non empty constructor
		boolean b = true;
		b = b ? true: false;
		return;	//WARNING
	}//c-tor2
	
	public void testMethod1(){
		//check the allowed/not empty method
		return;	//WARNING if option 'allowReturnInEmptyMethodsAndConstructors' is off
	}//testMethod1
	
	public void testMethod2(){
		//nested method definition
		MyTestClass test = new MyTestClass(){
			public void testMethod(){
				int y=0;
				int u=8;
				int e=u-y;
				return;	//WARNING
			}
		};
		
		for (int i = 0; i < 10; i++) {
			i++;
		}
		return;	//WARNING
	}//testMethod1
	
	public static void main(String[] args) {
		System.out.println("Hello, World !!!");
	}//void main
	
	public void testTryCatch()
	{
		try {
			int y=0;
			int u=8;
			int e=u-y;
			return;	//WARNING
		} 
		catch (Exception e) {
			System.out.println(e);
			return;	//WARNING
		}
		finally
		{
			return;	//WARNING
		}
	}
	
	public void testTryCatch2()
	{
		try {
		} 
		catch (Exception e) {
		}
		finally
		{
		}
	}
	
	public void testNoBraces(){
		int i=0;
		while(true) if(i++ == 10) return;
	}//testNoBraces

	public void testNoBraces2(){
		for(int i=0;true;i++) if(i == 10) return;
	}//testNoBraces2
	
	private class MyTestClass{
		public MyTestClass(){}
		public void testMethod(){
			return;	//WARNING if option 'allowReturnInEmptyMethodsAndConstructors' is off
		}	
	}//myTestClass
	
	public void testTryCatch3()
	{
		try {
			int y=0;
			int u=8;
			int e=u-y;
		} 
		catch (IllegalArgumentException e) {
			System.out.println(e);
			return;	//WARNING
		}
		catch (IllegalStateException ex) {
		    	System.out.println(ex);
		    	return;	//WARNING
		}
	}
	
    public void testTryCatch4()
    {
	    int y=0;
        int u=8;
        try {
            int e=u-y;
        } 
        catch (IllegalArgumentException e) {
            System.out.println(e);
            return;	//WARNING
        }
    }
    public void setFormats() {
		try {
			int k = 4;
		} catch (Exception e) {
			Object k = null;
			if (k != null)
				k = "ss";
			else {
				return; //WARNING
			}
		}
    }
    public void setFormats1() {
		try {
			int k = 4;
		} catch (Exception e) {
			Object k = null;
			if (k != null) {
				k = "ss";
			} else {
				return; //WARNING
			}
		}
    }
    public void setFormats2() {
		try {
			int k = 4;
		} catch (Exception e) {
			Object k = null;
			if (k != null) {
				k = "ss";
				return;	//WARNING
			} 
		}
    }
    public void setFormats3() {
		try {
			int k = 4;
		} catch (Exception e) {
			Object k = null;
			if (k != null) {
				k = "ss";
				
			} 
		}
    }
    public int getRandomNumber() {
    	return 4;
    }
    public InputRedundantReturnCheck(double content) {
    	
    }
    public void setFormat() {
		try {
			int k = 4;
		} catch (Exception e) {
			Object k = null;
			if (k != null) {
				k = "ss";
				if (k.toString().concat("ss") == "ssss") {
					if (e.getMessage() == "Exception") {
						return;	// WARNING
					}
				}
			} 
		}
    }
    
    private static void foo(int x) {}
    
    private static void foo1() {
    	try {
    		char c = 'c';
    	} finally {
    		
    	}
    }
    
}
