package project;

import File;
//import java.io.File;

public class InputForbidInstantiationCheckWithoutDots
{        
    public void method() {
        NullPointerException ex = new NullPointerException("message"); // !
        int []x = new int[10];
        new InputForbidInstantiationCheck();
        NullPointerException ex2 = new java.lang.NullPointerException("message"); // !
        File File = new File();
        File File = new java.io.File(); // 2 !
        String a = new String(); // 2 !
        InputForbidInstantiationCheck c = new InputForbidInstantiationCheck();
    }

}