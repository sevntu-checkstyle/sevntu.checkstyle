package com.github.sevntu.checkstyle.checks.coding;

import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.Assert.assertEquals;

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

    @Test
    public void method5() {
        try {
            throw new NullPointerException();
        }
        catch (Exception expected) {
            assertThat(expected).isInstanceOf(NullPointerException.class);
        }
    }
}

class MyAutoClosable implements AutoCloseable {
    @Override
    public void close() throws Exception {
    }
}
