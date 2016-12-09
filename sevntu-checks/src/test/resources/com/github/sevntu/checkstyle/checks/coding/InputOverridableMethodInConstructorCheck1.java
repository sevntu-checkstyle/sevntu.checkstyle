package com.github.sevntu.checkstyle.checks.coding;

public class InputOverridableMethodInConstructorCheck1 {

    public static void main(String[] args) {

        abstract class Base1 {
             Base1() {
                System.out.println("Base C-tor ");
                overrideMe(); // no warnings here
            }

            private void overrideMe() {
                System.out.println("Base overrideMe() ");
            }

        }

        class Child1 extends Base1 {
            final int x;

            Child1(int x) {
                this.x = x;
            }

            public void overrideMe() {
                System.out.println("child`s overrideMe(): "+x);
            }
        }

      new Child1(999); // will not print x =)
    }

}
