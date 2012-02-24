public class InputOneReturnInMethodCheckCtors
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
        return a + a * a;
    }

    // two "returns"
    public InputReturnCountExtendedCheckCtors(int x, String s) {
        System.out.println();
        int a = x + s.length();
        if (a != 4) {
            return 1;
        }
        else {
            System.out.println(a);
            return 2;
        }
    }

    // three "returns"
    public InputReturnCountExtendedCheckCtors(String z, double y) {
        System.out.println();
        int a = z;
        y+=0.6;
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

    // four "returns"
    public InputReturnCountExtendedCheckCtors(String z, int x, double y) {
        System.out.println();
        int a = x;
        if (a != 4)  {
            if (a != 6) {
                return 1;
            }
            else {
                a++;
            }
            return x;
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
}
