package com.github.sevntu.checkstyle.checks.coding;
 public class InputForbidCCommentsInMethodsCheck
{
    /*
     * first comment, that hasn't error
     */
    
    private void anyMethod()
    {
        /*
         *the first comment, that has error
         */
        int i = 0;
        i++;
        //second comment, that hasn't error
        String s = new String();
        /*the second comment, that has error*/
    }
    private class anyInnerClass
    {
        /*
         * third comment, that hasn't error
         */
        private void anyMethod()
        {
            /*
             *the third comment, that has error
             */
            int i = 0;
            i++;
            //fourth comment, that hasn't error
            String s = new String();
            /*the fourth comment, that has error*/
        }
    }
}
class InnerInputForbidCCommentsInMethods2
{
    /*
     * fifth comment, that hasn't error
     */
    
    private void anyMethod()
    {
        /*
         *the fifth comment, that has error
         */
        int i = 0;
        i++;
        //sixth comment, that hasn't error
        String s = new String();
        /*the sixth comment, that has error*/
    }
    private class anyInnerClass
    {
        /*
         * seventh comment, that hasn't error
         */
        private void anyMethod()
        {
            /*
             *the seventh comment, that has error
             */
            int i = 0;
            i++;
            //eigth comment, that hasn't error
            String s = new String();
            /*the eigth comment, that has error*/
        }
    }
}
class InnerInputForbidCCommentsInMethods3 {
    /* Comment before field. */
    private int field;

    /* Comment on same line as method. */ void method1() {}
    void method2() {} /* Comment on same line as method. */
    void method3() { /* Comment on same line as method but inside. */ }

    void method4() {
        new innerClass() {
            /* Comment inside anonymous class. */
            public void innerMethod() {}
        };
    }

    private static void /* Comment insside definition. */ method5() {}
    /* Comment before native method. */
    public native void method6();

    private abstract class innerClass {
        public abstract void innerMethod();
    }
}
