package com.github.sevntu.checkstyle.checks.coding;

public class InputOverridableMethodInConstructorCheck15 {

    public static void main(String[] args) {

        abstract class Base1 {
             Base1() {
                System.out.println("Base C-tor ");
                overrideMe(5); // no warnings here
                overrideMe("bla"); // no warnings here
                overrideMe('6'); // no warnings here
                this.toString();
            }

            public void overrideMe() { // !
                System.out.println("Base overrideMe() ");
            }
            
            public void overrideMe(int x) { // !
                System.out.println("Base overrideMe(int x) ");
            }

            private void overrideMe(String str) {
                System.out.println("Base overrideMe(String str) ");
            }
            
            private void overrideMe(char ch) { // !
                System.out.println("Base overrideMe(String str) ");
                overrideMe();
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
