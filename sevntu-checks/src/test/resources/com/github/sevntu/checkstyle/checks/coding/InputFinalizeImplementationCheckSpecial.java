package com.github.sevntu.checkstyle.checks.coding;

public class InputFinalizeImplementationCheckSpecial {
    private boolean condition;
    @Override
    protected void finalize() throws Throwable {
        if (condition) {
            method();
        }
        method();
        super.finalize();
    }

    private void method() {
    }
}
class InputFinalizeImplementationCheckSpecial2 {
    @Override
    protected void finalize() throws Throwable {
        this.method();
    }

    private void method() {
    }
}
