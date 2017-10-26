package com.github.sevntu.checkstyle.checks.coding;

import static org.junit.Assert.fail;

import java.util.function.Consumer;

import org.junit.Assert;
import org.junit.Test;

public class InputRequireFailForTryCatchInJunitCheck5 {
    @Test
    public void method1() throws Exception {
        try {
            Assert.fail();
        }
        catch (Exception e) {
        }
        try {
            fail();
        }
        catch (Exception e) {
        }

        final Consumer<Object> test = ast -> {
            try {
            }
            catch (Exception e) {
                // fail can't be required because lambdas can't throw checked
                // exceptions, so they must be caught and if warranted,
                // re-thrown as an unchecked exception
            }
        };
    }

    @org.junit.Test
    public void method2() throws Exception {
        try {
            Assert.fail();
        }
        catch (Exception e) {
        }
        try {
            fail();
        }
        catch (Exception e) {
        }

        final Consumer<Object> test = ast -> {
            try {
            }
            catch (Exception e) {
                // fail can't be required because lambdas can't throw checked
                // exceptions, so they must be caught and if warranted,
                // re-thrown as an unchecked exception
            }
        };
    }
}
