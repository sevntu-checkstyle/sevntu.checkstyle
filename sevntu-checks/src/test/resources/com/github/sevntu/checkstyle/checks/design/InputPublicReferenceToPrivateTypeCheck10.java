package com.github.sevntu.checkstyle.checks.design;

import java.util.*;
public class InputPublicReferenceToPrivateTypeCheck10 {
    private class PrivateInner{
        private String name="Private inner";
    }   
    public class PublicInner{
        private String name="Public inner";
    }   
    public PublicInner returnPublic(){  //OK
        return new PublicInner();
    }
    public Set<TreeSet<TreeMap<String, PrivateInner>>> returnPrivate1(){    //WARNING
        return new HashSet<TreeSet<TreeMap<String, PrivateInner>>>();
    }
    private Set<PrivateInner> returnPrivate2(){ //OK
        return new HashSet<PrivateInner>();
    }
    protected Set<TreeSet<HashMap<String, PrivateInner>>> returnPrivate3(){ //WARNING
        return new HashSet<TreeSet<HashMap<String, PrivateInner>>>();
    }
    Set<TreeSet<HashMap<String, PrivateInner[][]>>> returnPrivate4(){   //WARNING
        return new HashSet<TreeSet<HashMap<String, PrivateInner[][]>>>();
    }
}
