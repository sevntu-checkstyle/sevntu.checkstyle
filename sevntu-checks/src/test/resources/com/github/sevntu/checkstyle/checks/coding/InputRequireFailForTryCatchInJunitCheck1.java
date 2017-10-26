package com.github.sevntu.checkstyle.checks.coding;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class InputRequireFailForTryCatchInJunitCheck1 {
    static {
    }

    static {
        try {
        }
        catch (Exception e) {
        }
    }

    public void method1() {
    }

    public void method2() {
        try {
        }
        catch (Exception e) {
        }
    }

    @Test
    public void method3() {
    }

    @Test
    public void method4() throws Exception {
        try {
        }
        finally {
        }
        try (MyAutoClosable test = new MyAutoClosable()) {
            test.close();
        }
    }
}

class MyAutoClosable implements AutoCloseable {
    @Override
    public void close() throws Exception {
    }
}
