package com.github.sevntu.checkstyle.checks.coding;

public class InputAvoidNotShortCircuitOperatorsForBooleanCheckBitwiseAfterArray {
    void boolFalse(String[] args) {
        final boolean[] values = {false};
        values[0] |= (args.length > 0);
        final boolean[][] val2 = {{false}, {false}};
        val2[0][0] |= (args.length > 0);
    }
}
