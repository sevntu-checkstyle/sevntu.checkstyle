package com.puppycrawl.tools.checkstyle.coding;

public class InputAvoidHidingCauseExceptionCheck1 {

    public void TestNested() {

        RuntimeException r;
        try {
        } 
        catch (ClassCastException e) { // nested: good --> bad
            //your code
            try {
            } catch (RuntimeException n) {
                throw new RuntimeException(n); //
            }
            throw new RuntimeException(); // !!!!!
        }

        catch (IndexOutOfBoundsException e) { // nested: bad --> good            
            //your code
            try {
            } catch (RuntimeException n) {
                throw new RuntimeException(r); // !!!!
            }            
            throw new RuntimeException(e); //
        }

        catch (java.lang.ArithmeticException e) { // nested: IDENT.getMessage() situation with good and bad reaction 
            //your code
            try {
                try {
                } catch (RuntimeException x) {
                    //your code
                    throw new RuntimeException(x.getMessage(), e); // !!!!
                }

            } catch (java.lang.ArithmeticException x) {
                //your code
                throw new RuntimeException(e.getMessage(), x); //
            }
            throw new RuntimeException(e.getMessage()); // !!!!!
        }
    }
}
