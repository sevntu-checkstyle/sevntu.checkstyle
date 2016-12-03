package com.github.sevntu.checkstyle.checks.coding;

public class InputConfusingConditionCheck2 {
    class InnerEmptyBlocks {
        void foo() {
        }
    }

    InnerEmptyBlocks anon = new InnerEmptyBlocks() {
        boolean flag = true;

        void foo() {
            if(flag);
            else;
        }
    };
}