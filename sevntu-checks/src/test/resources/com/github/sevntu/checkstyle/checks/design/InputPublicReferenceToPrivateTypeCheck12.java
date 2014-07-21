package com.github.sevntu.checkstyle.checks.design;

import java.util.*;

public class InputPublicReferenceToPrivateTypeCheck12 {
    private interface PrivateInner{
        public String name = "Private inner";
    }   
    public interface PublicInner{
        public String name="Public inner";
    }   
    
    public Set<? super PrivateInner> returnPrivate(){   //WARNING
        return new HashSet<PrivateInner>();
    }
    protected Set<? extends PublicInner> returnPublic(){  //OK
        return new HashSet<PublicInner>();
    }
    public Set<PrivateInner> returnPrivate5(){  //WARNING
        return new TreeSet<PrivateInner>();
    }
}
