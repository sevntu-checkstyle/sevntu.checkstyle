package com.github.sevntu.checkstyle.checks.design;

import java.io.File;

import org.junit.Test;

public class InputCheckstyleTestMakeupCheckInvalid {
    @Test
    public void method1() {
        verify();
    }

    @Test
    public void method2() {
        DefaultConfiguration config;
        verify();
    }

    @Test
    public void method3() {
        DefaultConfiguration config = customCreateConfig();
        verify();
    }

    @Test
    public void method4() {
        DefaultConfiguration config = new DefaultConfiguration();
        verify();
    }

    @Test
    public void method5() {
        final Configuration config = createModuleConfig();
        File file = new File("");
        file = new File("");
        config.addAttribute("", file.getPath());
        config.addAttribute("", file.getAbsolutePath());
        config.addAttribute("", customValue());
        config.addAttribute("", ENUM.TEST.getName(0));
        config.addAttribute("", ENUM.TEST.other());
        config.addAttribute("", ENUM.TEST.same.getName(0));
        config.addAttribute("", 0);
        config.addAttribute("", "" + 0);
        config.addAttribute("", 0 + "");
        verify();
    }

    private static String customValue() {
        return null;
    }

    private static void verify() {
    }

    private DefaultConfiguration customCreateConfig() {
        return new DefaultConfiguration();
    }

    private static DefaultConfiguration createModuleConfig() {
        return new DefaultConfiguration();
    }

    private static class Configuration {
        public void addAttribute(String s, String t) {
        }

        public void addAttribute(String s, int i) {
        }
    }

    private static class DefaultConfiguration extends Configuration {
    }

    private enum ENUM {
        TEST;

        private static final ENUM same = TEST;

        public String other() {
            return name();
        }

        public String getName(int i) {
            return name();
        }
    }
}
