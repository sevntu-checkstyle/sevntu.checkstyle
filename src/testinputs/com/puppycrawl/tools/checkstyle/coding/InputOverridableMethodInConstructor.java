package com.puppycrawl.tools.checkstyle.coding;

public class InputOverridableMethodInConstructor {

    public static void main(String[] args) {
        
        abstract class Base {
            Base() {
                overrideMe(); // a warning here
            }

           void overrideMe() {}
           // public void overrideMe() {}
           // protected void overrideMe() {}
           // abstract void overrideMe();
           // abstract public void overrideMe();
           // abstract protected void overrideMe();
           
        }
        
        class Child extends Base {
            final int x;

            Child(int x) {
                this.x = x;
            }

            public void overrideMe() {
                System.out.println(x);
            }
        }
        
        new Child(999); // prints "0"
    }

}





