package com.github.sevntu.checkstyle.checks.design;

public class InputPublicReferenceToPrivateTypeCheck17 {
    public Inner innerFromMain = new Inner();   //WARNING
    
    private class Inner {
        public Inner lol = new Inner();
    }
    public Inner1 innerFromMain1 = new Inner1();    //WARNING
    
    private class Inner1 {
        
    }
    
    private Inner1 privateInner = new Inner1();     //OK
    
}