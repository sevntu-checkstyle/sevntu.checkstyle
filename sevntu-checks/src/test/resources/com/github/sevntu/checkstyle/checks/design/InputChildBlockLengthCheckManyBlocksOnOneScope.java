package com.github.sevntu.checkstyle.checks.design;

public class InputChildBlockLengthCheckManyBlocksOnOneScope {

    public static void main(String[] args)
    {
        
        { // braces without any operator
            
          int number = 0;
          
          if (isTrue()) { // 26 lines
              number = 2;
              
              if (isTrue()) { // 13 lines = 50% !
                                  
                  
                  
                  number = 3;
                  
                  
                  
                  
                  
                  
                  
                  
                  
              }
              
              if (isTrue()) { // 5 lines = 19,23% !
                          
                  
                  number = 3;  
                  
                  
              }
              
          }                 
          
        }

    }

    public void test() {
        int number;

        if (isTrue()) {
            if (isTrue())
                number = 3;
            else
                number = 3;
        }
    }

    public static boolean isTrue() {
        return Boolean.TRUE;
    }

}
