public abstract class InputAvoidMainMethodInAbstractClass {
    public static void main(String[] args) {}
}
abstract class InputAvoidMainMethodInAbstractClass1 {
    public static void main(String... args) {}
}
abstract class InputAvoidMainMethodInAbstractClass2 {
    public static String main(String[] args) {return "";}
}
abstract class InputAvoidMainMethodInAbstractClass3 {
    public void main(String... args) {}
}
abstract class InputAvoidMainMethodInAbstractClass4 {
    static void main(String... args) {}
}
abstract class InputAvoidMainMethodInAbstractClass5 {
    static void main(String[] args) {}
}
abstract class InputAvoidMainMethodInAbstractClass6 {
    public void main(String[] args) {}
}
abstract class InputAvoidMainMethodInAbstractClass7 {
    public static String main(String... args) { return null; }
}
abstract class InputAvoidMainMethodInAbstractClass8 {
    public static void main(String[] args) {}
}
abstract class InputAvoidMainMethodInAbstractClass9 {
    public static String main(String[] args) { return null; }
}
abstract class InputAvoidMainMethodInAbstractClass10 {
    public static void main() {}
}

abstract class Abstract1 {
    private static abstract class NestedInner {
        public static void main(String... args) {}
    }
    abstract class Inner_Abstract1 {
        public void main(String[] args) {}
    }
}
abstract class Abstract2 {
    private static abstract class NestedInner {
        public static void main(String[] args) {}
    }
    abstract class Inner_Abstract1 {
        public void main(String[] args) {}
    }
    void doStuff() {
        abstract class MethodInner {
            public void main(String... args) {}
        }
    };
}
class NotAbstract {
    private static abstract class NestedInner {
        public static void main(String... args) {}
    }
    abstract class Inner_Abstract1 {
        public void main(String[] args) {}
    }
    public static void main(String[] args) {}
    void doStuff() {
        abstract class MethodInner {
            public void main(String... args) {}
        }
        class MethodInner2 {
            public void main(String... args) {}
        }
    }
}
abstract class Dumb {
    public abstract void main(String[] args);
}

abstract class AbstractWithAnonymous {
    Dumb d = new Dumb() {
        public void main(String[] args) {}
    };
    public static void main(String[] args) {}
    
}