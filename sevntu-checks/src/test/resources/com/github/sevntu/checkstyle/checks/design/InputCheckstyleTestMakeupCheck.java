package com.github.sevntu.checkstyle.checks.design;

import org.junit.Test;

public class InputCheckstyleTestMakeupCheck {
    private String s;

    public void method1() {
    }

    public void method2() {
        String s = "";
        method1();
    }

    @Test
    public void method3() {
    }

    @org.junit.Test
    public void method4() {
    }

    @Test
    public void method5() {
        new Thread(new Runnable() {
            private String s;

            @Override
            public void run() {
            }
        });
    }

    @Test
    public void method6() {
        String s = "";
        java.util.List<String> t;
        method1();
        InputCheckstyleTestMakeupCheck.test();
        addAttribute("", "");
        test2().test3();
    }

    private static void test() {
    }

    private static void addAttribute(String s, String t) {
    }

    private InputCheckstyleTestMakeupCheck test2() {
        return this;
    }

    private InputCheckstyleTestMakeupCheck test3() {
        return this;
    }
}
