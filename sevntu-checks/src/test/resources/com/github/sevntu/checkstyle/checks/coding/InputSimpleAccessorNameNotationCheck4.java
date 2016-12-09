package com.github.sevntu.checkstyle.checks.coding;
public class InputSimpleAccessorNameNotationCheck4
{
    private String field1;
    private int mField2;

    public void setField1(String aField1)
    {
        aField1 = field1;
    }

    public String getField1()
    {
        String anyString = null;
        return anyString;
    }

    public void setField2(int field2)
    {
        field2 = mField2;
    }

    public int getField2()
    {
        String anyString = null;
        return 4;
    }
}
