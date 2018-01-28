////////////////////////////////////////////////////////////////////////////////
// Test case file for checkstyle.
// Created: 2017
////////////////////////////////////////////////////////////////////////////////
package com.github.sevntu.checkstyle.checks.coding;

import org.junit.Assert;

import java.io.File;
import java.util.Arrays;

/**
 * Test case for detecting usage of forbidden method.
 * @author Raghav Kumar Gautam
 **/
class InputForbidCertainMethodCheck
{
    /**
     * no param constructor
     */
    InputForbidCertainMethodCheck() {
        System.exit(1);
    }

    /**
     * non final param method
     */
    void method(String s) {
        Assert.assertTrue(1 != 2);
        Assert.assertTrue("Good assert with some reason.", true);
        ForbiddenMethod.exit2();
        ForbiddenMethod.INSTANCE.exit2();
        //method call that does not need "." in it
        method("");
        // new usage that does not invoke constructor
        String[] strs = new String[4];
        Arrays.stream(strs).map(File::new).count();
    }

    private static class ForbiddenMethod {
        static final ForbiddenMethod INSTANCE = new ForbiddenMethod();
        static void exit2() {
            Arrays.asList(1);
            Arrays.asList(1, 2);
            Arrays.asList(1, 2, 3);
            Arrays.asList(1, 2, 3, 4);
            Arrays.asList(1, 2, 3, 4, 5);
            Arrays.asList(1, 2, 3, 4, 5, 6);
            Arrays.asList(1, 2, 3, 4, 5, 6, 7);
            Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
            Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9);
            Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
            Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
            Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);
        }
    }
}
