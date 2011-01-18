package com.puppycrawl.tools.checkstyle.coding;

import java.rmi.AccessException;

public class InputAvoidHidingCauseExceptionCheck1
{
    public void foo()
    {
        try { // empty 
        }
        catch (IndexOutOfBoundsException e) {
        }

        catch (ArithmeticException e) {
            //your code
            throw new RuntimeException("dcdf",e);
        }

        catch (RuntimeException e) {
            //your code
            throw new RuntimeException("dcdf");
        }
        
    }

}