package com.github.sevntu.checkstyle.checks.coding;

import java.io.File;

public class InputForbidInstantiationCheckWithoutDots
{        
    public void method() {
        NullPointerException ex = new NullPointerException("message"); // !
        int []x = new int[10];
        new InputForbidInstantiationCheck();
        NullPointerException ex2 = new java.lang.NullPointerException("message"); // !
        com.github.sevntu.checkstyle.checks.test.utils.File File = new com.github.sevntu.checkstyle.checks.test.utils.File(""); // !
        File File1 = new java.io.File(""); // 2 !
        String a = new String(); // 2 !
        InputForbidInstantiationCheck c = new InputForbidInstantiationCheck();
    }

}