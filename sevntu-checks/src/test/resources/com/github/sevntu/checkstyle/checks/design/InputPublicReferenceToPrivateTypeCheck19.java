package com.github.sevntu.checkstyle.checks.design;

public class InputPublicReferenceToPrivateTypeCheck19 {
    public void render(int c, C1 d) {} // WARNING
    
    public int render(C1 c) {}  //WARNING
    
    protected void someMethod(List<String, C1> s) {}   //WARNING
    
    private class C1{}
}