package com.github.sevntu.checkstyle.checks.coding;
class InputCustomDeclarationOrderCheckMainMethod {
    private int t;
    public static void main(String[] agrs) {}
    public void method(){}
}

class B {
    public static void main(String[] agrs) {}
    private int t;
    public void method(){}
}

class C {
    public int d;
    public static void main(String[] agrs) {}
    private int t;
    public void method(){}
}

class D {
    public void method(){}
    public static void main(String[] agrs) {}
}