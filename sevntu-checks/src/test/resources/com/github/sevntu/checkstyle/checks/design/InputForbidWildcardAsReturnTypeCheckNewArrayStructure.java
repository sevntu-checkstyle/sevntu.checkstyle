package com.github.sevntu.checkstyle.checks.design;

public class InputForbidWildcardAsReturnTypeCheckNewArrayStructure {
    protected Class<?>[] getAnnotatedClasses() {
        return new Class<?>[]{
                Parent.class,
                Child.class
        };
    }

    private class Parent {
    }

    private class Child {
    }
}
