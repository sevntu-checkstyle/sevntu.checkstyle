package com.github.sevntu.checkstyle.checks.coding;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;

import javax.swing.tree.TreePath;


public class InputDiamondOperatorForVariableDefinitionCheck {
    static Map<String, List<String>> myMap1 = new TreeMap<String, List<String>>();
    List<Integer> list1 =
            new ArrayList<Integer>();
    Map<String, List<String>> myMap = new HashMap<String, List<String>>();
    List<Character> abc = new LinkedList<Character>();
    private TreePath[] suppressParentPaths(TreePath[] paths) {
        List<TreePath> selectedPaths = Arrays.asList(paths);
        Collections.sort(selectedPaths, new Comparator<TreePath>() {
            @Override
            public int compare(TreePath o1, TreePath o2) {
                return 1;
            }
        });
        return paths;
     }
    
    private TreePath[] suppressParentPaths1(TreePath[] paths) {
        List<TreePath> selectedPaths = Arrays.asList(paths);
        Collections.sort(selectedPaths, new Comparator<TreePath>() {
            @Override
            public int compare(TreePath o1, TreePath o2) {
                return 0;
            }
        });
     
    
    Object statusArray[] = null;
    Arrays.sort(statusArray, new Comparator<Object>() {
        public int compare(Object s1, Object s2) {
            return 1;
        }
    });
    return paths; }

    List<Integer> list = new LinkedList<Integer>();
    private transient Map<Method, String> shadowMatchCache = new ConcurrentHashMap<Method, String>(32);
    
}
    
    