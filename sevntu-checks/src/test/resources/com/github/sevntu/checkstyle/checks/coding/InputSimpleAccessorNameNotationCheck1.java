package com.github.sevntu.checkstyle.checks.coding;
public class InputSimpleAccessorNameNotationCheck1
{
    private String field1;
    private int mField2;

    public void setField1(String aField1) //prefix "m" !!
    {
        this.field1 = aField1;
    }

    public String getField1() //prefix "m" !!
    {
        return this.field1;
    }
    
    public void setField2(int field2) //prefix "" !!
    {
        mField2 = field2;
    }
    
    public int getField2() //prefix "" !!
    {
        return mField2;
    }
}
