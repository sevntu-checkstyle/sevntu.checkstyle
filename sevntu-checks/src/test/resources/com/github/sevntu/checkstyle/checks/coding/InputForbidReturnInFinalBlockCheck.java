package com.github.sevntu.checkstyle.checks.coding;

public class InputForbidReturnInFinalBlockCheck
{
    private class A1
    {
        boolean meth1()
        {
            boolean g = false;
            try {
                return false;
            }
            finally {
                return g;
            }
        }
    }

    class A2
    {
        boolean meth2()
        {
            try {

            }
            finally {
                if (true) {
                    for (;;) {
                        try {
                        }
                        catch (Exception e) {
                            if (true)
                                return true;
                        }
                    }
                }
                return false;
            }
        }

        boolean meth3()
        {
            try {

            }
            finally {
                try {

                }
                catch (Exception e) {

                }
                finally {
                    return false;
                }
            }
        }

        boolean meth4()
        {
            try {

            }
            finally {
            }
            try {
            }
            catch (Exception e) {

            }
            return false;
        }

    }

    private static void meth5()
    {
        try {
            while (true) {
                break;
            }
        }
        finally {
        }
    }

}
