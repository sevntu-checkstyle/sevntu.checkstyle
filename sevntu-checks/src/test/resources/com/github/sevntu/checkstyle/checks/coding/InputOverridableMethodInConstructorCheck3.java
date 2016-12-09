package com.github.sevntu.checkstyle.checks.coding;

public class InputOverridableMethodInConstructorCheck3 {

    public static void main(String[] args) {

        class Base3 {
            private Base3() {
                System.out.println("Base3 first C-tor.");
                overrideMe(); // warning
                Base3.this.overrideMe(); // warning
            }

            private Base3(int x) {
                System.out.println("Base3 second C-tor.");
                overrideMe(); // warning
            }

            public void overrideMe() {
                System.out.println("Base3 overrideMe().");
            }
        }

        class Child3 extends Base3 {
            final int x;

            Child3(int x) {
                this.x = x;
                System.out.println("Child3 C-tor.");
            }

            public void overrideMe() {
                System.out.println("Child3 overrideMe(): "+x);
            }
        }

        new Child3(999); // will print "0"
    }

}
