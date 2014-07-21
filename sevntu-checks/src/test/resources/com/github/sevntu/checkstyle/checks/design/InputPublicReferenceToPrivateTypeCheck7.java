package com.github.sevntu.checkstyle.checks.design;

import java.util.*;
public class InputPublicReferenceToPrivateTypeCheck7 {
    private class PrivateInner{
        private String name="Private inner";
    }   
    public class PublicInner{
        private String name="Public inner";
    }   
    public PublicInner returnPublic(){  //OK
        return new PublicInner();
    }
    public Set<TreeSet<PrivateInner>>returnPrivate(){   //WARNING
        return new HashSet<TreeSet<PrivateInner>>();
    }
}
