package com.github.sevntu.checkstyle.checks.coding;

import java.util.function.Function;

public class InputNumericLiteralNeedsUnderscoreCheck3 {

    private static final float G = 6.6740831E-11f;
    private static final double H = 6.626070040E-34d;

    float serialVersionUID = 0x1234567F;

    public void foo() {
        SomeBean bean = new SomeBean() {
            private static final long serialVersionUID = -35450329839532371L;
            public static final long ABC = 1234567L;
            public static final long RED = 0xFF0000FFL;
            public static long blue = 0x0000FFFFL;

            @Override
            public int bar() {
                Function<Integer, Integer> f = (i) -> {
                    return 1234567 + i;
                };
                return 1234567;
            }
        };
    }

    private void bar() {
        float a = 0x0000FFP1F;
        float b = 0x0000FFp1F;
        float c = 0x0000FF;
        float d = 0x0000FF00;
    }

    static class SomeException extends Exception {

        private static final long serialVersionUID = -35450329839532371L;
        private static final int CONSTANT = 1;
        static final int RED = 0xF00000;

    }

    static abstract class SomeBean {
        public abstract int bar();
    }

}
