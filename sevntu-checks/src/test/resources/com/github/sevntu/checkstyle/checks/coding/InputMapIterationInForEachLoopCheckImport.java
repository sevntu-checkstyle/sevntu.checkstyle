package com.github.sevntu.checkstyle.checks.coding;
import com.github.sevntu.checkstyle.checks.coding.external.InputMyMap;
import java.util.*;

public class InputMapIterationInForEachLoopCheckImport
{
    public static void main(String[] args)
    {
        Map<String, String> map = new HashMap<String, String>();
        InputMyMap<String, String> myMap = new InputMyMap<String, String>();

        for (String key : map.keySet())
        {
            System.out.println(key + " --> " + map.get(key));
        }

        for (InputMyMap.Entry<String, String> entry : myMap.entrySet())
        {
            System.out.println(entry.getValue() + " --> ");
        }
    }
    
}
