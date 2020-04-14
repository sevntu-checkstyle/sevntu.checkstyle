package com.github.sevntu.checkstyle.checks.coding;

import com.puppycrawl.tools.checkstyle.api.LocalizedMessage;

import java.util.Locale;
import java.lang.Integer;

public class InputForbidFieldAccessCheckTest {
    private final static String ROOT = "ROOT";

    public static void setLocaleToRoot() {
        LocalizedMessage.setLocale(Locale.ROOT);
        System.out.println(ROOT);
    }

    public static boolean isInRange(int x) {
        return x < Integer.MAX_VALUE;
    }

}
