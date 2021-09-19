package com.github.sevntu.checkstyle.checks.coding;

import static org.junit.jupiter.api.Assertions.fail;

import java.util.function.Consumer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class InputRequireFailForTryCatchInJunitCheck5 {
    @Test
    public void method1() throws Exception {
        try {
            Assertions.fail();
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

    @org.junit.jupiter.api.Test
    public void method2() throws Exception {
        try {
            Assertions.fail();
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
