package com.github.sevntu.checkstyle.checks.coding;
public class InputNoNullForCollectionReturnCheck3
{
    public Object[] scrollTo(String revision) throws Throwable {
        Object[] record = null;

        if (true) {
            if (true) {
                record = new Object[10];
                for (int index = 0; index < record.length; ++index) {
                    if (true) {
                        record[index] = "";
                        return null; //!!
                    } else {
                        record[index] = "";
                        return null; //!!
                    }
                }
            }
            record = new Object[6];
        } else {
            record = new Object[6];
        }

        return record; 
    }
    
    public String[] method2()
    {
        String[] result = null;
        if (true) {
            result = new String[] {  };
        } else {
            result = new String[] {  };
        }
        return result;
    }
}