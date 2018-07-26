package com.github.sevntu.checkstyle.checks.design;

import java.io.Serializable; import java.util.Collection; 
import java.util.Comparator; import java.util.List;
import java.util.Map;
import java.util.ArrayList;
class Wildcards
{
    public <T> List<? super T> met1()
    {
        return null;
    }

    <T> List<? super T> met2()
    {
        return null;
    }

    protected <T> List<? super T> met3()
    {
        return null;
    }

    private <T> List<? super T> met4()
    {
        return null;
    }

    public <T> List<? extends T> met5()
    {
        return null;
    }

    <T> List<? extends T> met6()
    {
        return null;
    }

    protected <T> List<? extends T> met7()
    {
        return null;
    }

    private <T> List<? extends T> met8()
    {
        return null;
    }

    private <T> List<? extends T> met9()
    {
        return null;
    }

    public Class<? extends Integer> met10()
    {
        return Integer.class;
    }

    public Class<? super Integer> met11()
    {
        return Integer.class;
    }

    public Class<?> met12()
    {
        return Integer.class;
    }
}

class NoWildcards
{
    public void simple1()
    {
    }

    public int simple2()
    {
        return 1;
    }

    public Object simple3()
    {
        return new Object();
    }

    <T> T simple4(T t)
    {
        return null;
    }

    <T extends Integer> T simple5(T t)
    {
        return null;
    }

    <T> List<T> simple6(T t)
    {
        return null;
    }
}

class ClassWildGenericType<P>
{
    public List<? extends P> get1()
    {
        return null;
    }

    public List<?> get2()
    {
        return null;
    }

    public static List<? extends Number> get3()
    {
        return null;
    }

    public static List<? super Number> get4()
    {
        return null;
    }

    public static List<?> get5()
    {
        return null;
    }

    public Map<? extends P, Integer> get6()
    {
        return null;
    }

    public Map<Integer, ? extends P> get7()
    {
        return null;
    }
}

class BigGeneric<A, B, C, D>
{
}

class UsingBigGeneric<A, B, C, D>
{

    final BigGeneric<?, B, C, D> get1()
    {
        return null;
    }

    synchronized BigGeneric<A, ?, C, D> get2()
    {
        return null;
    }

    strictfp BigGeneric<A, B, ?, D> get3()
    {
        return null;
    }

    native BigGeneric<A, B, C, ?> get4();

    BigGeneric<? extends String, B, C, D> get5()
    {
        return null;
    }

    BigGeneric<A, ? extends String, C, D> get6()
    {
        return null;
    }

    BigGeneric<A, B, ? extends String, D> get7()
    {
        return null;
    }

    BigGeneric<A, B, C, ? extends String> get8()
    {
        return null;
    }

    BigGeneric<A, B, C, D> get9()
    {
        return null;
    }

    BigGeneric<? extends String, ? super String, C, D> get10()
    {
        return null;
    }
}

class MultipleInterfacesInGeneric
{
    <U extends Number & Serializable> List<U> mult1()
    {
        return null;
    }

    <U extends Number & Serializable> List<? extends U> mult2()
    {
        return null;
    }

    <U extends Number & Serializable> List<? super U> mult3()
    {
        return null;
    }
}

class CheckIgnoreList {
    Comparable<? extends Integer> opt1(){
        return null;
    }
    
    Comparator<? extends Double> opt2(){
        return null;
    }
    Collection<?> opt3(){
        return null;
    }
}
/////////////////////
class OuterClass {
    class ZeroInnerClass {
        public List<? extends Number> met1(){
            return null;
        }
        protected List<? super Number> met2(){
            return null;
        }
        private List<?> met3(){
            return null;
        }
        List<Number> met4(){
            return null;
        }
    }
    abstract class FirstInnerClass {
        public abstract List<? extends Number> met1();
        protected abstract List<? super Number> met2();
        abstract List<?> met3();
        protected abstract List<Number> met4();
    }
    static class SecondInnerClass {
        static List<? extends Number> met1(){
            return null;
        }
        final List<? super Number> met2(){
            return null;
        }
        synchronized List<?> met3(){
            return null;
        }
        List<Number> met4(){
            return null;
        }
    }
    
    public static void main(String[] args) {
        class ThirdInnerClass {
            List<? extends Number> met1(){
                return null;
            }
            List<? super Number> met2(){
                return null;
            }
            List<?> met3(){
                return null;
            }
        }
    }
}

interface InterfaceTest{
    void withoutWildcard1();
    Map<Double,Integer> withoutWildcard2();
    Map<?,Integer> getMap1();
    Map<Double,?> getMap2();
    Map<? extends Double,?> getMap3();
    Map<?,? extends Double> getMap4();
    Map<? super Double,?> getMap5();
    Map<?,? super Double> getMap6();
    Map<? extends Double, ? super Integer> getMap7();
}

class OuterTest{
    private Cloneable t = new Cloneable(){
        List<? extends Number> met1(){
            return null;
        }
        public List<? super Number> met2(){
            return null;
        }
        private List<?> met3(){
            return null;
        }
    };
}

class MethodsWithAnnotations extends SomeClass {
    @Deprecated public List<?> met1(){
        return null;
    }
    @Override public List<?> met2(){
        return null;
    }
    @SuppressWarnings("unchecked") public List<?> met3() {
        return null;
    }
    @java.lang.Deprecated
    @java.lang.Override
    @SuppressWarnings("unused")
    public List<?> met4() {
        return null;
    }
}

class GenericMethods {
    public static <T extends Comparable<? super T>> T max(List<? extends T> t) {
        return null;
    }
}

class DeprecatedMethods {
    @Deprecated
    public static <E extends List<?>> Class<?> getArgumentClass(List<?> listener) {
        return listener.getClass();
    }
}

class MyClass<T> {}
class A extends MyClass<A>{}
class B extends A{}

public class InputForbidWildcardAsReturnTypeCheck
{
    public static void main(String args[]) {
        A objA = max(new ArrayList<A>());
        B objB = max(new ArrayList<B>());
    }
    static <T extends MyClass<? super T>> T max(List<? extends T> list) {
        return null;
    }
}

abstract class SomeClass {
    abstract List met2();
    abstract List met4();
}

