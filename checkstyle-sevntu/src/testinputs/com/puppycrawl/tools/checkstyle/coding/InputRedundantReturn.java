package com.puppycrawl.tools.checkstyle.coding;

import com.HelloWorld.MyTestClass;

public class HelloWorld {

	/**
	 * @param args
	 */
	public HelloWorld(){
		//check the allowed/not empty c-tor
		return;
	}//c-tor
	
	public	HelloWorld(String s){
		//check the non empty costructor
		boolean b = true;
		b = b ? true: false;
		return;
	}//c-tor2
	
	public void testMethod1(){
		//check the allowed/not empty method
		return;
	}//testMethod1
	
	public void testMethod2(){
		//nested method definition
		MyTestClass test = new MyTestClass(){
			public void testMethod(){
				int y=0;
				int u=8;
				int e=u-y;
				return;
			}
		};
		
		for (int i = 0; i < 10; i++) {
			i++;
		}
		return;
	}//testMethod1
	
	public static void main(String[] args) {
		System.out.println("Hello, World !!!");// TODO Auto-generated method s
	}//void main
	
	public void testTryCatch()
	{
		try {
			int y=0;
			int u=8;
			int e=u-y;
			return;
		} 
		catch (Exception e) {
			System.out.println(e);
			return;
		}
		finally
		{
			return;
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
	
		public void testMethod(){
			return;
		}	
	}//myTestClass
	
}
