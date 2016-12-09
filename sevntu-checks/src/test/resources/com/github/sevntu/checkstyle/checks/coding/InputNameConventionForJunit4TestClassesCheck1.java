package com.github.sevntu.checkstyle.checks.coding;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


public class InputNameConventionForJunit4TestClassesCheck1
{

    @RunWith(Suite.class)
    @SuiteClasses(InnerTestClass.class)
    public class InnerTestClass
    {
        
    }
}
