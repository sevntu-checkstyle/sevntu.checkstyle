package com.github.sevntu.checkstyle.checks.design;

public class InputPublicReferenceToPrivateTypeCheck18 {
    private class InnerClass extends Inner implements InnerInterface, AnotherInnerInterface {} 
    private interface InnerInterface {} 
    private interface AnotherInnerInterface {}
    private class Inner {}
    private class InnerClass1 extends InputPublicAccessToPrivateTypeCheck1 
        implements InnerInterface, Comparable {

        @Override
        public int compareTo(Object o)
        {
            return 0;
        }
    }
    public InnerClass getObj(){     //WARNING
        return new InnerClass(); 
    }
    
    public InnerClass1 getObj1() {  //OK
        return new InnerClass1();
    }
    
}