package com.github.sevntu.checkstyle.checks.coding;

import java.util.HashMap;

public class InputMapIterationInForEachLoopCheckExtendingMap {
    public static class TestMap extends HashMap<Integer, Integer> {
        public void test() {
            for (Entry<Integer, Integer> entry : this.entrySet()) {
            }
        }
        public void test2() {
            for (Entry<Integer, Integer> entry : entrySet()) {
            }
        }
    }
}
