package com.github.sevntu.checkstyle.checks.coding;

public class InputSimpleAccessorNameNotationCheckOverride extends ParentClass {
    @Override
    public String getOne() {
        return two;
    }

    @java.lang.Override
    public String getTwo() {
        return one;
    }
}

abstract class ParentClass {
    protected String one;
    protected String two;

    public abstract String getOne();

    public abstract String getTwo();
}
