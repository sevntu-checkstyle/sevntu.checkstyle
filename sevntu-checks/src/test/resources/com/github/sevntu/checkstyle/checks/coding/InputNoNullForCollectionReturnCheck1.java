package com.github.sevntu.checkstyle.checks.coding;
public class InputNoNullForCollectionReturnCheck1
{
    private int[] method1()
    {
        return new int[5];
    }
    
    private int[] method2()
    {
        return null; //!!
    }
    
    private int[] method3()
    {
        int[] array;
        array = null;
        return array; //!!
    }
    
    private int[] method4()
    {
        int[] array = new int[4];
        return array;
    }
    
    private int[] method5()
    {
        int[] array = null;
        array = method4();
        return array;
    }
    
    private int[] method6()
    {
        int[] array = null;
        int[] array2 = new int[5];
        array = array2;
        return array;
    }
    
    private int[] method7()
    {
        if(true)
        {
            return null; //!!
        }
        return new int[4];
    }
    
    private int[] method8()
    {
        if(true)
            return null; //!!
        if(true)
        {
            if(false)
            {
                return null; //!!
            }
        }
        Boolean test = false;
        return test == null? new int[3] : new int[4];
    }
    
}
