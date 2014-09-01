package com.github.sevntu.checkstyle.checks.coding;
public class InputReturnCountExtendedCheckCtors
{

    public int a = 0;
    
    // one "return"
    public InputReturnCountExtendedCheckCtors() {
        return;
    }

    // one "return"
    public InputReturnCountExtendedCheckCtors(int x) {
        for (int i = 0; i < x; i++) {
            System.out.println();
            int a = 1;
            if (a != 2 && true) {
                if (true | false) {
                    if (a - a != 0) {
                        a += 1;
                    }
                }
            }
        }
        return;
    }

    // two "returns"
    public InputReturnCountExtendedCheckCtors(int x, String s) {
        System.out.println();
        int a = x + s.length();
        if (a != 4) {
            return;
        }
        else {
            System.out.println(a);
            return;
        }
    }

    // three "returns"
    public InputReturnCountExtendedCheckCtors(String z, double y) {
        System.out.println();
        int a = Integer.parseInt(z);
        y+=0.6;
        if (a != 4)
        {
            return;
        }
        else
        {
            if (a - 1 != 2) {
                this.a = 0;
                return;
            }
            else {
                System.out.println();
                return;
            }
        }
    }

    // four "returns" (one "return is empty!")
    public InputReturnCountExtendedCheckCtors(String z, int x, double y) {
        System.out.println();
        int a = x;
        if (a != 4)  {
            if (a != 6) {
                return;
            }
            else {
                a++;
            }
            return;
        }
        else {
            if (a - 1 != 2) {
                this.a = 0;
                return;
            }
            else {
                System.out.println();
                return;
            }
        }
    }
}
