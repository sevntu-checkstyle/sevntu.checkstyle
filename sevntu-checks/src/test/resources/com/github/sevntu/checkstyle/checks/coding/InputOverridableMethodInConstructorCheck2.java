package com.github.sevntu.checkstyle.checks.coding;

public class InputOverridableMethodInConstructorCheck2 {

    public static void main(String[] args) {

        class Base2 {
            Base2() {
                System.out.println("Base2 C-tor.");
                overrideMe(); // warning here!!
                System.out.checkError();
                this.toString();
            }

            void overrideMe() {
                System.out.println("Base overrideMe().");
            }
        }

        class Child2 extends Base2 {
            final int x;

            Child2(int x) {
                this.x = x;
                System.out.println("Child2 C-tor.");
            }

            public void overrideMe() {
                System.out.println("Child2 overrideMe(): "+x);
            }
        }

        class Child2_2 extends Child2 {
            final int x;

            Child2_2(int x) {
                super(100);                
                System.out.println("Child2_2 C-tor.");
                this.x = x;
                //overrideMe();
            }

            public void overrideMe() {
                System.out.println("Child2_2 overrideMe(): "+x);
            }
        }

        new Child2_2(999); // will not print anything
    }

}
