package com.github.sevntu.checkstyle.checks.design;

import java.util.*;
public class InputPublicReferenceToPrivateTypeCheck6 {
    private class PrivateInner{
        private String name="Private inner";
    }   
    public class PublicInner{
        private String name="Public inner";
    }   
    public PublicInner returnPublic(){  //OK
        return new PublicInner();
    }
    public Set<PrivateInner>returnPrivate(){    //WARNING
        return new HashSet<PrivateInner>();
    }
}
