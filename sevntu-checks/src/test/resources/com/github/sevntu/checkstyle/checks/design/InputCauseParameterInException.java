package com.github.sevntu.checkstyle.checks.design;

public class InputCauseParameterInException extends ParentException
{

    public InputCauseParameterInException(String str)
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

    public InputCauseParameterInException(String str, int integer)
    {
        super(str, null);
    }
    
    private class ParentException {

        public ParentException(String str, Exception e) {
        }

        public ParentException(Exception cause) {
        }

    }

}