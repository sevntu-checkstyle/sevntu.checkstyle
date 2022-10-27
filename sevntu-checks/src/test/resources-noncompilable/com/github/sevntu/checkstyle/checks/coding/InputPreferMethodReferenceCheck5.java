package com.github.sevntu.checkstyle.checks.coding;

public class InputPreferMethodReferenceCheck5 {
    public void test() {
        var x = "foo";
        var y = switch (x) {
            case "foo" -> "foo2";
            default -> null;
        };
        System.out.println(y);
    }
}
