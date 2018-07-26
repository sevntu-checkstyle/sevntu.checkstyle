package com.github.sevntu.checkstyle.checks.coding;

import org.junit.Test;

public class InputRequireFailForTryCatchInJunitCheck3 {
    @Test
    public void method1() throws Exception {
        try {
        }
        catch (Exception e) {
        }
        try {
            for (int a = 0; a < 5; a++) {
            }
        }
        catch (Exception e) {
        }
        try {
            method1();
        }
        catch (Exception e) {
        }
        try {
            InputRequireFailForTryCatchInJunitCheck3.method();
        }
        catch (Exception e) {
        }
        try {
            fail("message");
        }
        catch (Exception e) {
        }
        try {
            Assert.fail("message");
        }
        catch (Exception e) {
        }
    }

    @org.junit.Test
    public void method2() throws Exception {
        try {
        }
        catch (Exception e) {
        }
        try {
            for (int a = 0; a < 5; a++) {
            }
        }
        catch (Exception e) {
        }
        try {
            method2();
        }
        catch (Exception e) {
        }
        try {
            InputRequireFailForTryCatchInJunitCheck3.method();
        }
        catch (Exception e) {
        }
        try {
            fail("message");
        }
        catch (Exception e) {
        }
        try {
            Assert.fail("message");
        }
        catch (Exception e) {
        }
    }

    @Test
    public void method3() throws Exception {
        try {
            int a = 0;
            a++;
        }
        catch (Exception e) {
        }
    }

    public static void method() {
    }

    public static void fail(String s) {
    }

    public static class Assert {
        public static void fail(String s) {
        }
    }
}
