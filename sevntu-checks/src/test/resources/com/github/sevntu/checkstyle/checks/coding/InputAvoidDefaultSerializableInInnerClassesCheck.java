package com.github.sevntu.checkstyle.checks.coding;

import java.io.Serializable;

public class InputAvoidDefaultSerializableInInnerClassesCheck {

    interface Test {
        class TestClass implements Serializable {
        }
    }
}
