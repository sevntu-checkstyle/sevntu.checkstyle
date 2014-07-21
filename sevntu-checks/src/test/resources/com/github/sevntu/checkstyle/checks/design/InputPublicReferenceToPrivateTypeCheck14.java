package com.github.sevntu.checkstyle.checks.design;

import java.util.*;

public class InputPublicReferenceToPrivateTypeCheck14 {
    private class PrivateInner{
        private String name="Private inner";
    }   
    public class PublicInner{
        private String name="Public inner";
    }   
    public PublicInner returnPublic(){  //OK
        return new PublicInner();
    }
    public PrivateInner[] returnPrivate1(){ //WARNING
        return new PrivateInner[]{new PrivateInner()};
    } 
    private interface PrivateInner1{
        public String name = "Private inner";
    }   
    public interface PublicInner1{
        public String name="Public inner";
    }   
    
    public Set<? super PrivateInner1> returnPrivate(){  //WARNING
        return new HashSet<PrivateInner1>();
    }
    protected Set<? extends PublicInner1> returnPrivate3(){ //OK
        return new HashSet<PublicInner1>();
    }
    public Set<PrivateInner1> returnPrivate5(){ //WARNING
        return new TreeSet<PrivateInner1>();
    }
    private enum First {One, Two}
    First a = First.One;    //WARNING
    
    protected First returnPrivate2() {  //WARNING
        return a;
    }
}
