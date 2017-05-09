package com.github.sevntu.checkstyle.checks.coding;

public class InputMoveVariableInsideIfCheckViolations {
    public void method1() {
        String variable = "";

        if (true) {
            variable = variable.substring(0);
        }
    }

    public void method2() {
        String variable = "";

        if (true) {
            variable = variable.substring(0);
        }
        else {
            method1();
        }
    }

    public void method3() {
        String variable = "";

        if (true)
            variable = variable.substring(0);
        else
            method1();
    }

    public void method4() {
        String variable = "";

        if (true) {
            method1();
        }
        else {
            variable = variable.substring(0);
        }
    }

    public void method5() {
        String variable = "";

        if (true)
            method1();
        else
            variable = variable.substring(0);
    }
}
