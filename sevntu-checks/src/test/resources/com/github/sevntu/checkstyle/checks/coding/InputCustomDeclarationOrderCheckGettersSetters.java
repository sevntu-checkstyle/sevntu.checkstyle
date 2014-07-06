package com.github.sevntu.checkstyle.checks.coding;
import org.aspectj.lang.annotation.Before;

import test.aop.Lockable;
import junit.framework.TestCase;
class Ok_1 {
    private int field;
    private double x;
    private boolean visible;

    public int getField() {
        return field;
    }

    public void setField(int field) {
        this.field = field;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void method() {
    }
}

// =========================================================================
// =========================================================================
// =========================================================================
class Errors_1 {
    private int field;
    private double x;
    private boolean visible;

    public void method() {
    }

    // wrong order
    public int getField() {
        System.out.println();
        return field;
    }

    // wrong order
    public void setField(int field) {
        this.field = field;
    }

    // wrong order
    public double getX() {
        return x;
    }

    // wrong order
    public void setX(double x) {
        this.x = x;
    }

    // wrong order
    public boolean isVisible() {
        return visible;
    }

    // wrong order
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

}

// =========================================================================
// =========================================================================
// =========================================================================
class Errors_2 {
    private int field;
    private double x;
    private boolean visible;

    // wrong order
    public void setField(int field) {
        this.field = field;
    }

    public int getField() {
        return field;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void method() {
    }
}

// =========================================================================
// =========================================================================
// =========================================================================
class Errors_3 {
    private int field;
    private double x;
    private boolean visible;

    // wrong order
    public void setField(int field) {
        this.field = field;
    }

    // wrong order
    public void setX(double x) {
        this.x = x;
    }

    // wrong order
    public void setVisible(boolean visible) {
        log.info("Visible is " + visible);
        this.visible = visible;
    }

    public double getX() {
        return x;
    }

    public boolean isVisible() {
        return visible;
    }

    public int getField() {
        return field;
    }

    public void method() {
    }
}

// =========================================================================
// =========================================================================
// =========================================================================
class Errors_4 {
    private int field;
    private double x;
    private boolean visible;

    // wrong order
    public void setField(int field) {
        this.field = field;
    }

    public void method() {
    }

    // wrong order
    public void setX(double x) {
        this.x = x;
    }

    public int getField() {
        return field;
    }
}

// =========================================================================
// =========================================================================
// =========================================================================
interface SetterI {
    void setValue(Object value);

    Object getValue();
}

abstract class Setter<T> {
    abstract void setValue(T value);

    abstract T getValue();
}

// =========================================================================
// =========================================================================
// =========================================================================
// There are no getters or setters.
class ASD {
    private int asd;

    public int getItIsNotGetter() {
        if (true) {
            System.out.println("Inside");
        } else {
            System.out.println();
        }
        return asd;
    }

    public void simpleMethod() {
    }

    public void setItIsNotSetter(int asd) {
        this.asd = asd;
        System.out.println("asd: " + asd);
    }

}

// =========================================================================
// =========================================================================
// =========================================================================
class Check {
    private void findGetterSetter(DetailAST aMethodDefAst) {

    }

    private boolean isInFoundGetters(String aMethodName) {
        boolean result = false;
        for (DetailAST methodAst : mGetters.peek()) {
            String methodName = getIdentifier(methodAst);
            if (methodName.equals(aMethodName)) {
                result = true;
            }
        }
        return result;
    }
}

// =========================================================================
// =========================================================================
// =========================================================================
class LocalVariable {

    public void checkDate() {
    }

    // it is not a getter
    public Comparator getComparator() {
        Comparator comparator = new Comparator();
        return comparator;
    }

    // it is not a setter
    public void setComparator(Comparator newComparator) {
        Comparator comparator;
        comparator = newComparator;
    }

    public void main() {
    }
}

class Errors_5 {
    private int field;
    private int x;
    private int y;
    private boolean visible;
    private WorkMode workMode;
    private UserCahe userCache;

    // wrong order
    public void setField(int field) {
        this.field = field;
    }

    public void method() {

    }

    // wrong order
    public void setX(Integer x) { // ! not a setter !!!
        x = Integer.parseInt(x + "");
    }
    
    public void setWorkMode(String workMode) {
        this.workMode = WorkMode.valueOf(workMode);
    }

    public void setUserCache(UserCache userCache) {
        super.setUserCache(userCache);
    }

    // wrong order
    public void setY(Integer y) { // setter
        this.y = Integer.parseInt(y + "");
    }
    
 class TestMnaNewsClassifier extends TestCase {

        private MnaNewsClassifier classifier;

        @Override
        protected void setUp() throws Exception {
            super.setUp();
            classifier = new MnaNewsClassifier();
        }

        @Override
        protected void tearDown() throws Exception {
            super.tearDown();
            classifier = null;
        }

        public void testMna() throws Exception {
            assertTrue(classifier.isMna(IoHelper.readText(getClass(), "mna1.txt")));
        }

        public void testNonMna() throws Exception {
            assertFalse(classifier.isMna(IoHelper.readText(getClass(), "non-mna1.txt")));
        }

        @Before(value="execution(void set*(*)) && this(mixin)", argNames="mixin")
        public void checkNotLocked(
            Lockable mixin)  // Bind to arg
        {
            // Can also obtain the mixin (this) this way
            //Lockable mixin = (Lockable) jp.getThis();
            if (mixin.locked()) {
                throw new IllegalStateException();
            }
        }
        
        public void setAge(int a) {}
        
        public void testMany() throws Exception {
            for (int i = 0; i < 1000; i++) {
                assertTrue(classifier.isMna(IoHelper.readText(getClass(), "mna1.txt")));
            }
        }

    }
    
}
