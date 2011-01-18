package com.puppycrawl.tools.checkstyle.coding;

import java.rmi.AccessException;

public class InputAvoidHidingCauseExceptionCheck2
{

    public void bar()
    {
        boolean x = false;

        try {
        }

        catch (IndexOutOfBoundsException e) { // Warning here!
            //your code
            throw new RuntimeException("Exception!!");
        }    
               
    
        
    }
}