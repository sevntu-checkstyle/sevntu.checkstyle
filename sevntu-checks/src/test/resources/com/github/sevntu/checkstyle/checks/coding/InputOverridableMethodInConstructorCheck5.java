package com.github.sevntu.checkstyle.checks.coding;

public class InputOverridableMethodInConstructorCheck5 {

    public static void main(String[] args) {

        abstract class Base {
            Base() {
                System.out.println();
            	this.overrideMe(); // a warning here
            	overrideMe(); // a second warning here
                System.out.checkError();
                this.toString();
            }

            private void overrideMe() {
                overrideMe2();
            }

            private void overrideMe2() {            
            	overrideMe3();
            }

            void overrideMe3() {            
            	System.out.checkError();
            	this.toString();
            	overrideMe2();            	
            }
            // int overrideMe3() {}
            // public void overrideMe3() {}
            // protected void overrideMe3() {}
            // abstract void overrideMe3();
            // abstract public void overrideMe3();
            // abstract protected void overrideMe3();
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
            
            void overrideMe3() {            
            	System.out.checkError();
            	this.toString();
            	overrideMe2();            	
            }            
            
            
        }

        new Child(999); // prints "0"
    }

}
