package com.github.sevntu.checkstyle.checks.design;

import java.util.*;

public class InputPublicReferenceToPrivateTypeCheck13 {
    enum Fruit {Apple, Pear}
    private enum First {One, Two}
    First a = First.One;    //WARNING
    Fruit fruit = Fruit.Apple;
    protected First returnPrivate() {   //WARNING
        return a;
    }
    protected Fruit returnDefault() {   //OK
        return fruit;
    }
}
