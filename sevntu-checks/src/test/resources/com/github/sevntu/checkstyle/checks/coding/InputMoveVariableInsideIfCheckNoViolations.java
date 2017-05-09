package com.github.sevntu.checkstyle.checks.coding;

import java.io.BufferedReader;
import java.io.FileReader;

public class InputMoveVariableInsideIfCheckNoViolations {
    String field = "";

    public void method1() {
    }

    public void method2() {
        String nothing = "";
    }

    public void method3() {
        for (int i = 0; i < 5; i++) {
            method1();

            if (true) {
                i++;
            }
        }

        try (BufferedReader in = new BufferedReader(new FileReader("foo.in"))) {
            method1();

            if (true) {
                in.readLine();
            }
        }
        catch (Exception e) {
            method1();

            if (true) {
                e.getCause();
            }
        }
    }

    public void method4() {
        String variable = "";

        variable = variable.substring(0);
    }

    public void method5() {
        String variable = "";

        variable = variable.substring(0);

        if (true) {
            variable = variable.substring(0);
        }
    }

    public void method6() {
        String variable = "";

        if (variable.contains("a")) {
            method1();
        }
    }

    public void method7() {
        String variable = "";

        if (true) {
            variable = variable.substring(0);
        }
        else {
            variable = variable.substring(0);
        }
    }

    public void method8() {
        String variable = "";

        if (true) {
            method1();
        }
        else if (true) {
            variable = variable.substring(0);
        }
        else {
            variable = variable.substring(0);
        }
    }

    public void method9() {
        String variable = "";

        if (true) {
            variable = variable.substring(0);
        }

        if (true) {
            variable = variable.substring(0);
        }
    }

    public void method10() {
        String variable = "";

        for (int i = 0; i < 10; i++) {
            variable = variable.substring(0);
        }
    }

    public void method11() {
        String variable = "";

        for (int i = 0; i < 10; i++) {
            if (true) {
                variable = variable.substring(0);
            }
        }
    }
}
