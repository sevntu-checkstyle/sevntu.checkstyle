package com.github.sevntu.checkstyle.checks.coding;

public class InputAvoidNotShortCircuitOperatorsForBooleanCheck1 {
boolean x,y,z;
boolean result=x|y||z; // !

    public void main(){

          boolean res1=x|y||z; // !
          int res2 = 4|56;
          if(x|y||z){ // !
            int kkk=5;
        }

          x|=x&&y||z; //
          x=x&y||z; //

          x |= isFalse();
          x = isFalse() | isFalse() & isTrue();

    }
    
    boolean isTrue() {
        return true|false; // !
    }
    
    public int doSomething() {
        return (5|6); //
    }
    
    boolean isFalse() {
        boolean i=false;
        for(int x=0; x<6|i; x|=5){ // !
            int k=0;
        }
        int k=6;
        int y=6;
        while((k&y) > 7){ //
            int h=0;
        }
        return false;
    }
    
    boolean isGood() {
        boolean i=true; {
        for(int x=0; x<6|i; x|=5){ //
            int k=0;
        }
        boolean k=true;
        boolean y=false;
        while(k&y){ // !
            int h=0;
        }
        return false;
    }}
      
}
