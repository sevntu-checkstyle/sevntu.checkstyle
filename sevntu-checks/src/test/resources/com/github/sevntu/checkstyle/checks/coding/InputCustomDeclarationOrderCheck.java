package com.github.sevntu.checkstyle.checks.coding;

import java.util.ArrayList;

public class InputCustomDeclarationOrderCheck {

    public static final int TEST = 0;
    public final int test = 1;
    
    private static final long serialVersionUID = 1L;
    
    int test1 = 1;

    private static final String[] TEST_NAMES = new String[] { "Task 1",
            "Task 2", "Task N" };

    @SuppressWarnings("test")
    private AbstractTest test2;

    protected double test3;

    @SuppressWarnings("serial")
    private final ArrayList<String[]> test4 = new ArrayList<String[]>()
    {
        private int innerTest1;
        public int innerTest2;

        public String test() {
            return null;
        }
    };

    private InputCustomDeclarationOrderCheck() {
        //constructor
    }

    public static String newTest(String aTest) {
        return null;
    }

    protected String test() {
        return "test";
    }

    InputCustomDeclarationOrderCheck(int aTest) {
        //constructor
    }

    public void createTest() {
    };

    public static void editTest() {
    }

    public abstract class AbstractTest extends
            InputCustomDeclarationOrderCheck
    {

        private static final long INNER_T1 = 1L;
        public final int innerT2 = 0;
        String innerT3;

        @Override
        protected String test() {
            return "test";
        }

        private String innerTest() {
            return null;
        }

        private void innerTest1() {
        }

    }

    private static class Test {
        private abstract static class AbstractTest1 {
        }
        public abstract class AbstractTest2 {
        }
    }

    @Deprecated
    public boolean test1() {
        return true;
    }

    public boolean test2() {
        return true;
    }

    public static int test5;

    protected void test3() {
    }

}

class Test1 {

    Test1() {
    }

    private String someTest;

    void test() {
    }
}

// Check must ignore these cases:
class ClassForIssue58 {
    public Object createB() {
        class B {

        }
        return new B();
    }

    void otherMethod() {
        
    }
}

class ClassForIssue528 {
    private void simplePrivate() {
        class Inner {
            int number;
            public void get(){}
            private void set(){}
        }
    }
    
    protected void simpleProtected() {
        class Inner {
            private int a;
            protected int b;
            int c;
            public int d;
            private void a(){}
            void c(){}
            public void d(){}
            class AnotherClass{
                public void t(){}
                private void y(){}
            }
        }
    }
    
    public void simplePublic() {
        class Inner {
            private int a;
            protected int b;
            int c;
            public int d;
            private void a(){}
            void c(){}
            public void d(){}
            class AnotherClass{
                public boolean right;
                private boolean left;
            }
        }
    }
    
    public void abc() {
        class A {
            public boolean is;
        }
        class B {
            private boolean is;
        }
    }
}

class ClassForIssue264
{
    final public int privateabc = 7;
    public final int abcprotected = 7;
    protected String abcprivate;
    protected double privatebcd;
    protected int abcprivateabc;
    private String protectedz;
    
    public abstract class privatea {
        
    }
    private class publicabstract {
        
    }
}
