package com.github.sevntu.checkstyle.checks.coding;
public class InputSimpleAccessorNameNotationCheck {
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
    public int setFirstAgain(int mFirst){
        this.mFirst = mFirst;
        return mFirst;
    }
    public void setTest(int mFirst){
    }
    public void setTest2(int mFirst){
        setTest(mFirst);
    }
    private java.util.List<String> names;
    public void setNames(String[] names) {
        this.names = java.util.Arrays.asList(names);
    }
    public void setNamesTwo(String[] names) {
        if (names == null)
            this.names = null;
        if (names != null)
            this.names = null;
    }
    boolean isExpiredToken() {
        return ((System.currentTimeMillis() - 0) >= 0);
    }
    public static boolean isTrue() {
        return Boolean.TRUE;
    }
    private int colors[];
    private void setColor(int color) {
        colors[0] = color;
    }
    private Object object;
    public void setInvocation(Object invocation) {
        this.object = ((Object)invocation);
    }
    public void setObject(Object object, Object ignore) {
        object = object;
    }
    public static <T> java.util.function.BinaryOperator<T> getTest() {
        return (t1, t2) -> {
            throw new IllegalStateException();
        };
    }
    int getTest2() {
        return ((java.util.Hashtable[]) object).length;
    }
    private static Object sfield;
    public void setTest3(int test) throws Exception {
        InputSimpleAccessorNameNotationCheck.sfield = sfield;
    }
}
interface InnerIterface {
    default void getFoo() {
        return;
    }
}
