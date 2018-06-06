package com.github.sevntu.checkstyle.checks.coding;

public class InputRedundantReturnCheckNestedMethods {
    public void method() {
        try {
            if (true) {
                new Nested() {
                    @Override
                    public boolean accept(String s) {
                        return true;
                    }
                };
            }
        }
        catch (Exception ex) {
        }
        finally {
        }
    }

    public void method2() {
        try {
            if (new Nested() {
                @Override
                public boolean accept(String s) {
                    return true;
                }
            }.accept(null)) {
                return; // violation
            }
        }
        catch (Exception ex) {
        }
        finally {
        }
    }

    public abstract class Nested {
        public abstract boolean accept(String s);
    }
}
