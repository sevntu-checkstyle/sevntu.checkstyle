package com.github.sevntu.checkstyle.checks.coding;

public class InputForbidThrowAnonymousExceptionsCheckAnotherClassName {
    
    ExceptionBlablabla someException = new ExceptionBlablabla() {};
    
    private void anonException() throws ExceptionBlablabla {
        try {
            int k = 1 / 0;
            throw someException;
        } catch (ExceptionBlablabla e) {
            throw someException;
        }
    }
    
    
    private class ExceptionBlablabla extends Exception {
        
    }
}
