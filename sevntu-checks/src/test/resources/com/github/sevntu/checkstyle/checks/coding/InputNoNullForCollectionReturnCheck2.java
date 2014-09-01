package com.github.sevntu.checkstyle.checks.coding;
import java.util.*;
public class InputNoNullForCollectionReturnCheck2
{
    private Collection method1()
    {
        return null; //!!
    }
    
    private LinkedList method2()
    {
        return new LinkedList();
    }
    
    private LinkedList<String> method3()
    {
        return null; //!!
    }
    
    private ArrayList method4()
    {
        return new ArrayList();
    }
    
    private HashSet method5()
    {
        return null; //!!
    }
    
    private HashSet method6()
    {
        return new HashSet();
    }
    
    private TreeSet method7()
    {
        return null; //!!
    }
    
    private TreeSet method8()
    {
        return new TreeSet();
    }
    
    private LinkedList method9()
    {
        return null; //!!
    }
    
    private LinkedList method10()
    {
        return new LinkedList();
    }
    
    private Stack method11()
    {
        return null; //!!
    }
    
    private Stack method12()
    {
        return new Stack();
    }
    
    private Vector method13()
    {
        return null; //!!
    }
    
    private Vector method14()
    {
        return new Vector();
    }
}
