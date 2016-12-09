package com.github.sevntu.checkstyle.checks.coding;

import org.junit.runner.*;
import org.junit.*;
import org.junit.runners.*;


public class InputEmptyPublicCtorInClassCheck10
{
    @RunWith(Parameterized.class)
    class Inner1 {
        public Inner1() {
            
        }
    }
    
    @Ignore
    class Inner2 {
        public Inner2() {
            
        }
    }
}
