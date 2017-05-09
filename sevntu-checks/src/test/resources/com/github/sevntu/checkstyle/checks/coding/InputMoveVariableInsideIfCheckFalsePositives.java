package com.github.sevntu.checkstyle.checks.coding;

import java.util.List;

public class InputMoveVariableInsideIfCheckFalsePositives {
    private List<String> field;

    public void method1(List<String> list) {
        final String variable = list.remove(0);
        final String next = list.get(0);

        if (next.equals("test")) {
            list.add(variable);
        }
    }

    public void method2(List<String> list) {
        final String variable = list.remove(0);

        if (true) {
            test(list.get(0));
        }

        if (true) {
            list.add(variable);
        }
    }

    public void method3(List<String> list) {
        final String variable = field.get(0);

        modifyField();

        if (true) {
            field.add(variable);
        }
    }

    private void test(String string) {
    }

    private void modifyField() {
        test(field.remove(0));
    }
}
