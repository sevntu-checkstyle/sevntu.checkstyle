package com.github.sevntu.checkstyle.checks.coding;
public class InputForbidThrowAnonymousExceptionsCheck2 {
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
            Object o = null;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }
    
    
    public void anonEx2() {
        RuntimeException run = new RuntimeException() {};
        try {
            //some code
            double s = 3.14;
        } catch (Exception e) {
            throw run;
        }
    }
    
    public void notAnonEx3() {
        RuntimeException run = new RuntimeException();
        try {
            //some code
            Object re = null;
        } catch (Exception e) {
            throw run;
        }
    }
    
}
