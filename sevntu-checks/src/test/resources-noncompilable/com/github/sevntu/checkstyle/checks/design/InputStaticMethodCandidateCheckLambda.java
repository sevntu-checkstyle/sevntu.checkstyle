package com.github.sevntu.checkstyle.checks.design;

import javax.swing.JButton;

/**
 * Compilable with Java 8. 
 */
public class InputStaticMethodCandidateCheckLambda {
    public void getEntityCallbackWithLambdas() {
        JButton testButton = new JButton("Test Button");
        testButton.addActionListener((e) -> System.out.println(""));
    }
}
