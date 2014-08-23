package com.github.sevntu.checkstyle.checks.design;
//there are few nested operators in this class
public class InputChildBlockLengthCheckNested {

    public static void main(String[] args)
    {
        
        { // braces without any operator
            
          int number = 0;
          
          if (isTrue()) { // simple if with braces if[12x10]
              number = 2;
              
              if (isTrue()) { // nested if if[15x14]
                  number = 3;
              }
          }
                    
          if (isTrue()) { // 3: if-else if[20x10]
              number = 3; 
              if (isTrue()) { // nested if 1 if[22x14]
                  number = 3; 
              }
          } else { // else[25x12]
              number = 4; 
              if (isTrue()) { // nested if 2 if[27x14]
                  number = 3; 
              }
          }
                 
          if (isTrue()) { // 4: if-else-if if[32x10]
              number = 5;
              if (isTrue()) { // nested if 1 if[34x14]
                  number = 3; 
              }
          } else if(isTrue()) { // else[37x12]
              number = 6;
              if (isTrue()) { // nested if 2 if[37x17]
                  number = 3; 
              }
          }

          for(int i=3; i<5; i++){ // 6: simple for with braces for[44x10]
              number = 8;
              if (isTrue()) { // nested if if[46x14]
                  number = 3; 
              }
          }     
                  
          
          while(isTrue()) { // 8: simple while with braces while[52x10]
              number = 10;
              if (isTrue()) { // nested if if[54x14]
                  number = 3; 
              }
          }
     
          do { // 9: do-while do[59x10]
              number = 11;    
              if (isTrue()) { // nested if if[61x14]
                  number = 3; 
              }
          } while(isTrue());

          switch(number) { // 10: switch switch[66x10]
          case 1:
              number = 12;
              if (isTrue()) { // nested if if[69x14]
                  number = 3; 
              }
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

        }

    }

    public static boolean isTrue() {
        return Boolean.TRUE;
    }

}
