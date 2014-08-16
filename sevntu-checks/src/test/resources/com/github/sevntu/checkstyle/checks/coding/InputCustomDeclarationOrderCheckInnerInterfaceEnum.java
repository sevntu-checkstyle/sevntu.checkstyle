package com.github.sevntu.checkstyle.checks.coding;
class InputCustomDeclarationOrderCheckInnerInterfaceEnum {
    private int fieldOne;
    public double fieldTwo;
    
    interface InnerInterface {
        int value = 90;
        int get();
    }
}

class Ok_2 {
    private int fieldOne;
    public double fieldTwo;
    
    enum InnerEnum {
        A, B, C;
    }
}

class Ok_3 {
    private int fieldOne;
    public double fieldTwo;
    
    interface InnerInterface {
        int value = 90;
        int get();
    }
    
    enum InnerEnum {
        A, B, C;
    }
}

class Errors_1 {
    interface InnerInterface {
        int value = 90;
        int get();
    }
    
    private int field;
}

class Errors_2 {
    enum InnerEnum {
        A, B, C;
    }
    
    private int field;
}

class Errors_3 {
    private int fieldOne;
    
    interface Inter_1 {}
    enum Enum_1 {}
    interface Inter_2 {}
    enum Enum_2 {}
}

class Errors_4 {
    private interface ProducerI {
        Object produce();
    }
    
    private enum Product {
        BUTTON, DISPLAY;
    }
    
    private static void process() {
    }
}

class Ok_4 {
    private int fieldOne;
    
    void process() {
    }
    
    interface Op {
        public int value = 89; 
    }
}