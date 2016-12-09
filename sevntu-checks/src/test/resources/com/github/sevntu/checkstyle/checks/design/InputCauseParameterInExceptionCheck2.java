package com.github.sevntu.checkstyle.checks.design;

public class InputCauseParameterInExceptionCheck2
{
    private class TestException2 extends Exception {
    public TestException2(String str)
    {
        super(str, null);
    }

    public TestException2(String str, int integer)
    {
        super(str, null);
    }

    class MyException2 {
        
        public MyException2()
        {
        }
        
        public MyException2(String str)
        {
        }
        
        public MyException2(String str, int integer)
        {
        }
        
    }   
    }
}
