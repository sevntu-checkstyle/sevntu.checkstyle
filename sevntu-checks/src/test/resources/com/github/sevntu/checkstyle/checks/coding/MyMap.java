package com.github.sevntu.checkstyle.checks.coding;
import java.util.Collection;
import java.util.Map;
import java.util.Iterator;
import java.util.Set;

public class MyMap<K, V> implements Iterable, Map
{
   

    @Override
    public Iterator iterator()
    {
        return null;
    }
    public static class Entry<K, V> {

        public String getValue()
        {
            return null;
        }

        public char[] getKey()
        {
            return null;
        }
        
    }
    @Override
    public int size()
    {
        return 0;
    }

    @Override
    public boolean isEmpty()
    {
        return false;
    }

    @Override
    public boolean containsKey(Object key)
    {
        return false;
    }

    @Override
    public boolean containsValue(Object value)
    {
        return false;
    }

    @Override
    public Object get(Object key)
    {
        return null;
    }

    @Override
    public Object put(Object key, Object value)
    {
        return null;
    }

    @Override
    public Object remove(Object key)
    {
        return null;
    }

    @Override
    public void putAll(Map m)
    {
        
    }

    @Override
    public void clear()
    {
        
    }

    @Override
    public Set keySet()
    {
        return null;
    }

    @Override
    public Collection values()
    {
        return null;
    }

    @Override
    public Set<MyMap.Entry<K, V>> entrySet()
    {
        return null;
    }
}
