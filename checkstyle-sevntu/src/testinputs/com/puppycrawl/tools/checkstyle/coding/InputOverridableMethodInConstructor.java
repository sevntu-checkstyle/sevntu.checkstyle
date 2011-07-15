package com.puppycrawl.tools.checkstyle.coding;

public class InputOverridableMethodInConstructor {

    public static void main(String[] args) {

        abstract class Base {
            Base() {
                this.overrideMe(); // a warning here
                System.out.checkError();
                this.toString();
            }

            int overrideMe() {
                return 1;
            }
            // void overrideMe() {}
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

            public int overrideMe() {
                System.out.println(x);
                return 6;
            }
        }

        new Child(999); // prints "0"
    }

}
