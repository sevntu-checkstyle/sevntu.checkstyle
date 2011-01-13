package com.puppycrawl.tools.checkstyle.coding;

public class InputIllegalCatchCheck
{
    public void foo()
    {
        try {
        }
        catch (RuntimeException e) {
        }
        catch (Exception e) {
        }
        catch (Throwable e) {
        }
    }

    public void bar()
    {
        boolean x = true;

        try {
        }

        catch (RuntimeException e) {
            //your code (logging, variable cleanup, etc)
            throw e;
        }

        catch (Exception e) {
            //your code (logging, variable cleanup, etc)
            throw new RuntimeException(e);
        }

        catch (Throwable e) {
            //your code (logging, variable cleanup, etc)
            if (x) {
                throw new RuntimeException(e);
            }

        }
    }
}