package com.github.sevntu.checkstyle.checks.coding;

import static org.junit.Assert.*;
import org.junit.Test;

public class InputRequireFailForTryCatchInJunitCheck8 {
    @Test
    public void junit4Test() {
        try {
            fail();
        }
        catch (Exception expected) {
        }
    }
}
