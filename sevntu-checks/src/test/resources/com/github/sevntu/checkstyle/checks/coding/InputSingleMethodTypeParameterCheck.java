package com.github.sevntu.checkstyle.checks.coding;

import java.util.List;
import java.util.Map;

public abstract class InputSingleMethodTypeParameterCheck {

    public interface A {
        void complaint(List<?> list, int i, int j);

        <E> void violation(List<E> list, int i, int j);

        <K, V> void violation(Map<K,V> map, int i, int j);

        void complaint(int key, Map<?,?> map, int value);

        <E,T> void complaint(List<E> first, List<T> second, int i, int j);
    }

    protected abstract <E> void complaint(List<E> list, int i, int j);

    abstract <K, V> void complaint(Map<K,V> map, int i, int j);

    abstract void complaint(int key, Map<?,?> map, int value);

    abstract <E,T> void complaint(List<E> first, List<T> second, int i, int j);

    public abstract <E> void violation(List<E> list, int i, int j);

    public abstract <K, V> void violation(Map<K,V> map, int i, int j);

    public abstract <T extends Comparable<T>> void violation(List<T> list, int i);

    public abstract void complaint(List<? extends Comparable> list, int i);

    public abstract <T,E extends Comparable<T>> void complaint(List<T> first, List<E> second);

    public abstract void complaint(int i, int j);

    public abstract void complaint();

    public abstract void violation(List<String> modules);

    public abstract List<?> complaint(List<?> modules, String string);

    public abstract List<String> violation(List<String> modules, String string);

    public abstract List<String> complaint(List<?> modules);

    public abstract List<String> violation(List<Class<?>> modules, int i, String names);

    public abstract <K> void violation(Map<K,?> map);

    public abstract <T> void complaint(List<T> dest, List<? extends T> src, String str);

    public abstract <T, S extends T> void complaint(List<T> dest, List<S> src, int i);

}
