package com.github.sevntu.checkstyle.checks.coding;

import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class InputRequireFailForTryCatchInJunitCheck6 {
    @Test
    public void valid() {
        try {
            Assertions.assertEquals("", "");
            Assertions.fail("");
        }
        catch (Exception expected) {
            Assertions.assertEquals(expected.getMessage(), "");
        }
    }

    @Test
    public void validStatic() {
        try {
            Assertions.assertTrue(false, "");
            fail("");
        }
        catch (Exception expected) {
            Assertions.assertEquals(expected.getMessage(), "");
        }
    }

    @Test
    public void violation() {
        try { // violation
            System.out.println("");
        }
        catch (Exception expected) {
            Assertions.assertEquals(expected.getMessage(), "");
        }
    }
}
