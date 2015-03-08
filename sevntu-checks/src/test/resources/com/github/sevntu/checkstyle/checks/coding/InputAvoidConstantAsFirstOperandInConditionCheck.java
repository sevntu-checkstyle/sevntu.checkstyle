package com.github.sevntu.checkstyle.checks.coding;

public class InputAvoidConstantAsFirstOperandInConditionCheck {

    private Object classField;

    private boolean someFunction() {
        return false;
    }

    private Object returnNull() {
        return null;
    }

    public void allTypesCheck() {

        Object someVariable = new Object();

        final int constant = 5;
        int testInt = 10;
        float testConst = 5.0f;
        boolean testBool = true;

        if ((null) == someVariable) {} //!!
        if (5 == testInt) {} //!!
        if (constant == testInt) {}
        if (5.0d == (Double) someVariable) {} //!!
        if (5.0f == (Float) someVariable) {} //!!
        if (5l == (Long) someVariable) {} //!!
        if (true == testBool) {} //!!
        if (false == testBool) {} //!!
    }

    /**
     * Basic tests.
     */
    public void ifCheck() {
        Object someVariable = new Object();
        Object nullVariable = null;

        if (null == null) {}

        // Compare variable
        if (someVariable == null) {}
        if (someVariable != null) {}
        if (null == someVariable) {} //!!
        if (null != someVariable) {} //!!

        // Compare function
        if (returnNull() == null) {}
        if (returnNull() != null) {}
        if (null == returnNull()) {} //!!
        if (null != returnNull()) {} //!!

        // Compare class field
        if (this.classField == null) {}
        if (classField != null) {}
        if (null == this.classField) {} //!!
        if (null != classField) {} //!!

        // Compare with null-variable
        if (someVariable == nullVariable) {}
        if (nullVariable == someVariable) {}

        // Short IF tag
        someVariable = (nullVariable == null) ? true : false;
        someVariable = (null == nullVariable) ? true : false; //!!

        // Test more than one condition
        if (someFunction() && (someVariable == null)) {}
        if (someFunction() && null == someVariable) {} //!!
        if (someFunction() && (null == someVariable)) {} //!!
        if ((null == someVariable) && someFunction()) {} //!!
        if (someFunction() || (null == someVariable) && someFunction()) {} //!!

        if (someVariable == null && null == someVariable) {} //!!
        if (null == someVariable && someVariable == null) {} //!!
        if (null == someVariable && null == someVariable) {} //!! twice

        // Test for different code style
        // (check line and position number in error message)
        if ((
                null
                        == someVariable)
                &&null==someVariable
                || (	null	==	someVariable)) {} //!!
    }

    /**
     * Almost all tests are checked in ifCheck() function,
     * so there are only checked some tests
     */
    public void whileCyclesCheck() {
        Object someVariable = new Object();

        while (someVariable == null) {}
        while (null == someVariable) {} //!!

        do {
            // Something...
        } while (null == someVariable); //!!
    }

    /**
     * Almost all tests are checked in ifCheck() function,
     * so there are only checked some tests
     */
    public void forCyclesCheck() {
        Object someVariable = new Object();

        for (;null==someVariable;) {} //!!
        for (someVariable = null; null == someVariable; someVariable = null) {} //!!
    }

}
