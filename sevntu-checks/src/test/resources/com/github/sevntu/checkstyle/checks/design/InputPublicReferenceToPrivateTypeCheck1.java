package com.puppycrawl.tools.checkstyle.design;

public class InputPublicReferenceToPrivateTypeCheck1 {   
    private class PrivateInner{
        private String name="Private inner";
    }   
    public class PublicInner{
        private String name="Public inner";
    }   
    public PublicInner returnPublic(){  //OK
        return new PublicInner();
    }
}