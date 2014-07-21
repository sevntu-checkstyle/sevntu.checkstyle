
package com.github.sevntu.checkstyle.checks.design;
import java.util.*;

public class InputPublicReferenceToPrivateTypeCheck8 {
    private class PrivateInner{
        private String name="Private inner";
    }
    protected class ProtectedInner{
        private String name="Protected inner";
    }
    class DefInner{
        
    }
    public class PublicInner{
        private String name="Public inner";
    }   
    
    public PublicInner returnPublic(){  //OK
        return new PublicInner();
    }   
    public DefInner ReturnedDefault(){  //OK
        return new DefInner();
    }
    public Set<TreeSet<HashMap<String, PrivateInner>>> returnPrivate(){ //WARNING
        return new HashSet<TreeSet<HashMap<String, PrivateInner>>>();
    }   
    public OutClass returnedOut(){  //WARNING
        return new OutClass();
    }
    public List<ProtectedInner> returnedProtected(){    //OK
        return (List<ProtectedInner>) new ProtectedInner();
    }
    private class OutClass{

        public OutClass()
        {
            // TODO Auto-generated constructor stub
        }
    }
    
}
