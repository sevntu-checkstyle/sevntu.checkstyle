package com.github.sevntu.checkstyle.checks.design;
import com.github.sevntu.checkstyle.checks.coding.ParentException;
public class InputCauseParameterInException2 extends ParentException
{

    public InputCauseParameterInException2(String str)
    {
        super(str, null);
    }

    public InputCauseParameterInException2(String str, int integer)
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