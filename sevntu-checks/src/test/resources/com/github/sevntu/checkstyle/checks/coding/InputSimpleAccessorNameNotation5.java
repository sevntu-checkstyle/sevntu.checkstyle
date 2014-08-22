package com.github.sevntu.checkstyle.checks.coding;
public class InputSimpleAccessorNameNotation5
{
    private boolean field1; 
    private boolean field2;
    private boolean field3;

    public void setField1(boolean aField1)
    {
        field1 = aField1;
    }

    public boolean isField1()
    {
        return field1;
    }

    public void setField2(boolean aField2)
    {
        aField2 = field2;
    }

    public boolean isField2()
    {
        return false;
    }
    
    public void setField3(boolean aField3) //prefix "" !!
    {
        field1 = aField3;
    }

    public boolean isField3() //prefix "" !!
    {
        return field1;
    }
}
