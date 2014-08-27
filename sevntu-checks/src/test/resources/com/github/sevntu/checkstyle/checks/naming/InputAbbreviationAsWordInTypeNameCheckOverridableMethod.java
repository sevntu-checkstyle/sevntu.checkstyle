package com.github.sevntu.checkstyle.checks.naming;

import org.junit.Before;

abstract class NonAAAAbstractClassName1 extends AbbreviationClass {
	public int serialNUMBER = 6;
	public final int s1erialNUMBER = 6;
	private static int s2erialNUMBER = 6;
	private static final int s3erialNUMBER = 6;
	
	@Override
	@SuppressWarnings(value = { "" })
	@Before
	protected void oveRRRRRrriddenMethod(){
	    int a = 0;
	    // blah-blah
	}
}

class Class1 {
    
    private void oveRRRRRrriddenMethod(){
        int a = 0;
        // blah-blah
    }
    
}

class Class2 extends AbbreviationClass {
    
    @Override
    @SuppressWarnings(value = { "" })
    @Before
    protected void oveRRRRRrriddenMethod1(){
        int a = 0;
        // blah-blah
    }
    
}
