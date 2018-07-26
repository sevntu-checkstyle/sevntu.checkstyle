package com.github.sevntu.checkstyle.checks.coding;

public class InputOverridableMethodInConstructorCheck27
{

    public static void main(String[] args)
    {

        class Base27
        {
            Base27()
            {
                System.out.println("Base27 C-tor.");
                overrideMe("override me nonprivate"); // warning here should be
                init(); //warning here should be
                this.toString();
            }

            void overrideMe(String aString)
            {
                System.out.println(aString);
            }

            private void overrideMe()
            {
                System.out.println("Base overrideMe().");
            }

            void init()
            {

            }
        }

        class Child27 extends Base27
        {
            final int x;

            Child27(int x)
            {
                this.x = x;
                System.out.println("Child27 C-tor.");
            }

            void init()
            {
                System.out.println("Overridden init");
            }

            void overrideMe(String aString)
            {
                System.out.println("Child27 overrideMe(): " + x);
            }
        }
    }

}
