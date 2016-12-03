package com.github.sevntu.checkstyle.checks.coding;

public class InputReturnCountExtendedCheckAnonymousClasses {
    public int method() {
        class InnerClass {
            public int method() {
                return 1;
            }
        }

        return 1;
    }

    public int method2() {
        class InnerClass {
            public int method2() {
                if (false)
                    return 0;

                return 1;
            }
        }

        if (false)
            return 0;

        return 1;
    }
}