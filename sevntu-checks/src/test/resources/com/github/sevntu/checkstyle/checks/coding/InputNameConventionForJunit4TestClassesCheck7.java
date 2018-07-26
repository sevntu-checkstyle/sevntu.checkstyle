package com.github.sevntu.checkstyle.checks.coding;


public class InputNameConventionForJunit4TestClassesCheck7
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
