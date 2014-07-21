package com.github.sevntu.checkstyle.checks.design;

import java.util.*;
public class InputPublicReferenceToPrivateTypeCheck11 {
    private class PrivateInner{
        private String name="Private inner";
    }   
    public class PublicInner{
        private String name="Public inner";
    }   
    public PublicInner returnPublic(){  //OK
        return new PublicInner();
    }
    public Set<? super PrivateInner> returnPrivate1(){  //WARNING
        return new HashSet<PrivateInner>();
    }
    private Set<PrivateInner> returnPrivate2(){ //OK
        return new HashSet<PrivateInner>();
    }
    protected Set<? extends PrivateInner> returnPrivate3(){ //WARNING
        return new HashSet<PrivateInner>();
    }
    Set<TreeSet<TreeMap<String, PrivateInner>>> returnPrivate4(){   //WARNING
        return new TreeSet<TreeSet<TreeMap<String, PrivateInner>>>();
    }
}
