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
