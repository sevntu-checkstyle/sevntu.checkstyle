package com.github.sevntu.checkstyle.checks.coding;

@SuppressWarnings("unused")
public class InputTernaryPerExpressionCountCheck {

    int x = 0;
    int y = 0;
    int z = 0;
    private static final int oO = 5;
    public boolean getSmth() {
        int a = 0;
        int b = 0;
        // examples of ugly code:
        int k = (a == b) ? (a == b) ? (a == b) ? 5 : 6 : 6 : 6;
        // simple:
        int a1 = 5;
        final int b1 = 6;
        final int d = (a == b) ? (a == b) ? 5 : 6 : 6; // bad (nested in first position)
        int c = (a == b) ? 5 : 6; // good
        final int d1 = (a == b) ? (a == b) ? 5 : 6 : 6; // bad (nested in first position)
        int e = (a == b) ? 5 : (a == b) ? 5 : 6; // bad (nested in second position)

        // more complex:
        Integer result = (0.2 == Math.random()) ? null : 5; // good
        final Integer result2 = (0.2 == Math.random()) ? (0.3 == Math.random()) ? null : 3 : 6; // bad (nested in first position)
        Integer result3 = (0.2 == Math.random()) ? null : (0.3 == Math.random()) ? null : 4; // bad (nested in second position)

        // and more complex:
        int r1 = (getSmth() || Math.random() == 5) ? null : (int) Math.cos(400 * (10 + 40)); // good
        final int r2 = (0.2 == Math.random()) ? (0.3 == Math.random()) ? null : (int) Math.cos(400 * (10 + 40)) : 6; // bad (nested in first position)
        int r3 = (Integer) ((0.2 == Math.random()) ? (Integer) null + apply(null) : (0.3 == Math.random()) ? (Integer) null : (int) Math.sin(300 * (12 + 30))); // bad (nested in second position)

        // String inline ternary:
        checkSmth("msg " + ((a == b) ? "5" : "6"), apply(null), getSmth()); // good
        checkSmth("msg " + ((a == b) ? (a == b) ? "5" : "6" : "6"), getSmth(), getSmth()); // bad (nested in first position)
        checkSmth("msg " + ((a == b) ? "5" : (a == b) ? "5" : "6"), getSmth(), getSmth()); // bad (nested in second position)
        String x = (getSmth() ? "A" : "B") + (getSmth() ? "B" : "C");
        x = getSmth() ? "A" : "B" + (getSmth() ? "B" : "C");
        String String = new String("");
        return getSmth();
    }

    void InputNestedTernaryCheck() {
        // in C-tor (final variable first-initialization):
        x = (getSmth() || Math.random() == 5) ? null : (int) Math
                .cos(400 * (10 + 40)); // good
        y = (0.2 == Math.random()) ? (0.3 == Math.random()) ? null : (int) Math
                .cos(400 * (10 + 40)) : 6; // bad (nested in first position)
        z = (Integer) ((0.2 == Math.random()) ? (Integer) null + apply(null)
                : (0.3 == Math.random()) ? (Integer) null : (int) Math
                        .sin(300 * (12 + 30))); // bad (nested in second
                                                // position)
    }

    public String apply(String column) {
        Object alias1 = null;
        return (String) (alias1 != null ? 6 : 5
                 + " = " + alias1 != null ? 7 : "ss");
    }
    
    

    private void foo() {
        String sectorName = null;
        new String(new String(
                "1. Ensure all tradenames in "
                + (sectorName != null ? sectorName + " " : "") //???
                + "2. If company possibly warrants placement in other industry"
                + ", flag to relevant analyst.\n"
                + "3. Update focus tag\n"
                + "4. Update " + (sectorName != null ? sectorName + " " : "") + "timestamp\n"));
    }

   public static void checkSmth(String string, boolean b, boolean c) {
       String catalogNameToUse = null;
       String schemaNameToUse = null;
       String procedureNameToUse = null;
       String callString = "{? = call " +
               ("".concat("ss") != null ? catalogNameToUse + "." : "") +
               ("".concat("ss") != null ? schemaNameToUse + "." : "") +
               procedureNameToUse + "(";
   } 

   int x1 = (oO == 5) ? 5 : 6;
   
    private void checkSmth(String arg, String string, boolean smth) {
        int d = 0;
        Object g = null;
        Object k = null;
        Object f = null;
        Object a = (d == 5) ? d : f
                +
                 new String((String) ((d == 6) ? g : k));
        Object b = (d == 5) ? d : f
                +
                 new String((String) ((d == 6) ? g : k));
    }
    private void checkSmth(int a, int k, int o, boolean p) {
        Object f = (k == 3) ? 5 : 2
                +
                 new String((String) ((o == 4) ? p ? (a == 2) : 5 : 6));
    }
}
