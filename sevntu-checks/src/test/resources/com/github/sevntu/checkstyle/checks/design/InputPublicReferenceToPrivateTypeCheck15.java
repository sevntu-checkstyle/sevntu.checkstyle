package com.github.sevntu.checkstyle.checks.design;

public class InputPublicReferenceToPrivateTypeCheck15 {
    public Inner.Inner1 getInner1() {   //WARNING
        return new Inner().returnPrivate();
        
    }
    
    public Inner.Inner2.Inner3 getInner3() {    //WARNING
        return new Inner.Inner2().returnPrivate();
        
    }
    
    public Inner getInner() {   //WARNING
        return new Inner().returnInner();
    }
    
    private static class Inner {
        public Inner returnInner() {
            return new Inner();
        }
        private static class Inner1 {
            static {
                System.out.println("!!!");
            }
        }
        public Inner1 returnPrivate(){     //OK
            return new Inner1();
        }
        private static class Inner2 {
            private static class Inner3 {
                static {
                    System.out.println("!!!");
                }
            }
            public Inner3 returnPrivate(){    //OK
                return new Inner3();
            }
        }
    }
}
