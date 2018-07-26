package com.github.sevntu.checkstyle.checks.coding;

public class InputRequireFailForTryCatchInJunitCheck2 {
    @Test
    public void method() {
        try {
        }
        catch (Exception e) {
        }
    }
}

@interface Test {
}
