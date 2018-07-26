package com.github.sevntu.checkstyle.checks.coding;

import java.util.Comparator;

public class InputForbidThrowAnonymousExceptionsCheck3 {
    protected RuntimeException exception = new RuntimeException() {};
    public void anonymousEx() {
        try {
            //some code
            int k = 4;
        } catch (Exception e) {
              throw new RuntimeException() { //anonymous exception declaration
                   //some code
              };
         } 
    }
    
    public void notAnonEx() {
        try {
            //some code
            RuntimeException exception = new RuntimeException() {};
        } catch (Exception e) {
            throw exception;
        }
    }
    
    
    public void anonEx2() {
        RuntimeException exception = new RuntimeException();
        try {
            //some code
            String re = "lol";
        } catch (Exception e) {
            throw exception;
        }
    }
    
    public void notAnonEx3() {
        RuntimeException someException = new RuntimeException();
        try {
            //some code
            String someString = "lol";
        } catch (Exception e) {
            throw someException;
        }
    }
    
    public void anonymousEx4() {

        Comparator<String> c = new Comparator<String>()
        {
            
            public int compare(String o1, String o2)
            {
                RuntimeException runtimeException = new RuntimeException() { //anonymous exception declaration
                    //some code
               };
                return 0;
            }
        };
        
          RuntimeException runtimeException = new RuntimeException() { //anonymous exception declaration
               //some code
          };
          
        throw runtimeException; 
    }
    
    public void anonymousEx5() {
        
        RuntimeException runtimeException = new RuntimeException() { //anonymous exception declaration
            //some code
        };

        Comparator<String> c = new Comparator<String>()
        {
            
            public int compare(String o1, String o2)
            {
                RuntimeException runtimeException = new RuntimeException() { //anonymous exception declaration
                    //some code
               };
                return 0;
            }
        };
        
          
        throw runtimeException; 
    }
    
    public void anonymousEx6() {

        Comparator<String> c = new Comparator<String>()
        {
            
            public int compare(String o1, String o2)
            {
               RuntimeException runtimeException = new RuntimeException();
                return 0;
            }
        };
        
          RuntimeException runtimeException = new RuntimeException() { //anonymous exception declaration
               //some code
          };
          
        throw runtimeException; 
    }
    
    public void anonymousEx7() {
        
        RuntimeException runtimeException1 = new RuntimeException() { //anonymous exception declaration
            //some code
        };

        Comparator<String> c = new Comparator<String>()
        {
            
            public int compare(String o1, String o2)
            {
                RuntimeException runtimeException = new RuntimeException();
                return 0;
            }
        };
        
          
        throw runtimeException1; 
    }
    
    private static final long serialVersionUID = 1L;
    
}
