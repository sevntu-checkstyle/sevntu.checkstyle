package com.github.sevntu.checkstyle.checks.design;

public class InputPublicReferenceToPrivateTypeCheck16 {
    public Inner.Inner2.Inner3 getLopByAnonymous() { //WARNING
        return (com.github.sevntu.checkstyle.checks.design.InputPublicReferenceToPrivateTypeCheck16.Inner.Inner2.Inner3)
        		new Inner.Inner2.Inner3().data;
    }
    
    private static class Inner {
        
        private static class Inner2 {
            
            private static class Inner3 {
                
                static {
                    System.out.println("!!!");
                }
                
                public  Object data = new Inner2() {
                    public Inner2.Inner3 ret() {    //OK
                        return new Inner2.Inner3();
                    }
                };
            }
        }
    }
}