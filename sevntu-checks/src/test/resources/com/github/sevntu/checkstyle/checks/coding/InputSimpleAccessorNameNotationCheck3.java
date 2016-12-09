package com.github.sevntu.checkstyle.checks.coding;
public class InputSimpleAccessorNameNotationCheck3
{
    private String field1;
    private int mField2;
    private String mField3;
    public void setFiel1(String aField1) // !! in both case
    {
        field1 = aField1;
    }

    public String getFiel1() // !! in both case
    {
        return field1;
    }

    public void setFie2(String aField2) // !! in both case
    {
        mField3 = aField2;
    }

    public int getFiel2() // !! in both case
    {
        return mField2;
    }

}
