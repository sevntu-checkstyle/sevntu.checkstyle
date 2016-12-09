package com.github.sevntu.checkstyle.checks.design;

public class InputCauseParameterInExceptionCheck
{
    private class TestException extends Exception {
    public TestException(String str)
    {
        super(str, null);
    }

    class MyException {

        public MyException()
        {
        }

        public MyException(String str)
        {
        }

        public MyException(String str, int integer)
        {
        }

        public MyException(String str, Exception ex) // Exception | Throwable only!
        {
        }

    }

    public TestException(String str, int integer)
    {
        super(str, null);
    }
    }
}
