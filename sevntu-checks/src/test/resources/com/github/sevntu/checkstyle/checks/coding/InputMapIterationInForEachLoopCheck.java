package com.github.sevntu.checkstyle.checks.coding;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

public class InputMapIterationInForEachLoopCheck {
    Map<String, String> mMap = new HashMap<String, String>();
    public static void main(String[] args) {
        Map<String, String> map = new HashMap<String, String>();
        Map<String, String> map2 = new HashMap<String, String>();
        map.put("key1", "value1");
        map.put("key2", "value2");
        
        // /////////////////////////////
        // FOR-EACH (ENCHANCED FOR) //
        // /////////////////////////////
        {
            System.out.println("\nUsing map.keySet():");
            for (String key : map.keySet()) { // WARNING HERE!
                System.out.println(key + " --> " + map.get(key));
            }

            for (String key : map.keySet()) { // NO WARNING HERE! (because of
                                                // map2.getKey() used)
                System.out.println(key + " --> " + map2.get(key));
            }

            System.out.println("\nUsing Map.Entry:");
            for (Map.Entry<String, String> entry : map.entrySet()) { // NO
                                                                        // WARNING
                                                                        // HERE!
                for (Map.Entry<String, String> entry2 : map.entrySet()) { // NO
                                                                            // WARNING
                                                                            // HERE!
                    System.out.println(entry.getKey() + " --> "
                            + entry2.getValue());
                    System.out.println(entry2.getKey() + " --> "
                            + entry.getValue());
                }
            }

            for (Map.Entry<String, String> entry : map.entrySet()) { // WARNING
                                                                        // HERE!
                for (Map.Entry<String, String> entry2 : map2.entrySet()) { // WARNING
                                                                            // HERE!
                    System.out.println(entry.getValue() + " --> ");
                    System.out.println(entry2.getKey() + " --> ");
                }
            }

            for (String key : map.keySet()) { // NO WARNING HERE (till we
                                                // implement checking of method
                                                // calls)
                System.out.println(key + " --> " + executeGetMethod(key, map));
            }

            for (Map.Entry<String, String> entry : map.entrySet()) { // NO
                                                                        // WARNING
                                                                        // HERE!
                System.out.println(entry.getKey() + " --> "
                        + executeGetMethod(entry, map));
            }

            // nested loops
            List<String> list = new LinkedList<String>();
            for (String str : list) { // NO WARNING HERE!
                // list.get(0);
                for (String key : map.keySet()) { // WARNING HERE!
                    System.out.println(key + " --> " + map.get(key));
                }
            }

            for (Map.Entry<String, String> entry2 : map.entrySet()) { // WARNING
                                                                        // HERE!
                System.out.println(entry2.getKey() + " --> ");
            }
        }

        
        for (String key : map.keySet()) { // WARNING HERE!
            System.out.println(" --> " + map.get(key));
            System.out.println(key);
        }

        for (String key : map.keySet()) { // WARNING HERE!
            System.out.println(" --> " + map.get(key));
        }

        for (String value : map.values()) { // NO WARNING HERE!
            System.out.println(" --> " + value);
        }
    }
    
    private void testMethod()
    {
        Map<String, String> mMap = new HashMap<String, String>();
        
        for (String key : mMap.keySet()) {//NO WARNING!
            System.out.println(" --> " + this.mMap.get(key));
            System.out.println(key);
        }
        
        for (String key : this.mMap.keySet()) {//WARNING!
            System.out.println(" --> " + this.mMap.get(key));
            System.out.println(key);
        }
        
        for (String key : this.mMap.keySet()) {//NO WARNING!
            System.out.println(" --> " + mMap.get(key));
            System.out.println(key);
        }
        
        for (Map.Entry<String, String> entry : this.mMap.entrySet()) { // WARNING
            // HERE!
            System.out.println(entry.getValue() + " --> ");
        }
    }

    private static String executeGetMethod(Entry<String, String> entry,
            Map<String, String> map) {
        entry.getValue();
        return null;
    }

    private static String executeGetMethod(String str, Map<String, String> map) {
        return map.get(str);
    }
    
    private static void someMethod(){
        Map<String, String> map = null;
        Set<Entry<String, String>> entrySet = map.entrySet();

        for (Iterator<Entry<String, String>> it = entrySet.iterator(); it.hasNext();) {
            Entry<String, String> entry = it.next();
            String string = entry.getKey();
            System.out.println(string);
        }
    }
}
