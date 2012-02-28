package project;

public class InputForbidInstantiationCheck
{        
    public void method() {
        NullPointerException ex = new NullPointerException("message");
        int []x = new int[10];
        new InputForbidInstantiationCheck();
    }

}