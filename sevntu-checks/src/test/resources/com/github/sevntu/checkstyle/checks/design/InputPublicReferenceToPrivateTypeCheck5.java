package com.github.sevntu.checkstyle.checks.design;

public class InputPublicReferenceToPrivateTypeCheck5 {
    private class PrivateInner{
        private String name="Private inner";
    }   
    public class PublicInner{
        private String name="Public inner";
    }   
    public PublicInner returnPublic(){
        return new PublicInner();
    }
    public PrivateInner[] returnPrivate(){  //WARNING
        return new PrivateInner[]{new PrivateInner()};
    }   
}
