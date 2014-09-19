package com.github.sevntu.checkstyle.checks.design;

public class InputPublicReferenceToPrivateTypeCheck4 {
    private class PrivateInner implements  Comparable{
        private String name="Private inner";
        
        public int compareTo(Object o) {
            return 0;
        }
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
