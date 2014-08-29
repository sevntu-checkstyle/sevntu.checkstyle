package com.github.sevntu.checkstyle.checks.coding;

public class InputNoNullForCollectionReturnCheck7
{
    private int[] method8()
    {
        Boolean test = null;
        return test == null? null : new int[4];
    }
}
