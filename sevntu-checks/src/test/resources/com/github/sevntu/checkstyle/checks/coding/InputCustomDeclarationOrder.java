package com.github.sevntu.checkstyle.checks.coding;

import java.util.ArrayList;

public class InputCustomDeclarationOrder {

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

    private InputCustomDeclarationOrder() {
        //constructor
    }

    public static String newTest(String aTest) {
        return null;
    }

    protected String test() {
        return "test";
    }

    InputCustomDeclarationOrder(int aTest) {
        //constructor
    }

    public void createTest() {
    };

    public static void editTest() {
    }

    public abstract class AbstractTest extends
            InputCustomDeclarationOrder
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
        public abstract class AbstractrTest2 {
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

class Test {

    Test() {
    }

    private String someTest;

    void test() {
    }
}
