package com.puppycrawl.tools.checkstyle.checks.coding;
public class InputSimpleAccesorNameNotation {
	private int mFirst;
	private int mSecond;
	private int mA;
	private int preB;
	public void setFirs(int k){
		mFirst=k;
	}
	public int getFir(){
		return this.mFirst;
	}
	public void setSec(int k){
		this.mSecond=k;
	}
	public int getSec(){
		return mSecond;
	}
	public void setAa(int k){
		k=mA;
	}
	public int getAa(int k){
		return k;
	}
	public void setB(int k){
		this.preB=k;
	}
	public int getB(){
		return preB;
	}
	public void setFirst(int mFirst){
		this.mFirst = mFirst;
	}
}
