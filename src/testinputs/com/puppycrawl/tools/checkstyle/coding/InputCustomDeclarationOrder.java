package com.puppycrawl.tools.checkstyle.coding;

import java.util.ArrayList;

import org.junit.Ignore;
import org.junit.Rule;

public class InputCustomDeclarationOrder
{

    public static final int TEST = 0;
    public final int test2 = 1;
    int test = 1;

    private static final String[] TEST_NAMES = new String[] { "Task 1",
            "Task 2", "Task N" };

    @SuppressWarnings("test")
    @Rule
    private AbstractTest test1;

    protected double allTests;

    @SuppressWarnings("serial")
    private final ArrayList<String> test4 = new ArrayList<String>()
    {
        private int innerTest1;
        public int innerTest2;

        public String getText(String test)
        {
            return test.toLowerCase();
        }
    };

    private InputCustomDeclarationOrder() {
    }

    protected String getTest()
    {
        return "test";
    }
    
    InputCustomDeclarationOrder(int test) {
    }

    public void setTest()
    {
    };

    public abstract class AbstractTest extends
            InputCustomDeclarationOrder
    {

        private static final long innerT1 = 1L;
        public final int innerT2 = 0;
        String innerT3;

        @Override
        protected String getTest()
        {
            return "test";
        }

        private String test()
        {
            int testStyle = 1;
            final int TEST_STYLE = 2;

            return null;
        }

        private void setTest1() {
        }

    }

    private static class Test {
        private abstract static class AbstractTest2 {
        }
        public abstract class AbstractrTest3 {
        }
    }

    @Ignore
    public boolean test2()
    {
        return true;
    }

    public static int test3;

    protected void test4()
    {
    }

}
