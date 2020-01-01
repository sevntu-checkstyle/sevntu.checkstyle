package com.github.sevntu.checkstyle.checks.coding;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;

public class InputRequireFailForTryCatchInJunitCheck7 {
    @org.junit.Test
    public void junit4Test() {
        try { // violation
            Assert.assertEquals("", "");
        }
        catch (Exception expected) {
            Assert.assertEquals(expected.getMessage(), "");
        }
    }
    @org.junit.jupiter.api.Test
    public void junit5Test() {
        try { // violation
            Assertions.assertEquals("", "");
        }
        catch (Exception expected) {
            Assertions.assertEquals(expected.getMessage(), "");
        }
    }
}
