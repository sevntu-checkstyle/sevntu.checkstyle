package com.github.sevntu.checkstyle.checks.coding;

import com.puppycrawl.tools.checkstyle.api.LocalizedMessage;

import static java.util.Locale.ROOT;
import static java.lang.Integer.MAX_VALUE;

public class InputForbidFieldAccessCheckStaticTest {

    public static void setLocaleToRoot() {
        LocalizedMessage.setLocale(ROOT);
    }

    public static boolean isInRange(int x) {
        return x < MAX_VALUE;
    }

}
