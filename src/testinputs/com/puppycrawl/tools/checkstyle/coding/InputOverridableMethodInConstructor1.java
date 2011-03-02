package com.puppycrawl.tools.checkstyle.coding;

public class InputOverridableMethodInConstructor1 {

    public static void main(String[] args) {

        abstract class Base1 {
            Base1() {
                overrideMe(); // no warnings here
            }

            private void overrideMe() {}

        }

        class Child1 extends Base1 {
            final int x;

            Child1(int x) {
                this.x = x;
            }

            public void overrideMe() {
                System.out.println(x);
            }
        }

        new Child1(999); // prints "0"
    }

}
