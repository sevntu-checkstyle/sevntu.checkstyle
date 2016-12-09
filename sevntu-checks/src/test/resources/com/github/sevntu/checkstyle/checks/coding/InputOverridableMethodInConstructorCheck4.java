package com.github.sevntu.checkstyle.checks.coding;

public class InputOverridableMethodInConstructorCheck4 {

    public static void main(String[] args) {

        abstract class Base {
            Base() {
                System.out.println();
            	this.overrideMe(); // a warning here
                System.out.checkError();
                this.toString();
            }

            private void overrideMe() {
                overrideMe2();
            }

            void overrideMe2() {
            }
            // int overrideMe2() {}
            // public void overrideMe2() {}
            // protected void overrideMe2() {}
            // abstract void overrideMe2();
            // abstract public void overrideMe2();
            // abstract protected void overrideMe2();

        }

        class Child extends Base {
            final int x;

            Child(int x) {
                this.x = x;
            }

            public void overrideMe() {
                System.out.println(x);
            }
            
            public void overrideMe2() {
                System.out.println(x);
            }
        }

        new Child(999); // prints "0"
    }

}
