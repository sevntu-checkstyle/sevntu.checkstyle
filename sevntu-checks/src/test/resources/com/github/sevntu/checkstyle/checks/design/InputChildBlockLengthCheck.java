package com.github.sevntu.checkstyle.checks.design;
// there are not any nested operators in this class
public class InputChildBlockLengthCheck {

    public static void main(String[] args)
    {
        
        { // braces without any operator
            
          int number = 0;
            
          if (isTrue()) number = 1; // 1: simple if
          
          if (isTrue()) { // 2: simple if with braces
              number = 2;
          }

          if (isTrue()) { // 3: if-else     
              number = 3; 
          } else {
              number = 4; 
          }
                 
          if (isTrue()) { // 4: if-else-if
              number = 5;
          } else if(isTrue()) {
              number = 6;
          }
          
          for(int i=3; i<5; i++) number = 7; // 5: simple for

          for(int i=3; i<5; i++){ // 6: simple for with braces
              number = 8;
          }     
          
          while(isTrue()) number = 9; // 7: simple while
          
          
          while(isTrue()) { // 8: simple while with braces
              number = 10;
          }
     
          do { // 9: do-while
              number = 11;         
          } while(isTrue());

          switch(number) { // 10: switch
          case 1:
              number = 12;
              break;
          case 2:
              number = 13;
              break;
          case 3:
              number = 14;
              break;
          default:
              number = 15;
              break;          
          }

          try {
          } 
          catch (Exception e) {
          }
          finally
          {
          }

          if (isTrue()) {}
        }

    }
    
    public static boolean isTrue() {
        return Boolean.TRUE;
    }

}
