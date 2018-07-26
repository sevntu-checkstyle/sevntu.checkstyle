package com.github.sevntu.checkstyle.checks.coding;
public class InputSimpleAccessorNameNotationCheck2
{
    private String field1;
    private int field2;
    private String field3;
    private String mField3;
    private int mField4;

    public void setField1(String aField1) //prefix "m" !!
    {
        field1 = aField1;
    }
    
    public String getFiel1() // !! in both case
    {
        return field1;
    }
    
    public void setFiel2(String aField2) // !! in both case
    {
        field3 = aField2;
    }
    
    public int getField2() //prefix "m" !!
    {
        return field2;
    }
    
    public void setField3(String aField3) //prefix "" !!
    {
        mField3 = aField3;
    }
    
    public String getFiel3() // !! in both case
    {
        return mField3;
    }
    
    public void setFiel4(String aField4) // !! in both case
    {
        mField3 = aField4;
    }
    
    public int getField4() //prefix "" !!
    {
        return mField4;
    }
    
}
