package com.github.sevntu.checkstyle.checks.coding;

public class InputForbidReturnInFinallyBlockCheck
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
                if (1 != 2) {
                    return g; //violation
                }
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
                                return true; //violation
                        }
                    }
                }

            }
            return false;
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
                    return false; //violation
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

        void meth5()
        {
            try {

            }
            finally {
                return; //violation
            }
        }

    }

    private static void meth6()
    {
        try {
            while (true) {
                break;
            }
        }
        finally {
        }
    }

    public void meth7()
    {
        try {
            
        }
        finally {
            Runtime.getRuntime().addShutdownHook(new Thread()
            {
                @Override
                public void run()
                {
                    return;
                }
            });
        }
    }
}
