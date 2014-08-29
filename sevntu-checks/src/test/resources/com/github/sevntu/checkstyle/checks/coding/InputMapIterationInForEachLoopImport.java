package com.github.sevntu.checkstyle.checks.coding;
import com.github.sevntu.checkstyle.checks.coding.MyMap;
import java.util.*;

public class InputMapIterationInForEachLoopImport
{
    public static void main(String[] args)
    {
        Map<String, String> map = new HashMap<String, String>();
        MyMap<String, String> myMap = new MyMap<String, String>();

        for (String key : map.keySet())
        {
            System.out.println(key + " --> " + map.get(key));
        }

        for (MyMap.Entry<String, String> entry : myMap.entrySet())
        {
            System.out.println(entry.getValue() + " --> ");
        }
    }
    
}
