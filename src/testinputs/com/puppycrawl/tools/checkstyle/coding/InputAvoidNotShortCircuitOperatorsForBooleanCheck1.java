package com.puppycrawl.tools.checkstyle.coding;

public class InputAvoidNotShortCircuitOperatorsForBooleanCheck1 {
boolean x,y,z;
boolean result=x|y||z; // !

    public void main(){
        
          boolean res1=x|y||z;
          int res2 = 4|56;
          if(x|y||z){ // !
            int kkk=5;
        }

          x|=x&&y||z; // !
          x=x&y||z; // !

          
    }
    
}
