package com.github.sevntu.checkstyle.checks.coding;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class InputRequireFailForTryCatchInJunitCheck9 {
    @Test
    public void junit4Test() {
        try {
            // fail();
        }
        catch (Exception expected) {
        }
    }
}
