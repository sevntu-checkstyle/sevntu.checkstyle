package com.github.sevntu.checkstyle.checks.coding;

import java.awt.EventQueue;

public class InputAvoidNotShortCircuitOperatorsForBooleanCheckLambdaWithMultiCatch {
    public void method() {
        EventQueue.invokeLater(() -> {
            if (true) {
                try {
                }
                catch (NullPointerException | IndexOutOfBoundsException ex) {
                }
            }
        });
    }
}
