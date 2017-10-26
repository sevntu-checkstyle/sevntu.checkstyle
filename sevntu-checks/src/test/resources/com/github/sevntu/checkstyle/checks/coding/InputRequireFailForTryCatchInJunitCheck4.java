package com.github.sevntu.checkstyle.checks.coding;

import org.junit.Assert;
import org.junit.Test;

public class InputRequireFailForTryCatchInJunitCheck4 {
    @Test
    public void method1() throws Exception {
        try {
            Assert.assertEquals("", "");
        }
        catch (Exception e) {
        }
        try {
            Dummy.fail();
        }
        catch (Exception e) {
        }
    }

    public static class Dummy {
        public static void fail() {
        }
    }
}
