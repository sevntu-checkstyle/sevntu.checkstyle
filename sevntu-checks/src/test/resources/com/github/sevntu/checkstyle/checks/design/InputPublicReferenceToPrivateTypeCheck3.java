package com.github.sevntu.checkstyle.checks.design;

public class InputPublicReferenceToPrivateTypeCheck3 {
    private class PrivateInner extends PublicInner{
        private String name="Private inner";
    }   
    public class PublicInner{
        private String name="Public inner";
    }   
    public PublicInner returnPublic(){  //OK
        return new PublicInner();
    }
    public PrivateInner returnPrivate(){    //OK
        return new PrivateInner();
    }

}
