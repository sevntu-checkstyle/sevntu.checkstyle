package com.puppycrawl.tools.checkstyle.coding;

import java.rmi.AccessException;

public class InputAvoidHidingCauseExceptionCheck3
{

    public void bar()
    {
        boolean x = false;

        try {
        }
        
        catch (IndexOutOfBoundsException e) {
            //your code
            throw new RuntimeException();
        }
        
        catch (RuntimeException e) {
            //your code
            throw e;
        }
        
        catch (java.lang.Exception e) {
            //your code
            throw new RuntimeException(e);
        }
       
    }
}