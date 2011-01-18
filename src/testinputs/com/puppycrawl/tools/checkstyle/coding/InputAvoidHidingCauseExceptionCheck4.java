package com.puppycrawl.tools.checkstyle.coding;

import java.rmi.AccessException;

public class InputAvoidHidingCauseExceptionCheck4
{

    public void bar()
    {
        boolean x = false;

        try {
        }

        catch (Throwable e) { // warning here! 
            //your code
            if (x) {
                while (x) {
                    do {
                        int k = Integer.numberOfLeadingZeros(100000);
            throw new RuntimeException("Exception!!");
                    } while (x);
                }
            }

        }
        
    
        
    }
}