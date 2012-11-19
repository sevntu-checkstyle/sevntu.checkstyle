public abstract class InputAvoidMainMethodInAbstractClass {
    public abstract void doSomething();
    public abstract void main();
    public abstract void doSomething2();
    public abstract String main(String arg);
    public abstract void doSomething3();
    abstract class Inner {
        void main() {}
    }
    public String main(String arg, int i) {
        return "";
    }
    class Inner2 {
        void main() {}
    }
}

class NotAbstractWithMain {
    public static void main(String[] args) {}
    private abstract class Inner {
        abstract void main();
    }
}

abstract class AbstractWithMain {
    abstract void main();
}