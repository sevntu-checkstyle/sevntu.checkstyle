package com.github.sevntu.checkstyle.checks.coding;

public class InputAvoidHidingCauseExceptionCheck
{
    public Exception fakeException = new Exception();
    
    public void Simple()
    {
        RuntimeException r = new RuntimeException();
        try {
        }
        catch (ClassCastException  e) {
            //your code
        }

        catch (IndexOutOfBoundsException e) {
            //your code
            throw new RuntimeException();  // !
        }

        catch (IllegalStateException e) {
            //your code
            throw new RuntimeException("Runtime Exception!"); // !
        }

        catch (java.lang.ArithmeticException e) {
            //your code
            throw new RuntimeException("Runtime Exception!", e);
        }

        catch (RuntimeException e) {
            //your code
            throw e;
        }

        catch (java.lang.Exception e) {
            //your code
            throw r;  // !
        }

    }
    
    public void Stronger()
    { 
        boolean x = false;
        RuntimeException r = new RuntimeException();
        try {       

        }
        catch (IndexOutOfBoundsException e) { 
            //your code
            if (x&x | !x) {
                while (!!!!!!!!x) {
                    for (int ee = 0 ; ee < 10; ee++)
                    throw new RuntimeException(); // !
                }
            }
        }
        
        catch (IllegalStateException e) {
            while (!!!!!!!!x) {
                x = !!!!!!!!false & !!!!!!!!true;
                double kkk = Math.pow(5, 25555555);
                int ee = (int) kkk;
                throw new RuntimeException("Runtime Exception!"); // !            
            }

        }
        catch (java.lang.ArithmeticException e) {
            int []err = new int [50];
            if (err[51]==0) { err[999]++; }
            throw new RuntimeException("Runtime Exception!", e);
        }

        catch (RuntimeException e) {
            for(int a = 0, b = 3; a < 6*a+b; a+= a-2) {
                throw e;
            }            
        }
        
        catch (java.lang.Exception e) { 
            int []err = new int [50];
            int []err2 = new int [50];
            for (int m : err2) {
                throw r; // !
            }
        }
       
    }
    

    public void TestNestedANDNotSimple()
    {
        RuntimeException myOwnException = new RuntimeException();
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
        catch (RuntimeException e) {
            RuntimeException sqlEx = new RuntimeException("failed to open DB connection to: " + e);
            try {
                sqlEx.initCause(e);
            } catch (Exception e2) {
                // ignore
            }
            throw sqlEx; //
        }
        catch (Exception e) {
            RuntimeException ex = null;
            RuntimeException ex4 = null;
            if (e instanceof RuntimeException) {
                ex = (RuntimeException) e; // null;
            } else {
                ex4 = new RuntimeException(e);
                ex4 = null; // No warning. You can change this if you really need.
            }
            throw ex4;
        }
        catch (Throwable e) { // nested try/catch + 
            RuntimeException ex = null;
            try {
            } catch (RuntimeException e2) {
                RuntimeException ex4 = null;
                if (ex4 instanceof RuntimeException) {
                    ex4 = (RuntimeException) e2;
                    ex = (RuntimeException) e;
                }
                throw ex4; //
            }

            if (e instanceof RuntimeException) {
                
            }
            throw ex; //
        }
    }

    public void trickyExamples() throws Exception
    {
        try {
        } catch (IndexOutOfBoundsException fakeException) {
            throw new RuntimeException(this.fakeException);
        }

        try {
        } catch (IndexOutOfBoundsException ee) {
            throw new RuntimeException(exceptionWrapper(ee));
        }

        try {
        } catch (ClassCastException e) {
            throw exceptionWrapper(e);
        }

        try {
        } catch (ClassCastException e) {
            throw this.exceptionWrapper(e);
        }
        try {
        } catch (ClassCastException e) {
            throw InputAvoidHidingCauseExceptionCheck.this.exceptionWrapper(null);
        }

        try {
        } catch (ClassCastException e) {
            throw (ClassCastException) e.getCause();
        }

        try {
        } catch (ClassCastException e) {
            throw new java.util.ArrayList<java.lang.Exception>().get(0);
        }

        try {
        } catch (final ClassCastException e) {
            // e is not populated in throw block, should be reported an problem 
            // but it is probably to complicated to be detected by CheckStyle
            throw new Exception() {
                public boolean equals(Object o) {
                    try {
                    } catch (Exception e2) {
                        return true;
                    }
                    Exception myException = e;
                    return false;
                }
            };
        }
    }

    private static Exception exceptionWrapper(Throwable e)
    {
        return new IllegalArgumentException(e);
    }
}