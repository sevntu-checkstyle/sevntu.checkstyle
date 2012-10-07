package com.github.sevntu.checkstyle.checks.coding;

public class InputIllegalCatchCheckNew
{
    public void foo()
    {
        try {
        }
        catch (RuntimeException e) {
        }
        catch (java.lang.Exception e) {
        }
        catch (Throwable e) {
        }
    }

    public void bar()
    {
        boolean x = false;

        try {
        }

        catch (RuntimeException e) {
            //your code
            throw e;
        }

        catch (java.lang.Exception e) {
            //your code
            throw new RuntimeException(e);
        }

        catch (Throwable e) {
            //your code
            if (x) {
                while (x) {
                    do {
                        int k = Integer.numberOfLeadingZeros(100000);
            throw new RuntimeException(e);
                    } while (x);
                }
            }

        }
    }
}