package com.github.sevntu.checkstyle.checks.coding;

public class InputNoNullForCollectionReturnCheckConstructor {
    public String[] method() {
        return null;
    }

    InputNoNullForCollectionReturnCheckConstructor() {
        if (true) {
            return;
        }
    }
}
