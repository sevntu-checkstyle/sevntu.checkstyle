package com.github.sevntu.checkstyle.checks.coding;
public class InputReturnCountExtendedCheckMethods
{

    int a = 0;

    public int oneReturnInMethod() {
        return a;
    }

    public int oneReturnInMethod2() {
        for (int i = 0; i < 10; i++) {
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
        return a + a * a;
    }

    public int twoReturnsInMethod() {
        System.out.println();
        int a = 1;
        if (a != 4) {
            return 1;
        }
        else {
            System.out.println(a);
            return 2;
        }
    }

    public int threeReturnsInMethod() {
        System.out.println();
        int a = 1;
        if (a != 4)
        {
            return 1;
        }
        else
        {
            if (a - 1 != 2) {
                this.a = 0;
                return 6;
            }
            else {
                System.out.println();
                return 2;
            }
        }
    }

    public int fourReturnsInMethod() {
        System.out.println();
        int a = 1;
        if (a != 4)  {
            if (a != 6) {
                return 1;
            }
            else {
                a++;
            }
            return 4;
        }
        else {
            if (a - 1 != 2) {
                this.a = 0;
                return 6;
            }
            else {
                System.out.println();
                return 2;
            }
        }
    }

    // exclusive test method with the "return" depth = 3 
    public int r() {
       do {
           if(true) {
               if(true)return 5;
           }
       } while(true);
    }
    
    //exclusive test for "try-catch block processing"
    public int nm()
    {
        if (true) {
            try {
            }
            catch (Exception e) {
                if (true)
                    return 5;
            }
        }
        return a;
    }

    public void returnFromLiteral() {
        if (true) {
            return;
        }

        switch (hashCode()) {
        case 0:
            return;
        }

        if (false) {
            for (;;) {
                return;
            }
        }

        if (false) {
            do {
                return;
            } while (false);
        }

        if (false)
            while (true) {
                return;
            }

        try {
            return;
        } catch (Exception e) {
        }
    }

    public void doNothing() {
        return;
    }
}
