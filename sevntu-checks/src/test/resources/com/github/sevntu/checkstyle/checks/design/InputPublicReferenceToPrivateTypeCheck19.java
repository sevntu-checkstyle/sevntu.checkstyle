package com.github.sevntu.checkstyle.checks.design;
import java.util.Map;
public class InputPublicReferenceToPrivateTypeCheck19 {
    public void render(int c, C1 d) {} // WARNING
    
    public int render(C1 c) { return 0; }  //WARNING
    
    protected void someMethod(Map<String, C1> s) {}   //WARNING
    
    private class C1{}
}