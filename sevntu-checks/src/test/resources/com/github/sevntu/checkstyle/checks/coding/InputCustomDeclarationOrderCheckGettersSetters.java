package com.github.sevntu.checkstyle.checks.coding;
import com.puppycrawl.tools.checkstyle.api.DetailAST;

import junit.framework.TestCase;
class InputCustomDeclarationOrderCheckGettersSetters {
    private int field;
    private double x;
    private boolean visible;

    public int getField() {
        return this.field;
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
class Errors_10 {
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
class Errors_20 {
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
class Errors_30 {
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
        String.format(String.valueOf(visible), 4);
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
class Errors_40 {
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
        int[] col = null; for (Object methodAst : col) {
            String methodName = col.toString();
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

class UserCache {

    public void setUserCache(UserCache userCache)
    {
        
        
    }
    
}

class Errors_5 extends UserCache {
    private int field;
    private int x;
    private int y;
    private boolean visible;
    private String workMode;
    private UserCache userCache;

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
        this.workMode = String.valueOf(workMode);
    }

    public void setUserCache(UserCache userCache) {
        super.setUserCache(userCache);
    }

    // wrong order
    public void setY(Integer y) { // setter
        this.y = Integer.parseInt(y + "");
    }
 
 class TestMnaNewsClassifier extends TestCase {

        private String classifier;

        @Override
        protected void setUp() throws Exception {
            super.setUp();
            classifier = new String();
        }

        @Override
        protected void tearDown() throws Exception {
            super.tearDown();
            classifier = null;
        }

        public void testMna() throws Exception {
            assertTrue(classifier.equals(String.format("mna1.txt")));
        }

        public void testNonMna() throws Exception {
            assertFalse(classifier.equals(String.format("non-mna1.txt")));
        }

        public void setAge(int a) {}
        
        public void testMany() throws Exception {
            for (int i = 0; i < 1000; i++) {
                assertTrue(classifier.equals(String.format("mna1.txt")));
            }
        }

    }


    
}