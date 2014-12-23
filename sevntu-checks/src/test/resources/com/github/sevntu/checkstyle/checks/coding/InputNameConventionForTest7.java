package com.github.sevntu.checkstyle.checks.coding;


public class InputNameConventionForTest7
{
    public class InnerTestClass
    {
        @SomeTestAnnotation
        public void method()
        {

        }
    }

    public @interface SomeTestAnnotation
    {

    }
}
