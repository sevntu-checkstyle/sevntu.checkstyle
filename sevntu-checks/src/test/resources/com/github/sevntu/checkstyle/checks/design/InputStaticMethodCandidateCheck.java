package com.github.sevntu.checkstyle.checks.design;

import static com.github.sevntu.checkstyle.checks.design.StaticMethodCandidateCheck.MSG_KEY;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Map;

import com.puppycrawl.tools.checkstyle.utils.ScopeUtils;

public class InputStaticMethodCandidateCheck extends ClassB {
    protected int instanceField;
    private static int classField;

    public void foo1(int a) {int localVariable = classField;}   // ok, as we check only private methods

    private void foo2() {this.classField++;}   // ok, as "this" is not allowed in static methods

    private void foo3() {instanceField++;}   // ok, as static methods can't use instance variables

    private void foo4() {foo1(instanceField);}   // ok, as static methods can't use instance methods

    private void foo5() {parentMethod();}   // ok, as we can't check whether a parent method is static

    private void foo6() {staticParentMethod();}   // ok, as we can't check whether a parent method is static

    private void foo7() {instanceBField++;}   // ok, as we can't check whether a parent variable is static

    private void foo8() {classBField++;}   // ok, as we can't check whether a parent variable is static

    private void foo9() {assertTrue(true);}   // ok, as we cannot determine whether a method or a variable is statically imported

    private void foo10() {String a = MSG_KEY;}    // ok, as we cannot determine whether a method or a variable is statically imported

    static ClassA ClassA = new ClassA();

    private void foo11() {    // violation
        ClassA.bar();
        ClassA.bar(1);

        ClassA ClassA = new ClassA();
        ClassA.bar();
        ClassA.bar(1);

        ClassA localVariable = new ClassA();
        localVariable.foo();
        int i = ClassA.classAField;
        int j = localVariable.instanceAField;
        Nested.nestedFoo3();
        Nested.nestedClassField++;
    }

    private static class Nested {
        private static int nestedClassField;

        private void nestedFoo1() {    //violation
            InputStaticMethodCandidateCheck local = new InputStaticMethodCandidateCheck();
            InputStaticMethodCandidateCheck.classField = local.instanceField;
            Nested nested = new Nested();
            nested.nestedClassField = nestedClassField + local.classField;
            nested.nestedFoo4();
            nestedFoo3();
        }

        private void nestedFoo2() {classBField++;}    // ok, as we can't check whether a parent variable is static

        private static void nestedFoo3() {classBField++;}    // ok, as it is already static

        private void nestedFoo4() {this.nestedFoo3();}     // ok, as "this" is not allowed in static methods
    }

    private class Inner {
        private void innerFoo() { }      // ok, as static methods can't be declared in inner classes
    }

    enum EmbeddedEnum {
        A(129),
        B(283),
        C(1212) {
            private void doSomethingInner() { }    // ok, as static methods can't be declared in enum constant definitions
        };
        
        EmbeddedEnum(int i) { }
        Map.Entry<String, Long> enumInstance;

        private void doSomething() {    // violation
            enumStatic.equals(null);
            C.doSomething();
            A.enumInstance = null;
            EmbeddedEnum i = A;
        }
        static String enumStatic;
    }
}

class ClassA {
    int instanceAField;
    static int classAField;

    public static void bar() { }    // ok, as we check only private methods

    public static void bar(int i) { }    // ok, as we check only private methods

    public static void foo() { }     // ok, as we check only private methods

    static class NestedClassA {
        public static void nestedFooClassA() { }    // ok, as it is already static
    }
}

class ClassB {
    int instanceBField;
    static int classBField;

    public static void staticParentMethod() { }   // ok, as we check only private methods

    public void parentMethod() { }   // ok, as we check only private methods
}

class Anonymous {
    static Object nullableStr = new Object();
    int instanceField;
    private void main(String[] args) {          // violation
        nullableStr.equals(new Runnable() {
            String nullableStr = null;

            public void run() { };   // ok, as static methods can't be declared in anonymous classes 

            private void anonClassMethod(){this.run();}    // ok, as static methods can't be declared in anonymous classes 
        });
    }

    private void method() {
        Runnable runner = new Runnable() {
            public void run() {
                instanceField++;
            };   // ok, as static methods can't be declared in anonymous classes 
        };
    }
}

class TestMethodSignature {
    public static void foo1(int i) { }    // ok, as it is already static

    public static void foo(int i) { }    // ok, as it is already static

    public static void foo() { }    // ok, as it is already static

    public void foo(String i) { }    // ok, as only private methods are checked

    private void bar1() {foo(1);}    // ok, because exists non-static method with the specified name and number of parameters

    private void bar2() {foo("1");}    // ok, because exists non-static method with the specified name and number of parameters

}

class TestExtended extends InputStaticMethodCandidateCheck {
    static {int i = 1;}

    {int i = 2;}

    static int classVar;
    Integer instanceVar;
    static TestExtended test = new TestExtended();
    static String string;

    public static Object foo(int i) {return new Object();}    // ok, as it is already static

    private void foo() {    // ok, as static methods can't use instance variables
        if (classVar != 0) {;}
        if (instanceVar != 0) {classVar++;}
    }

    private static TestExtended getTestExtended() {return test;}    // ok, as method is static

    private void bar() {foo(classVar).getClass().getName().equals("");}    // violation
    
    private void bar1() {instanceVar.getClass().getName().equals("");}    // ok, as static methods can't use instance variables
    
    private void foobar() {ScopeUtils.getSurroundingScope(null);}    // ok, as we cannot determine that ScopeUtils is not inherited instance field

    private void barfoo() {String i = StaticMethodCandidateCheck.MSG_KEY;}    // ok, we cannot check class StaticMethodCandidateCheck

    private void fooBar() {(new TestExtended()).bar();}    // violation

    private void barFoo() {TestExtended.test.classVar++;}    // violation

    private void fooo() {TestExtended.test.hashCode();}    // violation

    private void baar() {getTestExtended().test.instanceVar++;}    // violation

    private void fOo() {classVar++; int classVar;}    // violation

    private void foO() {int localVar = 1; localVar++;}    // violation

    private void bAr() {new File(this.test.getClass().getName());} // ok, as "this" is not allowed in static methods
}

class TestTypeVariablesAndInnerClasses<C, V>  {

    static Object instance;
    public class Inner{}

    private void foo1(Map<C, Inner> map) {    // ok, as type variables are not allowed in static methods
    }

    private Map<C, Inner> foo2() {    // ok, as type variables are not allowed in static methods
        return null;
    }

    private void foo3(C c) {    // ok, as type variables are not allowed in static methods
        C local = null;
    }

    private void foo4() {    // ok, as type variables are not allowed in static methods
        Object o1 = (Double) new Object();
        Object o2 = (C) new Object();
    }

    private void foo5() {    // ok, as non-static classes are not allowed in static methods
        Object o2 = new Inner();
    }

    private byte foo6(Inner i) {    // ok, as non-static classes are not allowed in static methods
        return 1;
    }
}

class TestInheritance extends TestTypeVariablesAndInnerClasses {
    private void foo1() {   // ok, as we cannot determine whether the static field or not is used
        Class cl = instance.getClass();
    }
}

class TestSkippedMethods extends TestTypeVariablesAndInnerClasses {
    private void readObject() {}

    private void writeObject() {}

    private void readObjectNoData() {}
}

class Issue393 {
    protected Object foo(Object[] arguments) {
        return this;
    }
}

class Issue393Child extends Issue393 {
    @Override
    protected Object foo(Object[] arguments) {
        return this;
    }

    private Issue393 bar() {
        return (Issue393) super.foo(null);
    }
}

class Issue397 {

    private void bar(int id, String status) {
        foo(1, id, status);
    }

    private void foo(int a, Object... params) {
        this.hashCode();
    }

    private static void foo(int a, String b, Object... params) {
    }
}
