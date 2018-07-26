package com.github.sevntu.checkstyle.checks.design;

import java.io.File;

import org.junit.Test;

public class InputCheckstyleTestMakeupCheckValid {
    @Test
    public void method1() {
        final Configuration config = null;
        verify();
    }

    @Test
    public void method2() {
        final Configuration config = createModuleConfig();
        verify();
    }

    @Test
    public void method3() {
        final DefaultConfiguration config = createRootConfig();
        verifyWarns();
    }

    @Test
    public void method4() {
        final Configuration config = createModuleConfig();
        config.addAttribute("", null);
        config.addAttribute("", "");
        config.addAttribute("", "" + "");
        final File file = new File("");
        config.addAttribute("", file.getPath());
        config.addAttribute("", ENUM.TEST.toString());
        config.addAttribute("", ENUM.TEST.getName());
        config.addAttribute("", ENUM.TEST.name());
        config.addAttribute("", getPath(""));
        config.addAttribute("", getNonCompilablePath(""));
        config.addAttribute("", getUriString(""));
        config.addAttribute("", getResourcePath(""));
        verify();
    }

    @Test
    public void method5() {
        // simulation of PowerMockito.mock(DefaultConfiguration.class);
        DefaultConfiguration config = InputCheckstyleTestMakeupCheckValid.createModuleConfig();
        verify();
    }

    private String getPath(String s) {
        return s;
    }

    private String getNonCompilablePath(String s) {
        return s;
    }

    private String getUriString(String s) {
        return s;
    }

    private String getResourcePath(String s) {
        return s;
    }

    private static void verify() {
    }

    private static void verifyWarns() {
    }

    private static void verifySuppressed() {
    }

    private static DefaultConfiguration createModuleConfig() {
        return new DefaultConfiguration();
    }

    private static DefaultConfiguration createRootConfig() {
        return new DefaultConfiguration();
    }

    private static class Configuration {
        public void addAttribute(String s, String t) {
        }
    }

    private static class DefaultConfiguration extends Configuration {
    }

    private enum ENUM {
        TEST;

        public String getName() {
            return name();
        }
    }
}
