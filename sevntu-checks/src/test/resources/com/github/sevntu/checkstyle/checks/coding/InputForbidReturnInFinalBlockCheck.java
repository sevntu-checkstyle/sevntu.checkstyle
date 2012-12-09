public class A1
{
    boolean meth1()
    {

        try {
            return;
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
                            return t;
                    }
                }
            }
            return;
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
                return g;
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
        return;
    }

}
