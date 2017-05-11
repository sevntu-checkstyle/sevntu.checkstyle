package com.github.sevntu.checkstyle.checks.coding;

public class InputAvoidNotShortCircuitOperatorsForBooleanCheck {
    public static boolean x;
    public boolean y, z;
    boolean result = x|y||z; // !

    public void main(){

          boolean res1 = x|y||z; //
          int res2 = 4|56|8|22;
          if (x|y||z) { //
            int kkk = 5;
          }

          x |= x&&y||z; //
          x = x&y||z; //

          x |= isFalse();
          x = isFalse() | isFalse() & isTrue();

    }
    
    boolean isTrue() {
        return true|false; // !
        //boolean res1=x|y||z; //
    }
    
    public int doSomething() {
        return (5|6); //
    }
    
    boolean isFalse() {
        boolean i = false;
        for (int x = 0; x < 6|i; x |= 5) { // !
            int k = 0;
        }
        int k = 6;
        int y = 6;
        while ((k&y) > 7) { //
            int h = 0;
        }
        return false|true;
    }
    
    boolean isGood() {
        boolean i = true; 
        for(int x = 0; x < 6|i; x |= 5) { // !
            int k = 0;
        }
        boolean k = true;
        boolean y = false;
        while (k&y) { // !
            int h = 0;
        }
        return false;
    }
    
    boolean testAnotherSituations() {
        
        boolean a = !x
                || x()
                || y()
                | (this.z // ! (because true/false is here)
                || InputAvoidNotShortCircuitOperatorsForBooleanCheck.x)
                && true || false;
        
        a = !x
        || x()
        || y()
        | (this.z // ! (because a is already defined boolean variable)
        || InputAvoidNotShortCircuitOperatorsForBooleanCheck.x);

        boolean IsThere=false;

        boolean r = !IsThere
        || x()
        || y()
        | (this.z // ! (because IsThere is a boolean variable)
        || InputAvoidNotShortCircuitOperatorsForBooleanCheck.x);

        boolean new1 = !x
        || x()
        || y()
        | (this.z // 
        || InputAvoidNotShortCircuitOperatorsForBooleanCheck.x);

        return a|a; // !

    }
    
    public void check() {

        boolean f = x|true; // !
        f = x|false; // !
        f = x|y; // !
        f |= this.z; // !

        boolean m = x|z; //
        boolean m1 = x|y; //
        x |= this.z; //
        while (x|y) {} //
        boolean x = true;
        y |= InputAvoidNotShortCircuitOperatorsForBooleanCheck.x; //

        y |= getMessage(x);


    }

    public boolean x() {
        int x;
        return true; }

    public boolean y() {
        return true;
    }

    public boolean getMessage(boolean from) {
        return true;
    }
    
    public void doSomethingElse() {
        //int x;
        while (x | y) {
        }
    }

    public void invoker()
    {
        boolean x = this.y | someMethod(y | z);
    }
    
    public Object someConstructor()
    {
        return new MyConstructor(x | y);
    }

    public boolean someMethod(boolean value)
    {
        return !value;
    }
    
    public void multiCatch()
    {
        try
        {
          
        }
        catch (NullPointerException | IndexOutOfBoundsException ex)
        {
          
        } 
    }
}

class MyConstructor
{
    MyConstructor(boolean expr)
    {
        boolean x = InputAvoidNotShortCircuitOperatorsForBooleanCheck.x | InputAvoidNotShortCircuitOperatorsForBooleanCheck.x;
    }

    public void test() {
        new Runnable() {
            @Override
            public void run() {
            }
            public boolean test() {
                try {
                } catch (IllegalArgumentException | NullPointerException e) {
                    return false;
                }
                return true;
            }
        };
    }
}
