package com.github.sevntu.checkstyle.checks.coding;

public class InputLogicConditionNeedOptimizationCheckPatterns {
    void m1(Object o) {
        for (int i = 0; o instanceof Integer myInt && myInt > 5;) { // ok
            // type pattern, no `PATTERN_DEF`
        }
        for (int i = 0; o instanceof (Integer myInt && myInt > 5);) { // ok
            // parenthesized pattern, `PATTERN_DEF`
        }
        for (int i = 0; o instanceof Integer myInt; ) { // ok
            // type pattern, no `PATTERN_DEF`
        }
    }

}
