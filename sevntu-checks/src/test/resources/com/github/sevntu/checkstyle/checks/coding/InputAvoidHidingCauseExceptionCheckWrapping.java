package com.github.sevntu.checkstyle.checks.coding;

public class InputAvoidHidingCauseExceptionCheckWrapping {
    private Exception field;

    public void executeLocal() {
        try {
        }
        catch (final Exception e) {
            this.field = InputAvoidHidingCauseExceptionCheckWrapping.method(new Exception(e));
        }
    }

    private static Exception method(Exception exception) {
        return exception;
    }
}
