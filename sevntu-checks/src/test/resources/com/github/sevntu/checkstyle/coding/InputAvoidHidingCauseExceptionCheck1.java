package com.github.sevntu.checkstyle.checks.coding;

public class InputAvoidHidingCauseExceptionCheck1 {

    public void TestNestedANDNotSimple() {

        RuntimeException myOwnException;
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
                throw new RuntimeException(myOwnException); // !!!!
            }            
            throw new RuntimeException(e); //
        }

        catch (NullPointerException e) { // nested: IDENT.getMessage() situation 
            // with good and bad reaction and DOT situation

            // your code
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

        catch (java.lang.ArithmeticException e) { // tests situation #5
            ArithmeticException ex = null;
            ArithmeticException ex1 = null;
            if (e instanceof ArithmeticException) {
                ex = (ArithmeticException) e; // bad
             // ex1 = (ArithmeticException) e; // good
                throw ex; //
            } else {
                ex = new ArithmeticException(e.getMessage());
            }
            throw ex1; // !!!!!
        }

        catch (IllegalArgumentException e) {
            // your code
            RuntimeException modelEx = new RuntimeException(e);
            RuntimeException modelEx2 = null;// = new RuntimeException(e);
            if (modelEx != null) {
                throw modelEx; //
            }
            throw new RuntimeException("Exception on set property to value! " +modelEx2.getMessage(), modelEx2); // !!!!!
        }


        catch (Exception e) {
            RuntimeException sqlEx = new RuntimeException("failed to open DB connection to: " + e); // null; 
            try {
                sqlEx.initCause(e);
            } catch (Exception e2) {
                // ignore
            }
            throw sqlEx; // 
        }

       catch (Throwable e) {
           RuntimeException ex = null;
           RuntimeException ex4 = null;
        if (e instanceof RuntimeException) {
            ex = (RuntimeException)e; // null;            
        } else {
           ex4 = new RuntimeException(e);
           ex4 = null; // Unsolved situation ?
        }
            throw ex4;
    }


    }
}


